import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Manta {
  public static void main( String[] args ) {
    try {

      BufferedWriter myWriter = new BufferedWriter( new FileWriter( "./Manta.csv" ) );

     DefaultSelenium selena = new DefaultSelenium( "localhost",4444,"*firefox","http://www.iecaonline.com/cfm_PublicSearch/pg_PublicSearch.cfm?mode=execute");
     selena.start();

     String startUrl =  "http://www.google.com/gwt/x?u=http%3A%2F%2Fwww.manta.com%2Fmb_45_C432C02T_05%2Famerican_restaurant%2Fcalifornia&btnGo=Go&source=wax&ie=UTF-8&oe=UTF-8"; 

     selena.open( startUrl );
     
     for ( int i=1; i<10; i++ ) {
       try {
	   selena.click( "dom=function a() { var b=document.getElementsByTagName('a'); return b.item(" + Integer.toString(i+30) + "); } a();" );
         Thread.sleep( 500 );
         selena.click( "dom=function a() { var anchors = document.getElementsByTagName('a'); return anchors.item(0); } a();" );
        Thread.sleep( 500 );
	System.out.println( selena.getText( "dom=function b() { var c= document.getElementsByTagName('span'); for ( var i=0; i<c.length; i++ ) { if ( c.item(i).getAttribute('style') == 'color:#252525;' ) { if ( spanCount == 5 ) { return c.item(i); } spanCount++; } } return 0; } b();" ) );

	selena.open( startUrl );

	 Thread.sleep( 500 );
       } catch ( SeleniumException mySE ) { mySE.printStackTrace(); selena.goBack(); selena.goBack(); }
      }

      myWriter.close();

    }
    catch ( Exception e ) { e.printStackTrace(); }
  }
}
