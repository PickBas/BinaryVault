package com.saied.binaryvault;

import com.saied.binaryvault.appuser.AppUserService;
import com.saied.binaryvault.appuser.dtos.AppUserCreationRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BinaryVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(BinaryVaultApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(AppUserService appUserService, PasswordEncoder encoder) {
        return args -> {
            appUserService.createAppUser(
                new AppUserCreationRequest(
                    "PickBas",
                    "asd@asd.asd",
                    "Kirill",
                    "Saied",
                    encoder.encode("asdf123!")
                )
            );
        };
    }
}
