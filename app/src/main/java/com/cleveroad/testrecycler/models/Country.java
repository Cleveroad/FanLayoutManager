package com.cleveroad.testrecycler.models;

public enum Country {
    ITALY("Italy"),
    USA("United States"),
    ROK("South Korea");

    private final String country;

    Country(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }
}
