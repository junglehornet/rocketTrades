package games.luminance.rockettrades;

import com.google.gson.*;
import com.google.gson.stream.MalformedJsonException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.platform.Platform;
import dev.architectury.registry.level.entity.trade.TradeRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class RocketTrades {
    public static final String MOD_ID = "rockettrades";
    private static final Path jsonPath = Path.of(Platform.getConfigFolder().toString() + "/rockettrades/trades.json");
    private static final Path jsonDir = Path.of(Platform.getConfigFolder().toString() + "/rockettrades/");
    private static ArrayList<RocketTrade> tradeList = new ArrayList<>();
    private static String json = "";

    private static void save() throws IOException, CommandSyntaxException {

        updateJson();
        try {
            FileWriter jsonWriter = new FileWriter(Platform.getConfigFolder().toString() + "/rockettrades/trades.json");
            jsonWriter.write(json);
            jsonWriter.close();
        } catch (IOException e) {
            System.out.println("Error: RocketTrades failed to save trades to disk.");
            e.printStackTrace();
        }
    }

    private static void createTradeFile() {
        if (!jsonDir.toFile().exists() || jsonDir.toFile().exists() && jsonDir.toFile().isDirectory()) {
            if (jsonDir.toFile().isDirectory()) {
                jsonDir.toFile().delete();
            }

            try {
                jsonDir.toFile().mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        if (!jsonPath.toFile().exists()) {
            try {
                jsonPath.toFile().createNewFile();
                json = "[" + getRocketTrade().getJson() + "]";
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
    }

    private static void updateJson() {
        JsonArray jsonArray = new JsonArray();
        for (RocketTrade trade: tradeList) {
            jsonArray.add(trade.getJson());
        }
        json = jsonArray.toString();
    }

    public static String[] getNameList() {
        String[] names = new String[tradeList.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = tradeList.get(i).getName();
        }
        return names;
    }

    public static void addTrade(RocketTrade trade) {
        tradeList.add(trade);
        try {
            save();
        } catch (IOException | CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        trade.registerTrade();
        System.out.printf("--RocketTrades registered new trade: %s\n", trade.getName());
    }

    public static boolean exists(String name) {
        for (RocketTrade t: tradeList) if (Objects.equals(t.getName(), name)) return true;
        return false;
    }

    public static void delTrade(String name) {
        tradeList.removeIf(t -> Objects.equals(t.getName(), name));
    }

    private static RocketTrade getRocketTrade() {
        ItemStack rockets = new ItemStack(Items.FIREWORK_ROCKET, 3);
        Fireworks fireworkComponent = new Fireworks(1, List.of());
        rockets.set(DataComponents.FIREWORKS, fireworkComponent);
        return new RocketTrade(new ItemStack(Items.EMERALD, 1), rockets, 7, 8, 0.02f, 1, VillagerProfession.CARTOGRAPHER, "Rocket Trade");
    }

    private static void loadTradesFromJson() throws CommandSyntaxException, FileNotFoundException {
        Scanner jsonReader = new Scanner(jsonPath.toFile());
        StringBuilder jsonBuilder = new StringBuilder();
        while (jsonReader.hasNextLine()) {
            jsonBuilder.append(jsonReader.nextLine());
        }
        jsonReader.close();
        json = jsonBuilder.toString();
        if (json.isEmpty()) json = "[]";

        JsonArray tradesGSON = JsonParser.parseString(json).getAsJsonArray();

        System.out.println("====  RocketTrades loading trades...  ====");
        for (JsonElement o: tradesGSON) {
            RocketTrade trade = RocketTrade.fromString(o.toString());
            if (trade != null) {
                tradeList.add(trade);
                trade.registerTrade();
                System.out.printf("--Trade Registered: %s\n", trade.getName());
            }
        }
        System.out.println("==== RocketTrades done loading trades ====");
    }

    public static void init() throws CommandSyntaxException, IOException {
        // Write common init code here.
        createTradeFile();
        loadTradesFromJson();

        RocketTradesCommands.register();
    }
}
