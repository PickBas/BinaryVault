package com.saied.binaryvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BinaryVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(BinaryVaultApplication.class, args);
    }

//    @Bean
//    CommandLineRunner commandLineRunner(AppUserService appUserService) {
//        return args -> {
//            appUserService.createAppUser(
//                new AppUserCreationRequest(
//                    "PickBas",
//                    "asd@asd.asd",
//                    "Kirill",
//                    "Saied",
//                    "asdf123!"
//                )
//            );
//        };
//    }
}
