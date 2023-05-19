package com.restinv.investmentcalculator.repositories;

import com.restinv.investmentcalculator.entites.UserProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<UserProperty, Long> {

    @Query("SELECT p from UserProperty p where p.user.userId = ?1 and p.market.state = ?3 and p.market.countyName = ?2")
    List<UserProperty> findAllByMarketEqualsAndUser(Long userId, String countyName, String state);

    @Query("SELECT p from UserProperty p where p.user.userId = ?1 and p.addressLine = ?2 and p.city = ?3 and p.propertyType = ?4")
    Optional<UserProperty> findPropertyByUserAndAddressLineAndCityAndPropertyType(Long userId, String addressLine,
                                                                                  String city, String propertyType);

    @Query("SELECT p from UserProperty p where p.user.userId = ?1 and p.market.marketId = ?2")
    List<UserProperty> findAllByUserAndMarketEquals(Long userId, Long marketId);
}
