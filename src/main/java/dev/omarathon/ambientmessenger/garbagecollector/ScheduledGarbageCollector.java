package dev.omarathon.ambientmessenger.garbagecollector;

import dev.omarathon.ambientmessenger.sql.Sql;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class ScheduledGarbageCollector extends RepeatingTask {
    private Sql sql;

    public ScheduledGarbageCollector(Sql sql, long cooldownAmount, TimeUnit cooldownUnit) {
        super(cooldownAmount, cooldownUnit);
        this.sql = sql;
    }

    @Override
    public void run() {
        try {
            sql.deleteExpiredMessages();
        }
        catch (SQLException e) {
            Bukkit.getLogger().severe("Garbage collection failed!");
            e.printStackTrace();
        }
    }
}
