package me.staartvin.statz.datamanager;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.DatabaseConnector;
import me.staartvin.statz.database.MySQLConnector;
import me.staartvin.statz.database.SQLiteConnector;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.database.datatype.Table;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.language.DescriptionMatcher;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * This class handles all requests for data of a player. Whenever you want to obtain data about a player (or update
 * data about a player), you will need to use this manager.
 * <br>
 * <br>
 * <h2>Requesting data</h2>
 * When you request data about a player, this manager will ask the caching manager for data about this player. If
 * there is no cached data yet, you'll need to load the user in the cache first. Note that retrieving data from the
 * database should be done asynchronously, as it blocks the thread it's working on.
 * <br>
 * <br>
 * <h2>Updating data</h2>
 * To update a player's data, you can use the
 * {@link #setPlayerInfo(UUID, me.staartvin.statz.datamanager.player.PlayerStat, Query)} method. Note that the
 * update will be cached immediately and will be sent to the database after a while. Whenever you update the player's
 * data, the changes will immediately appear in the cache.
 */
public class DataManager {

    private final Statz plugin;

    public DataManager(final Statz instance) {
        plugin = instance;

        // Load SQL connector
        if (instance.getConfigHandler().isMySQLEnabled()) {
            instance.getLogger().info("Using MySQL database!");
            instance.setDatabaseConnector(new MySQLConnector(instance));
        } else {
            instance.getLogger().info("Using SQLite database!");
            instance.setDatabaseConnector(new SQLiteConnector(instance));
        }

        // Load tables into hashmap
        instance.getDatabaseConnector().loadTables();

        // Create and load database
        instance.getDatabaseConnector().load();
    }

    /**
     * This method will obtain all data that is known about a player. Note that this data is
     * cached for performance reasons. When a player is not loaded into the cache, the method will return null. The
     * player should first be loaded into the cache.
     *
     * @param uuid UUID of the player to search for
     * @return a {@link PlayerInfo} class that contains the data of a player or null if no the player was not loaded
     * in the cache yet.
     * @throws IllegalArgumentException if the given uuid is null
     */
    public PlayerInfo getPlayerInfo(final UUID uuid)
            throws IllegalArgumentException {

        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null.");
        }

        if (!this.isPlayerLoaded(uuid)) {
            return null;
        }

        return plugin.getCachingManager().getCachedPlayerData(uuid);
    }

    /**
     * Get all known data of a player for a given statistic. This is different from {@link #getPlayerInfo(UUID)} as
     * that method grabs all data of a player, while this method only retrieves data about a given statistic.
     *
     * @param uuid     UUID of the player
     * @param statType Type of statistic to get data of
     * @return PlayerInfo object with data of the requested player and the given statistic
     * @throws IllegalArgumentException if the given uuid is null.
     */
    public PlayerInfo getPlayerInfo(UUID uuid, PlayerStat statType) throws IllegalArgumentException {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null.");
        }

        if (!this.isPlayerLoaded(uuid, statType)) {
            return null;
        }

        PlayerInfo info = this.getPlayerInfo(uuid);

        if (info == null) {
            return null;
        }

        // Create a new PlayerInfo object that only has the data of a given statistic.
        PlayerInfo newInfo = new PlayerInfo(uuid);

        List<Query> queriesStored = info.getDataOfPlayerStat(statType);

        // Don't store data that is null or empty.
        if (queriesStored != null && !queriesStored.isEmpty()) {
            newInfo.setData(statType, info.getDataOfPlayerStat(statType));
        }

        return newInfo;
    }

    /**
     * Get data of a player for a given statistic. This method will obtain 'fresh' data from the database, meaning
     * that it will ignore cached data. Hence, this method will block the thread it is ran on. It is therefore
     * advised to run this method asynchronously.
     * <br>
     * <br>
     * It is recommended to use another method to obtain the data of a player when it is not loaded into the cache
     * yet: using {@link #loadPlayerData(UUID, PlayerStat)}, the retrieved data will also be stored in the cache, so
     * you can retrieve it the next time without making an expensive call to the database.
     *
     * @param uuid     UUID of the player.
     * @param statType Type of statistic.
     * @return fresh player data in the form of a {@link PlayerInfo} object.
     * @throws IllegalArgumentException if the given uuid is null.
     */
    public PlayerInfo getFreshPlayerInfo(UUID uuid, PlayerStat statType) throws IllegalArgumentException {

        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null.");
        }

        Table table = DatabaseConnector.getTable(statType);

        List<Query> databaseRows = plugin.getDatabaseConnector().getObjects(table,
                new RowRequirement("uuid", uuid.toString()));

        // Set specification of query, so we know how we can read data.
        databaseRows.forEach(query -> query.setSpecification(statType.getSpecification()));

        PlayerInfo info = new PlayerInfo(uuid);

        info.setData(statType, databaseRows);


        return info;
    }

    /**
     * Get Player info like {@link #getPlayerInfo(UUID, PlayerStat)}, but check for additional conditions.
     * Let's say you want to get all the player info for a player on world 'world'. You would call this method with
     * the player's UUID,
     * provide the statType and add a Query condition with StatzUtil.makeQuery().
     *
     * @param uuid         UUID of the player
     * @param statType     Type of stat to get player info of.
     * @param requirements Extra conditions that need to apply. See {@link RowRequirement}.
     * @return a {@link PlayerInfo} object.
     */
    public PlayerInfo getPlayerInfo(final UUID uuid, final PlayerStat statType, RowRequirement... requirements) {
        PlayerInfo info = this.getPlayerInfo(uuid, statType);

        // There are no requirement, so we don't need to check any data.
        if (requirements == null || requirements.length == 0) {
            return info;
        }

        // Remove query if it does not meet the given requirements.
        info.getDataOfPlayerStat(statType).removeIf(query -> !query.meetsAllRequirements(Arrays.asList(requirements)));

        return info;
    }

    /**
     * Check whether there is cached data of a player. If not, the player should first be loaded before trying to
     * obtain data. Note that loading player data is asynchronous!
     *
     * @param uuid UUID of the player
     * @return true if there is cached data about a player, false otherwise.
     */
    public boolean isPlayerLoaded(UUID uuid) {
        return plugin.getCachingManager().isPlayerCacheLoaded(uuid);
    }

    /**
     * Check whether there is cached data of a given statistic for a player. If not, the player should first be
     * loaded before trying to obtain data. Note that loading player data is asynchronous!
     *
     * @param uuid     UUID of the player
     * @param statType Type of statistic
     * @return true if there is cached data regarding the given statistic loaded in the cache, false otherwise.
     */
    public boolean isPlayerLoaded(UUID uuid, PlayerStat statType) {
        return plugin.getCachingManager().isPlayerCacheLoaded(uuid, statType);
    }

    /**
     * Load the data of a player of a given statistic into the cache, so it can be retrieved.
     * Note that this method will block the thread it is on and so it should be run asynchronously.
     *
     * @param uuid     UUID of the player
     * @param statType Type of statistic.
     * @return the PlayerInfo data that was loaded into the cache, ready for use.
     * @throws IllegalArgumentException if the given uuid is null
     */
    public PlayerInfo loadPlayerData(UUID uuid, PlayerStat statType) throws IllegalArgumentException {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null.");
        }

        // Retrieve info from database.
        PlayerInfo info = this.getFreshPlayerInfo(uuid, statType);

        // Put new data into cache.
        plugin.getCachingManager().addCachedData(uuid, info);

        return info;
    }

    /**
     * Load all data of a player into the cache, so it can be retrieved.
     * Note that this method will block the thread it is on and so it should be run asynchronously.
     *
     * @param uuid UUID of the player
     * @return the PlayerInfo data that was loaded into the cache, ready for use.
     * @throws IllegalArgumentException if the given uuid is null
     */
    public PlayerInfo loadPlayerData(UUID uuid) throws IllegalArgumentException {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null.");
        }

        PlayerInfo info = new PlayerInfo(uuid);

        // Load all data of a player
        for (PlayerStat statType : PlayerStat.values()) {
            PlayerInfo freshPlayerInfo = getFreshPlayerInfo(uuid, statType);

            info.setData(statType, freshPlayerInfo.getDataOfPlayerStat(statType));
        }

        // Put new data into cache.
        plugin.getCachingManager().registerCachedData(uuid, info);

        return info;
    }

    /**
     * Update a player's data with a given query. Note that it may take a while before the data actually reaches the
     * database (due to the pooling system). Passing a query with new data means it will be added to the already
     * existing value of the data, e.g. you cannot overwrite the data, merely add to it. Hence, updates should be
     * relative, not absolute.
     *
     * @param uuid        UUID of the player
     * @param statType    Type of statistic the given query belongs to
     * @param updateQuery Query that contains updated data.
     */
    public void setPlayerInfo(final UUID uuid, final me.staartvin.statz.datamanager.player.PlayerStat statType,
                              Query updateQuery) {

        // If the query does not have a UUID, add it in manually.
        if (!updateQuery.hasColumn("uuid")) {
            updateQuery.setValue("uuid", uuid);
        }

        // Add query to pool of updates
        plugin.getUpdatePoolManager().registerNewUpdateQuery(updateQuery, statType, uuid);
    }

    /**
     * Convenience method for updating the data of a player via a playerinfo object.
     *
     * @param info Info object to use as data holder.
     * @See {@link #setPlayerInfo(UUID, PlayerStat, Query)} for more info.
     */
    public void setPlayerInfo(PlayerInfo info) {

        UUID uuid = info.getUUID();

        for (Map.Entry<PlayerStat, List<Query>> entry : info.getRowsPerStatistic().entrySet()) {
            for (Query query : entry.getValue()) {
                this.setPlayerInfo(uuid, entry.getKey(), query);
            }
        }
    }

    public void sendStatisticsList(CommandSender sender, String playerName, UUID uuid, int pageNumber, List<PlayerStat> list) {
        List<String> messages = new ArrayList<>();
        List<BaseComponent[]> messagesSpigot = new ArrayList<>();
        boolean canShowSpigotMessages = sender instanceof Player;

        for (PlayerStat statType : list) {
            if (statType.equals(PlayerStat.PLAYERS)) {
                continue;
            }

            PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, statType);
            if (info == null) {
                info = plugin.getDataManager().loadPlayerData(uuid, statType);
            }

            if (info.getDataOfPlayerStat(statType).isEmpty()) {
                continue;
            }

            String messageString = DescriptionMatcher.getTotalDescription(info, statType);

            if (canShowSpigotMessages) {
                BaseComponent[] spigotMessage = new ComponentBuilder(messageString)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz list " + playerName + " " + statType))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click for more info about " + statType)
                                .color(ChatColor.GOLD.asBungee()).create()))
                        .create();
                messagesSpigot.add(spigotMessage);
            } else {
                messages.add(messageString);
            }
        }

        int messagesPerPage = 8;
        int pages = (int) Math.ceil((double) (canShowSpigotMessages ? messagesSpigot.size() : messages.size()) / messagesPerPage);
        if (pageNumber >= pages || pageNumber < 0) pageNumber = 0;

        sender.sendMessage(ChatColor.YELLOW + "---------------- [Statz of " + playerName + "] ----------------");
        for (int j = 0; j < messagesPerPage; j++) {
            int index = (pageNumber * messagesPerPage) + j;
            if (canShowSpigotMessages) {
                if (index >= messagesSpigot.size()) break;
                Player player = (Player) sender;
                player.spigot().sendMessage(messagesSpigot.get(index));
            } else {
                if (index >= messages.size()) break;
                sender.sendMessage(messages.get(index));
            }
        }

        BaseComponent[] pageClicker = new ComponentBuilder("<<< ")
                .color(ChatColor.GOLD.asBungee())
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz list " + playerName + " " + Math.max(pageNumber, 0)))
                .append("Page ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_AQUA.asBungee())
                .append(String.valueOf(pages == 0 ? pageNumber : pageNumber + 1), ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN.asBungee())
                .append(" of " + pages, ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_AQUA.asBungee())
                .append(" >>>", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GOLD.asBungee())
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz list " + playerName + " " + Math.min(pageNumber + 1, pages - 1))).create();

        if (canShowSpigotMessages) {
            Player player = (Player) sender;
            player.spigot().sendMessage(pageClicker);
        } else {
            sender.sendMessage(ChatColor.GOLD + "<<< " + ChatColor.DARK_AQUA + "Page " + ChatColor.GREEN
                    + (pageNumber + 1) + ChatColor.DARK_AQUA + " of " + pages + ChatColor.GOLD + " >>>");
        }
    }

    /**
     * Get a list of players that are stored in the connected database.
     *
     * @return a list of UUIDs corresponding to the players that are in the database.
     */
    public CompletableFuture<List<UUID>> getStoredPlayers() {

        return CompletableFuture.supplyAsync(() -> {
            List<Query> rows =
                    plugin.getDatabaseConnector().getObjects(DatabaseConnector.getTable(PlayerStat.PLAYERS));

            if (rows == null) {
                return new ArrayList<UUID>();
            }

            return rows.stream().map(Query::getUUID).collect(Collectors.toList());
        });
    }
}
