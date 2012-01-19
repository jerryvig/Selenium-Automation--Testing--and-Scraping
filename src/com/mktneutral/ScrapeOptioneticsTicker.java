package com.mktneutral;

import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ScrapeOptioneticsTicker implements Runnable {
    private static Connection myConn;
    private static Statement myStmt;
    private static SimpleDateFormat myDF;
    private static SimpleDateFormat myDFII;
    private String insertQuery;
    private String ticker;

    ScrapeOptioneticsTicker( String aTicker ) {
       myDF = new SimpleDateFormat( "MMM dd, yyyy" );
       myDFII = new SimpleDateFormat( "MM/dd/yyyy" );

       ticker = aTicker;
    }

    public void run() {
       myConn = null;
       try {
	  myConn = DriverManager.getConnection( "jdbc:mysql://localhost/option_income?user=root" );
          myStmt = myConn.createStatement();
       } catch ( SQLException sqle ) { sqle.printStackTrace(); }
 
       try {
	 System.out.println( "ticker = " + ticker );
         
         URL myUrl = new URL( "http://www.optionetics.com/marketdata/details.asp?symb=" + ticker + "&page=chain" );
         String responseContent = "";
         BufferedReader myReader = new BufferedReader( new InputStreamReader(myUrl.openStream()) );
         try {
             String line = "";
             while ( (line = myReader.readLine()) != null ) {
		 responseContent += line;
             }
             myReader.close();
	 }
	 catch ( IOException myIOE ) { myIOE.printStackTrace(); }        
         
         //do procedure to get these values.
         String lastPriceValue = ""; 
         Date lastPriceDate = null;
         ArrayList<Date> expiryDates = new ArrayList<Date>();

         int startIdx = responseContent.indexOf("<div id=\"quoteInfo\"");
         int endIdx = responseContent.indexOf( "<div style=\"padding: 3px 3px 3px 3px;\">" );

         int lastIndex = responseContent.indexOf( "Last:<b>" );              
	 lastPriceValue = responseContent.substring( lastIndex+9, lastIndex+15 );
         lastPriceValue = lastPriceValue.replaceAll("<","").replaceAll("/","");
         lastIndex = responseContent.indexOf( "9px;\">As of" );
         try {
	   lastPriceDate = new Date(  (myDFII.parse( responseContent.substring( lastIndex+13, lastIndex+64 ).trim() )).getTime() );
         } catch ( ParseException myPE ) { myPE.printStackTrace(); }
         
         String blockString = responseContent.substring( startIdx, endIdx );
         while ( (lastIndex = blockString.indexOf( "<p><b>Options Expiration:" )) != -1 ) {
             try {
	       expiryDates.add( new Date( (myDF.parse( blockString.substring( lastIndex+30, lastIndex+42 ) )).getTime() ) );
             } catch ( ParseException myPE ) { myPE.printStackTrace(); }
             blockString = blockString.substring( lastIndex+42 );
         }          

         try {			
	     myStmt.executeUpdate( "INSERT INTO closing_stock_prices_on_demand ( trade_date, ticker_symbol, close ) VALUES ( '" + lastPriceDate.toString() + "', '" + ticker + "', " + lastPriceValue + " )" );
         } catch ( SQLException mySQLE ) { mySQLE.printStackTrace(); }
		   
	 startIdx = responseContent.indexOf( "<table class='chaintable'" );
         String tables = responseContent.substring( startIdx, endIdx );
        
         Parser myParser = new Parser();
         try {
           myParser.setInputHTML( tables );
           NodeList nList = myParser.parse( null );
           
           int chainTableCount = 0;

           insertQuery = "INSERT INTO option_records_on_demand ( trade_date, ticker_symbol, option_type, expiration_date, strike_price, bid, ask, last, volume, open_interest ) VALUES ";

           for ( int i=0; i<nList.size(); i++ ) {
	     if ( nList.elementAt(i).getText().contains("class='chaintable'") ) {
		NodeList trList = nList.elementAt(i).getChildren();

                String expiryDate = expiryDates.get(chainTableCount).toString();                 
		// System.out.println( expiryDate );

                for ( int j=0; j<trList.size(); j++ ) {
		 if ( trList.elementAt(j).toString().contains("href='details.asp?") ) {
		   NodeList tdList = trList.elementAt(j).getChildren();
                   
                   if ( tdList.size() == 13 ) {
		     String callOpenInterest = tdList.elementAt(1).toPlainTextString().trim();
                     callOpenInterest = callOpenInterest.replaceAll(",", "");
                     String callVolume = tdList.elementAt(2).toPlainTextString().trim();
                     callVolume = callVolume.replaceAll(",", "");
                     String callLast = tdList.elementAt(3).toPlainTextString().trim();
                     String callBid =  tdList.elementAt(4).toPlainTextString().trim();
                     String callAsk =  tdList.elementAt(5).toPlainTextString().trim();
		     String strikePrice = (tdList.elementAt(6).toPlainTextString()).trim();
                     String putBid = (tdList.elementAt(7).toPlainTextString()).trim();
                     String putAsk = (tdList.elementAt(8).toPlainTextString()).trim();
                     String putLast = (tdList.elementAt(9).toPlainTextString()).trim();
                     String putVolume = (tdList.elementAt(10).toPlainTextString()).trim();
                     putVolume = putVolume.replaceAll(",", "");
                     String putOpenInterest = (tdList.elementAt(11).toPlainTextString()).trim();
                     putOpenInterest = putOpenInterest.replaceAll(",", "");
                                     
                     if ( callLast.trim().equals("") ) callLast = "0.0";
                     if ( putLast.trim().equals("") ) putLast = "0.0";
                  
		     insertQuery += "( '" + lastPriceDate.toString() + "', '" + ticker + "', 'CALL', '" + expiryDate.toString() + "', " + strikePrice + ", " + callBid + ", " + callAsk + ", " + callLast + ", " + callVolume + ", " + callOpenInterest + " ),( '" + lastPriceDate.toString() + "', '" + ticker + "', 'PUT', '" + expiryDate.toString() + "', " + strikePrice + ", " + putBid + ", " + putAsk + ", " + putLast + ", " + putVolume + ", " + putOpenInterest + " ),";
                    
		     //System.out.println( insertQuery );

                   }   
                 } 
                }
                chainTableCount++;
             }
           }
           myStmt.executeUpdate( insertQuery.substring(0,insertQuery.length()-1) );
	 }
         catch ( ParserException pe ) { pe.printStackTrace(); }
         catch ( SQLException sqle ) { sqle.printStackTrace(); }  
       }
       catch ( IOException myIOE ) { myIOE.printStackTrace(); }

    }
}