
package com.crio.warmup.stock;
import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;

import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.util.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

public static RestTemplate restTemplate=new RestTemplate();
public static PortfolioManager portfolioManager=PortfolioManagerFactory.getPortfolioManager(restTemplate);
public static String getToken()
{
  return "3e046e737fef392f9b502d74e50501257a5793d3";
}
//old changes

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
//args[0]args[0]

   File f = resolveFileFromResources(args[0]);  
   ObjectMapper om = getObjectMapper();  
   PortfolioTrade[] trades=om.readValue(f, PortfolioTrade[].class);  
   List<String> arr = new ArrayList<>();   

   for(PortfolioTrade trade:trades)    {  

    arr.add(trade.getSymbol());        

  }   

 return arr; 

 }








  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>



  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues
 
  public static List<String> debugOutputs() {

     String valueOfArgument0 = "trades.json";
     String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/jaisminjata444-ME_QMONEY_V2/qmoney/bin/main/trades.json";
     String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@815b41f";
     String functionNameFromTestFileInStackTrace = "mainReadFile";
     String lineNumberFromTestFileInStackTrace = "29";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }
 

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {

    RestTemplate rt=new RestTemplate();
    String apiKey="3e046e737fef392f9b502d74e50501257a5793d3";
    List<PortfolioTrade> pt=readTradesFromJson(args[0]);
    List<TotalReturnsDto>ls=new ArrayList<TotalReturnsDto>();
    for(PortfolioTrade trades:pt)
    {
      String symbol = trades.getSymbol();
      LocalDate localDate=LocalDate.parse(args[1]);
      String Url=prepareUrl(trades, localDate, apiKey);
      TiingoCandle[] tc=rt.getForObject(Url, TiingoCandle[].class);
      if(tc==null)
      {
        continue;
      }
      TotalReturnsDto temp=new TotalReturnsDto(symbol,tc[tc.length-1].getClose());
      ls.add(temp);
    }
     Collections.sort(ls,new Comparator<TotalReturnsDto>() {
      @Override
      public int compare(TotalReturnsDto p1,TotalReturnsDto p2)
      {
        return (int)(p1.getClosingPrice().compareTo(p2.getClosingPrice()));
      }
    });
    List<String>ans=new ArrayList<>();
    for(int i=0;i<ls.size();i++)
    {
      ans.add(ls.get(i).getSymbol());
    }
    return ans;
  
  
  }

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
   ObjectMapper om = getObjectMapper();
   PortfolioTrade[] pf = om.readValue(resolveFileFromResources(filename), PortfolioTrade[].class);
   List<PortfolioTrade> ls = Arrays.asList(pf);
   return ls;
 
 
    
  }


  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    
     String url="https://api.tiingo.com/tiingo/daily/"+trade.getSymbol()+"/prices?startDate="+trade.getPurchaseDate()+"&endDate="+endDate+"&token="+token;
     return url;
  }
//new changes
  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.

   


  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  public static Double getOpeningPriceOnStartDate(List<Candle> candles) {      
    return candles.get(0).getOpen();
 }

 public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size()-1).getClose();
 }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
     RestTemplate rt=new RestTemplate();
     String url=prepareUrl(trade, endDate, token);
     List<Candle>ls=Arrays.asList(rt.getForObject(url, TiingoCandle[].class));

     return ls;
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
      List<AnnualizedReturn>annualizedReturnsList=new ArrayList<>();
      List<PortfolioTrade> trades=readTradesFromJson(args[0]);

      LocalDate endDate=LocalDate.parse(args[1]);
      for(PortfolioTrade eachTrade:trades)
      {
        List<Candle>Candles=fetchCandles(eachTrade, endDate, getToken());
        annualizedReturnsList.add(calculateAnnualizedReturns(endDate, eachTrade, getOpeningPriceOnStartDate(Candles),getClosingPriceOnEndDate(Candles)));
      }
     
      Collections.sort(annualizedReturnsList,new Comparator<AnnualizedReturn>() {
        @Override
        public int compare(AnnualizedReturn a1,AnnualizedReturn a2)
        {
          return (int)(a1.getAnnualizedReturn().compareTo(a2.getAnnualizedReturn()));
        }
      });
      Collections.reverse(annualizedReturnsList);

     return annualizedReturnsList;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        double totalReturn=(sellPrice-buyPrice)/buyPrice;
        LocalDate startDate=trade.getPurchaseDate();
        double totalNumberOfYears=startDate.until(endDate,ChronoUnit.DAYS)/365.24;
        double annualizedReturn=Math.pow((1+totalReturn),(1/totalNumberOfYears))-1;
      return new AnnualizedReturn(trade.getSymbol(), annualizedReturn, totalReturn);
      }








  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

 
  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
  throws Exception {
   String file = args[0];
   LocalDate endDate = LocalDate.parse(args[1]);
   String contents = readFileAsString(file);
   ObjectMapper objectMapper = getObjectMapper();
   PortfolioTrade[] portfolioTrades=objectMapper.readValue(contents,PortfolioTrade[].class);
   return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
}



  public static String readFileAsString(String file) throws IOException, URISyntaxException{
    BufferedReader reader = new BufferedReader(new FileReader(file));
    StringBuilder stringBuilder = new StringBuilder();
    String line = null;
    String ls = System.getProperty("line.separator");
    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line);
      stringBuilder.append(ls);
    }
    // delete the last new line separator
    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    reader.close();
    String content = stringBuilder.toString();
    return content;
  }

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());
    printJsonObject(mainReadQuotes(args));
    printJsonObject(mainCalculateSingleReturn(args));
    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}
