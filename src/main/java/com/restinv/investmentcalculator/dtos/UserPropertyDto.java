package com.restinv.investmentcalculator.dtos;

import com.restinv.investmentcalculator.entites.UserProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPropertyDto implements Serializable {

    private Long userPropertyId;
    private String propertyType;

    private String imageLink;
    private String note;
    private Double purchasePrice;
    private Double percentDownPayment;
    private Double closingCosts;
    private Double repairsRenovationCost;
    private Double percentInterestRate;
    private Integer yearsFinanced;
    private Integer paymentsPerYear;
    private Double propertyTaxesPerYear;
    private Double insurancePerYear;
    private Double percentPropertyManagementFee;
    private Double percentMaintenanceCost;
    private Double monthlyUtilities;
    private Double hoaPerYear;
    private Integer ltrNumberOfUnits;
    private Double ltrMonthlyRentalIncome;
    private Double ltrPercentVacancyRate;
    private Double ltrLeasingCostPerUnit;
    private Double ltrAverageOccupancyYears;
    private Double strAverageDailyRate;
    private Double strPercentOccupancyRate;
    private Double strStartupCosts;
    private Double strPercentBookingFees;
    private Double strPercentLodgingTaxOther;
    private Double strMonthlySupplies;
    private Double strMonthlyCableInternet;
    private Double strOtherMonthlyCosts;
    private String statusName;
    private String addressLine;
    private String city;
    private String zipcode;
    private String state;
    private UserDto userDto;
    private MarketDto marketDto;

    public UserPropertyDto(UserProperty userProperty) {
        if (userProperty.getUserPropertyId() != null) {
            this.userPropertyId = userProperty.getUserPropertyId();
        }
        if (userProperty.getPropertyType() != null) {
            this.propertyType = userProperty.getPropertyType();
        }

        if (userProperty.getImageLink() != null) {
            this.imageLink = userProperty.getImageLink();
        }
        if (userProperty.getNote() != null) {
            this.note = userProperty.getNote();
        }
        if (userProperty.getPurchasePrice() != null) {
            this.purchasePrice = userProperty.getPurchasePrice();
        }
        if (userProperty.getPercentDownPayment() != null) {
            this.percentDownPayment = userProperty.getPercentDownPayment();
        }
        if (userProperty.getClosingCosts() != null) {
            this.closingCosts = userProperty.getClosingCosts();
        }
        if (userProperty.getRepairsRenovationCost() != null) {
            this.repairsRenovationCost = userProperty.getRepairsRenovationCost();
        }
        if (userProperty.getPercentInterestRate() != null) {
            this.percentInterestRate = userProperty.getPercentInterestRate();
        }
        if (userProperty.getYearsFinanced() != null) {
            this.yearsFinanced = userProperty.getYearsFinanced();
        }
        if (userProperty.getPaymentsPerYear() != null) {
            this.paymentsPerYear = userProperty.getPaymentsPerYear();
        }
        if (userProperty.getPropertyTaxesPerYear() != null) {
            this.propertyTaxesPerYear = userProperty.getPropertyTaxesPerYear();
        }
        if (userProperty.getInsurancePerYear() != null) {
            this.insurancePerYear = userProperty.getInsurancePerYear();
        }
        if (userProperty.getPercentPropertyManagementFee() != null) {
            this.percentPropertyManagementFee = userProperty.getPercentPropertyManagementFee();
        }
        if (userProperty.getPercentMaintenanceCost() != null) {
            this.percentMaintenanceCost = userProperty.getPercentMaintenanceCost();
        }
        if (userProperty.getMonthlyUtilities() != null) {
            this.monthlyUtilities = userProperty.getMonthlyUtilities();
        }
        if (userProperty.getHoaPerYear() != null) {
            this.hoaPerYear = userProperty.getHoaPerYear();
        }
        if (userProperty.getLtrNumberOfUnits() != null) {
            this.ltrNumberOfUnits = userProperty.getLtrNumberOfUnits();
        }
        if (userProperty.getLtrMonthlyRentalIncome() != null) {
            this.ltrMonthlyRentalIncome = userProperty.getLtrMonthlyRentalIncome();
        }
        if (userProperty.getLtrPercentVacancyRate() != null) {
            this.ltrPercentVacancyRate = userProperty.getLtrPercentVacancyRate();
        }
        if (userProperty.getLtrLeasingCostPerUnit() != null) {
            this.ltrLeasingCostPerUnit = userProperty.getLtrLeasingCostPerUnit();
        }
        if (userProperty.getLtrAverageOccupancyYears() != null) {
            this.ltrAverageOccupancyYears = userProperty.getLtrAverageOccupancyYears();
        }
        if (userProperty.getStrAverageDailyRate() != null) {
            this.strAverageDailyRate = userProperty.getStrAverageDailyRate();
        }
        if (userProperty.getStrPercentOccupancyRate() != null) {
            this.strPercentOccupancyRate = userProperty.getStrPercentOccupancyRate();
        }
        if (userProperty.getStrStartupCosts() != null) {
            this.strStartupCosts = userProperty.getStrStartupCosts();
        }
        if (userProperty.getStrPercentBookingFees() != null) {
            this.strPercentBookingFees = userProperty.getStrPercentBookingFees();
        }
        if (userProperty.getStrPercentLodgingTaxOther() != null) {
            this.strPercentLodgingTaxOther = userProperty.getStrPercentLodgingTaxOther();
        }
        if (userProperty.getStrMonthlySupplies() != null) {
            this.strMonthlySupplies = userProperty.getStrMonthlySupplies();
        }
        if (userProperty.getStrMonthlyCableInternet() != null) {
            this.strMonthlyCableInternet = userProperty.getStrMonthlyCableInternet();
        }
        if (userProperty.getStrOtherMonthlyCosts() != null) {
            this.strOtherMonthlyCosts = userProperty.getStrOtherMonthlyCosts();
        }
        if (userProperty.getStatusName() != null) {
            this.statusName = userProperty.getStatusName();
        }
        if (userProperty.getAddressLine() != null) {
            this.addressLine = userProperty.getAddressLine();
        }
        if (userProperty.getCity() != null) {
            this.city = userProperty.getCity();
        }
        if (userProperty.getZipcode() != null) {
            this.zipcode = userProperty.getZipcode();
        }
        if (userProperty.getState() != null) {
            this.state = userProperty.getState();
        }
    }
}
