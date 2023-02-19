package com.currencyExchange.audit.services;
import com.currencyExchange.audit.models.Audit;
import com.currencyExchange.audit.models.ExchangeDetails;
import com.currencyExchange.audit.models.RatesExchangeDetails;
import com.currencyExchange.audit.reposistory.AuditReposistory;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import javax.transaction.Transactional;
import java.io.*;
import java.util.*;

@Service
@Transactional
public class AuditServicesImp implements AuditServices{

    private RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private AuditReposistory auditReposistory;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Override
    public Map<String,Double> FetchAllExchangeRates(ExchangeDetails exchangeDetails) throws IOException {
        Map<String, Double> exhangeRateMap = new HashMap<String, Double>();
        TreeSet<RatesExchangeDetails> rDESet = new TreeSet<RatesExchangeDetails>();
        for (String country : exchangeDetails.getCountries()) {
            RatesExchangeDetails ratesExchange = new RatesExchangeDetails();
            Audit audit = new Audit();
            audit.setStatus("Request_Recieved");
            audit.setRequest(country + "-USD");
            Date date = new Date();
            audit.setUpdatedTime(Date.from(date.toInstant()));
            audit.setCreatedTime(Date.from(date.toInstant()));
            createAudit(audit);
            long start = System.currentTimeMillis();
            long end = start + 3 * 1000;
            while (System.currentTimeMillis() < end) {

                String url = "https://api.apilayer.com/exchangerates_data/" + exchangeDetails.getDate() +
                        "?symbols=" + country + "&base=USD";

                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                headers.set("apikey", "dfehaCM5GreNMoNvNlezawMb07bB8c0m");
                ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
                Double rate = response.getBody().get("rates").get(country).asDouble();
                ratesExchange.setBase_Curency(country);
                ratesExchange.setConversion_Curency("USD");
                ratesExchange.setRate(rate);
                ratesExchange.setTimeStamp(Date.from(date.toInstant()));
                rDESet.add(ratesExchange);
                audit.setStatus("Reponse_Completed");
                updateAudit(audit);
                exhangeRateMap.put(country, rate);
            }
        }
        writeExcel(rDESet);
        return exhangeRateMap;
    }

    @Override
    public Audit createAudit(Audit audit) {
        return this.auditReposistory.save(audit);
    }

    @Override
    public Audit updateAudit(Audit audit) {
        Optional<Audit> auditObj = this.auditReposistory.findById(audit.getRequestId());
        if(auditObj.isPresent()){
            Audit auditUpdate = auditObj.get();
            auditUpdate.setStatus(audit.getStatus());
            Date date = new Date();
            auditUpdate.setUpdatedTime(Date.from(date.toInstant()));
            return this.auditReposistory.save(auditUpdate);
        }else {
            throw new RuntimeException("No Audit Found");
        }
    }

    @Override
    public void writeExcel(TreeSet<RatesExchangeDetails> rDESet) throws IOException {
        for(RatesExchangeDetails rde : rDESet){
            File currDir = new File(".");
            String path = currDir.getAbsolutePath();
            String fileLocation = path.substring(0, path.length() - 1) + "Currency_Exchange_Details.xlsx";

            Workbook workbook = new XSSFWorkbook(new FileInputStream(fileLocation));

            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = sheet.getLastRowNum() + 1;

            Row row = sheet.createRow(rowNum);
            Cell cell = row.createCell(0);
            cell.setCellValue(rde.getBase_Curency());

            cell = row.createCell(1);
            cell.setCellValue(rde.getConversion_Curency());

            cell = row.createCell(2);
            cell.setCellValue(rde.getRate());

            cell = row.createCell(3);
            cell.setCellValue(rde.getTimeStamp().toString());

            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();
        }
    }
}
