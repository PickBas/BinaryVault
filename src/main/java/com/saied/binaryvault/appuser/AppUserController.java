package com.saied.binaryvault.appuser;

import com.saied.binaryvault.appuser.dtos.AppUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
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
    @Operation(description = "Get user's details.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Details of the user.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "404",
                description = "The user was not found.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
        }
    )
    public AppUserDTO getById(@PathVariable("id") Long id) {
        log.info("Handling getById() request with id: {}", id);
        return appUserService.findAndReturnDTO(id);
    }
}
