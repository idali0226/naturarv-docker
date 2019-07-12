/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.web.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

/**
 *
 * @author idali
 */
public class SolrClientTest {

    private static SolrServer server;
    private static String url = "http://localhost:8983/solr";
//    private static final String url = "http://dina-db.nrm.se:8983/solr";

    public static void main(String[] args) throws SolrServerException {

        server = new HttpSolrServer(url);

//        testSearch();
//        testDate();
//        testDataFromLastYear();
//        testGroup();
//        testGeoSpatial();
//        testGeoSpatialExactPoint();
//        testGeoSpatialFacet();
//        testGeoSpatialGroup();
//        testCollectionFacet();
//        testStatisticData();
//        testSearchAllAutoComplete();
//        testSearchAutoComplete();
//        testEscapeQueryChars();
//        testSymGroup();
//        testSym();
        //       testFaced();
//        testSpatial();
//         testNewData();
//        testFacetWithSum();
        testChartData();
        
        testGroupChatData();

    }

    public static class Accumulator {

        private long total = 0;

        public void add(long value) {
            total += value;
        }
    }

    private static void testChartData() {
        try {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery("ctdyr:0");
            QueryResponse queryResponse1 = server.query(solrQuery);

            SolrDocumentList list1 = queryResponse1.getResults();
            System.out.println("Total found : " + list1.getNumFound());

            solrQuery.setQuery("*:*").setFacet(true).addFacetField("ctdyr").setFacetMinCount(1).setFacetLimit(10000);
            solrQuery.setStart(0);
            solrQuery.setRows(1000);
            solrQuery.addSort("ctdyr", SolrQuery.ORDER.asc);
            
            int fromYear = 2004;
            
            try {
                QueryResponse queryResponse = server.query(solrQuery);
                System.out.println("time : " + queryResponse.getElapsedTime());
                
                SolrDocumentList list = queryResponse.getResults();
                System.out.println("Total found : " + list.getNumFound());
                
                Map<String, Integer> map = new HashMap<>();
                
                FacetField field = queryResponse.getFacetField("ctdyr");
                
                List<Count> counts = field.getValues();
                counts.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));
                
                
                
                Accumulator accumulator = new Accumulator();
                counts.stream()
                        .forEach(x -> {
                            System.out.println("Counts : " + x.getName() + " --- " + x.getCount());
                            accumulator.add(x.getCount());
                            if (Integer.parseInt(x.getName()) >= fromYear) {
                                map.put(x.getName(), (int) accumulator.total);
                            }
                        });
                map.put("totalOld", (int) accumulator.total);
                System.out.println(map);
            } catch (SolrServerException ex) {
                Logger.getLogger(SolrClientTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SolrServerException ex) {
            Logger.getLogger(SolrClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void testGroupChatData() {
     
        int fromYear = 2004;
        try {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery("*:*");
            
            solrQuery.setParam("group", true);
            solrQuery.setParam("group.field", "ctdyr");
            solrQuery.addSort("ctdyr", SolrQuery.ORDER.asc);
            solrQuery.setStart(0);
            solrQuery.setRows(5000);
            
             
            QueryResponse queryResponse = server.query(solrQuery);
            System.out.println("time : " + queryResponse.getElapsedTime());

            NamedList respNL = queryResponse.getResponse();
            NamedList groupInfo = (NamedList) respNL.get("grouped");
            NamedList thisGroupInfo = (NamedList) groupInfo.get("ctdyr");
//            Number totalUngrouped = (Number) thisGroupInfo.get("matches");

//            int totalNumberOfUngroupedDocuments = totalUngrouped.intValue(); 
//            System.out.println("total : " + totalNumberOfUngroupedDocuments);
//            
            List<Object> groupData = (List<Object>) thisGroupInfo.get("groups");
            
            Map<String, Integer> map = new HashMap<>();
            Accumulator accumulator = new Accumulator();
            int total = 0; 
            groupData.stream() 
                    .forEach(g -> {
                        NamedList thisGroup = (NamedList) g;
                        SolrDocumentList sdl = (SolrDocumentList) thisGroup.get("doclist");
                        List groups = thisGroup.getAll("groupValue");
                        
                        accumulator.add((int) sdl.getNumFound());
                
                        
                        
                        if (groups.get(0) == null) {
                            System.out.println("null number : " + (int) sdl.getNumFound());  
                        } else {
                            int geohash = (int) groups.get(0);  
                            if (geohash >= fromYear) {
                                map.put(String.valueOf(geohash), (int) accumulator.total);
                            }   
                            System.out.println("test : " + geohash + " --- " +  (int) sdl.getNumFound()); 
                        } 
                    });
            map.put("totalOld", (int) accumulator.total);
            System.out.println("map : " + map);
//            System.out.println("group : " + groupData.size());
//            
//            

//            for (Object o : groupData) {
//                NamedList thisGroup = (NamedList) o;
//                SolrDocumentList sdl = (SolrDocumentList) thisGroup.get("doclist");
////            SolrDocument document = sdl.get(0);
//                
//                List groups = thisGroup.getAll("groupValue");                           // group value
//                
//                System.out.println("group : " + groups);
//                
//                if(groups.get(0) == null) {
//                    System.out.println("null number : " + (int) sdl.getNumFound());
//                } else {
//                    int geohash = (int) groups.get(0);
//                    int totalDocsInThisGroup = (int) sdl.getNumFound();
//                
//                    System.out.println("test : " + geohash + " --- " + totalDocsInThisGroup);
//                }
//                
//            }
            System.out.println("end");
        } catch (SolrServerException ex) {
            Logger.getLogger(SolrClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private static void testFacetWithSum() {
        try {
            SolrQuery solrQuery = new SolrQuery();

            solrQuery.setQuery("ctdyr:2014").setFacet(true).addFacetField("cln").setFacetMinCount(1).setFacetLimit(1000);
            solrQuery.setStart(0);
            solrQuery.setRows(100);

            QueryResponse queryResponse = server.query(solrQuery);
            System.out.println("time : " + queryResponse.getElapsedTime());
//            SolrDocumentList documents = queryResponse.getResults();

            FacetField field = queryResponse.getFacetField("cln");
            List<Count> counts = field.getValues();

            for (Count count : counts) {

                String ctd = count.getName();
                System.out.println("test : " + ctd + " --- " + count.getCount());

            }
        } catch (SolrServerException ex) {
            Logger.getLogger(SolrClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void testGeoSpatialGroup() throws SolrServerException {
        System.out.println("start");
        SolrQuery solrQuery = new SolrQuery();

        solrQuery.setStart(0);
//        solrQuery.setQuery("geo:[-32.68073527041952,-61.5234375 TO 33.59200173681582,61.5234375]");
        solrQuery.setQuery("geo:[-90,-180 TO 90,180]");
//        solrQuery.setFacet(true).addFacetField("geo").setFacetLimit(5000).setFacetMinCount(1);

        solrQuery.setParam("group", true);
        solrQuery.setParam("group.field", "geopoint");
        solrQuery.setStart(0);
        solrQuery.setRows(5000);

        QueryResponse queryResponse = server.query(solrQuery);
        System.out.println("time : " + queryResponse.getElapsedTime());
//            SolrDocumentList documents = queryResponse.getResults();
//
//            FacetField field = queryResponse.getFacetField("geo");
//             
//            List<Count> counts = field.getValues();
//            Map<String, Integer> map = new HashMap<String, Integer>();
//            for (Count count : counts) {
//                
//                String ctd = count.getName();
//                System.out.println("geo : " + ctd + " --- " + count.getCount());
//            }

        NamedList respNL = queryResponse.getResponse();
        NamedList groupInfo = (NamedList) respNL.get("grouped");
        NamedList thisGroupInfo = (NamedList) groupInfo.get("geopoint");
        Number totalUngrouped = (Number) thisGroupInfo.get("matches");

        int totalNumberOfUngroupedDocuments = totalUngrouped.intValue();

        System.out.println("total : " + totalNumberOfUngroupedDocuments);

        List<Object> groupData = (List<Object>) thisGroupInfo.get("groups");
        System.out.println("group : " + groupData.size());
        for (Object o : groupData) {
            NamedList thisGroup = (NamedList) o;
            SolrDocumentList sdl = (SolrDocumentList) thisGroup.get("doclist");
//            SolrDocument document = sdl.get(0);

            List groups = thisGroup.getAll("groupValue");                           // group value 

            String geohash = (String) groups.get(0);
            int totalDocsInThisGroup = (int) sdl.getNumFound();

//            System.out.println("test : " + geohash + " --- " + totalDocsInThisGroup);
        }
        System.out.println("end");
    }

    private static void testGeoSpatialFacet() throws SolrServerException {
        System.out.println("start");
        SolrQuery solrQuery = new SolrQuery();

        solrQuery.setStart(0);
//        solrQuery.setQuery("geo:[-32.68073527041952,-61.5234375 TO 33.59200173681582,61.5234375]");

        solrQuery.setQuery("+(ftx:*asil* ht:*asil*) +geo:[-90,-180 TO 90,180]");
        solrQuery.setFacet(true).addFacetField("geo").setFacetLimit(5000).setFacetMinCount(1);

//        solrQuery.setParam("group", true);
//        solrQuery.setParam("group.field", "geopoint"); 
//        solrQuery.setStart(0);
//        solrQuery.setRows(5000); 
//        solrQuery.setParam("group", true);
//        solrQuery.setParam("group.field", "geo");
//        solrQuery.setStart(0);
//        solrQuery.setRows(5000);
//        solrQuery.setRows(100);
//        QueryResponse response = server.query(solrQuery);
        QueryResponse queryResponse = server.query(solrQuery);
        System.out.println("time : " + queryResponse.getElapsedTime());
//            SolrDocumentList documents = queryResponse.getResults();

        FacetField field = queryResponse.getFacetField("geo");

        System.out.println("total count : " + field.getValueCount());
        List<Count> counts = field.getValues();
        Map<String, Integer> map = new HashMap<String, Integer>();

        int c = 0;
        for (Count count : counts) {

            String ctd = count.getName();
            System.out.println("geo : " + ctd + " --- " + count.getCount());
            c += count.getCount();
        }

        System.out.println("total amount : " + c);
//            NamedList respNL = queryResponse.getResponse();
//        NamedList groupInfo = (NamedList) respNL.get("grouped");
//        NamedList thisGroupInfo = (NamedList) groupInfo.get("geopoint");
//        Number totalUngrouped = (Number) thisGroupInfo.get("matches");
//         
//        int totalNumberOfUngroupedDocuments = totalUngrouped.intValue();
//         
//        
//        System.out.println("total : " + totalNumberOfUngroupedDocuments);
//  
//        List<Object> groupData = (List<Object>) thisGroupInfo.get("groups");
//        System.out.println("group : " + groupData.size());
//        for (Object o : groupData) {
//            NamedList thisGroup = (NamedList) o;
//            SolrDocumentList sdl = (SolrDocumentList) thisGroup.get("doclist");
////            SolrDocument document = sdl.get(0);
//             
//            List groups = thisGroup.getAll("groupValue");                           // group value 
//
//            String geohash = (String) groups.get(0); 
//            int totalDocsInThisGroup = (int) sdl.getNumFound();
//              
//                System.out.println("test : " + geohash + " --- " + totalDocsInThisGroup);
//            
//        }   
        System.out.println("end");

//        NamedList respNL = response.getResponse();
//        NamedList groupInfo = (NamedList) respNL.get("grouped");
//        NamedList thisGroupInfo = (NamedList) groupInfo.get("geo");
//        Number totalUngrouped = (Number) thisGroupInfo.get("matches");
//        int totalNumberOfUngroupedDocuments = totalUngrouped.intValue();
//         
//        
//        System.out.println("total : " + totalNumberOfUngroupedDocuments);
//  
//        List<Object> groupData = (List<Object>) thisGroupInfo.get("groups");
//        for (Object o : groupData) {
//            NamedList thisGroup = (NamedList) o;
//            SolrDocumentList sdl = (SolrDocumentList) thisGroup.get("doclist");
//            SolrDocument document = sdl.get(0);
//
//            List groups = thisGroup.getAll("groupValue");                           // group value 
//            String code = (String) groups.get(0);
// 
//            int totalDocsInThisGroup = (int) sdl.getNumFound();
//
//            System.out.println("group : " + code + " --- " + totalDocsInThisGroup);
//        }   
        System.out.println("end");

    }

    private static void testGeoSpatialExactPoint() throws SolrServerException {
        SolrQuery solrQuery = new SolrQuery();

        String s = "58.9333,12.2";
        solrQuery.setStart(0);
        solrQuery.setQuery("geo:*");
        solrQuery.addFilterQuery("{!bbox}");
        solrQuery.setParam("sfield", "geo");
        solrQuery.setParam("pt", "58.9333,12.2");
        solrQuery.setParam("d", "0.0001");

        solrQuery.setRows(100);
        QueryResponse response = server.query(solrQuery);
        SolrDocumentList list = response.getResults();

        if (list.getNumFound() > 0) {
            System.out.println("num found 1 : " + list.getNumFound());
        }

        System.out.println("size : " + list.size());
        for (SolrDocument d : list) {
            System.out.println("test: " + d.getFieldValue("geo"));
            String gvalue = (String) d.getFieldValue("geo");
            System.out.println("test 1 : " + gvalue.equals(s));
        }

    }

    private static String getBoxString(int i, int j, String latlng) {

        double rLat = 5;
        double rLng = 5;

        double toLat = i * rLat;
        double startLat = toLat - rLat;

        double toLng = j * rLng;
        double startLng = toLng - rLng;

        StringBuilder sb = new StringBuilder();
        if (latlng.equals("nw")) {
            rLng = rLng * (-1);

            startLng = j * rLng;
            toLng = startLng - rLng;

        } else if (latlng.equals("se")) {
            rLat = (-1) * rLat;

            startLat = i * rLat;
            toLat = startLat - rLat;
        } else if (latlng.equals("sw")) {
            rLng = (-1) * rLng;
            rLat = (-1) * rLat;

            startLng = j * rLng;
            toLng = startLng - rLng;

            startLat = i * rLat;
            toLat = startLat - rLat;
        }

        if (toLat > 90) {
            toLat = 90;
        }

        if (toLng > 180) {
            toLng = 180;
        }
        sb.append("geo:[");
        sb.append(startLat);
        sb.append(",");
        sb.append(startLng);
        sb.append(" TO ");
        sb.append(toLat);
        sb.append(",");
        sb.append(toLng);
        sb.append("]");

        return sb.toString().trim();
    }

    private static void testGeoSpatial() throws SolrServerException {

        SolrQuery solrQuery = new SolrQuery();

        solrQuery.setStart(0);
        solrQuery.setQuery("*:*");

        solrQuery.setRows(1);

        int count = 0;

        for (int i = 1; i <= 18; i++) {
            for (int j = 1; j <= 36; j++) {
                String searchString = getBoxString(i, j, "sw");
//                System.out.println("search String : " + i + " -- " + j);

                solrQuery.setFilterQueries(searchString);
                solrQuery.addFilterQuery("cln:327681");

                QueryResponse response = server.query(solrQuery);
                SolrDocumentList list = response.getResults();

                if (list.getNumFound() > 0) {
                    System.out.println("num found 1 : " + list.getNumFound());
                }

//                searchString = getBoxString(i, j, "nw");
////                System.out.println("search String : " + i + " -- " + j);
//                
//                solrQuery.setFilterQueries(searchString);
//
//                
//                response = server.query(solrQuery);
//                list = response.getResults();
//                
//                if(list.getNumFound() > 0 ) {
//                    System.out.println("num found 2 : " + list.getNumFound());
//                }
//                
//                
//                searchString = getBoxString(i, j, "se");
////                System.out.println("search String : " + i + " -- " + j);
//                
//                solrQuery.setFilterQueries(searchString);
//
//                
//                response = server.query(solrQuery);
//                list = response.getResults();
//                
//                if(list.getNumFound() > 0 ) {
//                    System.out.println("num found 3 : " + list.getNumFound());
//                }
//                
//                searchString = getBoxString(i, j, "sw");
////                System.out.println("search String : " + i + " -- " + j);
//                
//                solrQuery.setFilterQueries(searchString);
//
//                
//                response = server.query(solrQuery);
//                list = response.getResults();
//                
//                if(list.getNumFound() > 0 ) {
//                    System.out.println("num found 4 : " + list.getNumFound());
//                }
//                
                count++;

            }

        }
        System.out.println("count : " + count);

//        for(int i = 0; i < 180; i = i + 5) { 
//            for(int j = 0; j < 90; j = j + 5) {
//                StringBuilder sb = new StringBuilder();
//                lng = i;
//                lat = j;
//
//                sb.append("geo:[");
//                sb.append(lat);
//                sb.append(",");
//                sb.append(lng);
//                sb.append(" TO ");
//                sb.append(lat + 5);
//                sb.append(",");
//                sb.append(lng + 5);
//                sb.append("]");
//                
//                
//                solrQuery.setFilterQueries(sb.toString());
//
//                
//                QueryResponse response = server.query(solrQuery);
//                SolrDocumentList list = response.getResults();
//                
//                if(list.getNumFound() > 0 ) {
//                    System.out.println("num found 1 : " + list.getNumFound());
//                } 
//            } 
//        } 
//        
//        for(int i = -180; i < 0; i = i + 5) {  
//            for(int j = -90; j < 0; j = j + 5) {
//                StringBuilder sb = new StringBuilder();
//                lng = i;
//                lat = j;
//
//                sb.append("geo:[");
//                sb.append(lat);
//                sb.append(",");
//                sb.append(lng);
//                sb.append(" TO ");
//                sb.append(lat + 5);
//                sb.append(",");
//                sb.append(lng + 5);
//                sb.append("]");
//                
//                
//                solrQuery.setFilterQueries(sb.toString());
//                QueryResponse response = server.query(solrQuery);
//                SolrDocumentList list = response.getResults();
//                
//                if(list.getNumFound() > 0 ) {
//                    System.out.println("num found 2 : " + list.getNumFound());
//                } 
//            } 
//        } 
//        
//        for(int i = -180; i < 0; i = i + 5) { 
//            for(int j = 0; j < 90; j = j + 5) {
//                StringBuilder sb = new StringBuilder();
//                lng = i;
//                lat = j;
//
//                sb.append("geo:[");
//                sb.append(lat);
//                sb.append(",");
//                sb.append(lng);
//                sb.append(" TO ");
//                sb.append(lat + 5);
//                sb.append(",");
//                sb.append(lng + 5);
//                sb.append("]");
//                
//                solrQuery.setFilterQueries(sb.toString());
// 
//                QueryResponse response = server.query(solrQuery);
//                SolrDocumentList list = response.getResults();
//                
//                if(list.getNumFound() > 0 ) {
//                    System.out.println("num found 3 : " + list.getNumFound());
//                } 
//            } 
//        } 
//        
//        for(int i = 0; i < 180; i = i + 5) { 
//            for(int j = -90; j < 0; j = j + 5) {
//                StringBuilder sb = new StringBuilder();
//                lng = i;
//                lat = j;
//
//                sb.append("geo:[");
//                sb.append(lat);
//                sb.append(",");
//                sb.append(lng);
//                sb.append(" TO ");
//                sb.append(lat + 5);
//                sb.append(",");
//                sb.append(lng + 5);
//                sb.append("]");
//                
//                solrQuery.setFilterQueries(sb.toString());
// 
//                QueryResponse response = server.query(solrQuery);
//                SolrDocumentList list = response.getResults();
//                
//                if(list.getNumFound() > 0 ) {
//                    System.out.println("num found 4 : " + list.getNumFound());
//                } 
//            } 
//        } 
//        for(SolrDocument d : list) {
//            for (Map.Entry<String, Object> entry : d.entrySet()) {
//                    System.out.println(entry.getKey() + " --- " + entry.getValue());
//
//                }
//                System.out.println("");
//        }
// 
    }

    private static void testGroup() throws SolrServerException {

        SolrQuery solrQuery = new SolrQuery();

        solrQuery.setQuery("cln:262144");
        solrQuery.setStart(0);
        solrQuery.setParam("group", true);
        solrQuery.setParam("group.field", "sfn");

//        solrQuery.setRows(100);
        QueryResponse queryResponse = server.query(solrQuery);
        NamedList respNL = queryResponse.getResponse();
        NamedList groupInfo = (NamedList) respNL.get("grouped");
        NamedList thisGroupInfo = (NamedList) groupInfo.get("sfn");

//        List<String> objs = ((List<Object>) thisGroupInfo.get("groups")).stream()
//                                            .map(o -> ((NamedList) o).getAll("groupValue"))
//                                            .map(g -> (String) g.get(0))
//                                            .collect(Collectors.toList()); 
//        
//        for(Object obj : objs) {
//            System.out.println("obj : " + obj);
//        }
        Number totalUngrouped = (Number) thisGroupInfo.get("matches");
        int totalNumberOfUngroupedDocuments = totalUngrouped.intValue();

        System.out.println("total : " + totalNumberOfUngroupedDocuments);

        List<Object> groupData = (List<Object>) thisGroupInfo.get("groups");
        for (Object o : groupData) {
            NamedList thisGroup = (NamedList) o;
            SolrDocumentList sdl = (SolrDocumentList) thisGroup.get("doclist");
            SolrDocument document = sdl.get(0);

            List groups = thisGroup.getAll("groupValue");                           // group value 
            String code = (String) groups.get(0);

            String t = (String) document.getFieldValue("sfn");
            System.out.println("t : " + t);

//            List<String> name = (List<String>) document.getFieldValue("sym");
            int totalDocsInThisGroup = (int) sdl.getNumFound();

            System.out.println("code : -- name -- total : " + code + " -- " + totalDocsInThisGroup);
        }

    }

    private static void testNewData() {
        try {
            String text = "cln:262144";
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(text);
            solrQuery.addField("clnm");
            solrQuery.addField("cln");
            solrQuery.addFacetField("sfn");
            solrQuery.setParam("group", true);
            solrQuery.setParam("group.field", "sfn");

//            solrQuery.setFacetLimit(100);
            solrQuery.setStart(0);
            solrQuery.setRows(1000);
            QueryResponse queryResponse = server.query(solrQuery);

            NamedList respNL = queryResponse.getResponse();
            NamedList groupInfo = (NamedList) respNL.get("grouped");
            NamedList thisGroupInfo = (NamedList) groupInfo.get("cln");

//            
//            groupData.stream()
//                    .map((o) -> (NamedList) o)
//                    .map((thisGroup) -> (SolrDocumentList) thisGroup.get("doclist"))
//                    .map((sdl) -> sdl.get(0)).forEach((document) -> {
//                System.out.println("code : -- name" + document.getFieldValue("clnm") + " -- " + document.getFieldValue("cln"));
//            });
        } catch (SolrServerException ex) {
        }
    }

    private static void testSpatial() {

        List<String> list = new ArrayList<String>();
        try {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery("*").setFacet(true).addFacetField("geo").addField("geo").addFacetQuery("!geo");
            solrQuery.setStart(0);
            solrQuery.setRows(2);

            QueryResponse queryResponse = server.query(solrQuery);
            SolrDocumentList documents = queryResponse.getResults();

            FacetField field = queryResponse.getFacetField("geo");
            List<Count> counts = field.getValues();
            Map<String, Integer> map = new HashMap<String, Integer>();
            for (Count count : counts) {
                String ctd = count.getName();
                System.out.println("geo : " + ctd + " --- " + count.getCount());
            }

            System.out.println("num found : " + documents.getNumFound());

            for (SolrDocument d : documents) {
                for (Map.Entry<String, Object> entry : d.entrySet()) {
                    System.out.println(entry.getKey() + " --- " + entry.getValue());

                }
                System.out.println("");
            }
            for (String string : list) {
                System.out.println("string : " + string);
            }
        } catch (SolrServerException ex) {
        }
    }

    private static void testFaced() {
        List<String> list = new ArrayList<String>();
        try {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery("*").setFacet(true).addFacetField("cln");
            solrQuery.setStart(0);
            solrQuery.setRows(2000);

            QueryResponse queryResponse = server.query(solrQuery);
            SolrDocumentList documents = queryResponse.getResults();

            System.out.println("num found : " + documents.getNumFound());

            for (SolrDocument d : documents) {
                for (Map.Entry<String, Object> entry : d.entrySet()) {
                    System.out.println(entry.getKey() + " --- " + entry.getValue());

                }
                System.out.println("");
            }
            for (String string : list) {
                System.out.println("string : " + string);
            }
        } catch (SolrServerException ex) {
        }
    }

    private static void testSym() {
        String input = "+sym:*tetrix* +sym:*kiefferi*";
        List<String> list = new ArrayList<String>();
        try {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(input).addField("ftx").addField("ht").addField("sym").addField("cn").addField("cy").addField("ct").addField("lc").addField("cm").addField("clnm").addField("col").addField("dn").addField("sfn");

            solrQuery.setRows(3000);
            QueryResponse queryResponse = server.query(solrQuery);
            SolrDocumentList documents = queryResponse.getResults();

            System.out.println("num found : " + documents.getNumFound());

            for (SolrDocument d : documents) {
                for (Map.Entry<String, Object> entry : d.entrySet()) {
                    System.out.println(entry.getKey() + " --- " + entry.getValue());

                }
                System.out.println("");
            }
            for (String string : list) {
                System.out.println("string : " + string);
            }
        } catch (SolrServerException ex) {
        }
    }

    private static void testSymGroup() {
        List<String> list = new ArrayList<String>();
        try {
            String text = "*:*";// + input.trim().toLowerCase() + "*";
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(text);
            solrQuery.setParam("group", true).setParam("group.field", "ftx");

            solrQuery.setRows(5000);
            QueryResponse queryResponse = server.query(solrQuery);

            NamedList respNL = queryResponse.getResponse();
            NamedList groupInfo = (NamedList) respNL.get("grouped");
            NamedList thisGroupInfo = (NamedList) groupInfo.get("ftx");

            Number totalUngrouped = (Number) thisGroupInfo.get("matches");

            List<Object> groupData = (List<Object>) thisGroupInfo.get("groups");
            for (Object o : groupData) {
                NamedList thisGroup = (NamedList) o;
                SolrDocumentList sdl = (SolrDocumentList) thisGroup.get("doclist");
                SolrDocument document = sdl.get(0);

                List groups = thisGroup.getAll("groupValue");                           // group value 
                String taxon = (String) groups.get(0);

                List<String> names = (ArrayList<String>) document.getFieldValue("sym");

                System.out.println("group : " + taxon);
                if (names != null) {
                    for (String string : names) {
                        System.out.println("sym : " + string);
                    }
                }

            }

        } catch (SolrServerException ex) {
        }
    }

    private static void testSearchAutoComplete() {
        String input = "Kevin";
        List<String> list = new ArrayList<String>();
        try {
            String text = "auth:*";// + input.trim().toLowerCase() + "*";
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(text).addField("ftx").addField("ht").addField("auth").addField("cn").addField("cy").addField("ct").addField("lc").addField("cm").addField("clnm").addField("col").addField("dn").addField("sfn");

            solrQuery.setRows(3000);
            QueryResponse queryResponse = server.query(solrQuery);
            SolrDocumentList documents = queryResponse.getResults();

            for (SolrDocument d : documents) {
                for (Map.Entry<String, Object> entry : d.entrySet()) {
                    System.out.println(entry.getKey() + " --- " + entry.getValue());

                }
                System.out.println("");
            }
            for (String string : list) {
                System.out.println("string : " + string);
            }
        } catch (SolrServerException ex) {
        }
    }

    private static void testBooleanQuery() {

        String input = "Kevin Holston";

        BooleanQuery q = new BooleanQuery();
        String text = "cs:*" + input + "*";
        Query query = new TermQuery(new Term("cs", "*kevin*"));
        q.add(query, Occur.MUST);

    }

    private static void testSearchAllAutoComplete() {
        String input = "Kevin Holston";
        List<String> list = new ArrayList<String>();
        try {
            String text = "cs:*" + input + "*";
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(text);

            solrQuery.setRows(3000);
            QueryResponse queryResponse = server.query(solrQuery);
            SolrDocumentList documents = queryResponse.getResults();

            for (SolrDocument d : documents) {
                for (Map.Entry<String, Object> entry : d.entrySet()) {
                    System.out.println("cs : " + entry.getKey() + entry.getValue());
                    if (!entry.getKey().equals("rm") && !entry.getKey().equals("cs")) {
                        if (entry.getValue().toString().toLowerCase().contains(input.toLowerCase().trim())) {
                            list.add(entry.getValue().toString());
                        }
                    }
                }
            }
            for (String string : list) {
                System.out.println("string : " + string);
            }
        } catch (SolrServerException ex) {
        }
    }

    private static void testStatisticData() throws SolrServerException {

        Map<String, Integer> map = new HashMap<String, Integer>();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*").setFacet(true).addFacetField("tsn").
                addFacetField("cy").addFacetField("dna").addFacetField("map").
                setFacetMinCount(1).setFacetLimit(5000);
        solrQuery.setParam("group", true);
        solrQuery.setParam("group.field", "cln");
        solrQuery.setStart(0);
        solrQuery.setRows(2000);

        QueryResponse queryResponse = server.query(solrQuery);

        NamedList respNL = queryResponse.getResponse();
        NamedList groupInfo = (NamedList) respNL.get("grouped");
        NamedList thisGroupInfo = (NamedList) groupInfo.get("cln");

        Number totalUngrouped = (Number) thisGroupInfo.get("matches");
        int totalNumberOfUngroupedDocuments = totalUngrouped.intValue();

        List<Object> groupData = (List<Object>) thisGroupInfo.get("groups");
        for (Object o : groupData) {
            NamedList thisGroup = (NamedList) o;
            SolrDocumentList sdl = (SolrDocumentList) thisGroup.get("doclist");
            SolrDocument document = sdl.get(0);

            List groups = thisGroup.getAll("groupValue");                           // group value 
            String code = (String) groups.get(0);

            String name = (String) document.getFieldValue("clnm");
            int totalDocsInThisGroup = (int) sdl.getNumFound();

            System.out.println("group : " + code + " --- " + name + " --- " + totalDocsInThisGroup);
        }

        FacetField type = queryResponse.getFacetField("tsn");
        List<FacetField.Count> typeCounts = type.getValues();
        int totalType = 0;
        for (FacetField.Count count : typeCounts) {
            totalType += count.getCount();
        }
        map.put("type", totalType);

        FacetField country = queryResponse.getFacetField("cy");
        List<FacetField.Count> countryCounts = country.getValues();
        for (FacetField.Count count : countryCounts) {
            if (count.getName().equals("sweden")) {
                map.put("sweden", (int) count.getCount());
                break;
            }
        }

        FacetField dna = queryResponse.getFacetField("dna");
        int totalDna = 0;
        List<FacetField.Count> dnaCounts = dna.getValues();
        for (FacetField.Count count : dnaCounts) {
            totalDna += count.getCount();
        }
        map.put("dna", totalDna);

        FacetField mapfield = queryResponse.getFacetField("map");
        List<FacetField.Count> mapCounts = mapfield.getValues();
        int mapCount = 0;
        for (FacetField.Count count : mapCounts) {
            mapCount += count.getCount();
        }
        map.put("map", mapCount);

        System.out.println("map : " + map);
    }

    private static void testEscapeQueryChars() {

        String text = "NHRS-COLE000000435";
        String newText = ClientUtils.escapeQueryChars(text);
        System.out.println("text : " + newText);
    }
}
