package com.poi.yow_point.infrastructure.repositories.organization;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;

import com.poi.yow_point.infrastructure.entities.Organization;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OrganizationRepositoryImpl implements OrganizationRepositoryCustom {

    private final R2dbcEntityTemplate template;

    public OrganizationRepositoryImpl(R2dbcEntityTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Organization> findByOrgCode(String orgCode) {
        return template.select(Organization.class)
                .matching(Query.query(Criteria.where("org_code").is(orgCode)))
                .one();
    }

    @Override
    public Flux<Organization> findAllActive() {
        return template.select(Organization.class)
                .matching(Query.query(Criteria.where("is_active").is(true)))
                .all();
    }

    // Recherche par type d'organisation avec criteria
    public Flux<Organization> findByOrgType(String orgType) {
        return template.select(Organization.class)
                .matching(Query.query(Criteria.where("org_type").is(orgType)))
                .all();
    }

    // Recherche par statut actif avec criteria
    public Flux<Organization> findByIsActive(Boolean isActive) {
        return template.select(Organization.class)
                .matching(Query.query(Criteria.where("is_active").is(isActive)))
                .all();
    }

    // Recherche par nom contenant (case insensitive) avec criteria
    public Flux<Organization> findByOrgName(String orgName) {
        return template.select(Organization.class)
                .matching(Query.query(Criteria.where("org_name").like("%" + orgName + "%").ignoreCase(true)))
                .all();
    }

    // Recherche combinée par type et statut avec criteria
    public Flux<Organization> findByOrgTypeAndIsActive(String orgType, Boolean isActive) {
        Criteria criteria = Criteria.where("org_type").is(orgType)
                .and("is_active").is(isActive);

        return template.select(Organization.class)
                .matching(Query.query(criteria))
                .all();
    }
}
