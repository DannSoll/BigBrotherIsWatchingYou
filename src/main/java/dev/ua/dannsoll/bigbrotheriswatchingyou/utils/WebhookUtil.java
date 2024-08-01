package dev.ua.dannsoll.bigbrotheriswatchingyou.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.ua.dannsoll.bigbrotheriswatchingyou.BigBrotherIsWatchingYou;
import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookUtil {

    public static void sendDiscordMessageAsync(String messageContent) {
        Bukkit.getScheduler().runTaskAsynchronously(BigBrotherIsWatchingYou.instance, () -> sendDiscordMessage(messageContent));
    }

    private static void sendDiscordMessage(String messageContent) {
        try {
            URL url = new URL(BigBrotherIsWatchingYou.webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            // Create the JSON payload
            JsonObject json = new JsonObject();
            json.addProperty("username", "Big Brother"); // Optional: Change the bot's display name in Discord

            // Create an embed object
            JsonObject embed = new JsonObject();
            embed.addProperty("title", "Admin activity detected");
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
                BigBrotherIsWatchingYou.instance.log("Discord webhook returned unexpected response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            BigBrotherIsWatchingYou.instance.log("Failed to send message to Discord");
        }
    }

}
