package com.poi.yow_point.infrastructure.repositories.appUser;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
//import org.springframework.stereotype.Repository;
import com.poi.yow_point.infrastructure.entities.AppUser;

import java.util.UUID;

public interface AppUserRepository extends R2dbcRepository<AppUser, UUID>, AppUserRepositoryCustom {

}