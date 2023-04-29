package chase.minecraft.architectury.betterharvesting.config;

public class BetterHarvestingConfig
{
	public boolean AllowFastLeafDecay = true;
	public boolean AllowRightClickHarvest = true;
	public boolean AllowAutomaticSaplingReplant = true;
	
	@Override
	public String toString()
	{
		return "BetterHarvestingConfig {" +
				"AllowFastLeafDecay=" + AllowFastLeafDecay +
				", AllowRightClickHarvest=" + AllowRightClickHarvest +
				", AllowAutomaticSaplingReplant=" + AllowAutomaticSaplingReplant +
				'}';
	}
}
