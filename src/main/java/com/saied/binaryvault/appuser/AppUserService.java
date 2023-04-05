package com.saied.binaryvault.appuser;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saied.binaryvault.appuser.dtos.AppUserCreationRequest;
import com.saied.binaryvault.appuser.exceptions.AppUserNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepo;

    @Transactional
    public AppUser findById(Long id) {
        return appUserRepo
            .findById(id)
            .orElseThrow(
                () -> new AppUserNotFoundException(
                    "Could not find user with id %d".formatted(id)
                )
            );
    }

    public AppUser createAppUser(AppUserCreationRequest appUserRequest) {
        AppUser user = AppUser
            .builder()
            .username(appUserRequest.getUsername())
            .email(appUserRequest.getEmail())
            .firstName(appUserRequest.getFirstName())
            .lastName(appUserRequest.getLastName())
            .password(appUserRequest.getPassword())
            .build();
        appUserRepo.saveAndFlush(user);
        log.info("Created AppUser with id: {}", user.getId());
        return user;
    }
}
