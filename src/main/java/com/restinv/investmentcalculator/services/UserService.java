package com.restinv.investmentcalculator.services;

import com.restinv.investmentcalculator.dtos.UserDto;
import com.restinv.investmentcalculator.dtos.UserRole;
import com.restinv.investmentcalculator.entites.TokenConfirmation;
import com.restinv.investmentcalculator.entites.User;
import com.restinv.investmentcalculator.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "The user having email %s is not found";
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenConfirmationService tokenConfirmationService;
    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format(USER_NOT_FOUND_MSG, email)));
    }

    @Transactional
    public String userSignUp(User user) {
        boolean userExists = userRepository
                .findByEmail(user.getEmail())
                .isPresent();

        Optional<User> accountUser = userRepository.findByEmail(user.getEmail());

        if (userExists) {

            boolean userAccountEnabled = accountUser.get().getAccountEnabled();
            if (userAccountEnabled) {
                return "Error the email is not available as an account is already assigned to it. Please login instead.";
            } else {
                Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
                boolean passwordMatch = passwordEncoder.matches(user.getPassword(), userOptional.get().getPassword());
                if (passwordMatch) {
                    String token = generateToken(user);
                    return token;

                } else {
                    return "Error the email or password do not match. Please try creating an account with new credentials";
                }

            }
        }

        String encodedPassword = bCryptPasswordEncoder
                .encode(user.getPassword());

        user.setPassword(encodedPassword);

        userRepository.save(user);

        String token = generateToken(user);

        return token;

    }

    public String generateToken(User user) {
        String token = UUID.randomUUID().toString();

        TokenConfirmation tokenConfirmation = new TokenConfirmation(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user);

        tokenConfirmationService.saveTokenConfirmation(
                tokenConfirmation);

        return token;
    }

    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }

    public List<String> userLogin(UserDto userDto) {

        List<String> response = new ArrayList<>();
        Optional<User> userOptional = userRepository.findByEmail(userDto.getEmail());
        if (userOptional.isPresent() && userOptional.get().getAccountEnabled()) {
            if (passwordEncoder.matches(userDto.getPassword(), userOptional.get().getPassword())) {
                response.add("http://localhost:8080/api/v1/restinv/market.html");
                response.add(String.valueOf(userOptional.get().getUserId()));
            } else {
                response.add("Username or password incorrect");
            }
        } else {
            response.add("Please make sure that the user exist or check email to activate account. ");
        }
        return response;
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<UserRole> roles) {
        Collection<? extends GrantedAuthority> mapRoles = roles.stream()
                .map(role -> new SimpleGrantedAuthority("USER"))
                .collect(Collectors.toList());
        return mapRoles;
    }

}
