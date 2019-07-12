/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.web.portal;
 
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;  
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author idali
 */
public class Main {
    
    public static void main(String[] args) {
        int a = 43;
        int b = 50;
        
        System.out.println("a / b : " + a/b);
        System.out.println("a % b : " + a % b);
         
        String str = "abcd123efgöäå.;fdas, df[](){}@€ //dsdf//";
        String s = str.replaceAll("\\W", "");
        System.out.println("s : " + s);
 

        String s1 = str.replaceAll("[(),/,]",""); 
        System.out.println("s1 : " + s1);
         
         
        
        
        System.out.println("Calendar.getInstance()  " +    Calendar.getInstance());
        
        
        int count = StringUtils.countMatches("cln:163840 cln:229376 cln:262144 cln:294912 cln:327681 cln:360449 cln:393217 cln:425985 cln:491521 cln:524289", "cln:");
        System.out.println("count : " + count);
         
     
        Double d = -57.1;
        int i = d.intValue();
        
        System.out.println("i " + i);
        System.out.println((i%2)==0);
        
        Double dd =  d + 1;
        System.out.println("" + dd.intValue());
          
        double diff = 180 - 70.16 + (-143.6) + 180;
        double    range = diff / 40; 
        System.out.println("range : " + range);
        
        diff = 154.046875 - 147.89453125;
        range = diff / 40;
        System.out.println("range : " + range);
        
        
        
        System.out.println("18 % 5 ? " + 18%5);
    }
    
}
