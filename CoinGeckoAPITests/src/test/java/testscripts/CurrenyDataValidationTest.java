package testscripts;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.coingecko.genericutility.BaseClass;

import io.restassured.response.Response;
import teststeps.APITestSteps;

public class CurrenyDataValidationTest extends BaseClass
{
   @Test(description = "TC_003: Validate at least one cryptocurrency is returned", priority = 1)
   public void verifyAtLeastOneCryptoIsReturned() 
   {
	Response response = APITestSteps.getResponseForCurrency(javaUtils.propertyData("endpoint"), "usd");

        List<Map<String, Object>> jsonResponse = response.jsonPath().getList("$");
        Assert.assertTrue(jsonResponse.size() > 0, "No cryptocurrencies returned!");
        test.pass("At least one cryptocurrency is returned");
        test.info("API response time: " + response.getTime() + "ms");
    }
	
    @Test(description = "TC_004: Validate usd Currency", priority = 2)
    public void verifyCurrencyData() 
    {
    	Response response = APITestSteps.getResponseForCurrency(javaUtils.propertyData("endpoint"), "usd"); //Testing with usd

        Assert.assertEquals(response.getStatusCode(), 200);
        String currency = response.jsonPath().getString("0.current_price");
        Assert.assertNotNull(currency, "Currency price is null for USD");
        test.pass("Validated usd currency");
        test.info("API response time: " + response.getTime() + "ms");
    }
    
    @Test(description = "TC_005: Validate Bitcoin Price", priority = 3)
    public void verifyBitcoinPriceIsValid() 
    {
    	Response response = APITestSteps.getResponseForCurrency(javaUtils.propertyData("endpoint"), "usd");

    	Map<String,Object> bitcoin = APITestSteps.getBitcoinData(response);
	Assert.assertNotNull(bitcoin, "Bitcoin data not found!");
		
        boolean isValid = APITestSteps.isBitcoinPriceValid(bitcoin);
        
	Assert.assertTrue(isValid, "Bitcoin price is not a valid number or not greater than zero!");
		
	test.pass(isValid ? "Bitcoin price is a valid number" : "Bitcoin price is not a valid number");
	test.info("API response time: " + response.getTime() + "ms");
    }
    
    @Test(description = "TC_006: Validate Data Updation Within The Last 60 Seconds", priority = 4)
    public void verifyDataIsUpdatedWithinLast60Seconds() 
    {
    	Response response = APITestSteps.getResponseForCurrency(javaUtils.propertyData("endpoint"), "usd");

        Map<String,Object> bitcoin = APITestSteps.getBitcoinData(response);

        Assert.assertNotNull(bitcoin, "Bitcoin data not found!");
        
        String lastUpdated = (String) bitcoin.get("last_updated");
        long lastUpdatedTime = java.time.Instant.parse(lastUpdated).toEpochMilli();
        long currentTime = System.currentTimeMillis();

        Assert.assertTrue((currentTime - lastUpdatedTime) < 60000, "Data is not updated within the last 60 seconds!");
        test.pass((currentTime - lastUpdatedTime) < 60000 ? "Data is updated within the last 60 seconds" : "Data not updated within the last 60 seconds");
        test.info("API response time: " + response.getTime() + "ms");
    }
    
    @Test(description = "TC_007: Validate Response Contains Essential Cryptocurrency Fields", priority = 5)
    public void verifyResponseContainsEssentialFields() 
    {
    	Response response = APITestSteps.getResponseForCurrency(javaUtils.propertyData("endpoint"), "usd");

        Map<String, Object> cryptoData = APITestSteps.getCryptoData(response);

        Assert.assertTrue(cryptoData.containsKey("id"), "Response does not contain 'id' field!");
        Assert.assertTrue(cryptoData.containsKey("symbol"), "Response does not contain 'symbol' field!");
        Assert.assertTrue(cryptoData.containsKey("current_price"), "Response does not contain 'current_price' field!");
        test.pass(cryptoData.containsKey("id") && cryptoData.containsKey("symbol") && cryptoData.containsKey("current_price") ?
        		"Response contains id, symbol and current_price fields" : "Response does not contain id, symbol and current_price fields");
        test.info("API response time: " + response.getTime() + "ms");
    }
}
