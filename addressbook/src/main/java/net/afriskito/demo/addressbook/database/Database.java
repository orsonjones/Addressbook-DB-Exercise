package net.afriskito.demo.addressbook.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.afriskito.demo.addressbook.Main;

public class Database {
    private static final String DB_FILE = "addressbook.db";
    private final Connection connection;
    private final PersonDB personDB;
    private final AddressDB addressDB;
    
    public Database() throws DatabaseException {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
            if (connection != null)
                this.connection = connection;
            else
                throw new DatabaseException("Unable to connect to " + DB_FILE);
            personDB = new PersonDB(connection);
            addressDB = new AddressDB(connection);
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseException("Error connecting to " + DB_FILE, ex);
        }
    }
    
    public void initialize() {
        personDB.initialize();
        addressDB.initialize();
    }
    
    /**
     * @return true if database is in a good state, false otherwise.
     */
    public boolean status() {
        return connection != null
            && personDB.status()
            && addressDB.status();
    }
    
    public PersonDB personDB() {
        return personDB;
    }
    
    public AddressDB AddressDB() {
        return addressDB;
    }
}
