package com.restinv.investmentcalculator.dtos;

import com.restinv.investmentcalculator.entites.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {
    private Long userId;
    private String email;
    private String password;

    public UserDto(User user) {
        if (user.getUserId() != null) {
            this.userId = user.getUserId();
        }
        if (user.getEmail() != null) {
            this.email = user.getEmail();
        }
        if (user.getPassword() != null) {
            this.password = user.getPassword();
        }
    }
}
