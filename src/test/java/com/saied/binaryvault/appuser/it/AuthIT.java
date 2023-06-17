package com.saied.binaryvault.appuser.it;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.saied.binaryvault.appuser.dtos.AppUserDTO;
import com.saied.binaryvault.auth.dtos.AuthenticationRefreshResponse;
import com.saied.binaryvault.auth.dtos.AuthenticationRequest;
import com.saied.binaryvault.auth.dtos.AuthenticationResponse;
import com.saied.binaryvault.auth.dtos.RegistrationRequest;
import com.saied.binaryvault.security.jwt.JWTUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = { "spring.jpa.hibernate.ddl-auto=create-drop" }
)
public class AuthIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JWTUtils jwtUtils;

    private static final String AUTH_PATH = "/api/v1/auth";

    private AppUserDTO generateFakeUser() {
        Faker faker = new Faker();
        Name fakeName = faker.name();
        String username = fakeName.username();
        String firstName = fakeName.firstName();
        String lastName = fakeName.lastName();
        String email = firstName + "@gmail.com";
        return new AppUserDTO(
            0L,
            username,
            email,
            firstName,
            lastName,
            List.of("ROLE_USER")
        );
    }

    @Test
    public void testLogin() {
        AppUserDTO userDTO = generateFakeUser();
        String password = "test123!";
        AuthenticationRequest authRequest = new AuthenticationRequest(userDTO.username(), password);
        AuthenticationRequest authRequestBadCredentials = new AuthenticationRequest(userDTO.username(), "INVALID");
        RegistrationRequest registrationRequest = new RegistrationRequest(
            userDTO.username(),
            userDTO.email(),
            userDTO.firstName(),
            userDTO.lastName(),
            password
        );
        webTestClient.post()
            .uri(AUTH_PATH + "/register")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(registrationRequest), RegistrationRequest.class)
            .exchange()
            .expectStatus()
            .isCreated();
        webTestClient.post()
            .uri(AUTH_PATH + "/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(authRequest), AuthenticationRequest.class)
            .exchange()
            .expectStatus()
            .isOk();
        webTestClient.post()
            .uri(AUTH_PATH + "/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(authRequestBadCredentials), AuthenticationRequest.class)
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    public void testJwtToken() {
        AppUserDTO userDTO = generateFakeUser();
        String password = "test123!";
        AuthenticationRequest authRequest = new AuthenticationRequest(userDTO.username(), password);
        RegistrationRequest registrationRequest = new RegistrationRequest(
            userDTO.username(),
            userDTO.email(),
            userDTO.firstName(),
            userDTO.lastName(),
            password
        );
        webTestClient.post()
            .uri(AUTH_PATH + "/register")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(registrationRequest), RegistrationRequest.class)
            .exchange()
            .expectStatus()
            .isCreated();
        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
            .uri(AUTH_PATH + "/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(authRequest), AuthenticationRequest.class)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {})
            .returnResult();
        AuthenticationResponse authResponse = result.getResponseBody();
        Assertions.assertNotNull(authResponse);
        AppUserDTO resultDTO = authResponse.userDTO();
        Assertions.assertTrue(jwtUtils.isTokenValid(authResponse.accessToken(), userDTO.username()));
        Assertions.assertTrue(jwtUtils.isTokenValid(authResponse.refreshToken(), userDTO.username()));
        Assertions.assertEquals(userDTO.username(), resultDTO.username());
        Assertions.assertEquals(userDTO.email(), resultDTO.email());
        Assertions.assertEquals(userDTO.firstName(), resultDTO.firstName());
        Assertions.assertEquals(userDTO.lastName(), resultDTO.lastName());
        Assertions.assertEquals(userDTO.authorities(), resultDTO.authorities());
    }

    @Test
    public void testRefreshToken() {
        AppUserDTO userDTO = generateFakeUser();
        String password = "test123!";
        RegistrationRequest registrationRequest = new RegistrationRequest(
            userDTO.username(),
            userDTO.email(),
            userDTO.firstName(),
            userDTO.lastName(),
            password
        );
        webTestClient.post()
            .uri(AUTH_PATH + "/register")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(registrationRequest), RegistrationRequest.class)
            .exchange()
            .expectStatus()
            .isCreated();
        AuthenticationRequest authRequest = new AuthenticationRequest(userDTO.username(), password);
        EntityExchangeResult<AuthenticationResponse> resultLogin = webTestClient.post()
            .uri(AUTH_PATH + "/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(authRequest), AuthenticationRequest.class)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {})
            .returnResult();
        Assertions.assertNotNull(resultLogin.getResponseBody());
        String refreshToken = resultLogin.getResponseBody().refreshToken();
        EntityExchangeResult<AuthenticationRefreshResponse> resultRefreshToken = webTestClient.get()
            .uri(AUTH_PATH + "/token-refresh")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", refreshToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<AuthenticationRefreshResponse>() {})
            .returnResult();
        Assertions.assertNotNull(resultRefreshToken.getResponseBody());
        String newAccessToken = resultRefreshToken.getResponseBody().accessToken();
        String newRefreshToken = resultRefreshToken.getResponseBody().refreshToken();
        String subject = jwtUtils.getSubject(newAccessToken);
        Assertions.assertTrue(jwtUtils.isTokenValid(newAccessToken, subject));
        Assertions.assertTrue(jwtUtils.isTokenValid(newRefreshToken, subject));
    }

    @Test
    public void testRegistration() {
        AppUserDTO userDTO = generateFakeUser();
        String password = "test_password123!";
        RegistrationRequest request = new RegistrationRequest(
            userDTO.username(),
            userDTO.email(),
            userDTO.firstName(),
            userDTO.lastName(),
            password
        );
        EntityExchangeResult<AppUserDTO> resultRefreshToken = webTestClient.post()
            .uri(AUTH_PATH + "/register")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), RegistrationRequest.class)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(new ParameterizedTypeReference<AppUserDTO>() {})
            .returnResult();
        AppUserDTO registeredUser = resultRefreshToken.getResponseBody();
        Assertions.assertNotNull(registeredUser);
        Assertions.assertEquals(userDTO.username(), registeredUser.username());
        Assertions.assertEquals(userDTO.email(), registeredUser.email());
        Assertions.assertEquals(userDTO.firstName(), registeredUser.firstName());
        Assertions.assertEquals(userDTO.lastName(), registeredUser.lastName());
    }

}
