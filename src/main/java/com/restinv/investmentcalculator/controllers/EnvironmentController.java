package com.restinv.investmentcalculator.controllers;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/")
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentController {

    @Value("${cloud.api-key}")
    private String apiKey;

    @Value("${cloud.api-secret}")
    private String apiSecret;

    @Value("${cloud.name}")
    private String cloudName;

    @Value("${cloud.preset}")
    private String cloudPreset;

    @GetMapping("/env")
    public Map<String, String> getCloudinaryEnvironment() {
        Map<String, String> cloudinary = new HashMap<String, String>();

        cloudinary.put("CloudPreset", cloudPreset);
        cloudinary.put("CloudName", cloudName);
        cloudinary.put("ApiSecret", apiSecret);
        cloudinary.put("ApiKey", apiKey);
        return cloudinary;
    }
}
