package com.example.dungeon.core;

import com.example.dungeon.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private final GameState state = new GameState();
    private final Map<String, Command> commands = new LinkedHashMap<>();

    static {
        WorldInfo.touch("Game");
    }

    public Game() {
        registerCommands();
        bootstrapWorld();
    }

    private void registerCommands() {
        List<String> knownNeighbors = List.of( "north" , "south" , "east" , "west" );

        commands.put( "help" , (ctx , a ) -> System.out.println( "Команды: " + String.join(", " , commands.keySet() ) ) );
        commands.put( "about" , (ctx , a ) -> {
            System.out.println( "Dungeon Mini" );
            System.out.println( "Версия: 1.0" );
            System.out.println( "Java-версия : " + System.getProperty( "java.version" ) );
            System.out.println( "https://github.com/tonyrouge/attestation/tree/attestation/Attestation02" );
        });
        commands.put( "gc-stats" , (ctx , a ) -> {
            Runtime rt = Runtime.getRuntime();
            long free = rt.freeMemory(), total = rt.totalMemory(), used = total - free;
            System.out.println("Память: used=" + used + " free=" + free + " total=" + total);
        });
        commands.put( "look" , (ctx, a) -> System.out.println( ctx.getCurrent().describe() ) );
        commands.put( "move" , (ctx, a) -> {
            Room currentRoom = ctx.getCurrent();
            String neighbor;
            boolean opened = true;

            if ( a.size() == 0 ) {
    			throw new InvalidCommandException( "направление должно быть указано" );
            } else {
                if ( a.size() > 1 ) {
                    throw new InvalidCommandException( "направление должно быть одно" );
                } else {
                    neighbor = a.get( 0 );
                    if ( !knownNeighbors.contains( neighbor ) ) {
                        throw new InvalidCommandException( "неизвестное направление" );
                    } else {
                        if ( currentRoom.getNeighbors().keySet().contains( neighbor ) ) {
                            if ( currentRoom.getDoors().keySet().contains( neighbor ) ) {
                                if ( !currentRoom.getDoors().get( neighbor ).getOpened() ) {
                                    opened = false;
                                    throw new InvalidCommandException( "Пройти нельзя, дверь заблокирована" );
                                }
                            }

                            if ( opened ) {
                                Room gotoRoom = currentRoom.getNeighbors().get( neighbor );
                                ctx.setCurrent( gotoRoom );
                                System.out.println( "Вы перешли в : " + gotoRoom.getName() );
                                System.out.println( ctx.getCurrent().describe() );
                            }
                        } else {
                            throw new InvalidCommandException( "указанное направление не доступно" );
                        }

                    }
                }
            }
        } );

        commands.put( "take" , (ctx , a ) -> {
             // Если предметы не указаны или указано "*", то будут взяты все предметы в комнате
             // Можно указать несколько через запятую

            String str = String.join( " " , a );
            List<String> itemNames = Arrays.asList( str.split( "[,]" ) )
                    .stream()
                    .filter(s -> !s.trim().isEmpty() ) // Убирает строки, которые становятся пустыми после trim()
                    .collect( Collectors.toList() );

            List<Item> items = ctx.getCurrent().getItems();
            boolean itemAll = false , itemFind = false;
            Iterator<Item> iterator = items.iterator();
            Player p = ctx.getPlayer();
            String itemName;

            if ( itemNames.size() == 0 ) {
                itemAll = true;
            }

            if ( itemNames.size() == 1 ) {
                if ( itemNames.getFirst().equals( "*" ) ) {
                    itemAll = true;
                }
            }

            while ( iterator.hasNext() ) {
                Item element = iterator.next();
                itemName = element.getName();

                if ( ( itemNames.contains( itemName.trim() ) || ( itemAll ) ) ) {
                    itemFind = true;
                    p.addInventory( element );
                    System.out.println("Взят : " + itemName );
                    iterator.remove(); // Безопасное удаление элемента
                }
            }

            if ( !itemFind ) {
                throw new InvalidCommandException( "указанный предмет не найден" );
            }
        } );

        commands.put( "inventory" , (ctx , a ) -> {
            ctx.getPlayer().getInventory().stream().map( Item::toString ).forEach( s -> System.out.println( s ) );
        } );

        commands.put( "use" , (ctx , a ) -> {
            String str = String.join( " " , a );
            Item itemFind = null;
            List<Item> items = ctx.getPlayer().getInventory();

            if ( str.length() == 0 ) {
                throw new InvalidCommandException( "Предмет не указан" );
            } else {
                for (Item item : items) {
                    if (item.getName().equalsIgnoreCase(str)) {
                        itemFind = item;
                        break;
                    }
                }

                if ( itemFind != null ) {
                    itemFind.apply( ctx );
                } else {
                    throw new InvalidCommandException( "Такого предмета у \"" + ctx.getPlayer().getName() + "\" нет" );
                }
            }
        } );


        commands.put( "fight" , (ctx, a) -> {
            Monster monster = ctx.getCurrent().getMonster();

            if ( monster == null ) {
                throw new InvalidCommandException( "в комнате нет монстра" );
            } else {
                if ( monster.getHp() == 0 ) {
                    throw new InvalidCommandException( "монстр уже мёртв" );
                } else {
                    Player player = ctx.getPlayer();
                    int monsterHP = monster.getHp() , monsterAttack = monster.getLevel() * 3;
                    int playerHP  = player.getHp()  , playerAttack = player.getAttack();
                    String monsterName = monster.getName();

                    Random rd = new Random();
                    int go = rd.nextInt( 1 );

                    while ( ( monsterHP > 0 ) && ( playerHP > 0 ) ) {

                        if ( go == 0 ) {
                            monsterHP = monster.attack( ctx , player.getName() , playerAttack );
                        } else {
                            playerHP = player.attack( monsterName , monsterAttack );
                        }

                        go = 1 - go;
                    }

                    if ( playerHP == 0 ) {
                        System.out.println( "Вы мертвы" );
                        SaveLoad.writeScore( player.getName() , ctx.getScore() );
                        System.exit(0);
                    }

                    if ( monsterHP == 0 ) {
                        ctx.getCurrent().setMonster( null );
                    }

                }
            }

        } );

        commands.put("save", (ctx, a) -> SaveLoad.save(ctx));
        commands.put("load", (ctx, a) -> SaveLoad.load(ctx));
//        commands.put("saveall", (ctx, a) -> SaveLoad.saveAll(ctx));
//        commands.put("loadall", (ctx, a) -> SaveLoad.loadAll());
        commands.put("scores", (ctx, a) -> SaveLoad.printScores());
        commands.put("exit", (ctx, a) -> {
            SaveLoad.writeScore( ctx.getPlayer().getName() , ctx.getScore() );
            System.out.println("Пока!");
            System.exit(0);
        });

        commands.put("div", (ctx, a) -> {
            if ( a.size() == 2 ) {
                int divisible = Integer.parseInt( a.get( 0 ) );
                int divider   = Integer.parseInt( a.get( 1 ) );
                double result = divisible / divider;      // возможно ошибка выполнения арифметических операций
                System.out.println( divisible + " / " + divider + " = " + result );
            } else {
                throw new InvalidCommandException( "аргументов должно быть дав - делимое и делитель" );
            }

            // Ошибка компиляции, забыта ;
            /*
                System.out.println( "div" )
            */

            // Ошибка компиляции, неизвестное свойство
            /*
                System.println( "div" );
            */

            // Ошибка компиляции, перепутан printf b println, вызов несуществующих/неправильных аргументов
            /*
                System.out.println( "%d / %d = %f" , divisible , divider , result );
            */
        });
    }

    private void bootstrapWorld() {
        Player hero = new Player("Герой", 20, 5);
        state.setPlayer(hero);

        Room r01 = new Room("Площадь", "Каменная площадь с фонтаном.");
        Room r02 = new Room("Лес", "Шелест листвы и птичий щебет.");
        Room r03 = new Room("Пещера", "Темно и сыро.");

        Room r04 = new Room( "Васильковое поле"  , "" );
        Room r05 = new Room( "Сосновый бор"      , "" );
        Room r06 = new Room( "Грот"              , "" );
        Room r07 = new Room( "Цементный завод"   , "" );
        Room r08 = new Room( "Цветочная поляна"  , "" );
        Room r09 = new Room( "Памятник"          , "" );
        Room r10 = new Room( "Сыродельня"        , "" );
        Room r11 = new Room( "Скобяная лавка"    , "" );
        Room r12 = new Room( "Башня"             , "" );
        Room r13 = new Room( "Пруд"              , "" );
        Room r14 = new Room( "Пасека"            , "" );
        Room r15 = new Room( "Овраг"             , "" );
        Room r16 = new Room( "Болото"            , "" );
        Room r17 = new Room( "Карьер"            , "" );
        Room r18 = new Room( "Фьёрд"             , "" );
        Room r19 = new Room( "Пустошь"           , "" );
        Room r20 = new Room( "Рынок"             , "" );
        Room r21 = new Room( "Пристань"          , "" );
        Room r23 = new Room( "Ратуша"            , "" );
        Room r24 = new Room( "Парк"              , "" );
        Room r25 = new Room( "Озеро"             , "" );
        Room r26 = new Room( "Утиная ферма"      , "" );
        Room r27 = new Room( "Дендрарий"         , "" );
        Room r28 = new Room( "Замок"             , "" );
        Room r29 = new Room( "Маковое поле"      , "" );
        Room r30 = new Room( "Мост"              , "" );
        Room r31 = new Room( "Перекрёсток"       , "" );
        Room r32 = new Room( "Заросли борщивика" , "" );
        Room r33 = new Room( "Пивзавод"          , "" );
        Room r34 = new Room( "Сопки"             , "" );
        Room r35 = new Room( "Мельница"          , "" );
        Room r36 = new Room( "Берёзовая роща"    , "" );
        Room r37 = new Room( "Родник"            , "" );
        Room r38 = new Room( "Ранчо"             , "" );
        Room r39 = new Room( "Большая гора"      , "" );
        Room r40 = new Room( "Космодром"         , "" );
        Room r41 = new Room( "Водопад"           , "" );
        Room r42 = new Room( "Избушка"           , "" );
        Room r43 = new Room( "Яблоневый сад"     , "" );
        Room r44 = new Room( "Кладбище"          , "" );
        Room r45 = new Room( "Эпадром"           , "" );

		r01.getNeighbors().put( "north" , r02 );
        r01.getNeighbors().put( "west"  , r04 );
        r01.getNeighbors().put( "east"  , r20 );
        r01.getNeighbors().put( "south" , r08 );

        r02.getNeighbors().put( "south" , r01 );
        r02.getNeighbors().put( "east"  , r03 );
        r02.getNeighbors().put( "north" , r13 );

        r03.getNeighbors().put( "west"  , r02 );

        r04.getNeighbors().put( "west"  , r05 );
        r04.getNeighbors().put( "east"  , r01 );

        r05.getNeighbors().put( "west"  , r06 );
        r05.getNeighbors().put( "east"  , r04 );
        r05.getNeighbors().put( "south" , r07 );

        r06.getNeighbors().put( "east"  , r05 );
        r06.getNeighbors().put( "west"  , r33 );

        r07.getNeighbors().put( "north" , r05 );

        r08.getNeighbors().put( "north" , r01 );
        r08.getNeighbors().put( "south" , r09 );

        r09.getNeighbors().put( "north" , r08 );
        r09.getNeighbors().put( "west"  , r10 );
        r09.getNeighbors().put( "east"  , r11 );
        r09.getNeighbors().put( "south" , r12 );

        r10.getNeighbors().put( "east"  , r09 );

        r11.getNeighbors().put( "west"  , r09 );

        r12.getNeighbors().put( "north" , r09 );

        r13.getNeighbors().put( "west"  , r14 );
        r13.getNeighbors().put( "east"  , r17 );
        r13.getNeighbors().put( "south" , r02 );

        r14.getNeighbors().put( "east"  , r13 );
        r14.getNeighbors().put( "south" , r15 );

        r15.getNeighbors().put( "north" , r14 );
        r15.getNeighbors().put( "west"  , r16 );

        r16.getNeighbors().put( "east"  , r15 );

        r17.getNeighbors().put( "west"  , r13 );
        r17.getNeighbors().put( "east"  , r18 );

        r18.getNeighbors().put( "west"  , r17 );
        r18.getNeighbors().put( "east"  , r19 );

        r19.getNeighbors().put( "west"  , r18 );

        r20.getNeighbors().put( "west"  , r01 );
        r20.getNeighbors().put( "east"  , r21 );

        r21.getNeighbors().put( "west"  , r20 );
        r21.getNeighbors().put( "east"  , r23 );

        r23.getNeighbors().put( "west"  , r21 );
        r23.getNeighbors().put( "east"  , r24 );

        r24.getNeighbors().put( "north" , r25 );
        r24.getNeighbors().put( "west"  , r23 );
        r24.getNeighbors().put( "east"  , r27 );

        r25.getNeighbors().put( "east"  , r26 );
        r25.getNeighbors().put( "south" , r24 );

        r26.getNeighbors().put( "west"  , r25 );

        r27.getNeighbors().put( "west"  , r24 );
        r27.getNeighbors().put( "east"  , r28 );

        r28.getNeighbors().put( "north" , r27 );
        r28.getNeighbors().put( "south" , r29 );
        r28.getNeighbors().put( "south" , r39 );

        r29.getNeighbors().put( "north" , r40 );
        r29.getNeighbors().put( "west"  , r28 );
        r29.getNeighbors().put( "east"  , r30 );

        r30.getNeighbors().put( "west"  , r29 );
        r30.getNeighbors().put( "east"  , r31 );

        r31.getNeighbors().put( "north" , r41 );
        r31.getNeighbors().put( "west"  , r30 );
        r31.getNeighbors().put( "east"  , r32 );
        r31.getNeighbors().put( "south" , r43 );

        r32.getNeighbors().put( "north" , r42 );
        r32.getNeighbors().put( "west"  , r31 );
        r32.getNeighbors().put( "east"  , r33 );
        r32.getNeighbors().put( "south" , r45 );

        r33.getNeighbors().put( "west"  , r32 );
        r33.getNeighbors().put( "east"  , r06 );

        r34.getNeighbors().put( "north" , r27 );
        r34.getNeighbors().put( "south" , r35 );

        r35.getNeighbors().put( "north" , r34 );
        r35.getNeighbors().put( "east"  , r36 );

        r36.getNeighbors().put( "west"  , r35 );
        r36.getNeighbors().put( "east"  , r37 );

        r37.getNeighbors().put( "west"  , r36 );
        r37.getNeighbors().put( "south" , r38 );

        r38.getNeighbors().put( "north" , r37 );

        r39.getNeighbors().put( "north" , r28 );

        r40.getNeighbors().put( "south" , r29 );

        r41.getNeighbors().put( "south" , r31 );

        r42.getNeighbors().put( "south" , r32 );

        r43.getNeighbors().put( "north" , r31 );
        r43.getNeighbors().put( "south" , r44 );

        r44.getNeighbors().put( "north" , r43 );

        r45.getNeighbors().put( "north" , r32 );

        Key k01 = new Key( "Синий ключ"       ); //
        Key k02 = new Key( "Оранжевый ключ"   ); //
        Key k03 = new Key( "Жёлтый ключ"      ); //
        Key k04 = new Key( "Голубой ключ"     ); //
        Key k05 = new Key( "Коричневый ключ"  ); //
        Key k06 = new Key( "Зелёный ключ"     ); //
        Key k07 = new Key( "Феолетовый ключ"  ); //
        Key k08 = new Key( "Тёмносиний ключ"  ); //
        Key k09 = new Key( "Салатовый ключ"   ); //
        Key k10 = new Key( "Розовый ключ"     ); //
        Key k11 = new Key( "Красный ключ"     ); //
        Key k12 = new Key( "Серебренный ключ" ); //
        Key k13 = new Key( "Золотой ключ"     ); //
        Key k14 = new Key( "Пурпурный ключ"   ); //
        Key k15 = new Key( "Бирюзовый ключ"   ); //
        Key k16 = new Key( "Белый ключ"       ); //

        Door d01 = new Door( "Синяя дверь"      , "" , r05 , r06 , k01 );
        Door d02 = new Door( "Оранжевая дверь"  , "" , r24 , r25 , k02 );
        Door d03 = new Door( "Жёлтая дверь"     , "" , r25 , r26 , k03 );
        Door d04 = new Door( "Голубая дверь"    , "" , r28 , r39 , k04 );
        Door d05 = new Door( "Коричневая дверь" , "" , r37 , r38 , k05 );
        Door d06 = new Door( "Зелёная дверь"    , "" , r05 , r07 , k06 );
        Door d07 = new Door( "Феолетовая дверь" , "" , r29 , r40 , k07 );
        Door d08 = new Door( "Тёмносиняя дверь" , "" , r32 , r42 , k08 );
        Door d09 = new Door( "Салатовая дверь"  , "" , r32 , r45 , k09 );
        Door d10 = new Door( "Розовая дверь"    , "" , r32 , r33 , k10 );
        Door d11 = new Door( "Красная дверь"    , "" , r09 , r12 , k10 );

        r05.getDoors().put( "west"  , d01 );
        r06.getDoors().put( "east"  , d01 );

        r24.getDoors().put( "north" , d02 );
        r25.getDoors().put( "south" , d02 );

        r26.getDoors().put( "west"  , d03 );
        r25.getDoors().put( "east"  , d03 );

        r39.getDoors().put( "north" , d04 );
        r28.getDoors().put( "south" , d04 );

        r38.getDoors().put( "north" , d05 );
        r37.getDoors().put( "south" , d05 );

        r07.getDoors().put( "north" , d06 );
        r05.getDoors().put( "south" , d06 );

        r29.getDoors().put( "north" , d07 );
        r40.getDoors().put( "south" , d07 );

        r32.getDoors().put( "north" , d08 );
        r42.getDoors().put( "south" , d08 );

        r45.getDoors().put( "north" , d09 );
        r32.getDoors().put( "south" , d09 );

        r33.getDoors().put( "west"  , d10 );
        r32.getDoors().put( "east"  , d10 );

        r12.getDoors().put( "north" , d11 );
        r09.getDoors().put( "south" , d11 );

        Monster m01 = new Monster( "Волк"              , 1 , 18 );
        Monster m02 = new Monster( "Дракон"            , 5 , 60 );
        Monster m03 = new Monster( "Кошей"             , 4 , 35 );
        Monster m04 = new Monster( "Крыса"             , 1 , 6 );
        Monster m05 = new Monster( "Чёрт"              , 4 , 28 );
        Monster m06 = new Monster( "Соловей-разбойник" , 2 , 18 );
        Monster m07 = new Monster( "Крокодил"          , 1 , 15 );
        Monster m08 = new Monster( "Чупакабра"         , 3 , 29 );
        Monster m09 = new Monster( "Водяной"           , 2 , 24 );
        Monster m10 = new Monster( "Грызли"            , 5 , 50 );
        Monster m11 = new Monster( "Привидение"        , 1 , 11 );
        Monster m12 = new Monster( "Леший"             , 1 , 17 );
        Monster m13 = new Monster( "Кот Базилио"       , 1 , 14 );
        Monster m14 = new Monster( "Упырь"             , 2 , 32 );
        Monster m15 = new Monster( "Зомби"             , 3 , 35 );
        Monster m16 = new Monster( "Орк"               , 3 , 31 );
        Monster m17 = new Monster( "Немезис"           , 2 , 19 );
        Monster m18 = new Monster( "Мимик"             , 2 , 16 );
        Monster m19 = new Monster( "Пьяный карлик"     , 3 , 21 );
        Monster m20 = new Monster( "Бармолей"          , 5 , 30 );

        Weapon w01 = new Weapon("Нож"        , 1 );
        Weapon w02 = new Weapon("Молоток"    , 1 );
        Weapon w03 = new Weapon("Топор"      , 2 );
        Weapon w04 = new Weapon("Кинжал"     , 2 );
        Weapon w05 = new Weapon("Меч"        , 3 );
        Weapon w06 = new Weapon("Пистолет"   , 3 );
        Weapon w07 = new Weapon("Винтовка"   , 4 );
        Weapon w08 = new Weapon("Автомат"    , 4 );
        Weapon w09 = new Weapon("Пулемёт"    , 5 );
        Weapon w10 = new Weapon("Гранатомёт" , 6 );

        Potion p01 = new Potion("Зелье №1", 5 );
        Potion p02 = new Potion("Зелье №2", 8 );
        Potion p03 = new Potion("Зелье №3", 10 );
        Potion p04 = new Potion("Зелье №4", 14 );
        Potion p05 = new Potion("Зелье №5", 18 );
        Potion p06 = new Potion("Зелье №6", 23 );
        Potion p07 = new Potion("Зелье №7", 28 );
        Potion p08 = new Potion("Зелье №8", 35 );
        Potion p09 = new Potion("Зелье №9", 40 );

        m01.getItems().add( k11 );
        m01.getItems().add( p01 );

        m02.getItems().add( k10 );
        m02.getItems().add( k15 );
        m02.getItems().add( p08 );

        m03.getItems().add( k07 );
        m03.getItems().add( k12 );
        m03.getItems().add( w07 );

        m04.getItems().add( k06 );

        m05.getItems().add( w03 );
        m05.getItems().add( p07 );

        m06.getItems().add( k03 );
        m06.getItems().add( w01 );

        m07.getItems().add( k02 );

        m08.getItems().add( k04 );
        m08.getItems().add( p05 );
        m08.getItems().add( k14 );

        m09.getItems().add( k05 );
        m09.getItems().add( p02 );

        m10.getItems().add( k01 );

        m12.getItems().add( k08 );
        m12.getItems().add( k16 );

        m13.getItems().add( k09 );
        m13.getItems().add( p02 );

        m14.getItems().add( k13 );

        m15.getItems().add( w06 );
        m13.getItems().add( p03 );

        m16.getItems().add( w05 );
        m16.getItems().add( p04 );

        m17.getItems().add( w02 );

        m18.getItems().add( p06 );

        m19.getItems().add( w04 );

        m20.getItems().add( p09 );

        //
        r04.getItems().add( k01 );
        r04.getItems().add( k06 );
        //

        r02.getItems().add( p01 );
        r02.getItems().add( k12 );

        r07.getItems().add( w10 );
        r19.getItems().add( w09 );
        r32.getItems().add( w08 );

        r03.getItems().add( p01 );
        r03.getItems().add( w10 );
        r05.getItems().add( p01 );
        r08.getItems().add( p01 );
        r07.getItems().add( p04 );
        r09.getItems().add( p01 );
        r10.getItems().add( p03 );
        r11.getItems().add( p03 );
        r12.getItems().add( p01 );
        r13.getItems().add( p02 );
        r15.getItems().add( p05 );
        r14.getItems().add( p06 );
        r19.getItems().add( p04 );
        r25.getItems().add( p03 );
        r31.getItems().add( p01 );
        r34.getItems().add( p04 );
        r38.getItems().add( p03 );
        r40.getItems().add( p02 );
        r41.getItems().add( p03 );
        r44.getItems().add( p02 );

        r02.setMonster( m01 );
        r39.setMonster( m02 );
        r19.setMonster( m03 );
        r30.setMonster( m04 );
        r35.setMonster( m05 );
        r05.setMonster( m06 );
        r13.setMonster( m07 );
        r07.setMonster( m08 );
        r25.setMonster( m09 );
        r14.setMonster( m10 );
        r28.setMonster( m11 );
        r36.setMonster( m12 );
        r42.setMonster( m13 );
        r45.setMonster( m14 );
        r33.setMonster( m15 );
        r41.setMonster( m16 );
        r11.setMonster( m17 );
        r10.setMonster( m18 );
        r44.setMonster( m19 );
        r31.setMonster( m20 );

        state.setCurrent( r01 );
		
		state.addRoom( r01 );
		state.addRoom( r02 );
		state.addRoom( r03 );
		state.addRoom( r04 );
		state.addRoom( r05 );
		state.addRoom( r06 );
		state.addRoom( r07 );
		state.addRoom( r08 );
		state.addRoom( r09 );
		state.addRoom( r10 );
		state.addRoom( r11 );
		state.addRoom( r12 );
		state.addRoom( r13 );
		state.addRoom( r14 );
		state.addRoom( r15 );
		state.addRoom( r16 );
		state.addRoom( r17 );
		state.addRoom( r18 );
		state.addRoom( r19 );
		state.addRoom( r20 );
		state.addRoom( r21 );
		state.addRoom( r23 );
		state.addRoom( r24 );
		state.addRoom( r25 );
		state.addRoom( r26 );
		state.addRoom( r27 );
		state.addRoom( r28 );
		state.addRoom( r29 );
		state.addRoom( r30 );
		state.addRoom( r31 );
		state.addRoom( r32 );
		state.addRoom( r33 );
		state.addRoom( r34 );
		state.addRoom( r35 );
		state.addRoom( r36 );
		state.addRoom( r37 );
		state.addRoom( r38 );
		state.addRoom( r39 );
		state.addRoom( r40 );
		state.addRoom( r41 );
		state.addRoom( r42 );
		state.addRoom( r43 );
		state.addRoom( r44 );
		state.addRoom( r45 );
    }

    public void run() {
        System.out.println("DungeonMini. 'help' — команды.");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("> ");
                String line = in.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> parts = Arrays.asList(line.split("\s+"));
                String cmd = parts.getFirst().toLowerCase(Locale.ROOT);
                List<String> args = parts.subList(1, parts.size());
                Command c = commands.get(cmd);
                try {
                    if (c == null) throw new InvalidCommandException("Неизвестная команда: " + cmd);
                    c.execute(state, args);
                    state.addScore(1);
                } catch (InvalidCommandException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Непредвиденная ошибка: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода: " + e.getMessage());
        }
    }
}
