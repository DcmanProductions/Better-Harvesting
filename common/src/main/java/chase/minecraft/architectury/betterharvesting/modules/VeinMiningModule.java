package chase.minecraft.architectury.betterharvesting.modules;

import chase.minecraft.architectury.betterharvesting.BetterHarvesting;
import chase.minecraft.architectury.betterharvesting.config.ConfigHandler;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
			
			if (isVeinMine)
			{
				Set<BlockPos> list = getBlocks(level, pos, ConfigHandler.getConfig().VeinMineMaxBlocks);
				breakBlocks(list, player, state, level, pos);
			} else if (isTreeCapitator)
			{
				Set<BlockPos> list = getBlocks(level, pos, ConfigHandler.getConfig().VeinMineMaxBlocks);
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
//			level.blockUpdated(blockPos, Blocks.AIR);
			level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 2);
		}
	}
	
	
	/**
	 * This function calculates the set of block positions to be affected by an explosion in a 16x16x16 cube around a given position.
	 *
	 * @param level   The server level where the explosion is happening.
	 * @param fromPos The position of the center of the explosion.
	 * @return The method is returning a set of BlockPos objects representing the blocks that will be affected by an explosion.
	 */
	public static Set<BlockPos> getBlocks(ServerLevel level, BlockPos fromPos, int range)
	{
		// Create set of block positions to be affected by the explosion
		Set<BlockPos> affectedBlocks = new HashSet<>();
		Block block = level.getBlockState(fromPos).getBlock();
		if (range < 3)
			return affectedBlocks;
		int maxRange = range / 2;
		int minRange = -maxRange;
		for (int x = minRange; x < maxRange; ++x)
		{
			for (int y = minRange; y < maxRange; ++y)
			{
				for (int z = minRange; z < maxRange; z++)
				{
					BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
					// Loop through until explosion radius is zero
					for (int i = 0; i < range; i++)
					{
						pos.setWithOffset(fromPos, x, y, z);
						
						// Check if position is within world bounds
						if (!level.isInWorldBounds(pos))
						{
							break;
						}
						if (level.getBlockState(pos).getBlock().equals(block))
							affectedBlocks.add(pos);
					}
				}
			}
		}
		if (ConfigHandler.getConfig().VeinMineOnlyConnectedBlocks)
			return getConnectedBlockPositions(sortBlockPosByClosest(affectedBlocks, fromPos));
		return affectedBlocks;
	}
	
	private static Set<BlockPos> sortBlockPosByClosest(Set<BlockPos> list, BlockPos startPos)
	{
		try
		{
			
			return list.stream().sorted((a, b) ->
			{
				int distanceA = (int) a.distToCenterSqr(startPos.getCenter());
				int distanceB = (int) b.distToCenterSqr(startPos.getCenter());
				return Integer.compare(distanceA, distanceB);
			}).collect(Collectors.toCollection(LinkedHashSet::new));
		} catch (Exception e)
		{
			BetterHarvesting.log.error("Unable to sort list: {}", e.getMessage(), e);
		}
		return list;
	}
	
	private static Set<BlockPos> getConnectedBlockPositions(Set<BlockPos> list)
	{
		Set<BlockPos> connected = new HashSet<>();
		Set<BlockPos> visited = new HashSet<>();
		for (BlockPos pos : list)
		{
			if (!visited.contains(pos))
			{
				distanceFromSource(pos, list, visited, connected);
			}
		}
		return connected;
	}
	private static final Vec3i[] DIRECTIONS = new Vec3i[]{
			new Vec3i(0, 1, 0),
			new Vec3i(1, 1, 0),
			new Vec3i(-1, 1, 0),
			new Vec3i(0, 1, 1),
			new Vec3i(0, 1, -1),
			new Vec3i(1, 0, 0),
			new Vec3i(-1, 0, 0),
			new Vec3i(0, 0, 1),
			new Vec3i(0, 0, -1),
			new Vec3i(0, -1, 0),
			new Vec3i(1, -1, 0),
			new Vec3i(-1, -1, 0),
			new Vec3i(0, -1, 1),
			new Vec3i(0, -1, -1),
			new Vec3i(1, 1, 1),
			new Vec3i(1, 1, -1),
			new Vec3i(-1, 1, -1),
			new Vec3i(-1, 1, 1),
			new Vec3i(1, 0, 1),
			new Vec3i(1, 0, -1),
			new Vec3i(-1, 0, -1),
			new Vec3i(-1, 0, 1),
			new Vec3i(1, -1, 1),
			new Vec3i(1, -1, -1),
			new Vec3i(-1, -1, -1),
			new Vec3i(-1, -1, 1)
	};
	private static void distanceFromSource(BlockPos pos, Set<BlockPos> list, Set<BlockPos> visited, Set<BlockPos> connected)
	{
		
		visited.add(pos);
		connected.add(pos);
		BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();
		for (Vec3i direction : DIRECTIONS)
		{
			neighbor.setWithOffset(pos, direction);
			if (list.contains(neighbor) && !visited.contains(neighbor))
			{
				distanceFromSource(neighbor, list, visited, connected);
			}
		}
	}
}
