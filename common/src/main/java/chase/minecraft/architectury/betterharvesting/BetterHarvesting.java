package chase.minecraft.architectury.betterharvesting;


import chase.minecraft.architectury.betterharvesting.commands.BetterHarvestingCommand;
import chase.minecraft.architectury.betterharvesting.modules.RightClickHarvestModule;
import chase.minecraft.architectury.betterharvesting.modules.VeinMiningModule;
import chase.minecraft.architectury.betterharvesting.networking.BetterHarvestingNetworking;
import com.mojang.brigadier.Command;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class BetterHarvesting
{
	public static HashMap<ServerPlayer, Boolean> isVeinmineKeyDown = new HashMap<>();
	public static final String MOD_ID = "betterharvesting";
	public static final Logger log = LogManager.getLogger(MOD_ID);
	
	public static void init()
	{
		RightClickHarvestModule.init();
		VeinMiningModule.init();
		BetterHarvestingNetworking.ServerNetworking.init();
		CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> BetterHarvestingCommand.register(dispatcher));
	}
	
	/**
	 * Allows the creation of Resource Location with the warp namespace.
	 *
	 * @param name
	 * @return
	 */
	public static ResourceLocation id(String name)
	{
		return new ResourceLocation(MOD_ID, name);
	}
}