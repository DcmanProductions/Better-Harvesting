package chase.minecraft.architectury.betterharvesting.forge;

import chase.minecraft.architectury.betterharvesting.BetterHarvesting;
import chase.minecraft.architectury.betterharvesting.client.BetterHarvestingClient;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BetterHarvesting.MOD_ID)
public class BetterHarvestingForge
{
	public BetterHarvestingForge()
	{
		// Submit our event bus to let architectury register our content on the right time
		EventBuses.registerModEventBus(BetterHarvesting.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		BetterHarvesting.init();
	}
}

