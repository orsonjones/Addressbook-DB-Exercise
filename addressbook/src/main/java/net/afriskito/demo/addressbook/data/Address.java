package net.afriskito.demo.addressbook.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Address {
    public static Address create(int addressId, int personId, String street, String city, String state, String postalCode) {
        return new AutoValue_Address(addressId, personId, street, city, state, postalCode);
    }
    
    public abstract Integer addressId();
    public abstract Integer personId();
    public abstract String street();
    public abstract String city();
    public abstract String state();
    public abstract String postalCode();
    
    public String displayString() {
        return street() + " " + city() + ", " + state() + " " + postalCode();
    }
}
