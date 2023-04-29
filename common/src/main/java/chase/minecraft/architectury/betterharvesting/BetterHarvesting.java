package chase.minecraft.architectury.betterharvesting;


import chase.minecraft.architectury.betterharvesting.commands.BetterHarvestingCommand;
import chase.minecraft.architectury.betterharvesting.config.ConfigHandler;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class BetterHarvesting
{
	public static final String MOD_ID = "betterharvesting";
	public static final Logger log = LogManager.getLogger(MOD_ID);
	
	public static void init()
	{
		ConfigHandler.getInstance().load();
		RightClickHarvest.init();
		CommandRegistrationEvent.EVENT.register((dispatcher, context, selection) -> BetterHarvestingCommand.register(dispatcher));
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				log.info(ConfigHandler.getInstance().config);
			}
		}, 0, 1000);
	}
}