package com.kierrapalmer.gem.Models;

/**
 * Created by theuh on 4/3/2018.
 */

public class User {
    private String email;
    private String password;
    private String first;
    private String phone;

    public User() {
    }



    public User(String email, String password, String first, String phone) {
        this.email = email;
        this.password = password;
        this.first = first;
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
