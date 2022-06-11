package com.raphaluiz.serverautenticador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RestController
public class Endpoint {

    @GetMapping("/employees")
    Collection<String> all() {
        return new ArrayList<>(Arrays.asList("Luiz", "Rapha", "Fortnite", "batata"));
    }

}
