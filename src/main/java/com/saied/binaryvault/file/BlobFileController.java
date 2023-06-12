package com.saied.binaryvault.file;

import com.saied.binaryvault.file.dtos.BlobFileDTO;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class BlobFileController {

    private final BlobFileService fileService;

    @PostMapping(
        path = "/upload-file",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<BlobFileDTO> uploadFile(Principal principal, MultipartFile file) {
        BlobFileDTO fileDTO = fileService.uploadFile(principal.getName(), file);
        return ResponseEntity.ok().body(fileDTO);
    }

    @GetMapping(
        path = "/download-file/{id}",
        produces = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public byte[] downloadFile(Principal principal, @PathVariable Long id) {
        return fileService.downloadFile(principal.getName(), id);
    }

}
