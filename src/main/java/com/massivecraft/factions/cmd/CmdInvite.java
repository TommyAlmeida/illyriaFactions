package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.SavageFactions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

public class CmdInvite extends FCommand {

    public CmdInvite() {
        super();
        this.aliases.add("invite");
        this.aliases.add("inv");

        this.requiredArgs.add("player name");

        this.requirements = new CommandRequirements.Builder(Permission.INVITE)
                .playerOnly()
                .withAction(PermissableAction.INVITE)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        FPlayer target = context.argAsBestFPlayerMatch(0);
        if (target == null) {
            return;
        }

        if (target.getFaction() == context.faction) {
            context.msg(TL.COMMAND_INVITE_ALREADYMEMBER, target.getName(), context.faction.getTag());
            context.msg(TL.GENERIC_YOUMAYWANT.toString() + SavageFactions.plugin.cmdBase.cmdKick.getUseageTemplate(context));
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!context.payForCommand(Conf.econCostInvite, TL.COMMAND_INVITE_TOINVITE.toString(), TL.COMMAND_INVITE_FORINVITE.toString())) {
            return;
        }

        if (context.faction.isInvited(target)) {
            context.msg(TL.COMMAND_INVITE_ALREADYINVITED, target.getName());
            return;
        }
        if (context.faction.isBanned(target)) {
            context.msg(TL.COMMAND_INVITE_BANNED, target.getName());
            return;
        }

        context.faction.invite(target);
        // Send the invitation to the target player when online, otherwise just ignore
        if (target.isOnline()) {
            // Tooltips, colors, and commands only apply to the string immediately before it.
            FancyMessage message = new FancyMessage(context.fPlayer.describeTo(target, true))
                    .tooltip(TL.COMMAND_INVITE_CLICKTOJOIN.toString())
                    .command("/" + Conf.baseCommandAliases.get(0) + " join " + context.faction.getTag())
                    .then(TL.COMMAND_INVITE_INVITEDYOU.toString())
                    .color(ChatColor.YELLOW)
                    .tooltip(TL.COMMAND_INVITE_CLICKTOJOIN.toString())
                    .command("/" + Conf.baseCommandAliases.get(0) + " join " + context.faction.getTag())
                    .then(context.faction.describeTo(target)).tooltip(TL.COMMAND_INVITE_CLICKTOJOIN.toString())
                    .command("/" + Conf.baseCommandAliases.get(0) + " join " + context.faction.getTag());

            message.send(target.getPlayer());
        }

        context.faction.msg(TL.COMMAND_INVITE_INVITED, context.fPlayer.describeTo(context.faction, true), target.describeTo(context.faction));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_INVITE_DESCRIPTION;
    }

}
