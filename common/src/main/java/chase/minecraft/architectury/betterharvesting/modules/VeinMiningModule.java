package chase.minecraft.architectury.betterharvesting.modules;

import chase.minecraft.architectury.betterharvesting.BetterHarvesting;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Deque;
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
			if (state.is(BlockTags.BEDS) || state.is(BlockTags.DOORS) || state.is(BlockTags.WOOL_CARPETS))
				return EventResult.pass();
			
			boolean isTreeCapitator = ConfigHandler.getConfig().AllowTreeCapitator && (state.is(BlockTags.LOGS) && (!ConfigHandler.getConfig().TreeCapitatorRequiresTool || (ConfigHandler.getConfig().TreeCapitatorRequiresTool && player.getMainHandItem().is(ItemTags.AXES))));
			boolean isVeinMine = ConfigHandler.getConfig().AllowVeinMining && (!ConfigHandler.getConfig().VeinMineOnlyWhenSneaking || (ConfigHandler.getConfig().VeinMineOnlyWhenSneaking && player.isShiftKeyDown()));
			
			if (BetterHarvesting.isVeinmineKeyDown.containsKey(player))
			{
				isVeinMine = ConfigHandler.getConfig().AllowVeinMining && BetterHarvesting.isVeinmineKeyDown.get(player);
			}
			
			if (!player.isCreative() && state.requiresCorrectToolForDrops())
			{
				if (player.getMainHandItem().getItem() instanceof DiggerItem diggerItem)
				{
					if (!diggerItem.isCorrectToolForDrops(state))
					{
						return EventResult.pass();
					}
				} else
				{
					return EventResult.pass();
				}
			}
			
			if (isVeinMine)
			{
				if (ConfigHandler.getConfig().VeinMineRange <= 0)
					return EventResult.pass();
				Set<BlockPos> list = getConnectedBlocks(level, pos, ConfigHandler.getConfig().VeinMineRange);
				breakBlocks(list, player, state, level, pos);
			} else if (isTreeCapitator)
			{
				if (ConfigHandler.getConfig().TreeCapitatorRange <= 0)
					return EventResult.pass();
				Set<BlockPos> list = getConnectedBlocks(level, pos, ConfigHandler.getConfig().TreeCapitatorRange);
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
				if (EnchantmentHelper.hasSilkTouch(player.getMainHandItem()))
				{
					state.getDrops(builder).forEach((itemStack) ->
					{
						ItemEntity entity = new ItemEntity(level, center.getCenter().x, center.getCenter().y, center.getCenter().z, itemStack);
						entity.setDefaultPickUpDelay();
						level.addFreshEntity(entity);
					});
				} else
				{
					
					Block.dropResources(state, builder);
				}
				player.getMainHandItem().hurtAndBreak(1, player, serverPlayer ->
				{
					serverPlayer.broadcastBreakEvent(EquipmentSlot.MAINHAND);
				});
				
			}
			boolean isDamageableItem = player.getMainHandItem().isDamageableItem();
			if (isDamageableItem)
			{
				int itemDurability = player.getMainHandItem().getMaxDamage() - player.getMainHandItem().getDamageValue();
				boolean itemBroken = itemDurability < (ConfigHandler.getConfig().VeinMinePreventToolBreaking ? 3 : 0);
				if (itemBroken)
				{
					break;
				}
			}
			level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 2 | 8);
		}
	}
	
	public static Set<BlockPos> getConnectedBlocks(ServerLevel level, BlockPos start, int range)
	{
		Set<BlockPos> connectedBlocks = new HashSet<>();
		Block targetBlock = level.getBlockState(start).getBlock();
		Deque<BlockPos> stack = new ArrayDeque<>();
		Set<BlockPos> visited = new HashSet<>();
		stack.push(start);
		
		while (!stack.isEmpty())
		{
			BlockPos currentPos = stack.pop();
			if (!visited.add(currentPos))
			{
				continue; // skip already visited blocks
			}
			connectedBlocks.add(currentPos);
			
			for (int dx = -1; dx <= 1; dx++)
			{
				for (int dy = -1; dy <= 1; dy++)
				{
					for (int dz = -1; dz <= 1; dz++)
					{
						if (dx == 0 && dy == 0 && dz == 0)
						{
							continue; // skip current block
						}
						BlockPos neighbor = currentPos.offset(dx, dy, dz);
						if (!isWithinRange(start, neighbor, range))
						{
							continue; // skip out-of-range blocks
						}
						BlockState neighborState = level.getBlockState(neighbor);
						Block neighborBlock = neighborState.getBlock();
						if (neighborBlock == targetBlock)
						{
							stack.push(neighbor); // add to stack for processing
						}
					}
				}
			}
		}
		
		return connectedBlocks;
	}
	
	private static boolean isWithinRange(BlockPos pos1, BlockPos pos2, int range)
	{
		return pos1.distManhattan(pos2) <= range;
	}
}
