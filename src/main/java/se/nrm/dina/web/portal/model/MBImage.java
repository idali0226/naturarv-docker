/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.web.portal.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author idali
 */
public class MBImage {
    
    
    private final String mbpath = "http://images.morphbank.nrm.se/?id=";
    private final String mbImg = "http://morphbank.nrm.se/?id=";
    private final String imgType ="&imgType=jpg";
    
    private final int mbId;  
    private final List<Integer> mbImgIds;
    
    private List<String> thumbs;
    private List<String> images;
    
    public MBImage(int mbId, List<Integer> mbImgIds) {
        this.mbId = mbId;
        this.mbImgIds = mbImgIds;
    }

    public String getMbpath() {
        return mbpath;
    }

    public String getImgType() {
        return imgType;
    }

    public int getMbId() {
        return mbId;
    }

    public List<Integer> getMbImgIds() {
        return mbImgIds;
    }
 
    public String getThumbByViewId(int imgId) {
        return mbpath + imgId +imgType;
    }

    public List<String> getImages() {
        images = new ArrayList();
        mbImgIds.stream().forEach((i) -> {
            images.add(mbImg + i);
        });
        return images;
    }
 
    public List<String> getThumbs() { 
        thumbs = new ArrayList(); 
        mbImgIds.stream().forEach((i) -> {
            thumbs.add(mbpath + i + imgType);
        });
        return thumbs;
    }
}
