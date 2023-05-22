package com.saied.binaryvault.file.dtos;

import com.saied.binaryvault.file.BlobFile;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class BlobFileDTOMapper implements Function<BlobFile, BlobFileDTO> {

    @Override
    public BlobFileDTO apply(BlobFile file) {
        return new BlobFileDTO(
            file.getId(),
            file.getPath(),
            file.getCreatedAt(),
            file.getUpdatedAt()
        );
    }
}
