package com.fastbase.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swapnil.Patel on 30-11-2017.
 */

public class Visitor {
    String Date;
    String ClientImg;
    String Referre;
    String PageViews;
    String TimeSpent;

    List<VisitedPages> visitedPagesList = new ArrayList<>();

    public Visitor(String date, String clientImg, String referre, String pageViews, String timeSpent) {
        Date = date;
        ClientImg = clientImg;
        Referre = referre;
        PageViews = pageViews;
        TimeSpent = timeSpent;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getClientImg() {
        return ClientImg;
    }

    public void setClientImg(String clientImg) {
        ClientImg = clientImg;
    }

    public String getReferre() {
        return Referre;
    }

    public void setReferre(String referre) {
        Referre = referre;
    }

    public String getPageViews() {
        return PageViews;
    }

    public void setPageViews(String pageViews) {
        PageViews = pageViews;
    }

    public String getTimeSpent() {
        return TimeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        TimeSpent = timeSpent;
    }

    public List<VisitedPages> getVisitedPagesList() {
        return visitedPagesList;
    }

    public void setVisitedPagesList(List<VisitedPages> visitedPagesList) {
        this.visitedPagesList = visitedPagesList;
    }

    @Override
    public String toString() {
        return "Visitor{" +
                "Date='" + Date + '\'' +
                ", ClientImg='" + ClientImg + '\'' +
                ", Referre='" + Referre + '\'' +
                ", PageViews='" + PageViews + '\'' +
                ", TimeSpent='" + TimeSpent + '\'' +
                ", visitedPagesList=" + visitedPagesList +
                '}';
    }
}
