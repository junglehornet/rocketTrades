package games.luminance.rockettrades;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.platform.Platform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public final class RocketTrades {
    public static final String MOD_ID = "rockettrades";
    private static String json = "";

    private static void save() throws IOException {
        FileWriter jsonWriter = new FileWriter(Platform.getConfigFolder().toString() + "/rockettrades/trades.json");
        jsonWriter.write(json);
        jsonWriter.close();
    }

    public static void init() throws CommandSyntaxException, FileNotFoundException {
        // Write common init code here.

        /*
        old code
        TradeRegistry.registerVillagerTrade(VillagerProfession.CARTOGRAPHER, 1, (entity, randomSource) -> {
            ItemStack rockets = new ItemStack(net.minecraft.world.item.Items.FIREWORK_ROCKET, 3);
            CompoundTag tag = new CompoundTag();
            CompoundTag flightTag = new CompoundTag();
            flightTag.putByte("Flight", (byte) 1);
            tag.put("Fireworks", flightTag);
            rockets.setTag(tag);

            return new MerchantOffer(
                    new ItemStack(net.minecraft.world.item.Items.EMERALD, 1), // BuyingItem
                    rockets, // SellingItem
                    7, // maxTrades
                    8, // xp
                    0.02F // priceMultiplier
            );
        });
         */

        ItemStack rockets = new ItemStack(Items.FIREWORK_ROCKET, 3);
        CompoundTag tag = new CompoundTag();
        CompoundTag flightTag = new CompoundTag();
        flightTag.putByte("Flight", (byte) 1);
        tag.put("Fireworks", flightTag);
        rockets.setTag(tag);
        RocketTrade fireworkTrade = new RocketTrade(new ItemStack(Items.EMERALD, 1), rockets, 7, 8, 0.02f, 1, VillagerProfession.CARTOGRAPHER);

        Path jsonPath = Path.of(Platform.getConfigFolder().toString() + "/rockettrades/trades.json");
        Path jsonDir = Path.of(Platform.getConfigFolder().toString() + "/rockettrades/");

        if (!jsonDir.toFile().exists() || jsonDir.toFile().exists() && jsonDir.toFile().isDirectory()) {
            if (jsonDir.toFile().isDirectory()) {
                jsonDir.toFile().delete();
            }

            try {
                jsonDir.toFile().mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!jsonPath.toFile().exists()) {
            try {
                jsonPath.toFile().createNewFile();
                json = "[" + fireworkTrade.getJson() + "]";
                try {
                    save();
                } catch (IOException e) {
                    System.out.println("Error: RocketTrades failed to save.");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Scanner jsonReader = new Scanner(jsonPath.toFile());
        StringBuilder jsonBuilder = new StringBuilder("");
        while (jsonReader.hasNextLine()) {
            jsonBuilder.append(jsonReader.nextLine());
        }
        jsonReader.close();
        json += jsonBuilder.toString();
        if (json.isEmpty()) {
            json = "[]";
            try {
                save();
            } catch (IOException e) {
                System.out.println("Error: RocketTrades failed to save.");
                e.printStackTrace();
            }
        }
        System.out.println("--------JSON--------\n" + fireworkTrade.getJson());
        JsonArray tradesGSON = JsonParser.parseString(json).getAsJsonArray();
        Object[] trades = tradesGSON.asList().toArray();

        for (Object o : trades) {
            RocketTrade trade = RocketTrade.fromString(o.toString());
            if (trade != null) {
                trade.registerTrade();
            }
        }
    }
}
