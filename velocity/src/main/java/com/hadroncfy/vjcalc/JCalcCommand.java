package com.hadroncfy.vjcalc;

import java.util.ArrayList;
import java.util.List;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import org.checkerframework.checker.nullness.qual.NonNull;

public class JCalcCommand implements Command {
    private final IPlugin plugin;

    public JCalcCommand(IPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSource source, String @NonNull [] args) {
        if (args.length == 1){
            String cmd = args[0];
            if (cmd.equals("list") && source instanceof Player){
                plugin.sendVariables((Player) source);
            }
            else if (cmd.equals("clear") && source instanceof Player){
                plugin.clearVariables((Player) source);
            }
        }
    }
    
    @Override
    public List<String> suggest(CommandSource source, String @NonNull [] currentArgs) {
        List<String> ret = new ArrayList<>();
        if (currentArgs.length == 0){
            ret.add("list");
            ret.add("clear");
        }
        return ret;
    }
}