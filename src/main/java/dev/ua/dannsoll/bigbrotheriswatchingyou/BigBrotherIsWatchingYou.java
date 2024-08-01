package dev.ua.dannsoll.bigbrotheriswatchingyou;

import com.mojang.brigadier.Command;
import dev.ua.dannsoll.bigbrotheriswatchingyou.listeners.CommandLogger;
import dev.ua.dannsoll.bigbrotheriswatchingyou.listeners.CreativeLogger;
import dev.ua.dannsoll.bigbrotheriswatchingyou.utils.WebhookUtil;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class BigBrotherIsWatchingYou extends JavaPlugin implements Listener {

    public static BigBrotherIsWatchingYou instance;
    public static String webhookUrl;

    private CommandLogger commandLogger;
    private CreativeLogger creativeLogger;

    @Override
    public void onEnable() {
        loadConfig();
        reloadCommand();
        instance = this;
        commandLogger = new CommandLogger(this.getConfig().getStringList("commands"));
        creativeLogger = new CreativeLogger(this.getConfig().getStringList("players"));
        Bukkit.getPluginManager().registerEvents(creativeLogger, this);
        Bukkit.getPluginManager().registerEvents(commandLogger, this);
        log("Big Brother Is Watching You!");
    }

    @Override
    public void onDisable() {
        log("Big Brother Went On Vacation!");
    }

    private void loadConfig() {
        this.saveDefaultConfig();
        webhookUrl = this.getConfig().getString("webhook-url");

        if (webhookUrl == null || webhookUrl.isEmpty() || webhookUrl.equals("YOUR_DISCORD_WEBHOOK_URL")) {
            getLogger().warning("Discord Webhook URL is not set in config.yml!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private void reloadCommand() {
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                    Commands.literal("bigbro")
                            .executes(ctx -> {
                                ctx.getSource().getSender().sendMessage(Component.text("Reloading BigBrother config...", NamedTextColor.RED));
                                commandLogger.updateMonitoredCommands(this.getConfig().getStringList("commands"));
                                creativeLogger.updateMonitoredPlayers(this.getConfig().getStringList("players"));
                                WebhookUtil.sendDiscordMessageAsync("Big Brother config reloaded!");
                                return Command.SINGLE_SUCCESS;
                            })
                            .build(),
                    "Command to reload the BigBrother config",
                    List.of("bb, bigbrother")
            );
        });
    }


    public void log(String message) {
        getLogger().info(ChatColor.RED + message);
    }

}
