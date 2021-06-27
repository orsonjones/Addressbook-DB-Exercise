package net.afriskito.demo.addressbook.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.afriskito.demo.addressbook.data.Address;

public class AddressDB extends AbstractDB {

    AddressDB(Connection connection) {
        super(connection, "addresses",
                "CREATE TABLE addresses ("
                + "addressId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "personId INTEGER NOT NULL, "
                + "street NVARCHAR(200), "
                + "city NVARCHAR(200), "
                + "state NVARCHAR(200), "
                + "postalCode NVARCHAR(200));",
                "SELECT personId, addressId, street, city, state, postalCode FROM addresses LIMIT 1");
    }
    
    public Address addAddress(int personId, String street, String city, String state, String postalCode) throws DatabaseException {
        String sql = "INSERT INTO addresses (personId, street, city, state, postalCode) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, personId);
            statement.setString(2, street);
            statement.setString(3, city);
            statement.setString(4, state);
            statement.setString(5, postalCode);
            statement.execute();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return Address.create(id, personId, street, city, state, postalCode);
                }
            }
            throw new DatabaseException("Unable to get id for new address");
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseException("Error creating address");
        }
    }
    
    public Optional<Address> getAddress(int personId, int addressId) throws DatabaseException {
        String sql = "SELECT street, city, state, postalCode FROM addresses WHERE personId = ? AND addressId = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, personId);
            statement.setInt(2, addressId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Address.create(
                            addressId,
                            personId,
                            rs.getString("street"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getString("postalCode")));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseException("Error getting address " + addressId);
        }
    }

    public List<Address> getAddresses(Integer personId) throws DatabaseException {
        String sql = "SELECT addressId, street, city, state, postalCode FROM addresses WHERE personId = ? ORDER BY addressId ASC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, personId);
            try (ResultSet rs = statement.executeQuery()) {
                List<Address> addresses = new ArrayList<>();
                while (rs.next()) {
                    addresses.add(Address.create(
                            rs.getInt("addressId"),
                            personId,
                            rs.getString("street"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getString("postalCode")));
                }
                return addresses;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseException("Error getting person count: ", ex);
        }
    }

    public void updateAddress(Integer addressId, Integer personId, String street, String city, String state, String postalCode)
            throws DatabaseException
    {
        String sql = "UPDATE addresses SET street = ?, city = ?, state = ?, postalCode = ? WHERE personId = ? AND addressId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, street);
            statement.setString(2, city);
            statement.setString(3, state);
            statement.setString(4, postalCode);
            statement.setInt(5, personId);
            statement.setInt(6, addressId);
            statement.execute();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseException("Unable to update address id " + addressId);
        }
    }

    public void deleteAddress(Integer personId, int addressId) throws DatabaseException {
        String sql = "DELETE FROM addresses WHERE personId = ? AND addressId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, personId);
            statement.setInt(2, addressId);
            statement.execute();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseException("Error deleting person " + personId);
        }
    }
}
