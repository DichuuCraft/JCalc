package com.hadroncfy.vjcalc;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.Player;

public class JCalcCommand implements RawCommand {
    private final IPlugin plugin;

    public JCalcCommand(IPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        String arg = invocation.arguments();
        CommandSource source = invocation.source();
        if (arg.equals("list") && source instanceof Player){
            plugin.sendVariables((Player) source);
        }
        else if (arg.equals("clear") && source instanceof Player){
            plugin.clearVariables((Player) source);
        }
    }
}