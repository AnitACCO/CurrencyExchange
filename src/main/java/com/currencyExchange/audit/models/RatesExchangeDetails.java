package com.currencyExchange.audit.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
public class RatesExchangeDetails implements Comparable<RatesExchangeDetails> {
    private String Base_Curency;
    private String Conversion_Curency;
    private Double Rate;
    private Date timeStamp;

    @Override
    public int compareTo(RatesExchangeDetails o) {
        return Base_Curency.compareTo(o.Base_Curency);
    }
}