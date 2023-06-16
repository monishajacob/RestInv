package com.restinv.investmentcalculator.services;

import com.restinv.investmentcalculator.entites.TokenConfirmation;
import com.restinv.investmentcalculator.repositories.TokenConfirmationRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TokenConfirmationService {
    @Autowired
    private TokenConfirmationRepository tokenConfirmationRepository;

    public void saveTokenConfirmation(TokenConfirmation token) {
        tokenConfirmationRepository.save(token);
    }

    public Optional<TokenConfirmation> getToken(String token) {
        return tokenConfirmationRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return tokenConfirmationRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
