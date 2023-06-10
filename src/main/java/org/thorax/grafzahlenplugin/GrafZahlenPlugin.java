package org.thorax.grafzahlenplugin;

import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.thorax.grafzahlenplugin.Term.TermSegment;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class GrafZahlenPlugin extends JavaPlugin {



    private static  FileConfiguration CONFIG;
    public static final Logger LOGGER = Bukkit.getLogger();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new OpenChestListener(), this);
        getServer().getPluginCommand("gzp").setExecutor(new GrafZahlenCommand());

        CONFIG = this.getConfig();
        ConfigurationSection storageSection =  CONFIG.getConfigurationSection("GrafZahlenStorage");
        if (storageSection == null) {
            storageSection = CONFIG.createSection("GrafZahlenStorage");
        }
        DataContainer.CHEST_SET = (Set<Chest>) storageSection.get("CHEST_SET");
        if (DataContainer.CHEST_SET == null) {
            LOGGER.log(Level.WARNING, "Could not read CHEST_SET");
            DataContainer.CHEST_SET = new HashSet<>();
        }
        DataContainer.SEGMENT_CHEST_MAP = (Map<TermSegment, Chest>) storageSection.get("SEGMENT_CHEST_MAP");
        if (DataContainer.SEGMENT_CHEST_MAP == null) {
            LOGGER.log(Level.WARNING, "Could not read SEGMENT_CHEST_MAP");
            DataContainer.SEGMENT_CHEST_MAP = new HashMap<>();
        }
        DataContainer.PLAYER_TERM_SEGMENT_MAP = (Map<Player, TermSegment>) storageSection.get("PLAYER_TERM_SEGMENT_MAP");
        if (DataContainer.PLAYER_TERM_SEGMENT_MAP == null) {
            LOGGER.log(Level.WARNING, "Could not read PLAYER_TERM_SEGMENT_MAP");
            DataContainer.PLAYER_TERM_SEGMENT_MAP = new HashMap<>();
        }
        DataContainer.CHEST_ALLOWED_PLAYER_MAP = (Map<Chest, Set<Player>>) storageSection.get("CHEST_ALLOWED_PLAYER_MAP");
        if (DataContainer.CHEST_ALLOWED_PLAYER_MAP == null) {
            LOGGER.log(Level.WARNING, "Could not read CHEST_ALLOWED_PLAYER_MAP");
            DataContainer.CHEST_ALLOWED_PLAYER_MAP = new HashMap<>();
        }
    }

    @Override
    public void onDisable() {

        ConfigurationSection storageSection = CONFIG.getConfigurationSection("GrafZahlenStorage");
        if(storageSection == null) {
            storageSection = CONFIG.createSection("GrafZahlenStorage");
        }
        storageSection.set("CHEST_SET", DataContainer.CHEST_SET);
        storageSection.set("SEGMENT_CHEST_MAP", DataContainer.SEGMENT_CHEST_MAP);
        storageSection.set("PLAYER_TERM_SEGMENT_MAP", DataContainer.PLAYER_TERM_SEGMENT_MAP);
        storageSection.set("CHEST_ALLOWED_PLAYER_MAP", DataContainer.CHEST_ALLOWED_PLAYER_MAP);

    }
}
