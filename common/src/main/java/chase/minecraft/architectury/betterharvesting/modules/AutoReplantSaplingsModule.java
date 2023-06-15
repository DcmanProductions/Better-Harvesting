package chase.minecraft.architectury.betterharvesting.modules;

import chase.minecraft.architectury.betterharvesting.BetterHarvesting;
import chase.minecraft.architectury.betterharvesting.config.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.tags.ItemTags.SAPLINGS;

public class AutoReplantSaplingsModule
{
	public static void AttemptReplantSapling(ItemEntity item)
	{
		if (ConfigHandler.getConfig().AllowAutomaticSaplingReplant)
		{
			if (item.getItem().getTags().anyMatch(i -> SAPLINGS == i))
			{
				if (item.getAge() >= ConfigHandler.getConfig().AutoPlantSaplingAfterTicks)
				{
					Level level = item.level;
					BlockPos groundPos = item.getOnPos();
					BlockPos spawnPos = groundPos.above();
					BlockState groundState = level.getBlockState(groundPos);
					BlockState spawnState = level.getBlockState(spawnPos);
					boolean canSpawn = spawnState.isAir() && groundState.is(BlockTags.DIRT);
					if (canSpawn)
					{
						try
						{
							BlockItem blockItem = (BlockItem) item.getItem().getItem();
							level.setBlock(spawnPos, blockItem.getBlock().defaultBlockState(), 2);
							BetterHarvesting.log.debug("Planting sapling before despawn: {}, at: [X: {}, Y: {}, Z:{}]", item.getName().getString(), spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
						} catch (ClassCastException e)
						{
							BetterHarvesting.log.error("Unable to plant sapling before despawn: {}, at: [X: {}, Y: {}, Z:{}], {}", item.getName().getString(), spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), e.getMessage());
						}
						if (item.getItem().getCount() == 1)
						{
							item.discard();
						} else
						{
							
							item.getItem().setCount(item.getItem().getCount() - 1);
						}
					}
					
				}
			}
		}
	}
}
