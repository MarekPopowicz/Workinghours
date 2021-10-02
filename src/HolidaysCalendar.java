/*
Klasa wyszukująca i zwracająca dni wolne od pracy
 */


import java.io.IOException;
import java.util.Calendar;

/**
 *
 * @author Marek Popowicz
 */
public class HolidaysCalendar {

    private static String [] holydaysPatterns;
    private static String [] actualHolydays;
    private final DataWriterReader dataReader =  new DataWriterReader();
    private Calendar date;

    public HolidaysCalendar() throws IOException{
        holydaysPatterns = dataReader.readFromJARFile("wolne.txt");
    }

   public HolidaysCalendar(Calendar c) throws IOException{
       this.date = c;
       holydaysPatterns = dataReader.readFromJARFile("wolne.txt");
       actualHolydays(holydaysPatterns);
    }

    private void actualHolydays(String [] arrDays){
      actualHolydays  = new String[holydaysPatterns.length];
        for(int i=0; i<arrDays.length; i++){
            actualHolydays[i] = arrDays[i].substring(0, 5);
        }
    }

    public String [] getHolydaysPatterns(){
        return holydaysPatterns;
    }

    public boolean isDayFreeFromWork(String day){
        String month = String.valueOf(date.get(Calendar.MONTH)+1);
        if(day.length()<2) day = "0"+ day; 
        if(month.length()<2) month = "0"+ month; 
        day = day + "-" + month;
       
        for(String d: actualHolydays){
             if(d.equals(day))
                return true;
        }
    return false; 
    }
}
