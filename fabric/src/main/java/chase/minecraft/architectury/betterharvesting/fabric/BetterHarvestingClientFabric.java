package chase.minecraft.architectury.betterharvesting.fabric;

import chase.minecraft.architectury.betterharvesting.client.BetterHarvestingClient;
import net.fabricmc.api.ClientModInitializer;

public class BetterHarvestingClientFabric extends BetterHarvestingClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		BetterHarvestingClient.init();
	}
}
