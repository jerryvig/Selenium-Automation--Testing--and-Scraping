import org.openqa.selenium.firefox.FirefoxDriver;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

public class GeeksDotCom {
  public static void main( String[] args ) {
    try {

     CSVWriter myWriter = new CSVWriter( new FileWriter( "./GeeksDotCom.csv" ) );

     FirefoxDriver driver = new FirefoxDriver();

     driver.get( "http://www.geeks.com/products.asp?cat=SYS" ); 

     myWriter.writeNext( new String[]{ "Description", "Price" } );
     
     List<WebElement> tableList = driver.findElementsByTagName("table");

     for ( WebElement myTable : tableList ) {
      String description = "";
      String price = "";
      // System.out.println( myTable.getText() );
      try {
       if ( (myTable.getAttribute("bordercolor")).equals("#CCCCCC") ) {
	  if ( myTable.getAttribute("class").equals("txt11px") ) {

	     List<WebElement> aList = myTable.findElements( By.tagName("a") ); 
             for ( WebElement anchor : aList ) {
	       if ( anchor.getAttribute("href").contains("details.asp") ) {
		 if ( anchor.getAttribute("class").equals("productlink") ) {
		     description = anchor.getText().trim();
                 } 
               }  
             }

             List<WebElement> spanList = myTable.findElements( By.tagName("span") );
             for ( WebElement span : spanList ) {
	       if ( span.getAttribute("class").equals("checkoutprice") ) {
		   price = span.getText().trim();
               } 
             }
          }
       }
      } catch ( NullPointerException npe ) { npe.printStackTrace(); }
      if ( description.contains( "GB" ) ) {
	  // try {
	  myWriter.writeNext( new String[]{ description, price } );
	  // } catch ( IOException myIOE ) { myIOE.printStackTrace(); }
      }
     }    

     myWriter.close();

    }
    catch ( Exception e ) { e.printStackTrace(); }
  }
}
