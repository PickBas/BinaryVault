package com.saied.binaryvault.appuser.it;


import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.saied.binaryvault.appuser.AppUserService;
import com.saied.binaryvault.auth.dtos.AuthenticationRequest;
import com.saied.binaryvault.auth.dtos.AuthenticationResponse;
import com.saied.binaryvault.auth.dtos.RegistrationRequest;
import com.saied.binaryvault.s3.S3Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = { "spring.jpa.hibernate.ddl-auto=create-drop" }
)
@AutoConfigureMockMvc
public class FileUploadDownloadIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AppUserService userService;

    private static final String AUTH_PATH = "/api/v1/auth";
    private static final String FILE_PATH = "/api/v1/file";

    @Test
    void testFileUpload() {
        Faker faker = new Faker();
        Name fakeName = faker.name();
        String username = fakeName.username();
        String firstName = fakeName.firstName();
        String lastName = fakeName.lastName();
        String email = firstName + "@gmail.com";
        String password = "Test123!";
        userService.createAppUser(
            new RegistrationRequest(username, email, firstName, lastName, password)
        );
        AuthenticationRequest authRequest = new AuthenticationRequest(username, password);
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
        AuthenticationResponse authResponse = resultLogin.getResponseBody();
        Assertions.assertNotNull(authResponse);
        String accessToken = authResponse.accessToken();
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder
            .part("file", new ClassPathResource("testfile.txt"))
            .contentType(MediaType.MULTIPART_FORM_DATA);
        webTestClient.post()
            .uri(FILE_PATH + "/upload-file")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus().isOk();
    }

}
