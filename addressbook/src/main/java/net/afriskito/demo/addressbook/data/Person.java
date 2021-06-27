package net.afriskito.demo.addressbook.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Person {
    public static Person create(Integer id, String firstName, String lastName) {
        return new AutoValue_Person(id, firstName, lastName);
    }
    
    public abstract Integer id();
    public abstract String firstName();
    public abstract String lastName();
    
    public String displayString() {
        return firstName() + " " + lastName();
    }
}
