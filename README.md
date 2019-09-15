# Ambient Messenger

A Minecraft Server API plugin which allows the sending of "ambient" messages, i.e messages to players whether they're online or offline.

If the player is online, the message is simply sent as normal.

However, if they're offline, it's stored in a MySQL table, and retrieved when they join back to the server. Then, if it hasn't expired, it's sent to them when they have joined back.

## Usage

Firstly, add the API as a dependency. It's hosted on JitPack.

Since the API listens to ``onPlayerJoin`` events, one must register the AmbientMessenger Listener class as a listener.

When constructing an AmbientMessenger, one must provide to it their SQL Connection object, from which SQL queries shall be executed from, and additionally the name of the table for the plugin to use. By default, it uses a table named **AmbientMessenger_Messages**.

Once you have constructed an AmbientMessenger and registered it as a listener, you may use the ``sendMessage`` function to send an ambient message to a player. You must provide the Player object, the message String, and the SQL Timestamp for when the message shall expire.

It's important that you use the ``beginGarbageCollector`` method to begin removing messages in the SQL Table which have expired. By default, the cooldown for garbage collection is set to 2 hours, however you may specify this cooldown by passing ``beginGarbageCollector`` the cooldown time as a ``long`` and the cooldown time unit as a ``TimeUnit``.

## Functionality

Below is a tl;dr for the main methods provided by the ``AmbientMessenger``:

- ``sendMessage`` - send an ambient message to a given Player. You must provide the ``Player`` to send it to, the ``String`` of the message and the SQL ``TimeStamp`` for when the message will expire. If the player is online, it's sent to them instantly as normal. Otherwise, it's sent to them when they re-join the server, if it has not expired.
- ``beginGarbageCollector`` - constructs a garbage collector and sets it running with either the given cooldown or a default one. Note that the garbage collector is ran as soon as this method is called, and then it will be ran indefinitely on a cooldown as specified.
- ``emptyQueue`` - an additional admin method for if you'd like to cancel all of the ambient messages in the queue (essentially empties the internal database table).

