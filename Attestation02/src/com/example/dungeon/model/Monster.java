package com.example.dungeon.model;

import java.util.ArrayList;
import java.util.List;

public class Monster extends Entity {
    private int level;
    private List<Item> items = new ArrayList<>();

    public Monster(String name, int level, int hp) {
        super(name, hp);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Item> getItems() {
        return items;
    }

    // монстра атаковали, он теряет здоровье, а когда умрёт выпадет лут
    public int attack( GameState ctx , String attackName , int attackHP ) {
        int monsterHP = attack( attackName , attackHP );

        if ( monsterHP == 0 ) {
            System.out.print( "\"" + getName() + "\" мёртв" );

            if ( items.isEmpty()) {
                System.out.println( "" );
            } else {
                System.out.println( " , из него выпало :" );
            }

            items.stream().map( Item::toString ).forEach( s -> System.out.println( s ) );
            ctx.getCurrent().getItems().addAll( items );
        }

        return monsterHP;
    }
}
