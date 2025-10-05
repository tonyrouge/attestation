package com.example.dungeon.model;

import java.util.*;

public class Door {
    private final String name;
    private final String description;
    private final Room r1 , r2;
    private final Key key;
    private boolean opened;

    public Door( String name , String description , Room r1 , Room r2 , Key key ) {
        this.name = name;
        this.description = description;
        this.r1 = r1;
        this.r2 = r2;
        this.key = key;
        this.opened = false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Room getR1() {
        return r1;
    }

    public Room getR2() {
        return r2;
    }

    public Key getKey() {
        return key;
    }

    public boolean getOpened() {
        return opened;
    }

    public void setClosed() {
        this.opened = false;
    }

    public void setOpened() {
        this.opened = true;
    }
}
