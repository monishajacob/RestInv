package com.restinv.investmentcalculator.repositories;

import com.restinv.investmentcalculator.entites.Market;
import com.restinv.investmentcalculator.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {
    List<Market> findAllByUserEquals(User user);

    @Query("SELECT m from Market m where m.user.userId = ?1 and m.countyName = ?2 and m.state = ?3")
    Optional<Market> findMarketByUserAndCountyName(@Param("userId") Long userId, @Param("county") String county,
                                                   @Param("state") String state);

}
