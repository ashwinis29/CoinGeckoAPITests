package testscripts;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.coingecko.genericutility.BaseClass;

import io.restassured.response.Response;
import teststeps.APITestSteps;

public class SortingPaginationTest extends BaseClass
{
	@Test(description = "TC_008: Validate sorting by market cap in ascending order", priority = 1)
    public void verifyMarketCapSortingAscending() 
	{
        Response response = APITestSteps.getResponseForMarketCapSorting(javaUtils.propertyData("endpoint"), "usd", "market_cap_asc");
        
        List<Map<String, Object>> jsonResponse = APITestSteps.getAllCryptoData(response);

        //Validate Sorting Order
        APITestSteps.validateAscendingOrder(jsonResponse, "market_cap");
        
        test.pass("Sorted by market_cap in ascending order");
        test.info("API response time: " + response.getTime() + "ms");
    }
    
    @Test(description = "TC_009: Validate Pagination works correctly for 100 records", priority = 2)
    public void verifyPagination() 
    {
        Response response = APITestSteps.getPaginatedResponse(javaUtils.propertyData("endpoint"), "usd", 100, 1);
        
        //Get All Crypto Data
        List<Map<String, Object>> recordsList = APITestSteps.getAllCryptoData(response);
        
        //Validate Pagination 
        Assert.assertEquals(recordsList.size(), 100, "Page does not contain 100 records!");
        Assert.assertTrue(recordsList.get(0).containsKey("id"), "Invalid data structure!");
        test.pass(recordsList.size() == 100 ? "Pagination works for 100 records" : "Page does not contain 100 records!");
        test.info("API response time: " + response.getTime() + "ms");
    }
    
    @Test(description = "TC_010: Validate Page 1 and Page 2 doesn't have duplicate data", priority = 3)
    public void verifyPaginationNoDuplicates() {
        Response page1Data = APITestSteps.getPaginatedResponse(javaUtils.propertyData("endpoint"), "usd", 100, 1);

        Response page2Data = APITestSteps.getPaginatedResponse(javaUtils.propertyData("endpoint"), "usd", 100, 2);
        
        List<Map<String, Object>> page1DataList = APITestSteps.getAllCryptoData(page1Data);
        
        List<Map<String, Object>> page2DataList = APITestSteps.getAllCryptoData(page2Data);

        Assert.assertNotEquals(page1DataList.get(0).get("id"), page2DataList.get(0).get("id"), "Duplicate data found between Page 1 and Page 2!");
        test.pass("Unique data found in Page 1 and Page 2");
    }
    
    @Test(description = "TC_011: Validate Requesting a very high page number returns an empty response or valid error", priority = 4)
    public void verifyHighPageNumber() {
        Response response = APITestSteps.getPaginatedResponse(javaUtils.propertyData("endpoint"), "usd", 100, 1000);
        
        Object jsonResponse = response.jsonPath().get("$");

        boolean isEmptyResponse = (jsonResponse instanceof List && ((List<?>) jsonResponse).isEmpty()) ||
                (jsonResponse instanceof Map && ((Map<?, ?>) jsonResponse).isEmpty());

        Assert.assertTrue(isEmptyResponse, "High page number doesn not return empty data!");
        test.pass(isEmptyResponse ? "High page number returns empty response" : "High page number does not return empty data!");
    }
}
