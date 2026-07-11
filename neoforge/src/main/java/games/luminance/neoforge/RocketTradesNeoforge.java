package games.luminance.neoforge;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.neoforged.fml.common.Mod;

import games.luminance.rockettrades.RocketTrades;

import java.io.IOException;

@Mod(RocketTrades.MOD_ID)
public final class RocketTradesNeoforge {
    public RocketTradesNeoforge() throws IOException, CommandSyntaxException {
        // Run our common setup.
        RocketTrades.init();
    }
}
