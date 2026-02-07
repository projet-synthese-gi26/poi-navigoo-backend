# Configuration Guide - Notification Service Integration

## 🔧 Required Configuration

Before running the application with notification support, you need to configure the following in `application.properties`:

### 1. Service Token (REQUIRED)

Replace the placeholder with your actual service token:

```properties
notification.service.token=YOUR_ACTUAL_SERVICE_TOKEN_HERE
```

> **How to get your service token:**
> The token was generated when you registered your POI API service with the Notification Service. If you don't have it, you'll need to register your service using the Notification Service API endpoint: `POST /api/v1/services`

---

### 2. Template IDs (REQUIRED)

Update these with the actual template IDs you created on the Notification Service:

```properties
# Template IDs (update with your actual IDs)
notification.template.poi-created-email=101
notification.template.poi-created-whatsapp=102
notification.template.poi-updated-email=103
notification.template.poi-updated-whatsapp=104
notification.template.monthly-digest-email=105
```

> **Template Requirements:**
> - **POI Created Email**: Should include variables: `{{userName}}`, `{{poiName}}`, `{{poiType}}`, `{{poiCity}}`, `{{poiDescription}}`
> - **POI Created WhatsApp**: Same variables as email
> - **POI Updated Email**: Should include: `{{creatorName}}`, `{{updaterName}}`, `{{poiName}}`, `{{poiType}}`, `{{poiCity}}`
> - **POI Updated WhatsApp**: Same variables as email
> - **Monthly Digest Email**: Should include: `{{userName}}`, `{{poiCount}}`, `{{poiSummary}}`

---

### 3. Monthly Digest Schedule (OPTIONAL)

Adjust the cron schedule if needed:

```properties
# Cron expression (default: 1st of month at 9 AM)
notification.digest.schedule=0 0 9 1 * ?

# Enable/disable monthly digest
notification.digest.enabled=true
```

**Cron Expression Examples:**
- `0 0 9 1 * ?` - 1st day of month at 9:00 AM (default)
- `0 0 10 15 * ?` - 15th day of month at 10:00 AM
- `0 0 8 * * MON` - Every Monday at 8:00 AM (weekly instead of monthly)
- `0 * * * * ?` - Every minute (for testing only!)

---

## 🚀 Quick Start

1. **Update `application.properties`** with your service token and template IDs
2. **Start the application**: `mvn spring-boot:run`
3. **Test POI creation** to verify notifications are sent
4. **Check logs** for notification confirmation messages

---

## 🧪 Testing Configuration

### Test with Invalid Token

To verify error handling works correctly:

1. Set an invalid token: `notification.service.token=INVALID_TOKEN`
2. Create a POI
3. Verify that:
   - POI is still created successfully
   - Error is logged: "Failed to send POI creation notifications"
   - Application continues to work normally

### Test Monthly Digest

To test without waiting for the scheduled time:

1. Change schedule to run every minute:
   ```properties
   notification.digest.schedule=0 * * * * ?
   ```
2. Restart the application
3. Wait 1 minute
4. Check logs for: "Starting monthly digest job..."
5. Verify digest emails are sent
6. **Don't forget to change back to monthly schedule!**

---

## 📊 Environment Variables (Production)

For production deployment, use environment variables instead of hardcoding in `application.properties`:

```bash
export NOTIFICATION_SERVICE_TOKEN=your_actual_token_here
export NOTIFICATION_TEMPLATE_POI_CREATED_EMAIL=101
export NOTIFICATION_TEMPLATE_POI_CREATED_WHATSAPP=102
export NOTIFICATION_TEMPLATE_POI_UPDATED_EMAIL=103
export NOTIFICATION_TEMPLATE_POI_UPDATED_WHATSAPP=104
export NOTIFICATION_TEMPLATE_MONTHLY_DIGEST_EMAIL=105
export NOTIFICATION_DIGEST_SCHEDULE="0 0 9 1 * ?"
export NOTIFICATION_DIGEST_ENABLED=true
```

Then in `application.properties`:

```properties
notification.service.token=${NOTIFICATION_SERVICE_TOKEN:YOUR_SERVICE_TOKEN_HERE}
notification.template.poi-created-email=${NOTIFICATION_TEMPLATE_POI_CREATED_EMAIL:101}
# ... etc
```

---

## 🔍 Troubleshooting

### Notifications Not Sending

**Check:**
1. Service token is correct
2. Template IDs exist on Notification Service
3. User has valid email/phone number
4. Notification Service is accessible: `https://notification-service.pynfi.com`
5. Application logs for error messages

**Common Issues:**
- **401 Unauthorized**: Invalid service token
- **404 Not Found**: Template ID doesn't exist
- **Timeout**: Notification Service is slow/down (notifications will retry)

### Monthly Digest Not Running

**Check:**
1. `notification.digest.enabled=true`
2. Cron expression is valid
3. Application logs for: "Starting monthly digest job..."
4. Users have email addresses
5. POIs exist in user organizations

---

## 📝 Logs to Monitor

**Successful Notifications:**
```
INFO  - POI created successfully with ID: ...
INFO  - Email notification sent to user@example.com
INFO  - WhatsApp notification sent to +237670000000
```

**Failed Notifications (graceful):**
```
ERROR - Failed to send POI creation notifications: ...
INFO  - POI created successfully with ID: ...  (POI still created!)
```

**Monthly Digest:**
```
INFO  - Starting monthly digest job...
INFO  - Monthly digest sent to: user@example.com
INFO  - Monthly digest job completed in 1234 ms
```

---

## ✅ Verification Checklist

- [ ] Service token configured
- [ ] Template IDs configured
- [ ] Application starts without errors
- [ ] POI creation triggers notifications
- [ ] POI update triggers notifications (when updater ≠ creator)
- [ ] Notification failures don't break POI operations
- [ ] Monthly digest schedule is correct
- [ ] Logs show notification success/failure messages
