package com.restinv.investmentcalculator.services;

import com.restinv.investmentcalculator.dtos.UserRegistrationRequest;
import com.restinv.investmentcalculator.dtos.UserRole;
import com.restinv.investmentcalculator.email.EmailSender;
import com.restinv.investmentcalculator.entites.TokenConfirmation;
import com.restinv.investmentcalculator.entites.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserRegistrationService {

    private  UserService userService;
    private  EmailValidatorService emailValidatorService;
    private  TokenConfirmationService tokenConfirmationService;
    private  EmailSender emailSender;
    private  EmailService emailService;

    public List<String> registerUser(UserRegistrationRequest request) {
        List<String> response = new ArrayList<>();
        boolean isValidEmail = emailValidatorService.test(request.getEmail());

        if (!isValidEmail) {
            response.add("The given email is not valid");
        }

        String token = userService.userSignUp(
                new User(request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        request.getPhoneNumber(),
                        UserRole.USER));
        if (token.startsWith("Error")) {
            response.add(token);
        } else {
            response.add("Please check your email to Activate your account or verify token " + token + " through API.");
            String link = "http://localhost:8080/api/v1/restinv/confirm?token=" + token;
            emailSender.send(
                    request.getEmail(),
                    emailService.buildEmail(request.getFirstName(), link));
        }

        return response;
    }

    @Transactional
    public String confirmToken(String token) {
        TokenConfirmation confirmationToken = tokenConfirmationService
                .getToken(token).orElse(null);
        if (confirmationToken == null) {
            return "Error token not found";
        }
        if (confirmationToken.getConfirmedAt() != null) {
            return "Error email already confirmed";
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            return "Error token expired";
        }

        tokenConfirmationService.setConfirmedAt(token);
        userService.enableUser(
                confirmationToken.getUser().getEmail());
        return "Successfully Activated the account";
    }
}
