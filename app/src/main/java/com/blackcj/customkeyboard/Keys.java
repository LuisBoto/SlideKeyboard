package com.blackcj.customkeyboard;

public enum Keys {

    TOP_LEFT(" ", " ", " "," ", "1"),
    TOP_CENTER("a","b","c","ç","2"),
    TOP_RIGHT("d","e","f"," ","3"),
    CENTER_LEFT("g", "h", "i", " ", "4"),
    CENTER_CENTER("j","k","l"," ", "5"),
    CENTER_RIGHT("m","n","o","ñ","6"),
    BOTTOM_LEFT("p","q","r","s","7"),
    BOTTOM_CENTER("t","u","v"," ","8"),
    BOTTOM_RIGHT("w","x","y","z","9"),
    SPECIAL_LEFT("-","/","_","@","*"),
    SPECIAL_CENTER(".","!",",","?","0"),
    SPECIAL_RIGHT(" "," "," "," "," ");

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
