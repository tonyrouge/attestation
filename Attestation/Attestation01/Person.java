package Attestation.Attestation01;

import java.util.ArrayList;
import java.util.Objects;

class Person {

    private String name;
    private int wallet;
    private ArrayList<Product> listProducts;

    //конструктор по умолчанию
    public Person() {
        this.name = "Noname";
        this.wallet = 0;
        this.listProducts = new ArrayList<>();
    }

    //конструктор только с именем
    public Person( String name , int wallet ) {
        this.name = name;
        this.wallet = wallet;
        this.listProducts = new ArrayList<>();
    }

    public boolean purchase( Product newProduct ) {
        int price = newProduct.getPrice();

        if( this.wallet >= price ) {
            this.listProducts.add( newProduct );
            this.wallet -= price;
            return true;
        } else {
            return false;
        }
    }

    //геттеры и сеттеры для свойств
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getWallet() {
        return wallet;
    }

    public void setWallet( int wallet ) {
        this.wallet = wallet;
    }

    @Override
    public boolean equals( Object o ) {
        if ( o == null || getClass() != o.getClass() ) return false;
        Person person = ( Person ) o;
        return wallet == person.wallet && Objects.equals( name , person.name ) && Objects.equals( listProducts , person.listProducts );
    }

    @Override
    public int hashCode() {
        return Objects.hash( name , wallet , listProducts );
    }

    @Override
    public String toString() {
        ArrayList<String> ProductNames = new ArrayList<>();
        String str = name + " - ";

        if( listProducts.size() == 0 ) {
            str += "Ничего не куплено";
        } else {
            for( Product elemProduct : listProducts ) {
                ProductNames.add( elemProduct.toString() );
            }

            str += String.join( " , " , ProductNames );
        }

        return str;
    }
}