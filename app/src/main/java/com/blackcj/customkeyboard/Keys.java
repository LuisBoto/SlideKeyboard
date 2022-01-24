package com.blackcj.customkeyboard;

public enum Keys {

    TOP_LEFT(" ", " ", " "," ", "1"),
    TOP_CENTER("a","b","c","d","2");

    private String west, north, east, south, backSymbol;

    private Keys(String west, String north, String east, String south, String backSymbol) {
        this(west, north, east, south);
        this.backSymbol = backSymbol;
    }

    private Keys(String west, String north, String east, String south) {
        this.west = west;
        this.north = north;
        this.east = east;
        this.south = south;
        this.backSymbol = "";
    }

    public String getWest() {
        return this.west;
    }

    public String getNorth() {
        return this.north;
    }

    public String getEast() {
        return this.east;
    }

    public String getSouth() {
        return this.south;
    }

    public String getBackSymbol() {
        return this.backSymbol;
    }
}
