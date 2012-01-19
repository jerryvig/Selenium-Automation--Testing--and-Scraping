import org.openqa.selenium.firefox.FirefoxDriver;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

public class GSAAuctions {
  public static void main( String[] args ) {
    try {

     CSVWriter myWriter = new CSVWriter( new FileWriter( "./GSAAuctions.csv" ) );
     myWriter.writeNext( new String[]{ "Item" } );

     FirefoxDriver driver = new FirefoxDriver();

     driver.get( "http://gsaauctions.gov/gsaauctions/gsaauctions/" ); 

     List<WebElement> inputList = driver.findElementsById("scSrchDescCtxt");
     inputList.get(0).sendKeys( "backhoe" );

     List<WebElement> inputListII = driver.findElementsByName("SearchCtxt");
     inputListII.get(0).click();

     Thread.sleep( 1500 );

     List<WebElement> divList = driver.findElementsByTagName("div");

     for ( WebElement myDiv : divList ) {
       if ( myDiv.getAttribute("class").equals("tr") ) {
	  myWriter.writeNext( new String[]{ myDiv.getText() } );
       }
     }

     myWriter.close();

    }
    catch ( Exception e ) { e.printStackTrace(); }
  }
}
