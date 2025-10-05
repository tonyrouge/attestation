package com.example.dungeon.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameState implements Serializable {
    private Player player;
    private Room current;
    private List<Room> rooms = new ArrayList<>();
    private int score;

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms( List<Room> rooms ) {
        this.rooms = rooms;
    }

    public void addRoom( Room room ) {
        this.rooms.add( room );
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player p) {
        this.player = p;
    }

    public Room getCurrent() {
        return current;
    }

    public void setCurrent(Room r) {
        this.current = r;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int d) {
        this.score += d;
    }
}
