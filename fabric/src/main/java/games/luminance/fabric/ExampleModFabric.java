package games.luminance.fabric;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;

import games.luminance.rockettrades.RocketTrades;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        try {
            RocketTrades.init();
        } catch (CommandSyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
