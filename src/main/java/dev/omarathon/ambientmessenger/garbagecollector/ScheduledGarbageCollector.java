package dev.omarathon.ambientmessenger.garbagecollector;

import dev.omarathon.ambientmessenger.sql.Sql;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class ScheduledGarbageCollector extends RepeatingTask {
    private Sql sql;
    private boolean broadcast;

    public ScheduledGarbageCollector(Sql sql, long cooldownAmount, TimeUnit cooldownUnit) {
        this(sql, cooldownAmount, cooldownUnit, false);
    }

    public ScheduledGarbageCollector(Sql sql, long cooldownAmount, TimeUnit cooldownUnit, boolean broadcast) {
        super(cooldownAmount, cooldownUnit);
        this.sql = sql;
        this.broadcast = broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public boolean getBroadcast() {
        return broadcast;
    }

    @Override
    public void run() {
        try {
            if (broadcast) Bukkit.getLogger().info("[AmbientMessenger Debug] Garbage collector ran!");
            sql.deleteExpiredMessages();
        }
        catch (SQLException e) {
            Bukkit.getLogger().severe("Garbage collection failed!");
            e.printStackTrace();
        }
    }
}
