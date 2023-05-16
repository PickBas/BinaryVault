package com.saied.binaryvault.auth.dtos;

import com.saied.binaryvault.appuser.dtos.AppUserDTO;

public record AuthenticationResponse(
    String accessToken,
    String refreshToken,
    AppUserDTO userDTO
) {

}
