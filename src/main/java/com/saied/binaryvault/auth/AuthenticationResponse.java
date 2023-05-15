package com.saied.binaryvault.auth;

import com.saied.binaryvault.appuser.dtos.AppUserDTO;

public record AuthenticationResponse(
    String accessToken,
    String refreshToken,
    AppUserDTO userDTO
) {

}
