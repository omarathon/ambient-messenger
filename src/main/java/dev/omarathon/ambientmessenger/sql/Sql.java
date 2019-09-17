package dev.omarathon.ambientmessenger.sql;

import java.sql.*;

public class Sql {
    private Connection connection;
    private String table;

    private Sql() {

    }

    public Sql(Connection connection, String table) {
        this.connection = connection;
        this.table = table;
    }

    public void createTableIfNotExist() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " + table + " (" + SqlConstants.ID_FIELD + " INT NOT NULL IDENTITY PRIMARY KEY, " + SqlConstants.UUID_FIELD + " CHAR(36) NOT NULL, " + SqlConstants.MESSAGE_FIELD + " VARCHAR(65535) NOT NULL, " + SqlConstants.EXPIRY_FIELD + " TIMESTAMP NOT NULL)"
        );
        statement.execute();
    }

    public void addMessage(String uuid, String message, Timestamp expiry) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO " + table + " VALUES (NULL,?,?,?)"
        );
        statement.setString(1, uuid);
        statement.setString(2, message);
        statement.setTimestamp(3, expiry);
        statement.execute();
    }

    public ResultSet getMessages(String uuid) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM " + table + " WHERE " + SqlConstants.UUID_FIELD + "=?"
        );
        statement.setString(1, uuid);
        return statement.executeQuery();
    }

    public void deleteMessage(int id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM " + table + " WHERE " + SqlConstants.ID_FIELD + "=?"
        );
        statement.setInt(1, id);
        statement.execute();
    }

    public void deleteExpiredMessages() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM " + table + " WHERE " + SqlConstants.EXPIRY_FIELD + " >= CURRENT_TIMESTAMP"
        );
        statement.execute();
    }

    public void truncateTable() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "TRUNCATE TABLE " + table
        );
        statement.execute();
    }

    public Connection getConnection() {
        return connection;
    }

    public String getTable() {
        return table;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
