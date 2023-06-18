package com.saied.binaryvault.index;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "Welcome to BinaryVault! API documentation is available under this uri: /api/v1/swagger-ui";
    }

}
