package me.staartvin.statz.language;

import me.staartvin.statz.datamanager.player.PlayerStat;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Every enumeration value has its path and default value.
 * To get the path, do {@link #getPath()}.
 * To get the default value, do {@link #getDefault()}.
 * <p>
 * For the defined value in the lang.yml config, use
 * {@link #getConfigValue(Object...)}.
 * String objects are expected as input.
 *
 * @author Staartvin and gomeow
 */
public enum StatisticDescription {

    /**
     * Joined the server &f{0}&9 times.
     */
    JOINS(PlayerStat.JOINS, "Joined the server &f{0}&9 times.", "&9Joined the server &f{0}&9 times."),

    /**
     * Died &f{0}&9 times on world '&7{1}&9'
     */
    DEATHS(PlayerStat.DEATHS, "Died &f{0}&9 times on world '&7{1}&9'.", "&9Died &f{0}&9 times."),

    /**
     * Caught &f{0}&9 &7{1}&9 times on world '{2}'
     */
    ITEMS_CAUGHT(PlayerStat.ITEMS_CAUGHT, "Caught &f{0}&9 &7{1}&9 times on world '{2}'.", "&9Caught" +
            " &f{0}&9 " + "items."),

    /**
     * Placed &f{0}&9 blocks of &7{1}&9 on world '&7{1}&9'
     */
    BLOCKS_PLACED(PlayerStat.BLOCKS_PLACED, "Placed &f{0}&9 blocks of &7{1}&9 on " +
            "world '{2}'.", "&9Placed &f{0}&9 blocks."),

    /**
     * Broke &f{0}&9 blocks of &7{1}&9 on world '{2}'.
     */
    BLOCKS_BROKEN(PlayerStat.BLOCKS_BROKEN, "Broke &f{0}&9 blocks of &7{1}&9 on " +
            "world " + "'{2}'.", "&9Broke &f{0}&9 blocks."),

    /**
     * Killed &f{0}&9 &7{1}&9s on world '{2}'.
     */
    KILLS_MOBS(PlayerStat.KILLS_MOBS, "Killed &f{0}&9 &7{1}&9s on world '{2}'.", "&9Killed &f{0}&9 " +
            "mobs."),

    /**
     * Killed &f{0}&9 &7{1}&9 times on world '{2}'.
     */
    KILLS_PLAYERS(PlayerStat.KILLS_PLAYERS, "Killed &f{0}&9 &7{1}&9 times on world '{2}'.",
            "&9Killed &f{0}&9" +
                    " players."),

    /**
     * Played for &f{0}&9 on world '&7{1}&9'.
     */
    TIME_PLAYED(PlayerStat.TIME_PLAYED, "Played for &f{0}&9 on world '&7{1}&9'.", "&9Played &f{0}&9."),

    /**
     * Eaten &f{0}&9 &7{1}&9 on world '{2}'.
     */
    FOOD_EATEN(PlayerStat.FOOD_EATEN, "Eaten &f{0}&9 &7{1}&9 on world '{2}'.", "&9Ate &f{0}&9 " +
            "consumables."),

    /**
     * Took &f{0}&9 points of damage by &7{1}&9 on world '{2}'.
     */
    DAMAGE_TAKEN(PlayerStat.DAMAGE_TAKEN, "Took &f{0}&9 points of damage by &7{1}&9 on world '{2}'" +
            ".", "&9Took " +
            "&f{0}&9 " +
            "points of damage."),

    /**
     * Shorn &f{0}&9 sheep on world '&7{1}&9'.
     */
    TIMES_SHORN(PlayerStat.TIMES_SHORN, "Shorn &f{0}&9 sheep on world '&7{1}&9'.", "&9Shorn &f{0}&9 " +
            "sheep."),

    /**
     * Travelled &f{0}&9 blocks on world '&7{1}&9' by {2}.
     */
    DISTANCE_TRAVELLED(PlayerStat.DISTANCE_TRAVELLED, "Travelled &f{0}&9 blocks on world " +
            "'&7{1}&9' by {2}.",
            "&9Travelled " +
                    "&f{0}&9 blocks."),

    /**
     * Crafted &f{0}&9 &7{1}&9 times on world '{2}'.
     */
    ITEMS_CRAFTED(PlayerStat.ITEMS_CRAFTED, "Crafted &f{0}&9 &7{1}&9 times on world '{2}'.",
            "&9Crafted " +
                    "&f{0}&9 items."),

    /**
     * Gained &f{0}&9 points of xp on world '&7{1}&9'
     */
    XP_GAINED(PlayerStat.XP_GAINED, "Gained &f{0}&9 points of xp on world '&7{1}&9'.", "&9Gained " +
            "&f{0}&9 " +
            "points of xp" +
            "."),

    /**
     * Voted &f{0}&9 times
     */
    VOTES(PlayerStat.VOTES, "Voted &f{0}&9 times.", "&9Voted &f{0}&9 times."),

    /**
     * Shot &f{0}&9 arrows on world '&7{1}&9'.
     */
    ARROWS_SHOT(PlayerStat.ARROWS_SHOT, "Shot &f{0}&9 arrows on world '&7{1}&9'" +
            ".", "&9Shot " +
            "&f{0}&9 " +
            "arrows."),

    /**
     * Slept &f{0}&9 times in a bed on world '&7{1}&9'.
     */
    ENTERED_BEDS(PlayerStat.ENTERED_BEDS, "Slept &f{0}&9 times in a bed on world '&7{1}&9'.",
            "&9Slept &f{0}&9 " +
                    "times."),

    /**
     * Performed &f{0}&9 &7{1}&9 times on world '{2}'.
     */
    COMMANDS_PERFORMED(PlayerStat.COMMANDS_PERFORMED, "Performed &f{0}&9 &7{1}&9 times on " +
            "world '{2}'.",
            "&9Performed " +
                    "&f" +
                    "&f{0}&9 commands."),

    /**
     * Kicked &f{0}&9 times on world '&7{1}&9' with reason '{2}'.
     */
    TIMES_KICKED(PlayerStat.TIMES_KICKED, "Kicked &f{0}&9 times on world '&7{1}&9' with reason " +
            "'{2}'.",
            "&9Kicked " +
                    "&f{0}&9 " +
                    "times."),

    /**
     * Broken &f{0}&9 &7{1}&9 times on world '{2}'.
     */
    TOOLS_BROKEN(PlayerStat.TOOLS_BROKEN, "Broken &f{0}&9 &7{1}&9 times on world '{2}'.",
            "&9Broken " +
                    "&f{0}&9 tools."),

    /**
     * Thrown &f{0}&9 eggs on world '&7{1}&9'.
     */
    EGGS_THROWN(PlayerStat.EGGS_THROWN, "Thrown &f{0}&9 eggs on world '&7{1}&9'.", "&9Thrown &f{0}&9" +
            " eggs."),

    /**
     * Changed from &f{0}&9 to &7{1}&9 {2} times.
     */
    WORLDS_CHANGED(PlayerStat.WORLDS_CHANGED, "Changed from &f{0}&9 to &7{1}&9 {2} times.",
            "&9Changed worlds " +
                    "&f{0}&9 times."),

    /**
     * Filled &f{0}&9 buckets on world '&7{1}&9'.
     */
    BUCKETS_FILLED(PlayerStat.BUCKETS_FILLED, "Filled &f{0}&9 buckets on world '&7{1}&9'.",
            "&9Filled &f{0}&9 " +
                    "buckets."),

    /**
     * Emptied &f{0}&9 buckets on world '&7{1}&9'.
     */
    BUCKETS_EMPTIED(PlayerStat.BUCKETS_EMPTIED, "Emptied &f{0}&9 buckets on world '&7{1}&9'.",
            "&9Emptied " +
                    "&f{0}&9 " +
                    "buckets."),

    /**
     * Dropped &f{0}&9 &7{1}&9 times on world '{2}'.
     */
    ITEMS_DROPPED(PlayerStat.ITEMS_DROPPED, "Dropped &f{0}&9 &7{1}&9 times on world '{2}'.",
            "&9Dropped " +
                    "&f{0}&9 " +
                    "items."),

    /**
     * Picked up &f{0}&9 &7{1}&9 times on world '{2}'.
     */
    ITEMS_PICKED_UP(PlayerStat.ITEMS_PICKED_UP, "Picked up &f{0}&9 &7{1}&9 times on world '{2}'" +
            ".", "&9Picked " +
            "up " +
            "&f{0}&9 " +
            "items."),

    /**
     * Teleported from &f{0}&9 to &7{1}&9 {2} times because of {3}.
     */
    TELEPORTS(PlayerStat.TELEPORTS, "Teleported from &f{0}&9 to &7{1}&9 {2} times because of {3}.",
            "&9Teleported " +
                    "&f{0}&9 " +
                    "times."),

    /**
     * Traded with &f{0}&9 Villagers on world '&7{1}&9' for item {2}.
     */
    VILLAGER_TRADES(PlayerStat.VILLAGER_TRADES, "Traded with &f{0}&9 Villagers on world " +
            "'&7{1}&9' for item {2}" +
            ".",
            "&9Traded " +
                    "with &f{0}&9 Villagers."),;

    private static FileConfiguration file;
    private String highDetailDesc, totalDesc;
    private PlayerStat relatedPlayerStat;

    /**
     * Statistic enum constructor.
     */
    StatisticDescription(PlayerStat correspondingStat, String highDetailDesc, String totalDesc) {
        this.highDetailDesc = highDetailDesc;
        this.totalDesc = totalDesc;
        this.relatedPlayerStat = correspondingStat;
    }

    /**
     * Set the {@code FileConfiguration} to use.
     *
     * @param config The config to set.
     */
    public static void setFile(final FileConfiguration config) {
        file = config;
    }

    /**
     * Get the value in the config with certain arguments.
     *
     * @param args arguments that need to be given. (Can be null)
     * @return value in config or otherwise default value
     */
    private String getConfigValue(String description, final Object... args) {

        if (description == null) {
            return null;
        }

        String value = ChatColor.translateAlternateColorCodes('&', description);

        if (args == null)
            return value;
        else {
            if (args.length == 0)
                return value;

            for (int i = 0; i < args.length; i++) {
                value = value.replace("{" + i + "}", args[i].toString());
            }
        }

        return value;
    }

    /**
     * Get the default value of the path.
     *
     * @return The default value of the path.
     */
    public String getHighDetailDescription(final Object... args) {
        return getConfigValue(highDetailDesc, args);
    }

    public String getTotalDescription(final Object... args) {
        return getConfigValue(totalDesc, args);
    }

    /**
     * Get the PlayerStat enum that is related to this statistic description.
     *
     * @return {@link PlayerStat}
     */
    public PlayerStat getRelatedPlayerStat() {
        return this.relatedPlayerStat;
    }

    public String getStringIdentifier() {
        return relatedPlayerStat.getTableName();
    }

    public enum DescriptionDetail {HIGH, MEDIUM, LOW}
}
