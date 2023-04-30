package chase.minecraft.architectury.betterharvesting.mixin;

import chase.minecraft.architectury.betterharvesting.modules.AutoReplantSaplingsModule;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin
{
	@Inject(at = @At("HEAD"), method = "tick")
	public void tick(CallbackInfo cb)
	{
		AutoReplantSaplingsModule.AttemptReplantSapling((ItemEntity) ((Object) this));
	}
	
}
