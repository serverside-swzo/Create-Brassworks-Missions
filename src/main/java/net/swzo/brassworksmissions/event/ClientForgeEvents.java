package net.swzo.brassworksmissions.event;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.swzo.brassworksmissions.BrassworksmissionsMod;
import net.swzo.brassworksmissions.init.KeybindingInit;
import net.swzo.brassworksmissions.network.OpenMissionsUIMessage;

@EventBusSubscriber(modid = BrassworksmissionsMod.MODID, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (KeybindingInit.OPEN_MISSIONS_UI_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new OpenMissionsUIMessage());
        }
    }
}
