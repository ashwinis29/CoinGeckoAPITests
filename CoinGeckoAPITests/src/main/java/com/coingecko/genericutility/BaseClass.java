package com.coingecko.genericutility;


import java.lang.reflect.Method;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import io.restassured.RestAssured;

public class BaseClass {
	
	public JavaUtility javaUtils = new JavaUtility();
	protected ExtentReports extent;
    protected ExtentTest test;
	
	@BeforeSuite
	public void setupExtent() 
	{
	    extent = ExtentReportUtility.setupReport();
	}
    @BeforeMethod
    public void setUp(Method method)
    {
    	//Makes sure that extent is not null before creating test
    	if (extent == null) 
    	{
    	     extent = ExtentReportUtility.setupReport();
    	}
    			
        //Create test
    	test = extent.createTest(method.getName());
    	//Set Base URI for API Tests
    	String baseURL = javaUtils.propertyData("baseURL");
        if (baseURL == null || baseURL.isEmpty()) 
        {
            throw new RuntimeException("Base URL is not set. Check config.properties file.");
        }
        RestAssured.baseURI = baseURL;
    }
    
    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.fail("Test Failed: " + result.getThrowable().getMessage());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test Passed.");
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.skip("Test Skipped: " + result.getThrowable().getMessage());
        }
    }

    @AfterSuite
    public void tearDownExtent() {
        if (extent != null) {
            extent.flush();
        }
    }
}
