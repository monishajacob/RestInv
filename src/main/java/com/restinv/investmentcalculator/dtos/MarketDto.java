package com.restinv.investmentcalculator.dtos;

import com.restinv.investmentcalculator.entites.Market;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketDto implements Serializable {
    private Long marketId;
    private String imageLink;
    private String countyName;
    private String state;
    private String note;

    public MarketDto(Market market) {
        if (market.getMarketId() != null) {
            this.marketId = market.getMarketId();
        }
        if (market.getImageLink() != null) {
            this.imageLink = market.getImageLink();
        }
        if (market.getCountyName() != null) {
            this.countyName = market.getCountyName();
        }
        if (market.getState() != null) {
            this.state = market.getState();
        }
        if (market.getNote() != null) {
            this.note = market.getNote();
        }
    }
}
