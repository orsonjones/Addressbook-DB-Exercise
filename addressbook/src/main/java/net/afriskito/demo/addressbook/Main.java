package net.afriskito.demo.addressbook;

import java.io.Console;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import net.afriskito.demo.addressbook.data.Address;
import net.afriskito.demo.addressbook.data.Person;
import net.afriskito.demo.addressbook.database.Database;
import net.afriskito.demo.addressbook.database.DatabaseException;

public class Main {
    Database db;
    Console console = System.console();
    PrintWriter writer = console.writer();

    public Main(Database db) {
        this.db = db;
    }
    
    public static void main(String[] args) {
        try {
            new Main(new Database()).console();
        } catch (DatabaseException ex) {
            System.out.println("Error connecting to database: " + ex.getLocalizedMessage());
        }
    }

    private void console() {
        db.initialize();
        if (!db.status())
            writer.println("Database in bad state");
        writer.println("Addressbook");
        while (true) {
            writer.println();
            writer.println("1:Add Person  2:Edit Person  3:Delete Person  4:Count People  5:List People   0:Exit");
            String readLine = console.readLine("Enter number to perform action: ");
            try {
                switch (Integer.parseInt(readLine)) {
                    case 1:
                        add();
                        continue;
                    case 2:
                        edit();
                        continue;
                    case 3:
                        delete();
                        continue;
                    case 4:
                        count();
                        continue;
                    case 5:
                        list();
                        continue;
                    case 0:
                        return;
                }
            } catch (NumberFormatException ex) {}
            writer.println("Unrecognized option: " + readLine);
        }
    }

    private void add() {
        try {
            writer.println("Add Person");
            String firstName = console.readLine("Enter first name: ");
            String lastName = console.readLine("Enter last name: ");
            Person person = db.personDB().addPerson(firstName, lastName);
            writer.printf("Added id:%d %s%n", person.id(), person.displayString());
        } catch (DatabaseException ex) {
            writer.println("Error adding person: " + ex.getLocalizedMessage());
        }
    }

    private void edit() {
        String readLine = console.readLine("Enter id of person to edit: ");
        try {
            int id = Integer.parseInt(readLine);
            try {
                Optional<Person> optionalPerson = db.personDB().getPerson(id);
                if (optionalPerson.isEmpty()) {
                    writer.println("Unable to find user id " + id);
                    return;
                }
                new EditMenu(db, console, writer, optionalPerson.get()).edit();
            } catch (DatabaseException ex) {
                writer.println("Error looking up user id " + id + ":" + ex.getLocalizedMessage());
            }
        } catch (NumberFormatException ex) {
            writer.println("Error, not a number");
        }
    }

    private void delete() {
        String readLine = console.readLine("Enter id of person to delete: ");
        try {
            int id = Integer.parseInt(readLine);
            try {
                Optional<Person> optionalPerson = db.personDB().getPerson(id);
                if (optionalPerson.isEmpty()) {
                    writer.println("Unable to find user id " + id);
                    return;
                }
                Person person = optionalPerson.get();
                readLine = console.readLine("Delete %s? (y/n)", person.displayString());
                if ("y".equalsIgnoreCase(readLine)) {
                    for (Address address : db.AddressDB().getAddresses(id))
                        db.AddressDB().deleteAddress(id, address.addressId());
                    db.personDB().deletePerson(id);
                    writer.printf("Deleted %s%n", person.displayString());
                }
            } catch (DatabaseException ex) {
                writer.println("Error looking up user id " + id + ":" + ex.getLocalizedMessage());
            }
        } catch (NumberFormatException ex) {
            writer.println("Error, not a number");
        }
    }

    private void count() {
        try {
            writer.printf("There are %s people in the addressbook.%n", db.personDB().personCount());
        } catch (DatabaseException ex) {
            writer.println("Error getting count: " + ex.getLocalizedMessage());
        }
    }

    private void list() {
        try {
            for (Person person : db.personDB().getAllPeople())
                writer.printf("%d\t%s%n", person.id(), person.displayString());
        } catch (DatabaseException ex) {
            writer.println("Error getting count: " + ex.getLocalizedMessage());
        }
    }
}
