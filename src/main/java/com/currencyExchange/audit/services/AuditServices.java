package com.currencyExchange.audit.services;

import com.currencyExchange.audit.models.Audit;
import com.currencyExchange.audit.models.ExchangeDetails;
import com.currencyExchange.audit.models.RatesExchangeDetails;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface AuditServices {
    public Map<String,Double> FetchAllExchangeRates(ExchangeDetails exchangeDetails) throws IOException;

    Audit createAudit(Audit audit);
    Audit updateAudit(Audit audit);
    public void writeExcel(TreeSet<RatesExchangeDetails> rDESet) throws IOException;
}
