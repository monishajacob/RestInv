package com.restinv.investmentcalculator.repositories;

import com.restinv.investmentcalculator.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByAccountEnabled(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u " + "SET u.accountEnabled = TRUE WHERE u.email = ?1")
    int enableUser(String email);
}
