package org.thorax.grafzahlenplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.thorax.grafzahlenplugin.Term.TermSegment;

import java.util.*;
import java.util.logging.Level;

public class GrafZahlenCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        try {
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "grafzahlen_truhe_markieren gets executed");
            if (strings.length == 0) {
                commandSender.sendMessage(Component.text(""));
            }
            switch (strings[0]) {
                case "help":
                    if (strings.length == 1) {
                        return displayHelp(commandSender, null);
                    } else {
                        return displayHelp(commandSender, strings[1]);
                    }
                case "add":
                    if (strings.length == 1) {
                        return addChest(commandSender, null);
                    }
                    if (strings.length == 4) {
                        return addChest(commandSender, Arrays.copyOfRange(strings, 1, 4));
                    }
                    if (strings.length == 5) {
                        return addChest(commandSender, Arrays.copyOfRange(strings, 1, 5));
                    }
                case "remove":
                    if (strings.length == 1) {
                        return removeChest(commandSender, null);
                    } else if (strings.length == 2) {
                        return removeChest(commandSender, Arrays.copyOfRange(strings, 1, 2));
                    } else if (strings.length == 4) {
                        return removeChest(commandSender, Arrays.copyOfRange(strings, 1, 4));
                    }else if (strings.length == 5) {
                        return removeChest(commandSender, Arrays.copyOfRange(strings, 1, 5));
                    }
                case "list":
                    return listChest(commandSender);
                case "answer":
                    return giveAnswer(commandSender, strings[1]);
                default:
                    return false;
            }
        } catch (Exception ex) {
            GrafZahlenPlugin.LOGGER.log(Level.SEVERE, "exeption while executing GrafZahlenCommand");
            GrafZahlenPlugin.LOGGER.log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    private boolean addChest(CommandSender commandSender, @Nullable String[] strings) {
        if (strings == null) {
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "strings.length == 0");
            if(!(commandSender instanceof Player)) {
                GrafZahlenPlugin.LOGGER.log(Level.WARNING, "keine Instanz von Player");
                return false;
            }
            Player p = (Player) commandSender;
            Block targetBlock = p.getTargetBlockExact(10);
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "Block gefunden");
            return this.addBlocktoChestSetAndPlayerAllowed(targetBlock);
        }
        else {
            if (stringArrayHasNullValue(strings)) return false;
            World currentworld;
            int x, z, y;
            if (strings.length == 3) {
                currentworld = this.getWorldBySender(commandSender);
                x = Integer.parseInt(strings[0]);
                y = Integer.parseInt(strings[1]);
                z = Integer.parseInt(strings[2]);
                GrafZahlenPlugin.LOGGER.log(Level.INFO, String.format("xyz: %s|%s|%s", strings[0], strings[1], strings[2]));
            } else if (strings.length == 4) {
                currentworld = Bukkit.getWorld(strings[0]);
                x = Integer.parseInt(strings[1]);
                y = Integer.parseInt(strings[2]);
                z = Integer.parseInt(strings[3]);
                GrafZahlenPlugin.LOGGER.log(Level.INFO, String.format("world: %s\txyz: %s|%s|%s", strings[0], strings[1], strings[2], strings[3]));
            } else {
                GrafZahlenPlugin.LOGGER.log(Level.SEVERE, "invalid strings.length | addChest()");
                return false;
            }
            if (currentworld == null) {
                GrafZahlenPlugin.LOGGER.log(Level.SEVERE, "currentworld is null | addChest()");
                return false;
            }
            Block blockAtLoc = currentworld.getBlockAt(x, y, z);
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "Block gefunden");
            return this.addBlocktoChestSetAndPlayerAllowed(blockAtLoc);
        }
    }

    private boolean removeChest(CommandSender commandSender, @Nullable String[] strings) {
        if (strings == null) {
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "strings.length == 0");
            if(!(commandSender instanceof Player)) {
                GrafZahlenPlugin.LOGGER.log(Level.WARNING, "keine Instanz von Player");
                return false;
            }
            Player p = (Player) commandSender;
            Block targetBlock = p.getTargetBlockExact(10);
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "Block gefunden");
            return this.removeBlockfromChestSetAndPlayersAllowed(targetBlock);
        }
        else {
            if (stringArrayHasNullValue(strings)) return false;
            World currentworld;
            if (strings.length == 1){
                return this.removeAllChestsOfWorldFromChestSetAndPlayersAllowed(strings[0]);
            }
            int x, z, y;
            if (strings.length == 3) {
                currentworld = this.getWorldBySender(commandSender);
                x = Integer.parseInt(strings[0]);
                y = Integer.parseInt(strings[1]);
                z = Integer.parseInt(strings[2]);
                GrafZahlenPlugin.LOGGER.log(Level.INFO, String.format("xyz: %s|%s|%s", strings[0], strings[1], strings[2]));
            } else if (strings.length == 4) {
                currentworld = Bukkit.getWorld(strings[0]);
                x = Integer.parseInt(strings[1]);
                y = Integer.parseInt(strings[2]);
                z = Integer.parseInt(strings[3]);
                GrafZahlenPlugin.LOGGER.log(Level.INFO, String.format("world: %s\txyz: %s|%s|%s", strings[0], strings[1], strings[2], strings[3]));
            } else {
                GrafZahlenPlugin.LOGGER.log(Level.INFO, "invalid strings.length | removeChest()");
                return false;
            }
            if (currentworld == null) {
                GrafZahlenPlugin.LOGGER.log(Level.INFO, "currentworld is null | removeChest()");
                return false;
            }
            Block blockAtLoc = currentworld.getBlockAt(x, y, z);
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "Block gefunden");
            return this.removeBlockfromChestSetAndPlayersAllowed(blockAtLoc);
        }
    }

    private World getWorldBySender(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "commandsender ist ein Player");
            Player p = (Player) commandSender;
            return p.getWorld();
        } else if (commandSender instanceof BlockCommandSender) {
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "commandsender ein BlockCommand");
            BlockCommandSender blockCommandSender = (BlockCommandSender) commandSender;
            return blockCommandSender.getBlock().getWorld();
        } else {
            return null;
        }
    }

    private boolean addBlocktoChestSetAndPlayerAllowed(Block block) {
        if (isBlockChestType(block)) {
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "Block ist Chest");
            Chest chest = (Chest) block.getState();
            GrafZahlenPlugin.CHEST_SET.add(chest);
            GrafZahlenPlugin.CHEST_ALLOWED_PLAYER_MAP.put(chest, new HashSet<Player>());
            return true;
        }
        GrafZahlenPlugin.LOGGER.log(Level.SEVERE, String.format("Block ist keine Chest (%s)", block.getType()));
        return false;
    }

    private boolean giveAnswer(CommandSender commandSender, String answer) {
        if (!(commandSender instanceof Player)) {
            GrafZahlenPlugin.LOGGER.log(Level.SEVERE, "Der Sender ist kein Player");
            return false;
        }
        double answerDouble = Double.parseDouble(answer);
        GrafZahlenPlugin.LOGGER.log(Level.INFO, "antwort ist: " + answerDouble);
        Player sender = (Player) commandSender;
        TermSegment termSegment = GrafZahlenPlugin.PLAYER_TERM_SEGMENT_MAP.get(sender);
        if (termSegment.valueEquals(answerDouble, 0)) {
            commandSender.sendMessage(Component.text(answer + " ist korrekt. Du darfst die Truhe öffnen"));
            Chest chest = GrafZahlenPlugin.SEGMENT_CHEST_MAP.get(termSegment);
            GrafZahlenPlugin.CHEST_ALLOWED_PLAYER_MAP.get(chest).add(sender);
        } else {
            commandSender.sendMessage(Component.text(answer + " ist leider nicht korrekt"));
        }
        return true;
    }

    private boolean displayHelp(CommandSender commandSender,@Nullable String topic) {Component comp;
        if (topic == null) {
            comp = Component.text()
                    .content("Nutzung: gzp help <anderer command>\n" +
                            "andere commands:\n" +
                            "add\n" +
                            "remove\n" +
                            "list\n" +
                            "answer").build();
            commandSender.sendMessage(comp);
            return true;
        } else {
            switch (topic) {
                case "add":
                    comp = Component.text()
                            .content("Beschreibung: wird genutzt um eine neue Truhe für das GZ-Plugin zu registrieren\n" +
                                    "Nutzung: gzp add [<worldId>] [<x> <z> <y>]\n" +
                                    "Parameterbeschreibung: worldId beschreibt die Dimension inder sich die zu registrierende Truhe befindet" +
                                    "das Argument kann den Wert 0 (OverWorld), 1 (Nether) oder 2 (The_End) annehmen" +
                                    "Wenn das Argument weggelassen wird, wird von der Dimension des command Ausführers ausgegangen\n" +
                                    "Die Parameter x z und y stehen für die Koordinaten der zu registrierenden Truhe. " +
                                    "Falls diese Argumente Weggelassen werden wird von dem derzeitig betrachteten Block ausgegangen.\n").build();
                    commandSender.sendMessage(comp);
                    break;
                case "remove":
                    comp = Component.text()
                            .content("Beschreibung: wird zur Deregestrierung einer Truhe im GZ-Plugin genutzt.\n" +
                                    "Nutzung: gzp remove [<worldId>] [<x> <z> <y>]\n" +
                                    "Parameterbeschreibung: worldId beschreibt die Dimension inder sich die zu deregistrierende Truhe befindet" +
                                    "das Argument kann den Wert 0 (OverWorld), 1 (Nether) oder 2 (The_End) annehmen" +
                                    "Wenn das Argument weggelassen wird, wird von der Dimension des command Ausführers ausgegangen.\n" +
                                    "Die Parameter x z und y stehen für die Koordinaten der zu deregistrierenden Truhe. " +
                                    "Falls diese Argumente Weggelassen werden wird von dem derzeitig betrachteten Block innerhalb 10 metern ausgegangen.").build();
                    commandSender.sendMessage(comp);
                    break;
                case "list":
                    comp = Component.text()
                            .content("Beschreibung: wird zum auflisten von vom GZ-Plugin Registrierten Truhen verwendet").appendNewline()
                            .content("Nutzung: gzp list [<worldId>]")
                            .content("Parameterbeschreibung: worldId beschreibt die Dimension dessen Regestrierte Truhen aufgezählt werden sollten. " +
                                    "Falls dieses Argument fehlt werden alle Registrierten Truhen Aufgezählt")
                            .content("Beschreibung: wird zum auflisten von vom GZ-Plugin Registrierten Truhen verwendet\n" +
                                    "Nutzung: gzp list [<worldId>]\n" +
                                    "Parameterbeschreibung: worldId beschreibt die Dimension dessen Registrierte Truhe aufgezählt werden sollten. " +
                                    "das Argument kann den Wert 0 (OverWorld), 1 (Nether) oder 2 (The_End) annehmen. " +
                                    "Falls dieses Argument fehlt werden alle Registrierten Truhen Aufgezählt").build();
                    commandSender.sendMessage(comp);
                case "answer":
                    comp = Component.text()
                            .content("Beschreibung: wird zum beantworten von gestellten Aufgaben genutzt").appendNewline()
                            .content("Nutzung: gzp answer <antwort>")
                            .content("Parameterbeschreibung: antwort ist der wert mit der eine Aufgabenstellung beantwortet wird " +
                                    "antwort muss eine Zahl sein. Ganzzahl-Stellen und nach-Komma-Stellen werden durch eine Punkt getrennt und nicht durch ein Komma")
                            .build();
                    commandSender.sendMessage(comp);

                default:
                    commandSender.sendMessage(Component.text("invalid Argument").color(NamedTextColor.RED));
                    break;
            }
        }
        return true;
    }

    private boolean removeBlockfromChestSetAndPlayersAllowed(Block block) {
        if (isBlockChestType(block)){
            GrafZahlenPlugin.LOGGER.log(Level.INFO, "Block ist Chest");
            Chest chest = (Chest) block.getState();
            GrafZahlenPlugin.CHEST_ALLOWED_PLAYER_MAP.remove(chest);
            return GrafZahlenPlugin.CHEST_SET.remove(chest);
        }
        GrafZahlenPlugin.LOGGER.log(Level.SEVERE, "Block ist keine Chest");
        return false;
    }

    private boolean removeAllChestsOfWorldFromChestSetAndPlayersAllowed(String worldString) {
        if (worldString.equalsIgnoreCase("all")) {
            for (Chest key:
                 GrafZahlenPlugin.CHEST_SET) {
                GrafZahlenPlugin.CHEST_ALLOWED_PLAYER_MAP.remove(key);
                GrafZahlenPlugin.CHEST_SET.remove(key);
            }
            return true;
        }
        World world = Bukkit.getWorld(worldString);
        for (Chest chest :
                GrafZahlenPlugin.CHEST_SET) {
            if (chest.getWorld().equals(world)) {
                GrafZahlenPlugin.CHEST_SET.remove(chest);
            }
        }
        return true;
    }

    private boolean listChest(CommandSender commandSender) {
        for (Chest chest:
             GrafZahlenPlugin.CHEST_SET) {
            Component comp = Component.text(String.format("world: %s\txyz: (%s|%s|%s)", chest.getWorld().getName(), chest.getX(), chest.getY(), chest.getZ()));
            commandSender.sendMessage(comp);
        }
        return true;
    }


    private boolean isBlockChestType(Block block) {
        switch (block.getType()){
            case CHEST:
            case ENDER_CHEST:
            case TRAPPED_CHEST:
                return true;
            default:
                return false;
        }
    }

    private boolean stringArrayHasNullValue(String[] strings) {
        for (String string :
                strings) {
            if (string == null) {
                return true;
            }
        }
        return false;
    }
}
