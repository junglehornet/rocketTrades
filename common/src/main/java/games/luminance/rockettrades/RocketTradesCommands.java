//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package games.luminance.rockettrades;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.architectury.event.events.common.CommandRegistrationEvent;

import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Items;

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

    private static final SuggestionProvider<CommandSourceStack> NAME_SUGGESTIONS_QUOTED = (context, builder) -> {
        String[] names = RocketTrades.getNameList();

        for(String name : names) {
            if (name.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
//                if (name.contains(" ")) name = "\"" + name + "\"";
                builder.suggest(StringArgumentType.escapeIfRequired(name));
            }
        }

        return builder.buildFuture();
    };

    private static final SuggestionProvider<CommandSourceStack> VILLAGER_PROFESSION_SUGGESTIONS = (context, builder) -> {
        for (VillagerProfession p: BuiltInRegistries.VILLAGER_PROFESSION) {
          String name = p.name();
          if (name.equals("nitwit") || name.equals("none")) {
            continue;
          }
          if (name.startsWith(builder.getRemaining().toLowerCase())) {
            builder.suggest(name);
          }
        }
        return builder.buildFuture();
    };

    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
    }

    private static void sendWarning(CommandContext<CommandSourceStack> context) {
        Component message = Component.literal("Trade \"" + StringArgumentType.getString(context, "name") + "\" successfully created.");
        message.getStyle().applyFormat(ChatFormatting.GREEN);
        context.getSource().sendSystemMessage(message);
        message = Component.literal("NOTE: This change will not take effect until the world is restarted.");
        message.getStyle().applyFormat(ChatFormatting.RED);
        context.getSource().sendSystemMessage(message);
    }

    private static boolean validate(CommandContext<CommandSourceStack> context) {
      if (RocketTrade.getProfessionFromString(StringArgumentType.getString(context, "villagerProfession"), context) == null) {
        Component message = Component.literal("Error: Invalid villager profession.");
        message.getStyle().applyFormat(ChatFormatting.RED);
        context.getSource().sendSystemMessage (message);
        return false;
      }
      ServerPlayer player = context.getSource().getPlayer();
      if (player == null) {
        Component message = Component.literal("You must be a player to run this command.");
        message.getStyle().applyFormat(ChatFormatting.RED);
        context.getSource().sendSystemMessage(message);
        return false;
      }
      if (player.getOffhandItem().is(Items.AIR)) {
        Component message = Component.literal("Error: cannot create trade without input item");
        message.getStyle().applyFormat(ChatFormatting.RED);
        player.sendSystemMessage(message);
        return false;
      }
      if (player.getMainHandItem().is(Items.AIR)) {
        Component message = Component.literal("Error: cannot create trade without output item");
        message.getStyle().applyFormat(ChatFormatting.RED);
        player.sendSystemMessage(message);
        return false;
      }
      return true;
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("addTrade")
                .requires(commandSourceStack -> (commandSourceStack.hasPermission(2) || commandSourceStack.getServer().isSingleplayer()) && commandSourceStack.isPlayer())
                .then(Commands.argument("name", StringArgumentType.string()).suggests(NAME_SUGGESTIONS_QUOTED)
                        .then(Commands.argument("villagerLevel", IntegerArgumentType.integer(1))
                                .then(Commands.argument("villagerProfession", StringArgumentType.string()).suggests(VILLAGER_PROFESSION_SUGGESTIONS)
                                    .executes(context -> {
                                        ServerPlayer player = context.getSource().getPlayer();
                                        if (validate(context)) {
                                            RocketTrade trade = new RocketTrade(player.getOffhandItem(), player.getMainHandItem(), 8, 8, 0.02f, IntegerArgumentType.getInteger(context, "villagerLevel"), RocketTrade.getProfessionFromString(StringArgumentType.getString(context, "villagerProfession"), context), StringArgumentType.getString(context, "name"));
                                            RocketTrades.addTrade(trade);
                                            sendWarning(context);
                                        }
                                        return 1;
                                        })
                                        .then(Commands.argument("maxTrades", IntegerArgumentType.integer(1)).then(Commands.argument("xp", IntegerArgumentType.integer(0)).then(Commands.argument("priceMultiplier", FloatArgumentType.floatArg(0.0F)).executes((context) -> {
                                          ServerPlayer player = context.getSource().getPlayer();
                                          if (validate(context)) {
                                              RocketTrade trade = new RocketTrade(player.getOffhandItem(), player.getMainHandItem(), IntegerArgumentType.getInteger(context, "maxTrades"), IntegerArgumentType.getInteger(context, "xp"), FloatArgumentType.getFloat(context, "priceMultiplier"), IntegerArgumentType.getInteger(context, "villagerLevel"), RocketTrade.getProfessionFromString(StringArgumentType.getString(context, "villagerProfession"), context), StringArgumentType.getString(context, "name"));
                                              RocketTrades.addTrade(trade);
                                              sendWarning(context);
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
