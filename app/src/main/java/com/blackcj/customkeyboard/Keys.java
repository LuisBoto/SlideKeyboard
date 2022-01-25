package com.blackcj.customkeyboard;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import java.util.Arrays;

public enum Keys {

    TOP_LEFT(65,' ', ' ', ' ',' ', '1'),
    TOP_CENTER(66,'a','b','c','ç','2'),
    TOP_RIGHT(67,'d','e','f',' ','3'),
    CENTER_LEFT(68,'g', 'h', 'i', ' ', '4'),
    CENTER_CENTER(69,'j','k','l',' ', '5'),
    CENTER_RIGHT(70,'m','n','o','ñ','6'),
    BOTTOM_LEFT(71,'p','q','r','s','7'),
    BOTTOM_CENTER(72,'t','u','v',' ','8'),
    BOTTOM_RIGHT(73,'w','x','y','z','9'),
    SPECIAL_LEFT(74,'-','/','_','@','*'),
    SPECIAL_CENTER(75,'.','!',',','?','0'),
    SPECIAL_RIGHT(76,' ',' ',' ',' ');

    public static int KEYCODE_START = 65;
    private int keyCode;
    private Character west, north, east, south, backSymbol;

    private Keys(int keyCode, Character west, Character north, Character east, Character south, Character backSymbol) {
        this(keyCode, west, north, east, south);
        this.backSymbol = backSymbol;
    }

    private Keys(int keyCode, Character west, Character north, Character east, Character south) {
        this.keyCode = keyCode;
        this.west = west;
        this.north = north;
        this.east = east;
        this.south = south;
        this.backSymbol = ' ';
    }

    public int getKeyCode() {
        return this.keyCode;
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

    public static int determineSwipedDirection(float initialX, float initialY, float finalX, float finalY, double threshold) {
        float deltaX = Math.abs(finalX - initialX);
        float deltaY = Math.abs(finalY - initialY);

        if (deltaX > threshold || deltaY > threshold) {
            if (deltaX > deltaY) {
                if (initialX < finalX)
                    return DIRECTION_RIGHT;
                return DIRECTION_LEFT;
            } if (deltaY > deltaX) {
                if (initialY < finalY)
                    return DIRECTION_DOWN;
                return DIRECTION_UP;
            }
        }
        return NO_DIRECTION;
    }

    public static Keys getKeyForCode(int keyCode) {
        for (Keys key : Keys.values())
            if (key.getKeyCode() == keyCode)
                return key;
        return Keys.SPECIAL_RIGHT;
    }
}
