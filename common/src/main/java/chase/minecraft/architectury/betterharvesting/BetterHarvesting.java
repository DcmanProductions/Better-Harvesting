package chase.minecraft.architectury.betterharvesting;


import chase.minecraft.architectury.betterharvesting.commands.BetterHarvestingCommand;
import chase.minecraft.architectury.betterharvesting.modules.RightClickHarvestModule;
import chase.minecraft.architectury.betterharvesting.modules.VeinMiningModule;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BetterHarvesting
{
	public static final String MOD_ID = "betterharvesting";
	public static final Logger log = LogManager.getLogger(MOD_ID);
	
	public static void init()
	{
		RightClickHarvestModule.init();
		VeinMiningModule.init();
		CommandRegistrationEvent.EVENT.register((dispatcher, context, selection) -> BetterHarvestingCommand.register(dispatcher));
	}
}