
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasa pomocnicza.
 * @author marekpopowicz
 */
public final class Utils {
    
  public static String dateToString(Date date){
        
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");  
                String strDate = dateFormat.format(date);  
                return  strDate;  
    }
    
   public static Date stringToDate(String date) throws ParseException{
       
       return new SimpleDateFormat("yyyy-mm-dd").parse(date);   
   }
   
}
