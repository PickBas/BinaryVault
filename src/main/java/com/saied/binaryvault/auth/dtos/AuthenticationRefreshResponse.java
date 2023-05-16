package com.saied.binaryvault.auth.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthenticationRefreshResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("refresh_token") String refreshToken
) {

}
