package net.swzo.brassworksmissions.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.swzo.brassworksmissions.BrassworksmissionsMod;

public record UpdateSelectedMissionMessage(int selectedSlot) implements CustomPacketPayload {

    public static final Type<UpdateSelectedMissionMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BrassworksmissionsMod.MODID, "update_selected_mission"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSelectedMissionMessage> STREAM_CODEC = StreamCodec.of(
            (buffer, message) -> buffer.writeInt(message.selectedSlot),
            (buffer) -> new UpdateSelectedMissionMessage(buffer.readInt())
    );

    @Override
    public Type<UpdateSelectedMissionMessage> type() {
        return TYPE;
    }

    public static void handleData(final UpdateSelectedMissionMessage message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.SERVERBOUND) {
            context.enqueueWork(() -> {

                if (context.player() instanceof ServerPlayer player) {
                    BrassworksmissionsModVariables.PlayerVariables playerVariables = player.getData(BrassworksmissionsModVariables.PLAYER_VARIABLES);
                    playerVariables.SelectedMission = message.selectedSlot();
                }
            });
        }
    }
}