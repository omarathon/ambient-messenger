[![Latest Release](https://img.shields.io/github/release/omarathon/ambient-messenger.svg)](https://github.com/omarathon/ambient-messenger/releases/latest) [![](https://jitpack.io/v/omarathon/ambient-messenger.svg)](https://jitpack.io/#omarathon/ambient-messenger)

# Ambient Messenger

A Minecraft Server API plugin which allows the sending of "ambient" messages, i.e messages to players whether they're online or offline.

If the [OfflinePlayer](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/OfflinePlayer.html) object is online, the message is simply sent as normal.

However, if it's offline, it's stored in a MySQL table, and retrieved when they join back to the server. Then, if it hasn't expired, it's sent to them when they have joined back.

There is in-built garbage collection on the SQL table, which by default removes expired messages from the table every **2 hours**, and initially when the [AmbientMessenger](src/main/java/dev/omarathon/ambientmessenger/AmbientMessenger.java) is constructed.

## Usage

Firstly, add the API as a dependency. It's hosted on [JitPack](https://jitpack.io/#omarathon/ambient-messenger/).

Since the API listens to ``onPlayerJoin`` events, one must register the [AmbientMessenger](src/main/java/dev/omarathon/ambientmessenger/AmbientMessenger.java) Listener class as a listener. 
This can be done like so:

```java
AmbientMessenger ambientMessenger = new AmbientMessenger(#);
getServer().getPluginManager().registerEvents(ambientMessenger, this);
// do what you want with your AmbientMessenger...
```

(where **#** is an SQL [Connection](https://docs.oracle.com/javase/7/docs/api/java/sql/Connection.html) object).

When constructing an AmbientMessenger, one must provide to it their SQL [Connection](https://docs.oracle.com/javase/7/docs/api/java/sql/Connection.html) object, from which SQL queries shall be executed from, and additionally the name of the table for the plugin to use. By default, it uses a table named **AmbientMessenger_Messages**.

Once you have constructed an AmbientMessenger and registered it as a listener, you may use the ``sendMessage`` function to send an ambient message to a player. You must provide the [OfflinePlayer](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/OfflinePlayer.html) object, the message String, and the SQL [Timestamp](https://docs.oracle.com/javase/8/docs/api/java/sql/Timestamp.html) for when the message shall expire.

You can use use the ``beginGarbageCollector`` method to reset the garbage collector to run on a given cooldown, by passing it the cooldown time as a long and the cooldown time unit as a [TimeUnit](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/TimeUnit.html). By default, the cooldown for garbage collection is set to **2 hours**.

## Functionality

Below is a tl;dr for the main methods provided by the [AmbientMessenger](src/main/java/dev/omarathon/ambientmessenger/AmbientMessenger.java):

- ``sendMessage`` - Send an ambient message to a given [OfflinePlayer](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/OfflinePlayer.html). You must provide the [OfflinePlayer](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/OfflinePlayer.html) to send it to, the String of the message and the SQL [Timestamp](https://docs.oracle.com/javase/8/docs/api/java/sql/Timestamp.html) for when the message will expire. If the [OfflinePlayer](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/OfflinePlayer.html) is online, it's sent to them instantly as normal. Otherwise, it's sent to them when they re-join the server, if it has not expired.
- ``beginGarbageCollector`` - Constructs a [ScheduledGarbageCollector](src/main/java/dev/omarathon/ambientmessenger/garbagecollector/ScheduledGarbageCollector.java) and sets it running with a given cooldown. Note that it's ran as soon as this method is called, and then it will be ran indefinitely on a cooldown as specified.
- ``emptyQueue`` - An additional admin method for if you'd like to cancel all of the ambient messages in the queue (essentially empties the internal database table).

