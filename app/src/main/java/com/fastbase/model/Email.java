package com.fastbase.model;

import java.io.Serializable;

/**
 * Created by Swapnil.Patel on 28-12-2017.
 */

public class Email implements Serializable {
    String Name;
    String Email;

    public Email(String name, String email) {
        Name = name;
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @Override
    public String toString() {
        return "Email{" +
                "Name='" + Name + '\'' +
                ", Email='" + Email + '\'' +
                '}';
    }
}
