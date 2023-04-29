package chase.minecraft.architectury.betterharvesting.mixin;

import chase.minecraft.architectury.betterharvesting.BetterHarvesting;
import chase.minecraft.architectury.betterharvesting.config.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.tags.ItemTags.SAPLINGS;

@Mixin(ItemEntity.class)
public class ItemEntityMixin
{
	@Shadow
	private int age;
	
	@Inject(at = @At("HEAD"), method = "tick")
	public void tick(CallbackInfo cb)
	{
		if (ConfigHandler.getInstance().config.AllowAutomaticSaplingReplant)
		{
			ItemEntity item = (ItemEntity) ((Object) this);
			if (item.getItem().getTags().anyMatch(i -> SAPLINGS == i))
			{
				if (this.age >= 5998)
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
						
						item.discard();
					}
					
				}
			}
		}
	}
}
