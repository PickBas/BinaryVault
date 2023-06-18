package com.saied.binaryvault.blobfile;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.saied.binaryvault.appuser.AppUser;
import com.saied.binaryvault.appuser.AppUserService;
import com.saied.binaryvault.file.BlobFile;
import com.saied.binaryvault.file.BlobFileRepository;
import com.saied.binaryvault.file.BlobFileService;
import com.saied.binaryvault.file.dtos.BlobFileDTO;
import com.saied.binaryvault.file.dtos.BlobFileDTOMapper;
import com.saied.binaryvault.s3.S3Buckets;
import com.saied.binaryvault.s3.S3Service;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class BlobFileServiceTest {

    @Mock
    private BlobFileRepository fileRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private AppUserService userService;

    @Value("${aws.s3.buckets.fileStorage}")
    private String fileStorage;

    private BlobFileService blobFileService;

    private AppUser testUser;
    private BlobFile testFile;

    @BeforeEach
    void setUp() {
        blobFileService = new BlobFileService(
            fileRepository,
            new BlobFileDTOMapper(),
            s3Service,
            new S3Buckets(),
            userService
        );
        Faker faker = new Faker();
        Name fakename = faker.name();
        testUser = AppUser.builder()
            .username(fakename.username())
            .firstName(fakename.firstName())
            .lastName(fakename.lastName())
            .email("test@testemail.com")
            .files(new ArrayList<>())
            .build();
        testFile = BlobFile.builder()
            .id(10L)
            .owner(testUser)
            .path("/files/123/test")
            .build();
    }

    @Test
    void testFileUpload() {
        when(userService.findByUsername(any())).thenReturn(testUser);
        MultipartFile file = new MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes()
        );
        BlobFileDTO fileDTO = blobFileService.uploadFile(testUser.getUsername(), file);
        verify(userService).findByUsername(
            testUser.getUsername()
        );
        assertTrue(fileDTO.path().contains("files/"));
    }

    @Test
    void testFileDownload() {
        when(userService.findByUsername(any())).thenReturn(testUser);
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(testFile));
        blobFileService.downloadFile(testUser.getUsername(), testFile.getId());
        verify(userService).findByUsername(testUser.getUsername());
        verify(fileRepository).findById(testFile.getId());
        verify(s3Service).getObject(fileStorage, testFile.getPath());
    }


}
