package com.saied.binaryvault.appuser;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
