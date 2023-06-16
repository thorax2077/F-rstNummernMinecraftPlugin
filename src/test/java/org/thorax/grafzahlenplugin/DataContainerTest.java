package org.thorax.grafzahlenplugin;

import dev.watchwolf.entities.Position;
import dev.watchwolf.entities.blocks.Block;
import dev.watchwolf.entities.blocks.Blocks;
import dev.watchwolf.server.ServerStopNotifier;
import dev.watchwolf.tester.AbstractTest;
import dev.watchwolf.tester.ExtendedClientPetition;
import dev.watchwolf.tester.TesterConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.util.Set;

@ExtendWith(DataContainerTest.class)
public class DataContainerTest extends AbstractTest {



    @ParameterizedTest
    @ArgumentsSource(DataContainerTest.class)
    public void testSerialize(TesterConnector testerConnector) throws IOException {
        ExtendedClientPetition extendedClientPetition = testerConnector.getClientPetition(0);
        Position chestPos = extendedClientPetition.getPosition().add(0, 0, 1);
        Block chest = Blocks.CHEST;

        testerConnector.setBlock(chestPos, chest);

        extendedClientPetition.lookAt(chestPos);
        extendedClientPetition.runCommand("gzp add");
        extendedClientPetition.use();

        Set<org.bukkit.block.Chest> chest1 = GrafZahlenPlugin.GrafZahlenData.getChestSet();
        testerConnector.

    }

}
