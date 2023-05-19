package com.restinv.investmentcalculator.entites;

import com.restinv.investmentcalculator.dtos.UserDto;
import com.restinv.investmentcalculator.dtos.UserRole;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    // setting default value of false
    private Boolean accountLocked = false;
    private Boolean accountEnabled = false;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public User(String firstName,
                String lastName,
                String email,
                String password,
                String phoneNumber,
                UserRole userRole) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.userRole = userRole;
    }

    public User(UserDto userDto) {
        if (userDto.getEmail() != null) {
            this.email = userDto.getEmail();
        }
        if (userDto.getPassword() != null) {
            this.password = userDto.getPassword();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {

        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return accountEnabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
