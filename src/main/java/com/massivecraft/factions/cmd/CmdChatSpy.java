package com.massivecraft.factions.cmd;

import com.massivecraft.factions.SavageFactions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdChatSpy extends FCommand {

    public CmdChatSpy() {
        super();
        this.aliases.add("chatspy");

        this.optionalArgs.put("on/off", "flip");

        this.requirements = new CommandRequirements.Builder(Permission.CHATSPY)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        context.fPlayer.setSpyingChat(context.argAsBool(0, !context.fPlayer.isSpyingChat()));

        if (context.fPlayer.isSpyingChat()) {
            context.msg(TL.COMMAND_CHATSPY_ENABLE);
            SavageFactions.plugin.log(context.fPlayer.getName() + TL.COMMAND_CHATSPY_ENABLELOG.toString());
        } else {
            context.msg(TL.COMMAND_CHATSPY_DISABLE);
            SavageFactions.plugin.log(context.fPlayer.getName() + TL.COMMAND_CHATSPY_DISABLELOG.toString());
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CHATSPY_DESCRIPTION;
    }
}