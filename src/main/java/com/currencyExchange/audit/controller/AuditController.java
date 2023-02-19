package com.currencyExchange.audit.controller;

import com.currencyExchange.audit.models.Audit;
import com.currencyExchange.audit.models.ExchangeDetails;
import com.currencyExchange.audit.models.RatesExchangeDetails;
import com.currencyExchange.audit.services.AuditServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
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

    @GetMapping("/AuditDetails")
    private ResponseEntity<List<Audit>> getAllAudit(){
        return ResponseEntity.ok().body(this.auditservices.getAllAudit());
    }

    @GetMapping("/RateExchangeDetails")
    private ResponseEntity<List<RatesExchangeDetails>> getAllRDE() throws IOException, ParseException {
        return ResponseEntity.ok().body(this.auditservices.getAllRatesExchangeDetails());
    }
}
