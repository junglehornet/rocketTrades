package games.luminance.rockettrades;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import dev.architectury.registry.level.entity.trade.TradeRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class RocketTrade {
    private final ItemCost cost;
    private final ItemStack outputItem;
    private final int maxTrades;
    private final int xp;
    private final float priceMultiplier;
    private final int villagerLevel;
    private final ResourceKey<VillagerProfession> profession;
    private final String name;

    public RocketTrade(ItemStack stackCost, ItemStack outputItem, int maxTrades, int xp, float priceMultiplier, int villagerLevel, ResourceKey<VillagerProfession> profession, String name) {
        DataComponentPredicate predicate = DataComponentPredicate.allOf(stackCost.getComponents());
        this.cost = new ItemCost(stackCost.getItemHolder(), stackCost.getCount(), predicate);
        this.outputItem = outputItem;
        this.maxTrades = maxTrades;
        this.xp = xp;
        this.priceMultiplier = priceMultiplier;
        this.villagerLevel = villagerLevel;
        this.profession = profession;
        this.name = name;
    }

    public RocketTrade(ItemCost cost, ItemStack outputItem, int maxTrades, int xp, float priceMultiplier, int villagerLevel, ResourceKey<VillagerProfession> profession, String name) {
        this.cost = cost;
        this.outputItem = outputItem;
        this.maxTrades = maxTrades;
        this.xp = xp;
        this.priceMultiplier = priceMultiplier;
        this.villagerLevel = villagerLevel;
        this.profession = profession;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static ResourceKey<VillagerProfession> getProfessionFromString(String professionString, CommandContext<CommandSourceStack> context) {
        for (VillagerProfession p: BuiltInRegistries.VILLAGER_PROFESSION) {
            String name = p.name();
            if (name.equals("nitwit") || name.equals("none")) {
                continue;
            }
            if (name.equals(professionString)) {
                ResourceKey<VillagerProfession> profession = BuiltInRegistries.VILLAGER_PROFESSION.getResourceKey(p).orElseThrow();
                return profession;
            }
        }
        return null;
    }

    private static JsonElement itemStackToJson(ItemStack stack) {
        Optional<JsonElement> optional = ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, stack).result();
        return optional.orElse(null);
    }

    private static JsonElement itemCostToJson(ItemCost cost) {
        Optional<JsonElement> option = ItemCost.CODEC.encodeStart(JsonOps.INSTANCE, cost).result();
        return option.orElse(null);
    }

    private static JsonObject resourceLocationToJson(ResourceLocation loc) {
        JsonObject json = new JsonObject();
        json.addProperty("namespace", loc.getNamespace());
        json.addProperty("path", loc.getPath());
        return json;
    }

    private static JsonObject resourceKeyToJson(ResourceKey key) {
        JsonObject json = new JsonObject();
        json.add("registry", resourceLocationToJson(key.registry()));
        json.add("location", resourceLocationToJson(key.location()));
        return json;
    }

    public JsonObject getJson() {
        JsonObject json = new JsonObject();
        json.add("priceItem", itemCostToJson(this.cost));
        json.add("soldItem", itemStackToJson(this.outputItem));
        json.addProperty("maxTrades", this.maxTrades);
        json.addProperty("xp", this.xp);
        json.addProperty("priceMultiplier", this.priceMultiplier);
        json.addProperty("villagerLevel", this.villagerLevel);
        json.add("profession", resourceKeyToJson(this.profession));
        json.addProperty("name", this.name);
        return json;
    }

    private static ResourceLocation jsonToResourceLocation(JsonObject obj) {
        return ResourceLocation.fromNamespaceAndPath(obj.get("namespace").getAsString(), obj.get("path").getAsString());
    }

    private static ResourceKey jsonToResourceKey(JsonObject obj) {
        ResourceKey key = ResourceKey.create(
                ResourceKey.createRegistryKey(jsonToResourceLocation(obj.getAsJsonObject("registry"))),
                jsonToResourceLocation(obj.getAsJsonObject("location"))
        );
        return key;
    }

    public static RocketTrade fromString(String jsonString) throws CommandSyntaxException {
        if (jsonString.isEmpty()) {
            return null;
        }
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        ItemCost cost = ItemCost.CODEC.parse(JsonOps.INSTANCE, json.get("priceItem")).getOrThrow();
        ItemStack soldItem = ItemStack.CODEC.parse(JsonOps.INSTANCE, json.get("soldItem")).getOrThrow();
        int maxUses = json.get("maxTrades").getAsInt();
        int xp = json.get("xp").getAsInt();
        float priceMultiplier = json.get("priceMultiplier").getAsFloat();
        int villagerLevel = json.get("villagerLevel").getAsInt();
        String name = json.get("name").getAsString();
        ResourceKey<VillagerProfession> profession = jsonToResourceKey(json.getAsJsonObject("profession"));
        RocketTrade trade = new RocketTrade(cost, soldItem, maxUses, xp, priceMultiplier, villagerLevel, profession, name);
        return trade;
    }

    public void registerTrade() {
        TradeRegistry.registerVillagerTrade(BuiltInRegistries.VILLAGER_PROFESSION.get(this.profession), this.villagerLevel, (entity, randomSource) -> new MerchantOffer(
                this.cost,
                this.outputItem,
                this.maxTrades,
                this.xp,
                this.priceMultiplier
        ));
    }
}
