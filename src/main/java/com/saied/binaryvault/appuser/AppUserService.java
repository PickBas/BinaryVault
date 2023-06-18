package com.saied.binaryvault.appuser;

import com.saied.binaryvault.appuser.dtos.AppUserDTO;
import com.saied.binaryvault.appuser.dtos.AppUserDTOMapper;
import com.saied.binaryvault.auth.dtos.RegistrationRequest;
import com.saied.binaryvault.file.BlobFile;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saied.binaryvault.exceptions.ResourceAlreadyExistsException;
import com.saied.binaryvault.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepo;
    private final AppUserDTOMapper appUserDTOMapper;
    private final PasswordEncoder encoder;

    @Transactional(readOnly = true)
    public AppUser findById(Long id) {
        return appUserRepo
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "Could not find user with id: %d".formatted(id)
                )
            );
    }

    @Transactional(readOnly = true)
    public AppUser findByUsername(String username) {
        return appUserRepo
            .findByUsername(username)
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "Could not find user with username: %s".formatted(username)
                )
            );
    }

    @Transactional(readOnly = true)
    public AppUser findByEmail(String email) {
        return appUserRepo
            .findByEmail(email)
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "Could not find user with email: %s".formatted(email)
                )
            );
    }

    @Transactional
    public AppUserDTO findAndReturnDTO(Long id) {
        return appUserDTOMapper.apply(findById(id));
    }

    public AppUser createAppUser(RegistrationRequest appUserRequest) {
        boolean usernameCheck = appUserRepo.selectExistsUsername(appUserRequest.getUsername());
        boolean emailCheck = appUserRepo.selectExistsEmail(appUserRequest.getEmail());
        if (usernameCheck) {
            throw new ResourceAlreadyExistsException(
                "User with provided username %s already exists".formatted(appUserRequest.getUsername())
            );
        }
        if (emailCheck) {
            throw new ResourceAlreadyExistsException(
                "User with provided email %s already exists".formatted(appUserRequest.getEmail())
            );
        }
        AppUser user = AppUser.builder()
            .username(appUserRequest.getUsername())
            .email(appUserRequest.getEmail())
            .firstName(appUserRequest.getFirstName())
            .lastName(appUserRequest.getLastName())
            .password(encoder.encode(appUserRequest.getPassword()))
            .build();
        appUserRepo.saveAndFlush(user);
        return user;
    }

    public void saveAppUser(AppUser user) {
        appUserRepo.save(user);
    }

    @Transactional
    public List<BlobFile> getAllFiles(Long id) {
        return findById(id).getFiles();
    }
}
