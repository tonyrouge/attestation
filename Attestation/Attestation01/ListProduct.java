package Attestation.Attestation01;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

class ListProduct {
    ArrayList<Product> list;

    //Используем конструктор по умолчанию
    public ListProduct() {
        this.list = new ArrayList<>();
    }

    public void read() {
        String endCommand = "END";
        int productCount = 0 , priceProduct;
        int productNameLenMin = 3;
        Scanner scanner = new Scanner( System.in );
        String[] strProducts , x;
        String nameProduct , textProducts;
        boolean state;

        //здесь вводим строку со списком товаров в магазине через точку с запятой
        System.out.println( "Введите список товаров в формате «Продукт = Цена»" );
        System.out.println( "Например: Вискас = 30 ; Чаппи = 50" );
        System.out.println( endCommand + " — завершить работу приложения" );
        while ( true ) {
            System.out.printf( "[ Продукты ] >>> " );
            textProducts = scanner.nextLine();

            if ( textProducts.equalsIgnoreCase( endCommand ) ) System.exit( 1 );

            strProducts = textProducts.split( ";" );
            state = true;

            if ( strProducts.length > 0 ) {

                for ( String product : strProducts ) {
                    x = product.split( "=" );

                    if ( x.length == 2 ) {
                        nameProduct = x[ 0 ].trim();
                        priceProduct = Integer.parseInt( x[ 1 ].trim() );

                        if( nameProduct.length() < productNameLenMin ) {
                            System.out.println( "Ошибка! Наименование товара не может быть короче " + productNameLenMin + " символов" );
                            state = false;
                        }

                        if( priceProduct < 0 ) {
                            System.out.println( "Ошибка! Цена товара не может быть отрицательной" );
                            state = false;
                        }

                        for( Product elemProduct : this.list ) {
                            if( elemProduct.getName().equalsIgnoreCase( nameProduct ) ) {
                                System.out.println( "Ошибка! \"" + nameProduct + "\" — неуникальное наименование товара" );
                                state = false;
                                break;
                            }
                        }

                        if( state ) {
                            this.list.add( new Product( nameProduct , priceProduct ) );
                            productCount++;
                        }
                    } else {
                        System.out.println( "\"" + product + "\": некорректный формат ввода!" );
                    }
                }
            }

            if ( productCount > 0 ) break;

            System.out.println( "Неверный ввод, повторите ещё раз");
        }

        System.out.println( "" );
    }

    public void print() {
        for( Product elemProduct : this.list ) {
            System.out.println( elemProduct.getName() + " = " + elemProduct.getPrice() );
        }
        System.out.println( "" );
    }

    public Product find( String name) {
        for( Product elemProduct : this.list ) {
            if( elemProduct.getName().equalsIgnoreCase( name ) ) {
                return elemProduct;
            }
        }

        return null;
    }

    //количество элементов
    public int size() {
        return this.list.size();
    }

    @Override
    public String toString() {
        return "{" + list + '}';
    }

    @Override
    public boolean equals( Object o ) {
        if ( o == null || getClass() != o.getClass() ) return false;
        ListProduct that = ( ListProduct ) o;
        return Objects.equals( list , that.list );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( list );
    }
}