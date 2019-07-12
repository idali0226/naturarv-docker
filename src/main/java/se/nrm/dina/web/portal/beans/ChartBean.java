package se.nrm.dina.web.portal.beans;

import java.io.Serializable;
import java.time.LocalDate; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner; 
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils; 
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.web.portal.services.solr.SolrSearch;
import se.nrm.dina.web.portal.util.DinaPortalHelper;
import se.nrm.dina.web.portal.util.MonthElement;
import se.nrm.dina.web.portal.util.StringMap;

/**
 *
 * @author idali
 */
@Named("chart")
@SessionScoped
public class ChartBean implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HttpSession session;

    private BarChartModel totalMonthModel;                                              // month chart
    private BarChartModel totalTenYearsModel;                                           // year chart

    private final Map<String, BarChartModel> collectionMonthsModelMap;                  // collections month chart
    private final Map<String, BarChartModel> collectionYearsModelMap;                   // collections year chart

    private List<String> collections;
    private int lastTenYear;
    private String strLastYearDate;
    private String locale;
 
    private int year;
    private int lastYear;
    private int month;
    private LocalDate lastYearDate;
    private boolean isSwedish;
     

    @Inject
    private SolrSearch solrSearch;

    public ChartBean() {
        totalMonthModel = new BarChartModel();
        totalTenYearsModel = new BarChartModel();
        collectionMonthsModelMap = new HashMap();
        collectionYearsModelMap = new HashMap<>();
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    }

    @PostConstruct
    public void init() {

        logger.info("init");

        initDate();
        locale = (String) session.getAttribute("locale");
        if (locale == null) {
            locale = "sv";
        }
        isSwedish = locale.equals("sv");

        if (collections == null || collections.isEmpty()) {
            collections = solrSearch.getGroupData("cln");                               // get collection codes from solr search
        }

        if (collectionMonthsModelMap == null || collectionMonthsModelMap.isEmpty()) {
            createModelForLastYearForCollections();
        }

        if (collectionYearsModelMap == null || collectionYearsModelMap.isEmpty()) {
            createModelForLastTenYearsForCollections();
        }

        if (totalMonthModel.getSeries() == null || totalMonthModel.getSeries().isEmpty()) { 
            createTotalMonthModel();
        }

        if (totalTenYearsModel.getSeries() == null || totalTenYearsModel.getSeries().isEmpty()) {
            createSpecimenModelForLastTenYears();
        } 
    }

    /**
     * Initiate date now
     */
    private void initDate() {
        
        logger.info("initDate");
        LocalDate date = LocalDate.now();
        year = date.getYear();
        lastYear = year - 1;
        month = date.getMonthValue();

        lastYearDate = LocalDate.of(lastYear, month, date.getDayOfMonth()); 
        
        strLastYearDate = DinaPortalHelper.localDateToStringWithTime(lastYearDate); 
        lastTenYear = year - 11;
    }
    
    private String buildLabel(MonthElement element) {
        
        StringJoiner sj = new StringJoiner(" ");
        String monName = StringMap.getInstance().getMonthName(element, isSwedish);
        int monNum = element.getMonthNumber();
        sj.add(monName);
        if(monNum > month) {
            sj.add(String.valueOf(lastYear)); 
        } else {
            sj.add(String.valueOf(year));
        }
        return sj.toString();
    }

    
    private void buildChartSeries(String collectionCode) {
        ChartSeries series = new ChartSeries();
        Map<String, Integer> monthMap = solrSearch.getLastYearRegistedDataForCollection(strLastYearDate, collectionCode);
       
        logger.info("month map : {}", monthMap); 
        IntStream.range(1, 13)
                .forEach(x -> { 
                    int m = lastYearDate.plusMonths(x).getMonthValue(); 
                    int total = monthMap.containsKey(String.valueOf(m)) ? monthMap.get(String.valueOf(m)) : 0;

                    series.set(buildLabel(MonthElement.getName(m)), total);
                });
 
        BarChartModel model = new BarChartModel();
        model.addSeries(series);

        model.setTitle(StringMap.getInstance().getMonthChartTitle(locale));
        model.setShowPointLabels(true);
        model.setShowDatatip(false);

        Axis axis = model.getAxis(AxisType.X);
        axis.setTickAngle(-50);
        axis.setLabel(StringMap.getInstance().getMonthChartAxis(locale));

        Axis yaxis = model.getAxis(AxisType.Y);
        yaxis.setLabel(StringMap.getInstance().getChartYaxis(locale));
        collectionMonthsModelMap.put(collectionCode, model);  
    }

    /**
     * To create chart model for last 12 months data for each collection
     */
    private void createModelForLastYearForCollections() { 
        collections.stream().forEach(this::buildChartSeries); 
    }

    /**
     * Create chart model for last ten years data for each collection
     */
    private void createModelForLastTenYearsForCollections() {  
        collections.stream().forEach(this::buildLastTenYearsForCollection); 
    }
    
    
    /**
     * Create chart model for all collection data for last ten years
     */
    private void buildLastTenYearsForCollection(String collection) {

        ChartSeries series = new ChartSeries();
 
        Map<String, Integer> yearMap = solrSearch.getLastTenYearsRegistedData(lastTenYear, collection);
  
        IntStream.range(lastTenYear, lastTenYear + 12) 
                            .forEach(i -> {      
                                series.set(String.valueOf(i), getCount(yearMap, String.valueOf(i))); 
                });
        BarChartModel model = new BarChartModel();
        model.addSeries(series);

        model.setTitle(StringMap.getInstance().getYearChartTitle(locale));
        model.setShowPointLabels(true);
        model.setShowDatatip(false);

        Axis axis = model.getAxis(AxisType.X);
        axis.setTickAngle(-50);
        axis.setLabel(StringMap.getInstance().getYearChartAxis(locale));

        Axis yaxis = model.getAxis(AxisType.Y);
        yaxis.setLabel(StringMap.getInstance().getChartYaxis(locale));

        collectionYearsModelMap.put(collection, model);
    }



    /**
     * Create chart model for all collection data for last 12 months
     */
    private void createTotalMonthModel() {
       
        Map<String, Integer> monthMap = solrSearch.getLastYearRegistedDataForCollection(strLastYearDate, null); 

        ChartSeries series = new ChartSeries();

        IntStream.range(1, 13)
                .forEach(x -> { 
                    int m = lastYearDate.plusMonths(x).getMonthValue(); 
                    int total = monthMap.containsKey(String.valueOf(m)) ? monthMap.get(String.valueOf(m)) : 0;

                    series.set(buildLabel(MonthElement.getName(m)), total);
                });
 
        totalMonthModel = new BarChartModel();
        totalMonthModel.addSeries(series);
        totalMonthModel.setTitle(StringMap.getInstance().getMonthChartTitle(locale));
        totalMonthModel.setShowPointLabels(true);
        totalMonthModel.setShowDatatip(false);

        Axis axis = totalMonthModel.getAxis(AxisType.X);
        axis.setTickAngle(-50);
        axis.setLabel(StringMap.getInstance().getMonthChartAxis(locale));

        Axis yaxis = totalMonthModel.getAxis(AxisType.Y);
        yaxis.setLabel(StringMap.getInstance().getChartYaxis(locale));
    }
    
    private int getCount(Map<String, Integer> map, String key) {
        return map.containsKey(key) ? map.get(key) : 0;
    }


    /**
     * Create chart model for all collection data for last ten years
     */
    private void createSpecimenModelForLastTenYears() {

        ChartSeries series = new ChartSeries();
 
        Map<String, Integer> yearMap = solrSearch.getLastTenYearsRegistedData(lastTenYear, null);
  
        IntStream.range(lastTenYear, lastTenYear + 12) 
                            .forEach(i -> {      
                                series.set(String.valueOf(i), getCount(yearMap, String.valueOf(i))); 
                            }); 
        totalTenYearsModel = new BarChartModel();
        totalTenYearsModel.addSeries(series);

        totalTenYearsModel.setTitle(StringMap.getInstance().getYearChartTitle(locale));
        totalTenYearsModel.setShowPointLabels(true);
        totalTenYearsModel.setShowDatatip(false);

        Axis axis = totalTenYearsModel.getAxis(AxisType.X);
        axis.setTickAngle(-50);
        axis.setLabel(StringMap.getInstance().getYearChartAxis(locale));

        Axis yaxis = totalTenYearsModel.getAxis(AxisType.Y);
        yaxis.setLabel(StringMap.getInstance().getChartYaxis(locale));
    }
  
    /**
     * changeLanguage - change chart labels
     *
     * @param locale
     */
    public void changeLanguage(String locale) {

        logger.info("changeLanguage : {}", locale);

        this.locale = locale;
        session.setAttribute("locale", locale);

        changeLocaleForCollectionsChart();
        changeLocaleFoeCollectionsTenYearsChart();
        totalMonthModel = changeSeriesLable(totalMonthModel);
        totalTenYearsModel = changeTenYearsSeriesLable(totalTenYearsModel);
    }

    private void changeLocaleFoeCollectionsTenYearsChart() {

        BarChartModel model;
        if (collectionYearsModelMap != null && !collectionYearsModelMap.isEmpty()) {
            for (Map.Entry<String, BarChartModel> entry : collectionYearsModelMap.entrySet()) {
                model = changeTenYearsSeriesLable(entry.getValue());
                collectionYearsModelMap.put(entry.getKey(), model);
            }
        } else {
            createModelForLastYearForCollections();
        }
    }

    private void changeLocaleForCollectionsChart() {

        BarChartModel model;
        if (collectionMonthsModelMap != null && !collectionMonthsModelMap.isEmpty()) {
            for (Map.Entry<String, BarChartModel> entry : collectionMonthsModelMap.entrySet()) {
                model = changeSeriesLable(entry.getValue());
                collectionMonthsModelMap.put(entry.getKey(), model);
            }
        } else {
            createModelForLastYearForCollections();
        }
    }

    private BarChartModel changeTenYearsSeriesLable(BarChartModel model) {

        logger.info("changeTenYearsSeriesLable : {}", model);

        model.setTitle(StringMap.getInstance().getYearChartTitle(locale));
        Axis axis = model.getAxis(AxisType.X);
        axis.setLabel(StringMap.getInstance().getYearChartAxis(locale));

        Axis yaxis = model.getAxis(AxisType.Y);
        yaxis.setLabel(StringMap.getInstance().getChartYaxis(locale));

        return model;
    }

    private BarChartModel changeSeriesLable(BarChartModel model) {

        logger.info("changeSeriesLable : {}", model);

        ChartSeries series = model.getSeries().get(0);
        ChartSeries newSeries = new ChartSeries();

        series.getData().entrySet().stream()
                .forEach(x -> {
                    String key = (String) x.getKey();
                    String mon = StringUtils.split(key, " ")[0];
                    String y = StringUtils.split(key, " ")[1];
                    int total = 0;
                    if (x.getValue() != null) {
                        total = (Integer) x.getValue();
                    }
                    newSeries.set(StringMap.getInstance().languageMonthMatch(mon, isSwedish) + " " + y, total);
                });

        model.clear();
        model.addSeries(newSeries);

        model.setTitle(StringMap.getInstance().getMonthChartTitle(locale));
        Axis axis = model.getAxis(AxisType.X);
        axis.setTickAngle(-50);
        axis.setLabel(StringMap.getInstance().getMonthChartAxis(locale));

        Axis yaxis = model.getAxis(AxisType.Y);
        yaxis.setLabel(StringMap.getInstance().getChartYaxis(locale));

        return model;
    }

    public BarChartModel createMonChartForCollection(String collection) {

        logger.info("createMonChartForCollection : {} - {}", collection, collectionMonthsModelMap);

        BarChartModel model = collectionMonthsModelMap.get(collection);

        if (model == null) {
            createModelForLastYearForCollections();
            model = collectionMonthsModelMap.get(collection);
        }
        return model;
    }

    public BarChartModel createTenYearsChartForCollection(String collection) {

        BarChartModel model = collectionYearsModelMap.get(collection);

        if (model == null) {
            createModelForLastTenYearsForCollections();
            model = collectionYearsModelMap.get(collection);
        }
        return model;
    }

    public BarChartModel getTotalMonthModel() {
        return totalMonthModel;
    }

    public BarChartModel getTotalTenYearsModel() {
        return totalTenYearsModel;
    } 
}
