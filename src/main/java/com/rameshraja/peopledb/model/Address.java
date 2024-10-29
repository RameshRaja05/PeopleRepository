package com.rameshraja.peopledb.model;

import com.rameshraja.peopledb.annotation.ID;

import java.util.Objects;

public final class Address {
    @ID
    private Long id;

    private String streetAddress;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String county;
    private Region region;

    public Address(Long id, String streetAddress, String address2, String city, String state, String postalCode, String country,
                   String county, Region region) {
        this(streetAddress,address2,city,state,postalCode,country,county,region);
        this.setId(id);
    }

    public Address(String streetAddress, String address2, String city, String state, String postalCode, String country,
                   String county, Region region) {
        this.streetAddress = streetAddress;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.county = county;
        this.region = region;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Address) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.streetAddress, that.streetAddress) &&
                Objects.equals(this.address2, that.address2) &&
                Objects.equals(this.city, that.city) &&
                Objects.equals(this.state, that.state) &&
                Objects.equals(this.postalCode, that.postalCode) &&
                Objects.equals(this.country, that.country) &&
                Objects.equals(this.county, that.county) &&
                Objects.equals(this.region, that.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, streetAddress, address2, city, state, postalCode, country, county, region);
    }

    @Override
    public String toString() {
        return "Address[" +
                "id=" + id + ", " +
                "streetAddress=" + streetAddress + ", " +
                "address2=" + address2 + ", " +
                "city=" + city + ", " +
                "state=" + state + ", " +
                "postalCode=" + postalCode + ", " +
                "country=" + country + ", " +
                "county=" + county + ", " +
                "region=" + region + ']';
    }

}
