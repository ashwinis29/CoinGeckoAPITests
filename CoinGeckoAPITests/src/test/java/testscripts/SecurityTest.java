package testscripts;

import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.coingecko.genericutility.BaseClass;

import io.restassured.response.Response;
import teststeps.APITestSteps;

public class SecurityTest extends BaseClass
{
    @Test(description = "TC_019: Validate API is not be vulnerable to SQL Injection", priority = 1)
    public void verifySQLInjectionProtection() 
    {
	//Attempt SQL Injection
	Response response = APITestSteps.getResponseForFilteredCoins(javaUtils.propertyData("endpoint"), "usd", "bitcoin' OR '1'='1");

        int statusCode = response.getStatusCode();

        //The API should return an error code (403, 400, or 422) instead of executing the injection
        List<Integer> expectedStatusCodes = Arrays.asList(403, 400, 422);
        Assert.assertTrue(expectedStatusCodes.contains(statusCode), 
            "Potential SQL Injection vulnerability! API returned status: " + statusCode);
        test.pass("API returns error code 403");
        test.info("API response time: " + response.getTime() + "ms");
    }
    
    @Test(description = "TC_020: Validate API doesn't not execute XSS scripts", priority = 2)
    public void verifyXSSProtection() 
    {
    	//Attempt XSS attack
    	Response response = APITestSteps.getResponseForFilteredCoins(javaUtils.propertyData("endpoint"), "usd", "<script>alert('XSS')</script>");

        String contentType = response.getContentType();
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        //If response is JSON, ensure it does not contain any <script> tags
        if (contentType.contains("application/json")) 
        {
            Assert.assertFalse(responseBody.contains("<script>"), "Potential XSS vulnerability! Script tags found in response.");
            test.fail("API doesn't execute XSS scripts");
            
        } else 
        {
            //If not JSON, ensure API rejects the request
            List<Integer> expectedStatusCodes = Arrays.asList(403, 400, 422);
            Assert.assertTrue(expectedStatusCodes.contains(statusCode), 
                "Potential XSS vulnerability! API returned unexpected status: " + statusCode);
            test.pass("API doesn't execute XSS scripts");
        }
        test.info("API response time: " + response.getTime() + "ms");
    }
    
    @Test(description = "TC_021: Validate API returns an error for invalid parameters", priority = 3)
    public void verifyInvalidParametersHandling() 
    {	
    	//Invalid parameter
    	Response response = APITestSteps.getResponseForFilteredCoins(javaUtils.propertyData("endpoint"), "usd", "abcxyz");

        int statusCode = response.getStatusCode();
        String responseBody = response.asString();

        //Expected error codes: 400 (Bad Request) or 422 (Unprocessable Entity)
        List<Integer> expectedStatusCodes = Arrays.asList(400, 422);
        
        Assert.assertTrue(expectedStatusCodes.contains(statusCode), 
            "Expected 400 or 422, but got: " + statusCode);

        //Response should indicate an invalid request
        Assert.assertTrue(responseBody.toLowerCase().contains("invalid"), 
            "Response does not indicate an invalid request: " + responseBody);
        test.pass("API returns an error for invalid parameters");
        test.info("API response time: " + response.getTime() + "ms");
    }
}
