package com.example.dungeon.model;

import java.util.*;

public class Room {
    private final String name;
    private final String description;
    private final Map<String, Room> neighbors = new HashMap<>();
    private final Map<String, Door> doors = new HashMap<>();
    private final List<Item> items = new ArrayList<>();
    private Monster monster;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Map<String, Room> getNeighbors() {
        return neighbors;
    }

    public Map<String, Door> getDoors() {
        return doors;
    }

    public List<Item> getItems() {
        return items;
    }

    public Monster getMonster() {
        return monster;
    }

    public void setMonster(Monster m) {
        this.monster = m;
    }

    public String describe() {
        StringBuilder sb = new StringBuilder(name + ": " + description);
        StringBuilder sbNeighbor;
        List<String> strs = new ArrayList<>();
        Door d;

        if ( !items.isEmpty() ) {
            sb.append( "\nПредметы : " ).append( String.join(" , ", items.stream().map( Item::getName ).toList() ) );
        }

        if ( monster != null ) {
            sb.append( "\nВ комнате " );

            if ( monster.getHp() == 0 ) {
                sb.append( "мёртвый " );
            }

            sb.append( "монстр : ").append( monster.getName()).append(" (уровень ").append( monster.getLevel() ).append( ")" );
        }

        if (!neighbors.isEmpty()) {
            sb.append("\nВыходы : ");

            for ( String s : neighbors.keySet() ) {
                sbNeighbor = new StringBuilder();
                sbNeighbor.append( s );

                if ( doors.keySet().contains( s ) ) {
                    d = doors.get( s );

                    if ( !d.getOpened() ) {
                        sbNeighbor.append( " (нужен \"" + d.getKey().getName() + "\")" );
                    }
                }

                strs.add( sbNeighbor.toString() );
            }

            sb.append( String.join(" , ", strs ) );
        }

        return sb.toString();
    }
}
