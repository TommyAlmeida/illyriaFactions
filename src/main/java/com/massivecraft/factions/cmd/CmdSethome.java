package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSethome extends FCommand {

    public CmdSethome() {
        this.aliases.add("sethome");
        this.optionalArgs.put("faction tag", "mine");

        this.requirements = new CommandRequirements.Builder(Permission.SETHOME)
                .playerOnly()
                .memberOnly()
                .withAction(PermissableAction.SETHOME)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!Conf.homesEnabled) {
            context.msg(TL.COMMAND_SETHOME_DISABLED);
            return;
        }

        Faction faction = context.argAsFaction(0, context.faction);
        if (faction == null) {
            return;
        }

        // Can the player set the faction home HERE?
        if (!Permission.BYPASS.has(context.player) &&
                Conf.homesMustBeInClaimedTerritory &&
                Board.getInstance().getFactionAt(new FLocation(context.player)) != faction) {
            context.msg(TL.COMMAND_SETHOME_NOTCLAIMED);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!context.payForCommand(Conf.econCostSethome, TL.COMMAND_SETHOME_TOSET, TL.COMMAND_SETHOME_FORSET)) {
            return;
        }

        faction.setHome(context.player.getLocation());

        faction.msg(TL.COMMAND_SETHOME_SET, context.fPlayer.describeTo(context.faction, true));
        faction.sendMessage(SavageFactions.plugin.cmdBase.cmdHome.getUseageTemplate(context));
        if (faction != context.faction) {
            context.msg(TL.COMMAND_SETHOME_SETOTHER, faction.getTag(context.fPlayer));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETHOME_DESCRIPTION;
    }

}