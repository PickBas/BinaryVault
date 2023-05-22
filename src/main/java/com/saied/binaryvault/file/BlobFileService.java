package com.saied.binaryvault.file;

import com.saied.binaryvault.appuser.AppUser;
import com.saied.binaryvault.appuser.AppUserService;
import com.saied.binaryvault.s3.S3Buckets;
import com.saied.binaryvault.s3.S3Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BlobFileService {

    private final BlobFileRepository fileRepository;
    private final S3Service s3Service;
    private final S3Buckets buckets;
    private final AppUserService userService;

    @Transactional
    public void uploadFile(Long userId, MultipartFile file) {
        AppUser user = userService.findById(userId);
        String fileUniqueId = UUID.randomUUID().toString();
        BlobFile fileObject = BlobFile.builder()
            .path("files/%s/%s".formatted(user.getId(), fileUniqueId))
            .owner(user)
            .build();
        try {
            s3Service.putObject(
                buckets.getFileStorage(),
                fileObject.getPath(),
                file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        user.addFile(fileObject);
        userService.saveAppUser(user);
    }
}
