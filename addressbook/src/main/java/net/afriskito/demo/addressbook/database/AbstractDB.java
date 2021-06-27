package net.afriskito.demo.addressbook.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractDB {
    protected final Connection connection;
    private final String tableName;
    private final String tableCreateSql;
    private final String tableCheckSql;

    public AbstractDB(Connection connection, String tableName, String tableCreateSql, String tableCheckSql) {
        this.connection = connection;
        this.tableName = tableName;
        this.tableCreateSql = tableCreateSql;
        this.tableCheckSql = tableCheckSql;
    }
    
    public void initialize() {
        if (!tableExists())
            createTable();
    }
    
    /**
     * @return true if database is in a good state, false otherwise.
     */
    public boolean status() {
        if (connection == null)
            return false;
        if (!tableWorks())
            return false;
        return true;
    }
    
    private boolean tableWorks() {
        if (!tableExists())
            return false;
        try (Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(tableCheckSql))
        {
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private boolean tableExists() {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
        try (Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql))
        {
            if (rs.next())
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private void createTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute(tableCreateSql);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
