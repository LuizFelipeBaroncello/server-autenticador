package com.raphaluiz.serverautenticador.model;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class Response2FA {
    private String barCodeUrl;
}
