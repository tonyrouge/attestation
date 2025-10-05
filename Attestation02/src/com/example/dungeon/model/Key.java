package com.example.dungeon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Key extends Item {
    public Key(String name) {
        super(name);
    }

    @Override
    public void apply( GameState ctx ) {
        Map<String,Door> doors = ctx.getCurrent().getDoors();
        Player p = ctx.getPlayer();
        boolean doorFind = false;
        Door door;

        for ( String s : doors.keySet() ) {
            door = doors.get( s );

            if ( door.getKey().getName().equals( getName() ) ) {
                System.out.println( "Дверь \"" + door.getName() + "\" разблокирована" );
                door.setOpened();
                p.getInventory().remove( this );
                doorFind = true;
                break;
            }
        }

        if ( !doorFind ) {
            System.out.println( "В этой комнате нет дверей открываемых этим ключом" );
        }
    }

    @Override
    public String toString() {
        return "Key : " + this.getName();
    }
}
