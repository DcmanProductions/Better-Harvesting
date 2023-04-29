package chase.minecraft.architectury.betterharvesting;

import chase.minecraft.architectury.betterharvesting.config.ConfigHandler;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class RightClickHarvest
{
	public static void init()
	{
		InteractionEvent.RIGHT_CLICK_BLOCK.register(RightClickHarvest::execute);
	}
	
	/**
	 * This function executes the harvesting of a crop block by a player in Minecraft, including awarding stats, causing food exhaustion, dropping items, and resetting the block state.
	 *
	 * @param player The player who triggered the event.
	 * @param hand   The hand with which the player is interacting with the block (either main hand or off-hand).
	 * @param pos    The BlockPos parameter represents the position of the block being interacted with. It is a 3D coordinate consisting of x, y, and z values.
	 * @param face   The `face` parameter is of type `Direction` and represents the direction that the player is facing when interacting with the block. It can be used to determine which side of the block the player is interacting with.
	 * @return The method is returning an instance of the EventResult class with the pass() method called on it.
	 */
	private static EventResult execute(Player player, InteractionHand hand, BlockPos pos, Direction face)
	{
		if (ConfigHandler.getInstance().config.AllowRightClickHarvest)
		{
			
			if (player.getLevel() instanceof ServerLevel level)
			{
				if (hand == InteractionHand.MAIN_HAND)
				{
					
					BlockState state = level.getBlockState(pos);
					if (state.getBlock() instanceof CropBlock crop)
					{
						if (crop.isMaxAge(state))
						{
							player.awardStat(Stats.BLOCK_MINED.get(crop));
							player.causeFoodExhaustion(0.005F);
							ItemStack stack = new ItemStack(crop.asItem());
							LootContext.Builder builder = (new LootContext.Builder(level))
									.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
									.withParameter(LootContextParams.BLOCK_STATE, state)
									.withOptionalParameter(LootContextParams.THIS_ENTITY, player)
									.withParameter(LootContextParams.TOOL, player.getMainHandItem());
							Item seed = crop.getCloneItemStack(level, pos, state).getItem();
							state.getDrops(builder).forEach((itemStackx) ->
							{
								if (itemStackx.getItem().equals(seed))
								{
									itemStackx.setCount(itemStackx.getCount() - 1);
								}
								Block.popResource(level, pos, itemStackx);
							});
							level.setBlock(pos, state.getBlock().defaultBlockState(), 0);
							player.getMainHandItem().hurtAndBreak(1, player, l ->
							{
								l.broadcastBreakEvent(EquipmentSlot.MAINHAND);
							});
						}
					}
				}
			}
		}
		return EventResult.pass();
	}
}
