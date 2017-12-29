package com.fastbase.model;

/**
 * Created by Swapnil.Patel on 30-11-2017.
 */

public class VisitedPages {
    String Time;
    String Url;

    public VisitedPages(String time, String url) {
        Time = time;
        Url = url;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    @Override
    public String toString() {
        return "VisitedPages{" +
                "Time='" + Time + '\'' +
                ", Url='" + Url + '\'' +
                '}';
    }
}
