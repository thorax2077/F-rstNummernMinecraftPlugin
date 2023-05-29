package org.thorax.grafzahlenplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.logging.Level;

public class OpenChestListener implements Listener {

    @EventHandler
    public void onRightClickChestBlock(PlayerInteractEvent ev) {
        try {
            if (!ev.getAction().isRightClick()) {
                return;
            }
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "[GrapfZahlen] ineraction event | right click");
            Block block = ev.getClickedBlock();
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "Block Instance " + block.getType());
            switch (block.getType()) {
                case CHEST:
                case ENDER_CHEST:
                case TRAPPED_CHEST:
                    Chest chest = (Chest) block.getState();
                    if (GrafZahlenPlugin.CHEST_SET.contains(chest)) {
                        ev.setCancelled(true);
                        GrafZahlenPlugin.LOGGER.log(Level.INFO, "[GrapfZahlen] interacted with a chest block");
                        Player player = ev.getPlayer();
                        player.sendMessage(Component.text("eins hahaha. zwei hahaha"));
                    }
                default:
            }
        } catch (Exception ex) {
            GrafZahlenPlugin.LOGGER.log(Level.SEVERE, "[GrafZahlen] onRightClickChestBlock exception aufgetreten");
            GrafZahlenPlugin.LOGGER.log(Level.SEVERE, ex.getMessage());
        }

    }

}
