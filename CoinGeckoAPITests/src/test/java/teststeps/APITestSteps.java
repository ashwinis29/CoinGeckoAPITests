package teststeps;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.Assert;

import com.coingecko.genericutility.JavaUtility;


public class APITestSteps 
{
    public static JavaUtility javaUtils = new JavaUtility();
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 5000; // 5seconds

   //Extract response for currency	
   public static Response getResponseForCurrency(String endpoint, String currency) 
   {
	Response response = given()
                .queryParam("vs_currency", currency)
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
		
	return response;
    }
	
   public static Map<String,Object> getBitcoinData(Response response)
   {
	Object jsonResponse = response.jsonPath().get("$");
        Map<String, Object> bitcoin;

        if (jsonResponse == null) 
        {
            throw new RuntimeException("API response is null");
        }
        
        bitcoin = jsonResponse instanceof List ? ((List<Map<String, Object>>) jsonResponse).stream()
        					.filter(coin -> "bitcoin".equals(coin.get("id"))).findFirst().orElse(null) 
        					: (Map<String, Object>) jsonResponse ;
        return bitcoin;
    }
	
    public static boolean isBitcoinPriceValid(Map<String, Object> bitcoinData) 
    {
	Object price;
		
        if (bitcoinData == null || !bitcoinData.containsKey("current_price")) 
        {
            return false;
        }

        price = bitcoinData.get("current_price");
        
        return price instanceof Number && ((Number) price).doubleValue() > 0;
    }

    //Extract response for single record
    public static Map<String,Object> getCryptoData(Response response)
    {
	Object jsonResponse = response.jsonPath().get("$");
	if (jsonResponse == null) 
        {
            throw new RuntimeException("API response is null");
        }
		
        Map<String, Object> cryptoData = (jsonResponse instanceof List) ? ((List<Map<String, Object>>) jsonResponse).get(0) 
        									: (Map<String, Object>) jsonResponse;
        return cryptoData;
    }
	
    public static Response getResponseForMarketCapSorting(String endpoint, String currency, String order) 
    {
		Response response = given()
                .queryParam("vs_currency", currency)
                .queryParam("order", order)
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
		
		return response;
    }
	
   public static List<Map<String, Object>> getAllCryptoData(Response response) 
   {
	Object jsonResponse = response.jsonPath().get("$");
	return (jsonResponse instanceof List) 
	            ? (List<Map<String, Object>>) jsonResponse 
	            : List.of((Map<String, Object>) jsonResponse);
    }

    //Validate Ascending order
    public static void validateAscendingOrder(List<Map<String, Object>> jsonResponse, String key) 
    {
        for (int i = 0; i < jsonResponse.size() - 1; i++) 
        {
            double current = ((Number) jsonResponse.get(i).get(key)).doubleValue();
            double next = ((Number) jsonResponse.get(i + 1).get(key)).doubleValue();
            Assert.assertTrue(current <= next, key + " is not sorted in ascending order!");
        }
    }

    //Extract response for certain records
    public static Response getPaginatedResponse(String endpoint, String currency, int perPage, int page) 
    {
    	Response response = given()
                .queryParam("vs_currency", currency)
                .queryParam("per_page", perPage)
                .queryParam("page", page)
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
    	
    	return response;
    }

    //429 Too Many Requests retry mechanism
    public static Response getResponseWithRetry(String endpoint, String currency) 
    {
        int statusCode = 429;
        Response response = null;

        for (int i = 0; i < MAX_RETRIES && statusCode == 429; i++) 
        {
            response = getResponseForCurrency(javaUtils.propertyData("endpoint"), "usd");

            statusCode = response.getStatusCode();

            if (statusCode == 429) 
            {
                System.out.println("Rate limit exceeded. Retrying in " + (RETRY_DELAY_MS / 1000) + " seconds...");
                try 
                {
                    Thread.sleep(RETRY_DELAY_MS);
                } 
                catch (InterruptedException e) 
                {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", e);
                }
            }
        }
        return response;
    }

    //Extract response based on coins
    public static Response getResponseForFilteredCoins(String endpoint, String currency, String coins) 
    {
        Response response =  given()
                .queryParam("vs_currency", currency)
                .queryParam("ids", coins)
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        return response;
    }

    //Retrieve coin ids
    public static List<String> getCoinIds(Response response) 
    {
        Object jsonResponse = response.jsonPath().get("$");
        
        List<String> coinIds = (jsonResponse instanceof List) 
                ? ((List<Map<String, Object>>) jsonResponse).stream().map(coin -> (String) coin.get("id")).collect(Collectors.toList()) 
                : List.of(((Map<String, Object>) jsonResponse).get("id").toString());
        
        return coinIds;
    }
}
