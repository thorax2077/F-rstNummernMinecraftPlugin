package org.thorax.grafzahlenplugin;

import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.thorax.grafzahlenplugin.Term.TermSegment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class GrafZahlenPlugin extends JavaPlugin {

    private static  FileConfiguration CONFIG;
    public static final Logger LOGGER = Bukkit.getLogger();

    public static DataContainer GrafZahlenData;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(DataContainer.class, "DataContainer");
        getServer().getPluginManager().registerEvents(new OpenChestListener(), this);
        getServer().getPluginCommand("gzp").setExecutor(new GrafZahlenCommand());
        try {
            FileConfiguration fileConfiguration = getConfig();
            File file = new File("GrafZahlenPluginData.yaml");
            if (!file.createNewFile()) {
                LOGGER.log(Level.INFO, "GrafZahlenPluginData existiert schon");
            }
            fileConfiguration.load("GrafZahlenPluginData.yaml");
            if (!fileConfiguration.contains("DataContainer")) {
                LOGGER.log(Level.WARNING, "DataContainer Path existiert nicht in GrafZahlenPluginData.yaml");
            }
            GrafZahlenData = fileConfiguration.getSerializable("DataContainer", DataContainer.class);
            if (GrafZahlenData == null) {
                LOGGER.log(Level.WARNING, "DataContainer could not be desirialized");
                GrafZahlenData = new DataContainer();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        FileConfiguration fileConfiguration = getConfig();
        try {
            fileConfiguration.load("GrafZahlenPluginData.yaml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        fileConfiguration.set("DataContainer", GrafZahlenData);

        GrafZahlenData = (DataContainer) fileConfiguration.get("DataContainer");
        LOGGER.log(Level.INFO, GrafZahlenData.getChestAllowedPlayerMap().toString());
        LOGGER.log(Level.INFO, GrafZahlenData.getChestSet().toString());
        LOGGER.log(Level.INFO, GrafZahlenData.getPlayerTermSegmentHashMap().toString());
        LOGGER.log(Level.INFO, GrafZahlenData.getSegmentChestMap().toString());
        try {
            fileConfiguration.save("GrafZahlenPluginData.yaml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
