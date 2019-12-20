package currencyconverter;

import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import currencyconverter.ExchangeRatesSeries;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.util.List;
import java.util.stream.*;

@RestController
public class CurrencyConverterController {
  private List<String> availableCurrencies = Arrays.asList("EUR", "USD", "CAD", "RUB", "JPY");
  private static final String NBP_API_URL_TEMPLATE = "http://api.nbp.pl/api/exchangerates/rates/A/%s?format=json";
  private final AtomicLong counter = new AtomicLong();
  //private static final LoggingJdbcConfiguration logger = new LoggingJdbcConfiguration();

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
	return builder.build();
  }

  @Autowired
  public RestTemplate restTemplate;

  public double getExchangeRateToPLN(String currency) {
    String uri = String.format(NBP_API_URL_TEMPLATE, currency);
    ExchangeRatesSeries rateSeries = restTemplate.getForObject(uri, ExchangeRatesSeries.class);
    double rate = rateSeries.getRate();
    //logger.addEntry(counter.incrementAndGet(), "queryExchangeRate", "api.nbp.pl", currency, "PLN", 1, String.valueOf(rate));
    return rate;
  }

  public double getExchangeRateFromPLN(String currency) {
    return 1.0/getExchangeRateToPLN(currency);
  }

  public double reverse(double f) {
    return 1.0/f;
  }

  @RequestMapping(value="/currency/{id}", method=RequestMethod.GET)
  @ResponseBody
  public String printExchangeRateForCurrency(
    @PathVariable("id") String currencyCode) throws Exception {
      String uri = String.format(NBP_API_URL_TEMPLATE, currencyCode);
      ExchangeRatesSeries rateSeries = restTemplate.getForObject(uri, ExchangeRatesSeries.class);
      String response = rateSeries.toString();
      //logger.addEntry(counter.incrementAndGet(), "printExchangeRateForCurrency", "api.nbp.pl", currencyCode, "PLN", 0.0, response);
      return response;
    }

  @RequestMapping(value="/currencies/available", method=RequestMethod.GET)
  @ResponseBody
  public String getAvailableCurrencies() {
    //logger.addEntry(counter.incrementAndGet(), "getAvailableCurrencies", "localhost", "PLN", "", 0, "");
    StringBuilder sb = new StringBuilder("You can convert:<br><br>");
    sb.append("<ul>");
    availableCurrencies.forEach(currency -> sb.append(String.format("<li>%s to PLN &nbsp;&nbsp; & &nbsp;&nbsp; PLN to %s</li>", currency, currency)));
    sb.append("</ul>");
    return sb.toString();
  }

  @RequestMapping(value="/currencies/exchangerates", method=RequestMethod.GET)
  @ResponseBody
  public String getExchangeRates() {
    //logger.addEntry(counter.incrementAndGet(), "getExchangeRates", "localhost", "PLN", null, 0, null);
    StringBuilder sb = new StringBuilder("Currency exchange rates:<br>");
    sb.append("<ul>");
    availableCurrencies.forEach(currencyCode -> {
        double rateToPLN = getExchangeRateToPLN(currencyCode);
        double rateToCurrency = reverse(rateToPLN);
        sb.append(String.format("<li>1 %s = %f PLN", currencyCode, rateToPLN));
        sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        sb.append(String.format("1 PLN = %f %s</li>", rateToCurrency, currencyCode));
    });
    sb.append("</ul>");
    return sb.toString();
  }

  @RequestMapping(value="/convert/{from}/{to}", params="value", method=RequestMethod.GET)
  @ResponseBody
  public String convertAmountFromTo(
      @PathVariable("from") String from,
      @PathVariable("to") String to,
      @RequestParam(name="value", defaultValue="1") String value
  ) throws Exception {
      // Param Validation
      if(from.equals(to))
        throw new Exception("Cannot convert to the same currency");
      if(!from.equals("PLN") && !to.equals("PLN"))
        throw new Exception("Converting between other currencies than PLN not supported yet");
      
      // Calculation
      double doubleVal = Double.valueOf(value);
      double result = 0.0;
      double exchangeRate = 0.0;
      if(from.equals("PLN")) {
          exchangeRate = getExchangeRateFromPLN(to);
      } else if(to.equals("PLN")) {
          exchangeRate = getExchangeRateToPLN(from); 
      }
      result = doubleVal*exchangeRate;

      String formattedResult = String.format("%.2f", result);
      //logger.addEntry(counter.incrementAndGet(), "convertAmountFromTo", "localhost", from, to, Double.valueOf(value), formattedResult);
      return "Your requested conversion:<br><br>" +
             "<p>" + value + " " + from + " = " + formattedResult + " " + to;
  }
}