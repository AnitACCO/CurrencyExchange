package com.currencyExchange.audit.controller;

import com.currencyExchange.audit.models.ExchangeDetails;
import com.currencyExchange.audit.services.AuditServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
public class AuditController {

    @Autowired
    private AuditServices auditservices;

    @GetMapping("/exchangeRate")
    private Map<String, Double> getExchangeRates(@RequestBody ExchangeDetails exchangeDetails) throws IOException {
        if(exchangeDetails.getDate() == null ){
            exchangeDetails.setDate(String.valueOf(java.time.LocalDate.now()));
        }
        return auditservices.FetchAllExchangeRates(exchangeDetails);
    }
}
