package net.swzo.brassworksmissions.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.swzo.brassworksmissions.missions.MissionController;
import net.swzo.brassworksmissions.procedures.OpenMissionsUI;

@EventBusSubscriber
public class MissionCommand {

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("missions")

                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    OpenMissionsUI.execute(player.level(), player.getX(), player.getY(), player.getZ(), player);
                    return 1;
                })

                .then(Commands.literal("reassign")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> reassignMissions(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> reassignMissions(context.getSource(), EntityArgument.getPlayer(context, "target")))
                        )
                )

                .then(Commands.literal("reroll")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("slot", IntegerArgumentType.integer(1, 6))
                                .executes(context -> rerollMission(context.getSource(), context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "slot")))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(context -> rerollMission(context.getSource(), EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "slot")))
                                )
                        )
                )

                .then(Commands.literal("claim")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("slot", IntegerArgumentType.integer(1, 6))
                                .executes(context -> claimMission(context.getSource(), context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "slot")))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(context -> claimMission(context.getSource(), EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "slot")))
                                )
                        )
                )

                .then(Commands.literal("claim_all")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> claimAllMissions(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> claimAllMissions(context.getSource(), EntityArgument.getPlayer(context, "target")))
                        )
                )

                .then(Commands.literal("complete")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("slot", IntegerArgumentType.integer(1, 6))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(context -> completeMission(context.getSource(), EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "slot")))
                                )
                        )
                )

                .then(Commands.literal("reset_reroll_cost")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> resetRerollCost(context.getSource(), EntityArgument.getPlayer(context, "target")))
                        )
                )
        );
    }

    private static int reassignMissions(CommandSourceStack source, ServerPlayer target) {
        MissionController.reassignMissions(target);
        boolean isSelf = source.getPlayer() == target;
        if (isSelf) {
            source.sendSuccess(() -> Component.literal("§aYour missions have been reassigned."), false);
        } else {
            source.sendSuccess(() -> Component.literal("§aReassigned all missions for " + target.getName().getString()), true);
            target.sendSystemMessage(Component.literal("§eAn administrator has reassigned your missions."));
        }
        return 1;
    }

    private static int rerollMission(CommandSourceStack source, ServerPlayer target, int slot) {

        MissionController.forceRerollMission(target, slot - 1);
        boolean isSelf = source.getPlayer() == target;
        if (!isSelf) {

            source.sendSuccess(() -> Component.literal("§eAttempted to reroll mission in slot " + slot + " for " + target.getName().getString()), true);
        }

        return 1;
    }

    private static int claimMission(CommandSourceStack source, ServerPlayer target, int slot) {

        boolean success = MissionController.claimReward(target, slot - 1);
        boolean isSelf = source.getPlayer() == target;
        if (success) {
            if (isSelf) {
                source.sendSuccess(() -> Component.literal("§aSuccessfully claimed reward in slot " + slot + "."), false);
            } else {
                source.sendSuccess(() -> Component.literal("§aSuccessfully claimed reward in slot " + slot + " for " + target.getName().getString()), true);
            }
        } else {
            source.sendFailure(Component.literal("§cFailed to claim reward in slot " + slot + ". (Is it complete and unclaimed?)"));
        }
        return success ? 1 : 0;
    }

    private static int claimAllMissions(CommandSourceStack source, ServerPlayer target) {
        boolean success = MissionController.claimAllRewards(target);
        boolean isSelf = source.getPlayer() == target;
        if (success) {
            if (isSelf) {
                source.sendSuccess(() -> Component.literal("§aSuccessfully claimed all available rewards."), false);
            } else {
                source.sendSuccess(() -> Component.literal("§aSuccessfully claimed all available rewards for " + target.getName().getString()), true);
            }
        } else {
            source.sendFailure(Component.literal("§cNo available mission rewards to claim for " + target.getName().getString()));
        }
        return success ? 1 : 0;
    }

    private static int completeMission(CommandSourceStack source, ServerPlayer target, int slot) {

        boolean success = MissionController.forceCompleteMission(target, slot - 1);
        if (success) {
            source.sendSuccess(() -> Component.literal("§aForce-completed mission in slot " + slot + " for " + target.getName().getString()), true);
            target.sendSystemMessage(Component.literal("§eAn administrator has completed your mission in slot " + slot + "."));
        } else {
            source.sendFailure(Component.literal("§cFailed to complete mission in slot " + slot + ". (Is it non-existent or already complete?)"));
        }
        return success ? 1 : 0;
    }

    private static int resetRerollCost(CommandSourceStack source, ServerPlayer target) {
        MissionController.resetRerollCost(target);
        source.sendSuccess(() -> Component.literal("§aReset reroll cost for " + target.getName().getString()), true);
        target.sendSystemMessage(Component.literal("§eAn administrator has reset your mission reroll cost."));
        return 1;
    }
}