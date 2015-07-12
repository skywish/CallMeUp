package com.example.skywish.imtest001.bean;

/**
 * Created by DYY-PC on 2015/7/1.
 */
public class Music {
    private String DURATION;
    private boolean isExist;
    private String TITLE;
    private String DATA;
    public Music(){}
    public String getDURATION(){
        return DURATION;
    }
    public void setDURATION(String DURATION){
        this.DURATION = DURATION;
    }
    public boolean getExist(){
        return isExist;
    }
    public void setExist(boolean isExist){
        this.isExist = isExist;
    }
    public String getTITLE(){
        return TITLE;
    }
    public void setTITLE(String TITLE)
    {
        this.TITLE = TITLE;
    }
    public String getDATA(){
        return DATA;
    }
    public void setDATA(String DATA){
        this.DATA =  DATA;
    }
}
