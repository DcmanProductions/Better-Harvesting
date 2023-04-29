package chase.minecraft.architectury.betterharvesting.fabric;

import chase.minecraft.architectury.betterharvesting.BetterHarvesting;
import net.fabricmc.api.ModInitializer;

public class BetterHarvestingFabric implements ModInitializer
{
	@Override
	public void onInitialize()
	{
		BetterHarvesting.init();
	}
}