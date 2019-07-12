/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.web.portal;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.shape.Point;
import com.spatial4j.core.shape.Rectangle;
import com.spatial4j.core.shape.Shape;
import com.spatial4j.core.shape.SpatialRelation;
import com.spatial4j.core.shape.impl.PointImpl;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.regex.Pattern; 
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
//import org.apache.lucene.spatial.prefix.tree.Node;
import org.apache.lucene.spatial.util.ShapeFieldCache;


/**
 *
 * @author idali
 */
public class App {

    public static void main(String[] args) {
 
        List<String> views = new ArrayList();
        
        views.add("ttt1");
        views.add("ttt");
        views.add("ttt");
        views.add("/dorsal/ddsfasdf");
        views.add("ttt");
        views.add("ttt");
        
        String s = views.stream()
                .filter(v -> StringUtils.containsIgnoreCase(v, "dorsal"))
                .findAny().orElse(views.get(0)); 
        
        System.out.println("s : " + s);
        
        
        
//        int i = 0;
//        int j = 0;
//        System.out.println("i : " + (i += 1) + " --- " + (j =+ 1) );
//        
//        System.out.println("i : " + (i += 1) + " --- " + (j =+ 1) );
//        
//        System.out.println("i : " + (i += 1) + " --- " + (i =+ 1) );
//        
//        TreeSet<Integer> set = new TreeSet<>();
//        set.add(125);
//        set.add(3);
//        set.add(18);
//        set.add(689);
//        set.add(12);
//        set.add(12);
//        
//        
//        System.out.println("set : " + set);
//        System.out.println("first : " + set.first());
//        System.out.println("lasl : " + set.last());
//        
//        for(Integer ig : set) {
//            System.out.println("ig : " + ig);
//        }
//        
//        System.out.println("set.headSet(element).size() : " + set.headSet(18).size());
        
        
        
        
//        Calendar c = Calendar.getInstance();
//        c.setTime(new Date());
//        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
//        
//        System.out.println("day : " + dayOfWeek);
//        
//        
//        String[] strs = new String[] {"a", "b", "c"};
//        System.out.println("strs : " + StringUtils.join(strs, ", "));
//        
//        
//        String replaceTest = "(sdf(sa-[erwer, eraw)er-rer[w, sd]fsf-e)";
//        System.out.println(replaceTest);
//        
//        String sddee = replaceTest.replaceAll("[\\[\\](),]", " ");  
//        
//        System.out.println("sddee : " + sddee);
//        System.out.println("sddee : " + sddee.trim());
//        
//        
//        String replacedString = StringUtils.replaceEach(replaceTest, new String[]{"-", ","}, new String[]{" ", " "});
//        System.out.println(replacedString);
//        
//        String[] ss = StringUtils.split(replacedString, " ");
//        for(String ssss : ss) {
//            System.out.println("eee : " + ssss);
//        }
//        
//        
//        
//        
//        
//        Pattern ptn = Pattern.compile("(?=.*?dorsal)(?=.*?adult)(?=.*?female).*", Pattern.CASE_INSENSITIVE); // you can also use (?i) at the beginning
//        System.out.println(ptn.matcher("1003343/male/adult//Dorsal/Dorsal").matches()); // true
    }
}
