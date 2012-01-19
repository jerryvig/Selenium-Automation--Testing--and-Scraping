import org.openqa.selenium.firefox.FirefoxDriver;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

public class PhoneTrades {
  public static void main( String[] args ) {
    try {

     CSVWriter myWriter = new CSVWriter( new FileWriter( "./PhoneTrades.csv" ) );
     myWriter.writeNext( new String[]{ "Item" } );

     FirefoxDriver driver = new FirefoxDriver();

     String searchString = "valve";

     //Scrape Ebay Instant Sale
     driver.get( "http://instantsale.ebay.com/" ); 

     List<WebElement> searchInput = driver.findElementsByName("search");
     searchInput.get(0).sendKeys( "iphone 16gb" );
     
     List<WebElement> submitButton = driver.findElementsByXPath("//input[@title='Search Instant Sale']");
     submitButton.get(0).click();

     List<WebElement> tdList = driver.findElementsByXPath("//td[@class='searchResultsProductInfo']");
    
     for ( WebElement td : tdList ) {
	 myWriter.writeNext( new String[] { td.getText() } );
     }
    
     //Scrape Flipswap
     driver.get( "http://tradein.flipswap.com/" );
    
     List<WebElement> queryInput = driver.findElementsByName("search_terms");
     queryInput.get(0).clear();
     queryInput.get(0).sendKeys( "iphone 16gb" );
     
     List<WebElement> searchButton = driver.findElementsByXPath("//input[@value='Search']");
     searchButton.get(0).click();
 
     List<WebElement> boxDiv = driver.findElementsByXPath("//p[@class='device-name']");
     boxDiv.get(0).click();
    
     List<WebElement> inputList = driver.findElementsByName("CELL_PHONE_DAMAGE_DROPDOWN");
     inputList.get(0).click();

     inputList = driver.findElementsByName("CELL_PHONE_WATER_DAMAGE");
     inputList.get(0).click();

     inputList = driver.findElementsByName("CELL_PHONE_POWER_UP_DROPDOWN");
     inputList.get(0).click();

     inputList = driver.findElementsByName("CELL_PHONE_LCD_FUNCTIONALITY_DROPDOWN");
     inputList.get(0).click();

     inputList = driver.findElementsByName("SN");
     inputList.get(0).sendKeys( "1" );
     
     inputList = driver.findElementsById("submit");
     inputList.get(0).click();
   
     boxDiv = driver.findElementsByXPath("//div[@class='box']");
     myWriter.writeNext( new String[]{ boxDiv.get(0).getText() } );

     //Scrape VZW trade in
     driver.get( "https://www.trade-in.vzw.com/AppraisalPage.aspx" );
 
     List<WebElement> optionList = driver.findElementsByXPath("//option[@value='Apple']");
     try {
       optionList.get(0).click();
     } catch ( Exception e ) { e.printStackTrace(); }
      
     Thread.sleep( 4000 );
     
     List<WebElement> selectList = driver.findElementsById("ctl00_cphMain_ddlModel");
     List<WebElement> optionListII = selectList.get(0).findElements( By.tagName("option") );     
     System.out.println( optionListII.size() );

     try {
      for ( WebElement myOpt : optionListII ) {
         System.out.println( myOpt.getText() );
	 if ( myOpt.getText().equals("iPhone 3G 16GB") ) {
	   System.out.println( "you are here." );
           try {
	     myOpt.click();
           } catch ( Exception e ) { e.printStackTrace(); }
	   } 
      }
     }  catch ( Exception e ) { e.printStackTrace(); }
    
     Thread.sleep( 3000 );

     driver.findElementsById("ctl00_cphMain_rdoYes0").get(0).click();
     driver.findElementsById("ctl00_cphMain_rdoYes1").get(0).click();
     driver.findElementsById("ctl00_cphMain_rdoNo2").get(0).click();

     Thread.sleep( 1000 );
    
     driver.findElementsById("ctl00_cphMain_imgBtnNext").get(0).click();

     Thread.sleep( 1000 );

     String appraisedValue = driver.findElementsById("ctl00_cphMain_dvNormalFlow").get(0).getText();
     myWriter.writeNext( new String[]{ appraisedValue } );

     myWriter.close();

    }
    catch ( Exception e ) { e.printStackTrace(); }
  }
}
