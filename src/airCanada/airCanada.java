package airCanada;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;



import org.openqa.selenium.JavascriptExecutor;

public class airCanada {
    static WebDriver driver;
    static Wait<WebDriver> wait;

    public static void main(String[] args) {
    	String url="https://www.aircanada.com/ca/en/aco/home.html";    	
    	boolean reach=true;
    	//reach=reachable(url);
    	if (false==reach)
    	{
            System.out.println(url+" is not reachable. Test failed.");
            System.exit(1);
    	}
        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, 30);
        driver.get(url);

        new WebDriverWait(driver, 30).until((ExpectedCondition<Boolean>) wd ->
        ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("enCAEdition")));
        boolean result;
        try {
            result = goodDeal();
        } catch(Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            driver.close();
        }

        System.out.println("Test " + (result? "passed." : "failed."));
        if (!result) {
            System.exit(1);
        }
    }
    public static boolean reachable(String url){    	
        HttpURLConnection connection = null;
        boolean result=false;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            int code = connection.getResponseCode();
            if (code != 404){
            	result=true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private static boolean goodDeal() {
    	boolean result=true;
    	Actions action = new Actions(driver);
    	JavascriptExecutor jse=(JavascriptExecutor)driver;
    	//language
    	driver.findElement(By.id("enCAEdition")).click();
    	
    	result=validate();
    	
    	// flight
        driver.findElement(By.id("tab_magnet_title_0")).click(); 
        //roundtrip
        driver.findElement(By.id("roundTrip")).click(); 
    	//depart
        WebElement depart=driver.findElement(By.xpath("//div[@id='flightLocationListOrginId0Label']"));
    	action.moveToElement(depart).click().build().perform();
    	action.moveToElement(depart).sendKeys("Vancouver").build().perform();

    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flightLocationListOrginId0_locationListItem_0")));
    	action.moveToElement(driver.findElement(By.id("flightLocationListOrginId0_locationListItem_0"))).click().build().perform();
  	
    	//destination
        WebElement destAddr=driver.findElement(By.id("destination_label_0"));
    	action.moveToElement(driver.findElement(By.id("destination_label_0"))).click().build().perform();
    	action.moveToElement(destAddr).sendKeys("Toronto").build().perform();
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flightLocationListDestinationId0_locationListItem_0")));
    	action.moveToElement(driver.findElement(By.id("flightLocationListDestinationId0_locationListItem_0"))).click().build().perform();
    	   	
     
        // depart date
    	WebElement departDate=driver.findElement(By.xpath("//div[@id='departureDateLabel']"));
    	
    	driver.findElement(By.xpath("//div[@id='departureDateLabel']")).click();
    	jse.executeScript("arguments[0].click();", departDate);
    	action.moveToElement(departDate).moveToElement(driver.findElement(By.xpath("//div[@id='ui-datepicker-div']/div[2]/table/tbody/tr[1]/td[7]"))).click().build().perform();
    	
    	// return date
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ui-datepicker-div")));
    	driver.findElement(By.xpath("//div[@id='ui-datepicker-div']/div[2]/table/tbody/tr[5]/td[2]")).click();

    	//WebElement returnDate=driver.findElement(By.xpath("//div[@id='returnDateLabel']"));
    	//action.moveToElement(returnDate).moveToElement(driver.findElement(By.xpath("//div[@id='ui-datepicker-div']/div[2]/table/tbody/tr[5]/td[5]"))).click().build().perform();
        
    	//passenger 
        WebElement passNum=driver.findElement(By.xpath("//div[@id='passengersInputLabel']"));
    	action.moveToElement(passNum).click().build().perform();
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnAdultCountAdd")));
    	action.moveToElement(driver.findElement(By.id("btnAdultCountAdd"))).click().build().perform();  
    	
    	driver.findElement(By.xpath("//input[@class='search-active-magnet btn btn-primary']")).click();
    	//wait search result
    	wait.until(ExpectedConditions.urlMatches("https://www.aircanada.com/ca/en/aco/home.html#/faredriven:0"));
    	
    	findGoodDeal("5:00");
        // Look for in the results
        return result;
    }

    public static void findGoodDeal(String afterTime){
    	//li: class=ac_itinerary , parent div id flightDetailInfo0, div, ul
    	int low=0;
    	int priceInt;
    	int i=0;
    	WebElement priceE;
    	WebElement priceP;
    	WebElement timeE;
    	WebElement timeEP;
    	WebElement lowElem=null;
    	String timeN;
    	String timeParent="li[@class='ac_itinerary']/div[@id='ac_itinerary-info-0-3']";
    	String priceParent="col-lg-3 col-md-3 col-sm-3 col-xs-12 ac_rol-price ac_econ-core";
    	String timePath="div[@class='col-lg-12 col-md-12 col-sm-12 col-xs-12']/div[@class='ac_flight_info']/span[@class='ac_time-box']/span[@class='ac_time_box_time font_face_b']";
    	String pricePath="li[@class='col-lg-3 col-md-3 col-sm-3 col-xs-12 ac_rol-price ac_econ-core']/a/span/span";
    	WebElement departE=driver.findElement(By.id("avail_bound_main_0"));
    	List<WebElement> elements=departE.findElements(By.xpath("//div[@id='flightDetailInfo0']/div/ul/li[@class='ac_itinerary']"));
    	for (WebElement ele : elements){
    		i=i+1;
    		try {
    			//time comparison
    			timeEP=ele.findElement(By.xpath(timeParent));
    			timeE=timeEP.findElement(By.xpath(timePath));
    			timeN=timeE.getText();
    			if (timeAfter(timeN, afterTime)){
    				//find price
    				priceP=ele.findElement(By.className(priceParent));
    				priceE=priceP.findElement(By.xpath(pricePath));
    				priceInt=Integer.parseInt(priceE.getText());
    				if (1==i){
    					low=priceInt;
    				}  
    				else{
    					if (low>priceInt){
    						low=priceInt;
    						
    						lowElem=priceP.findElement(By.tagName("a"));
    					}
    				}
    			}
    	    } catch (Exception e) { /* ignore this element */
    	    	if (1==i){i=1;}
    	    }
    	  }
    	if (lowElem!=null){
    		lowElem.click();
    	}
    	
    }

    public static boolean timeAfter(String tA, String tB){
    	LocalTime timeA=LocalTime.parse(tA);
    	LocalTime timeB=LocalTime.parse(tB);

    	return timeA.isAfter(timeB);
    }
    public static boolean validate(){
    	boolean result=true;
    	try{
    		WebElement elm=driver.findElement(By.xpath("//input[@id='origin_0']"));
    		if ("FROM" != elm.getAttribute("placeholder")){
    			result=false;
    		}
    	} catch (Exception e) { result=false; }

    	return result;
    }
    public static WebElement filter(String attr, String value, List<WebElement> elements){
    	for (WebElement element : elements){
    		try {
    			if (element.getAttribute(attr) == value) {
        	    	return element;
        	    }
    	    } catch (Exception e) { /* ignore this element */ }
    	    
    	   }
    	return null;
    }
}
