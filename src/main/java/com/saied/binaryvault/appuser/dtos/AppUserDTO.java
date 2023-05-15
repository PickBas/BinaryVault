package com.saied.binaryvault.appuser.dtos;

public record AppUserDTO(
    Long id,
    String username,
    String email,
    String firstName,
    String lastName
) {

}
