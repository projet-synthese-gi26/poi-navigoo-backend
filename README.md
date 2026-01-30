# YowPoint API - Points of Interest Management System

## 📋 Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [Database Setup](#database-setup)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Deployment](#deployment)

---

## 🎯 Overview

**YowPoint** is a comprehensive RESTful API for managing Points of Interest (POI), designed with a reactive architecture using Spring Boot WebFlux and R2DBC. The system supports multi-organization management, user authentication, content creation (blogs, podcasts), reviews, and analytics.

### Key Features

- ✅ **Reactive Architecture**: Built with Spring WebFlux and R2DBC for high-performance non-blocking operations
- ✅ **Multi-Organization Support**: Manage multiple organizations with different types (Merchant, Supplier, Distributor, Internal)
- ✅ **Comprehensive POI Management**: Full CRUD operations with geospatial support (PostGIS)
- ✅ **Content Management**: Blogs and Podcasts associated with POIs
- ✅ **Review System**: User reviews with ratings, likes/dislikes
- ✅ **Analytics**: Access logs and platform statistics
- ✅ **Event-Driven**: Kafka integration for real-time event processing
- ✅ **Caching**: Redis integration for improved performance
- ✅ **Database Migrations**: Liquibase for version-controlled schema management

---

## 🏗️ Architecture

### Technology Stack

| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot 3.5.0 |
| **Reactive Stack** | Spring WebFlux, Project Reactor |
| **Database** | PostgreSQL 15+ with PostGIS extension |
| **Data Access** | Spring Data R2DBC (Reactive) |
| **Migration** | Liquibase |
| **Caching** | Redis |
| **Messaging** | Apache Kafka |
| **API Documentation** | SpringDoc OpenAPI 3 (Swagger) |
| **Validation** | Jakarta Validation |
| **Mapping** | MapStruct |
| **Build Tool** | Maven |
| **Java Version** | 21 |

### Project Structure

```
api_poi/
├── src/main/java/com/poi/yow_point/
│   ├── application/
│   │   ├── model/           # Domain models and enums
│   │   └── services/        # Business logic layer
│   ├── infrastructure/
│   │   ├── configuration/   # Spring configurations
│   │   ├── entities/        # Database entities
│   │   ├── kafka/          # Kafka producers/consumers
│   │   └── repositories/   # R2DBC repositories
│   └── presentation/
│       ├── controllers/    # REST controllers
│       └── dto/           # Data Transfer Objects
└── src/main/resources/
    ├── db/changelog/      # Liquibase migrations
    └── application.properties
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **PostgreSQL 15+** with PostGIS extension
- **Redis** (for caching)
- **Apache Kafka** (for event processing)

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd api_poi
```

2. **Configure Database**

Edit `src/main/resources/application.properties`:

```properties
# R2DBC Configuration (Reactive)
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/poi
spring.r2dbc.username=postgres
spring.r2dbc.password=your_password

# JDBC Configuration (for Liquibase)
spring.datasource.url=jdbc:postgresql://localhost:5432/poi
spring.datasource.username=postgres
spring.datasource.password=your_password

# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
```

3. **Build the project**
```bash
mvn clean install
```

4. **Run the application**
```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

---

## 🗄️ Database Setup

### PostgreSQL Setup

1. **Create Database**
```sql
CREATE DATABASE poi;
```

2. **Enable PostGIS Extension**
```sql
\c poi
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

3. **Liquibase Migrations**

Liquibase will automatically create all tables on application startup. The schema includes:

- `organization` - Organizations management
- `app_user` - User accounts
- `point_of_interest` - POI data with geospatial support
- `blog` - Blog posts
- `podcast` - Podcast episodes
- `poi_review` - User reviews
- `poi_access_log` - Access tracking
- `poi_platform_stat` - Platform statistics

---

## 📚 API Documentation

### Interactive API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

### Base URL
```
http://localhost:8080/api
```

---

## 🔗 API Endpoints

### 1. Organizations API (`/api/organizations`)

#### Create Organization
```http
POST /api/organizations
Content-Type: application/json

{
  "organizationName": "YowTech Solutions",
  "orgCode": "YOWTECH001",
  "orgType": "MERCHANT",
  "isActive": true
}
```

**Response (201 Created):**
```json
{
  "organizationId": "123e4567-e89b-12d3-a456-426614174000",
  "organizationName": "YowTech Solutions",
  "orgCode": "YOWTECH001",
  "orgType": "MERCHANT",
  "isActive": true,
  "createdAt": "2026-01-30T10:00:00Z"
}
```

#### Get All Organizations
```http
GET /api/organizations
```

#### Get Organization by ID
```http
GET /api/organizations/{id}
```

#### Update Organization
```http
PUT /api/organizations/{id}
Content-Type: application/json

{
  "organizationName": "YowTech Solutions Updated",
  "orgType": "DISTRIBUTOR"
}
```

#### Delete Organization
```http
DELETE /api/organizations/{id}
```

#### Additional Endpoints
- `GET /api/organizations/code/{org_code}` - Get by organization code
- `GET /api/organizations/type/{org_type}` - Get by type
- `GET /api/organizations/active/{is_active}` - Get by active status
- `GET /api/organizations/search/{org_name}` - Search by name

**Organization Types:** `MERCHANT`, `SUPPLIER`, `DISTRIBUTOR`, `INTERNAL`

---

### 2. Users API (`/api/users`)

#### Create User
```http
POST /api/users
Content-Type: application/json

{
  "organizationId": "123e4567-e89b-12d3-a456-426614174000",
  "username": "john.doe",
  "email": "john.doe@example.com",
  "phone": "+237670000000",
  "password": "SecurePassword123!",
  "role": "USER",
  "isActive": true
}
```

**Response (201 Created):**
```json
{
  "userId": "987e6543-e21b-12d3-a456-426614174000",
  "organizationId": "123e4567-e89b-12d3-a456-426614174000",
  "username": "john.doe",
  "email": "john.doe@example.com",
  "phone": "+237670000000",
  "role": "USER",
  "isActive": true,
  "createdAt": "2026-01-30T10:00:00Z"
}
```

#### Get All Users
```http
GET /api/users
```

#### Get User by ID
```http
GET /api/users/{id}
```

#### Get User by Username
```http
GET /api/users/username/{username}
```

#### Get User by Email
```http
GET /api/users/email/{email}
```

#### Update User
```http
PUT /api/users/{id}
Content-Type: application/json

{
  "email": "john.updated@example.com",
  "phone": "+237670000001"
}
```

#### Delete User
```http
DELETE /api/users/{id}
```

#### Additional Endpoints
- `GET /api/users/exists/{id}` - Check if user exists
- `GET /api/users/username-exists/{username}` - Check username availability
- `GET /api/users/email-exists/{email}` - Check email availability
- `GET /api/users/organization/{org_id}/active` - Get active users by organization
- `GET /api/users/role/{role}` - Get users by role
- `GET /api/users/organization/{orgId}/count` - Count active users

**User Roles:** `USER`, `ADMIN`, `SUPER_ADMIN`

---

### 3. Points of Interest API (`/api/pois`)

#### Create POI
```http
POST /api/pois
Content-Type: application/json

{
  "organization_id": "123e4567-e89b-12d3-a456-426614174000",
  "created_by_user_id": "987e6543-e21b-12d3-a456-426614174000",
  "poi_name": "Restaurant Le Gourmet",
  "poi_type": "RESTAURANT",
  "poi_category": "FOOD_DRINK",
  "poi_description": "Fine dining restaurant in Yaoundé",
  "latitude": 3.8480,
  "longitude": 11.5021,
  "address_street_name": "Avenue Kennedy",
  "address_city": "Yaoundé",
  "address_country": "Cameroon",
  "website_url": "https://legourmet.cm",
  "poi_contacts": [
    {
      "contact_type": "phone",
      "contact_value": "+237670000000"
    }
  ],
  "poi_images_urls": ["https://example.com/image1.jpg"],
  "poi_amenities": ["WiFi", "Parking", "Air Conditioning"],
  "poi_keywords": ["restaurant", "fine dining", "yaoundé"],
  "is_active": true
}
```

**Response (201 Created):**
```json
{
  "poi_id": "456e7890-e12b-34d5-a678-901234567890",
  "organization_id": "123e4567-e89b-12d3-a456-426614174000",
  "poi_name": "Restaurant Le Gourmet",
  "poi_type": "RESTAURANT",
  "poi_category": "FOOD_DRINK",
  "latitude": 3.8480,
  "longitude": 11.5021,
  "address_city": "Yaoundé",
  "popularity_score": 0.0,
  "is_active": true,
  "created_at": "2026-01-30T10:00:00Z"
}
```

#### Get All POIs
```http
GET /api/pois
```

#### Get POI by ID
```http
GET /api/pois/{poi_id}
```

#### Update POI
```http
PUT /api/pois/{poi_id}
Content-Type: application/json

{
  "poi_name": "Restaurant Le Gourmet Premium",
  "poi_description": "Premium fine dining experience"
}
```

#### Delete POI
```http
DELETE /api/pois/{poi_id}
```

#### Search and Filter Endpoints
- `GET /api/pois/organization/{organization_id}` - Get POIs by organization
- `GET /api/pois/location?latitude={lat}&longitude={lon}&radius={radius}` - Search by location
- `GET /api/pois/type/{poi_type}` - Get by type
- `GET /api/pois/category/{poi_category}` - Get by category
- `GET /api/pois/name/{poi_name}` - Search by name
- `GET /api/pois/city/{city}` - Get by city
- `GET /api/pois/user/{user_id}` - Get POIs created by user

#### Management Endpoints
- `PUT /api/pois/{poi_id}/deactivate` - Deactivate POI
- `PUT /api/pois/{poi_id}/activate` - Activate POI
- `PUT /api/pois/{poi_id}/popularity?score={score}` - Update popularity score
- `GET /api/pois/check-name?name={name}&organizationId={orgId}` - Check name availability

**POI Types:** `RESTAURANT`, `HOTEL`, `SUPERMARCHE`, `MUSEE`, `PARC`, `HOPITAL`, `ECOLE`, etc.

**POI Categories:** `FOOD_DRINK`, `ACCOMMODATION`, `SHOPPING_RETAIL`, `TRANSPORTATION`, `HEALTH_WELLNESS`, `LEISURE_CULTURE`, `PUBLIC_ADMIN_SERVICES`, `FINANCE`, `EDUCATION`, `WORSHIP_SPIRITUALITY`

---

### 4. Blogs API (`/api/blogs`)

#### Create Blog Post
```http
POST /api/blogs
Content-Type: application/json

{
  "userId": "987e6543-e21b-12d3-a456-426614174000",
  "poiId": "456e7890-e12b-34d5-a678-901234567890",
  "title": "Discovering Yaoundé's Best Restaurants",
  "description": "A guide to fine dining in Cameroon's capital",
  "content": "Full blog content here...",
  "coverImageUrl": "https://example.com/cover.jpg",
  "isActive": true
}
```

**Response (201 Created):**
```json
{
  "blogId": "111e2222-e33b-44d5-a666-777788889999",
  "userId": "987e6543-e21b-12d3-a456-426614174000",
  "poiId": "456e7890-e12b-34d5-a678-901234567890",
  "title": "Discovering Yaoundé's Best Restaurants",
  "description": "A guide to fine dining in Cameroon's capital",
  "coverImageUrl": "https://example.com/cover.jpg",
  "isActive": true,
  "createdAt": "2026-01-30T10:00:00Z"
}
```

#### Additional Endpoints
- `GET /api/blogs` - Get all blogs
- `GET /api/blogs/{blog_id}` - Get blog by ID
- `GET /api/blogs/user/{user_id}` - Get blogs by user
- `GET /api/blogs/poi/{poi_id}` - Get blogs by POI
- `GET /api/blogs/search?title={title}` - Search by title
- `PUT /api/blogs/{blog_id}` - Update blog
- `DELETE /api/blogs/{blog_id}` - Delete blog

---

### 5. Podcasts API (`/api/podcasts`)

#### Create Podcast
```http
POST /api/podcasts
Content-Type: application/json

{
  "userId": "987e6543-e21b-12d3-a456-426614174000",
  "poiId": "456e7890-e12b-34d5-a678-901234567890",
  "title": "History of Yaoundé's Cuisine",
  "description": "Exploring traditional and modern food culture",
  "audioFileUrl": "https://example.com/podcast.mp3",
  "coverImageUrl": "https://example.com/podcast-cover.jpg",
  "durationSeconds": 1800,
  "isActive": true
}
```

**Response (201 Created):**
```json
{
  "podcastId": "222e3333-e44b-55d6-a777-888899990000",
  "userId": "987e6543-e21b-12d3-a456-426614174000",
  "poiId": "456e7890-e12b-34d5-a678-901234567890",
  "title": "History of Yaoundé's Cuisine",
  "audioFileUrl": "https://example.com/podcast.mp3",
  "durationSeconds": 1800,
  "isActive": true,
  "createdAt": "2026-01-30T10:00:00Z"
}
```

#### Additional Endpoints
- `GET /api/podcasts` - Get all podcasts
- `GET /api/podcasts/{podcast_id}` - Get podcast by ID
- `GET /api/podcasts/user/{user_id}` - Get podcasts by user
- `GET /api/podcasts/poi/{poi_id}` - Get podcasts by POI
- `GET /api/podcasts/search?title={title}` - Search by title
- `GET /api/podcasts/duration?minDuration={min}&maxDuration={max}` - Filter by duration
- `PUT /api/podcasts/{podcast_id}` - Update podcast
- `DELETE /api/podcasts/{podcast_id}` - Delete podcast

---

### 6. Reviews API (`/api-reviews/reviews`)

#### Create Review
```http
POST /api-reviews/reviews
Content-Type: application/json

{
  "poiId": "456e7890-e12b-34d5-a678-901234567890",
  "userId": "987e6543-e21b-12d3-a456-426614174000",
  "organizationId": "123e4567-e89b-12d3-a456-426614174000",
  "platformType": "WEB",
  "rating": 5,
  "reviewText": "Excellent restaurant! Highly recommended.",
  "likes": 0,
  "dislikes": 0
}
```

**Response (201 Created):**
```json
{
  "reviewId": "333e4444-e55b-66d7-a888-999900001111",
  "poiId": "456e7890-e12b-34d5-a678-901234567890",
  "userId": "987e6543-e21b-12d3-a456-426614174000",
  "rating": 5,
  "reviewText": "Excellent restaurant! Highly recommended.",
  "likes": 0,
  "dislikes": 0,
  "createdAt": "2026-01-30T10:00:00Z"
}
```

#### Review Management
- `GET /api-reviews/reviews` - Get all reviews
- `GET /api-reviews/reviews/{review_id}` - Get review by ID
- `GET /api-reviews/reviews/poi/{poi_id}` - Get reviews by POI
- `GET /api-reviews/reviews/user/{user_id}` - Get reviews by user
- `GET /api-reviews/reviews/organization/{organization_id}` - Get reviews by organization
- `PUT /api-reviews/reviews/{review_id}` - Update review
- `DELETE /api-reviews/reviews/{review_id}` - Delete review

#### Review Analytics
- `GET /api-reviews/reviews/poi/{poi_id}/average-rating` - Get average rating
- `GET /api-reviews/reviews/poi/{poi_id}/count` - Get review count
- `PUT /api-reviews/reviews/{review_id}/like` - Increment likes
- `PUT /api-reviews/reviews/{review_id}/dislike` - Increment dislikes
- `GET /api-reviews/reviews/poi/{poi_id}/stats` - Get comprehensive stats

**Rating:** Integer from 1 to 5

---

### 7. Access Logs API (`/api/access-logs`)

Track POI access for analytics.

#### Create Access Log
```http
POST /api/access-logs
Content-Type: application/json

{
  "poiId": "456e7890-e12b-34d5-a678-901234567890",
  "organizationId": "123e4567-e89b-12d3-a456-426614174000",
  "userId": "987e6543-e21b-12d3-a456-426614174000",
  "platformType": "MOBILE_APP",
  "accessType": "VIEW",
  "metadata": {
    "device": "iPhone 15",
    "os": "iOS 17"
  }
}
```

#### Analytics Endpoints
- `GET /api/access-logs` - Get all logs
- `GET /api/access-logs/{access_id}` - Get log by ID
- `GET /api/access-logs/poi/{poi_id}` - Get logs by POI
- `GET /api/access-logs/organization/{organization_id}` - Get logs by organization
- `GET /api/access-logs/user/{user_id}` - Get logs by user
- `GET /api/access-logs/platform/{platform_type}` - Get logs by platform
- `GET /api/access-logs/poi/{poi_id}/count` - Count accesses
- `GET /api/access-logs/organization/{organization_id}/count` - Count by organization

---

### 8. Platform Statistics API (`/api/platform-stats`)

#### Create Platform Stat
```http
POST /api/platform-stats
Content-Type: application/json

{
  "orgId": "123e4567-e89b-12d3-a456-426614174000",
  "poiId": "456e7890-e12b-34d5-a678-901234567890",
  "platformType": "WEB",
  "statDate": "2026-01-30",
  "views": 150,
  "reviews": 5,
  "likes": 20,
  "dislikes": 2
}
```

#### Statistics Endpoints
- `GET /api/platform-stats` - Get all stats
- `GET /api/platform-stats/{stat_id}` - Get stat by ID
- `GET /api/platform-stats/poi/{poi_id}` - Get stats by POI
- `GET /api/platform-stats/organization/{org_id}` - Get stats by organization
- `GET /api/platform-stats/platform/{platform_type}` - Get stats by platform
- `GET /api/platform-stats/date-range?startDate={start}&endDate={end}` - Get stats by date range
- `PUT /api/platform-stats/{stat_id}` - Update stats
- `DELETE /api/platform-stats/{stat_id}` - Delete stats

---

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PointOfInterestControllerIntegrationTest

# Run with coverage
mvn clean test jacoco:report
```

### Test Configuration

Tests use H2 in-memory database. Configuration in `src/test/resources/application-test.properties`:

```properties
spring.r2dbc.url=r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1
spring.r2dbc.username=sa
spring.r2dbc.password=
spring.sql.init.mode=never
```

### Manual Testing with cURL

Example cURL commands are available in the artifact `curl_commands.md`.

---

## 🔐 Security Considerations

- **Password Hashing**: User passwords are hashed using BCrypt
- **Validation**: All inputs are validated using Jakarta Validation
- **CORS**: Configured to allow cross-origin requests (configure appropriately for production)
- **SQL Injection**: Protected by R2DBC parameterized queries

---

## 📊 Monitoring & Observability

### Actuator Endpoints

Spring Boot Actuator is enabled. Access health and metrics:

```
GET /actuator/health
GET /actuator/metrics
GET /actuator/info
```

### Logging

Logging is configured in `application.properties`:

```properties
logging.level.com.poi.yow_point=DEBUG
logging.level.org.springframework.data.r2dbc=DEBUG
```

---

## 🚢 Deployment

### Building for Production

```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/yow_point-0.0.1-SNAPSHOT.jar
```

### Docker Deployment (Optional)

Create a `Dockerfile`:

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/yow_point-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
docker build -t yowpoint-api .
docker run -p 8080:8080 yowpoint-api
```

---

## 📝 Environment Variables

For production deployment, use environment variables:

```bash
export SPRING_R2DBC_URL=r2dbc:postgresql://prod-db:5432/poi
export SPRING_R2DBC_USERNAME=prod_user
export SPRING_R2DBC_PASSWORD=secure_password
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/poi
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=secure_password
export SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
export SPRING_DATA_REDIS_HOST=redis
export SPRING_DATA_REDIS_PORT=6379
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is proprietary software developed for YowPoint.

---

## 📧 Contact

For questions or support, contact the development team.

---

## 🔄 Version History

- **v0.0.1-SNAPSHOT** - Initial development version
  - Core POI management
  - Multi-organization support
  - Content management (Blogs, Podcasts)
  - Review system
  - Analytics and logging
  - Liquibase integration

---

## 🛠️ Troubleshooting

### Common Issues

**Issue**: Tables not created on startup
- **Solution**: Ensure PostgreSQL is running and Liquibase is properly configured. Check `LiquibaseConfiguration.java` is present.

**Issue**: Connection refused to PostgreSQL
- **Solution**: Verify PostgreSQL is running on port 5432 and credentials are correct.

**Issue**: Redis connection errors
- **Solution**: Ensure Redis is running on port 6379 or update configuration.

**Issue**: Kafka errors
- **Solution**: Verify Kafka broker is accessible at configured bootstrap servers.

---

**Built with ❤️ using Spring Boot Reactive Stack**
