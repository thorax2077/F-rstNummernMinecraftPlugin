package org.thorax.grafzahlenplugin;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.thorax.grafzahlenplugin.Term.TermSegment;

import javax.crypto.ExemptionMechanismException;
import java.io.*;
import java.util.*;

@SerializableAs("DataContainer")
public class DataContainer implements Serializable, ConfigurationSerializable {

    private static final long serialVersionUID = 1L;

    public Set<Chest> getChestSet() {
        return chestSet;
    }

    public Map<TermSegment, Chest> getSegmentChestMap() {
        return segmentChestMap;
    }

    public Map<Player, TermSegment> getPlayerTermSegmentHashMap() {
        return playerTermSegmentHashMap;
    }

    public Map<Chest, Set<Player>> getChestAllowedPlayerMap() {
        return chestAllowedPlayerMap;
    }


    private  Set<Chest> chestSet = new HashSet<>();
    private Map<TermSegment, Chest> segmentChestMap = new HashMap<TermSegment, Chest>();
    private  Map<Player, TermSegment> playerTermSegmentHashMap = new HashMap<Player, TermSegment>();
    private  Map<Chest, Set<Player>> chestAllowedPlayerMap = new HashMap<>();


    public DataContainer() {
        chestSet = new HashSet<>();
        segmentChestMap = new HashMap<>();
        playerTermSegmentHashMap = new HashMap<>();
        chestAllowedPlayerMap = new HashMap<>();
    }

    DataContainer(Set<Chest> chestSet, Map<TermSegment, Chest> termSegmentChestMap,
                  Map<Player, TermSegment> playerTermSegmentMap, Map<Chest, Set<Player>> chestPlayerMap) {
        this.chestSet = chestSet;
        this.segmentChestMap = termSegmentChestMap;
        this.playerTermSegmentHashMap = playerTermSegmentMap;
        this.chestAllowedPlayerMap = chestPlayerMap;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {

        Map<String, Object> toReturn = new HashMap<>();

        toReturn.put("ChestSet" ,this.serializeChestSet());

        toReturn.put("TermSegmenChestMap" ,this.serializeTermSegmentChestMap());

        toReturn.put("PlayerTermSegmentMap" ,this.serializePlayerTermSegmentMap());

        toReturn.put("ChestAllowedPlayersMap" ,this.serializeChestAllowedPlayersMap());


        return toReturn;
    }

    private Map<String, Object> serializeChestSet() {
        Map<String, Object> toReturn = new HashMap<>();
        List<Location> chest_locations = new ArrayList<>();
        for (Chest chest :
                chestSet) {
            chest_locations.add(chest.getLocation());
        }
        toReturn.put("locations", chest_locations);
        return toReturn;
    }

    private Map<String, Object> serializeTermSegmentChestMap() {
        Map<String, Object> toReturn = new HashMap<>();

        List<Location> segment_chest_map_chests = new ArrayList<>();
        List<TermSegmentSerializationWrapper> segment_chest_map_terms = new ArrayList<>();
        Chest curChest;
        for (TermSegment termseg :
                segmentChestMap.keySet()) {
            curChest = segmentChestMap.get(termseg);
            segment_chest_map_chests.add(curChest.getLocation());
            segment_chest_map_terms.add(new TermSegmentSerializationWrapper(termseg));
        }

        toReturn.put("chests", segment_chest_map_chests);
        toReturn.put("termSegmentWrappers", segment_chest_map_terms);

        return toReturn;
    }

    private Map<String, Object> serializePlayerTermSegmentMap() {
        Map<String, Object> toReturn = new HashMap<>();

        List<Player> players = new ArrayList<>();
        List<TermSegmentSerializationWrapper> termSegmentWrappers = new ArrayList<>();

        for (Player player :
                this.playerTermSegmentHashMap.keySet()) {
            players.add(player);
            termSegmentWrappers.add(new TermSegmentSerializationWrapper(this.playerTermSegmentHashMap.get(player)));
        }

        toReturn.put("players", players);
        toReturn.put("termSegmentWrappers", termSegmentWrappers);

        return toReturn;
    }

    private Map<String, Object> serializeChestAllowedPlayersMap() {
        Map<String, Object> toReturn = new HashMap<>();

        List<Location> chest_allowed_player_map_chests = new ArrayList<>();
        List<List<OfflinePlayer>> chest_allowed_player_map_playerSetLists = new ArrayList<>();
        List<OfflinePlayer> chest_allowed_player_map_playerSets;
        Set<Player> playerSet;
        for (Chest chest :
                chestAllowedPlayerMap.keySet()) {
            playerSet = chestAllowedPlayerMap.get(chest);
            chest_allowed_player_map_playerSets = new ArrayList<>(playerSet);
            chest_allowed_player_map_playerSetLists.add(chest_allowed_player_map_playerSets);
            chest_allowed_player_map_chests.add(chest.getLocation());
        }
        toReturn.put("chests", chest_allowed_player_map_chests);
        toReturn.put("playerSet", chest_allowed_player_map_playerSetLists);

        return toReturn;

    }

    public static DataContainer deserialize(ConfigurationSection section) throws Exception {
        DataContainer toReturn = new DataContainer();

        Set<Chest> chests = new HashSet<>(deserializeChestSet(section.getConfigurationSection("ChestSet")));

        Map<TermSegment, Chest> termSegmentChestMap =
                new HashMap<>(deserializeTermSegmentChestMap(section.getConfigurationSection("TermSegmenChestMap")));

        Map<>

        /*
        toReturn.put("player_term_player", player_term_player);
        toReturn.put("player_term_TermSegments", player_term_TermSegments);
         */
        List<Player> playerTermPlayers = (List<Player>) section.getList("player_term_player");
        List<TermSegment> playerTermTermSegments = new ArrayList<>((de));

        /*
        toReturn.put("chest_allowed_player_map_chests", chest_allowed_player_map_chests);
        toReturn.put("chest_allowed_player_map_playerSetLists", chest_allowed_player_map_playerSetLists);
        */

        return toReturn;
    }

    private static Set<Chest> deserializeChestSet(ConfigurationSection configurationSection) {
        Set<Chest> toReturn = new HashSet<>();
        List<Location> ChestLocations = (List<Location>) configurationSection.getList("locations");

        for (Location location :
                ChestLocations) {
            toReturn.add((Chest) location.getBlock());
        }

        return toReturn;
    }

    private static Map<TermSegment, Chest> deserializeTermSegmentChestMap(ConfigurationSection configurationSection) {
        Map<TermSegment, Chest> toReturn = new HashMap<>();

        List<Location> segment_chest_map_chests = (List<Location>) configurationSection.getList("chests");
        List<TermSegmentSerializationWrapper> segment_chest_map_terms =
                (List<TermSegmentSerializationWrapper>) configurationSection.getList("termSegmentWrappers");
        for (int i = 0; i < segment_chest_map_chests.size(); i++) {
            toReturn.put(
                    segment_chest_map_terms.get(i).toTermSegment(), (Chest) segment_chest_map_chests.get(i).getBlock());
        }

        return toReturn;
    }

    private static Map<Player, TermSegment> deserializePlayerTermSegmentMap(ConfigurationSection configurationSection) {
        Map<Player, TermSegment> toReturn = new HashMap<>();
        List<Player> players = (List<Player>) configurationSection.getList("players");
        List<TermSegmentSerializationWrapper> termSegmentWrappers =
                (List<TermSegmentSerializationWrapper>) configurationSection.getList("termSegmentWrappers");
        for (int i = 0; i < players.size(); i++) {
            toReturn.put(players.get(i).getPlayer(), termSegmentWrappers.get(i).toTermSegment());
        }
        return toReturn;
    }

    private static Map<Chest, Set<Player>> deserializeChestAllowedPlayers(ConfigurationSection configurationSection) {
        Map <Chest, Set<Player>> toReturn = new HashMap<>();
        List<Chest> chests = (List<Chest>) configurationSection.getList("chests");
        List<List<Player>> playerSet = (List<List<Player>>) configurationSection.getList("playerSet");
        Set<Player> tempPlayerSet;
        for (int i = 0; i < chests.size(); i++) {
            tempPlayerSet = new HashSet<>();
            for (Player player :
                    playerSet.get(i)) {
                tempPlayerSet.add(player);
            }
            toReturn.put(chests.get(i), tempPlayerSet);
        }
        return toReturn;
    }

}
