package dev.ua.dannsoll.bigbrotheriswatchingyou.listeners;

import dev.ua.dannsoll.bigbrotheriswatchingyou.utils.WebhookUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.List;

public class CommandLogger implements Listener {

    private List<String> notMonitoredCommands;
    private List<String> monitoredPlayers;

    public CommandLogger(List<String> notMonitoredCommands, List<String> monitoredPlayers) {
        this.notMonitoredCommands = notMonitoredCommands;
        this.monitoredPlayers = monitoredPlayers;
    }

    public void updateMonitoredCommands(List<String> monitoredCommands) {
        this.notMonitoredCommands = monitoredCommands;
    }

    public void updateMonitoredPlayers(List<String> monitoredPlayers) {
        this.monitoredPlayers = monitoredPlayers;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String playerName = event.getPlayer().getName();
        String command = event.getMessage().split(" ")[0].toLowerCase().replace("/", "");
        if (monitoredPlayers.contains(playerName) && !notMonitoredCommands.contains(command)) {
            WebhookUtil.sendDiscordMessageAsync(playerName + " issued server command: `" + event.getMessage() + "`");
        }
    }

    @EventHandler
    public void onRconCommand(ServerCommandEvent event) {
        String command = event.getCommand().split(" ")[0].toLowerCase().replace("/", "");
        if (!notMonitoredCommands.contains(command)) {
            CommandSender sender = event.getSender();
            if (sender instanceof RemoteConsoleCommandSender) {
                WebhookUtil.sendDiscordMessageAsync("Rcon issued server command: `" + event.getCommand() + "`");
            } else if (sender instanceof ConsoleCommandSender) {
                WebhookUtil.sendDiscordMessageAsync("Console issued server command: `" + event.getCommand() + "`");
            } else {
                WebhookUtil.sendDiscordMessageAsync("Unknown sender issued server command: `" + event.getCommand() + "`");
            }
        }
    }
}
