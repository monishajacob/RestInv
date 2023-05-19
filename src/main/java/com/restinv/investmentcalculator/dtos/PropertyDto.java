package com.restinv.investmentcalculator.dtos;

import com.restinv.investmentcalculator.entites.UserProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDto implements Serializable {
    private Long userPropertyId;
    private String propertyType;

    private String imageLink;
    private String addressLine;
    private Double purchasePrice;
    private Double percentDownPayment;
    private Double percentInterestRate;

    public PropertyDto(UserProperty userProperty) {
        if (userProperty.getUserPropertyId() != null) {
            this.userPropertyId = userProperty.getUserPropertyId();
        }
        if (userProperty.getPropertyType() != null) {
            this.propertyType = userProperty.getPropertyType();
        }
        if (userProperty.getImageLink() != null) {
            this.imageLink = userProperty.getImageLink();
        }
        if (userProperty.getAddressLine() != null) {
            this.addressLine = userProperty.getAddressLine();
        }
        if (userProperty.getPurchasePrice() != null) {
            this.purchasePrice = userProperty.getPurchasePrice();
        }
        if (userProperty.getPercentDownPayment() != null) {
            this.percentDownPayment = userProperty.getPercentDownPayment();
        }
        if (userProperty.getPercentInterestRate() != null) {
            this.percentInterestRate = userProperty.getPercentInterestRate();
        }

    }
}
