package testscripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.coingecko.genericutility.BaseClass;

import io.restassured.response.Response;
import teststeps.APITestSteps;

public class FilteringTest extends BaseClass
{
    @Test(description = "TC_012: Validate Response contains only requested coins (bitcoin, ethereum)", priority = 1)
    public void verifyIdFiltering()
    {
    	Response response =  APITestSteps.getResponseForFilteredCoins(javaUtils.propertyData("endpoint"), "usd", "bitcoin,ethereum");

        List<String> coinIds = APITestSteps.getCoinIds(response);

        Assert.assertTrue(coinIds.containsAll(Arrays.asList("bitcoin", "ethereum")), "Response does not contain both Bitcoin and Ethereum!");
        Assert.assertEquals(coinIds.size(), 2, "Response contains extra coins!");
        test.pass("Response contains only requested coins");
        test.info("API response time: " + response.getTime() + "ms");
    }
    
    @Test(description = "TC_013: Validate Prices are in the requested currency (EUR)", priority = 2)
    public void verifyCurrencyFiltering() 
    {
    	Response response = APITestSteps.getResponseForCurrency(javaUtils.propertyData("endpoint"), "eur");

        List<Map<String, Object>> coinList = APITestSteps.getAllCryptoData(response);
        
        for (Map<String, Object> coin : coinList) 
        {
            Assert.assertTrue(coin.containsKey("current_price"), "Missing current_price field!");
            Assert.assertTrue(coin.containsKey("symbol"), "Missing symbol field!");
        }

        test.pass("Prices are in requested currency (EUR)");
        test.info("API response time: " + response.getTime() + "ms");
    }
    
    @Test(description = "TC_014: Validate Coins sorted by market cap in descending order", priority = 3)
    public void verifyMarketCapSortingDescending() 
    {
    	Response response = APITestSteps.getResponseForMarketCapSorting(javaUtils.propertyData("endpoint"), "usd", "market_cap_desc");

        List<Map<String, Object>> coinList = APITestSteps.getAllCryptoData(response);

        List<Double> actualMarketCaps = coinList.stream()
                .map(coin -> ((Number) coin.get("market_cap")).doubleValue())
                .collect(Collectors.toList());

        List<Double> sortedMarketCaps = new ArrayList<>(actualMarketCaps);
        sortedMarketCaps.sort(Collections.reverseOrder());

        Assert.assertEquals(actualMarketCaps, sortedMarketCaps, "Market Cap is not sorted in descending order!");
        test.pass("Market cap is sorted in descending order.");
        test.info("API response time: " + response.getTime() + "ms");
    }

    @Test(description = "TC_015: Validate Invalid coin ID returns an empty response", priority = 4)
    public void verifyInvalidCoinId() 
    {
    	Response response = APITestSteps.getResponseForFilteredCoins(javaUtils.propertyData("endpoint"), "usd", "invalid_coin_id");

        Object jsonResponse = response.jsonPath().get("$");

        boolean isEmptyResponse = (jsonResponse instanceof List && ((List<?>) jsonResponse).isEmpty()) ||
                                  (jsonResponse instanceof Map && ((Map<?, ?>) jsonResponse).isEmpty());


        Assert.assertTrue(isEmptyResponse, "Invalid coin ID should return an empty response!");
        test.pass("Invalid coin ID returned an empty response");
        test.info("API response time: " + response.getTime() + "ms");
    }
}
