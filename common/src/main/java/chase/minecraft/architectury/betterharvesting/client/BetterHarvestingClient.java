package chase.minecraft.architectury.betterharvesting.client;

import chase.minecraft.architectury.betterharvesting.networking.BetterHarvestingNetworking;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;

public abstract class BetterHarvestingClient
{
	private static boolean isVeinmineKeyDown = false;
	public static final KeyMapping VEINMINE_KEY = new KeyMapping("key.betterharvesting.veinmine", InputConstants.KEY_V, "key.categories.betterharvesting");
	
	public static void init()
	{
		KeyMappingRegistry.register(VEINMINE_KEY);
		PlayerEvent.CHANGE_DIMENSION.register((serverPlayer, old, current) ->
		{
			BetterHarvestingNetworking.ServerNetworking.resetKey();
		});
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player ->
		{
			BetterHarvestingNetworking.ServerNetworking.resetKey();
		});
		ClientTickEvent.CLIENT_POST.register((client) ->
		{
			if (BetterHarvestingClient.isVeinmineKeyDown != VEINMINE_KEY.isDown())
			{
				BetterHarvestingClient.isVeinmineKeyDown = VEINMINE_KEY.isDown();
				FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
				buf.writeBoolean(isVeinmineKeyDown);
				PacketSender.c2s().send(BetterHarvestingNetworking.PING, buf);
				KeyMapping.setAll();
			}
		});
	}
}
