package com.wxffxx.touhoubrews.command;

import com.mojang.brigadier.CommandDispatcher;
import com.wxffxx.touhoubrews.config.BrewConfigManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ReloadCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext buildContext,
                                Commands.CommandSelection selection) {
        dispatcher.register(
            Commands.literal("touhoubrews")
                .requires(src -> src.hasPermission(2)) // operator level 2
                .then(Commands.literal("reload")
                    .executes(ctx -> {
                        boolean success = BrewConfigManager.load();
                        if (success) {
                            ctx.getSource().sendSuccess(
                                () -> Component.literal("[TouhouBrews] Config reloaded successfully."),
                                true
                            );
                        } else {
                            ctx.getSource().sendFailure(
                                Component.literal("[TouhouBrews] Config reload failed — check logs.")
                            );
                        }
                        return success ? 1 : 0;
                    })
                )
        );
    }
}
