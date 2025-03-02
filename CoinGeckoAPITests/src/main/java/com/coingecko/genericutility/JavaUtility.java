package com.coingecko.genericutility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class JavaUtility {
	
	private static Properties pobj;
	
	static
	{
  		try {
  			pobj = new Properties();
			pobj.load(new FileInputStream("./src/main/resources/config.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 /**
		 * This method will be used to read the common data from the property file
		 * @author Ashwini Saravannavar
		 * @param key
		 * @return value
		 * @throws FileNotFoundException
		 * @throws IOException
		 * 
		 */
	
	    //Read data from properties file
	  	public String propertyData(String key)
	  	{
	  		String value = pobj.getProperty(key);
	  		return value;
	  	}
	  	
	  	
}
