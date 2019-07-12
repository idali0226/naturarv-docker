package se.nrm.dina.web.portal.mailhandler;
 
import se.nrm.dina.web.portal.beans.ErrorReportBean;
import se.nrm.dina.web.portal.model.SolrRecord;

/**
 *
 * @author idali
 */
public class ErropReportEmail {
     
    private static final String NORMAL_FONT = "#000000;";
    private static final String RED_FONT = "red;";

    public String createErrorReport(String desc, SolrRecord data, ErrorReportBean errorbean) {
         
        StringBuilder sb = new StringBuilder();

        if (data != null) {
            sb.append("<div style=\"background-color: #AAD11C; border: 2px solid #000000;overflow: auto; padding: 10px 0; text-align: center; width: 800px;  \">");
            sb.append("<div style=\"font-size: 1.2em;  font-weight: bold; \">Felrapport</div>");

            sb.append("<div style=\"background-color: ivory; border-top: 1px solid #000000; float: left; font-weight: normal; margin-top: 10px; padding: 10px 0 0 0; text-align: left; \">");
            sb.append("<div style=\" border-bottom: 1px solid #000000; float: left; margin-bottom: 10px; width: 800px;\">");

            sb.append("<div style=\"float: left;  width: 800px;\">");
            sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width: 15%;\">Reg. nr.: </div>");
            sb.append("<div style=\"display: inline; color: ");
            sb.append(getFontColor(errorbean.isCatalogNumber()));
            sb.append(" \">");
            sb.append(errorbean.isCatalogNumber() ? " * " : "");
            sb.append(data.getCatalogNum());
            sb.append("</div>");
            sb.append("</div>");

            sb.append("<div style=\"float: left;  width: 800px;\">");
            sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width:15%;\">Artnamn: </div>");
            sb.append("<div style=\"display: inline; color: ");
            sb.append(getFontColor(errorbean.isSpecimenName()));
            sb.append(" \">");
            sb.append(errorbean.isSpecimenName() ? " * " : "");
            sb.append(data.getFullname() != null ? data.getFullname() : "");
            sb.append("</div>");
            sb.append("</div>");

            sb.append("<div style=\"float: left;  width: 800px;\">");
            sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width: 15%;\">Typinformation: </div>");
            sb.append("<div style=\"display: inline; color: ");
            sb.append(getFontColor(errorbean.isTypeinfo()));
            sb.append(" \">");
            sb.append(errorbean.isTypeinfo() ? " * " : "");
            sb.append(data.getType() != null ? data.getType() : "");
            sb.append("</div>");
            sb.append("</div>");

            sb.append("<div style=\"float: left;  width: 800px;\">");
            sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width: 15%;\">Family: </div>");
            sb.append("<div style=\"display: inline; color: ");
            sb.append(getFontColor(errorbean.isFamily()));
            sb.append(" \">");
            sb.append(errorbean.isFamily() ? " * " : "");
            sb.append(data.getHighTaxon() != null ? data.getHighTaxon() : "");
            sb.append("</div>");
            sb.append("</div>");

            sb.append("<div style=\"float: left;  width: 800px;\">");
            sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width: 15%;\">Insamlare: </div>");
            sb.append("<div style=\"display: inline; color: ");
            sb.append(getFontColor(errorbean.isCollector()));
            sb.append(" \">");
            sb.append(errorbean.isCollector() ? " * " : "");
            sb.append(data.getCollector() != null ? data.getCollector() : "");
            sb.append("</div>");
            sb.append("</div>");

            sb.append("<div style=\"float: left;  width: 800px;\">");
            sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width: 15%;\">Datum: </div>");
            sb.append("<div style=\"display: inline; color: ");
            sb.append(getFontColor(errorbean.isDate()));
            sb.append(" \">");
            sb.append(errorbean.isDate() ? " * " : "");
            sb.append(data.getStartDate() != null ? data.getStartDate() : "");
            sb.append("</div>");
            sb.append("</div>");

            sb.append("<div style=\"float: left;  width: 800px;\">");
            sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width: 15%;\">Lokal: </div>");
            sb.append("<div style=\"display: inline; color: ");
            sb.append(getFontColor(errorbean.isLocality()));
            sb.append(" \">");
            sb.append(errorbean.isLocality() ? " * " : "");
            sb.append(data.getLocality() != null ? data.getLocality() : "");
            sb.append("</div>");
            sb.append("</div>");

            sb.append("<div style=\"float: left;  width: 800px;\">");
            sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width: 15%;\">Kontinent: </div>");
            sb.append("<div style=\"display: inline; color: ");
            sb.append(getFontColor(errorbean.isContinent()));
            sb.append(" \">");
            sb.append(errorbean.isContinent() ? " * " : "");
            sb.append(data.getContinent() != null ? data.getContinent() : "");
            sb.append("</div>");
            sb.append("</div>");

            sb.append("<div style=\"float: left;  width: 800px;\">");
            sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width: 15%;\">Land: </div>");
            sb.append("<div style=\"display: inline; color: ");
            sb.append(getFontColor(errorbean.isCountry()));
            sb.append(" \">");
            sb.append(errorbean.isCountry() ? " * " : "");
            sb.append(data.getCountry() != null? data.getCountry() : "");
            sb.append("</div>");
            sb.append("</div>");

            sb.append("<div style=\"float: left;  width: 800px;\">");
            sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width: 15%;\">Koordinat: </div>");
            sb.append("<div style=\"display: inline; color: ");
            sb.append(getFontColor(errorbean.isCoordinate())); 
            sb.append(" \">");
            sb.append(errorbean.isCoordinate() ? " * " : "");
            if (data.getCoordinate() == 0) {
                sb.append(data.getLat());
                sb.append(" -- ");
                sb.append(data.getLnt());
            } else if (data.getCoordinate() == 1) {
                sb.append(data.getLat());
            } else if (data.getCoordinate() == 2) {
                sb.append(data.getLnt());
            }

            sb.append("</div>");
            sb.append("</div>");

            sb.append("<div style=\"float: left;  width: 800px;\">");
            sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width: 15%;\">&Ouml;vrig etikettinfo: </div>");
            sb.append("<div style=\"display: inline; color: ");
            sb.append(getFontColor(errorbean.isOtherinfo()));
            sb.append(" \">");
            sb.append(errorbean.isOtherinfo() ? " * " : "");
            sb.append(data.getRemarkList() != null ? data.getRemarkList() : "");
            sb.append("</div>");
            sb.append("</div>");

            sb.append("<div style=\"float: left;  width: 800px;\">");
            sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width: 15%;\">Best&auml;mningar: </div>");
            sb.append("<div style=\"display: inline; color: ");
            sb.append(getFontColor(errorbean.isDeterminater()));  
            sb.append(" \">");
            sb.append(errorbean.isDeterminater() ? " * " : "");
            sb.append(data.getDeterminer() != null ? data.getDeterminer() : ""); 
            sb.append("</div>");
            sb.append("</div>");

            sb.append("</div>");
            
            sb.append("<div style=\"display: block; float: left; font-size: 1em; font-weight: bold; padding: 0 15px 15px 10px; width: 700px;\">Beskrivning: </div>");
            sb.append("<div style=\"float: left; width: 700px;\">");
            sb.append("<div style=\"float: left; width: 700px; padding-left: 10px; padding-bottom: 10px; \">");
            sb.append(desc);
            sb.append("</div>");
            sb.append("</div>");
            
            if(errorbean.getReportorsEmail() != null && !errorbean.getReportorsEmail().isEmpty()) {
                
                sb.append("<div style=\"float: left;  width: 800px;\">");
                sb.append("<br />");
                sb.append("<br />");
                sb.append("<div style=\"display: inline; float: left; font-size: 1em; font-weight: bold;  padding: 0 15px 5px 10px; width: 15%;\">Rapporterat av: </div>");
                sb.append("<div style=\"display: inline;  \">");  
                sb.append(errorbean.getReportorsEmail());
                sb.append("</div>");
                sb.append("</div>");
                sb.append("</div>");
                
                
                
                
                
                
//                sb.append("<div style=\"display: block; float: left; font-size: 1em; font-weight: bold; padding: 0 15px 15px 10px; width: 700px;\">Rapporterat av: </div>");
//                sb.append("<div style=\"float: left; width: 700px;\">");
//                sb.append("<div style=\"float: left; width: 700px; padding-left: 10px; padding-bottom: 10px; \">");
//                sb.append(errorbean.getReportorsEmail());
//                sb.append("</div>");
//                sb.append("</div>");
            }
            
            
            sb.append("</div>");
            
            
            
            
            sb.append("</div>");
        }
        return sb.toString();
    }

    private String getFontColor(boolean isError) {
        return (isError) ? RED_FONT : NORMAL_FONT;
    }
}
