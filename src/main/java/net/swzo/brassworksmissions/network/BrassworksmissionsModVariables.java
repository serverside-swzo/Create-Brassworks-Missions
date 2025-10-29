package net.swzo.brassworksmissions.network;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.swzo.brassworksmissions.BrassworksmissionsMod;
import net.swzo.brassworksmissions.missions.ActiveMission;
import net.swzo.brassworksmissions.missions.PlayerMissionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@EventBusSubscriber
public class BrassworksmissionsModVariables {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, BrassworksmissionsMod.MODID);
    public static final Supplier<AttachmentType<PlayerVariables>> PLAYER_VARIABLES = ATTACHMENT_TYPES.register("player_variables", () -> AttachmentType.serializable(PlayerVariables::new).build());

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        BrassworksmissionsMod.addNetworkMessage(PlayerVariablesSyncMessage.TYPE, PlayerVariablesSyncMessage.STREAM_CODEC, PlayerVariablesSyncMessage::handleData);
        BrassworksmissionsMod.addNetworkMessage(UpdateSelectedMissionMessage.TYPE, UpdateSelectedMissionMessage.STREAM_CODEC, UpdateSelectedMissionMessage::handleData);
    }

    @EventBusSubscriber
    public static class EventBusVariableHandlers {
        @SubscribeEvent
        public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player)
                player.getData(PLAYER_VARIABLES).syncPlayerVariables(event.getEntity());
        }

        @SubscribeEvent
        public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof ServerPlayer player)
                player.getData(PLAYER_VARIABLES).syncPlayerVariables(event.getEntity());
        }

        @SubscribeEvent
        public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.getEntity() instanceof ServerPlayer player)
                player.getData(PLAYER_VARIABLES).syncPlayerVariables(event.getEntity());
        }

        @SubscribeEvent
        public static void clonePlayer(PlayerEvent.Clone event) {
            PlayerVariables original = event.getOriginal().getData(PLAYER_VARIABLES);
            PlayerVariables clone = new PlayerVariables();
            clone.lastWeeklyResetTime = original.lastWeeklyResetTime;
            clone.SelectedMission = original.SelectedMission;
            clone.reRollAmount = original.reRollAmount;
            clone.hasmissiondata = original.hasmissiondata;
            clone.missionData = original.missionData;
            clone.trackedMissions = new ArrayList<>(original.trackedMissions);
            event.getEntity().setData(PLAYER_VARIABLES, clone);
        }
    }

    public static class PlayerVariables implements INBTSerializable<CompoundTag> {
        public long lastWeeklyResetTime = 1;
        public double SelectedMission = 1.0;
        public int reRollAmount = 1;
        public boolean hasmissiondata = false;
        public PlayerMissionData missionData;
        public List<Integer> trackedMissions;

        public PlayerVariables() {
            this.lastWeeklyResetTime = 1;
            this.SelectedMission = 1.0;
            this.reRollAmount = 1;
            this.trackedMissions = new ArrayList<>();

            this.missionData = new PlayerMissionData();

            final String missionType = "brassworksmissions:disabled";
            final String title = "No Mission";
            final int requiredAmount = 1;
            final String requirementType = "block";
            final int rewardAmount = 1;

            ItemStack requirementStack = new ItemStack(Items.BARRIER, requiredAmount);
            ItemStack rewardStack = new ItemStack(Items.DIAMOND, rewardAmount);

            int fillSlots = Math.min(6, PlayerMissionData.MISSION_SLOTS);
            for (int i = 0; i < fillSlots; i++) {
                ActiveMission mission = new ActiveMission(
                        missionType,
                        title,
                        requirementStack.copy(),
                        requiredAmount,
                        requirementType,
                        rewardStack.copy()
                );
                missionData.setMission(i, mission);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider lookupProvider) {
            CompoundTag nbt = new CompoundTag();
            nbt.putLong("lastWeeklyResetTime", lastWeeklyResetTime);
            nbt.putDouble("SelectedMission", SelectedMission);
            nbt.put("missionData", missionData.serializeNBT(lookupProvider));
            nbt.putInt("reRollAmount", reRollAmount);
            nbt.putBoolean("hasmissiondata", hasmissiondata);
            nbt.putIntArray("trackedMissions", trackedMissions.stream().mapToInt(Integer::intValue).toArray());
            return nbt;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider lookupProvider, CompoundTag nbt) {
            lastWeeklyResetTime = nbt.getLong("lastWeeklyResetTime");
            SelectedMission = nbt.getDouble("SelectedMission");
            reRollAmount = nbt.getInt("reRollAmount");
            hasmissiondata = nbt.getBoolean("hasmissiondata");
            if (nbt.contains("missionData", CompoundTag.TAG_COMPOUND)) {

                missionData.deserializeNBT(lookupProvider, nbt.getCompound("missionData"));
            }
            if (nbt.contains("trackedMissions", CompoundTag.TAG_INT_ARRAY)) {
                trackedMissions = Arrays.stream(nbt.getIntArray("trackedMissions")).boxed().collect(Collectors.toList());
            }
        }

        public void syncPlayerVariables(Entity entity) {
            if (entity instanceof ServerPlayer serverPlayer)
                PacketDistributor.sendToPlayer(serverPlayer, new PlayerVariablesSyncMessage(this));
        }
    }

    public record PlayerVariablesSyncMessage(PlayerVariables data) implements CustomPacketPayload {
        public static final Type<PlayerVariablesSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(BrassworksmissionsMod.MODID, "player_variables_sync"));
        public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVariablesSyncMessage> STREAM_CODEC = StreamCodec
                .of((RegistryFriendlyByteBuf buffer, PlayerVariablesSyncMessage message) -> buffer.writeNbt(message.data().serializeNBT(buffer.registryAccess())), (RegistryFriendlyByteBuf buffer) -> {
                    PlayerVariablesSyncMessage message = new PlayerVariablesSyncMessage(new PlayerVariables());
                    message.data.deserializeNBT(buffer.registryAccess(), Objects.requireNonNull(buffer.readNbt()));
                    return message;
                });

        @Override
        public @NotNull Type<PlayerVariablesSyncMessage> type() {
            return TYPE;
        }

        public static void handleData(final PlayerVariablesSyncMessage message, final IPayloadContext context) {
            if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
                context.enqueueWork(() -> context.player().getData(PLAYER_VARIABLES).deserializeNBT(context.player().registryAccess(), message.data.serializeNBT(context.player().registryAccess()))).exceptionally(e -> {
                    context.connection().disconnect(Component.literal(e.getMessage()));
                    return null;
                });
            }
        }
    }
}
