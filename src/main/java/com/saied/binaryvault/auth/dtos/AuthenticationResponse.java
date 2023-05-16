package com.saied.binaryvault.auth.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.saied.binaryvault.appuser.dtos.AppUserDTO;

public record AuthenticationResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("user_details") AppUserDTO userDTO
) {

}
