package com.currencyExchange.audit;

import com.currencyExchange.audit.models.ExchangeDetails;
import com.currencyExchange.audit.models.RatesExchangeDetails;
import com.currencyExchange.audit.services.AuditServices;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootTest
class AuditApplicationTests {

	@Autowired
	private AuditServices auditServices;


	@Test
	void apiTest() throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://api.apilayer.com/exchangerates_data/convert?to=USD&from=AED&amount=1"))
				.header("apikey", "dfehaCM5GreNMoNvNlezawMb07bB8c0m")
				.header("contentType","application/json")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
	}

	@Test
	void apiTestWithDate() throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://api.apilayer.com/exchangerates_data/2023-02-01?symbols=AED%2CJPY%2CINR&base=USD"))
				.header("apikey", "dfehaCM5GreNMoNvNlezawMb07bB8c0m")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
	}




	@Test
	void apiTest2() throws IOException {
		List<String> countries = new ArrayList<String>();
		countries.add("INR");
		countries.add("AED");
		countries.add("JPY");
		countries.add("CAD");
		countries.add("EUR");
		ExchangeDetails exchangeDetails = new ExchangeDetails();
		exchangeDetails.setDate("2022-01-01");
		exchangeDetails.setCountries(countries);
		System.out.println(auditServices.FetchAllExchangeRates(exchangeDetails));
	}

	@Test
	 public void writingInExcelTest() throws IOException {
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Persons");
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 4000);

		Row header = sheet.createRow(0);

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 16);
		font.setBold(true);
		headerStyle.setFont(font);

		Cell headerCell = header.createCell(0);
		headerCell.setCellValue("Name");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(1);
		headerCell.setCellValue("Age");
		headerCell.setCellStyle(headerStyle);

		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);

		Row row = sheet.createRow(2);
		Cell cell = row.createCell(0);
		cell.setCellValue("John Smith");
		cell.setCellStyle(style);

		cell = row.createCell(1);
		cell.setCellValue(20);
		cell.setCellStyle(style);

		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

		FileOutputStream outputStream = new FileOutputStream(fileLocation);
		workbook.write(outputStream);
		workbook.close();
	}

	@Test
	public void appendExcel() throws IOException{
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

		Workbook workbook = new XSSFWorkbook(new FileInputStream(fileLocation));

		Sheet sheet = workbook.getSheetAt(0);
		int rowNum = sheet.getLastRowNum() + 1;

		Row row = sheet.createRow(rowNum);
		Cell cell = row.createCell(0);
		cell.setCellValue("khqh,EQ .JKA");

		cell = row.createCell(1);
		cell.setCellValue(40);

		FileOutputStream outputStream = new FileOutputStream(fileLocation);
		workbook.write(outputStream);
		workbook.close();
	}

	@Test
	public void creatingExcelFile() throws IOException {
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Details");
		sheet.setColumnWidth(0, 8000);
		sheet.setColumnWidth(1, 8000);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 4000);

		Row header = sheet.createRow(0);

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		headerStyle.setFont(font);

		Cell headerCell = header.createCell(0);
		headerCell.setCellValue("Base_Currency");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(1);
		headerCell.setCellValue("Conversion_Currency");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(2);
		headerCell.setCellValue("Rate");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(3);
		headerCell.setCellValue("TimeStamp");
		headerCell.setCellStyle(headerStyle);


		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "Currency_Exchange_Details.xlsx";

		FileOutputStream outputStream = new FileOutputStream(fileLocation);
		workbook.write(outputStream);
		workbook.close();
	}

	@Test
	void readExcel() throws IOException, ParseException {
		Workbook workbook = new XSSFWorkbook(new FileInputStream("Currency_Exchange_Details.xlsx"));
		Sheet sheet = workbook.getSheetAt(0);
		List<RatesExchangeDetails> li = new ArrayList<RatesExchangeDetails>();
		int i = 0;
		for(Row row : sheet){
			if(i == 0){
				i = 1;
			}
			else {
				SimpleDateFormat formatter=new SimpleDateFormat("E MMM dd hh:mm:ss z yyyy");
				Date date = formatter.parse(row.getCell(3).toString());
				RatesExchangeDetails rde = new RatesExchangeDetails();
				rde.setBase_Curency(row.getCell(0).getStringCellValue());
				rde.setConversion_Curency(row.getCell(1).getStringCellValue());
				rde.setRate(row.getCell(2).getNumericCellValue());
				rde.setTimeStamp(date);
				li.add(rde);
			}
		}
		for (RatesExchangeDetails rde: li) {
			System.out.println(rde.getRate());
		}
	}
}
