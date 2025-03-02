package testscripts;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.coingecko.genericutility.BaseClass;

import io.restassured.response.Response;
import teststeps.APITestSteps;

public class RateLimitingAndPerformanceTest extends BaseClass
{
	//Rate Limiting 
    @Test(description = "TC_016: Validate API doesn't return 429 Too Many Requests", priority = 1)
    public void verifyNoRateLimiting() 
    {
        Response response = APITestSteps.getResponseForCurrency(javaUtils.propertyData("endpoint"), "usd");

        Assert.assertNotEquals(response.getStatusCode(), 429, "API returned 429 Too Many Requests!");
        test.pass("API doesn't return 429 Too Many Requests");
        test.info("API response time: " + response.getTime() + "ms");
    }
    
    @Test(description = "TC_017: Validate Handle rate limit by retrying if 429 received", priority = 2)
    public void handleRateLimitWithRetry() 
    {
    	Response response = APITestSteps.getResponseWithRetry(javaUtils.propertyData("endpoint"), "usd");

        Assert.assertNotEquals(response.getStatusCode(), 429, "API still returning 429 after retries!");
        test.pass("Handled rate limiting (Retry if 429 Too Many Requests received)");
        test.info("API response time: " + response.getTime() + "ms");
    }
    
    @Test(description = "TC_018: Response time should be less than 400ms", priority = 3)
    public void verifyResponseTime() {
        long responseTime = given()
                                .queryParam("vs_currency", "usd")
                                .when()
                                .get(javaUtils.propertyData("endpoint"))
                                .then()
                                .extract()
                                .time();

        Assert.assertTrue(responseTime < 400, "Response time exceeded 400ms! Actual: " + responseTime + "ms");
        test.pass("Response time is less than 400ms");
        test.info("API response time: " + responseTime + "ms");
    }
}
