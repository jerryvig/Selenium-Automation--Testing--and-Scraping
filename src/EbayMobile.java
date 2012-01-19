import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class EbayMobile {
  public static void main( String[] args ) {
    try {

      BufferedWriter myWriter = new BufferedWriter( new FileWriter( "./Manta.csv" ) );

     DefaultSelenium selena = new DefaultSelenium( "localhost",4444,"*firefox","https://m.ebay.com/Login.aspx");
     selena.start();

     selena.open( "https://m.ebay.com/Login.aspx" );
     selena.type( "//input[@name='uid']", "agentq314" );
     selena.type( "//input[@name='upw']", "dk87nup4841" );
     selena.click( "//input[@name='btnSubmit']" );

     myWriter.close();

    }
    catch ( Exception e ) { e.printStackTrace(); }
  }
}
