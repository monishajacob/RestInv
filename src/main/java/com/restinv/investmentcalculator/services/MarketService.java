package com.restinv.investmentcalculator.services;

import com.restinv.investmentcalculator.dtos.MarketDto;
import com.restinv.investmentcalculator.entites.Market;
import com.restinv.investmentcalculator.entites.User;
import com.restinv.investmentcalculator.entites.UserProperty;
import com.restinv.investmentcalculator.repositories.MarketRepository;
import com.restinv.investmentcalculator.repositories.PropertyRepository;
import com.restinv.investmentcalculator.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MarketService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarketRepository marketRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @Transactional
    public List<String> addMarket(MarketDto marketDto, Long userId) {
        List<String> response = new ArrayList<>();
        try {
            // check if market already exist to prevent duplicate market creation
            Optional<Market> marketOptional = marketRepository.findMarketByUserAndCountyName(userId,
                    marketDto.getCountyName(), marketDto.getState());
            Optional<User> userOptional = userRepository.findById(userId);

            if (marketOptional.isPresent()) {
                response.add("This market is already present. Please create a new market ");
            } else {
                String validate = validateCounty(marketDto);
                System.out.println(validate);
                if (validate.startsWith("Error")) {
                    response.add(validate);
                } else {
                    Market market = new Market(marketDto);
                    userOptional.ifPresent(market::setUser);
                    marketRepository.saveAndFlush(market);
                    response.add("Successfully added market");
                }
            }
            return response;

        } catch (Exception e) {
            response.add(e.getMessage());
            response.add("Error occurred while saving market");
            return response;
        }

    }

    public List<MarketDto> getAllMarketByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            List<Market> marketList = marketRepository.findAllByUserEquals(userOptional.get());
            return marketList.stream().map(market -> new MarketDto(market)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Transactional
    public List<String> deleteUserMarketByCounty(Long userId, Long marketId) {
        List<String> response = new ArrayList<>();
        try {
            Optional<Market> marketOptional = marketRepository.findById(marketId);
            if (marketOptional.isPresent()) {
                List<UserProperty> userPropertyList = propertyRepository.findAllByMarketEqualsAndUser(userId,
                        marketOptional.get().getCountyName(), marketOptional.get().getState());
                if (userPropertyList.size() != 0) {
                    propertyRepository.deleteAll(userPropertyList);
                    response.add("Successfully deleted market and properties within market");
                } else {
                    response.add("No property to delete within market, deleted market");
                }
                marketRepository.delete(marketOptional.get());
                return response;
            } else {
                response.add("Market does not exist");
                return response;
            }

        } catch (Exception e) {
            response.add("Error occurred while deleting market");
            return response;
        }

    }

    private String validateCounty(MarketDto marketDto) {
        String county = marketDto.getCountyName();
        String state = getStateId(marketDto.getState());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "https://api.census.gov/data/2020/dec/responserate?get=NAME&for=county:*&in=state:" + state))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            if (response.statusCode() == 200) {
                List<List<String>> data = Arrays.stream(response.body().split("\n"))
                        .map(line -> Arrays.asList(line.split(",")))
                        .collect(Collectors.toList());

                boolean found = data.stream()
                        .skip(1) // skip the header row
                        .anyMatch(row -> row.get(0).toLowerCase().contains(county.toLowerCase()));

                if (found) {

                    return "County was found in the response.";
                } else {
                    return "Error County was not found in the State. Please Try again";
                }
            } else {
                return "Error API request failed with response code " + response.statusCode();
            }
        } catch (Exception e) {
            return "Error API request failed with error: " + e.getMessage();
        }
    }

    private String getStateId(String state) {

        Map<String, String> states = new HashMap<String, String>();
        states.put("Alabama", "01");
        states.put("Alaska", "02");
        states.put("Arizona", "04");
        states.put("Arkansas", "05");
        states.put("California", "06");
        states.put("Colorado", "08");
        states.put("Connecticut", "09");
        states.put("Delaware", "10");
        states.put("District Of Columbia", "11");
        states.put("Florida", "12");
        states.put("Georgia", "13");
        states.put("Hawaii", "15");
        states.put("Idaho", "16");
        states.put("Illinois", "17");
        states.put("Indiana", "18");
        states.put("Iowa", "19");
        states.put("Kansas", "20");
        states.put("Kentucky", "21");
        states.put("Louisiana", "22");
        states.put("Maine", "23");
        states.put("Maryland", "24");
        states.put("Massachusetts", "25");
        states.put("Michigan", "26");
        states.put("Minnesota", "27");
        states.put("Mississippi", "28");
        states.put("Missouri", "29");
        states.put("Montana", "30");
        states.put("Nebraska", "31");
        states.put("Nevada", "32");
        states.put("New Hampshire", "33");
        states.put("New Jersey", "34");
        states.put("New Mexico", "35");
        states.put("New York", "36");
        states.put("North Carolina", "37");
        states.put("North Dakota", "38");
        states.put("Ohio", "39");
        states.put("Oklahoma", "40");
        states.put("Oregon", "41");
        states.put("Pennsylvania", "42");
        states.put("Puerto Rico", "72");
        states.put("Rhode Island", "44");
        states.put("South Carolina", "45");
        states.put("South Dakota", "46");
        states.put("Tennessee", "47");
        states.put("Texas", "48");
        states.put("Utah", "49");
        states.put("Vermont", "50");
        states.put("Virgin Islands", "78");
        states.put("Virginia", "51");
        states.put("Washington", "53");
        states.put("West Virginia", "54");
        states.put("Wisconsin", "55");
        states.put("Wyoming", "56");

        state = WordUtils.capitalizeFully(state);
        if (states.containsKey(state)) {
            return states.get(state);
        } else {
            return "State not found please make sure the format is according to the website https://www.bls.gov/respondents/mwr/electronic-data-interchange/appendix-d-usps-state-abbreviations-and-fips-codes.htm ";
        }
    }

    public String getMarketIdByCountyUserId(String county, String state, Long userId) {
        String response;
        Optional<Market> marketOptional = marketRepository.findMarketByUserAndCountyName(userId, county, state);
        if (marketOptional.isPresent()) {
            response = String.valueOf(marketOptional.get().getMarketId());
            return response;
        }
        return "0";

    }
}
