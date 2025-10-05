package Attestation.Attestation01; //Приложение собирает список покупателей, список продуктов и обрабатывает покупки

import java.util.Scanner;

public class App {
    public static void main( String[] args ) {
        String marketName = "Мария-РА";
        ListPerson persons = new ListPerson();
        ListProduct products = new ListProduct();

        System.out.printf( "### %s ###\n\n" , marketName );

        persons.read();                     //Печатаем имена покупателей
        persons.print();                    //Вводим сумму денег в кошельке покупателей
        products.read();                    //Вводим имеющиеся в наличии продукты
        products.print();                   //Выводим прайс-лист на экран
        acquisition( persons , products );  //Вводим покупки клиентов
        persons.check();                    //Смотрим, какой клиент что купил
        persons.print();                    //Сальдо у клиента
    }

    private static void acquisition( ListPerson persons , ListProduct products ) {
        String endCommand = "END";
        Scanner scanner = new Scanner( System.in );
        Product p;
        String strPurchase , namePerson , nameProduct;
        String[] x;

        //Вводим список продуктов в одну строку через точку с запятой
        System.out.println( "Введите имя клиента и наименование товара в формате «Йеннифер - Хлеб»" );
        System.out.println( endCommand + " — завершить ввод покупок" );

        while ( true ) {
            System.out.printf( "[ Покупка ] >>> " );
            strPurchase = scanner.nextLine();

            if ( strPurchase.equalsIgnoreCase( endCommand ) ) return;

            x = strPurchase.split( "-" );

            if ( x.length == 2 ) {
                namePerson = x[0].trim();
                nameProduct = x[1].trim();
                p = products.find( nameProduct );

                if( p != null ) {
                    persons.purchase( namePerson , p );
                } else {
                    System.out.println("Товар с наименованием \"" + nameProduct + "\" не найден" );
                }
            } else {
                System.out.println( "\"" + strPurchase + "\": некорректный формат ввода!" );
            }
        }
    }
}
