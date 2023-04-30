package chase.minecraft.architectury.betterharvesting.mixin;

import chase.minecraft.architectury.betterharvesting.config.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin
{
	
	@Shadow
	public abstract void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource);
	
	@Inject(at = @At("RETURN"), method = "tick")
	void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo cb)
	{
		if (ConfigHandler.getConfig().AllowFastLeafDecay)
		{
			randomTick(blockState, serverLevel, blockPos, randomSource);
		}
	}
}
