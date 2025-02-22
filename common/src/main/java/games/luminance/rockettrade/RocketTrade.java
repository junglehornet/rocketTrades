package games.luminance.rockettrade;

import dev.architectury.registry.level.entity.trade.TradeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public final class RocketTrade {
    public static final String MOD_ID = "rockettrade";

    public static void init() {
        // Write common init code here.

        TradeRegistry.registerVillagerTrade(VillagerProfession.CARTOGRAPHER, 1, (entity, randomSource) -> {
            ItemStack rockets = new ItemStack(net.minecraft.world.item.Items.FIREWORK_ROCKET, 3);
            CompoundTag tag = new CompoundTag();
            CompoundTag flightTag = new CompoundTag();
            flightTag.putByte("Flight", (byte) 1);
            tag.put("Fireworks", flightTag);
            rockets.setTag(tag);

            return new MerchantOffer(
                    new ItemStack(net.minecraft.world.item.Items.EMERALD, 1),
                    rockets,
                    7,
                    8,
                    0.02F
            );
        });
    }

}
