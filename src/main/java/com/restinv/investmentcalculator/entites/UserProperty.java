package com.restinv.investmentcalculator.entites;

import com.restinv.investmentcalculator.dtos.UserPropertyDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class UserProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPropertyId;
    private String propertyType;
    @Column(columnDefinition = "TEXT")
    private String imageLink;
    @Column(columnDefinition = "TEXT")
    private String note;
    private Double purchasePrice;
    private Double percentDownPayment;
    private Double closingCosts;
    private Double repairsRenovationCost;
    private Double percentInterestRate;
    @Column(columnDefinition = "int4 default 30")
    private Integer yearsFinanced = 30;
    @Column(columnDefinition = "int4 default 12")
    private Integer paymentsPerYear = 12;
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
    @Column(columnDefinition = "varchar(255) default 'Active'")
    private String statusName = "Active";

    @Column(columnDefinition = "TEXT")
    private String addressLine;
    private String city;
    private String zipcode;
    private String state;

    @ManyToOne(cascade = CascadeType.MERGE)

    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE)

    @JoinColumn(name = "market_id", referencedColumnName = "marketId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Market market;

    public UserProperty(UserPropertyDto userPropertyDto) {
        if (userPropertyDto.getPropertyType() != null) {
            this.propertyType = userPropertyDto.getPropertyType();
        }
        if (userPropertyDto.getImageLink() != null) {
            this.imageLink = userPropertyDto.getImageLink();
        }
        if (userPropertyDto.getNote() != null) {
            this.note = userPropertyDto.getNote();
        }
        if (userPropertyDto.getPurchasePrice() != null) {
            this.purchasePrice = userPropertyDto.getPurchasePrice();
        }
        if (userPropertyDto.getPercentDownPayment() != null) {
            this.percentDownPayment = userPropertyDto.getPercentDownPayment();
        }
        if (userPropertyDto.getClosingCosts() != null) {
            this.closingCosts = userPropertyDto.getClosingCosts();
        }
        if (userPropertyDto.getRepairsRenovationCost() != null) {
            this.repairsRenovationCost = userPropertyDto.getRepairsRenovationCost();
        }
        if (userPropertyDto.getPercentInterestRate() != null) {
            this.percentInterestRate = userPropertyDto.getPercentInterestRate();
        }
        if (userPropertyDto.getYearsFinanced() != null) {
            this.yearsFinanced = userPropertyDto.getYearsFinanced();
        }
        if (userPropertyDto.getPaymentsPerYear() != null) {
            this.paymentsPerYear = userPropertyDto.getPaymentsPerYear();
        }
        if (userPropertyDto.getPropertyTaxesPerYear() != null) {
            this.propertyTaxesPerYear = userPropertyDto.getPropertyTaxesPerYear();
        }
        if (userPropertyDto.getInsurancePerYear() != null) {
            this.insurancePerYear = userPropertyDto.getInsurancePerYear();
        }
        if (userPropertyDto.getPercentPropertyManagementFee() != null) {
            this.percentPropertyManagementFee = userPropertyDto.getPercentPropertyManagementFee();
        }
        if (userPropertyDto.getPercentMaintenanceCost() != null) {
            this.percentMaintenanceCost = userPropertyDto.getPercentMaintenanceCost();
        }

        if (userPropertyDto.getMonthlyUtilities() != null) {
            this.monthlyUtilities = userPropertyDto.getMonthlyUtilities();
        }
        if (userPropertyDto.getHoaPerYear() != null) {
            this.hoaPerYear = userPropertyDto.getHoaPerYear();
        }
        if (userPropertyDto.getLtrNumberOfUnits() != null) {
            this.ltrNumberOfUnits = userPropertyDto.getLtrNumberOfUnits();
        }
        if (userPropertyDto.getLtrMonthlyRentalIncome() != null) {
            this.ltrMonthlyRentalIncome = userPropertyDto.getLtrMonthlyRentalIncome();
        }
        if (userPropertyDto.getLtrPercentVacancyRate() != null) {
            this.ltrPercentVacancyRate = userPropertyDto.getLtrPercentVacancyRate();
        }
        if (userPropertyDto.getLtrLeasingCostPerUnit() != null) {
            this.ltrLeasingCostPerUnit = userPropertyDto.getLtrLeasingCostPerUnit();
        }
        if (userPropertyDto.getLtrAverageOccupancyYears() != null) {
            this.ltrAverageOccupancyYears = userPropertyDto.getLtrAverageOccupancyYears();
        }
        if (userPropertyDto.getStrAverageDailyRate() != null) {
            this.strAverageDailyRate = userPropertyDto.getStrAverageDailyRate();
        }
        if (userPropertyDto.getStrPercentOccupancyRate() != null) {
            this.strPercentOccupancyRate = userPropertyDto.getStrPercentOccupancyRate();
        }
        if (userPropertyDto.getStrStartupCosts() != null) {
            this.strStartupCosts = userPropertyDto.getStrStartupCosts();
        }
        if (userPropertyDto.getStrPercentBookingFees() != null) {
            this.strPercentBookingFees = userPropertyDto.getStrPercentBookingFees();
        }
        if (userPropertyDto.getStrPercentLodgingTaxOther() != null) {
            this.strPercentLodgingTaxOther = userPropertyDto.getStrPercentLodgingTaxOther();
        }
        if (userPropertyDto.getStrMonthlySupplies() != null) {
            this.strMonthlySupplies = userPropertyDto.getStrMonthlySupplies();
        }
        if (userPropertyDto.getStrMonthlyCableInternet() != null) {
            this.strMonthlyCableInternet = userPropertyDto.getStrMonthlyCableInternet();
        }
        if (userPropertyDto.getStrOtherMonthlyCosts() != null) {
            this.strOtherMonthlyCosts = userPropertyDto.getStrOtherMonthlyCosts();
        }
        if (userPropertyDto.getStatusName() != null) {
            this.statusName = userPropertyDto.getStatusName();
        }
        if (userPropertyDto.getAddressLine() != null) {
            this.addressLine = userPropertyDto.getAddressLine();
        }
        if (userPropertyDto.getCity() != null) {
            this.city = userPropertyDto.getCity();
        }
        if (userPropertyDto.getZipcode() != null) {
            this.zipcode = userPropertyDto.getZipcode();
        }
        if (userPropertyDto.getState() != null) {
            this.state = userPropertyDto.getState();
        }

    }
}
