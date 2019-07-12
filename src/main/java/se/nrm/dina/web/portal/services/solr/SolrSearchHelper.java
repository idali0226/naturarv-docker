package se.nrm.dina.web.portal.services.solr;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.common.util.HelpClass;
import se.nrm.dina.web.portal.model.QueryBean;

/**
 *
 * @author idali
 */
public class SolrSearchHelper {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static SolrSearchHelper instance = null; 
    
    private final static String LOCAL_SOLR_URL = "http://localhost:8983/solr/";
    private final static String REMOTE_SOLR_URL = "http://dina-portal:8983/solr/";    
//    private final static String LOCAL_SOLR_URL = "http://dina-db:8983/solr/";                      // dina-portal 
    private final static String REMOTE_SOLR_URL_AS = "http://dina-db:8983/solr/";
     
    
    private static String SOLR_URL = REMOTE_SOLR_URL;

    SolrSearchHelper() {
        InetAddress inetAddress;
        String hostName = "";
        try {
            inetAddress = InetAddress.getLocalHost();
            hostName = inetAddress.getHostName();
        } catch (UnknownHostException ex) {
            logger.error(ex.getMessage());
        }
         
        logger.info("hostName : {}", hostName);
        if(hostName.toLowerCase().contains("ida")) {
           SOLR_URL = LOCAL_SOLR_URL;
        } else if(hostName.contains("dina-web") || hostName.contains("as.nrm.se")) {
            SOLR_URL = REMOTE_SOLR_URL_AS;
        } else {
            SOLR_URL = REMOTE_SOLR_URL;
        }   
    }
     
    
    
    public static synchronized SolrSearchHelper getInstance() {
        if (instance == null) {
            instance = new SolrSearchHelper(); 
        }
        return instance;
    } 
    
    protected String getSolrUrl() { 
        return SOLR_URL;
    }
    
    
    public String buildFullTextSearchText(String value) {
        
        if(value != null && !value.isEmpty()) {
            value = replaceChars(value.trim()); 
        
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            sb.append(buildContainsString(value, "ftx", true));
            sb.append(") ");
            sb.append("(");
            sb.append(buildStartsWithString(value, "ftx", true));
            sb.append(") ");
            sb.append("(");
            sb.append(buildContainsString(value, "text", false));
            sb.append(")");
            return sb.toString().trim();
        } 
        return value;
    }
    
  
    private String buildContainsString(String value, String field, boolean boost) { 
        StringBuilder sb = new StringBuilder();
        String[] strings = value.split(" ");
        if(strings.length == 1) {
            sb.append(field);
            sb.append(":*");
            sb.append(value);
            sb.append(boost ? "*^2" : "*");
        } else { 
            sb.append("(");
            for (String s : strings) {
                if(!s.isEmpty()) {
                    sb.append("+");
                    sb.append(field);
                    sb.append(":*");
                    sb.append(s);
                    sb.append(boost ? "*^2 " : "* "); 
                }
            }
            sb.append(")");
        }
        return sb.toString().trim();
    }
    
    private String buildStartsWithString(String value, String field, boolean boost) {
        StringBuilder sb = new StringBuilder();
        String[] strings = value.split(" ");
        
        if(strings.length > 1) {
            sb.append("(+"); 
        } 
        sb.append(field);
        sb.append(":");
        sb.append(strings[0]);
        sb.append(boost ? "*^2 " : "* ");

        for (int i = 1; i < strings.length; i++) {
            if(!strings[i].isEmpty()) {
                sb.append("+");
                sb.append(field);
                sb.append(":*");
                sb.append(strings[i]);
                sb.append(boost ? "*^2 " : "* "); 
            }
        }
        if(strings.length > 1) {
            sb.append(")"); 
        } 
        return sb.toString().trim();
    }
    
    
    
    
    /**
     * Replace "[,],(,)" chars with empty space
     * 
     * @param value
     * @return String
     */
    private String replaceChars(String value) {
        String s = value.replaceAll("[\\[\\](),]", " ");  
        return s.trim(); 
    }
         
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
     
    public String buildAdvanceSearchText(QueryBean bean) {
        
        logger.info("buildAdvanceSearchText");
         
        String value = bean.getValue(); 
        if(value != null && !value.isEmpty()) {
            value = replaceChars(value.trim());
        } else {
            value = "*";
        }

        String op = bean.getOperattion();

        StringBuilder sb = new StringBuilder();
        switch (op) {
            case "not":
                sb.append("-");
                break;
            case "and":
                sb.append("+");
                break;
        }

        sb.append("(");
        sb.append(buildString(value, bean.getField(), bean.getContent()));
        sb.append(")");
        return sb.toString().trim();
    } 
    
    public String buildAdvanceFullTextSearchText(String value, String content) {
        
        logger.info("buildAdvanceFullTextSearchText");
        String text = replaceChars(value); 
        return buildString(text, "text", content);
    }
    
    
    public String buildString(String value, String field, String content) {
         
        if(value.contains("(") || value.contains(")") || value.contains(",")) {
            value = replaceChars(value);
        } 
        
        StringBuilder sb = new StringBuilder();
        switch (content) {
            case "exact":
                sb.append(buildExactString(value, field, false));
                break;
            case "startswith":
                sb.append(buildStartsWithString(value, field, false));
                break; 
            default:
                sb.append(buildContainsString(value, field, false));
                break;
        }
        return sb.toString().toLowerCase();
    }
    
    private String buildExactString(String value, String field, boolean boost) { 
        logger.info("buildExactString : {}", value, field);
        
        StringBuilder sb = new StringBuilder(); 
        
        String[] strings = value.split(" ");
        if(strings.length > 1) { 
            sb.append("(");
            for (String s : strings) {
                if(!s.isEmpty()) {
                    sb.append("+");
                    sb.append(field);
                    sb.append(":");
                    sb.append(s);
                    sb.append(boost ? "^2" : " ");
                } 
            }
            sb.append(")");
        } else {
            sb.append(field);
            sb.append(":");
            sb.append(value); 
            if(boost) {
                sb.append("^2");
            }
        } 
        return sb.toString().trim();
    }
    

    

    public String buildCommonNameSearchText(QueryBean bean) {
        logger.info("buildCommonNameSearchText : {}", bean.getValue());
        
        String value = bean.getValue();
        if(value != null && !value.isEmpty()) {
            value = replaceChars(bean.getValue().trim()); 
        } else {
            value = "*";
        }
        
        
        
        String op = bean.getOperattion(); 
        StringBuilder sb = new StringBuilder(); 
        switch (op) {
            case "not":
                sb.append("-");
                break;
            case "and":   
                sb.append("+");
                break;
        }
        sb.append("("); 
        sb.append(buildExactString(value, "cm", false)); 
         
        String content = bean.getContent(); 
        switch (content) {
            case "startswith":
                sb.append(" ");
                sb.append(buildStartsWithString(value, "cm", false));
                break;
            case "contains":
                sb.append(" "); 
                sb.append(buildContainsString(value, "cm", false));
                break;
        }
        sb.append(")");
        return sb.toString().trim();
    }
    
    public String buildDeterminationSearch(QueryBean bean) { 
        logger.info("buildDeterminationSearch : {}", bean.getValue());
        String value = bean.getValue();
        if(value != null && !value.isEmpty()) {
            value = replaceChars(bean.getValue().trim()); 
        } else {
            value = "*";
        } 
         
        String op = bean.getOperattion(); 
        StringBuilder sb = new StringBuilder(); 
        switch (op) {
            case "not":
                sb.append("-");
                break;
            case "and":   
                sb.append("+");
                break;
        }
        sb.append("(");
 
        String content = bean.getContent(); 
        switch (content) {
            case "exact":
                sb.append(buildExactString(value, "ftx", false));
                break;
            case "startswith":
                sb.append(buildStartsWithString(value, "ftx", false));
                break; 
            default:
                sb.append(buildContainsString(value, "ftx", false));
                break;
        }
        sb.append(")"); 
        return sb.toString().trim();
    }
    
    public String buildClassificationSearch(QueryBean bean) {
        
        logger.info("buildClassificationSearch : {}", bean.getValue());
        
        String value = bean.getValue();
        if(value != null && !value.isEmpty()) {
            value = replaceChars(bean.getValue().trim()); 
        } else {
            value = "*";
        } 
        
        String op = bean.getOperattion(); 
        StringBuilder sb = new StringBuilder(); 
        switch (op) {
            case "not":
                sb.append("-");
                break;
            case "and":   
                sb.append("+");
                break;
        }
        sb.append("(");
  
        String content = bean.getContent();  
        
        StringBuilder tsb = new StringBuilder();
        StringBuilder hsb = new StringBuilder();
 
        tsb.append("(");
        hsb.append("(");
         
        switch (content) {
            case "exact":
                tsb.append(buildExactString(value, "ftx", true));
                hsb.append(buildExactString(value, "ht", false));
                break;
            case "startswith":
                tsb.append(buildStartsWithString(value, "ftx", true));
                hsb.append(buildStartsWithString(value, "ht", false));
                break;
            default: 
                tsb.append(buildContainsString(value, "ftx", true));
                hsb.append(buildContainsString(value, "ht", false));
                break;
        }
        tsb.append(")");
        hsb.append(")");
        sb.append(tsb.toString().trim());
        sb.append(" ");
        sb.append(hsb.toString().trim());
        sb.append(")");

        return sb.toString().trim();
    }
    
    
    
    
    
    
    
    
    
    public String buildSeason(QueryBean bean) {
        int stMonth = bean.getStartMonth();
        int edMonth = bean.getEndMonth();
        
        if(stMonth == 0) {
            stMonth = 1;
        }
        
        if(edMonth == 0) {
            edMonth = 12;
        }

        int startDay = bean.getStartDay();
        int endDay = bean.getEndDay();
        
        int fromDayOfYear = getDayOfYear(stMonth, startDay);
        int toDayOfYear = getDayOfYear(edMonth, endDay);

        StringBuilder sb = new StringBuilder();
        String operation = bean.getOperattion();

        switch (operation) {
            case "not":
                sb.append("-");
                break;
            case "and":
                sb.append("+");
                break;
        }
        sb.append("dy:[");
  
        sb.append(fromDayOfYear);
        sb.append(" TO ");
        sb.append(toDayOfYear);
        sb.append("]");

        return sb.toString().trim();
    }
    
    /**
     * buildDate
     * @param bean
     * @return String
     */
    public String buildDate(QueryBean bean) {
          
        String strTimeFrom = "T00:00:00Z";
        String strTimeTo = "T23:59:59Z";
        
        String op = bean.getOperattion();
        
        Date fromDate = bean.getFromDate();
        Date toDate = bean.getToDate();
         
        String strFromDate = "*";
        String strToDate = "*";
        if(fromDate != null) {  
             strFromDate = HelpClass.convertDateToUTCString(fromDate, strTimeFrom);
        }
        
        if(toDate != null) { 
            strToDate = HelpClass.convertDateToUTCString(toDate, strTimeTo);
        } 
        
        String key = "sd:["; 
        switch (op) {
            case "not":
                key = "-sd:[";
                break;
            case "and":
                key = "+sd:[";
                break;
        }
        
        StringBuilder sb = new StringBuilder(); 
        sb.append(key);
        sb.append(strFromDate);
        sb.append(" TO ");
        sb.append(strToDate);
        sb.append("]");
          
        return sb.toString();
    }
    
    

 
        
    
    public String buildFullTextSearchText(QueryBean bean) {

        StringBuilder sb = new StringBuilder();
        String op = bean.getOperattion();
        switch (op) {
            case "and":
                sb.append("+");
                break;
            case "not":
                sb.append("-");
                break;
        }
        sb.append("(");
        
        String value = bean.getValue();
        if( value != null && !value.isEmpty()) {
            value = replaceChars(bean.getValue().trim());  
        } else {
            value = "*";
        }
         
        
        String content = bean.getContent();
        switch (content) {
            case "exact":
                sb.append(buildExactString(value, "text", false));
                break;
            case "startswith":
                sb.append(buildStartsWithString(value, "text", false));
                break;  
            default:
                sb.append(buildFullTextSearchText(value));
                break;
        }
        sb.append(")");
        return sb.toString().trim();
    }

    /**
     * getDayOfYear
     * @param month
     * @param day
     * @return int - day of year
     */
    private int getDayOfYear(int month, int day) {
        int year = 2000;
        
        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append("-");
        sb.append(month);
        sb.append("-");
        sb.append(day);
        
        Date date = HelpClass.stringToDate(sb.toString().trim());
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        return cal.get(Calendar.DAY_OF_YEAR); 
    }
}
