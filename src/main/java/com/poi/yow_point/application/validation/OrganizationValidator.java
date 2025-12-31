package com.poi.yow_point.application.validation;

import com.poi.yow_point.presentation.dto.OrganizationDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class OrganizationValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return OrganizationDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OrganizationDTO dto = (OrganizationDTO) target;

        if (dto.getOrganizationName() == null || dto.getOrganizationName().trim().isEmpty()) {
            errors.rejectValue("organizationName", "organizationName.empty", "Organization name cannot be empty");
        }

        if (dto.getOrgCode() == null || dto.getOrgCode().trim().isEmpty()) {
            errors.rejectValue("orgCode", "orgCode.empty", "Organization code cannot be empty");
        }

        if (dto.getOrgType() == null) {
            errors.rejectValue("orgType", "orgType.null", "Organization type cannot be null");
        }
    }
}
