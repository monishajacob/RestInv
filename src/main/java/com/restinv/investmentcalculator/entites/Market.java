package com.restinv.investmentcalculator.entites;

import com.restinv.investmentcalculator.dtos.MarketDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class Market {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long marketId;
    @Column(columnDefinition = "TEXT")
    private String imageLink;
    private String countyName;
    private String state;
    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    public Market(MarketDto marketDto) {
        if (marketDto.getImageLink() != null) {
            this.imageLink = marketDto.getImageLink();
        }
        if (marketDto.getCountyName() != null) {
            this.countyName = marketDto.getCountyName();
        }
        if (marketDto.getState() != null) {
            this.state = marketDto.getState();
        }
        if (marketDto.getNote() != null) {
            this.note = marketDto.getNote();
        }
    }
}
