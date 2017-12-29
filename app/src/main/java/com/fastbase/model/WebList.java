package com.fastbase.model;

/**
 * Created by Swapnil.Patel on 17-11-2017.
 */

public class WebList {
    String url;
    String ProfileId;
    String AccountId;

    public WebList() {
    }

    public WebList(String url, String profileId, String accountId) {
        this.url = url;
        ProfileId = profileId;
        AccountId = accountId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfileId() {
        return ProfileId;
    }

    public void setProfileId(String profileId) {
        ProfileId = profileId;
    }

    public String getAccountId() {
        return AccountId;
    }

    public void setAccountId(String accountId) {
        AccountId = accountId;
    }

    @Override
    public String toString() {
        return "WebList{" +
                "url='" + url + '\'' +
                ", ProfileId='" + ProfileId + '\'' +
                ", AccountId='" + AccountId + '\'' +
                '}';
    }
}
