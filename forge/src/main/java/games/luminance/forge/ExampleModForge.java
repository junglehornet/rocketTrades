package games.luminance.forge;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import games.luminance.rockettrades.RocketTrades;

import java.io.FileNotFoundException;

@Mod(RocketTrades.MOD_ID)
public final class ExampleModForge {
    public ExampleModForge() throws CommandSyntaxException, FileNotFoundException {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(RocketTrades.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        RocketTrades.init();
    }
}
