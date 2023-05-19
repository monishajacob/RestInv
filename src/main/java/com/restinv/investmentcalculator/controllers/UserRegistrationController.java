package com.restinv.investmentcalculator.controllers;

import com.restinv.investmentcalculator.dtos.UserDto;
import com.restinv.investmentcalculator.dtos.UserRegistrationRequest;
import com.restinv.investmentcalculator.services.UserRegistrationService;
import com.restinv.investmentcalculator.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserRegistrationController {
    private final UserRegistrationService userRegistrationService;
    @Autowired
    private UserService userService;

    @GetMapping(path = "/register")
    public String registerUser() {
        return "register";
    }

    @PostMapping(path = "/register")
    public List<String> registerUser(@RequestBody UserRegistrationRequest request) {
        return userRegistrationService.registerUser(request);
    }

    @GetMapping(path = "/confirm")
    public String confirm(@RequestParam("token") String token) {
        return userRegistrationService.confirmToken(token);
    }

    @PostMapping("/login")
    public List<String> userLogin(@RequestBody UserDto userDto) {
        return userService.userLogin(userDto);
    }
}
