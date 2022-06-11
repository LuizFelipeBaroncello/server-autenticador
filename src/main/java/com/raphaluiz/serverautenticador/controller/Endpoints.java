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
    ResponseEntity createUser(@RequestBody AuthData authData) {

        Optional<Person> optionalPerson = personRepository.getByName(authData.getUserName());

        if (optionalPerson.isPresent())
            return new ResponseEntity<>("Usuário existente", HttpStatus.BAD_REQUEST);

        String encodedAuthToken = scryptService.encode(authData.getAuthToken());

        Person person = new Person();

        person.setName(authData.getUserName());
        person.setAuthToken(encodedAuthToken);

        personRepository.save(person);

        return ResponseEntity.ok(null);
    }

    @PostMapping("/auth")
    ResponseEntity authenticateUser(@RequestBody AuthData authData) {
        String encodedAuthToken = scryptService.encode(authData.getAuthToken());

        Optional<Person> optionalPerson = personRepository.getByName(authData.getUserName());

        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            if (person.getAuthToken().equals(encodedAuthToken)) {
                String secretKey = System.getenv().get("SECRET_KEY_TWO_FACTOR");

                String email = "email@gmail.com";
                String companyName = "Empresa";
                String barCodeUrl = twoFactorAuthService.getGoogleAuthenticatorBarCode(secretKey, email, companyName);

                int width = 246;
                int height = 246;

                // Fica no diretório do projeto.
                try {
                    twoFactorAuthService.createQRCode(barCodeUrl, "matrixURL.png", height, width);

                    //System.out.println("Procure o arquivo matrixCode.png no diretorio do projeto e leia o QR code para digitar o código");
                    //twoFactorAuthService.createQRCode(TOTPcode, "matrixCode.png", height, width);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // coisas do two factory authentication
                //retorna qr code
                //treco do google
                return ResponseEntity.ok(null);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
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
