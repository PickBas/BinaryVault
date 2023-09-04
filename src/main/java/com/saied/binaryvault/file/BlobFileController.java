package com.saied.binaryvault.file;

import com.saied.binaryvault.file.dtos.BlobFileDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class BlobFileController {

    private final BlobFileService fileService;

    @PostMapping(
        path = "/upload-file",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(description = "Upload file.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "File was successfully uploaded.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Could not upload the file.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
        }
    )
    public ResponseEntity<BlobFileDTO> uploadFile(Principal principal, MultipartFile file) {
        BlobFileDTO fileDTO = fileService.uploadFile(principal.getName(), file);
        URI uri = URI.create(
            ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/file/upload-file")
                .toUriString()
        );
        return ResponseEntity.created(uri).body(fileDTO);
    }

    @GetMapping(
        path = "/download-file/{id}",
        produces = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(description = "Download file by it's id.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Downloaded file.",
                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            @ApiResponse(
                responseCode = "404",
                description = "File was not found.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
        }
    )
    public byte[] downloadFile(Principal principal, @PathVariable Long id) {
        return fileService.downloadFile(principal.getName(), id);
    }


    @GetMapping("/user-files/{id}")
    @Operation(description = "Retrieve all files' details that were uploaded by a user.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Retrieved files' data.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
        }
    )
    public ResponseEntity<List<BlobFileDTO>> getAllFilesOfUser(@PathVariable Long id) {
        return ResponseEntity.ok().body(fileService.getAllFilesOfUser(id));
    }
}
