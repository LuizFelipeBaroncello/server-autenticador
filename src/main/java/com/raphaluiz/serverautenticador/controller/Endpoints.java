package com.raphaluiz.serverautenticador.controller;

import com.google.zxing.WriterException;
import com.raphaluiz.serverautenticador.model.AuthData;
import com.raphaluiz.serverautenticador.model.entity.Person;
import com.raphaluiz.serverautenticador.repository.PersonRepository;
import com.raphaluiz.serverautenticador.service.ScryptService;
import com.raphaluiz.serverautenticador.service.TwoFactorAuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class Endpoints {

    private ScryptService scryptService;
    private PersonRepository personRepository;
    private TwoFactorAuthService twoFactorAuthService;


    @PostMapping("/create-user")
    @ResponseBody
    ResponseEntity<String> createUser(@RequestBody AuthData authData) {

        Optional<Person> optionalPerson = personRepository.getByName(authData.getUserName());

        if (optionalPerson.isPresent())
            return ResponseEntity.ok("Usuário existente");

        String encodedAuthToken = scryptService.encode(authData.getAuthToken());

        Person person = new Person();

        person.setName(authData.getUserName());
        person.setAuthToken(encodedAuthToken);

        personRepository.save(person);

        String secretKey = twoFactorAuthService.generateSecretKey();
        String email = "email@gmail.com";
        String companyName = "Empresa";
        String barCodeUrl = twoFactorAuthService.getGoogleAuthenticatorBarCode(secretKey, email, companyName);

        return ResponseEntity.ok(barCodeUrl);
    }
    @PostMapping("/two-factor-auth")
    ResponseEntity twoFactorAuthenticateUser(@RequestBody String securityCode) {
        String secretKey = System.getenv().get("SECRET_KEY_TWO_FACTOR");

        String TOTPcode = twoFactorAuthService.getTOTPCode(secretKey);

        if (securityCode.equals(TOTPcode)) {
            return ResponseEntity.ok(null);
        }

        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }
}
