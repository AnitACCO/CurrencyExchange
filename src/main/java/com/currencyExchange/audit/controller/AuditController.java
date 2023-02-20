package com.currencyExchange.audit.controller;

import com.currencyExchange.audit.models.Audit;
import com.currencyExchange.audit.models.ExchangeDetails;
import com.currencyExchange.audit.models.RatesExchangeDetails;
import com.currencyExchange.audit.services.AuditServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@RestController
public class AuditController {

    @Autowired
    private AuditServices auditservices;

    @PostMapping("/exchangeRate")
    private Map<String, Double> getExchangeRate(@RequestBody ExchangeDetails exchangeDetails) throws IOException {
        if(exchangeDetails.getDate() == null ){
            exchangeDetails.setDate(String.valueOf(java.time.LocalDate.now()));
        }
        return auditservices.fetchAllExchangeRates(exchangeDetails);
    }

    @PostMapping("/exchangeRates")
    private  Map<String, Double> getExchangeRates(@RequestBody ExchangeDetails exchangeDetails) throws IOException, InterruptedException, ExecutionException {
        if(exchangeDetails.getDate() == null ){
            exchangeDetails.setDate(String.valueOf(java.time.LocalDate.now()));
        }
        Map<String, Double> exchangeRates = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(exchangeDetails.getCountries().size());
        String finalDate = exchangeDetails.getDate();
        List<Callable<Map.Entry<String, Double>>> tasks = exchangeDetails.getCountries().stream().map(currency -> {
            return (Callable<Map.Entry<String, Double>>)() -> {
                try{
                    Double rate = auditservices.fetchAllExchangeRate(finalDate, currency);
                    return new AbstractMap.SimpleEntry<>(currency, rate);
                } catch(IOException e) {
                    e.printStackTrace();
                    return null;
                }
            };
        }).collect(Collectors.toList());
        TreeSet<RatesExchangeDetails> rDESet = new TreeSet<RatesExchangeDetails>();
        List<Future<Map.Entry<String, Double>>> futures = executorService.invokeAll(tasks, 10, TimeUnit.SECONDS);
        for(Future<Map.Entry<String, Double>> future : futures) {
            Map.Entry<String, Double> entry = future.get();
            if(entry != null) {
                Audit audit = new Audit();
                audit =  auditservices.createAudit(audit,entry.getKey());
                exchangeRates.put(entry.getKey(), entry.getValue());
                RatesExchangeDetails ratesExchange = new RatesExchangeDetails();
                Date date = new Date();
                ratesExchange.setBase_Curency(entry.getKey());
                ratesExchange.setConversion_Curency("USD");
                ratesExchange.setRate(entry.getValue());
                ratesExchange.setTimeStamp(Date.from(date.toInstant()));
                rDESet.add(ratesExchange);
                auditservices.updateAudit(audit);
            }
        }
        auditservices.writeExcel(rDESet);
        return exchangeRates;
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
