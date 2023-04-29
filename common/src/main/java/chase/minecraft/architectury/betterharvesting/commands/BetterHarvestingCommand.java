package chase.minecraft.architectury.betterharvesting.commands;

import chase.minecraft.architectury.betterharvesting.config.BetterHarvestingConfig;
import chase.minecraft.architectury.betterharvesting.config.ConfigHandler;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BetterHarvestingCommand
{
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(
				literal("betterharvesting")
						.requires((commandSourceStack) -> commandSourceStack.hasPermission(4))
						.executes(context ->
						{
							MutableComponent component = Component.literal("Better Harvesting Configuration:\n").withStyle(ChatFormatting.AQUA);
							component.append(Component.literal("%sAllow Fast Leaf Decay%s is %s%s\n".formatted(ChatFormatting.GOLD, ChatFormatting.RESET, ConfigHandler.getInstance().config.AllowFastLeafDecay ? ChatFormatting.GREEN : ChatFormatting.RED, ConfigHandler.getInstance().config.AllowFastLeafDecay ? "ENABLED" : "DISABLED")));
							component.append(Component.literal("%sAllow Right Click Harvest%s is %s%s\n".formatted(ChatFormatting.GOLD, ChatFormatting.RESET, ConfigHandler.getInstance().config.AllowRightClickHarvest ? ChatFormatting.GREEN : ChatFormatting.RED, ConfigHandler.getInstance().config.AllowRightClickHarvest ? "ENABLED" : "DISABLED")));
							component.append(Component.literal("%sAllow Automatic Sapling Replant%s is %s%s\n".formatted(ChatFormatting.GOLD, ChatFormatting.RESET, ConfigHandler.getInstance().config.AllowAutomaticSaplingReplant ? ChatFormatting.GREEN : ChatFormatting.RED, ConfigHandler.getInstance().config.AllowAutomaticSaplingReplant ? "ENABLED" : "DISABLED")));
							context.getSource().sendSuccess(component, true);
							return 1;
						})
						.then(literal("reload")
								.executes(context ->
								{
									ConfigHandler.getInstance().load();
									context.getSource().sendSuccess(Component.literal("[Better Harvesting] reloaded config"), true);
									return 1;
								}))
						.then(
								literal("fast_leaf_decay")
										.executes(context ->
										{
											context.getSource().sendSuccess(Component.literal("%sAllow Fast Leaf Decay%s is %s%s\n".formatted(ChatFormatting.GOLD, ChatFormatting.RESET, ConfigHandler.getInstance().config.AllowFastLeafDecay ? ChatFormatting.GREEN : ChatFormatting.RED, ConfigHandler.getInstance().config.AllowFastLeafDecay ? "ENABLED" : "DISABLED")), true);
											ConfigHandler.getInstance().load();
											return 1;
										})
										.then(
												argument("value", bool())
														.executes(context ->
														{
															BetterHarvestingConfig config = ConfigHandler.getInstance().config;
															config.AllowFastLeafDecay = getBool(context, "value");
															ConfigHandler.getInstance().config = config;
															context.getSource().sendSuccess(Component.literal("%sAllow Fast Leaf Decay%s is %s%s\n".formatted(ChatFormatting.GOLD, ChatFormatting.RESET, ConfigHandler.getInstance().config.AllowFastLeafDecay ? ChatFormatting.GREEN : ChatFormatting.RED, ConfigHandler.getInstance().config.AllowFastLeafDecay ? "ENABLED" : "DISABLED")), true);
															ConfigHandler.getInstance().save();
															return 1;
														})
										)
						)
						.then(
								literal("right_click_harvest")
										.executes(context ->
										{
											context.getSource().sendSuccess(Component.literal("%sAllow Right Click Harvest%s is %s%s\n".formatted(ChatFormatting.GOLD, ChatFormatting.RESET, ConfigHandler.getInstance().config.AllowRightClickHarvest ? ChatFormatting.GREEN : ChatFormatting.RED, ConfigHandler.getInstance().config.AllowRightClickHarvest ? "ENABLED" : "DISABLED")), true);
											ConfigHandler.getInstance().load();
											return 1;
										})
										.then(
												argument("value", bool())
														.executes(context ->
														{
															BetterHarvestingConfig config = ConfigHandler.getInstance().config;
															config.AllowFastLeafDecay = getBool(context, "value");
															ConfigHandler.getInstance().config = config;
															context.getSource().sendSuccess(Component.literal("%sAllow Right Click Harvest%s is %s%s\n".formatted(ChatFormatting.GOLD, ChatFormatting.RESET, ConfigHandler.getInstance().config.AllowRightClickHarvest ? ChatFormatting.GREEN : ChatFormatting.RED, ConfigHandler.getInstance().config.AllowRightClickHarvest ? "ENABLED" : "DISABLED")), true);
															ConfigHandler.getInstance().save();
															return 1;
														})
										)
						)
						.then(
								literal("auto_sapling_replant")
										.executes(context ->
										{
											ConfigHandler.getInstance().load();
											context.getSource().sendSuccess(Component.literal("%sAllow Automatic Sapling Replant%s is %s%s\n".formatted(ChatFormatting.GOLD, ChatFormatting.RESET, ConfigHandler.getInstance().config.AllowAutomaticSaplingReplant ? ChatFormatting.GREEN : ChatFormatting.RED, ConfigHandler.getInstance().config.AllowAutomaticSaplingReplant ? "ENABLED" : "DISABLED")), true);
											return 1;
										})
										.then(
												argument("value", bool())
														.executes(context ->
														{
															BetterHarvestingConfig config = ConfigHandler.getInstance().config;
															config.AllowAutomaticSaplingReplant = getBool(context, "value");
															ConfigHandler.getInstance().config = config;
															context.getSource().sendSuccess(Component.literal("%sAllow Automatic Sapling Replant%s is %s%s\n".formatted(ChatFormatting.GOLD, ChatFormatting.RESET, ConfigHandler.getInstance().config.AllowAutomaticSaplingReplant ? ChatFormatting.GREEN : ChatFormatting.RED, ConfigHandler.getInstance().config.AllowAutomaticSaplingReplant ? "ENABLED" : "DISABLED")), true);
															ConfigHandler.getInstance().save();
															return 1;
														})
										)
						)
		);
	}
	
}
