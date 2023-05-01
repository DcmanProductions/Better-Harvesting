package chase.minecraft.architectury.betterharvesting.networking;

import chase.minecraft.architectury.betterharvesting.BetterHarvesting;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.C2SPacketReceiver;
import lol.bai.badpackets.api.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public abstract class BetterHarvestingNetworking
{
	public static final ResourceLocation PING = BetterHarvesting.id("ping");
	
	public static class ServerNetworking extends BetterHarvestingNetworking
	{
		public static void init()
		{
			C2SPacketReceiver.register(PING, ((server, player, handler, buf, responseSender) ->
			{
				BetterHarvesting.isVeinmineKeyDown.put(player, buf.readBoolean());
			}));
		}
		
		public static void resetKey(){
			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
			buf.writeBoolean(false);
			PacketSender.c2s().send(BetterHarvestingNetworking.PING, buf);
		}
	}
}
