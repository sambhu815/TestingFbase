package com.fastbase.model;

import java.io.Serializable;

/**
 * Created by Swapnil.Patel on 29-11-2017.
 */

public class Contact implements Serializable {
    String Name;
    String Designation;
    String Img;
    String Current;
    String Previous;
    String Education;
    String LinkedInUrl;

    public Contact(String name, String designation, String img, String current, String previous, String education, String linkedInUrl) {
        Name = name;
        Designation = designation;
        Img = img;
        Current = current;
        Previous = previous;
        Education = education;
        LinkedInUrl = linkedInUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }

    public String getCurrent() {
        return Current;
    }

    public void setCurrent(String current) {
        Current = current;
    }

    public String getPrevious() {
        return Previous;
    }

    public void setPrevious(String previous) {
        Previous = previous;
    }

    public String getEducation() {
        return Education;
    }

    public void setEducation(String education) {
        Education = education;
    }

    public String getLinkedInUrl() {
        return LinkedInUrl;
    }

    public void setLinkedInUrl(String linkedInUrl) {
        LinkedInUrl = linkedInUrl;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "Name='" + Name + '\'' +
                ", Designation='" + Designation + '\'' +
                ", Img='" + Img + '\'' +
                ", Current='" + Current + '\'' +
                ", Previous='" + Previous + '\'' +
                ", Education='" + Education + '\'' +
                ", LinkedInUrl='" + LinkedInUrl + '\'' +
                '}';
    }
}
