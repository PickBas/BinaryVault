package com.saied.binaryvault.appuser.dtos;

import com.saied.binaryvault.appuser.AppUser;
import com.saied.binaryvault.appuser.dtos.AppUserDTO;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class AppUserDTOMapper implements Function<AppUser, AppUserDTO> {

    @Override
    public AppUserDTO apply(AppUser appUser) {
        return new AppUserDTO(
            appUser.getId(),
            appUser.getUsername(),
            appUser.getEmail(),
            appUser.getFirstName(),
            appUser.getLastName()
        );
    }
}
