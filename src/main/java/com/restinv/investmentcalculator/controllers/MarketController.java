package com.restinv.investmentcalculator.controllers;

import com.restinv.investmentcalculator.dtos.MarketDto;
import com.restinv.investmentcalculator.services.MarketService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/market")
@AllArgsConstructor
public class MarketController {

    @Autowired
    MarketService marketService;

    @PostMapping("/{userId}")
    public List<String> addMarket(@RequestBody MarketDto marketDto, @PathVariable Long userId) {
        return marketService.addMarket(marketDto, userId);
    }

    @GetMapping("/{userId}")
    public List<MarketDto> getAllMarketByUser(@PathVariable Long userId) {
        return marketService.getAllMarketByUserId(userId);
    }

    @GetMapping("/{county}/{state}/{userId}")
    public String getMarketIdByCounty(@PathVariable String county, @PathVariable String state,
                                      @PathVariable Long userId) {
        return marketService.getMarketIdByCountyUserId(county, state, userId);
    }

    @DeleteMapping("{userId}/delete/{marketId}")
    public List<String> deleteUserMarketByCounty(@PathVariable Long userId, @PathVariable Long marketId) {

        return marketService.deleteUserMarketByCounty(userId, marketId);
    }

}
