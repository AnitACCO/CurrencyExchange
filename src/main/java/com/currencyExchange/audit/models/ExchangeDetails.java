package com.currencyExchange.audit.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExchangeDetails {
    private String date;
    private List<String> countries;
}
