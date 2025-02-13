package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.SavageFactions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.FactionWarpsFrame;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.material.Command;

import java.util.UUID;

public class CmdFWarp extends FCommand {

    public CmdFWarp() {
        super();
        this.aliases.add("warp");
        this.aliases.add("warps");
        this.optionalArgs.put("warpname", "warpname");
        this.optionalArgs.put("password", "password");

        this.requirements = new CommandRequirements.Builder(Permission.WARP)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.WARP)
                .build();
    }

    @Override
    public void perform(CommandContext context) {

        if (context.args.size() == 0) {
            new FactionWarpsFrame(context.faction).buildGUI(context.fPlayer);
        } else if (context.args.size() > 2) {
            context.msg(TL.COMMAND_FWARP_COMMANDFORMAT);
        } else {
            final String warpName = context.argAsString(0);
            final String passwordAttempt = context.argAsString(1);

            if (context.faction.isWarp(context.argAsString(0))) {

                // Check if requires password and if so, check if valid. CASE SENSITIVE
                if (context.faction.hasWarpPassword(warpName) && !context.faction.isWarpPassword(warpName, passwordAttempt)) {
                    context.faction.msg(TL.COMMAND_FWARP_INVALID_PASSWORD);
                    return;
                }

                // Check transaction AFTER password check.
                if (!transact(context.fPlayer, context)) return;

                final FPlayer fPlayer = context.fPlayer;
                final UUID uuid = context.player.getUniqueId();
                context.doWarmUp(WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, warpName, () -> {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            player.teleport(fPlayer.getFaction().getWarp(warpName).getLocation());
                            fPlayer.msg(TL.COMMAND_FWARP_WARPED, warpName);
                        }
                }, SavageFactions.plugin.getConfig().getLong("warmups.f-warp", 0));
            } else { context.msg(TL.COMMAND_FWARP_INVALID_WARP, warpName); }
        }
    }

    private boolean transact(FPlayer player, CommandContext context) {
        return !SavageFactions.plugin.getConfig().getBoolean("warp-cost.enabled", false) || player.isAdminBypassing() || context.payForCommand(SavageFactions.plugin.getConfig().getDouble("warp-cost.warp", 5), TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FWARP_DESCRIPTION;
    }
}
