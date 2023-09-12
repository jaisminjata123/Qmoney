
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.management.RuntimeErrorException;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {




  private RestTemplate restTemplate;


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  public static String getToken()
{
  return "3e046e737fef392f9b502d74e50501257a5793d3";
}


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF



 
  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        if(from.compareTo(to)>=0)
        {
          throw new RuntimeException();
        }
        String url=buildUri(symbol, from, to);
        TiingoCandle[] stockStartToEndDate=restTemplate.getForObject(url, TiingoCandle[].class);
        if(stockStartToEndDate==null)

        {
          return new ArrayList<Candle>();
        }
        else{
          List<Candle>stockList=Arrays.asList(stockStartToEndDate);
          return stockList;
        }
      
     
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String url="https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?startDate="+startDate+"&endDate="+endDate+"&token="+getToken();
    return url;
  }


 @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) {

    // TODO Auto-generated method stub
    AnnualizedReturn annualizedReturn;
    List<AnnualizedReturn>annualizedReturns=new ArrayList<AnnualizedReturn>();
    for(int i=0;i<portfolioTrades.size();i++)
    {
      annualizedReturn=getAnnualizedReturn(portfolioTrades.get(i),endDate);
      annualizedReturns.add(annualizedReturn);
    }
    Comparator<AnnualizedReturn>sortByAnnualReturn=Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
    Collections.sort(annualizedReturns,sortByAnnualReturn);
    return annualizedReturns;
  }




  private AnnualizedReturn getAnnualizedReturn(PortfolioTrade trade, LocalDate endDate) {
    AnnualizedReturn annualizedReturn;
    String symbol=trade.getSymbol();
    LocalDate startDate=trade.getPurchaseDate();
    try{
      //Fetch data
      List<Candle>stockStartToEnd;
      stockStartToEnd=getStockQuote(symbol, startDate, endDate);
      Candle stockStartDate=stockStartToEnd.get(0);
      Candle stockEndDate=stockStartToEnd.get(stockStartToEnd.size()-1);

      //get stock price on open and close dates
        Double buyPrice=stockStartDate.getOpen();
        Double sellPrice=stockEndDate.getClose();

      //calculate total returns
      Double totalReturn =(sellPrice-buyPrice)/buyPrice;

      //calculate years
      Double totalNumberOfYears=startDate.until(endDate,ChronoUnit.DAYS)/365.24;

      //calculate annualized return using formula
      Double annualizedReturns=Math.pow((1+totalReturn),(1/totalNumberOfYears))-1;

      annualizedReturn=new AnnualizedReturn(symbol, annualizedReturns, totalReturn);



    }
    catch(JsonProcessingException e)
    {
      annualizedReturn=new AnnualizedReturn(symbol, Double.NaN, Double.NaN);
    }

    return annualizedReturn;
  }


 
}
