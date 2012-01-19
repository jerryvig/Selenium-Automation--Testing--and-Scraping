import org.openqa.selenium.firefox.FirefoxDriver;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

public class GovLiquidation {
  public static void main( String[] args ) {
    try {

     CSVWriter myWriter = new CSVWriter( new FileWriter( "./GovLiquidation.csv" ) );
     myWriter.writeNext( new String[]{ "Item" } );

     FirefoxDriver driver = new FirefoxDriver();

     String searchString = "valve";

     driver.get( "http://www.govliquidation.com/auction/endecaSearch?Ntt=" + searchString + "&Ntk=P_Lot_Title&Ntx=mode+matchall&N=0&Nty=1&Ns=P_Lot_Number|0&words=valve&cmd=keyword&perPage=200" ); 

     List<WebElement> trList = driver.findElementsByTagName("tr");

     for ( WebElement myTr : trList ) {
       if ( myTr.getAttribute("class").equals("resultsBackground1") || myTr.getAttribute("class").equals("resultsBackground2") ) {
	   myWriter.writeNext( new String[]{ myTr.getText() } );
       }   
     }

     myWriter.close();

    }
    catch ( Exception e ) { e.printStackTrace(); }
  }
}
