package com.hadroncfy.vjcalc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.inject.Inject;
import com.hadroncfy.jcalc.parser.CompilationException;
import com.hadroncfy.jcalc.parser.TextRange;
import com.hadroncfy.jcalc.run.Complex;
import com.hadroncfy.jcalc.run.Context;
import com.hadroncfy.jcalc.run.ExecutionException;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import org.slf4j.Logger;

import net.kyori.text.TextComponent;
import net.kyori.text.format.Style;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;

@Plugin(id = "vjcalc", name = "In-game calculator", version = "1.0", description = "", authors = "hadroncfy")
public class CalcPlugin implements IPlugin {
    @Inject
    private ProxyServer server;
    @Inject
    private Logger logger;

    private final Context root = new Context(null);
    private final Map<UUID, Context> ctxs = new HashMap<>();

    @Subscribe
    public void onPlayerConnected(DisconnectEvent event) {
        ctxs.remove(event.getPlayer().getUniqueId());
    }

    private static TextComponent normalText(String s) {
        return TextComponent.of(s, Style.of(TextColor.GRAY));
    }

    private static TextComponent errorText(String s) {
        return TextComponent.of(s, Style.of(TextColor.YELLOW, TextDecoration.UNDERLINED));
    }

    private static TextComponent makeErrorText(String input, TextRange range) {
        int a = range.getStartColumn();
        int b = range.getEndColumn();
        if (a >= input.length()) {
            return normalText(input).append(errorText(" "));
        } else {
            String s1 = a > 0 ? input.substring(0, a) : "";
            String err = input.substring(a, b);
            String s2 = input.substring(b, input.length());
            return normalText(s1).append(errorText(err)).append(normalText(s2));
        }
    }

    private Context getOrCreateContext(UUID uuid) {
        Context ctx = ctxs.get(uuid);
        if (ctx != null) {
            return ctx;
        } else {
            ctx = new Context(root);
            ctxs.put(uuid, ctx);
            return ctx;
        }
    }

    private void sendResult(Player player, Complex c) {
        server.broadcast(
                TextComponent.of(player.getUsername() + " > ", TextColor.GREEN).append(TextComponent.of(c.toString(), TextColor.AQUA)));
    }

    private void sendError(Player player, String msg, String input, TextRange range){
        player.sendMessage(TextComponent.of(msg + ": ", TextColor.RED).append(makeErrorText(input, range)));
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        String msg = event.getMessage();
        if (msg.startsWith("==")) {
            msg = msg.substring(2);
            Context ctx = getOrCreateContext(event.getPlayer().getUniqueId());
            try {
                sendResult(event.getPlayer(), ctx.eval(msg));
            } catch (CompilationException e) {
                sendError(event.getPlayer(), e.getMessage(), msg, e.getRange());
            } catch (ExecutionException e) {
                sendError(event.getPlayer(), e.getMessage(), msg, e.getRange());
            }
          
        }
    }

    @Override
    public void sendVariables(Player player) {
        Context ctx = ctxs.get(player.getUniqueId());
        if (ctx != null){
            ctx.getVariables().forEach((name, val) -> {
                
            });
        }
    }

    @Override
    public void clearVariables(Player player) {
        ctxs.remove(player.getUniqueId());
    }
}