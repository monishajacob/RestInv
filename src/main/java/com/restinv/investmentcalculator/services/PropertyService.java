package com.restinv.investmentcalculator.services;

import com.restinv.investmentcalculator.dtos.*;
import com.restinv.investmentcalculator.entites.Market;
import com.restinv.investmentcalculator.entites.User;
import com.restinv.investmentcalculator.entites.UserProperty;
import com.restinv.investmentcalculator.repositories.MarketRepository;
import com.restinv.investmentcalculator.repositories.PropertyRepository;
import com.restinv.investmentcalculator.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class PropertyService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MarketRepository marketRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @Transactional
    public List<String> addProperty(UserPropertyDto userPropertyDto, Long userId, Long marketId) {
        List<String> response = new ArrayList<>();
        try {
            String addressLine1 = userPropertyDto.getAddressLine().replaceAll(" ", "+");
            String addressLine2 = (userPropertyDto.getCity() + " " + userPropertyDto.getState()).replaceAll(" ", "+");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://www.yaddress.net/api/address?AddressLine1=" + addressLine1
                            + "&AddressLine2=" + addressLine2 + "&UserKey="))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> jsonResponse = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());
            JSONObject adr = new JSONObject(jsonResponse.body());
            Optional<Market> marketOptional = marketRepository.findById(marketId);
            String countyName = adr.getString("County") + " County";
            if (countyName.equalsIgnoreCase(marketOptional.get().getCountyName())) {
                int errorCode = adr.getInt("ErrorCode");
                if (errorCode == 0) {
                    Optional<UserProperty> propertyOptional = propertyRepository
                            .findPropertyByUserAndAddressLineAndCityAndPropertyType(userId,
                                    adr.getString("AddressLine1"), adr.getString("City"),
                                    userPropertyDto.getPropertyType());
                    Optional<User> userOptional = userRepository.findById(userId);

                    if (propertyOptional.isPresent()) {
                        response.add("This property is already present. Please create a new property ");
                    } else {
                        String validate = validateProperty(userPropertyDto);
                        if (validate.startsWith("Error")) {
                            response.add(validate);
                        } else {
                            userPropertyDto.setAddressLine(adr.getString("AddressLine1"));
                            userPropertyDto.setCity(adr.getString("City"));
                            userPropertyDto.setState(adr.getString("State"));
                            validateAndSetStatus(userPropertyDto);
                            UserProperty userProperty = new UserProperty(userPropertyDto);
                            userOptional.ifPresent(userProperty::setUser);
                            marketOptional.ifPresent(userProperty::setMarket);
                            propertyRepository.saveAndFlush(userProperty);
                            response.add("Successfully added Property");
                        }
                    }
                } else {
                    response.add(adr.getString("ErrorMessage"));
                }
            } else {
                response.add("County mismatch! Please check your county name.");
            }

            return response;

        } catch (Exception e) {
            response.add(e.getMessage());
            response.add("Error occurred while saving Property");
            return response;
        }

    }

    public List<? extends Property> getAllUserPropertyByMarket(Long userId, Long marketId) {

        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Market> marketOptional = marketRepository.findById(marketId);
        if (userOptional.isPresent() && marketOptional.isPresent()) {
            List<UserProperty> propertyList = propertyRepository
                    .findAllByUserAndMarketEquals(userOptional.get().getUserId(), marketOptional.get().getMarketId());

            List<UserPropertyDto> userPropertyDto = propertyList.stream().map(property -> new UserPropertyDto(property))
                    .collect(Collectors.toList());

            List<? extends Property> calculate = userPropertyDto.stream()
                    .filter(userProp -> userProp.getPropertyType().toUpperCase().equals("LTR")
                            || userProp.getPropertyType().toUpperCase().equals("STR"))
                    .flatMap(userProp -> {
                        if (userProp.getPropertyType().toUpperCase().equals("LTR")) {
                            LtrDto ltrDto = calculateLTR(userProp);
                            return Stream.of(ltrDto);

                        } else {
                            StrDto strDto = calculateSTR(userProp);
                            return Stream.of(strDto);
                        }
                    }).collect(Collectors.toList());

            return calculate;

        }
        return Collections.emptyList();
    }

    public List<PropertyDto> viewAllUserPropertyByMarket(Long userId, Long marketId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Market> marketOptional = marketRepository.findById(marketId);
        if (userOptional.isPresent() && marketOptional.isPresent()) {
            List<UserProperty> propertyList = propertyRepository
                    .findAllByUserAndMarketEquals(userOptional.get().getUserId(), marketOptional.get().getMarketId());
            return propertyList.stream().map(property -> new PropertyDto(property)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<? extends Property> getPropertyByUserPropertyId(Long userPropertyId) {
        Optional<UserProperty> propertyOptional = propertyRepository.findById(userPropertyId);
        if (propertyOptional.isPresent()) {
            Optional<UserPropertyDto> userPropertyDto = Optional.of(new UserPropertyDto(propertyOptional.get()));

            List<? extends Property> calculate = userPropertyDto.stream()
                    .filter(userProp -> userProp.getPropertyType().toUpperCase().equals("LTR")
                            || userProp.getPropertyType().toUpperCase().equals("STR"))
                    .flatMap(userProp -> {
                        if (userProp.getPropertyType().toUpperCase().equals("LTR")) {
                            LtrDto ltrDto = calculateLTR(userProp);
                            return Stream.of(ltrDto);

                        } else {
                            StrDto strDto = calculateSTR(userProp);
                            return Stream.of(strDto);
                        }
                    }).collect(Collectors.toList());
            return calculate;
        }
        return Collections.emptyList();
    }

    @Transactional
    public List<? extends Property> updatePropertyByUserPropertyId(UserPropertyDto userPropertyDto, Long userId,
                                                                   Long marketId, Long userPropertyId) {
        Optional<UserProperty> propertyOptional = propertyRepository.findById(userPropertyId);
        if (propertyOptional.isPresent()) {
            if (userPropertyDto.getNote() != null) {
                propertyOptional.get().setNote(userPropertyDto.getNote());
            }
            if (userPropertyDto.getPurchasePrice() != null) {
                propertyOptional.get().setPurchasePrice(userPropertyDto.getPurchasePrice());
            }
            if (userPropertyDto.getPercentDownPayment() != null) {
                propertyOptional.get().setPercentDownPayment(userPropertyDto.getPercentDownPayment());
            }
            if (userPropertyDto.getClosingCosts() != null) {
                propertyOptional.get().setClosingCosts(userPropertyDto.getClosingCosts());
            }
            if (userPropertyDto.getRepairsRenovationCost() != null) {
                propertyOptional.get().setRepairsRenovationCost(userPropertyDto.getRepairsRenovationCost());
            }

            if (userPropertyDto.getPercentInterestRate() != null) {
                propertyOptional.get().setPercentInterestRate(userPropertyDto.getPercentInterestRate());
            }

            if (userPropertyDto.getYearsFinanced() != null) {
                propertyOptional.get().setYearsFinanced(userPropertyDto.getYearsFinanced());
            }

            if (userPropertyDto.getPaymentsPerYear() != null) {
                propertyOptional.get().setPaymentsPerYear(userPropertyDto.getPaymentsPerYear());
            }

            if (userPropertyDto.getPropertyTaxesPerYear() != null) {
                propertyOptional.get().setPropertyTaxesPerYear(userPropertyDto.getPropertyTaxesPerYear());
            }

            if (userPropertyDto.getInsurancePerYear() != null) {
                propertyOptional.get().setInsurancePerYear(userPropertyDto.getInsurancePerYear());
            }

            if (userPropertyDto.getPercentPropertyManagementFee() != null) {
                propertyOptional.get()
                        .setPercentPropertyManagementFee(userPropertyDto.getPercentPropertyManagementFee());
            }

            if (userPropertyDto.getPercentMaintenanceCost() != null) {
                propertyOptional.get().setPercentMaintenanceCost(userPropertyDto.getPercentMaintenanceCost());
            }

            if (userPropertyDto.getMonthlyUtilities() != null) {
                propertyOptional.get().setMonthlyUtilities(userPropertyDto.getMonthlyUtilities());
            }

            if (userPropertyDto.getHoaPerYear() != null) {
                propertyOptional.get().setHoaPerYear(userPropertyDto.getHoaPerYear());
            }

            if (userPropertyDto.getLtrNumberOfUnits() != null) {
                propertyOptional.get().setLtrNumberOfUnits(userPropertyDto.getLtrNumberOfUnits());
            }

            if (userPropertyDto.getLtrPercentVacancyRate() != null) {
                propertyOptional.get().setLtrPercentVacancyRate(userPropertyDto.getLtrPercentVacancyRate());
            }

            if (userPropertyDto.getLtrLeasingCostPerUnit() != null) {
                propertyOptional.get().setLtrLeasingCostPerUnit(userPropertyDto.getLtrLeasingCostPerUnit());
            }

            if (userPropertyDto.getLtrAverageOccupancyYears() != null) {
                propertyOptional.get().setLtrAverageOccupancyYears(userPropertyDto.getLtrAverageOccupancyYears());
            }

            if (userPropertyDto.getStrAverageDailyRate() != null) {
                propertyOptional.get().setStrAverageDailyRate(userPropertyDto.getStrAverageDailyRate());
            }

            if (userPropertyDto.getStrPercentOccupancyRate() != null) {
                propertyOptional.get().setStrPercentOccupancyRate(userPropertyDto.getStrPercentOccupancyRate());
            }

            if (userPropertyDto.getStrStartupCosts() != null) {
                propertyOptional.get().setStrStartupCosts(userPropertyDto.getStrStartupCosts());
            }

            if (userPropertyDto.getStrPercentBookingFees() != null) {
                propertyOptional.get().setStrPercentBookingFees(userPropertyDto.getStrPercentBookingFees());
            }

            if (userPropertyDto.getStrPercentLodgingTaxOther() != null) {
                propertyOptional.get().setStrPercentLodgingTaxOther(userPropertyDto.getStrPercentLodgingTaxOther());
            }

            if (userPropertyDto.getStrMonthlySupplies() != null) {
                propertyOptional.get().setStrMonthlySupplies(userPropertyDto.getStrMonthlySupplies());
            }

            if (userPropertyDto.getStrMonthlyCableInternet() != null) {
                propertyOptional.get().setStrMonthlyCableInternet(userPropertyDto.getStrMonthlyCableInternet());
            }

            if (userPropertyDto.getStrOtherMonthlyCosts() != null) {
                propertyOptional.get().setStrOtherMonthlyCosts(userPropertyDto.getStrOtherMonthlyCosts());
            }

            if (userPropertyDto.getStatusName() != null && userPropertyDto.getStatusName() != "") {
                validateAndSetStatus(userPropertyDto);
                propertyOptional.get().setStatusName(userPropertyDto.getStatusName());
            }

            propertyRepository.saveAndFlush(propertyOptional.get());
            List<? extends Property> calculate = getPropertyByUserPropertyId(userPropertyId);
            return calculate;
        }
        return Collections.emptyList();
    }

    @Transactional
    public List<String> deletePropertyByUserPropertyId(Long userId, Long marketId, Long userPropertyId) {
        List<String> response = new ArrayList<>();
        Optional<UserProperty> propertyOptional = propertyRepository.findById(userPropertyId);
        if (propertyOptional.isPresent()) {
            propertyRepository.delete(propertyOptional.get());
            response.add("Successfully deleted property");
        } else {
            response.add("Sorry property does not exist");
        }
        return response;

    }

    public String validateProperty(UserPropertyDto userPropertyDto) {

        if (userPropertyDto.getPropertyType() == null) {
            return "Error property type is required. Please retry.";
        } else {
            if (userPropertyDto.getPropertyType().toUpperCase().equals("LTR")) {
                if (userPropertyDto.getLtrNumberOfUnits() == null || userPropertyDto.getLtrPercentVacancyRate() == null
                        || userPropertyDto.getLtrLeasingCostPerUnit() == null
                        || userPropertyDto.getLtrAverageOccupancyYears() == null) {
                    return "Error You have missing required LTR values. Please retry.";
                } else {
                    return "Successfully Validated Property";
                }
            } else if (userPropertyDto.getPropertyType().toUpperCase().equals("STR")) {
                if (userPropertyDto.getStrAverageDailyRate() == null
                        || userPropertyDto.getStrPercentOccupancyRate() == null
                        || userPropertyDto.getStrStartupCosts() == null
                        || userPropertyDto.getStrPercentBookingFees() == null
                        || userPropertyDto.getStrPercentLodgingTaxOther() == null
                        || userPropertyDto.getStrMonthlySupplies() == null
                        || userPropertyDto.getStrMonthlyCableInternet() == null
                        || userPropertyDto.getStrOtherMonthlyCosts() == null) {
                    return "Error You have missing required STR values. Please retry.";
                } else {
                    return "Successfully Validated Property";
                }
            } else {
                return "Error Invalid property Type. Please retry.";
            }

        }
    }

    public void validateAndSetStatus(UserPropertyDto userPropertyDto) {
        if (userPropertyDto.getStatusName() != null && userPropertyDto.getStatusName() != "") {
            if (userPropertyDto.getStatusName().equalsIgnoreCase("active")) {
                userPropertyDto.setStatusName("Active");
            } else if (userPropertyDto.getStatusName().equalsIgnoreCase("coming-soon")) {
                userPropertyDto.setStatusName("Coming-Soon");
            } else if (userPropertyDto.getStatusName().equalsIgnoreCase("accepting-backup-offers")) {
                userPropertyDto.setStatusName("Accepting-Backup-Offers");
            } else if (userPropertyDto.getStatusName().equalsIgnoreCase("under-contract/pending")) {
                userPropertyDto.setStatusName("Under-Contract/Pending");
            } else if (userPropertyDto.getStatusName().equalsIgnoreCase("sold")) {
                userPropertyDto.setStatusName("Sold");
            } else {
                // status is given invalid value setting to default active
                userPropertyDto.setStatusName("Active");
            }
        }
    }

    public LtrDto calculateLTR(UserPropertyDto userPropertyDto) {
        LtrDto ltrDto = new LtrDto();
        ltrDto.setPropertyType(userPropertyDto.getPropertyType());
        ltrDto.setStatus(userPropertyDto.getStatusName());
        ltrDto.setNote(userPropertyDto.getNote());
        ltrDto.setAddress(
                userPropertyDto.getAddressLine() + "," + userPropertyDto.getCity() + "," + userPropertyDto.getState());

        Map<String, Double> purchaseData = new HashMap<>();
        purchaseData.put("Purchase Price ($)", userPropertyDto.getPurchasePrice());
        purchaseData.put("Down Payment (%)", userPropertyDto.getPercentDownPayment());
        purchaseData.put("Closing Costs ($)", userPropertyDto.getClosingCosts());
        purchaseData.put("Repairs/Renovation ($)", userPropertyDto.getRepairsRenovationCost());
        ltrDto.setPurchaseData(purchaseData);

        Map<String, Object> loanData = new HashMap<>();
        Double amountFinanced = userPropertyDto.getPurchasePrice()
                - (userPropertyDto.getPurchasePrice() * (userPropertyDto.getPercentDownPayment() / 100));
        loanData.put("Amount Financed ($)", amountFinanced);
        loanData.put("Interest Rate (%)", userPropertyDto.getPercentInterestRate());
        loanData.put("Years", userPropertyDto.getYearsFinanced());
        loanData.put("Payments per Year ($)", userPropertyDto.getPaymentsPerYear());
        ltrDto.setLoanData(loanData);

        Map<String, Object> propertyData = new HashMap<>();
        propertyData.put("Number of Units", userPropertyDto.getLtrNumberOfUnits());
        propertyData.put("Property Taxes/Year ($)", userPropertyDto.getPropertyTaxesPerYear());
        propertyData.put("Insurance/Year ($)", userPropertyDto.getInsurancePerYear());
        propertyData.put("Monthly Gross Rental Income ($)", userPropertyDto.getLtrMonthlyRentalIncome());
        propertyData.put("Vacancy Rate (%)", userPropertyDto.getLtrPercentVacancyRate());
        propertyData.put("HOA per year ($)", userPropertyDto.getHoaPerYear());
        ltrDto.setPropertyData(propertyData);

        Map<String, Double> propertyManagementData = new HashMap<>();
        propertyManagementData.put("Property Management Fee (%)", userPropertyDto.getPercentPropertyManagementFee());
        propertyManagementData.put("Leasing Cost per Unit ($)", userPropertyDto.getLtrLeasingCostPerUnit());
        propertyManagementData.put("Average Occupancy (years)", userPropertyDto.getLtrAverageOccupancyYears());
        propertyManagementData.put("Maintenance Costs (%)", userPropertyDto.getPercentMaintenanceCost());
        propertyManagementData.put("Monthly Utilities/Other ($)", userPropertyDto.getMonthlyUtilities());
        ltrDto.setPropertyManagementData(propertyManagementData);

        Map<String, Double> monthlyNetOperatingIncome = new HashMap<>();
        monthlyNetOperatingIncome.put("Monthly Gross Rental Income ($)", userPropertyDto.getLtrMonthlyRentalIncome());
        Double averageVacancy = userPropertyDto.getLtrMonthlyRentalIncome()
                * (userPropertyDto.getLtrPercentVacancyRate() / 100);
        monthlyNetOperatingIncome.put("Average Vacancy ($)", averageVacancy);
        Double monthlyNetRentalIncome = userPropertyDto.getLtrMonthlyRentalIncome() - averageVacancy;
        monthlyNetOperatingIncome.put("Net Rental Income ($)", monthlyNetRentalIncome);
        Double monthlyPropertyManagement = monthlyNetRentalIncome
                * (userPropertyDto.getPercentPropertyManagementFee() / 100);
        monthlyNetOperatingIncome.put("Property Management ($)", monthlyPropertyManagement);
        Double monthlyLeasing = (userPropertyDto.getLtrLeasingCostPerUnit() * userPropertyDto.getLtrNumberOfUnits()
                / 12) / userPropertyDto.getLtrAverageOccupancyYears();
        monthlyNetOperatingIncome.put("Leasing ($)", monthlyLeasing);
        Double monthlyMaintenance = userPropertyDto.getLtrMonthlyRentalIncome()
                * (userPropertyDto.getPercentMaintenanceCost() / 100);
        monthlyNetOperatingIncome.put("Maintenance ($)", monthlyMaintenance);
        monthlyNetOperatingIncome.put("Monthly Utilities/Other ($)", userPropertyDto.getMonthlyUtilities());
        Double monthlyPropertyTaxes = userPropertyDto.getPropertyTaxesPerYear() / 12;
        monthlyNetOperatingIncome.put("Property Taxes ($)", monthlyPropertyTaxes);
        Double monthlyInsurance = userPropertyDto.getInsurancePerYear() / 12;
        monthlyNetOperatingIncome.put("Insurance ($)", monthlyInsurance);
        Double hoa = userPropertyDto.getHoaPerYear() / 12;
        monthlyNetOperatingIncome.put("Monthly HOA ($)", hoa);
        Double monthlyExpenses = monthlyPropertyManagement + monthlyLeasing + monthlyMaintenance
                + userPropertyDto.getMonthlyUtilities() + monthlyPropertyTaxes + monthlyInsurance + hoa;
        monthlyNetOperatingIncome.put("Monthly Expenses ($)", monthlyExpenses);
        Double monthlyGrossOperatingIncome = monthlyNetRentalIncome - monthlyExpenses;
        monthlyNetOperatingIncome.put("Monthly Net Operating Income ($)", monthlyGrossOperatingIncome);
        ltrDto.setMonthlyNetOperatingIncome(monthlyNetOperatingIncome);

        Map<String, Double> monthlyAndAnnualCashflow = new HashMap<>();
        monthlyAndAnnualCashflow.put("Monthly Net Operating Income ($)", monthlyGrossOperatingIncome);
        Double monthlyRate = (userPropertyDto.getPercentInterestRate() / 100) / userPropertyDto.getPaymentsPerYear();
        Integer termInMonths = userPropertyDto.getYearsFinanced() * userPropertyDto.getPaymentsPerYear(); // Years*Payments
        // per Year
        Double monthlyPrincipalAndInterest = calculatePMT(monthlyRate, amountFinanced, termInMonths);
        monthlyAndAnnualCashflow.put("Monthly Principal and Interest ($)", monthlyPrincipalAndInterest);
        Double yearlyNetOperatingIncome = monthlyGrossOperatingIncome * 12;
        monthlyAndAnnualCashflow.put("Yearly Net Operating Income ($)", yearlyNetOperatingIncome);
        Double yearlyPrincipalAndInterest = monthlyPrincipalAndInterest * 12;
        monthlyAndAnnualCashflow.put("Yearly Principal and Interest ($)", yearlyPrincipalAndInterest);
        Double monthlyCashflow = monthlyGrossOperatingIncome - monthlyPrincipalAndInterest;
        monthlyAndAnnualCashflow.put("Monthly Cashflow ($)", monthlyCashflow);
        Double yearlyCashflow = monthlyCashflow * 12;
        monthlyAndAnnualCashflow.put("Yearly Cashflow ($)", yearlyCashflow);
        ltrDto.setMonthlyAndAnnualCashflow(monthlyAndAnnualCashflow);

        Map<String, Double> totalInvestment = new HashMap<>();
        Double downPayment = userPropertyDto.getPurchasePrice() * (userPropertyDto.getPercentDownPayment() / 100);
        totalInvestment.put("Down Payment ($)", downPayment);
        totalInvestment.put("Closing Costs ($)", userPropertyDto.getClosingCosts());
        totalInvestment.put("Repairs/Renovation ($)", userPropertyDto.getRepairsRenovationCost());
        Double totalInvestmentSum = downPayment + userPropertyDto.getClosingCosts()
                + userPropertyDto.getRepairsRenovationCost();
        totalInvestment.put("Total Investment ($)", totalInvestmentSum);
        ltrDto.setTotalInvestment(totalInvestment);

        Double cashOnCashReturn = (yearlyCashflow / totalInvestmentSum) * 100;
        ltrDto.setCashOnCashReturn(cashOnCashReturn);

        return ltrDto;
    }

    public StrDto calculateSTR(UserPropertyDto userPropertyDto) {
        StrDto strDto = new StrDto();
        strDto.setPropertyType(userPropertyDto.getPropertyType());
        strDto.setStatus(userPropertyDto.getStatusName());
        strDto.setNote(userPropertyDto.getNote());
        strDto.setAddress(
                userPropertyDto.getAddressLine() + "," + userPropertyDto.getCity() + "," + userPropertyDto.getState());

        Map<String, Double> rentalIncomeData = new HashMap<>();
        rentalIncomeData.put("Average Daily Rate ($)", userPropertyDto.getStrAverageDailyRate());
        rentalIncomeData.put("Occupancy Rate (%)", userPropertyDto.getStrPercentOccupancyRate());
        strDto.setRentalIncomeData(rentalIncomeData);

        Map<String, Double> purchaseData = new HashMap<>();
        purchaseData.put("Purchase Price ($)", userPropertyDto.getPurchasePrice());
        purchaseData.put("Down Payment (%)", userPropertyDto.getPercentDownPayment());
        purchaseData.put("Closing Costs ($)", userPropertyDto.getClosingCosts());
        purchaseData.put("Repairs/Renovation ($)", userPropertyDto.getRepairsRenovationCost());
        purchaseData.put("Furniture/Other Startup Costs ($)", userPropertyDto.getStrStartupCosts());
        strDto.setPurchaseData(purchaseData);

        Map<String, Object> loanData = new HashMap<>();
        Double amountFinanced = userPropertyDto.getPurchasePrice()
                - (userPropertyDto.getPurchasePrice() * (userPropertyDto.getPercentDownPayment() / 100));
        loanData.put("Amount Financed ($)", amountFinanced);
        loanData.put("Interest Rate (%)", userPropertyDto.getPercentInterestRate());
        loanData.put("Years", userPropertyDto.getYearsFinanced());
        loanData.put("Payments per Year", userPropertyDto.getPaymentsPerYear());
        strDto.setLoanData(loanData);

        Map<String, Double> expensesData = new HashMap<>();
        expensesData.put("Property Taxes/Year ($)", userPropertyDto.getPropertyTaxesPerYear());
        expensesData.put("Insurance/Year ($)", userPropertyDto.getInsurancePerYear());
        expensesData.put("HOA per year", userPropertyDto.getHoaPerYear());
        expensesData.put("Property Management Fee (%)", userPropertyDto.getPercentPropertyManagementFee());
        expensesData.put("Maintenance Costs (%)", userPropertyDto.getPercentMaintenanceCost());
        expensesData.put("Booking Fees (%)", userPropertyDto.getStrPercentBookingFees());
        expensesData.put("Lodging Tax/Other (%)", userPropertyDto.getStrPercentLodgingTaxOther());
        expensesData.put("Monthly Supplies ($)", userPropertyDto.getStrMonthlySupplies());
        expensesData.put("Monthly Utilities/Other ($)", userPropertyDto.getMonthlyUtilities());
        expensesData.put("Monthly Cable/Internet ($)", userPropertyDto.getStrMonthlyCableInternet());
        expensesData.put("Other Monthly Costs ($)", userPropertyDto.getStrOtherMonthlyCosts());
        strDto.setExpensesData(expensesData);

        Map<String, Double> monthlyNetOperatingIncome = new HashMap<>();
        Double monthlyIncome = (userPropertyDto.getStrAverageDailyRate() * 365
                * (userPropertyDto.getStrPercentOccupancyRate() / 100)) / 12;
        monthlyNetOperatingIncome.put("Monthly Income ($)", monthlyIncome);
        Double monthlyPropertyTaxes = userPropertyDto.getPropertyTaxesPerYear() / 12;
        monthlyNetOperatingIncome.put("Monthly Property Taxes ($)", monthlyPropertyTaxes);
        Double monthlyInsurance = userPropertyDto.getInsurancePerYear() / 12;
        monthlyNetOperatingIncome.put("Monthly Insurance ($)", monthlyInsurance);
        Double hoa = userPropertyDto.getHoaPerYear() / 12;
        monthlyNetOperatingIncome.put("Monthly HOA ($)", hoa);
        Double monthlyPropertyManagement = monthlyIncome * (userPropertyDto.getPercentPropertyManagementFee() / 100);
        monthlyNetOperatingIncome.put("Monthly Property Management ($)", monthlyPropertyManagement);
        Double monthlyMaintenance = monthlyIncome * (userPropertyDto.getPercentMaintenanceCost() / 100);
        monthlyNetOperatingIncome.put("Monthly Maintenance ($)", monthlyMaintenance);
        Double monthlyBooking = monthlyIncome * (userPropertyDto.getStrPercentBookingFees() / 100);
        monthlyNetOperatingIncome.put("Monthly Booking ($)", monthlyBooking);
        Double monthlyLodgingTaxOther = monthlyIncome * (userPropertyDto.getStrPercentLodgingTaxOther() / 100);
        monthlyNetOperatingIncome.put("Monthly Lodging Tax/Other ($)", monthlyLodgingTaxOther);
        monthlyNetOperatingIncome.put("Monthly Supplies ($)", userPropertyDto.getStrMonthlySupplies());
        monthlyNetOperatingIncome.put("Monthly Utilities ($)", userPropertyDto.getMonthlyUtilities());
        monthlyNetOperatingIncome.put("Monthly Cable/Internet ($)", userPropertyDto.getStrMonthlyCableInternet());
        monthlyNetOperatingIncome.put("Monthly Other ($)", userPropertyDto.getStrOtherMonthlyCosts());

        Double monthlyExpenses = monthlyPropertyTaxes + monthlyInsurance + hoa + monthlyPropertyManagement
                + monthlyMaintenance + monthlyBooking + monthlyLodgingTaxOther + userPropertyDto.getStrMonthlySupplies()
                + userPropertyDto.getMonthlyUtilities() + userPropertyDto.getStrMonthlyCableInternet()
                + userPropertyDto.getStrOtherMonthlyCosts();
        monthlyNetOperatingIncome.put("Monthly Expenses ($)", monthlyExpenses);
        Double monthlyGrossOperatingIncome = monthlyIncome - monthlyExpenses;
        monthlyNetOperatingIncome.put("Monthly Net Operating Income ($)", monthlyGrossOperatingIncome);
        strDto.setMonthlyNetOperatingIncome(monthlyNetOperatingIncome);

        Map<String, Double> monthlyAndAnnualCashflow = new HashMap<>();
        monthlyAndAnnualCashflow.put("Monthly Net Operating Income ($)", monthlyGrossOperatingIncome);
        Double monthlyRate = (userPropertyDto.getPercentInterestRate() / 100) / userPropertyDto.getPaymentsPerYear();
        Integer termInMonths = userPropertyDto.getYearsFinanced() * userPropertyDto.getPaymentsPerYear(); // Years*Payments
        // per Year
        Double monthlyPrincipalAndInterest = calculatePMT(monthlyRate, amountFinanced, termInMonths);
        monthlyAndAnnualCashflow.put("Monthly Principal and Interest ($)", monthlyPrincipalAndInterest);
        Double yearlyNetOperatingIncome = monthlyGrossOperatingIncome * 12;
        monthlyAndAnnualCashflow.put("Yearly Net Operating Income ($)", yearlyNetOperatingIncome);
        Double yearlyPrincipalAndInterest = monthlyPrincipalAndInterest * 12;
        monthlyAndAnnualCashflow.put("Yearly Principal and Interest ($)", yearlyPrincipalAndInterest);
        Double monthlyCashflow = monthlyGrossOperatingIncome - monthlyPrincipalAndInterest;
        monthlyAndAnnualCashflow.put("Monthly Cashflow ($)", monthlyCashflow);
        Double yearlyCashflow = monthlyCashflow * 12;
        monthlyAndAnnualCashflow.put("Yearly Cashflow ($)", yearlyCashflow);
        strDto.setMonthlyAndAnnualCashflow(monthlyAndAnnualCashflow);

        Map<String, Double> totalInvestment = new HashMap<>();
        Double downPayment = userPropertyDto.getPurchasePrice() * (userPropertyDto.getPercentDownPayment() / 100);
        totalInvestment.put("Down Payment ($)", downPayment);
        totalInvestment.put("Closing Costs ($)", userPropertyDto.getClosingCosts());
        totalInvestment.put("Repairs/Renovation ($)", userPropertyDto.getRepairsRenovationCost());
        totalInvestment.put("Furniture/Other Startup Cost ($)", userPropertyDto.getStrStartupCosts());
        Double totalInvestmentSum = downPayment + userPropertyDto.getClosingCosts()
                + userPropertyDto.getRepairsRenovationCost() + userPropertyDto.getStrStartupCosts();
        totalInvestment.put("Total Investment ($)", totalInvestmentSum);
        strDto.setTotalInvestment(totalInvestment);

        Double cashOnCashReturn = (yearlyCashflow / totalInvestmentSum) * 100;
        strDto.setCashOnCashReturn(cashOnCashReturn);

        return strDto;
    }

    public Double calculatePMT(Double monthlyRate, Double loanAmount, Integer termInMonths) {
        return (loanAmount * monthlyRate) /
                (1 - Math.pow(1 + monthlyRate, -termInMonths));
    }

}
