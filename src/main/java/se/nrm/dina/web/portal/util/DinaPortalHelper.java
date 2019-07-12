package se.nrm.dina.web.portal.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author idali
 */
public class DinaPortalHelper {
    
    private static final String DEFAULT_LANGUAGE = "defaultlanguage";
    
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat dft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSS'Z'");
    
    private static final SimpleDateFormat genericDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSS'Z'");
    
    public static String getDefaultLanguage() {
        return PropertyHandler.getInstance().getProperty(DEFAULT_LANGUAGE);
    }
    
    public static String dateToStringWithTime(Date date) {
        if(date == null) {
            return null;
        } else {
            return dft.format(date);
        }
    }

    public static String localDateToStringWithTime(LocalDate date) {

        if (date == null) {
            return null;
        }
        Instant instant = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return dateToStringWithTime(Date.from(instant));
    }
}
