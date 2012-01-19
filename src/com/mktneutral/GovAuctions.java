package com.mktneutral;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.regex.Pattern;
import org.htmlparser.Parser;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class GovAuctions {
    private static String searchString;
    private static String responseData; 
    private static Connection hsql;
    private static Statement stmt;
    private static String searchState;
    private static HashMap<String,String> stateMap;

    public static void main( String[] args ) {
	String resp = runScrapers("gold",16,"ALL");
	System.out.println( resp );        
    }

    public static String runScrapers( String _searchString, int searchEngineInt, String _searchState ) { 
	try {
	   Class.forName("org.hsqldb.jdbcDriver");
	} catch ( ClassNotFoundException cnfe ) { cnfe.printStackTrace(); }
   
        String[] stateNameList = { "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "District of Columbia", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming", "American Samoa", "Guam", "Northern Mariana Islands", "Puerto Rico" };
        String[] stateAbbrevList = { "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "AS", "GU", "MP", "PR" };
          
        stateMap = new HashMap<String,String>();
        for ( int i=0; i<stateNameList.length; i++ ) {
	   stateMap.put( stateAbbrevList[i], stateNameList[i] );
        }

	hsql = null;
	try {
	   hsql = DriverManager.getConnection( "jdbc:hsqldb:file:govauctionsdb", "SA", "" );
	}
	catch ( SQLException sqle ) { sqle.printStackTrace(); }

	try {
	    stmt = hsql.createStatement();
	    stmt.executeUpdate( "DROP TABLE IF EXISTS auction_records" );
	    stmt.executeUpdate( "CREATE TABLE auction_records ( website VARCHAR(64), url VARCHAR(1024), results_content VARCHAR(1536) )" );
	} catch ( SQLException sqle ) { sqle.printStackTrace(); }

	searchState = _searchState;
	searchString = _searchString;

	responseData = "";

	if ( searchEngineInt == 0 ) {
	    GovLiquidation();  
	}
	if ( searchEngineInt == 1 ) {
	    GSAAuctions();
	}
	if ( searchEngineInt == 2 ) {
	    IRSAuctions(); 
	}
	if ( searchEngineInt == 3 ) {
	    BidCorp();
	}
	if ( searchEngineInt == 4 ) {
	    bid4Assets();
	}
       	if ( searchEngineInt == 5 ) {
	    BankruptcySales();
	}
       	if ( searchEngineInt == 6 ) {
	    PublicSurplus();
	}
       	if ( searchEngineInt == 7 ) {
	    GovernmentAuction();
	}
       	if ( searchEngineInt == 8 ) {
	    HomeSteps();
	}
       	if ( searchEngineInt == 9 ) {
	    HomePath();
	}
       	if ( searchEngineInt == 10 ) {
	    HomeSalesGov();
	}
       	if ( searchEngineInt == 17 ) {
	    HudHomeStore();
	}
       	if ( searchEngineInt == 11 ) {
	    GSAGov();
	}
       	if ( searchEngineInt == 12 ) {
	    GovSales();
	}
       	if ( searchEngineInt == 13 ) {
	    LonestarOnline();
	}
       	if ( searchEngineInt == 14 ) {
	    GovDeals();
	}
       	if ( searchEngineInt == 15 ) {
	    IllinoisIbid();
	}
       	if ( searchEngineInt == 16 ) {
	    PropertyRoom();
	}
          
	try {
	    hsql.close();
	} 
	catch ( SQLException sqle ) { sqle.printStackTrace(); }

	return responseData;
    }
   
    public static void GovLiquidation() {
       String location = "";
       String ntt = "";
       String ntk = "";

       if ( !searchState.equals("ALL") ) {
           ntt = "US_" + searchState + "|" + searchString;
           location = "&location=US_" + searchState;
           ntk = "P_Auction_Country_State|P_Lot_Title";
       }
       else { 
          ntt = searchString;
          location = "";
          ntk = "P_Lot_Title"; 
       }
    
       String message = "Ntt=" + ntt + "&Ntk=" + ntk + "&Ntx=mode+matchall|mode+matchall&N=0&Nty=1&Ns=P_Lot_Number|0" + location + "&words=" + searchString + "&cmd=keyword";

       try {
        URL theUrl = new URL( "http://www.govliquidation.com/auction/endecaSearch?" + message );
        String resp = "";        
        BufferedReader reader = new BufferedReader(new InputStreamReader(theUrl.openStream()));
        String line;
        while ((line = reader.readLine()) != null) {
	    resp += line;
        }
        reader.close();

        int startIdx = resp.indexOf("<TABLE CELLSPACING=\"0\" CELLPADDING=\"0\" border=\"0\" WIDTH=\"748\" ALIGN=\"CENTER\" id=\"lots_table\">");
        int endIdx = resp.indexOf("<!-- Load the Brightcove JavaScript API -->");

        Parser htmlParser = new Parser();
        if ( endIdx > 0 && startIdx > 0 ) {
          try {
	     htmlParser.setInputHTML( resp.substring( startIdx, endIdx ) );  
             NodeList nList = htmlParser.parse(null);
             if ( nList.size() > 0 ) {
	       NodeList trList = nList.elementAt(0).getChildren();
               for ( int i=0; i<trList.size(); i++ ) {
		   if ( trList.elementAt(i).getText().contains("resultsBackground1") || trList.elementAt(i).getText().contains("resultsBackground2") ) {
		     NodeList tdList = trList.elementAt(i).getChildren();
		     for ( int j=0; j<tdList.size(); j++ ) {
		       if ( (tdList.elementAt(j).getText()).trim().equals("TD VALIGN=\"top\" class=\"annotationText1\"") ) {
			  String resultsContent = (tdList.elementAt(j).toPlainTextString()).trim();
                          resultsContent = resultsContent.replaceAll("\"", "'" );
			  resultsContent = resultsContent.replaceAll("\n", "");
                          resultsContent = resultsContent.replaceAll("\t", "");

			  NodeList aList = tdList.elementAt(j).getChildren();
                          for ( int k=0; k<aList.size(); k++ ) {
			    if ( aList.elementAt(k).getText().contains("A HREF=\"http://www.govliquidation.com/auction/view") ) {
			       String anchorHref = aList.elementAt(k).getText().trim();
                               String[] pieces = anchorHref.split("\"");
                               anchorHref = pieces[1].trim();
                               
                               try {
                                 System.out.println( "INSERT INTO auction_records ( website, url, results_content ) VALUES ( 'http://www.govliquidation.com/', '" + anchorHref + "', '" +  resultsContent + "' )" );

			          stmt.executeUpdate( "INSERT INTO auction_records ( website, url, results_content ) VALUES ( 'http://www.govliquidation.com/', '" + anchorHref + "', '" +  resultsContent + "' )" );
                               } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                            }   
                          } 
                       }   
                     }
                   }
               }
             }
          } catch ( ParserException pe ) { pe.printStackTrace(); }
        }
       } catch ( MalformedURLException me ) { me.printStackTrace(); }
       catch ( IOException ioe ) { ioe.printStackTrace(); }
    }

    public static void GSAAuctions() {
      if ( searchState.equals("ALL") ) {
       try {
	String message = "Continue=" + URLEncoder.encode("Continue");       
        
        URL theUrl = new URL("http://gsaauctions.gov/gsaauctions/gsaauctions/");
        HttpURLConnection conn = (HttpURLConnection) theUrl.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        conn.setRequestProperty("User-Agent","Mozilla/5.0");
        OutputStream os = conn.getOutputStream();
        os.write(message.getBytes("UTF-8"));
        os.close();

        BufferedReader myReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        String resp = "";
        while ((line = myReader.readLine()) != null) {
	    resp += line;
        }
        myReader.close();
       
        int scIdx = resp.indexOf("name=\"scParam\" VALUE=\"");
        if ( scIdx > 0 ) {
	  String[] pieces = resp.substring( scIdx+10, scIdx+80 ).split("VALUE=\"");
          if ( pieces.length > 1 ) {          
	    String[] piecesII = pieces[1].split("\"");
	    String scParam =  piecesII[0].trim();

            int webPcmIdx = resp.indexOf("name=\"WEBPCMTRANSID\" VALUE=\"");
            if ( webPcmIdx > 1 ) {
	       pieces = resp.substring( webPcmIdx+10, webPcmIdx+80 ).split("VALUE=\"");
               if ( pieces.length > 0 ) {
		 piecesII = pieces[1].split("\"");
                 String webPcmParam = piecesII[0].trim();
                  
                 String msg2 = "scParam=" + URLEncoder.encode(scParam) + "&WEBPCMTRANSID=" + URLEncoder.encode( webPcmParam ) + "&sc=&scAction=" + URLEncoder.encode("AUCALSRH") + "&scRegn=&scSalNo=&scLotNo=&scQuery=&query=&order=&af2eq=&scCatCode=&scStCode=&scPassUpd=&scSrchDesc=" + URLEncoder.encode( searchString ) + "&scSrchDescCtxt=&catMenuNew=";

                 URL url2 = new URL("http://gsaauctions.gov/gsaauctions/aucindx");
                 HttpURLConnection con2 = (HttpURLConnection) url2.openConnection();
                 con2.setDoOutput(true);
                 con2.setRequestMethod("POST");
                 con2.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                 con2.setRequestProperty("User-Agent","Mozilla/5.0");

                 OutputStream writer = con2.getOutputStream();
                 writer.write(msg2.getBytes("UTF-8"));
	         writer.close();
             
                 BufferedReader leer = new BufferedReader(new InputStreamReader(con2.getInputStream()));
                 line = "";
                 resp = "";
                 while ((line = leer.readLine()) != null) {
	          resp += line;
                 }
                 leer.close();

                 int startIdx = resp.indexOf("var tab=[");
                 int endIdx = resp.indexOf("bldTr();"); 

                 pieces =  resp.substring( startIdx, endIdx ).split(Pattern.quote("["));
                 if ( pieces.length > 1 ) {
                   piecesII = pieces[1].split(Pattern.quote("]"));
		   pieces = piecesII[0].split("\",\"");

                   String resultsContent = "";
                   String anchorHref = "";
                   if ( pieces.length > 6 ) {
                    for ( int j=0; j<pieces.length; j+=7 ) {
		     resultsContent = pieces[j].replace("\"","") + " " + pieces[j+1] + " " + pieces[j+2] + " " + pieces[j+3] + " " + pieces[j+4] + " " + pieces[j+5] + " " + pieces[j+6].replace("\",","");
                     anchorHref = "javascript:itemSel('" + pieces[j+1].substring(0,11) + "','" + pieces[j+1].substring(11) + "');";
                     try {
                       stmt.executeUpdate( "INSERT INTO auction_records ( website, url, results_content ) VALUES ( 'http://gsaauctions.gov/', '" + anchorHref + "', '" + resultsContent + "' )" );
                     } catch ( SQLException sqle ) { sqle.printStackTrace(); }
		    }
                   }
		 }
	       }
	    }
          }
	}
         
       } catch ( MalformedURLException me ) { me.printStackTrace(); }
       catch ( IOException ioe ) { ioe.printStackTrace(); }
      }
    }

    public static void IRSAuctions() {
      if ( searchState.equals("ALL") ) {
        try {         
	 URL theUrl = new URL("http://search.treas.gov/search?q=" + URLEncoder.encode(searchString) + "&output=xml_no_dtd&sort=date%3AD%3AL%3Ad1&ie=UTF-8&client=auctions&oe=UTF-8&proxystylesheet=auctions&site=Auctions");
         String resp = "";        
         BufferedReader reader = new BufferedReader(new InputStreamReader(theUrl.openStream()));
         String line;
         while ((line = reader.readLine()) != null) {
	    resp += line;
         }
         reader.close();

         int startIdx =  resp.indexOf("<p><font size=\"-2\">");
         int endIdx = resp.indexOf("In order to show you");

         if ( startIdx > 0 && endIdx > startIdx ) {
           Parser htmlParser = new Parser();
           try {
             htmlParser.setInputHTML( resp.substring(startIdx,endIdx) );
             NodeList nList = htmlParser.parse(null);
             
             for ( int i=0; i<nList.size(); i++ ) {
	      if ( nList.elementAt(i).getText().startsWith("p") ) {
		NodeList pChildList = nList.elementAt(i).getChildren();
                for ( int j=0; j<pChildList.size(); j++ ) {
		  String anchorHref = "";
		  if ( pChildList.elementAt(j).getText().startsWith("a href=\"http://www.treasury") ) {
		    String[] pieces = pChildList.elementAt(j).getText().split("\"");
                    anchorHref = pieces[1].trim();      
                  }
                  if ( pChildList.elementAt(j).getText().startsWith("span class=\"s\"") ) {
		    String resultsContent = pChildList.elementAt(j).toPlainTextString().trim();
                    resultsContent = resultsContent.replaceAll("\"", "");
		    resultsContent = resultsContent.replaceAll("\n", "");
                    try {
                      stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://search.treas.gov/', '" + anchorHref + "', '" + resultsContent + "' )" );
                    } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                  } 
                }   
              }   
             }            
           } catch ( ParserException pe ) { pe.printStackTrace(); }                             
         }
	} catch ( IOException ioe ) { ioe.printStackTrace(); }
         
      }
    }

    public static void BidCorp() {
      if ( searchState.equals("ALL" ) ) {
        try {
	  URL theUrl = new URL("http://www.bidcorp.com/AdvancedSearch.aspx"); 
          String resp = "";        
          BufferedReader reader = new BufferedReader(new InputStreamReader(theUrl.openStream()));
          String line;
          while ((line = reader.readLine()) != null) {
	    resp += line;
          }
          reader.close();
     
          int vsIdx = resp.indexOf("id=\"__VIEWSTATE\"");
          int endIdx = resp.indexOf("=\" />");
          String viewState = resp.substring(vsIdx,endIdx+1).split("value=\"")[1];
          vsIdx = resp.indexOf("id=\"__EVENTVALIDATION\"");
          endIdx = resp.indexOf("<table class=\"mainTable\""); 
	  String[] pieces = resp.substring(vsIdx,endIdx).split("value=\"");
          String evtValidation = pieces[1].split("\"")[0].trim();
          
          String message = "__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=" + URLEncoder.encode(viewState) + "&__EVENTVALIDATION=" + URLEncoder.encode(evtValidation) +  "&ctl00$ContentPlaceHolder1$txtKeywords=" + URLEncoder.encode( searchString ) + "&ctl00$ContentPlaceHolder1$rdoKeywordStatus=" + URLEncoder.encode("3") + "&ctl00$ContentPlaceHolder1$btnSubmitKeyword=" + URLEncoder.encode("Submit") + "&ctl00$ContentPlaceHolder1$txtLotNumber=";

          URL url2 = new URL("http://www.bidcorp.com/AdvancedSearch.aspx");
          HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
          conn.setDoOutput(true);
          conn.setRequestMethod("POST");
          conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
          conn.setRequestProperty("User-Agent","Mozilla/5.0");

          OutputStream writer = conn.getOutputStream();
          writer.write(message.getBytes("UTF-8"));
	  writer.close();
             
          BufferedReader myReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          line = "";
          resp = "";
          while ((line = myReader.readLine()) != null) {
	     resp += line;
          }
          myReader.close();

          int startIdx = resp.indexOf("<table cellspacing=\"0\" cellpadding=\"4\"");
          endIdx = resp.indexOf("<div style=\"margin: 5px; ");

          if ( startIdx > 0 && endIdx > startIdx ) { 
	    Parser htmlParser = new Parser();
            try {
              htmlParser.setInputHTML( resp.substring(startIdx,endIdx) );
              NodeList nList = htmlParser.parse(null);
              if ( nList.size() > 0 ) {
		NodeList trList = nList.elementAt(0).getChildren();
                for ( int i=0; i<trList.size(); i++ ) {
		  if ( trList.elementAt(i).getText().contains("bgcolor=\"White\"") || trList.elementAt(i).getText().contains("bgcolor=\"#CCCCCC\"") ) {
		     NodeList tdList = trList.elementAt(i).getChildren();
                     if ( tdList.size() > 2 ) {
		        String resultsContent = (tdList.elementAt(2).toPlainTextString()).trim();
                        resultsContent = resultsContent.replaceAll("\"", "");
		        resultsContent = resultsContent.replaceAll("\n", "");
                        resultsContent = resultsContent.replaceAll("\t", ""); 
           
                        String[] piecesII = (tdList.elementAt(2).getChildren().elementAt(1).getText()).split("'");
                        String anchorHref = "http://www.bidcorp.com/" + piecesII[1].trim();
                        try {
			   
                          stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://www.bidcorp.com/', '" + anchorHref + "', '" + resultsContent + "' )" );

                        } catch ( SQLException sqle ) { sqle.printStackTrace(); }

                     }
                  }             
                }
              }
	    } catch ( ParserException pe ) { pe.printStackTrace(); }
          }
        } catch ( IOException ioe ) { ioe.printStackTrace(); }   
      }
    }

    public static void bid4Assets() {
	try { 
            String stateSearch = "";
            if ( searchState.equals("ALL") ) {
		stateSearch = "all"; }
            else { stateSearch = searchState; }
             
	    String message = "fuseaction=" + URLEncoder.encode("search") + "&type=" + URLEncoder.encode( "powerSearch" ) + "&criteria=" + URLEncoder.encode( searchString ) + "&keywordType=" + URLEncoder.encode( "allWords" ) + "&channel=" + URLEncoder.encode("all") + "&cat2=" + URLEncoder.encode("all") + "&cat3=" + URLEncoder.encode("all") + "&LocationChoice=" + URLEncoder.encode("1") + "&locatedState=" + URLEncoder.encode( stateSearch ) + "&ZIp=&ZipRadius=" + URLEncoder.encode("1") + "&assetstatus=" + URLEncoder.encode("Live") + "&DateHistory=" + URLEncoder.encode("6") + "&sort=" + URLEncoder.encode("bidCloseTime");

	    URL theUrl = new URL( "http://www.bid4assets.com/search/index.cfm" );
	    HttpURLConnection connection = (HttpURLConnection) theUrl.openConnection();
	    connection.setDoOutput( true );
	    connection.setRequestMethod( "POST" );
	    connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );

	    OutputStream writer = connection.getOutputStream();
	    writer.write( message.getBytes("UTF-8") );
	    writer.close();

	    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    String line;
	    String responseContent = "";

	    while ((line = reader.readLine()) != null) {
		responseContent += line;       
	    }
	    reader.close();

	    int startIdx = responseContent.indexOf( "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\">" );
	    int endIdx = responseContent.indexOf( "<!-- google_ad_section_end" ); 

            if ( startIdx<0 || endIdx<0 ) return;

	    Parser htmlParser = new Parser();
	    try {
		//  System.out.println( startIdx + " " + endIdx );
	      if ( startIdx > 0 && endIdx > 0 ) { 
               
		htmlParser.setInputHTML( responseContent.substring( startIdx, endIdx ) );
		NodeList nList = htmlParser.parse( null );               
	
		if ( nList.size() > 4 ) {
		   
		   NodeList trList = nList.elementAt(4).getChildren();

		   for ( int i=0; i<trList.size(); i++ ) {
		     if ( !trList.elementAt(i).toPlainTextString().trim().equals("") ) {
		       String resultsContent = trList.elementAt(i).toPlainTextString().trim();
                       resultsContent = resultsContent.replaceAll("\"", "");
		       resultsContent = resultsContent.replaceAll("\n", " ");
                       resultsContent = resultsContent.replaceAll("\t", " ");
                       resultsContent = resultsContent.replace("\\", "");
                       resultsContent = resultsContent.replaceAll("'", "");
                      
                       NodeList tdList = trList.elementAt(i).getChildren();
                      try {
                       for ( int j=0; j<tdList.size(); j++ ) {
			 if ( tdList.elementAt(j).getText().contains("mediumArial02") ) {
			     NodeList aList = tdList.elementAt(j).getChildren();
                             for ( int k=0; k<aList.size(); k++ ) {
			       if ( aList.elementAt(k).getText().contains("href") ) {
				 String href = aList.elementAt(k).getText().trim();
                                
                                  String anchorHref = href.split("=\"")[1];
                                  anchorHref = "http://www.bid4assets.com/" + anchorHref.substring(0,anchorHref.length()-1);                  
				  //System.out.println( "6" );
                                  try {
                                    stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://www.bid4assets.com/', '" + anchorHref + "', '" + resultsContent + "' )" );
                                  } catch ( SQLException sqle ) { sqle.printStackTrace(); }
				
                               }   
                             }
                         }
		       }
                      } catch ( NullPointerException npe ) { npe.printStackTrace(); }
		     }
		   }
		}
	      }
	    } catch ( ParserException pe ) { pe.printStackTrace(); }      
	} catch ( IOException ioe ) { ioe.printStackTrace(); }
    }
   
    public static void BankruptcySales() {
      if ( searchState.equals("ALL" ) ) {
       try {
	 String msg = "keyWord=" + URLEncoder.encode( searchString );

         URL theUrl = new URL("http://www.bankruptcysales.com/assets_search2.cfm"); 
         HttpURLConnection conn = (HttpURLConnection) theUrl.openConnection();
         conn.setDoOutput(true);        
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");           
         conn.setRequestProperty("User-Agent","Mozilla/5.0"); 
         OutputStream os = conn.getOutputStream();
         os.write(msg.getBytes("UTF-8"));
         os.close();

         BufferedReader lector = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         String line;
         String resp = "";
         while ((line = lector.readLine())!= null) {
	   resp = resp.concat(line);
         }
         lector.close();

         int startIdx = resp.indexOf("<!-- InstanceBeginEditable name=\"Body\" -->");
         int endIdx = resp.indexOf("CLASS=\"bottomLinks\"");

         Parser htmlParser = new Parser();
         try {
	   htmlParser.setInputHTML( resp.substring(startIdx,endIdx) );
           NodeList nList = htmlParser.parse(null);
           
           for ( int i=0; i<nList.size(); i++ ) {
	     if ( nList.elementAt(i).getText().startsWith("TABLE WIDTH=\"95%\"") ) {
	       NodeList nextChildren = nList.elementAt(i).getChildren();
               for ( int j=0; j<nextChildren.size(); j++ ) {
		 if ( nextChildren.elementAt(j).getText().equals("TR") ) {
		   NodeList nxtChildren = nextChildren.elementAt(j).getChildren();
                   if ( nxtChildren.size() > 0 ) {
		     NodeList pChildren = nxtChildren.elementAt(1).getChildren();
                     for ( int k=0; k<pChildren.size(); k++ ) {
		       if ( pChildren.elementAt(k).getText().startsWith("TABLE WIDTH=\"95%\"") ) {
			  NodeList trList = pChildren.elementAt(k).getChildren();
                          for ( int l=2; l<trList.size(); l++ ) {
			     if ( !trList.elementAt(l).toPlainTextString().trim().equals("") ) {
			       String resultsContent = trList.elementAt(l).toPlainTextString().trim();
                               resultsContent = resultsContent.replaceAll("\t","");
                               resultsContent = resultsContent.replaceAll("\n","");                                  
                               while ( resultsContent.contains("  ") ) {
				  resultsContent = resultsContent.replaceAll("  ","");   
                               }
                             
                               String[] hrefCandidates = trList.elementAt(l).toHtml().split("HREF=\"");
                               if ( hrefCandidates.length > 0 ) {
				  String anchorHref = hrefCandidates[1].split("\"")[0];
                                  anchorHref = "http://www.bankruptcysales.com/" + anchorHref;
                                  try {
				      //System.out.println(  "INSERT INTO auction_records VALUES ( 'http://www.bankruptcysales.com/', '" + anchorHref + "', '" + resultsContent + "' )" );
                                     stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://www.bankruptcysales.com/', '" + anchorHref + "', '" + resultsContent + "' )" );
                                  } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                               }
                               
                             }    
                          }
                       } 
                     }   
                   }
                 }     
               } 
             }  
           }

         } catch ( ParserException pe ) { pe.printStackTrace(); }
	} catch ( IOException ioe ) { ioe.printStackTrace(); }
      }
    }

    public static void PublicSurplus() {
      try {
	String stateSearch = searchState;  
	if ( searchState.equals("ALL") ) stateSearch = "";

	String message = "http://www.publicsurplus.com/sms/browse/search?posting=y&slth=&page=0&sortBy=timeLeft&keyWord=" + searchString + "&catId=-1&endHours=-1&startHours=-1&lowerPrice=0&higherPrice=0&milesLocation=-1&zipCode=&region=" + "all%2C" + stateSearch;

	URL theUrl = new URL( message );
        String resp = "";        
        BufferedReader reader = new BufferedReader(new InputStreamReader(theUrl.openStream()));
        String line;
        while ((line = reader.readLine()) != null) {
	   resp += line;
        }
        reader.close(); 
 
        int startIdx = resp.indexOf("<div class=\"SepTable\">");
        int endIdx = resp.indexOf("Hide Images");

        if ( startIdx > 0 && endIdx > 0 ) {
        //System.out.println( resp.substring(startIdx,endIdx) );

         Parser htmlParser = new Parser();
         try {
	  htmlParser.setInputHTML(resp.substring(startIdx,endIdx));
          NodeList nList = htmlParser.parse(null);   

          for ( int i=0; i<nList.size(); i++ ) {
	    if ( nList.elementAt(i).getText().equals("table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" class=\"tabCurr\"") ) {
	       NodeList tHeadList = nList.elementAt(i).getChildren();
               for ( int j=4; j<tHeadList.size(); j++ ) {
		 if ( tHeadList.elementAt(j).getText().startsWith("tr") ) {
		     String resultsContent = tHeadList.elementAt(j).toPlainTextString().trim();
		     resultsContent = resultsContent.replaceAll("&nbsp;","");
                     resultsContent = resultsContent.replaceAll("\n","");
                     resultsContent = resultsContent.replaceAll("\t","");
                     while ( resultsContent.contains("  ") ) {
			 resultsContent = resultsContent.replaceAll("  "," ");
                     }
                     //System.out.println( resultsContent );
		     String[] pieces = tHeadList.elementAt(j).toHtml().split("href=\"");
                     String[] piecesII = pieces[1].split("\"");
                     String anchorHref = "http://www.publicsurplus.com" + piecesII[0].trim();
                     try {
		        stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://http://www.publicsurplus.com/', '" + anchorHref + "', '" + resultsContent + "' )" );
                     } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                 }
               } 
            }  
          }

         } catch ( ParserException pe ) { pe.printStackTrace(); }
	}
      } catch ( IOException ioe ) { ioe.printStackTrace(); }    
    }

    public static void GovernmentAuction() {
      if ( searchState.equals("ALL" ) ) {
	String message = "http://auctions.governmentauction.com/view-auctions/individual-lots/?key=" + searchString + "&page=1&cat=&catm=any&order=timeleft&live=yes&timed=yes&regular=yes&buynow=yes&makeoffer=yes&auctioneer=&minprice=&maxprice=&items=48";
        try {
	 URL theUrl = new URL( message );
         String resp = "";        
         BufferedReader reader = new BufferedReader(new InputStreamReader(theUrl.openStream()));
         String line;
         while ((line = reader.readLine()) != null) {
	   resp += line;
         }
         reader.close(); 

         int startIdx = resp.indexOf("<div id=\"lac");
         int endIdx = resp.indexOf("<div id=\"footer\">");
         
         if ( startIdx > 0 && endIdx > startIdx ) {
         Parser prse = new Parser();
         try {
	   prse.setInputHTML( resp.substring(startIdx,endIdx) );
           NodeList nList = prse.parse(null);   

           for ( int i=0; i<nList.size(); i++ ) {
	     if ( nList.elementAt(i).getText().contains("id=\"lac") ) {
		NodeList divList = nList.elementAt(i).getChildren();
                for ( int j=0; j<divList.size(); j++ ) {
		  if ( divList.elementAt(j).getText().equals("div class=\"name\"") ) {
		    String resultsContent = divList.elementAt(j).toPlainTextString();
                    resultsContent = resultsContent.replaceAll("\n","");
                    resultsContent = resultsContent.replaceAll("\t","");
                    while ( resultsContent.contains("  ") ) {
		       resultsContent = resultsContent.replaceAll("  "," ");
                    }
                    //System.out.println( resultsContent );
                    String[] pieces = divList.elementAt(j).toHtml().split("href=\"");
                    String[] piecesII = pieces[1].split("\"");
                    String anchorHref = piecesII[0].trim();
                    anchorHref = "http://auctions.governmentauction.com" + anchorHref;
                    try {
		        stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://auctions.governmentauction.com', '" + anchorHref + "', '" + resultsContent + "' )" );
                     } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                  }  
                }
             }
           }
         } catch ( ParserException pe ) { pe.printStackTrace(); }
         } 
        } catch ( IOException ioe ) { ioe.printStackTrace(); }
      }
    }

    public static void HomeSteps() {
      if ( searchState.equals("ALL" ) ) {
	  return;
      }
      String msg = "City=&County=&State=" + URLEncoder.encode(searchState) + "&Zip=&PriceRangeLow=" + URLEncoder.encode("*") + "&PriceRangeHigh=" + URLEncoder.encode("*") + "&TotalRooms=" + URLEncoder.encode("*") + "&NumberBedrooms=" + URLEncoder.encode("*") + "&NumberBathrooms=" + URLEncoder.encode("*") + "&B1=Find Homes&IPP=50";

      try {
        URL theUrl = new URL("http://www.homesteps.com/cgi-bin/dynamic/formsearch.cgi"); 
        HttpURLConnection conn = (HttpURLConnection) theUrl.openConnection();
        conn.setDoOutput(true);        
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");           
        conn.setRequestProperty("User-Agent","Mozilla/5.0"); 
        OutputStream os = conn.getOutputStream();
        os.write(msg.getBytes("UTF-8"));
        os.close();

        BufferedReader myReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        String resp = "";
        while ((line = myReader.readLine())!= null) {
          resp = resp.concat(line);
        }
        myReader.close();
     
        int startIdx = resp.indexOf("<table cellpadding=1 cellspacing=1 class=tblHomesteps>");
        int endIdx = resp.indexOf("Freddie Mac takes");
 
        Parser prsr = new Parser();
        try {
	  prsr.setInputHTML(resp.substring(startIdx,endIdx));
          NodeList nList = prsr.parse(null);
          for ( int i=0; i<nList.size(); i++ ) {            
	    if ( nList.elementAt(i).getText().equals("table cellpadding=1 cellspacing=1 class=tblHomesteps") ) {
	       NodeList trList = nList.elementAt(i).getChildren();
               for ( int j=2; j<trList.size(); j++ ) {
		 if ( trList.elementAt(j).getText().equals("tr") ) {
		    String resultsContent = trList.elementAt(j).toPlainTextString();
                    resultsContent = resultsContent.replaceAll("\n","");
                    resultsContent = resultsContent.replaceAll("\t","");
                    while ( resultsContent.contains("  ") ) {
		       resultsContent = resultsContent.replaceAll("  "," ");
                    }
                    String anchorHref = "http://www.homesteps.com/featuresearch.html";
                    try {
		        stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://www.homesteps.com/featuresearch.html', '" + anchorHref + "', '" + resultsContent + "' )" );
                     } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                 } 
               }
            }
          }
        } catch ( ParserException pe ) { pe.printStackTrace(); }
      } catch ( IOException ioe ) { ioe.printStackTrace(); }
    }

    public static void HomePath() {
      if ( searchState.equals("ALL" ) ) {
	  return;
      }

      String message = "http://www.homepath.com/search.html?st=" + searchState + "&cno=000&ci=&zip=&src_ref=&mlsid=&pi=&pa=&bdi=&bhi=&x=26&y=11&ms=&xs=";
      try {
	 URL ubl = new URL( message );
         String resp = "";        
         BufferedReader reader = new BufferedReader(new InputStreamReader(ubl.openStream()));
         String line;
         while ((line = reader.readLine()) != null) {
	   resp += line;
         }
         reader.close(); 

         int startIdx = resp.indexOf("<table class=\"multipleLinesSearchResults\"");
         int endIdx = resp.indexOf("Get FREE HomePath");
 
         if ( startIdx > 0 && endIdx > 0 ) {
	   Parser htmlParser = new Parser();
           try {
	     htmlParser.setInputHTML(resp.substring(startIdx,endIdx));
             NodeList nList = htmlParser.parse(null);

             for ( int i=0; i<nList.size(); i++ ) {
	       if ( nList.elementAt(i).getText().equals("table class=\"multipleLinesSearchResults\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"") ) {
		  NodeList trList = nList.elementAt(i).getChildren();
                  for ( int j=0; j<trList.size(); j++ ) {
		    if ( trList.elementAt(j).getText().startsWith("tr class") ) {
		      String resultsContent = trList.elementAt(j).toPlainTextString().trim();
                      resultsContent = resultsContent.replaceAll("\n","");
                      resultsContent = resultsContent.replaceAll("\t","");
                      resultsContent = resultsContent.replaceAll("&nbsp;","");
                      resultsContent = resultsContent.replaceAll("SaveMap","");
                      while ( resultsContent.contains("  ") ) {
		        resultsContent = resultsContent.replaceAll("  "," ");
                      }
                      //System.out.println( resultsContent );
                      String[] pieces = trList.elementAt(j).toHtml().split("href=\"");
                      String[] piecesII = pieces[1].split("\"");
                      String anchorHref = piecesII[0].trim();
                      anchorHref = anchorHref;
                      anchorHref = "http://www.homepath.com" + anchorHref;
                      if ( !resultsContent.equals("") ) {
                        try {
		          stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://www.homepath.com/', '" + anchorHref + "', '" + resultsContent + "' )" );
                        } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                      }
                    }  
                  } 
               }  
             }
           } catch ( ParserException pe ) { pe.printStackTrace(); }
	 }
      } catch ( IOException ioe ) { ioe.printStackTrace(); }
    }

    public static void HomeSalesGov() {
      if ( searchState.equals("ALL" ) ) {
	  return;
      }  
      String msg = "selectedCities=" + URLEncoder.encode("ALL") + "&selBedrooms=&selBaths=&pageAction=" + URLEncoder.encode("Search for Properties") + "&stateName=" + URLEncoder.encode( stateMap.get(searchState) ) + "&state=" + URLEncoder.encode(searchState) + "&propertyType=" + URLEncoder.encode("RESIDENTIAL");
      try {     
        URL theUrl = new URL("http://www.homesales.gov/homesales/mainAction.do"); 
        HttpURLConnection conn = (HttpURLConnection) theUrl.openConnection();
        conn.setDoOutput(true);        
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");           
        conn.setRequestProperty("User-Agent","Mozilla/5.0"); 
        OutputStream os = conn.getOutputStream();
        os.write(msg.getBytes("UTF-8"));
        os.close();

        BufferedReader myReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        String resp = "";
        while ((line = myReader.readLine())!= null) {
          resp = resp.concat(line);
        }
        myReader.close();

        int startIdx = resp.indexOf("<table width=\"80%\" align=\"center\" border=\"0\" rules=\"none\">");
        int endIdx = resp.indexOf(" Total Result Pages:");
        
        if ( startIdx > 0 && endIdx > startIdx ) {
         try {
	  Parser prs = new Parser();
          prs.setInputHTML(resp.substring(startIdx,endIdx));

          NodeList nList = prs.parse(null);
          for ( int i=0; i<nList.size(); i++ ) {     
	    if ( nList.elementAt(i).getText().startsWith("table width=\"80%\"") ) {
	       NodeList trList = nList.elementAt(i).getChildren();
               for ( int j=0; j<trList.size(); j++ ) {
		 if ( trList.elementAt(j).getText().equals("tr") ) {
		    String resultsContent = trList.elementAt(j).toPlainTextString();
                    resultsContent = resultsContent.replaceAll("&nbsp;","");
                    resultsContent = resultsContent.replaceAll("\n","");
                    resultsContent = resultsContent.replaceAll("\t","");
                    while ( resultsContent.contains("  ") ) {
		       resultsContent = resultsContent.replaceAll("  "," ");
                    }
                   
                    String[] pieces = trList.elementAt(j).toHtml().split("href=\"");
                    if ( pieces.length > 1 ) {
                     String[] piecesII = pieces[1].split("\"");
                     if ( piecesII.length > 0 ) {
                      String anchorHref = piecesII[0].trim();
                      anchorHref = anchorHref;
                      try {
		        stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://www.homepath.com/', '" + anchorHref + "', '" + resultsContent + "' )" );
                      } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                     }
                    }
                 }  
               }
            }
          }
         } catch ( ParserException pe ) { pe.printStackTrace(); }
        }
      } catch ( IOException ioe ) { ioe.printStackTrace(); }
    }

    public static void HudHomeStore() {
      if ( searchState.equals("ALL" ) ) {
	  return;
      }

      String message = "https://hudhomestore.secureportalk.net/HUD/PropertySearchResult.aspx?PageId=1&zipCode=&city=&county=&sState="+searchState+"&fromPrice=0&toPrice=0&caseNumber=&bed=0&bath=0&street=&buyerType=0&specialProgram=&Status=0&OrderbyName=SCASENUMBER&OrderbyValue=ASC&sPageSize=50";
      try {
	 URL ubl = new URL( message );
         String resp = "";        
         BufferedReader reader = new BufferedReader(new InputStreamReader(ubl.openStream()));
         String line;
         while ((line = reader.readLine()) != null) {
	   resp += line;
         }
         reader.close(); 

         int startIdx = resp.indexOf("<tr class=\"FormTablerow\"");
         int endIdx = resp.indexOf("<!-- =============  Data List");

         try {
	   Parser p = new Parser();
           p.setInputHTML(resp.substring(startIdx,endIdx));           
           NodeList nList = p.parse(null);
           for ( int i=0; i<nList.size(); i++ ) {  
	     if ( nList.elementAt(i).getText().startsWith("tr class=\"FormTablerow") ) {
                 String resultsContent = nList.elementAt(i).toPlainTextString();
                 resultsContent = resultsContent.replaceAll("&nbsp;","");
                 resultsContent = resultsContent.replaceAll("\n","");
                 resultsContent = resultsContent.replaceAll("\t","");
                 resultsContent = resultsContent.replaceAll("Exclusive","");
                 resultsContent = resultsContent.replaceAll("View","");
                 resultsContent = resultsContent.replaceAll("Street","");
                 resultsContent = resultsContent.replaceAll("Map it","");
                 resultsContent = resultsContent.replaceAll("Email","");
                 resultsContent = resultsContent.replaceAll("Info","");
                 resultsContent = resultsContent.replaceAll("Extended","");
                 resultsContent = resultsContent.replaceAll("Lottery","");
                 while ( resultsContent.contains("  ") ) {
		    resultsContent = resultsContent.replaceAll("  "," ");
                 }
                 //System.out.println( resultsContent );
                 NodeList tdList = nList.elementAt(i).getChildren();
                 for ( int j=0; j<tdList.size(); j++ ) {
		   if ( tdList.elementAt(j).getText().startsWith("td align=\"center\" valign=\"middle\"") ) {
		     NodeList aList = tdList.elementAt(j).getChildren();
                     for ( int k=0; k<aList.size(); k++ ) {
		       if ( aList.elementAt(k).getText().startsWith("a href=\"#;\"") ) {
			   String[] pieces = aList.elementAt(k).getText().split(Pattern.quote("getGoogleTranslationstring(&#039;"));
                         if ( pieces.length > 0 ) {
			   String[] piecesII = pieces[1].split(Pattern.quote("&#039;"));
                           if ( piecesII.length > 0 ) {
			     String anchorHref = piecesII[0].trim();
                             try {
			       System.out.println( "INSERT INTO auction_records VALUES ( 'http://www.hudhomestore.com/', '" + anchorHref + "', '" + resultsContent + "' )" );
		                stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://www.hudhomestore.com/', '" + anchorHref + "', '" + resultsContent + "' )" );
                             } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                           }  
                         }  
                       }  
                     }  
                   }   
                 }
             }                 
           }
         } catch ( ParserException pe ) { pe.printStackTrace(); }
      } catch ( IOException ioe ) { ioe.printStackTrace(); }
    }

    public static void GSAGov() {
      if ( searchState.equals("ALL" ) ) {
	  return;
      }

      String message = "https://extportal.pbs.gsa.gov/ResourceCenter/PRHomePage/searchProperty.do?state=5&amp;statename=" + stateMap.get(searchState).toUpperCase() +  "&amp;propType=&amp;propTypeName=ALL&amp;searchType=S";
      try {
	 URL ubl = new URL( message );
         String resp = "";        
         BufferedReader reader = new BufferedReader(new InputStreamReader(ubl.openStream()));
         String line;
         while ((line = reader.readLine()) != null) {
	   resp += line;
         }
         reader.close(); 

         int startIdx = resp.indexOf("<div class=\"cmSectionText\">");
         int endIdx = resp.indexOf("<!-- div cmSectionText -->");
           
        if ( startIdx > 0 && endIdx > startIdx ) {
	  Parser p = new Parser();
	  try {   
	    p.setInputHTML( resp.substring(startIdx,endIdx) );
            NodeList nList = p.parse(null); 

            for ( int i=0; i<nList.size(); i++ ) {
	      if ( nList.elementAt(i).getText().startsWith("div class=\"cmSectionText\"") ) {
		NodeList divChildren = nList.elementAt(i).getChildren();
                for ( int j=0; j<divChildren.size(); j++ ) {
		  if ( divChildren.elementAt(j).getText().contains("D1D1D1") ) {
		    String resultsContent = divChildren.elementAt(j).toPlainTextString().trim();
                    resultsContent = resultsContent.replaceAll("Click here for more information","");
                    resultsContent = resultsContent.replaceAll("&nbsp;","");
                    resultsContent = resultsContent.replaceAll("\n","");
                    resultsContent = resultsContent.replaceAll("\t","");
                    resultsContent = resultsContent.replaceAll("\"","");
                    while ( resultsContent.contains("  ") ) {
		       resultsContent = resultsContent.replaceAll("  "," ");
                    }  
                    //System.out.println( resultsContent );  
                    String htm = divChildren.elementAt(j).toHtml();
                    //System.out.println( htm );  
                    int hrefIdx = htm.indexOf("loadProperty.do");
                    if ( hrefIdx > 0 ) {
		      String[] pieces = htm.substring(hrefIdx,hrefIdx+34).split(">");
                      if ( pieces.length > 0 ) {
			 String anchorHref = pieces[0].substring(0,pieces[0].length()-1);
                         anchorHref = "https://extportal.pbs.gsa.gov/ResourceCenter/PRHomePage/" + anchorHref;
                          try {
			      //System.out.println( "INSERT INTO auction_records VALUES ( 'https://propertydisposal.gsa.gov/', '" + anchorHref + "', '" + resultsContent + "' )" );
		                stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'https://propertydisposal.gsa.gov/', '" + anchorHref + "', '" + resultsContent + "' )" );
                          } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                      }
                    }
                  } 
                } 
              }  
            }  
	  } catch ( ParserException pe ) { pe.printStackTrace(); }
        }
      } catch ( IOException ioe ) { ioe.printStackTrace(); }
    }

    public static void GovSales() {
      String msg = "http://www.govsales.gov/fassys/fassys/?function=010000000000";
      
      try {
	URL ubl = new URL( msg );
        String resp = "";        
        BufferedReader reader = new BufferedReader(new InputStreamReader(ubl.openStream()));
        String line;
        while ((line = reader.readLine()) != null) {
	   resp += line;
        }
        reader.close(); 

        String scParam = "";
	int scParamIdx = resp.indexOf("name=scParam VALUE=\"");
        if ( scParamIdx > 0 ) {
	   scParam = resp.substring(scParamIdx, scParamIdx+80);
	   String[] pieces = scParam.split("VALUE=\"");
           if ( pieces.length > 1 ) {
             String[] piecesII = pieces[1].split("\"");
             if ( piecesII.length > 1 ) {
		scParam = piecesII[0].trim();
             } 
	   }
        }
        String webPcm = "";
        int webPcmIdx = resp.indexOf("WEBPCMTRANSID");
        if ( webPcmIdx > 0 ) {
	   webPcm = resp.substring(webPcmIdx, webPcmIdx+40);
           String[] pieces = webPcm.split("VALUE=");
           if ( pieces.length > 1 ) {
             String[] piecesII =  pieces[1].split("\"");
             if ( piecesII.length > 1 ) {
                webPcm = piecesII[1].trim();
             }  
           }
        }

        String msg2 = "scParam=" + URLEncoder.encode( scParam ) + "&scCurTabCat=" + URLEncoder.encode("010000000000") + "&scSelTabCat=" + URLEncoder.encode("010000000000") + "&scSelLink=" + URLEncoder.encode("GS") + "&scGSName=" + URLEncoder.encode( searchString.toUpperCase() ) + "&scGSOptn=" + URLEncoder.encode("1") + "&scSelState=&scSelBCCat=&scSelRow=&scSelCatList=&scSeeAllCat=&WEBPCMTRANSID=" + URLEncoder.encode( webPcm );

        URL usl = new URL("http://www.govsales.gov/fassys/fasallcat/");
        HttpURLConnection conn = (HttpURLConnection) usl.openConnection();
        conn.setDoOutput(true);        
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");           
        conn.setRequestProperty("User-Agent","Mozilla/5.0"); 
        OutputStream os = conn.getOutputStream();
        os.write(msg2.getBytes("UTF-8"));
        os.close();

        BufferedReader lecteur = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String rayo;
        String resp2 = "";
        while ((rayo = lecteur.readLine())!= null) {
	    resp2 += rayo;
        }
        lecteur.close();

        scParam = "";
	scParamIdx = resp2.indexOf("name=scParam VALUE=\"");
        if ( scParamIdx > 0 ) {
	   scParam = resp2.substring(scParamIdx, scParamIdx+80);
	   String[] pieces = scParam.split("VALUE=\"");
           if ( pieces.length > 1 ) {
             String[] piecesII = pieces[1].split("\"");
             if ( piecesII.length > 1 ) {
		scParam = piecesII[0].trim();
             } 
	   }
        }
        String scStoreLocal1 = "";
	int scSL1Idx = resp2.indexOf("name=scStoreLocal1");
        if ( scSL1Idx > 0 ) {
	   scStoreLocal1 = resp2.substring(scSL1Idx, scSL1Idx+160);
	   String[] pieces = scStoreLocal1.split("VALUE=\"");
           if ( pieces.length > 1 ) {
             String[] piecesII = pieces[1].split("\"");
             if ( piecesII.length > 1 ) {
		scStoreLocal1 = piecesII[0].trim();
             } 
	   }
        }
        String scStoreLocal2 = "";
	int scSL2Idx = resp2.indexOf("name=scStoreLocal2");
        if ( scSL1Idx > 0 ) {
	   scStoreLocal2 = resp2.substring(scSL2Idx, scSL2Idx+160);
	   String[] pieces = scStoreLocal2.split("VALUE=\"");
           if ( pieces.length > 1 ) {
             String[] piecesII = pieces[1].split("\"");
             if ( piecesII.length > 1 ) {
		scStoreLocal2 = piecesII[0];
             } 
	   }
        }
        String scStoreLocal3 = "";
	int scSL3Idx = resp2.indexOf("name=scStoreLocal3");
        if ( scSL3Idx > 0 ) {
	   scStoreLocal3 = resp2.substring(scSL3Idx, scSL3Idx+160);
	   String[] pieces = scStoreLocal3.split("VALUE=\"");
           if ( pieces.length > 1 ) {
             String[] piecesII = pieces[1].split("\"");
             if ( piecesII.length > 1 ) {
		scStoreLocal3 = piecesII[0];
             } 
	   }
        }
        String scStoreLocal5 = "";
	int scSL5Idx = resp2.indexOf("name=scStoreLocal5");
        if ( scSL5Idx > 0 ) {
	   scStoreLocal5 = resp2.substring(scSL5Idx, scSL5Idx+160);
	   String[] pieces = scStoreLocal5.split("VALUE=\"");
           if ( pieces.length > 1 ) {
             String[] piecesII = pieces[1].split("\"");
             if ( piecesII.length > 1 ) {
		scStoreLocal5 = piecesII[0];
             } 
	   }
        }
        webPcm = "";
        webPcmIdx = resp2.indexOf("WEBPCMTRANSID");
        if ( webPcmIdx > 0 ) {
	   webPcm = resp2.substring(webPcmIdx, webPcmIdx+40);
           String[] pieces = webPcm.split("VALUE=");
           if ( pieces.length > 1 ) {
             String[] piecesII =  pieces[1].split("\"");
             if ( piecesII.length > 1 ) {
                webPcm = piecesII[1].trim();
             }  
           }
        }

        String msg3 = "scParam=" + URLEncoder.encode( scParam ) + "&scStoreLocal1=" + URLEncoder.encode( scStoreLocal1 ) + "&scStoreLocal2=" + URLEncoder.encode( scStoreLocal2 ) + "&scStoreLocal3=" + URLEncoder.encode( scStoreLocal3 ) + "&scStoreLocal4=&scStoreLocal5=" + URLEncoder.encode( scStoreLocal5 ) + "&scStoreLocal6=&scStoreLocal7=&scStoreLocal8=&scStoreLocal9=&scStoreLocal10=&scStoreLocal11=&scStoreLocal12=&scCurTabCat=&scSelTabCat=&scSelLink=&scGSName=&scGSOptn=" + URLEncoder.encode("1") + "&scSelState=&scSelRow=" + URLEncoder.encode("2G1WB58K381263163") + "&scSortOrder=&scPageNo=" + URLEncoder.encode("1") + "&WEBPCMTRANSID=" + URLEncoder.encode("0690484,2970121");

        //System.out.println( msg3 );

        int startIdx = resp2.indexOf("var tab=");
        int endIdx = resp2.indexOf("bldTr();");
       
        String dataBlock = resp2.substring(startIdx,endIdx).replaceAll(Pattern.quote("["),"").replaceAll(Pattern.quote("];"),"");
        dataBlock = dataBlock.replaceAll("var tab=\"","");
        dataBlock = dataBlock.replaceAll("  "," "); 
	//System.out.println( dataBlock );
        String[] dataBlockPieces = dataBlock.split("\", \"");

        ArrayList<String> resultsContentStrings = new ArrayList<String>();
        ArrayList<String> urlIdList = new ArrayList<String>();

        for ( int i=0; i<dataBlockPieces.length; i+=12 ) {
	  String content = "";
	  for ( int j=0; j<12; j++ ) {
	    content += dataBlockPieces[i+j] + " ";
	  }
	  resultsContentStrings.add( content );
          urlIdList.add( dataBlockPieces[i+1] );
        }
 
        ArrayList<String> msgs = new ArrayList<String>();

        for ( String urlId : urlIdList ) {
	    msgs.add( "scParam=" + URLEncoder.encode( scParam ) + "&scStoreLocal1=" + URLEncoder.encode( scStoreLocal1 ) + "&scStoreLocal2=" + URLEncoder.encode( scStoreLocal2 ) + "&scStoreLocal3=" + URLEncoder.encode( scStoreLocal3 ) + "&scStoreLocal4=&scStoreLocal5=" + URLEncoder.encode( scStoreLocal5 ) + "&scStoreLocal6=&scStoreLocal7=&scStoreLocal8=&scStoreLocal9=&scStoreLocal10=&scStoreLocal11=&scStoreLocal12=&scCurTabCat=&scSelTabCat=&scSelLink=&scGSName=&scGSOptn=" + URLEncoder.encode("1") + "&scSelState=&scSelRow=" + URLEncoder.encode( urlId ) + "&scSortOrder=&scPageNo=" + URLEncoder.encode("1") + "&WEBPCMTRANSID=" + URLEncoder.encode("0690484,2970121") );
        }

        ArrayList<String> urlList = new ArrayList<String>();

        for ( String msg1 : msgs ) {
	  urlList.add( "javascript:jQuery.post( &quot;http://www.govsales.gov/fassys/fassrchlist/&quot;, &quot;" + msg1 + "&quot; )" ); 
        }

        for ( int i=0; i<resultsContentStrings.size()-1; i++ ) {
          try {	
	      stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://www.govsales.gov/', '" + urlList.get(i) + "', '" + resultsContentStrings.get(i) + "' )" ); 
          } catch ( SQLException sqle ) { sqle.printStackTrace(); }
        }

      } catch ( IOException ioe ) { ioe.printStackTrace(); }

    }

    public static void LonestarOnline() {   
       String msg = "search_type=title_search&search_name=Title+%26+Description+Search&search_text=" + searchString + "&phrase_match=any&category=-1&search_span=title&search_limit=active&order_by=title&sort_order=ASC";
 
       try {
         URL theUrl = new URL( "http://www.lonestaronline.com/search/search_results.cfm?" + msg );
         String resp = "";        
         BufferedReader reader = new BufferedReader(new InputStreamReader(theUrl.openStream()));
         String line;
         while ((line = reader.readLine()) != null) {
	    resp += line;
         }
         reader.close();

         int startIdx = resp.indexOf("<table border=0 cellspacing=0 cellpadding=2 noshade width=780>");
         int endIdx = resp.indexOf("<form name=\"blah\"");
         if ( startIdx > 0 && endIdx > startIdx ) {
	     //System.out.println( startIdx + " " + endIdx );

            Parser psr = new Parser();
            try {
		//System.out.println( resp.substring( startIdx, endIdx ) );
              psr.setInputHTML( resp.substring(startIdx,endIdx) );   
              NodeList nList = psr.parse(null);
              
              for ( int i=0; i<nList.size(); i++ ) {
		if ( nList.elementAt(i).getText().equals("table border=0 cellspacing=0 cellpadding=2 noshade width=780") ) {
		   NodeList trList =  nList.elementAt(i).getChildren();
                   for ( int j=0; j<trList.size(); j++ ) {
		     if ( trList.elementAt(j).getText().startsWith("tr") ) {
			String resultsContent = trList.elementAt(j).toPlainTextString();
                        resultsContent = resultsContent.replaceAll("&nbsp;","");
                        resultsContent = resultsContent.replaceAll("\n","");
                        resultsContent = resultsContent.replaceAll("\t","");
                        resultsContent = resultsContent.replaceAll("\"","");
                        resultsContent = resultsContent.replaceAll("'","");
                        while ( resultsContent.contains("  ") ) {
		          resultsContent = resultsContent.replaceAll("  "," ");
                        }
                        NodeList tdList = trList.elementAt(j).getChildren();
                        for ( int k=0; k<tdList.size(); k++ ) {
			   if ( tdList.elementAt(k).getText().equals("td width=\"496\"") ) {
			       String tdContent = tdList.elementAt(k).toHtml();
                               String[] pieces = tdContent.split("href=\"");
                               if ( pieces.length > 1 ) {
				  String[] piecesII = pieces[1].split("\"");
                                  String anchorHref = "http://www.lonestaronline.com" + piecesII[0].trim();
				  try {	
	                            stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://www.lonestaronline.com', '" + anchorHref + "', '" + resultsContent + "' )" ); 
                                  } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                               }   
                           }   
                        }
                     }  
                   }  
                }  
              }
            } catch ( ParserException pe ) { pe.printStackTrace(); }
         }
       } catch ( IOException ioe ) { ioe.printStackTrace(); }
    }

    public static void GovDeals() {
      String msg = "";
      if ( searchState.equals("ALL") ) {    
         msg = "fa=Main.AdvSearchResults&timing1=&mystate=&myseller=0&myselectbox=00&desc=" + searchString + "&inv_nbr=";
      }
      else {
	  msg = "fa=Main.AdvSearchResults&timing1=&mystate=" + searchState + "&myseller=0&myselectbox=00&desc=" + searchString + "&inv_nbr=";
      }  

       try {
         URL ubl = new URL( "http://www.govdeals.com/index.cfm?" + msg );
         String resp = "";        
         BufferedReader reader = new BufferedReader(new InputStreamReader(ubl.openStream()));
         String line;
         while ((line = reader.readLine()) != null) {
	    resp += line;
         }
         reader.close();

         int startIdx = resp.indexOf("<tr bgcolor=\"#CCCCCC\">");
         int endIdx = resp.indexOf("<td colspan=\"9\" style=\"border-bottom:none\" scope=\"row\">");

         if ( startIdx > 0 && endIdx > startIdx ) {
	     // System.out.println( startIdx + " " + endIdx );
           Parser prse = new Parser();
           try {
	      prse.setInputHTML( resp.substring( startIdx, endIdx ) );
              NodeList nList = prse.parse(null);
              for ( int i=0; i<nList.size(); i++ ) { 
		 if ( nList.elementAt(i).getText().startsWith("tr valign=\"top\"") ) {
		   String resultsContent = nList.elementAt(i).toPlainTextString();
                   resultsContent = resultsContent.replaceAll("&nbsp;","");
                   resultsContent = resultsContent.replaceAll("\n","");
                   resultsContent = resultsContent.replaceAll("\t","");
                   resultsContent = resultsContent.replaceAll("\"","");
                   resultsContent = resultsContent.replaceAll("'","");
                   resultsContent = resultsContent.replaceAll("View by same:CategoryLocationMake/BrandModelProximity-------------Terms & ConditionsView this Item","");
                   while ( resultsContent.contains("  ") ) {
		      resultsContent = resultsContent.replaceAll("  "," ");
                   }
                   resultsContent = resultsContent.replaceAll("0Bids","0 Bids");
                   //System.out.println( resultsContent );
                   NodeList tdList = nList.elementAt(i).getChildren();
                   for ( int j=0; j<tdList.size(); j++ ) {
		     if ( tdList.elementAt(j).getText().equals("td valign=\"top\" nowrap=\"nowrap\"") ) {
		      String[] pieces = tdList.elementAt(j).toHtml().split("href=\"");
                      if ( pieces.length > 1 ) {
			String[] piecesII = pieces[1].split("\"");
                        String anchorHref = "http://www.govdeals.com/" + piecesII[0];
                        try {	
	                   stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://www.govdeals.com/', '" + anchorHref + "', '" + resultsContent + "' )" ); 
                        } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                      }
                     }   
                   }                        
                 }
              }
           } catch ( ParserException pe ) { pe.printStackTrace(); }
         }
       } catch ( IOException ioe ) { ioe.printStackTrace(); }    
    }

    public static void IllinoisIbid() {
      String msg = "search=1&catid=&SearchStr=" + searchString + "&AllAnyExact=all&Region1=&Region2=&Region3=&Region4=&customs_criteria=1&cfs_txt_EqNum=&cfs_txt_VIN=&cfs_int_min_Odometer=&cfs_int_max_Odometer=&cfs_txt_make=&cfs_txt_model=&cfs_txt_modelyear=&PriceFrom=&PriceTo=&StartFrom=&StartTo=&EndFrom=&EndTo=&ExcludeStr=&OrderBy=end_asc&MaxResults=50&sbmtAdvSearch=Go#results";    
      try {
       URL ubl = new URL( "http://ibid.illinois.gov/advancedsearch.asp?" + msg );
       String resp = "";
       BufferedReader lecteur = new BufferedReader(new InputStreamReader(ubl.openStream()));
       String line;
       while ((line = lecteur.readLine()) != null) {
          resp += line;
       }
       lecteur.close();

       int startIdx = resp.indexOf( "<div id=\"SearchResults\">");
       int endIdx = resp.indexOf( "<div id=\"PageLinksS\">" );       
       //System.out.println( startIdx + " " + endIdx ); 
       if ( startIdx > 0 && endIdx > startIdx ) {
	 Parser prs = new Parser();
         try { 
	   prs.setInputHTML( resp.substring(startIdx,endIdx) );
           NodeList nList = prs.parse(null);

           for ( int i=0; i<nList.size(); i++ ) {
	      if ( nList.elementAt(i).getText().equals("div id=\"SearchResults\"") ) {
		 NodeList tblList = nList.elementAt(i).getChildren();
                 for ( int j=0; j<tblList.size(); j++ ) {
		    if ( tblList.elementAt(j).getText().equals("table cellpadding=\"2\" cellspacing=\"1\"") ) {
		      NodeList trList = tblList.elementAt(j).getChildren();
                      for ( int k=0; k<trList.size(); k++ ) {
			if ( trList.elementAt(k).getText().startsWith("tr class=\"Color") ) {
			  String resultsContent = trList.elementAt(k).toPlainTextString();
                          resultsContent = resultsContent.replaceAll("&nbsp;","");
                          resultsContent = resultsContent.replaceAll("\n","");
                          resultsContent = resultsContent.replaceAll("\t","");
                          resultsContent = resultsContent.replaceAll("\"","");
                          resultsContent = resultsContent.replaceAll("'","");
                          resultsContent = resultsContent.replace("+","");
                          while ( resultsContent.contains("  ") ) {
		            resultsContent = resultsContent.replaceAll("  "," ");
                          }
                          resultsContent = resultsContent.trim();
                          String[] pieces = trList.elementAt(k).toHtml().split("href=\"");
                          if ( pieces.length > 1 ) {
			     String[] piecesII = pieces[1].split("\"");
			     String anchorHref = piecesII[0].trim();
                             try {
	                       stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://ibid.illinois.gov', '" + anchorHref + "', '" + resultsContent + "' )" ); 
                             } catch ( SQLException sqle ) { sqle.printStackTrace(); } 
                          }     
                        }    
                      } 
                    } 
                 }  
              } 
           }   
	 } catch ( ParserException pe ) { pe.printStackTrace(); }
       }
      } catch ( IOException ioe ) { ioe.printStackTrace(); }
    }

    public static void PropertyRoom() {
      try {
       URL ubl = new URL( "http://propertyroom.com/s/" + searchString.toLowerCase() + "/1" );
       String resp = "";
       BufferedReader lector = new BufferedReader(new InputStreamReader(ubl.openStream()));
       String line;
       while ((line = lector.readLine()) != null) {
          resp += line;
       }
       lector.close();
      
       int startIdx = resp.indexOf("<div id=\"uxListingContainer\"");
       int endIdx = resp.indexOf("<div class=\"FatFootLayer\">");

       if ( startIdx>0 && endIdx>startIdx ) {
	 Parser ps = new Parser();
         try {
	   ps.setInputHTML( resp.substring( startIdx, endIdx ) );
           NodeList nList = ps.parse(null);
            
           for ( int i=0; i<nList.size(); i++ ) {
	     if ( nList.elementAt(i).getText().startsWith("div id=\"uxListingContainer\"") ) {
	       NodeList uxChildren = nList.elementAt(i).getChildren();
               for ( int j=0; j<uxChildren.size(); j++ ) {
		  if ( uxChildren.elementAt(j).getText().startsWith("div id=\"uxListingClass\"") ) {
		    NodeList uxListChildren = uxChildren.elementAt(j).getChildren();
                    String resultsContent = "";
                    String anchorHref = "";
                    for ( int k=0; k<uxListChildren.size(); k++ ) {
		      if ( uxListChildren.elementAt(k).getText().startsWith("a href") ) {
			 resultsContent += uxListChildren.elementAt(k).toPlainTextString();
                         String[] pieces = uxListChildren.elementAt(k).getText().split("\"");
                         if ( pieces.length > 1 ) {
			    anchorHref = "http://propertyroom.com" + pieces[1].trim();
                         }
                      }
                      if ( uxListChildren.elementAt(k).getText().startsWith("div id=\"uxPrice\"") ) {
                          resultsContent += " " + uxListChildren.elementAt(k).toPlainTextString();
                      }
                      resultsContent = resultsContent.replaceAll("\n","");
                      resultsContent = resultsContent.replaceAll("'","");
                      resultsContent = resultsContent.replaceAll("\"","");
                      while ( resultsContent.contains("  ") ) {
		        resultsContent = resultsContent.replaceAll("  "," ");
                      }
                    }
                    if ( !resultsContent.trim().equals("") ) {
			// System.out.println( resultsContent  + " " + anchorHref );
                       try {
	                  stmt.executeUpdate( "INSERT INTO auction_records VALUES ( 'http://propertyroom.com/', '" + anchorHref + "', '" + resultsContent + "' )" ); 
                       } catch ( SQLException sqle ) { sqle.printStackTrace(); }
                    }
                  }  
               }  
             }  
           } 
         } catch ( ParserException pe ) { pe.printStackTrace(); }
       }
      } catch ( IOException ioe ) { ioe.printStackTrace(); } 
    }

}
