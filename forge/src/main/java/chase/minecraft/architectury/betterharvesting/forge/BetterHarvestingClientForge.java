package chase.minecraft.architectury.betterharvesting.forge;

import chase.minecraft.architectury.betterharvesting.BetterHarvesting;
import chase.minecraft.architectury.betterharvesting.client.BetterHarvestingClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BetterHarvesting.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BetterHarvestingClientForge
{
	public BetterHarvestingClientForge()
	{
		BetterHarvestingClient.init();
	}
	
}
