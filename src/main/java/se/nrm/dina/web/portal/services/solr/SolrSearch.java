package se.nrm.dina.web.portal.services.solr;

import java.io.Serializable; 
import java.util.ArrayList; 
import java.util.Collections;
import java.util.Date;
import java.util.HashMap; 
import java.util.List;
import java.util.Map;  
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.swing.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer.RemoteSolrException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nrm.dina.common.util.exception.SolrServerConnectionException;
import se.nrm.dina.common.util.HelpClass;
import se.nrm.dina.web.portal.model.BoxData;
import se.nrm.dina.web.portal.model.CollectionData;
import se.nrm.dina.web.portal.model.GeoData;
import se.nrm.dina.web.portal.model.MBImage;
import se.nrm.dina.web.portal.model.MapRecord;
import se.nrm.dina.web.portal.model.MorphBankImage;
import se.nrm.dina.web.portal.model.SolrRecord;
import se.nrm.dina.web.portal.model.SolrResult;
import se.nrm.dina.web.portal.model.StatisticData;
import se.nrm.dina.web.portal.model.StatisticDataInstitution;
import se.nrm.dina.web.portal.util.SortUtil;
import se.nrm.dina.web.portal.util.StringMap;

/**
 *
 * @author idali
 */
@Stateless
public class SolrSearch implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SolrServer solrServer;

    private static final String WILD_SEARCH_TEXT = "*:*";
    private static final String FILTER_TYPE = "tsn:*";
    private static final String FILTER_MAP = "geo:*";
    private static final String FILTER_IMAGE = "mbid:*";
    private static final String NRM_CODE = "nrm";
    private static final String GNM_CODE = "gnm";
    
    private String ftx;
    private String catalogNumber;
    private String collectionCode;
    private String selectedView;
    private String imgMBid;

    private static final int MB_IMG_FATCH_SIZE = 15000;

    private static final String MB_THUMB_PATH = "http://images.morphbank.nrm.se/?id=";
    private static final String MB_IMAGE = "http://morphbank.nrm.se/?id=";
    private final static String MB_IMAGE_TYPE = "&imgType=thumb";
    
    private StringBuilder mbSb;

    private List<String> list;

    private SolrQuery solrQuery;
    private QueryResponse queryResponse;
    private SolrDocumentList documents;

    public SolrSearch() {
        solrServer = new HttpSolrServer(SolrSearchHelper.getInstance().getSolrUrl());
    }

    /**
     * Retrieve all the images morphbank id from solr.
     *
     *
     * @param input
     * @param filterMap
     * @param start
     * @param type
     * @param map
     * @return List<List<>>
     */
    public List<List<MorphBankImage>> getMBImages(String input, Map<String, String> filterMap, int start, boolean type, boolean map) {
 
        solrQuery = new SolrQuery();
        solrQuery.setQuery(input)
                            .addField("mbview")
                            .addField("ftx")
                            .addField("cn")
                            .addField("cln");

        addSearchFilters(solrQuery, filterMap, type, map, true);                        // add search query into solr 

        solrQuery.setRows(MB_IMG_FATCH_SIZE);
        try {  
            List<MorphBankImage> imgList = solrServer.query(solrQuery).getResults()
                                                    .stream()
                                                    .map(this::createNewMBImage)
                                                    .collect(Collectors.toList()); 
            
            return createSubList(imgList); 
        } catch (SolrServerException ex) {
            throw new SolrServerConnectionException(ex);
        } 
    }
 
    public List<List<MorphBankImage>> getMBImagesWithOption(String input, Map<String, String> filterMap,
                                                            List<String> views, boolean type, boolean map) {
        logger.info("getMBImagesWithOption : {} -- {}", input, views);
          
        solrQuery = new SolrQuery();
        solrQuery.setQuery(input)
                    .addField("mbview")
                    .addField("ftx")
                    .addField("cn")
                    .addField("cln");
        addSearchFilters(solrQuery, filterMap, type, map, true);
        solrQuery.setRows(MB_IMG_FATCH_SIZE);
        
        List<MorphBankImage> imgList = new ArrayList<>();
        try {  
            solrServer.query(solrQuery).getResults()
                    .stream()
                    .forEach(d -> {
                        ((List<String>) d.getFieldValue("mbview")).stream()
                        .filter(v -> views.stream()
                                .anyMatch(x -> StringUtils.containsIgnoreCase(v, x)))
                        .forEach(v -> {
                            imgList.add(createNewMBImage(d, v));
                        });
                    });
            return createSubList(imgList);
        } catch (SolrServerException ex) {
            throw new SolrServerConnectionException(ex);
        }  
    }
    
    private List<List<MorphBankImage>> createSubList(List<MorphBankImage> imgList) {

        List<List<MorphBankImage>> mbids = new ArrayList<>();

        final int sizeOfList = imgList.size();
        final int breakApart = 15;
        for (int i = 0; i < sizeOfList; i += breakApart) {
            mbids.add(new ArrayList<>(
                    imgList.subList(i, Math.min(sizeOfList, i + breakApart)))
            );
        }
        return mbids;
    } 
    
    /**
     * getImagesByCatalogNumber retrieves all the images belong to the given
     * catalognumber
     *
     * @param catalogNumber
     * @param collectionCode
     * @return mbimages
     */
    public MBImage getImagesByCatalogNumberAndCollectionCode(String catalogNumber, String collectionCode) {

        logger.info("getImagesByCatalogNumberAndCollectionCode : {}", catalogNumber);

        StringBuilder sb = new StringBuilder();
        sb.append("+cn:");
        sb.append(catalogNumber);
        sb.append(" +cln:");
        sb.append(collectionCode);

        try {
            solrQuery = new SolrQuery();
            solrQuery.setQuery(sb.toString()).addField("img").addField("mbid");

            solrQuery.setRows(20);                                                          // take first 20 images 
            documents = solrServer.query(solrQuery).getResults();

            String mbid;  
            if (documents.getNumFound() > 0) {
                SolrDocument document = documents.get(0);
                mbid = (String) document.getFieldValue("mbid");
                
                List<Integer> imgIds = ((List<String>) document.getFieldValue("img"))
                                                                    .stream()
                                                                    .map(Integer::parseInt)
                                                                    .collect(Collectors.toList());
                return new MBImage(Integer.parseInt(mbid), imgIds);             
            }
        } catch (SolrServerException ex) {
            throw new SolrServerConnectionException(ex);
        }
        return null;
    }

    /**
     * Solr search with group data for given field
     *
     * @param groupField
     * @return List<> - a list of group
     */
    public List<String> getGroupData(String groupField) {
        logger.info("getGroupData : {} ", groupField);

        solrQuery = new SolrQuery();

        solrQuery.setQuery("*");
        solrQuery.setStart(0);
        solrQuery.setParam("group", true);
        solrQuery.setParam("group.field", groupField);
        solrQuery.setRows(100);
 
        try { 
            NamedList respNL = solrServer.query(solrQuery).getResponse();
            NamedList groupInfo = (NamedList) respNL.get("grouped");
            NamedList thisGroupInfo = (NamedList) groupInfo.get(groupField);
 
            return ((List<Object>) thisGroupInfo.get("groups")).stream()
                                        .map(o -> ((NamedList) o).getAll("groupValue"))
                                        .map(g -> (String) g.get(0))
                                        .collect(Collectors.toList()); 
        } catch (SolrServerException ex) {
            logger.warn(ex.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Search for collectionobjects created in last year. Facet by create date
     * of month.
     *
     * @param strDate - the date to start
     * @param code - collection code
     * @return Map<String, Integer> - key = Number of month in String. Value =
     * total of collectionobjects created
     */
    public Map<String, Integer> getLastYearRegistedDataForCollection(String strDate, String code) {
        logger.info("getLastYearRegistedData : {} -- {}", strDate, code);

        StringBuilder sb = new StringBuilder();
        sb.append("ctd:[");
        sb.append(strDate);
        sb.append(" TO *]");

        String searchText = WILD_SEARCH_TEXT;
        if (code != null) {
            searchText = "cln:" + code;
        }
        try {
            solrQuery = new SolrQuery();
            solrQuery.setQuery(searchText)
                    .setFilterQueries(sb.toString())
                    .setFacet(true)
                    .addFacetField("ctdmt")
                    .setFacetMinCount(1)
                    .setFacetLimit(100);
            solrQuery.setStart(0);
            solrQuery.setRows(30);
 
            FacetField field = solrServer.query(solrQuery).getFacetField("ctdmt");

            List<Count> counts = field.getValues();
            return counts.stream()
                    .collect(Collectors.toMap(
                                    m -> m.getName(),
                                    m -> (int) m.getCount()
                            )); 
        } catch (SolrServerException ex) {
            logger.warn(ex.getMessage());
            return null;
        }
    }

    /**
     * getLastTenYearsRegistedData - get registed data from last ten years.
     *
     * @param fromYear - the start year
     * @param code - collection code
     * @return Map<String, Integer>
     */
    public Map<String, Integer> getLastTenYearsRegistedData(int fromYear, String code) {
        logger.info("getLastTenYearsRegistedData : {}", fromYear);

        String searchText = WILD_SEARCH_TEXT;
        if (code != null) {
            searchText = "cln:" + code;
        }
          
        try {
            solrQuery = new SolrQuery();
            solrQuery.setQuery(searchText).setFacet(true).addFacetField("ctdyr").setFacetMinCount(1).setFacetLimit(1000);
            solrQuery.setStart(0);
            solrQuery.setRows(100);
            solrQuery.addSort("ctdyr", SolrQuery.ORDER.asc);
 
            queryResponse = solrServer.query(solrQuery); 
            
            FacetField field = queryResponse.getFacetField("ctdyr");

            List<Count> counts = field.getValues();
            counts.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));

            Map<String, Integer> map = new HashMap<>();
            Accumulator accumulator = new Accumulator();
            counts.stream()
                    .forEach(x -> {
                        accumulator.add(x.getCount());
                        if (Integer.parseInt(x.getName()) >= fromYear) {
                            map.put(x.getName(), (int) accumulator.total);
                        }
                    });
            return map;
        } catch (SolrServerException ex) {
            logger.warn(ex.getMessage()); 
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        }
        return null;
    }
     

    private CollectionData buildCollectionData(NamedList d) {
        SolrDocumentList sdl = (SolrDocumentList) d.get("doclist");
        SolrDocument document = sdl.get(0);
        String code = (String) d.getAll("groupValue").get(0);
        int totalDocsInThisGroup = (int) sdl.getNumFound();  
        return new CollectionData(code, (String) document.getFieldValue("clnm"), totalDocsInThisGroup);
    }

    
    /**
     *
     * @param text
     * @param locale
     * @param filterQueries
     * @param type
     * @param map
     * @param image
     * @return : StatisticData
     */
    public StatisticData getStatisticData(String text, String locale, Map<String, String> filterQueries,
                                                                    boolean type, boolean map, boolean image) {
 
        logger.info("getStatisticData : {} -- {}", text, filterQueries);
        List<CollectionData> collectionList = new ArrayList<>();
        List<StatisticDataInstitution> institutions = new ArrayList<>();
  
        solrQuery = new SolrQuery();
        solrQuery.setQuery(text)
                .setFacet(true)
                .addFacetField("tsn")
                .addFacetField("cy")
                .addFacetField("dna")
                .addFacetField("map")
                .addFacetField("mbid")
                .setFacetMinCount(1)
                .setFacetLimit(10000);

        solrQuery.setStart(0);
        solrQuery.setParam("group", true);
        solrQuery.setParam("group.field", "cln");                                               // group by collection
        try {
            solrQuery.addSort("ctd", SolrQuery.ORDER.desc);   
        } catch (Exception e) {
            // This is solr error.  Some th
        }
                                              // sort by cataloged date

        addSearchFilters(solrQuery, filterQueries, type, map, image);

        String nrm = StringMap.getInstance().getInstitutionName(NRM_CODE, locale);
        String gnm = StringMap.getInstance().getInstitutionName(GNM_CODE, locale);

        Map<String, Integer> dataMap = new HashMap<>();
        solrQuery.setRows(1000);
        try { 
            queryResponse = solrServer.query(solrQuery); 
             
            NamedList thisGroupInfo= (NamedList)((NamedList)((NamedList)queryResponse.getResponse()).get("grouped")).get("cln"); 
            int totalNumberOfUngroupedDocuments = ((Number) thisGroupInfo.get("matches")).intValue();                        // all data 
            List<Object> groupData = (List<Object>) thisGroupInfo.get("groups");
            
            Accumulator accumulator = new Accumulator();
            groupData.stream()
                    .map(d -> (NamedList)d)
                    .map(this::buildCollectionData)
                    .forEach(m -> {
                        collectionList.add(m);
                        String institution = StringMap.getInstance().getInstitution(m.getCode());
                        if (institution.equals(NRM_CODE)) {
                            accumulator.addNrmTotal(m.getTotal()); 
                        } else if (institution.contains(GNM_CODE)) {
                            accumulator.addGnmTotal(m.getTotal()); 
                        }
                    }); 
            institutions.add(new StatisticDataInstitution(nrm, NRM_CODE, (int)accumulator.nrmTotal, null));
            institutions.add(new StatisticDataInstitution(gnm, GNM_CODE, (int)accumulator.gnmTotal, null));

            Collections.sort(collectionList, new SortUtil(SortOrder.DESCENDING));

            addData(queryResponse, dataMap, "tsn", "type");
            addData(queryResponse, dataMap, "mbid", "image");
            addData(queryResponse, dataMap, "dna", "dna");
            addData(queryResponse, dataMap, "map", "map");
 
            FacetField country = queryResponse.getFacetField("cy");
            int swedenCount = country.getValues().stream()
                                    .filter(c -> c.getName().equals("sweden"))
                                    .mapToInt(c -> (int)c.getCount())
                                    .sum(); 
            dataMap.put("sweden", swedenCount);

            return new StatisticData(totalNumberOfUngroupedDocuments, collectionList, institutions, dataMap);  
        } catch (RemoteSolrException ex) { 
            return new StatisticData(0, collectionList, institutions, dataMap);
        } catch (SolrServerException ex) {
            throw new SolrServerConnectionException(ex);
        }
    }

    private void addData(QueryResponse queryResponse, Map<String, Integer> dataMap, String field, String key) {
    
        FacetField facetField = queryResponse.getFacetField(field);
      
        int total = facetField.getValues().stream()
                .mapToInt(c -> (int)c.getCount())
                .sum();
        dataMap.put(key, total); 
    }

    private void createCollectionsQuery(String string) {
        StringBuilder sb = new StringBuilder();
        sb.append("cln: (");
        List<String> collections = StringMap.getInstance().getInstitutionCollections(string);
        if (!collections.isEmpty()) {
            collections
                    .stream()
                    .forEach(t -> {
                        sb.append(t);
                        sb.append(" ");
                    });
            sb.append(")");

            if (sb.toString().isEmpty()) {
                solrQuery.addFilterQuery(sb.toString().trim());
            }
        }
    }  

    /**
     * addSearchFilters
     *
     * @param solrQuery
     * @param filterQueries
     * @param type
     * @param map
     * @param image
     */
    private void addSearchFilters(SolrQuery solrQuery, Map<String, String> filterQueries, boolean type, boolean map, boolean image) {

        
        if (filterQueries != null && !filterQueries.isEmpty()) {                                                // add filters into search
            filterQueries.entrySet()
                    .stream()    
                    .forEach(x -> { 
                        if (x.getKey().equals("institution")) {
                            createCollectionsQuery(x.getValue());
                        } else {
                            solrQuery.addFilterQuery(x.getKey().trim() + x.getValue().trim()); 
                        }
                    });
        }
        if (type) {                                                                                             // filter with type
            solrQuery.addFilterQuery(FILTER_TYPE);
        }
        if (map) {                                                                                              // filter with map
            solrQuery.addFilterQuery(FILTER_MAP);
        }
        if (image) {
            solrQuery.addFilterQuery(FILTER_IMAGE);
        }
    }
     

    /**
     * SimpleSearch : Full text search
     *
     * @param input : Search text
     * @param start : Start result
     * @param numFetch : Number of results to fetch
     * @param type : Filter for documents have type object
     * @param map : Filter for documents have coordinates
     * @param image : Filter for documents have images
     * @param sort : Sort order
     * @return SolrResult
     */
    public SolrResult simpleSearch(String input, int start, int numFetch, boolean type, boolean map, boolean image, String sort) {

        logger.info("simpleSearch : {} -- {}", input, start);

        solrQuery = new SolrQuery();
        solrQuery.setQuery(input);

        try {
            if (sort != null) {
                if (sort.equals("score")) {
                    solrQuery.addSort("score", SolrQuery.ORDER.desc);
                } else {
                    solrQuery.addSort(sort, SolrQuery.ORDER.asc);
                }
            } 
            addSearchFilters(solrQuery, null, type, map, image);

            solrQuery.setRows(numFetch);
            solrQuery.setStart(start); 
            queryResponse = solrServer.query(solrQuery); 
            long numFound = queryResponse.getResults().getNumFound();

            if (queryResponse.getResults().getNumFound() > 0) {
                List<SolrRecord> resultList = queryResponse.getBeans(SolrRecord.class);
                return new SolrResult(numFound, start, resultList);
            } else {
                return null;
            }
        } catch (SolrServerException ex) {
            throw new SolrServerConnectionException(ex);
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        }
        return null;
    }
    
     
    /**
     * Search images by MorphBank id
     * @param mbid
     * @return 
     */
    public SolrRecord searchByMBid(String mbid) {
        solrQuery = new SolrQuery();
        solrQuery.setQuery("mbid:" + mbid);

        try {
            queryResponse = solrServer.query(solrQuery);  

            if (queryResponse.getResults().getNumFound() > 0) {
                return queryResponse.getBeans(SolrRecord.class).get(0); 
            } else {
                return null;
            }
        } catch (SolrServerException ex) {
            logger.warn(ex.getMessage());
            return null;
        }
    }

    /**
     *
     * @param input : Search text
     * @param filterMap
     * @param start : Start result
     * @param numFetch : Number of results to fetch
     * @param type : Filter for documents have type object
     * @param map : Filter for documents have coordinates
     * @param image : Filter for documents have images
     * @param sort
     * @return SolrResult
     */
    public SolrResult searchWithQueryFilter(String input, Map<String, String> filterMap, int start, int numFetch,
            boolean type, boolean map, boolean image, String sort) {
        logger.info("searchWithQueryFilter : {} -- {}", input, filterMap + " -- " + sort);

        solrQuery = new SolrQuery();
        solrQuery.setQuery(input);
 
        try {
            if (sort != null) {
                if (sort.equals("score")) {
                    solrQuery.addSort("score", SolrQuery.ORDER.desc);
                } else {
                    solrQuery.addSort(sort, SolrQuery.ORDER.asc);
                }  
            }
        } catch(Exception e) {
            // Do nothing. 
        }
             
        try {     
            addSearchFilters(solrQuery, filterMap, type, map, image);

            if (numFetch > 0) {
                solrQuery.setRows(numFetch);
            }
            solrQuery.setStart(start);

            queryResponse = solrServer.query(solrQuery);
            long numFound = queryResponse.getResults().getNumFound();

            if (numFound > 0) {
                List<SolrRecord> resultList = queryResponse.getBeans(SolrRecord.class);
                return new SolrResult(numFound, start, resultList);
            }
        } catch (SolrServerException ex) {
            logger.warn(ex.getMessage());
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    /**
     * auto completion
     *
     * @param input
     * @param content
     * @return  *
     */
    public List<String> autoCompleteTaxon(String input, String content) {

        if (content.equals("exact")) {
            content = "startswith";
        }
        String searchText = SolrSearchHelper.getInstance().buildString(input, "ftx", content);

        list = new ArrayList<>();
        try {
            String text = searchText;
            solrQuery = new SolrQuery();
            solrQuery.setQuery(text);
            solrQuery.setParam("group", true).setParam("group.field", "ftx");

            solrQuery.setStart(0);
            solrQuery.setRows(5000);
            queryResponse = solrServer.query(solrQuery);
 
            NamedList thisGroupInfo = (NamedList)((NamedList) (queryResponse.getResponse()).get("grouped")).get("ftx");  
            ((List<Object>) thisGroupInfo.get("groups")).stream()
                    .map(o ->  ((SolrDocumentList) ((NamedList) o).get("doclist")).get(0))  
                    .forEach(d -> {  
                        if(d.getFieldValue("sym") != null) {
                            list = ((ArrayList<String>) d.getFieldValue("sym")).stream()
                                    .filter(s -> s.toLowerCase().contains(input.toLowerCase().trim()))
                                    .collect(Collectors.toList());
                        } 
                        list.add((String) d.getFieldValue("ftx")) ;  
                    });   
        } catch (SolrServerException ex) {
            logger.warn(ex.getMessage());
        }
        return HelpClass.sortUniqueValues(list);
    }
 

    public List<String> autoCompleteUseFacet(String value, String content, String fieldname) {
        list = new ArrayList<>();

        if (content.equals("exact")) {
            content = "startswith";
        }
        String searchText = SolrSearchHelper.getInstance().buildString(value, fieldname, content);
        try {
            solrQuery = new SolrQuery();
            solrQuery.setQuery(searchText);
            solrQuery.setFacet(true).addFacetField(fieldname).setFacetLimit(10000).setFacetMinCount(1);
 
            FacetField field = solrServer.query(solrQuery).getFacetField(fieldname);

            List<Count> counts = field.getValues(); 
            list = counts.stream()
                    .map(c -> c.getName())
                    .filter(c -> !list.contains(c))
                    .collect(Collectors.toList()); 
            return list;
        } catch (SolrServerException ex) {
            throw new SolrServerConnectionException(ex);
        }
    }

    /**
     *
     * @param input
     * @param field
     * @param content
     * @return
     */
    public List<String> autoCompleteSearch(String input, String field, String content) {
//        logger.info("autoCompleteSearch : {} -- {}", input, field);

        if (content.equals("exact")) {
            content = "startswith";
        }
        String searchText = SolrSearchHelper.getInstance().buildString(input, field, content);
        try {
            solrQuery = new SolrQuery();
            solrQuery.setQuery(searchText).addField(field);
            solrQuery.setRows(1000);

            queryResponse = solrServer.query(solrQuery);

            list = new ArrayList<>();
            
            if(field.equals("auth") || field.equals("cm")) {
                 
                list = queryResponse.getResults().stream()
                        .map(d -> ((List<String>) d.getFieldValue(field)).toString())  
//                        .filter(c -> !list.contains(c))
                        .distinct()
                        .collect(Collectors.toList());
            } else {
                list = queryResponse.getResults().stream()
                        .map(d -> (String) d.getFieldValue(field))
//                        .filter(c -> !list.contains(c))
                        .distinct()
                        .collect(Collectors.toList());
            } 
            return list; 
        } catch (SolrServerException ex) {
            throw new SolrServerConnectionException(ex);
        }
    }
     

    /**
     * autoCompleteSearchAllField - auto complete search from default field
     *
     * @param input
     * @param content
     * @return
     */
    public List<String> autoCompleteSearchAllField(String input, String content) {
//        logger.info("autoCompleteSearchAllField : {}", input);

        list = new ArrayList<>();

        if (content.equals("exact")) {
            content = "startswith";
        }

        String searchText = SolrSearchHelper.getInstance().buildAdvanceFullTextSearchText(input, content);
        try {
            solrQuery = new SolrQuery();

            solrQuery.setQuery(searchText);
            solrQuery.setHighlight(true).setHighlightSnippets(1);                                                                   //set other params as needed
            solrQuery.setParam("hl.fl", "ac");
            solrQuery.setRows(3000);
 
            queryResponse = solrServer.query(solrQuery);
            documents = queryResponse.getResults();
            documents.stream()
                    .map(d -> (String) d.getFieldValue("id"))
                        .forEach(id -> {
                            List<String> highlightSnippets = queryResponse.getHighlighting().get(id).get("ac");
                            if(highlightSnippets != null) {
                                list = highlightSnippets.stream()
                                            .map(s -> StringUtils.substringBetween(s, "<em>", "</em>").trim())
                                            .distinct() 
                                            .collect(Collectors.toList());
                            } 
                        });
            
            return HelpClass.sortUniqueValues(list); 
        } catch (SolrServerException ex) {
            throw new SolrServerConnectionException(ex);
        }
    }
 

    public GeoData spatialSearchAllData(String searchText, Map<String, String> filter, boolean type, boolean image, GeoData geoData) {
//        logger.info("spatialSearchAllData: {}", filter);

        solrQuery = new SolrQuery();
        solrQuery.setQuery(searchText);

        addSearchFilters(solrQuery, filter, type, true, image);
        solrQuery.setStart(0);
        solrQuery.setRows(5000);

        try {
            String query = geoData.buildSearchBox();
            solrQuery.addFilterQuery(query);

            queryResponse = solrServer.query(solrQuery); 
            geoData.setMapRecords(queryResponse.getBeans(MapRecord.class)); 
        } catch (SolrServerException ex) {
            logger.warn(ex.getMessage());
        }
        return geoData;
    }
 
 
    private BoxData buildBoxData(SolrDocumentList documentList) {

        SolrDocument document = documentList.get(0);
        String geopoint = (String) document.getFieldValue("geo");
        double lat = (Double) document.getFieldValue("lat1");
        double lng = (Double) document.getFieldValue("lnt1");

        int total = (int) documentList.getNumFound();
        MapRecord mapRecord = new MapRecord();
        mapRecord.setLat1(lat);
        mapRecord.setLnt1(lng);
        mapRecord.setGeopoint(geopoint);

        if (total == 1) {
            mapRecord.setId((String) document.getFieldValue("id"));
            mapRecord.setCatalogNum((String) document.getFieldValue("cn"));
            mapRecord.setFullname((String) document.getFieldValue("ftx"));
            mapRecord.setLat((String) document.getFieldValue("lat"));
            mapRecord.setLnt((String) document.getFieldValue("lnt"));
            mapRecord.setLocality((String) document.getFieldValue("lc"));
            mapRecord.setStartDate((Date) document.getFieldValue("sd"));
        }  
         
        return new BoxData(total, lat, lng, mapRecord);
    }

    public Map<String, Object> geoSpatialGroupSearch(String searchText, Map<String, String> filter,
                                                        boolean type, boolean image, GeoData geoData) {
        logger.info("geoSpatialGroupSearch : {} -- {}", searchText, filter);

        Map<String, Object> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append("+(");
        sb.append(searchText);
        sb.append(") ");
        sb.append("+geo:[");
        sb.append(geoData.getMinLat());
        sb.append(",");
        sb.append(geoData.getMinLng());
        sb.append(" TO ");
        sb.append(geoData.getMaxLat());
        sb.append(",");
        sb.append(geoData.getMaxLng());
        sb.append("]");

        solrQuery = new SolrQuery();
        solrQuery.setQuery(sb.toString().trim());
        addSearchFilters(solrQuery, filter, type, true, image);
        solrQuery.setParam("group", true);
        solrQuery.setParam("group.field", "geopoint");
        solrQuery.setStart(0);
        solrQuery.setRows(5000);
  
        try {
            queryResponse = solrServer.query(solrQuery);
  
            NamedList thisGroupInfo = (NamedList) ((NamedList) queryResponse.getResponse().get("grouped")).get("geopoint");
            Number totalUngrouped = (Number) thisGroupInfo.get("matches");

            List<Object> groupData = (List<Object>) thisGroupInfo.get("groups");
            if (groupData.size() > 0) { 
                if (totalUngrouped.intValue() <= 500) {  
                    GeoData geo = spatialSearchAllData(searchText, filter, type, image, geoData);
                    map.put("resultList", geo.getMapRecords());
                    if(groupData.size() == 1) {
                        map.put("resultType", "singleGroup");
                    } else {
                        map.put("resultType", "smallDataSet");
                    } 
                    return map; 
                } else {
                    List<BoxData> boxDataList = new ArrayList();
                    MinMaxCoordinates minMaxCoordintes = new MinMaxCoordinates();
                    groupData.stream()
                            .map(o -> (NamedList) o)
                            .map(n -> (SolrDocumentList) n.get("doclist"))
                            .map(this::buildBoxData)
                            .forEach(b -> {
                                boxDataList.add(b);
                                minMaxCoordintes.compareLat(b.getLat());
                                minMaxCoordintes.compareLng(b.getLng());  
                            });
                             
                    map.put("minlat", minMaxCoordintes.minlat);
                    map.put("minlng", minMaxCoordintes.minlng);
                    map.put("maxlat", minMaxCoordintes.maxlat);
                    map.put("maxlng", minMaxCoordintes.maxlng);
                    map.put("resultList", boxDataList);
                    
                    map.put("resultType", "multiGroup");
                    return map;
                } 
            }
        } catch (SolrServerException ex) {
            logger.warn(ex.getMessage());
        }
        return null;
    }
     

    public SolrRecord searchByCatalogNumber(String catNum) {

        try {
            solrQuery = new SolrQuery();
            solrQuery.setQuery("*");

            solrQuery.addFilterQuery("cn:*" + catNum + "*");
            solrQuery.setRows(5);

            queryResponse = solrServer.query(solrQuery);
            return queryResponse.getBeans(SolrRecord.class).get(0);
        } catch (SolrServerException ex) {
            logger.warn(ex.getMessage());
            return null;
        }
    }
    
    
    Predicate<String> pDorsal = v -> StringUtils.containsIgnoreCase(v, "dorsal");

    private String selectView(List<String> views) { 
        return views.stream()
                .filter(pDorsal)
                .findAny()
                .orElse(views.get(0));           
    }
     
    private MorphBankImage createNewMBImage(SolrDocument document) {
        ftx = (String) document.getFieldValue("ftx");
        catalogNumber = (String) document.getFieldValue("cn");
        collectionCode = (String) document.getFieldValue("cln");
        selectedView = selectView((List<String>) document.getFieldValue("mbview"));
        imgMBid = StringUtils.split(selectedView, "/")[0];
        
        mbSb = new StringBuilder();
        mbSb.append(MB_THUMB_PATH);
        mbSb.append(imgMBid);
        mbSb.append(MB_IMAGE_TYPE);
        return new MorphBankImage(Integer.parseInt(imgMBid), MB_IMAGE + imgMBid, collectionCode,
                                    catalogNumber, ftx, selectedView, mbSb.toString());
    }
    
    private MorphBankImage createNewMBImage(SolrDocument document, String view) {
        ftx = (String) document.getFieldValue("ftx");
        catalogNumber = (String) document.getFieldValue("cn");
        collectionCode = (String) document.getFieldValue("cln"); 
        imgMBid = StringUtils.split(view, "/")[0];
        
        mbSb = new StringBuilder();
        mbSb.append(MB_THUMB_PATH);
        mbSb.append(imgMBid);
        mbSb.append(MB_IMAGE_TYPE);
        return new MorphBankImage(Integer.parseInt(imgMBid), MB_IMAGE + imgMBid, collectionCode,
                                    catalogNumber, ftx, selectedView, mbSb.toString());
    }
    

    @FunctionalInterface
    public interface WorkerInterface {

        public void doSomeWork();
    }

    public static void execute(WorkerInterface worker) {
        worker.doSomeWork();
    }
    
    
    /**
     * Accumulator class to help accumulator long in foreach statement
     */
    public static class MinMaxCoordinates {

        double minlat = 90.0;
        double minlng = 180.0;
        double maxlat = -90.0;
        double maxlng = -180.0;

        public void compareLat(double lat) {
            minlat = minlat > lat ? lat : minlat;  
            maxlat = maxlat < lat ? lat : maxlat;
        }
 
        public void compareLng(double lng) {
            minlng = minlng > lng ? lng : minlng;
            maxlng = maxlng < lng ? lng : maxlng;
        }  
    }

    /**
     * Accumulator class to help accumulator long in foreach statement
     */
    public static class Accumulator {

        private long total = 0;
        
        private long nrmTotal = 0;
        private long gnmTotal = 0;

        public void add(long value) {
            total += value;
        }
        
        public void addNrmTotal(long value) {
            nrmTotal += value;
        }
        
        public void addGnmTotal(long value) {
            gnmTotal += value;
        }
    } 
}
