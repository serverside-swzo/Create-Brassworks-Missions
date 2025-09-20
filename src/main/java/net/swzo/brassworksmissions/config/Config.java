package net.swzo.brassworksmissions.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final var builder = new ModConfigSpec.Builder();
        CLIENT = new Client(builder);
        CLIENT_SPEC = builder.build();
    }

    public static class Client {
        public final ModConfigSpec.IntValue HUD_X_OFFSET;
        public final ModConfigSpec.IntValue HUD_Y_OFFSET;
        public final ModConfigSpec.BooleanValue LEFT_ALIGN_HUD;

        public Client(ModConfigSpec.Builder builder) {
            builder.push("Mission HUD");
            HUD_X_OFFSET = builder
                    .comment("X offset for the mission HUD from the corner of the screen.")
                    .defineInRange("hudXOffset", 4, 0, Integer.MAX_VALUE);
            HUD_Y_OFFSET = builder
                    .comment("Y offset for the mission HUD from the corner of the screen.")
                    .defineInRange("hudYOffset", 4, 0, Integer.MAX_VALUE);
            LEFT_ALIGN_HUD = builder
                    .comment("Align the mission HUD to the left side of the screen instead of the right.")
                    .define("leftAlignHud", false);
            builder.pop();
        }
    }
}