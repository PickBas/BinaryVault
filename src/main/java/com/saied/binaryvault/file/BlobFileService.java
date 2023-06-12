package com.saied.binaryvault.file;

import com.saied.binaryvault.appuser.AppUser;
import com.saied.binaryvault.appuser.AppUserService;
import com.saied.binaryvault.exceptions.ResourceNotFoundException;
import com.saied.binaryvault.file.dtos.BlobFileDTO;
import com.saied.binaryvault.file.dtos.BlobFileDTOMapper;
import com.saied.binaryvault.s3.S3Buckets;
import com.saied.binaryvault.s3.S3Service;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BlobFileService {

    private final BlobFileRepository fileRepository;
    private final BlobFileDTOMapper fileDTOMapper;
    private final S3Service s3Service;
    private final S3Buckets buckets;
    private final AppUserService userService;

    @Transactional
    public BlobFileDTO uploadFile(String username, MultipartFile file) {
        AppUser user = userService.findByUsername(username);
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
        fileRepository.saveAndFlush(fileObject);
        return fileDTOMapper.apply(fileObject);
    }

    @Transactional
    public byte[] downloadFile(String username, Long fileId) {
        AppUser user = userService.findByUsername(username);
        BlobFile file = fileRepository
            .findById(fileId)
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "Could not find file with id %s".formatted(fileId)
                )
            );
        return s3Service.getObject(buckets.getFileStorage(), file.getPath());
    }

    public void deleteFileById(Long fileId) {
        fileRepository.deleteById(fileId);
    }
}
