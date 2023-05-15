package com.saied.binaryvault.appuser.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppUserCreationRequest {
    @NotBlank
    private String username;
    @Email
    @NotBlank
    private String email;
    private String firstName;
    private String lastName;
    @NotBlank
    private String password;
}
