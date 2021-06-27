package net.afriskito.demo.addressbook.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.afriskito.demo.addressbook.data.Person;

public class PersonDB extends AbstractDB{

    PersonDB(Connection connection) {
        super(connection, "people",
                "CREATE TABLE people ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "firstName NVARCHAR(200), "
                + "lastName NVARCHAR(200));",
                "SELECT id, firstName, lastName FROM people LIMIT 1");
    }
    
    public Person addPerson(String firstName, String lastName) throws DatabaseException {
        String sql = "INSERT INTO people (firstName, lastName) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.execute();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return Person.create(id, firstName, lastName);
                }
            }
            throw new DatabaseException("Unable to get id for " + firstName + " " + lastName);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseException("Error creating " + firstName + " " + lastName);
        }
    }
    
    public Optional<Person> getPerson(int id) throws DatabaseException {
        String sql = "SELECT firstName, lastName FROM people WHERE id = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Person.create(
                            id, 
                            rs.getString("firstName"),
                            rs.getString("lastName")));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseException("Error getting person " + id);
        }
    }

    public void updatePerson(int id, String firstName, String lastName) throws DatabaseException {
        String sql = "UPDATE people SET firstName = ?, lastName = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setInt(3, id);
            statement.execute();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseException("Unable to update id " + id + " to " + firstName + " " + lastName);
        }
    }

    public void deletePerson(int id) throws DatabaseException {
        String sql = "DELETE FROM people WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseException("Error deleting person " + id);
        }
    }
    
    public int personCount() throws DatabaseException {
        String sql = "SELECT count(*) AS count FROM people";
        try (Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql))
        {
            if (rs.next()) {
                return rs.getInt("count");
            }
            throw new DatabaseException("Unable to get person count.");
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseException("Error getting person count: ", ex);
        }
    }

    public List<Person> getAllPeople() throws DatabaseException {
        String sql = "SELECT id, firstName, lastName FROM people ORDER BY id ASC";
        try (Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql))
        {
            List<Person> people = new ArrayList<>();
            while (rs.next()) {
                people.add(Person.create(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName")));
            }
            return people;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseException("Error getting person count: ", ex);
        }
    }
}
