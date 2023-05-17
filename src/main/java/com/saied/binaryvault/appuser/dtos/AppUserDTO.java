package com.saied.binaryvault.appuser.dtos;

import java.util.List;

public record AppUserDTO(
    Long id,
    String username,
    String email,
    String firstName,
    String lastName,
    List<String> authorities
) {

}
