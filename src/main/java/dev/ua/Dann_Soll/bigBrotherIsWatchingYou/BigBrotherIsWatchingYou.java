package dev.ua.Dann_Soll.bigBrotherIsWatchingYou;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class BigBrotherIsWatchingYou extends JavaPlugin implements Listener {

    private String webhookUrl;
    private List<String> detectedCommands;
    private List<String> monitoredPlayers;

    @Override
    public void onEnable() {
        loadConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Big Brother Is Watching You!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Big Brother Is On Vacation!");
    }

    public void loadConfig() {
        this.saveDefaultConfig();
        webhookUrl = this.getConfig().getString("webhook-url");
        detectedCommands = this.getConfig().getStringList("commands");
        monitoredPlayers = this.getConfig().getStringList("players");

        // Check if the webhook URL is set
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            getLogger().warning("Discord Webhook URL is not set in config.yml!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String playerName = event.getPlayer().getName();
        String command = event.getMessage().split(" ")[0].substring(1); // Remove leading '/'

        if (monitoredPlayers.contains(playerName) && detectedCommands.contains(command)) {
            String message = playerName + " issued server command: " + event.getMessage();
            sendDiscordMessageAsync(message);
        }
    }

    @EventHandler
    public void onConsoleCommand(ServerCommandEvent event) {
        String command = event.getCommand();
        String message = "Console issued server command: " + command;
        sendDiscordMessageAsync(message);
    }

    private void sendDiscordMessageAsync(String messageContent) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> sendDiscordMessage(messageContent));
    }

    private void sendDiscordMessage(String messageContent) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            // Create the JSON payload
            JsonObject json = new JsonObject();
            json.addProperty("username", "Big Brother"); // Optional: Change the bot's display name in Discord

            // Create an embed object
            JsonObject embed = new JsonObject();
            embed.addProperty("title", "Command Issued");
            embed.addProperty("description", messageContent);
            embed.addProperty("color", 15258703); // Embed color in decimal (e.g., light blue)

            // Add the embed to a JsonArray
            JsonArray embeds = new JsonArray();
            embeds.add(embed);

            // Add the embeds array to the main JSON payload
            json.add("embeds", embeds);

            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Optionally read the response (to trigger and verify the request)
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                getLogger().warning("Discord webhook returned unexpected response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            getLogger().warning("Failed to send message to Discord");
        }
    }
}
