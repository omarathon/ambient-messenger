package dev.omarathon.ambientmessenger;

import dev.omarathon.ambientmessenger.garbagecollector.ScheduledGarbageCollector;
import dev.omarathon.ambientmessenger.sql.Sql;
import dev.omarathon.ambientmessenger.sql.SqlConstants;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public final class AmbientMessenger implements Listener {
    private Sql sql;
    private ScheduledGarbageCollector garbageCollector;

    private AmbientMessenger() {

    }

    public AmbientMessenger(Connection sqlConnection, boolean broadcast) throws SQLException {
        this(sqlConnection, broadcast, "AmbientMessenger_Messages");
    }

    // throws SQLException if it failed to create the table
    public AmbientMessenger(Connection sqlConnection, boolean broadcast, String tableName) throws SQLException {
        sql = new Sql(sqlConnection, tableName);
        sql.createTableIfNotExist();
        beginGarbageCollector(2, TimeUnit.HOURS, broadcast);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ResultSet messages = null;
        try {
            messages = sql.getMessages(player.getUniqueId().toString());
            while (messages.next()) {
                player.sendMessage(messages.getString(SqlConstants.MESSAGE_FIELD));
                sql.deleteMessage(messages.getInt(SqlConstants.ID_FIELD));
            }
            messages.close();
        }
        catch (SQLException e) {
            Bukkit.getLogger().severe("Error occurred in either obtaining or processing ambient messages on new player join event for player with UUID " + player.getUniqueId().toString());
            e.printStackTrace();
            return;
        }
    }

    // throws SQLException if error adding the message to the table for an offline player
    public void sendMessage(OfflinePlayer offlinePlayer, String message, Timestamp expiry) throws SQLException {
        if (expiry.before(Timestamp.valueOf(LocalDateTime.now()))) {
            Bukkit.getLogger().warning("Not sending message: " + message + " to player with UUID " + offlinePlayer.getUniqueId() + " because it has already expired!");
            return;
        }
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            sql.addMessage(offlinePlayer.getUniqueId().toString(), message, expiry);
        }
        else {
            player.sendMessage(message);
        }
    }

    // throws SQLException it it failed
    public void emptyQueue() throws SQLException {
        sql.truncateTable();
    }

    public void beginGarbageCollector(long cooldownTime, TimeUnit cooldownUnit, boolean broadcast) {
        garbageCollector = new ScheduledGarbageCollector(sql, cooldownTime, cooldownUnit, broadcast);
        garbageCollector.begin();
    }

    public void disable() throws SQLException {
        garbageCollector.stop(); // stop garbage collector
        sql.close(); // close SQLConnection
    }

    public ScheduledGarbageCollector getGarbageCollector() {
        return garbageCollector;
    }

    public Sql getSql() {
        return sql;
    }

    public void setSql(Sql sql) {
        this.sql = sql;
    }
}