package testscripts;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.coingecko.genericutility.BaseClass;
import io.restassured.response.Response;
import teststeps.APITestSteps;

public class StatusCodeAndFormatTest extends BaseClass
{
	@Test(description = "TC_001: Validate Status Code", priority = 1)
	public void verifyAPIStatusCode()
	{
	    Response response = APITestSteps.getResponseForCurrency(javaUtils.propertyData("endpoint"), "usd");

	    Assert.assertEquals(response.getStatusCode(), 200, "Expected Status code: 200 but got "+response.getStatusCode());
	    test.pass(response.getStatusCode() == 200 ? "Status code is 200" : "Status code is not 200");
	    test.info("API response time: " + response.getTime() + "ms");
	}
	
	@Test(description = "TC_002: Validate Response Format", priority = 2)
	public void verifyAPIResponseFormat()
	{
	    Response response = APITestSteps.getResponseForCurrency(javaUtils.propertyData("endpoint"), "usd");
		
	    Assert.assertTrue(response.getContentType().equals("application/json; charset=utf-8"), "Response is not in JSON format");
	    test.pass(response.getContentType().equals("application/json; charset=utf-8") ? "Response is in JSON format" : "Response is not in Json Format");
	    test.info("API response time: " + response.getTime() + "ms");
	}
}
