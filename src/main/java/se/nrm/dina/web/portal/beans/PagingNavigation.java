package se.nrm.dina.web.portal.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped; 
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import se.nrm.dina.web.portal.model.SolrResult;

/**
 *
 * @author idali
 */
@SessionScoped
@Named
public class PagingNavigation implements Serializable {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private int currentPage = 1;
    private int totalPages = 1; 
    
    private int totalFound = 0;
    private int start = 0; 
    private int end = 0;
    
    private int resultViewStart = 1;
    
    private List<Integer> pages = new ArrayList<>(); 
    private boolean previousDisable = false;
    private boolean nextDisable = false; 

    
    public PagingNavigation() {
        
    }
    
    public void resetdata() {
        currentPage = 1;
        totalFound = 0;
        totalPages = 1;
        start = 0;
        end = 0;
        previousDisable = false;
        nextDisable = false;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }
 

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
    
    
    
    

    public int getTotalFound() {
        return totalFound;
    }

    public void setTotalFound(int totalFound) {
        this.totalFound = totalFound;
    }
    
    
    
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
   
    public boolean isNextDisable() {
        return nextDisable;
    }

    public void setNextDisable(boolean nextDisable) {
        this.nextDisable = nextDisable;
    } 

    public boolean isPreviousDisable() {
        return previousDisable;
    }

    public void setPreviousDisable(boolean previousDisable) {
        this.previousDisable = previousDisable;
    }
 
    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getResultViewStart() {
        return resultViewStart;
    }

    public void setResultViewStart(int resultViewStart) {
        this.resultViewStart = resultViewStart;
    }
    
    public void resetCurrentPage(SolrResult result, int numPerPage) {
        start = result.getStart();
        if(start == 0) {
            currentPage = 1;
        } else {
            currentPage = (start + numPerPage) / numPerPage;
        } 
    }
     
    
    
    public int resetPage(SolrResult result, int numPerPage, int total) {
        
        logger.info("resetPage :  total : {}, start : {}", total, result.getStart());
           
        totalFound = total;
        
        if (result != null) {  
            start = result.getStart(); 
            if(start == 0) {
                currentPage = 1;
            }   
            
            end = start + numPerPage; 
            if(end > totalFound) {
                end = totalFound;
            }
            setNavigationButtons(numPerPage);
            resultViewStart = start + 1; 
        }  else {
            totalFound = 0; 
            pages = new ArrayList<>();
            currentPage = 0;
            start = 0; 
            end = 0;
            resultViewStart = 0;
        }   
        return currentPage;
    }
    
    public void setPreviousPage(int numDisplay) {  
        
        if(currentPage > 1) { 
            start -= numDisplay;   
            currentPage--; 
        } 
    }
    
    public void setNextPage(int numDisplay) {
        
        logger.info("num per page : {}", numDisplay ); 
        if(currentPage < totalPages) { 
            start += numDisplay;  
            currentPage++; 
        }  
    }
    
    public void setFirstPage() {
        start = 0;
        setCurrentPage(1);
    }
    
    public void setLastPage(int numDisplay) { 
        
        start = numDisplay * (totalPages - 1);  
        setCurrentPage(totalPages);   
    }
    
    public void setNavigationButtons(int numPerPage) {
        
        logger.info("setNavigationButtons : {}", numPerPage);
         
        boolean onePage = true;
        
        pages = new ArrayList<>(); 
        setPreviousDisable(false); 
        setNextDisable(false); 
         
        if(totalFound > numPerPage) {
            totalPages = totalFound / numPerPage; 
            
            if(totalPages > 0) {
                if(totalFound % numPerPage > 0) {
                    totalPages++;
                } 
            } 
            onePage = false;
               
            if(totalPages > 10) {
                setPageNum(totalPages);
            } else {  
                for(int i = 1; i <= totalPages; i++) {
                    pages.add(i);
                }  
            }
        } else {   
            if(totalFound > 0) {
                setCurrentPage(1);
                setTotalPages(1); 
                pages = new ArrayList<>();
                pages.add(1);
            }
        }
        setPageNavigationButtons(onePage);  
    }
    
    
    
    private void setPageNavigationButtons(boolean onePage) {
        
        logger.info("currentpage : {} -- start {}", currentPage, start);
        
        if(onePage) { 
            setPreviousDisable(true); 
            setNextDisable(true); 
        } else {
            if(currentPage == 1) { 
                setPreviousDisable(true); 
            } else if(currentPage == totalPages) {  
                setNextDisable(true); 
            } 
        }
    }
        
    private void setPageNum(int totalPages) {
         
        if (currentPage <= 6) {
            pages = new ArrayList<>();
            for(int i= 1; i <= 10; i++) {
                pages.add(i);
            }
        } else { 
            if(totalPages - currentPage > 4) {
                for (int i = 5; i >= 0; i--) {
                    int count = currentPage - i;
                    pages.add(count);
                }

                if (currentPage < totalPages) {
                    int count = currentPage;
                    while (count < totalPages && pages.size() < 10) {
                        count++;
                        pages.add(count);
                    }
                }
            } else {
                int remainder = totalPages - 9;
                for (int i = remainder; i <= currentPage; i++) {
                    pages.add(i);
                }

                if (currentPage <= totalPages) {
                    int count = currentPage;
                    while (count < totalPages && pages.size() < 10) {
                        count++;
                        pages.add(count);
                    }
                }
            } 
        } 
    }  
}
