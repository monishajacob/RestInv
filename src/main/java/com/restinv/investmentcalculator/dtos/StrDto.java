package com.restinv.investmentcalculator.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class StrDto implements Serializable, Property {
    private String propertyType;
    private String status;
    private String address;
    private Map<String, Double> rentalIncomeData;
    private Map<String, Double> purchaseData;
    private Map<String, Object> loanData;
    private Map<String, Double> expensesData;
    private Map<String, Double> monthlyNetOperatingIncome;

    private Map<String, Double> monthlyAndAnnualCashflow;

    private Map<String, Double> totalInvestment;

    private Double cashOnCashReturn;

    private String note;

}
