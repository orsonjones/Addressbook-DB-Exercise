package net.afriskito.demo.addressbook;

import java.io.Console;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.afriskito.demo.addressbook.data.Address;
import net.afriskito.demo.addressbook.data.Person;
import net.afriskito.demo.addressbook.database.Database;
import net.afriskito.demo.addressbook.database.DatabaseException;

public class EditMenu {
    private final Database db;
    private final Console console;
    private final PrintWriter writer;
    private Person person;

    public EditMenu(Database db, Console console, PrintWriter writer, Person person) {
        this.db = db;
        this.console = console;
        this.writer = writer;
        this.person = person;
    }

    void edit() {
        while (true) {
            writer.println();
            writer.println(person.firstName() + " " + person.lastName());
            try {
                for (Address address : db.AddressDB().getAddresses(person.id()))
                    writer.printf("%d %s%n", address.addressId(), address.displayString());
            } catch (DatabaseException ex) {
                Logger.getLogger(EditMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
            writer.println("1:Edit Name  2:Add Address  3:Edit Address  4:Delete Address   0:Main Menu");
            String readLine = console.readLine("Enter number to perform action: ");
            try {
                switch (Integer.parseInt(readLine)) {
                    case 1:
                        editName();
                        continue;
                    case 2:
                        addAddress();
                        continue;
                    case 3:
                        editAddress();
                        continue;
                    case 4:
                        deleteAddress();
                        continue;
                    case 0:
                        return;
                }
            } catch (NumberFormatException ex) {}
            writer.println("Unrecognized option: " + readLine);
        }
    }

    private void editName() {
        try {
            String firstName = console.readLine("Enter first name: ");
            String lastName = console.readLine("Enter last name: ");
            db.personDB().updatePerson(person.id(), firstName, lastName);
            person = Person.create(person.id(), firstName, lastName);
        } catch (DatabaseException ex) {
            Logger.getLogger(EditMenu.class.getName()).log(Level.SEVERE, null, ex);
            writer.println("Error updating person: " + ex.getLocalizedMessage());
        }
    }

    private void addAddress() {
        try {
            writer.println("Add Address");
            String street = console.readLine("Enter street: ");
            String city = console.readLine("Enter city: ");
            String state = console.readLine("Enter state: ");
            String postalCode = console.readLine("Enter postal code: ");
            Address address = db.AddressDB().addAddress(person.id(), street, city, state, postalCode);
            writer.printf("Added id:%d %s %s, %s %s%n", address.addressId(), address.street(), address.city(),
                    address.state(), address.postalCode());
        } catch (DatabaseException ex) {
            writer.println("Error adding address: " + ex.getLocalizedMessage());
        }
    }

    private void editAddress() {
        String readLine = console.readLine("Enter id of address to edit: ");
        try {
            int addressId = Integer.parseInt(readLine);
            try {
                Optional<Address> optionalAddress = db.AddressDB().getAddress(person.id(), addressId);
                if (optionalAddress.isEmpty()) {
                    writer.println("Unable to find address id " + addressId);
                    return;
                }
                try {
                    Address address = optionalAddress.get();
                    String street = console.readLine("Enter street: ");
                    String city = console.readLine("Enter city: ");
                    String state = console.readLine("Enter state: ");
                    String postalCode = console.readLine("Enter postal code: ");
                    db.AddressDB().updateAddress(address.addressId(), person.id(), street, city, state, postalCode);
                } catch (DatabaseException ex) {
                    Logger.getLogger(EditMenu.class.getName()).log(Level.SEVERE, null, ex);
                    writer.println("Error updating person: " + ex.getLocalizedMessage());
                }
            } catch (DatabaseException ex) {
                writer.println("Error looking up user id " + addressId + ":" + ex.getLocalizedMessage());
            }
        } catch (NumberFormatException ex) {
            writer.println("Error, not a number");
        }
    }

    private void deleteAddress() {
        String readLine = console.readLine("Enter id of address to delete: ");
        try {
            int addressId = Integer.parseInt(readLine);
            try {
                Optional<Address> optionalAddress = db.AddressDB().getAddress(person.id(), addressId);
                if (optionalAddress.isEmpty()) {
                    writer.println("Unable to find address id " + addressId);
                    return;
                }
                Address address = optionalAddress.get();
                readLine = console.readLine("Delete %s? (y/n)", address.displayString());
                if ("y".equalsIgnoreCase(readLine)) {
                    db.AddressDB().deleteAddress(person.id(), addressId);
                    writer.printf("Deleted %s%n", address.displayString());
                }
            } catch (DatabaseException ex) {
                writer.println("Error looking up address id " + addressId + ":" + ex.getLocalizedMessage());
            }
        } catch (NumberFormatException ex) {
            writer.println("Error, not a number");
        }
    }
}
