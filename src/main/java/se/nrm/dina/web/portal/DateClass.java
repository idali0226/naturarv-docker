package se.nrm.dina.web.portal;

import java.util.Calendar;
import java.util.Date;  

/**
 *
 * @author idali
 */
public class DateClass {

    public static void main(String[] args) {
        Date date = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR); 
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        
        System.out.println("mon : " + cal.MONTH);
           
        Calendar  c = Calendar.getInstance();
        for(int i = 0; i > -12; i--) { 
            c.setTime(date);
            c.add(Calendar.MONTH, i);
            c.add(Calendar.YEAR, i);
            System.out.println("past : " + c.get(Calendar.MONTH) + " -- year ---   " + c.get(Calendar.YEAR) + " --- i " + i);
        }
        
        System.out.println("year month day : " + year + " --- " + month + " --- " + day + " --- " + cal.get(Calendar.MONTH));
    }
}
