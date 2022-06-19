package com.raphaluiz.serverautenticador.model;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class Auth2FA {

    private String userName;
    private String OTPCode;
}
