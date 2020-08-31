package com.hadroncfy.vjcalc;

import com.velocitypowered.api.proxy.Player;

public interface IPlugin {
    void sendVariables(Player player);
    void clearVariables(Player player);
}