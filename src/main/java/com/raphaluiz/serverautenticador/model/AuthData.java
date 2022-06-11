package com.raphaluiz.serverautenticador.model;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class AuthData {

    private String userName;
    private String authToken;

}
