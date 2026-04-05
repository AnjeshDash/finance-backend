package com.finance.financebackend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private String email;
    private String name;
    private String role;
    private String message;
}