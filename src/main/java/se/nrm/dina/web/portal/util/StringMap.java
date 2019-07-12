package se.nrm.dina.web.portal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner; 

/**
 *
 * @author idali
 */
public class StringMap { 
    
    private static final String SIMPLE_SEARCH_DEFAULT_TEXT_EN = "Search collections (species, genus, family, collectors, location, etc.)";
    private static final String SIMPLE_SEARCH_DEFAULT_TEXT_SV = "Sök i samlingar (art, släkte, familj, insamlare, plats etc.)";
    
    private final static String MONTH_CHART_TITLE_SV = "Registrerade föremål senaste 12 månaderna";
    private final static String YEAR_CHART_TITLE_SV = "Ackumulerat antal registrerade föremål";
    
    private final static String MONTH_CHART_TITLE_EN = "Registered specimens last 12 months";
    private final static String YEAR_CHART_TITLE_EN = "Cumulative number of registered specimens";
     
    private static final String MONTH_CHART_AXIS_SV = "Månad";
    private static final String MONTH_CHART_AXIS_EN = "Month";
    
    private static final String YEAR_CHART_AXIS_SV = "År";
    private static final String YEAR_CHART_AXIS_EN = "Year";
    
    private static final String CHART_YAXIS_SV = "Antal föremål";
    private static final String CHART_YAXIS_EN = "Total specimens";
    
    private final static String JAN_SHORT = "Jan";
    private final static String FEB_SHORT = "Feb";
    private final static String MAR_SHORT = "Mar";
    private final static String APR_SHORT = "Apr";
    private final static String MAY_SHORT = "May";
    private final static String JUN_SHORT = "Jun";
    private final static String JUL_SHORT = "Jul";
    private final static String AUG_SHORT = "Aug";
    private final static String SEP_SHORT = "Sep";
    private final static String OCT_SHORT = "Oct";
    private final static String NOV_SHORT = "Nov";
    private final static String DEC_SHORT = "Dec";
     
    private final static String JAN_SV_SHORT = "jan";
    private final static String FEB_SV_SHORT = "feb";
    private final static String MAR_SV_SHORT = "mar";
    private final static String APR_SV_SHORT = "apr";
    private final static String MAY_SV_SHORT = "maj";
    private final static String JUN_SV_SHORT = "jun";
    private final static String JUL_SV_SHORT = "jul";
    private final static String AUG_SV_SHORT = "aug";
    private final static String SEP_SV_SHORT = "sep";
    private final static String OCT_SV_SHORT = "okt";
    private final static String NOV_SV_SHORT = "nov";
    private final static String DEC_SV_SHORT = "dec";
    
    private StringJoiner string_map_key;
    
    private static Map<String, List<String>> institutionMap;
    
    
        
    private static final Map<Enum, String> MONTH_MAP_EN;
    private static final Map<Enum, String> MONTH_MAP_SV;

    static {
        MONTH_MAP_EN = new HashMap<>();
        MONTH_MAP_SV = new HashMap<>();
        
        MONTH_MAP_EN.put(MonthElement.Jan, JAN_SHORT);
        MONTH_MAP_EN.put(MonthElement.Feb, FEB_SHORT);
        MONTH_MAP_EN.put(MonthElement.Mar, MAR_SHORT);
        MONTH_MAP_EN.put(MonthElement.Apr, APR_SHORT);
        MONTH_MAP_EN.put(MonthElement.May, MAY_SHORT);
        MONTH_MAP_EN.put(MonthElement.Jun, JUN_SHORT);
        MONTH_MAP_EN.put(MonthElement.Jul, JUL_SHORT);
        MONTH_MAP_EN.put(MonthElement.Aug, AUG_SHORT);
        MONTH_MAP_EN.put(MonthElement.Sep, SEP_SHORT);
        MONTH_MAP_EN.put(MonthElement.Oct, OCT_SHORT);
        MONTH_MAP_EN.put(MonthElement.Nov, NOV_SHORT);
        MONTH_MAP_EN.put(MonthElement.Dec, DEC_SHORT);
        
        MONTH_MAP_SV.put(MonthElement.Jan, JAN_SV_SHORT);
        MONTH_MAP_SV.put(MonthElement.Feb, FEB_SV_SHORT);
        MONTH_MAP_SV.put(MonthElement.Mar, MAR_SV_SHORT);
        MONTH_MAP_SV.put(MonthElement.Apr, APR_SV_SHORT);
        MONTH_MAP_SV.put(MonthElement.May, MAY_SV_SHORT);
        MONTH_MAP_SV.put(MonthElement.Jun, JUN_SV_SHORT);
        MONTH_MAP_SV.put(MonthElement.Jul, JUL_SV_SHORT);
        MONTH_MAP_SV.put(MonthElement.Aug, AUG_SV_SHORT);
        MONTH_MAP_SV.put(MonthElement.Sep, SEP_SV_SHORT);
        MONTH_MAP_SV.put(MonthElement.Oct, OCT_SV_SHORT);
        MONTH_MAP_SV.put(MonthElement.Nov, NOV_SV_SHORT);
        MONTH_MAP_SV.put(MonthElement.Dec, DEC_SV_SHORT);
    }
    
    public String getMonthName(Enum monthElement, boolean isSwedish) {
        return isSwedish ? MONTH_MAP_SV.get(monthElement) : MONTH_MAP_EN.get(monthElement);
    }
    
    
    
    
    public String getSearchDefaultText(String locale) {
        return locale.equals("sv") ? SIMPLE_SEARCH_DEFAULT_TEXT_SV : SIMPLE_SEARCH_DEFAULT_TEXT_EN;
    }
     
    
    
    /**
     * Get month name by locale
     * @param numMonth
     * @param locale
     * @return String
     */
    public String getMonthShort(int numMonth, String locale) {
         
        switch (numMonth) {
            case 1:  return locale.equals("sv") ? JAN_SV_SHORT : JAN_SHORT; 
            case 2:  return locale.equals("sv") ? FEB_SV_SHORT : FEB_SHORT; 
            case 3:  return locale.equals("sv") ? MAR_SV_SHORT : MAR_SHORT; 
            case 4:  return locale.equals("sv") ? APR_SV_SHORT : APR_SHORT; 
            case 5:  return locale.equals("sv") ? MAY_SV_SHORT : MAY_SHORT; 
            case 6:  return locale.equals("sv") ? JUN_SV_SHORT : JUN_SHORT; 
            case 7:  return locale.equals("sv") ? JUL_SV_SHORT : JUL_SHORT; 
            case 8:  return locale.equals("sv") ? AUG_SV_SHORT : AUG_SHORT; 
            case 9:  return locale.equals("sv") ? SEP_SV_SHORT : SEP_SHORT; 
            case 10: return locale.equals("sv") ? OCT_SV_SHORT : OCT_SHORT; 
            case 11: return locale.equals("sv") ? NOV_SV_SHORT : NOV_SHORT; 
            case 12: return locale.equals("sv") ? DEC_SV_SHORT : DEC_SHORT;  
            default: return locale.equals("sv") ? JAN_SV_SHORT : JAN_SHORT; 
        }
    }
    
    public String getMonthChartTitle(String locale) {
        return locale.equals("sv") ? MONTH_CHART_TITLE_SV : MONTH_CHART_TITLE_EN;
    }
    
    public String getYearChartTitle(String locale) {
        return locale.equals("sv") ? YEAR_CHART_TITLE_SV : YEAR_CHART_TITLE_EN;
    }
    
    public String getMonthChartAxis(String locale) {
        return locale.equals("sv") ? MONTH_CHART_AXIS_SV : MONTH_CHART_AXIS_EN;
    }
    
    public String getYearChartAxis(String locale) {
        return locale.equals("sv") ? YEAR_CHART_AXIS_SV : YEAR_CHART_AXIS_EN;
    }
    
    public String getChartYaxis(String locale) {
        return locale.equals("sv") ? CHART_YAXIS_SV : CHART_YAXIS_EN;
    }
    
    public List<String> getInstitutionCollections(String institution) { 
        List<String> collections = institutionMap.get(institution);
        return collections == null ? new ArrayList<>() : collections; 
    }
    
    
    /**
     * 
     * Get institution code.
     * @param key
     * @return institution code
     */
    public String getInstitution(String key) {
        if (key.equals("4")) {
            return "gnm";
        } else {
            return "nrm";
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private static StringMap instance = null;
      
    
//    private static final Map<String, List<String>> institutionSubCollectionMap = new HashMap<>();
//    private static Map<String, String> subCollectionMap;
    private static Map<String, String> stringMap;
    
    
//    private static List<String> subCollectionList_en;
//    private static List<String> subCollectionList_sv;
    
    private static Map<String, String> collectionCodeMap; 
    private static Map<String, Integer> collectionNameCodeMap;
    
    
      
    private static Map<String, String> fieldNameMap;
    

    
    private final static String SELECT_MONTH = "Select month";
    private final static String JAN = "January";
    private final static String FEB = "February";
    private final static String MAR = "March";
    private final static String APR = "April";
    private final static String MAY = "May";
    private final static String JUN = "June";
    private final static String JUL = "July";
    private final static String AUG = "Augst";
    private final static String SEP = "September";
    private final static String OCT = "October";
    private final static String NOV = "November";
    private final static String DEC = "December";
    
    private final static String SELECT_MONTH_SV = "Välj månad";
    private final static String JAN_SV = "Januari";
    private final static String FEB_SV = "Februari";
    private final static String MAR_SV = "Mars";
    private final static String APR_SV = "April";
    private final static String MAY_SV = "Maj";
    private final static String JUN_SV = "Juni";
    private final static String JUL_SV = "Juli";
    private final static String AUG_SV = "Augusti";
    private final static String SEP_SV = "September";
    private final static String OCT_SV = "Oktober";
    private final static String NOV_SV = "November";
    private final static String DEC_SV = "December";
    

    
    
    private final String NRM_EN = "Swedish Museum of Natural History";
    private final String NRM_SV = "Naturhistoriska riksmuseet";
    private final String GNM_EN = "Gothenburg Natural History Museum";
    private final String GNM_SV = "Göteborgs naturhistoriska museum";
//    private final String EM_EN = "Museum of Evolution";
//    private final String EM_SV = "Evolutionsmuseet";
    
    
    private static final String ONE_DAY_EN = "Since 1 day";
    private static final String TWO_DAY_EN = "Since 2 days";
    private static final String ONE_WEEK_EN = "Since 1 week";
    private static final String ONE_MONTH_EN = "Since 1 month";
    private static final String END_OF_YEAR_EN = "This year";
    private static final String ONE_YEAR_EN = "Since 1 year";
    private static final String TWO_YEAR_EN = "Since 2 years";
    
    private static final String ONE_DAY_SV = "Sedan 1 dag";
    private static final String TWO_DAY_SV = "Sedan 2 dagar";
    private static final String ONE_WEEK_SV = "Sedan 1 vecka";
    private static final String ONE_MONTH_SV = "Sedan 1 månad";
    private static final String END_OF_YEAR_SV = "Sedan årsskiftet";
    private static final String ONE_YEAR_SV = "Sedan 1 år";
    private static final String TWO_YEAR_SV = "Sedan 2 år";
    
    private static final String TOTAL_EN = "Total: ";
    private static final String TOTAL_SV = "Totalt: ";
     
    
    private static final String ALL_EN = "all";
    private static final String ALL_SV = "alla";
    private static final String CHECKED_EN = "checked";
    private static final String CHECKED_SV = "markerade";
     

    
    
    private static List<String> months_en;
    private static List<String> months_sv;
    
    
    private static void initData() {
        List<String> collectionList = new ArrayList<>();
        collectionList.add("163840"); 
        collectionList.add("262144"); 
        collectionList.add("327681"); 
        collectionList.add("393217");
        collectionList.add("425985");
        collectionList.add("458752"); 
        collectionList.add("491521"); 
        collectionList.add("589825");
        collectionList.add("655361");
        collectionList.add("688128");
                
        institutionMap = new HashMap<>();
        institutionMap.put("nrm", collectionList);

        collectionList = new ArrayList<>();
        collectionList.add("458752");
        institutionMap.put("gnm", collectionList);   
        
        fieldNameMap = new HashMap<>();
        fieldNameMap.put("tx_en", "Scientific name");
        fieldNameMap.put("tx_sv", "Vetenskapligt namn"); 
        
        fieldNameMap.put("ftx_en", "Classification");
        fieldNameMap.put("ftx_sv", "Klassifikation"); 
        
        fieldNameMap.put("eftx_en", "Determination");
        fieldNameMap.put("eftx_sv", "Bestämning"); 
        
        fieldNameMap.put("cm_en", "Common name"); 
        fieldNameMap.put("cm_sv", "Svenska namn"); 
        fieldNameMap.put("au_en", "Author"); 
        fieldNameMap.put("au_sv", "Auktor");
        
        fieldNameMap.put("auth_en", "Author"); 
        fieldNameMap.put("auth_sv", "Auktor");
         
        fieldNameMap.put("cn_en", "Catalog number"); 
        fieldNameMap.put("cn_sv", "Cataloguenumber");
        
        fieldNameMap.put("lc_en", "Locality"); 
        fieldNameMap.put("lc_sv", "Lokal / Geografi");
        
        fieldNameMap.put("sfn_en", "Station field number"); 
        fieldNameMap.put("sfn_sv", "Station field number");
        
        fieldNameMap.put("col_en", "Collector"); 
        fieldNameMap.put("col_sv", "Insamlare av");
        fieldNameMap.put("dn_en", "Determiner"); 
        fieldNameMap.put("dn_sv", "Bestämd av");
         
        stringMap = new HashMap();
        stringMap.put("all_en", ALL_EN);
        stringMap.put("all_sv", ALL_SV);
        stringMap.put("checked_en", CHECKED_EN);
        stringMap.put("checked_sv", CHECKED_SV); 
        
        stringMap.put("oneDay_en", ONE_DAY_EN);
        stringMap.put("twoDay_en", TWO_DAY_EN);
        stringMap.put("oneWeek_en", ONE_WEEK_EN);
        stringMap.put("oneMonth_en", ONE_MONTH_EN);
        stringMap.put("endOfYear_en", END_OF_YEAR_EN); 
        stringMap.put("oneYear_en", ONE_YEAR_EN);
        stringMap.put("twoYear_en", TWO_YEAR_EN);
        
        
        
        stringMap.put("oneDay_sv", ONE_DAY_SV);
        stringMap.put("twoDay_sv", TWO_DAY_SV);
        stringMap.put("oneWeek_sv", ONE_WEEK_SV);
        stringMap.put("oneMonth_sv", ONE_MONTH_SV);
        stringMap.put("endOfYear_sv", END_OF_YEAR_SV); 
        stringMap.put("oneYear_sv", ONE_YEAR_SV);
        stringMap.put("twoYear_sv", TWO_YEAR_SV);
        
        stringMap.put("total_sv", TOTAL_SV);
        stringMap.put("total_en", TOTAL_EN);
        
        stringMap.put("sendmail_en", "Thank you for reporting the error. Your message will be delivered to staff responsible for evaluating and correcting the source dataset.");
        stringMap.put("sendmail_sv", "Tack för din felrapport! Den kommer nu skickas till ansvarig personal för utvärdering och uppdatering av databasen."); 
         
        
        collectionCodeMap = new HashMap<>();
        collectionCodeMap.put("163840", "NHRS");
        collectionCodeMap.put("229376", "NHRS_INV");
        collectionCodeMap.put("262144", "SMTP_INV");
        collectionCodeMap.put("294912", "SMTP_OBS");
        collectionCodeMap.put("327681", "NRMORN"); 
        collectionCodeMap.put("393217", "NRMMAM");
        collectionCodeMap.put("425985", "NRMHERP");
        collectionCodeMap.put("458752", "GNMENT");
        collectionCodeMap.put("491521", "PZ");
        collectionCodeMap.put("524289", "PB");
        collectionCodeMap.put("589825", "NRM_FISH_DNA");
        
        collectionNameCodeMap = new HashMap<>();
        collectionNameCodeMap.put("NHRS", 163840);
        collectionNameCodeMap.put("NHRS_INV", 229376);
        collectionNameCodeMap.put("SMTP_INV", 262144);
        collectionNameCodeMap.put("SMTP_OBS", 294912);
        collectionNameCodeMap.put("NRMORN", 327681);
        collectionNameCodeMap.put("NRM_FISH_DNA", 589825);
        collectionNameCodeMap.put("NRMMAM", 393217);
        collectionNameCodeMap.put("NRMHERP", 425985);
        collectionNameCodeMap.put("GNMENT", 458752);
        collectionNameCodeMap.put("PZ", 491521);
        collectionNameCodeMap.put("PB", 524289); 
        collectionNameCodeMap.put("NRMMIN", 557057); 
        
                
        months_en = new ArrayList();
        months_en.add(SELECT_MONTH);
        months_en.add(JAN);
        months_en.add(FEB);
        months_en.add(MAR);
        months_en.add(APR);
        months_en.add(MAY);
        months_en.add(JUN);
        months_en.add(JUL);
        months_en.add(AUG);
        months_en.add(SEP);
        months_en.add(OCT);
        months_en.add(NOV);
        months_en.add(DEC);
        
        months_sv = new ArrayList(); 
        months_sv.add(SELECT_MONTH_SV);
        months_sv.add(JAN_SV);
        months_sv.add(FEB_SV);
        months_sv.add(MAR_SV);
        months_sv.add(APR_SV);
        months_sv.add(MAY_SV);
        months_sv.add(JUN_SV);
        months_sv.add(JUL_SV);
        months_sv.add(AUG_SV);
        months_sv.add(SEP_SV);
        months_sv.add(OCT_SV);
        months_sv.add(NOV_SV);
        months_sv.add(DEC_SV); 
    }
    
  
    public static synchronized StringMap getInstance() {
        if (instance == null) {
            instance = new StringMap();
            initData(); 
        } 
        return instance;
    } 
    
    public String languageMonthMatch(String mon, boolean isSwedish) {

        if (isSwedish) {
            switch (mon) {
                case JAN_SHORT:
                    return JAN_SV_SHORT;
                case FEB_SHORT:
                    return FEB_SV_SHORT;
                case MAR_SHORT:
                    return MAR_SV_SHORT;
                case APR_SHORT:
                    return APR_SV_SHORT;
                case MAY_SHORT:
                    return MAY_SV_SHORT;
                case JUN_SHORT:
                    return JUN_SV_SHORT;
                case JUL_SHORT:
                    return JUL_SV_SHORT;
                case AUG_SHORT:
                    return AUG_SV_SHORT;
                case SEP_SHORT:
                    return SEP_SV_SHORT;
                case OCT_SHORT:
                    return OCT_SV_SHORT;
                case NOV_SHORT:
                    return NOV_SV_SHORT;
                case DEC_SHORT:
                    return DEC_SV_SHORT;
                default:
                    return mon;
            }
        } else {
            switch (mon) {
                case JAN_SV_SHORT:
                    return JAN_SHORT;
                case FEB_SV_SHORT:
                    return FEB_SHORT;
                case MAR_SV_SHORT:
                    return MAR_SHORT;
                case APR_SV_SHORT:
                    return APR_SHORT;
                case MAY_SV_SHORT:
                    return MAY_SHORT;
                case JUN_SV_SHORT:
                    return JUN_SHORT;
                case JUL_SV_SHORT:
                    return JUL_SHORT;
                case AUG_SV_SHORT:
                    return AUG_SHORT;
                case SEP_SV_SHORT:
                    return SEP_SHORT;
                case OCT_SV_SHORT:
                    return OCT_SHORT;
                case NOV_SV_SHORT:
                    return NOV_SHORT;
                case DEC_SV_SHORT:
                    return  DEC_SHORT;
                default:
                    return mon;
            } 
        }
    }
    
    public int getCollectionCodeByName(String collectionName) {
        return collectionNameCodeMap.get(collectionName);
    }
    
    public String getFieldName(String key) {
        return fieldNameMap.get(key);
    }
    
    public String getCollectionCode(String key) {
        return collectionCodeMap.get(key);
    }
    
    public String getCollectionId(String collectionCode) {
        for(Map.Entry<String, String> entry : collectionCodeMap.entrySet()) {
            if(entry.getValue().equals(collectionCode)) {
                return entry.getKey();
            }
        }
        return "";
    }
   

     
    public String getInstitutionName(String key, String locale) {
        if(locale.equals("en")) {
            return key.equals("nrm") ? NRM_EN : GNM_EN;
        } else {
            return key.equals("nrm") ? NRM_SV : GNM_SV;
        }
    }
    

     
    

    

    

    

    
    public String getMonth(int numMonth, String locale, boolean isMin) {
         
        switch (numMonth) {
            case 1:  return locale.equals("sv") ? JAN_SV : JAN; 
            case 2:  return locale.equals("sv") ? FEB_SV : FEB; 
            case 3:  return locale.equals("sv") ? MAR_SV : MAR; 
            case 4:  return locale.equals("sv") ? APR_SV : APR; 
            case 5:  return locale.equals("sv") ? MAY_SV : MAY; 
            case 6:  return locale.equals("sv") ? JUN_SV : JUN; 
            case 7:  return locale.equals("sv") ? JUL_SV : JUL; 
            case 8:  return locale.equals("sv") ? AUG_SV : AUG; 
            case 9:  return locale.equals("sv") ? SEP_SV : SEP; 
            case 10: return locale.equals("sv") ? OCT_SV : OCT; 
            case 11: return locale.equals("sv") ? NOV_SV : NOV; 
            case 12: return locale.equals("sv") ? DEC_SV : DEC; 
            default: return isMin ? locale.equals("sv") ? JAN_SV : JAN : locale.equals("sv") ? DEC_SV : DEC; 
        }
    }
    
    public List<String> getMonths(String locale) {
        
        return locale.equals("en") ? months_en : months_sv;
    }
    
    
    
    public String getString(String key, String locale) { 
        string_map_key = new StringJoiner("_");
        string_map_key.add(key).add(locale); 
        
        return stringMap.get(string_map_key.toString());
    }
}
