/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  javax.json.JsonObject
 *  javax.json.JsonValue
 */
package com.floreantpos.ui.views.payment;

import javax.json.JsonObject;
import javax.json.JsonValue;

public class KalaResponse {
    String mykala_id;
    String first_name;
    String last_name;
    String company_name;
    String address;
    String city;
    String county;
    String state;
    String zip;
    String phone1;
    String phone2;
    String email;
    String web;
    String offer_id;
    String points;
    boolean success;
    String message;
    String coupon;
    String offer;

    public void parse(JsonObject object) {
        this.mykala_id = object.getString("id");
        this.first_name = object.getString("first_name");
        this.last_name = object.getString("last_name");
        this.company_name = object.getString("company_name");
        this.address = object.getString("address");
        this.city = object.getString("city");
        this.county = object.getString("county");
        this.state = object.getString("state");
        this.zip = object.getString("zip");
        this.phone1 = object.getString("phone1");
        this.phone2 = object.getString("phone2");
        this.email = object.getString("email");
        this.web = object.getString("first_name");
        this.offer_id = object.getString("kala_id");
        this.points = object.getString("points");
        this.success = Boolean.valueOf(((JsonValue)object.get((Object)"success")).toString());
        this.message = object.getString("message");
        this.coupon = object.getString("coupon");
        this.offer = object.getString("offer").replaceAll("%", "");
    }

    public String getMykala_id() {
        return this.mykala_id;
    }

    public void setMykala_id(String id) {
        this.mykala_id = id;
    }

    public String getFirst_name() {
        return this.first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return this.last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getCompany_name() {
        return this.company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return this.county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone1() {
        return this.phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return this.phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeb() {
        return this.web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getOffer_id() {
        return this.offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getPoints() {
        return this.points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCoupon() {
        return this.coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getOffer() {
        return this.offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getName() {
        return this.first_name + " " + this.last_name;
    }
}

