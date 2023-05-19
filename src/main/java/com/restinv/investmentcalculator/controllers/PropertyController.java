package com.restinv.investmentcalculator.controllers;

import com.restinv.investmentcalculator.dtos.Property;
import com.restinv.investmentcalculator.dtos.PropertyDto;
import com.restinv.investmentcalculator.dtos.UserPropertyDto;
import com.restinv.investmentcalculator.services.PropertyService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/market")
@AllArgsConstructor
public class PropertyController {

    @Autowired
    PropertyService propertyService;

    @PostMapping("/{userId}/{marketId}/create/property")
    public List<String> addProperty(@RequestBody UserPropertyDto userPropertyDto, @PathVariable Long userId,
                                    @PathVariable Long marketId) {
        return propertyService.addProperty(userPropertyDto, userId, marketId);
    }

    @GetMapping("/{userId}/{marketId}/properties")
    public List<? extends Property> getAllUserPropertyByMarket(@PathVariable Long userId, @PathVariable Long marketId) {
        return propertyService.getAllUserPropertyByMarket(userId, marketId);
    }

    @GetMapping("/{userId}/{marketId}/property")
    public List<PropertyDto> viewAllUserPropertyByMarket(@PathVariable Long userId, @PathVariable Long marketId) {
        return propertyService.viewAllUserPropertyByMarket(userId, marketId);
    }

    @GetMapping("/{userId}/{marketId}/property/{userPropertyId}")
    public List<? extends Property> getPropertyByUserPropertyId(@PathVariable Long userId, @PathVariable Long marketId,
                                                                @PathVariable Long userPropertyId) {
        return propertyService.getPropertyByUserPropertyId(userPropertyId);
    }

    @PutMapping("/{userId}/{marketId}/property/{userPropertyId}")
    public List<? extends Property> updatePropertyByUserPropertyId(@RequestBody UserPropertyDto userPropertyDto,
                                                                   @PathVariable Long userId, @PathVariable Long marketId, @PathVariable Long userPropertyId) {
        return propertyService.updatePropertyByUserPropertyId(userPropertyDto, userId, marketId, userPropertyId);
    }

    @DeleteMapping("/{userId}/{marketId}/property/delete/{userPropertyId}")
    public List<String> deletePropertyByUserPropertyId(@PathVariable Long userId, @PathVariable Long marketId,
                                                       @PathVariable Long userPropertyId) {
        return propertyService.deletePropertyByUserPropertyId(userId, marketId, userPropertyId);
    }

}
