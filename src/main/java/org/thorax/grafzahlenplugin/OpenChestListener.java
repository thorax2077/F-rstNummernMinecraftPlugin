package org.thorax.grafzahlenplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.thorax.grafzahlenplugin.Term.TermGenerator;
import org.thorax.grafzahlenplugin.Term.TermSegment;

import java.util.logging.Level;

public class OpenChestListener implements Listener {

    @EventHandler
    public void onRightClickChestBlock(PlayerInteractEvent ev) {
        try {
            if (!ev.getAction().isRightClick()) {
                return;
            }
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "[GrafZahl] ineraction event | right click");
            Block block = ev.getClickedBlock();
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "Block Instance " + block.getType());
            switch (block.getType()) {
                case CHEST:
                case ENDER_CHEST:
                case TRAPPED_CHEST:
                    Chest chest = (Chest) block.getState();
                    if (GrafZahlenPlugin.CHEST_SET.contains(chest) && !GrafZahlenPlugin.CHEST_ALLOWED_PLAYER_MAP.get(chest).contains(ev.getPlayer())) {
                        ev.setCancelled(true);
                        GrafZahlenPlugin.LOGGER.log(Level.INFO, "[GrafZahl] interacted with a chest block");
                        Player player = ev.getPlayer();
                        player.sendMessage(Component.text("eins hahaha. zwei hahaha"));
                        TermSegment termSegment = TermGenerator.newTerm()
                                .maxDepth(2).termCount(2).valueRange(-50, 50).build();
                        GrafZahlenPlugin.PLAYER_TERM_SEGMENT_MAP.put(ev.getPlayer(), termSegment);
                        GrafZahlenPlugin.SEGMENT_CHEST_MAP.put(termSegment, chest);
                        player.sendMessage(Component.text("Was ist das ergebnis zu diesem Term: " + termSegment.toStringValuesRounded(0) + " ?"));
                        player.sendMessage(Component.text("Gib mir eine \\gzp answer!"));
                    }
                default:
            }
        } catch (Exception ex) {
            GrafZahlenPlugin.LOGGER.log(Level.SEVERE, "[GrafZahl] onRightClickChestBlock exception aufgetreten");
            GrafZahlenPlugin.LOGGER.log(Level.SEVERE, ex.getMessage());
        }
    }
}
