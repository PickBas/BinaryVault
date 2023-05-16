package com.saied.binaryvault.auth.dtos;

public record AuthenticationRefreshResponse(
    String accessToken,
    String refreshToken
) {

}
