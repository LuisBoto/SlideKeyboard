package com.blackcj.customkeyboard;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public enum Keys {

    TOP_LEFT(' ', ' ', ' ',' ', '1'),
    TOP_CENTER('a','b','c','ç','2'),
    TOP_RIGHT('d','e','f',' ','3'),
    CENTER_LEFT('g', 'h', 'i', ' ', '4'),
    CENTER_CENTER('j','k','l',' ', '5'),
    CENTER_RIGHT('m','n','o','ñ','6'),
    BOTTOM_LEFT('p','q','r','s','7'),
    BOTTOM_CENTER('t','u','v',' ','8'),
    BOTTOM_RIGHT('w','x','y','z','9'),
    SPECIAL_LEFT('-','/','_','@','*'),
    SPECIAL_CENTER('.','!',',','?','0'),
    SPECIAL_RIGHT(' ',' ',' ',' ');

    private Character west, north, east, south, backSymbol;

    private Keys(Character west, Character north, Character east, Character south, Character backSymbol) {
        this(west, north, east, south);
        this.backSymbol = backSymbol;
    }

    private Keys(Character west, Character north, Character east, Character south) {
        this.west = west;
        this.north = north;
        this.east = east;
        this.south = south;
        this.backSymbol = ' ';
    }

    public Character getWest() {
        return this.west;
    }

    public Character getNorth() {
        return this.north;
    }

    public Character getEast() {
        return this.east;
    }

    public Character getSouth() {
        return this.south;
    }

    public Character getBackSymbol() {
        return this.backSymbol;
    }

    private static final int DIRECTION_UP = 0;
    private static final int DIRECTION_LEFT = 1;
    private static final int DIRECTION_RIGHT = 2;
    private static final int DIRECTION_DOWN = 3;
    private static final int NO_DIRECTION = 4;

    public int getCodeFor(int direction) {
        switch (direction) {
            case DIRECTION_UP:
                return this.getNorth();
            case DIRECTION_DOWN:
                return this.getSouth();
            case DIRECTION_LEFT:
                return this.getWest();
            case DIRECTION_RIGHT:
                return this.getEast();
        }
        return this.getBackSymbol();
    }

    public static int determineSwipedDirection(MotionEvent event1, MotionEvent event2, double[] displayDimensions) {
        // Event 2 is last in time
        double thresholdY = displayDimensions[0] * 0.01;
        double thresholdX = displayDimensions[1] * 0.01;
        float initialX = event1.getX();
        float initialY = event1.getY();
        float finalX = event2.getX();
        float finalY = event2.getY();
        float deltaX = Math.abs(finalX - initialX);
        float deltaY = Math.abs(finalY - initialY);

        if (deltaX > thresholdX || deltaY > thresholdY) {
            if (deltaX >= thresholdX) {
                if (initialX < finalX)
                    return DIRECTION_RIGHT;
                return DIRECTION_LEFT;
            } if (deltaY >= thresholdY) {
                if (initialY < finalY)
                    return DIRECTION_UP;
                return DIRECTION_DOWN;
            }
        }
        return NO_DIRECTION;
    }
}
