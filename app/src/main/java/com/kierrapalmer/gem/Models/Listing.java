package com.kierrapalmer.gem.Models;

/**
 * Created by theuh on 4/6/2018.
 */

public class Listing {
    private String id, userId, photoURL, title, price, category, desc, zip, phone, email;

    public Listing(){
        //Required
    }
    public Listing(String id, String userId, String photoUrl, String title, String price, String category, String desc, String zip, String phone, String email) {
        this.id = id;
        this.userId = userId;
        this.photoURL = photoUrl;
        this.title = title;
        this.price = price;
        this.category = category;
        this.desc = desc;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
