package com.mktneutral;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;

public class ScrapeOptioneticsOnDemand {
    private static Connection myConn;
    private static Statement myStmt;

   public static void main( String[] args ) {
       
       ArrayList<String> tickerList = new ArrayList<String>();
       myConn = null;
       try {
	  myConn = DriverManager.getConnection( "jdbc:mysql://localhost/option_income?user=root" );
          myStmt = myConn.createStatement();
          ResultSet rs = myStmt.executeQuery( "SELECT DISTINCT ticker_symbol FROM weekly_optionable_stocks ORDER BY ticker_symbol ASC" );
          while ( rs.next() ) {
	    tickerList.add( rs.getString(1).trim() );
          }
          rs.close();
       }
       catch ( SQLException mySQLE ) { mySQLE.printStackTrace(); }

       getTickers( tickerList.toArray(new String[tickerList.size()]) );

       try { 
          myConn.close();
       } catch ( SQLException mySQLE ) { mySQLE.printStackTrace(); }
   }

   public static void getTickers( String[] args ) {
       try {
	  myStmt.executeUpdate( "TRUNCATE TABLE closing_stock_prices_on_demand" );
          myStmt.executeUpdate( "TRUNCATE TABLE option_records_on_demand" );
       }
       catch ( SQLException mySQLE ) { mySQLE.printStackTrace(); }

       ExecutorService execService = Executors.newFixedThreadPool(5);
       List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

       for ( int i=0; i<args.length; i++ ) {
           System.out.println( "Processing ticker " + args[i].trim() );
           tasks.add(Executors.callable(  new ScrapeOptioneticsTicker(args[i]) ));
       }
       
       try {
         execService.invokeAll( tasks );
         execService.shutdown();
       } catch ( InterruptedException ie ) { ie.printStackTrace(); }

       try {
           CallableStatement cStmt = myConn.prepareCall("{call compute_filtered_adjusted_annual_yields_on_demand( ?, ? )}");
           cStmt.setDate( 1, new Date( (new java.util.Date()).getTime() ) );
           cStmt.setDouble( 2, 60.0 );
           cStmt.execute();         
       }
       catch ( SQLException mySQLE ) { mySQLE.printStackTrace(); }
   }
}