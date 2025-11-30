//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package games.luminance.rockettrades;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.architectury.event.events.common.CommandRegistrationEvent;

import java.util.Objects;

import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.VillagerProfession;

public class RocketTradesCommands {
    private static final SuggestionProvider<CommandSourceStack> NAME_SUGGESTIONS = (context, builder) -> {
        String[] names = RocketTrades.getNameList();

        for(String name : names) {
            if (name.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                builder.suggest(name);
            }
        }

        return builder.buildFuture();
    };

    private static final SuggestionProvider<CommandSourceStack> VILLAGER_PROFESSION_SUGGESTIONS = (context, builder) -> {
        Object[] professions = context.getSource().getServer().registryAccess().lookupOrThrow(Registries.VILLAGER_PROFESSION).stream().toArray();
        for (Object o: professions) {
            VillagerProfession p;
            p = (VillagerProfession) o;
            String name = p.name().getString().toLowerCase();
            if (name.startsWith(builder.getRemaining().toLowerCase())) {
                builder.suggest(name);
            }
        }
        return builder.buildFuture();
    };

    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("addTrade")
                .requires(commandSourceStack -> (commandSourceStack.hasPermission(2) || commandSourceStack.getServer().isSingleplayer()) && commandSourceStack.isPlayer())
                .then(Commands.argument("name", StringArgumentType.string())
                        .then(Commands.argument("villagerLevel", IntegerArgumentType.integer(1))
                                .then(Commands.argument("villagerProfession", StringArgumentType.string()).suggests(VILLAGER_PROFESSION_SUGGESTIONS)
                                    .executes(context -> {
                                            if (RocketTrade.getProfessionFromString(StringArgumentType.getString(context, "villagerProfession"), context) == null) {
                                                Component message = Component.literal("Error: Invalid villager profession.");
                                                message.getStyle().applyFormat(ChatFormatting.RED);
                                                context.getSource().sendSystemMessage (message);
                                            }
                                            return 1;
                                        })
                                        .then(Commands.argument("maxTrades", IntegerArgumentType.integer(1)).then(Commands.argument("xp", IntegerArgumentType.integer(0)).then(Commands.argument("priceMultiplier", FloatArgumentType.floatArg(0.0F)).executes((context) -> {
                                            if (RocketTrade.getProfessionFromString(StringArgumentType.getString(context, "villagerProfession"), context) == null) {
                                                Component message = Component.literal("Error: Invalid villager profession.");
                                                message.getStyle().applyFormat(ChatFormatting.RED);
                                                context.getSource().sendSystemMessage(message);
                                            } else {
                                                RocketTrade trade = new RocketTrade(Objects.requireNonNull(context.getSource().getPlayer()).getOffhandItem(), Objects.requireNonNull(context.getSource().getPlayer()).getMainHandItem(), IntegerArgumentType.getInteger(context, "maxTrades"), IntegerArgumentType.getInteger(context, "xp"), FloatArgumentType.getFloat(context, "priceMultiplier"), IntegerArgumentType.getInteger(context, "villagerLevel"), RocketTrade.getProfessionFromString(StringArgumentType.getString(context, "villagerProfession"), context), StringArgumentType.getString(context, "name"));
                                                RocketTrades.addTrade(trade);
                                                Component message = Component.literal("Trade " + StringArgumentType.getString(context, "name") + " successfully created.");
                                                message.getStyle().applyFormat(ChatFormatting.GREEN);
                                                context.getSource().sendSystemMessage(message);
                                                message = Component.literal("NOTE: This change will not take effect until the world is restarted.");
                                                message.getStyle().applyFormat(ChatFormatting.RED);
                                                context.getSource().sendSystemMessage(message);
                                            }
                                            return 1;
                                        }))))))));
        
        dispatcher.register(Commands.literal("removeTrade")
                .requires(commandSourceStack -> (commandSourceStack.hasPermission(2) || commandSourceStack.getServer().isSingleplayer()) && commandSourceStack.isPlayer())
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .suggests(NAME_SUGGESTIONS)
                        .executes(context -> {
                            String name = StringArgumentType.getString(context, "name");
                            if (RocketTrades.exists(name)) {
                                RocketTrades.delTrade(name);
                                Component message = Component.literal("Trade " + name + " successfully deleted.");
                                message.getStyle().applyFormat(ChatFormatting.GREEN);
                                context.getSource().sendSystemMessage(message);
                                message = Component.literal("NOTE: This change will not take effect until the world is restarted.");
                                message.getStyle().applyFormat(ChatFormatting.RED);
                                context.getSource().sendSystemMessage(message);
                                return 1;
                            } else {
                                Component message = Component.literal("Error: Unknown trade: " + name);
                                message.getStyle().applyFormat(ChatFormatting.RED);
                                context.getSource().sendSystemMessage(message);
                                return 0;
                            }
                        })));
        
        dispatcher.register(Commands.literal("listTrades")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2) || commandSourceStack.getServer().isSingleplayer())
                .executes(context -> {
                    Component tradeList = Component.literal(Strings.join(RocketTrades.getNameList(), "\n"));
                    if (tradeList.equals(Component.literal(""))) {
                        tradeList = Component.literal("There are no trades.");
                    }
                    context.getSource().sendSystemMessage(tradeList);
                    return 1;
                }));
    }
}
