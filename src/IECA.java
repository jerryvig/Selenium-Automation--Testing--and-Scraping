import com.thoughtworks.selenium.DefaultSelenium;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class IECA {
  public static void main( String[] args ) {
    try {

      BufferedWriter myWriter = new BufferedWriter( new FileWriter( "./IECA.csv" ) );

     DefaultSelenium selena = new DefaultSelenium( "localhost",4444,"*firefox","http://www.iecaonline.com/cfm_PublicSearch/pg_PublicSearch.cfm?mode=execute");
     selena.start();

      for ( int i=0; i<1000; i++ ) {
	  selena.open( "http://www.iecaonline.com/cfm_PublicSearch/wnd_MemberDataWindow.cfm?MemberID=" + Integer.toString(i) );
          String bodyTxt = selena.getBodyText();
          String[] lines  = bodyTxt.split("\n");
          try {
	   String bPhone = "";
           String fax = "";
           String web = "";
           String email = "";
           String addy = "";
           String name = lines[1].trim();
           int bPhoneLine = 0;
           for ( int j=0; j<lines.length; j++ ) {
	       if ( lines[j].trim().startsWith("Business Phone:") ) {
		  bPhone = lines[j].split(":")[1].trim();
                  bPhoneLine = j;
              }
              if ( lines[j].trim().startsWith("Fax:") ) {
		  fax = lines[j].split(":")[1].trim();
              }
              if ( lines[j].trim().startsWith("Email:") ) {
		  email = lines[j].split(":")[1].trim();
              }
              if ( lines[j].trim().startsWith("Web:") ) {
		  web = lines[j].split(":")[1].trim();
              }
           }
          
           for ( int j=2; j<bPhoneLine; j++ ) {
	       addy += lines[j] + "\n";
           }

           myWriter.write( "\"" + name + "\",\"" + addy + "\",\"" + bPhone + "\",\"" + fax + "\",\"" + email + "\",\"" + web + "\"\n" );
         
           // myWriter.write( lines[0] + "\n" );
          } catch ( ArrayIndexOutOfBoundsException e ) {}
          
	  }

      myWriter.close();

    }
    catch ( Exception e ) { e.printStackTrace(); }
  }
}
