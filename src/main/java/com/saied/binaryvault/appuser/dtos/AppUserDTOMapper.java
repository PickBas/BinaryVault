package com.saied.binaryvault.appuser.dtos;

import com.saied.binaryvault.appuser.AppUser;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

@Component
public class AppUserDTOMapper implements Function<AppUser, AppUserDTO> {

    @Override
    public AppUserDTO apply(AppUser appUser) {
        return new AppUserDTO(
            appUser.getId(),
            appUser.getUsername(),
            appUser.getEmail(),
            appUser.getFirstName(),
            appUser.getLastName(),
            appUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
        );
    }
}
