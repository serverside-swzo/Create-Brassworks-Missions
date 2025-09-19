package net.swzo.brassworksmissions.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CloseMissionsUI {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player player)
			player.closeContainer();
	}
}