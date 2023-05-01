package chase.minecraft.architectury.betterharvesting.commands;

import chase.minecraft.architectury.betterharvesting.config.ConfigHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BetterHarvestingCommand
{
	
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		LiteralArgumentBuilder<CommandSourceStack> cmd = literal("betterharvesting")
				.requires((commandSourceStack) -> commandSourceStack.hasPermission(4))
				.executes(context ->
				{
					context.getSource().sendSuccess(ConfigHandler.getInstance().getAll(), true);
					return 1;
				})
				.then(literal("reload")
						.executes(context ->
						{
							ConfigHandler.getInstance().load();
							context.getSource().sendSuccess(Component.literal("[Better Harvesting] reloaded config"), true);
							return 1;
						}));
		for (String field : ConfigHandler.getInstance().suggestions())
		{
			LiteralArgumentBuilder<CommandSourceStack> sub = literal(field);
			if (ConfigHandler.getInstance().get(field) instanceof Boolean)
			{
				sub = sub.then(argument("value", bool()).executes(ctx -> set(ctx, field, getBool(ctx, "value")) ? 1 : 0));
			} else if (ConfigHandler.getInstance().get(field) instanceof Integer)
			{
				sub = sub.then(argument("value", integer()).executes(ctx -> set(ctx, field, getInteger(ctx, "value")) ? 1 : 0));
			} else if (ConfigHandler.getInstance().get(field) instanceof Float)
			{
				sub = sub.then(argument("value", floatArg()).executes(ctx -> set(ctx, field, getFloat(ctx, "value")) ? 1 : 0));
			} else if (ConfigHandler.getInstance().get(field) instanceof Double)
			{
				sub = sub.then(argument("value", doubleArg()).executes(ctx -> set(ctx, field, getDouble(ctx, "value")) ? 1 : 0));
			} else if (ConfigHandler.getInstance().get(field) instanceof String[] list)
			{
				
				sub = sub
						.then(literal("add")
								.then(argument("value", string())
										.executes(ctx ->
										{
											List<String> tmp = new java.util.ArrayList<>(Arrays.stream(list).toList());
											tmp.add(getString(ctx, "value"));
											return set(ctx, field, tmp.toArray(String[]::new)) ? 1 : 0;
										})))
						.then(literal("remove")
								.then(argument("value", string())
										.suggests((context, builder)-> SharedSuggestionProvider.suggest(list, builder))
										.executes(ctx ->
										{
											List<String> tmp = new java.util.ArrayList<>(Arrays.stream(list).toList());
											tmp.remove(getString(ctx, "value"));
											return set(ctx, field, tmp.toArray(String[]::new)) ? 1 : 0;
										})));
			}
			sub = sub.executes(ctx -> get(ctx, field) ? 1 : 0);
			cmd.then(sub);
		}
		dispatcher.register(cmd);
	}
	
	private static boolean set(CommandContext<CommandSourceStack> context, String name, Object value)
	{
		if (!ConfigHandler.getInstance().exists(name))
		{
			context.getSource().sendFailure(Component.literal("[Better Harvesting] Config does NOT exist: %s".formatted(name)).withStyle(ChatFormatting.RED));
			return false;
		}
		ConfigHandler.getInstance().set(name, value);
		context.getSource().sendSuccess(Objects.requireNonNull(ConfigHandler.getInstance().getAsComponent(name)), true);
		return true;
	}
	
	private static boolean get(CommandContext<CommandSourceStack> context, String name)
	{
		if (!ConfigHandler.getInstance().exists(name))
		{
			context.getSource().sendFailure(Component.literal("[Better Harvesting] Config does NOT exist: %s".formatted(name)).withStyle(ChatFormatting.RED));
			return false;
		}
		context.getSource().sendSuccess(Objects.requireNonNull(ConfigHandler.getInstance().getAsComponent(name)), true);
		return true;
	}
	
}
