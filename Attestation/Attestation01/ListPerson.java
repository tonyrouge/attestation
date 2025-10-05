package Attestation.Attestation01;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

class ListPerson {
    ArrayList<Person> list;

    public ListPerson() { //используем конструктор по умолчанию
    this.list = new ArrayList<>();
    }

    public void read() {
        String endCommand = "END"; //команда завершения ввода
        int personNameLenMin = 3; //минимальная длина имени клиента
        int personCount = 0 , sumPerson;
        boolean state;
        Scanner scanner = new Scanner( System.in );
        String[] strPersons , x;
        String namePerson , textPersons;

        //здесь вводим строку со списком покупателей через точку с запятой
        System.out.println( "Введите список покупок в формате Йеннифер = 2000 ; Лютик = 1000 ; Геральт = 10000" );
        System.out.println( endCommand + " — завершить работу приложения" );
        while ( true ) {
            System.out.printf( "[ Покупатели ] >>> " );
            textPersons = scanner.nextLine();

            if ( textPersons.equalsIgnoreCase( endCommand ) ) System.exit( 1 );

            strPersons = textPersons.split( ";" );
            state = true;

            if ( strPersons.length > 0 ) {
                for ( String strPerson : strPersons ) {
                    x = strPerson.split( "=" );

                    if ( x.length == 2 ) {
                        namePerson = x[ 0 ].trim();
                        sumPerson = Integer.parseInt( x[ 1 ].trim() );

                        if( namePerson.length() < personNameLenMin ) {
                            System.out.println( "Ошибка! Имя не может быть короче " + personNameLenMin + " символов" );
                            state = false;
                        }

                        if( sumPerson < 0 ) {
                            System.out.println( "Сальдо клиента не может быть отрицательным!" );
                            state = false;
                        }

                        for( Person elemPerson : this.list ) {
                            if( elemPerson.getName().equalsIgnoreCase( namePerson ) ) {
                                System.out.println( "Ошибка! Имя клиента должно быть уникальным, в списке \"" + namePerson + "\" уже имеется" );
                                state = false;
                                break;
                            }
                        }

                        if( state ) {
                            this.list.add( new Person( namePerson , sumPerson ) );
                            personCount++;
                        }
                    } else {
                        System.out.println( "\"" + strPerson + "\": некорректный формат ввода!" );
                    }
                }
            }

            if ( personCount > 0 ) break;

            System.out.println( "Неверно, повторите ввод ещё раз");
        }

        System.out.println( "" );
    }

    public void print() {
        for( Person elemPerson : this.list ) {
            System.out.println( elemPerson.getName() + " = " + elemPerson.getWallet() );
        }
        System.out.println( "" );
    }

    public Person find( String name) {
        for( Person elemPerson : this.list ) {
            if( elemPerson.getName().equalsIgnoreCase( name ) ) {
                return elemPerson;
            }
        }

        return null;
    }

    public void check() {
        for( Person elemPerson : this.list ) {
            System.out.println( elemPerson.toString() );
        }
        System.out.println( "" );
    }

    public boolean purchase( String namePerson , Product newProduct ) {
        for( Person elemPerson : this.list ) {
            if( elemPerson.getName().equalsIgnoreCase( namePerson ) ) {
                if( elemPerson.purchase( newProduct ) ) {
                    System.out.println( "\"" + namePerson + "\" купил \"" + newProduct.getName() + "\"" );
                    return true;
                } else {
                    System.out.println( "\"" + namePerson + "\" не может позволить себе \"" + newProduct.getName() + "\"" );
                    return false;
                }
            }
        }

        System.out.println( "Ошибка! Покупатель с именем \"" + namePerson + "\" не найден" );
        return false;
    }

    // Количество элементов
    public int size() {
        return this.list.size();
    }

    @Override
    public String toString() {
        return "{" + list + '}';
    }

    @Override
    public boolean equals(Object o) {
        if ( o == null || getClass() != o.getClass() ) return false;
        ListPerson that = ( ListPerson ) o;
        return Objects.equals( list , that.list );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( list );
    }
}