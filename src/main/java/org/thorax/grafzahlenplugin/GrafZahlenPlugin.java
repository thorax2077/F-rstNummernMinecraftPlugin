package org.thorax.grafzahlenplugin;

import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.thorax.grafzahlenplugin.Term.TermSegment;

import java.util.*;
import java.util.logging.Logger;


public final class GrafZahlenPlugin extends JavaPlugin {

    public static final Logger LOGGER = Bukkit.getLogger();

    public static final Set<Chest> CHEST_SET = new HashSet<>();

    public static final Map<TermSegment, Chest> SEGMENT_CHEST_MAP = new HashMap<TermSegment, Chest>();

    public static final Map<Player, TermSegment> PLAYER_TERM_SEGMENT_MAP = new HashMap<Player, TermSegment>();

    public static final Map<Chest, Set<Player>> CHEST_ALLOWED_PLAYER_MAP = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new OpenChestListener(), this);
        getServer().getPluginCommand("gzp").setExecutor(new GrafZahlenCommand());
    }

    @Override
    public void onDisable() {

        getServer().getPluginManager().disablePlugin(this);

    }
}
