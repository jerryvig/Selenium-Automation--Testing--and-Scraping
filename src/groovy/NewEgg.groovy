import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.By;
import java.util.List;

class NewEgg {
  static main( args ) {

    def csv = new File("./NewEgg.csv");

    def browser = new FirefoxDriver();

    browser.get( "http://www.newegg.com/Store/SubCategory.aspx?SubCategory=10&name=Desktop-PCs&Order=PRICE&Pagesize=100" );
 
    csv.write( "\"Description\",\"Price\"\n" );

    browser.findElementsByTagName("div").findAll{ it.getAttribute("class") == "itemCell" }.each { 
        def description = "";
        def price = "";

        it.findElements( By.tagName("span") ).findAll { span -> span.getAttribute("class") == "itemDescription" }.each { span ->
           description = it.getText();
        }
        
        it.findElements( By.tagName("li") ).findAll { li -> li.getAttribute("class") == "priceFinal" }.each { li ->
              price = li.getText().replaceAll( "now:", "" ).replaceAll("\n","");
        }
        csv.append( "\"${description}\",\"${price}\"\n" );
     }
  }
}