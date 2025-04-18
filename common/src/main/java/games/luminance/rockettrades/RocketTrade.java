package games.luminance.rockettrades;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.registry.level.entity.trade.TradeRegistry;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffer;
import org.json.JSONObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.Arrays;

public class RocketTrade {
    private final ItemStack priceItem;
    private final ItemStack soldItem;
    private final int maxTrades;
    private final int xp;
    private final float priceMultiplier;
    private final int villagerLevel;
    private final VillagerProfession profession;

    public RocketTrade(ItemStack priceItem, ItemStack soldItem, int maxTrades, int xp, float priceMultiplier, int villagerLevel, VillagerProfession profession) {
        this.priceItem = priceItem;
        this.soldItem = soldItem;
        this.maxTrades = maxTrades;
        this.xp = xp;
        this.priceMultiplier = priceMultiplier;
        this.villagerLevel = villagerLevel;
        this.profession = profession;
    }

    private static VillagerProfession getProfessionFromString(String professionString) {
        Field[] professions = VillagerProfession.class.getFields();
        System.out.println(Arrays.toString(VillagerProfession.class.getFields()));
        for (Field f: professions) {
            if (!f.isEnumConstant()) {
                continue;
            }
            VillagerProfession p;
            try {
                p = (VillagerProfession) f.get(VillagerProfession.class);
            } catch (IllegalAccessException e) {
                System.out.println("Error: Could not get villager profession");
                return null;
            }
            if (p.name().equals(professionString)) {
                return p;
            }
        }
        return null;
    }

    public String getJson() {
        JsonObject json = new JsonObject();
        json.addProperty("priceItem", this.priceItem.save(new CompoundTag()).toString());
        json.addProperty("soldItem", this.soldItem.save(new CompoundTag()).toString());
        json.addProperty("maxTrades", this.maxTrades);
        json.addProperty("xp", this.xp);
        json.addProperty("priceMultiplier", this.priceMultiplier);
        json.addProperty("villagerLevel", this.villagerLevel);
        json.addProperty("profession", profession.name());
        return json.toString();
    }

    public static RocketTrade fromString(String jsonString) throws CommandSyntaxException {
        if (jsonString.isEmpty()) {
            return null;
        }
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        ItemStack priceItem = ItemStack.of(TagParser.parseTag(json.get("priceItem").getAsString()));
        ItemStack soldItem = ItemStack.of(TagParser.parseTag(json.get("soldItem").getAsString()));
        int maxUses = json.get("maxTrades").getAsInt();
        int xp = json.get("xp").getAsInt();
        float priceMultiplier = json.get("priceMultiplier").getAsFloat();
        int villagerLevel = json.get("villagerLevel").getAsInt();
        VillagerProfession profession = getProfessionFromString(json.get("profession").getAsString());
        if (profession == null) {
            return null;
        }
        RocketTrade trade = new RocketTrade(priceItem, soldItem, maxUses, xp, priceMultiplier, villagerLevel, profession);
        return trade;
    }

    public void registerTrade() {
        TradeRegistry.registerVillagerTrade(this.profession, this.villagerLevel, (entity, randomSource) -> new MerchantOffer(
                this.priceItem,
                this.soldItem,
                this.maxTrades,
                this.xp,
                this.priceMultiplier
        ));
    }
}
