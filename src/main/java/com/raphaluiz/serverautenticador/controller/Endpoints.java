package com.raphaluiz.serverautenticador.controller;

import com.google.zxing.WriterException;
import com.raphaluiz.serverautenticador.model.Auth2FA;
import com.raphaluiz.serverautenticador.model.AuthData;
import com.raphaluiz.serverautenticador.model.Response2FA;
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

import static org.springframework.http.ResponseEntity.ok;

@RestController
@AllArgsConstructor
public class Endpoints {

    private ScryptService scryptService;
    private PersonRepository personRepository;
    private TwoFactorAuthService twoFactorAuthService;


    @PostMapping("/create-user")
    ResponseEntity createUser(@RequestBody AuthData authData) {

        Optional<Person> optionalPerson = personRepository.getByName(authData.getUserName());

        if (optionalPerson.isPresent())
            return new ResponseEntity<>("Usuário existente", HttpStatus.BAD_REQUEST);

        String encodedAuthToken = scryptService.encode(authData.getAuthToken());
        String secretKey = twoFactorAuthService.generateSecretKey();

        Person person = new Person();

        person.setName(authData.getUserName());
        person.setAuthToken(encodedAuthToken);
        person.setOTPSecret(secretKey);

        personRepository.save(person);

        String email = "raphaluiz@gmail.com";
        String companyName = "Raphael&Luiz";
        String barCodeUrl = twoFactorAuthService.getGoogleAuthenticatorBarCode(secretKey, email, companyName);

        return ResponseEntity.ok(new Response2FA(barCodeUrl));
    }

    @PostMapping("/auth")
    ResponseEntity authenticateUser(@RequestBody AuthData authData) {

        String encodedAuthToken = scryptService.encode(authData.getAuthToken());
        Optional<Person> optionalPerson = personRepository.getByName(authData.getUserName());

        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            if (person.getAuthToken().equals(encodedAuthToken)) {
                return ResponseEntity.ok("Please, verify your OTP code!");
            }
        }
        return new ResponseEntity<>("Ops, wrong password!!!", HttpStatus.UNAUTHORIZED);
    }


    @PostMapping("/two-factor-auth")
    ResponseEntity twoFactorAuthenticateUser(@RequestBody Auth2FA auth2fa) {

        Optional<Person> optionalPerson = personRepository.getByName(auth2fa.getUserName());

        if (optionalPerson.isPresent())
            return new ResponseEntity<>("Usuário existente", HttpStatus.BAD_REQUEST);

        String secretKey = optionalPerson.get().getOTPSecret();

        String OTPcode = twoFactorAuthService.getTOTPCode(secretKey);

        if (auth2fa.getOTPCode().equals(OTPcode)) {
            return ok(null);
        }
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }
}
