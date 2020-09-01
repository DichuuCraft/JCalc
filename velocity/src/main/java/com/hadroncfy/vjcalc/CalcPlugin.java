package com.hadroncfy.vjcalc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.hadroncfy.jcalc.parser.CompilationException;
import com.hadroncfy.jcalc.parser.TextRange;
import com.hadroncfy.jcalc.run.Complex;
import com.hadroncfy.jcalc.run.Context;
import com.hadroncfy.jcalc.run.ExecutionException;
import com.hadroncfy.jcalc.run.Mathx;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import org.slf4j.Logger;

import net.kyori.text.Component;
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
    @Inject
    @DataDirectory
    private Path dataPath;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Config config;

    private final Context root = new Context(null);
    private final Map<UUID, Context> ctxs = new HashMap<>();
    private final Lang lang = new Lang();

    {
        root.defFunctions(Mathx.class);
        try {
            lang.load("zh_cn");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() throws IOException {
        File dir = dataPath.toFile();
        if (!dir.exists()){
            if (!dir.mkdirs()){
                throw new IOException("Failed to create data directory");
            }
        }
        File jsonFile = dataPath.resolve("config.json").toFile();
        if (jsonFile.exists()){
            try (Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8)){
                config = gson.fromJson(reader, Config.class);
            }
        }
        else {
            config = new Config();
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(jsonFile))){
            writer.write(gson.toJson(config));
        }
        updateConfig();
    }

    @Subscribe
    public void onProxyInitialized(ProxyInitializeEvent event){
        server.getCommandManager().register(new JCalcCommand(this), "jcalc");
        try {
            loadConfig();
        } catch (Exception e) {
            logger.error("Failed to load config", e);
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onReload(ProxyReloadEvent event){
        try {
            loadConfig();
        } catch (Exception e) {
            logger.error("Failed to load config", e);
            e.printStackTrace();
        }
    }

    private void updateConfig(){
        for (Context ctx: ctxs.values()){
            ctx.setVariableLimit(config.variableLimit);
        }
    }

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
            ctx.setVariableLimit(config.variableLimit);
            return ctx;
        }
    }

    private void sendResult(Player player, List<Complex> c) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Complex a: c){
            if (!first){
                sb.append(", ");
            }
            first = false;
            sb.append(a.toString());
        }
        server.getScheduler().buildTask(this, () -> {
            server.broadcast(
                    TextComponent.of(player.getUsername() + " > ", TextColor.GREEN).append(TextComponent.of(sb.toString(), TextColor.AQUA)));
        }).delay(config.messageDelay, TimeUnit.MILLISECONDS).schedule();
    }

    private void sendError(Player player, String msg, String input, TextRange range){
        server.getScheduler().buildTask(this, () -> {
            player.sendMessage(TextComponent.of(lang.getString(msg) + ": ", TextColor.RED).append(makeErrorText(input, range)));
        }).delay(config.messageDelay, TimeUnit.MILLISECONDS).schedule();
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        String msg = event.getMessage();
        if (msg.startsWith(config.triggerPrefix)) {
            msg = msg.substring(config.triggerPrefix.length());
            Context ctx = getOrCreateContext(event.getPlayer().getUniqueId());
            try {
                sendResult(event.getPlayer(), ctx.evalList(msg));
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
                Component m = TextComponent.of(name + " = ", TextColor.GREEN).append(TextComponent.of(val.toString(), TextColor.AQUA));
                player.sendMessage(m);
            });
        }
    }

    @Override
    public void clearVariables(Player player) {
        ctxs.remove(player.getUniqueId());
    }
}