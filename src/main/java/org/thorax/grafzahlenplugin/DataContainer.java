package org.thorax.grafzahlenplugin;

import jdk.internal.misc.OSEnvironment;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.thorax.grafzahlenplugin.Term.TermSegment;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataContainer implements Serializable {

    private static final long serialVersionUID = 1L;
    public static Set<Chest> CHEST_SET = new HashSet<>();
    public static Map<TermSegment, Chest> SEGMENT_CHEST_MAP = new HashMap<TermSegment, Chest>();
    public static Map<Player, TermSegment> PLAYER_TERM_SEGMENT_MAP = new HashMap<Player, TermSegment>();
    public static Map<Chest, Set<Player>> CHEST_ALLOWED_PLAYER_MAP = new HashMap<>();

    public static void saveData() {
        try {
            File file = new File("GrafZahlen.data");
            if (file.createNewFile()) {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(file.toPath()));
                objectOutputStream.writeObject((HashSet<Chest>) CHEST_SET);
                objectOutputStream.writeObject((HashMap<TermSegment, Chest>) SEGMENT_CHEST_MAP);
                objectOutputStream.writeObject((HashMap<Player, TermSegment>) PLAYER_TERM_SEGMENT_MAP);
                objectOutputStream.writeObject((HashMap<Chest, Set<Player>>) CHEST_ALLOWED_PLAYER_MAP);
                objectOutputStream.flush();
                objectOutputStream.close();
            }
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    public static void loadData() {
        try {
            File file = new File("GrafZahlen.data");
            if (file.createNewFile()) {
                ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(file.toPath()));
                CHEST_SET = (HashSet<Chest>) objectInputStream.readObject();
                SEGMENT_CHEST_MAP = (HashMap<TermSegment, Chest>) objectInputStream.readObject();
                PLAYER_TERM_SEGMENT_MAP = (HashMap<Player, TermSegment>) objectInputStream.readObject();
                CHEST_ALLOWED_PLAYER_MAP = (HashMap<Chest, Set<Player>>) objectInputStream.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
