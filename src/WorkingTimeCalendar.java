/*
Klasa oblicza liczbę dni w bierzącym miesiącu, a następnie zapisuje w 3 oddzielnych 
tablicach: nazwy wszystkich dni tygodnia oraz przyporządkowany numer dna, a także 
ilość godzin pracy w danym dniu.
 */


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Marek Popowicz
 */
public class WorkingTimeCalendar {

    private String[] weekDays; //kolejne nazwy dni tygodnia odpowiednio do indeksów tablicy "monthDays"
    private int[] monthDays; // spolszczone nazwy miesięcy
    private double[] labourDays; // ilość godzin pracy w danym dniu.
    private final int[] freeDays = new int[31]; // indeksy tablicy "monthDays", które przypadają na dni wolne.
    private double totalTimeOfWork; // ilość godzin pracy w miesiącu
    private String monthName;
    private String[] data; // numery zadań wczytane z pliku zewnętrznego
    private  double unitTime; // jednostka czasu pracy przypadająca na dane zadanie w każdym dniu pracy
    private  String workingTimeMode;
    protected int  currentMonth; //miesiąc aktualnie rozpatrywany;
    protected int  currentYear; //rok aktualnie rozpatrywany;
    private boolean considerFreeTime;
    /**
     *
     * @param workingTimeMode Tryb pracy
     * @param currentMonth Aktualny miesiąc (domyślny)
     * @throws IOException
     */

    public WorkingTimeCalendar(String workingTimeMode, Calendar currentMonth, String filePath, boolean considerFreeTime) throws IOException {
        this.considerFreeTime = considerFreeTime;
        this.currentMonth = currentMonth.get(Calendar.MONTH);
        this.currentYear = currentMonth.get(Calendar.YEAR);
        this.workingTimeMode = workingTimeMode;
            setWeekDays(currentMonth);
            setMonthDays(currentMonth);
            setWorkDays(currentMonth);
            setMonthName(currentMonth);
        this.unitTime = countUnitWorkTimePerHour(filePath);
    }

    /**
     *
     * @param workingTimeMode Tryb pracy
     * @param customMonth Miesiąc wybrany przez użytkownika
     * @throws IOException
     */
    public WorkingTimeCalendar(String workingTimeMode, int customMonth, String  filePath, boolean considerFreeTime) throws IOException {
        this.currentMonth = customMonth-1;
        this.considerFreeTime = considerFreeTime;
        Calendar customCal = Calendar.getInstance(Locale.getDefault());
        this.currentYear = customCal.get(Calendar.YEAR);
        customCal.set(Calendar.MONTH, currentMonth); //ustawienie kalendarza na miesiąc aktualnie rozpatrywany

        this.workingTimeMode = workingTimeMode;
        setWeekDays(customCal);
        setMonthDays(customCal);
        setWorkDays(customCal);
        setMonthName(customCal);
        this.unitTime = countUnitWorkTimePerHour(filePath);
    }

    public WorkingTimeCalendar() {

    }

    private void setMonthName(Calendar date) {
        this.monthName = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }
    
    private Calendar getFirstDayOfMonth(Calendar c) {
      
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c;
    }
    
    public String getNameOfMonth() {
        return this.monthName;
    }
     
    public double getUnitWorkTimePerHour() {
        return this.unitTime;
    }
     
    private double countUnitWorkTimePerHour(String file) throws IOException{
         double totalTime=this.totalTimeOfWork;
         int totalLabourDays=0;
         double timePerUnit = 0.0;
         getData(file);
         if(data!=null) 
            timePerUnit = totalTime/this.data.length; // Czas pracy na jedno zadanie w skali miesiąca
         
         for(int i=0; i<labourDays.length;i++){ // policz liczbę dni roboczych w miesiącu
             if(labourDays[i]>0) totalLabourDays++;
         }
         
        double hourPerUnit = Math.round((timePerUnit/totalLabourDays)*10.0)/10.0; //Czas na jedno zadanie na każdy dzień pracy
        return hourPerUnit;
     }
     
    private void getData(String file) throws IOException{
         DataWriterReader dr = new DataWriterReader();
         String[] dataFromFile = dr.readDataFromFile(file);
            
            this.data = dataFromFile;
     }
    
// nazwy dni tygodnia
    private void setWeekDays(Calendar date) {

      date =  getFirstDayOfMonth(date);
      String[] weekDaysArr = new String[date.getActualMaximum(Calendar.DAY_OF_MONTH)];
        for (int i = 0; i < weekDaysArr.length; i++) {
             weekDaysArr[i] = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());


                        date.add(Calendar.DATE, 1);
        }
        this.weekDays = weekDaysArr;
        date.set(Calendar.MONTH, currentMonth); //reset po zakończeniu pętli
        date.set(Calendar.YEAR, currentYear);
    }
    
// ilość dni w miesiącu
    private void setMonthDays(Calendar date) {
      date =  getFirstDayOfMonth(date); 
        monthDays = new int[date.getActualMaximum(Calendar.DAY_OF_MONTH)];
        for (int i = 0; i < monthDays.length; i++) {
            monthDays[i] = date.get(Calendar.DAY_OF_MONTH);
            date.add(Calendar.DATE, 1);
        }
        date.set(Calendar.MONTH, currentMonth); //reset po zakończeniu pętli
        date.set(Calendar.YEAR, currentYear);
    }
    
// godziny pracy
    private void setWorkDays(Calendar date) throws IOException {
        int j=0;
        int dayOfWeek;
        boolean isDayFree;

            date =  getFirstDayOfMonth(date); 
        
        this.labourDays = new double[date.getActualMaximum(Calendar.DAY_OF_MONTH)];
        HolidaysCalendar hc = new HolidaysCalendar(date);
       
        for (int i = 0; i < this.labourDays.length; i++) {

            if(!considerFreeTime) { //Jeśli wyłączona opcja uwzględniania zadeklarowanych dni wolnych
                isDayFree = false; //Wszystkie dni są pracujące
            }
            else // Sprawdź, czy jest to jaki zadeklarowany dzień wolny.
                isDayFree = hc.isDayFreeFromWork(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));

            dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY || isDayFree) {
                    this.labourDays[i] = 0;
                    this.freeDays[j] = i; j++;
            }
            else if(dayOfWeek == Calendar.FRIDAY) {
                if(workingTimeMode.equals("8.5/6.0")) this.labourDays[i] = 6;
                if(workingTimeMode.equals("8.0/8.0")) this.labourDays[i] = 8;
            }
            else {
                if(workingTimeMode.equals("8.5/6.0")) this.labourDays[i] = 8.5;
                if(workingTimeMode.equals("8.0/8.0")) this.labourDays[i] = 8;
            }
            
            date.add(Calendar.DATE, 1);
            this.totalTimeOfWork += this.labourDays[i];
        }
            date.set(Calendar.MONTH, currentMonth); //reset po zakończeniu pętli
            date.set(Calendar.YEAR, currentYear);
    }
     
    public String[] getWeekDays(){
        return this.weekDays;
     }

    public int[] getMonthDays(){
        return this.monthDays;
     }
     
    public double[] getWorkDays(){
        return this.labourDays;
     }
     
    public double getLabourTotalTime(){
        return this.totalTimeOfWork;
     }
    
    public int[] getFreeDays(){
        return this.freeDays;
     }
     
}
