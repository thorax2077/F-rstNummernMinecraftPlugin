package org.thorax.grafzahlenplugin;

import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;


public final class GrafZahlenPlugin extends JavaPlugin {

    public static final Logger LOGGER = Bukkit.getLogger();

    public static Set<Chest> CHEST_SET = new HashSet<>();



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
