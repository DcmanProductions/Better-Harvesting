package chase.minecraft.architectury.betterharvesting.modules;

import chase.minecraft.architectury.betterharvesting.config.ConfigHandler;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class VeinMiningModule
{
	public static void init()
	{
		BlockEvent.BREAK.register((clientlevel, pos, state, player, xp) -> execute(clientlevel, pos, state, player));
		
	}
	
	private static EventResult execute(Level clientlevel, BlockPos pos, BlockState state, ServerPlayer player)
	{
		if (clientlevel instanceof ServerLevel level)
		{
			boolean isTreeCapitator = ConfigHandler.getConfig().AllowTreeCapitator && (state.is(BlockTags.LOGS) && (!ConfigHandler.getConfig().TreeCapitatorRequiresTool || (ConfigHandler.getConfig().TreeCapitatorRequiresTool && player.getMainHandItem().is(ItemTags.AXES))));
			boolean isVeinMine = ConfigHandler.getConfig().AllowVeinMining && (!ConfigHandler.getConfig().VeinMineOnlyWhenSneaking || (ConfigHandler.getConfig().VeinMineOnlyWhenSneaking && player.isCrouching()));
			if (isTreeCapitator)
			{
				Set<BlockPos> list = getBlocks(level, pos);
				breakBlocks(list, player, state, level, pos);
			}
			if (isVeinMine)
			{
				Set<BlockPos> list = getBlocks(level, pos);
				breakBlocks(list, player, state, level, pos);
			}
		}
		return EventResult.pass();
	}
	
	private static void breakBlocks(Set<BlockPos> list, ServerPlayer player, BlockState state, ServerLevel level, BlockPos center)
	{
		for (BlockPos blockPos : list)
		{
			player.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
			if (!player.isCreative())
			{
				player.causeFoodExhaustion(0.01F);
				LootContext.Builder builder = (new LootContext.Builder(level))
						.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(center))
						.withParameter(LootContextParams.BLOCK_STATE, state)
						.withOptionalParameter(LootContextParams.THIS_ENTITY, player)
						.withParameter(LootContextParams.TOOL, player.getMainHandItem());
				state.getDrops(builder).forEach((itemStacks) ->
				{
					Block.popResource(level, center, itemStacks);
				});
				state.spawnAfterBreak(level, center, ItemStack.EMPTY, true);
				player.getMainHandItem().hurtAndBreak(1, player, l ->
				{
					l.broadcastBreakEvent(EquipmentSlot.MAINHAND);
				});
			}
			level.destroyBlock(blockPos, false, player);
		}
	}
	
	private static Set<BlockPos> getBlocks(ServerLevel level, BlockPos fromPos)
	{
		// Create set of block positions to be affected by the explosion
		Set<BlockPos> affectedBlocks = new HashSet<>();
		Block block = level.getBlockState(fromPos).getBlock();
		
		// Loop through 16x16x16 cube around the explosion
		for (int x = 0; x < 16; ++x)
		{
			for (int y = 0; y < 16; ++y)
			{
				for (int z = 0; z < 16; ++z)
				{
					// Check if on the outer edge of the cube
					if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15)
					{
						// Calculate vector based on distance from center of explosion
						double dx = (float) x / 15.0F * 2.0F - 1.0F;
						double dy = (float) y / 15.0F * 2.0F - 1.0F;
						double dz = (float) z / 15.0F * 2.0F - 1.0F;
						double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
						dx /= distance;
						dy /= distance;
						dz /= distance;
						
						// Calculate explosion radius based on random number and multiply by 0.7 to 1.3
						float radius = ConfigHandler.getConfig().VeinMineMaxBlocks;
						
						// Set initial explosion position
						double posX = fromPos.getX();
						double posY = fromPos.getY();
						double posZ = fromPos.getZ();
						
						// Loop through until explosion radius is zero
						for (; radius > 0.0F; radius -= 0.22500001F)
						{
							// Get block position and state of current position
							BlockPos pos = BlockPos.containing(posX, posY, posZ);
							
							// Check if position is within world bounds
							if (!level.isInWorldBounds(pos))
							{
								break;
							}
							if (level.getBlockState(pos).getBlock().equals(block))
								affectedBlocks.add(pos);
							
							// Move explosion position based on vector direction
							posX += dx * 0.30000001192092896;
							posY += dy * 0.30000001192092896;
							posZ += dz * 0.30000001192092896;
						}
					}
				}
			}
		}
		return affectedBlocks;
	}
	
	
}
