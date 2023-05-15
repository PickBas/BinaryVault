package com.saied.binaryvault.appuser;

import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.saied.binaryvault.appuser.dtos.AppUserCreationRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public record AppUserController(AppUserService appUserService) {

    @GetMapping("/user/{id}")
    public AppUser getById(@PathVariable("id") Long id) {
        log.info("Handling getById() request with id: {}", id);
        return appUserService.findById(id);
    }

    @PostMapping("/user/create")
    public ResponseEntity<?> createUser(@RequestBody @Valid AppUserCreationRequest appUserCreationRequest) {
        URI uri = URI.create(
            ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/user/create")
                .toUriString()
        );
        return ResponseEntity.created(uri).body(appUserService.createAppUser(appUserCreationRequest));
    }
}
