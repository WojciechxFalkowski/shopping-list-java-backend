package com.shoppinglist.shoppinglist.security;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @Setter
    @Getter
    private String username;
    private String password;
    private String email;
}
