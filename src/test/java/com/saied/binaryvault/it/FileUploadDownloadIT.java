package com.saied.binaryvault.it;


import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.saied.binaryvault.AbstractTestContainers;
import com.saied.binaryvault.appuser.AppUserService;
import com.saied.binaryvault.appuser.dtos.AppUserDTO;
import com.saied.binaryvault.auth.dtos.AuthenticationRequest;
import com.saied.binaryvault.auth.dtos.AuthenticationResponse;
import com.saied.binaryvault.auth.dtos.RegistrationRequest;
import com.saied.binaryvault.file.dtos.BlobFileDTO;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
public class FileUploadDownloadIT extends AbstractTestContainers {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AppUserService userService;

    private static final String AUTH_PATH = "/api/v1/auth";
    private static final String FILE_PATH = "/api/v1/file";

    @Test
    void testFileUpload() {
        AppUserDTO userDTO = generateFakeUserDTO();
        String accessToken = getAccessToken(userDTO, "Test123!");
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder
            .part("file", new ClassPathResource("testfile.txt"))
            .contentType(MediaType.MULTIPART_FORM_DATA);
        EntityExchangeResult<BlobFileDTO> resultFileUpload = webTestClient.post()
            .uri(FILE_PATH + "/upload-file")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<BlobFileDTO>() {})
            .returnResult();
        BlobFileDTO fileDTO = resultFileUpload.getResponseBody();
        Assertions.assertNotNull(fileDTO);
        Long userId = userService.findByUsername(userDTO.username()).getId();
        Assertions.assertTrue(fileDTO.path().contains("files/%s".formatted(userId)));
    }

    @Test
    void testFileDownload() {
        AppUserDTO userDTO = generateFakeUserDTO();
        String accessToken = getAccessToken(userDTO, "Test123!");
        Long userId = userService.findByUsername(userDTO.username()).getId();
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
            .expectStatus()
            .isOk();
        EntityExchangeResult<List<BlobFileDTO>> resultListOfUserFiles = webTestClient.get()
            .uri(FILE_PATH + "/user-files/%s".formatted(userId))
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<List<BlobFileDTO>>() {})
            .returnResult();
        List<BlobFileDTO> fileDTOs = resultListOfUserFiles.getResponseBody();
        Assertions.assertNotNull(fileDTOs);
        Assertions.assertEquals(1, fileDTOs.size());
        Long fileId = fileDTOs.get(0).id();
        EntityExchangeResult<byte[]> resultDownloadFile = webTestClient.get()
            .uri(FILE_PATH + "/download-file/%s".formatted(fileId))
            .accept(MediaType.MULTIPART_FORM_DATA)
            .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<byte[]>() {})
            .returnResult();
        byte[] downloadedFile = resultDownloadFile.getResponseBody();
        byte[] originFile;
        try {
            originFile = new ClassPathResource("testfile.txt").getContentAsByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertNotNull(downloadedFile);
        Assertions.assertArrayEquals(downloadedFile, originFile);
    }

    private AppUserDTO generateFakeUserDTO() {
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

    private String getAccessToken(
        AppUserDTO userDTO,
        String password
    ) {
        userService.createAppUser(
            new RegistrationRequest(
                userDTO.username(),
                userDTO.email(),
                userDTO.firstName(),
                userDTO.lastName(),
                password
            )
        );
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
        AuthenticationResponse authResponse = resultLogin.getResponseBody();
        Assertions.assertNotNull(authResponse);
        return authResponse.accessToken();
    }

}
