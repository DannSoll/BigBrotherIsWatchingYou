package dev.ua.dannsoll.bigbrotheriswatchingyou.listeners;

import dev.ua.dannsoll.bigbrotheriswatchingyou.utils.WebhookUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class CreativeLogger implements Listener {

    private List<String> monitoredPlayers;

    public CreativeLogger(List<String> monitoredPlayers) {
        this.monitoredPlayers = monitoredPlayers;
    }

    public void updateMonitoredPlayers(List<String> monitoredPlayers) {
        this.monitoredPlayers = monitoredPlayers;
    }

    @EventHandler
    public void onCreativeInventoryClick(InventoryCreativeEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (monitoredPlayers.contains(player.getName())) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    ItemStack item = event.getCursor();
                    if (item.getType() != Material.AIR) {
                        String itemName = item.getType().name();
                        int itemAmount = item.getAmount();
                        String message = player.getName() + " took " + itemAmount + "x " + itemName + " from creative inventory";
                        WebhookUtil.sendDiscordMessageAsync(message);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCreativeDropEvent(PlayerDropItemEvent event) {
        if (monitoredPlayers.contains(event.getPlayer().getName())) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                ItemStack item = event.getItemDrop().getItemStack();
                String itemName = item.getType().name();
                int itemAmount = item.getAmount();
                String message = event.getPlayer().getName() + " dropped " + itemAmount + "x " + itemName + " from creative inventory";
                WebhookUtil.sendDiscordMessageAsync(message);
            }
        }
    }

    @EventHandler
    public void onCreativePlaceEvent(BlockPlaceEvent event) {
        if (monitoredPlayers.contains(event.getPlayer().getName())) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                ItemStack item = event.getItemInHand();
                String itemName = item.getType().name();
                String location = event.getBlockPlaced().getLocation().getBlockX() + ", " + event.getBlockPlaced().getLocation().getBlockY() + ", " + event.getBlockPlaced().getLocation().getBlockZ();
                String message = event.getPlayer().getName() + " placed " + itemName + " at " + location + " while in creative mode";
                WebhookUtil.sendDiscordMessageAsync(message);
            }
        }
    }

    @EventHandler
    public void onInventoryInteraction(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (monitoredPlayers.contains(player.getName())) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        ItemStack item = event.getCurrentItem();
                        if (item != null) {
                            String itemName = item.getType().name();
                            int itemAmount = item.getAmount();
                            String containerType = event.getInventory().getType().name();
                            String message = player.getName() + " moved " + itemAmount + "x " + itemName + " in " + containerType;
                            WebhookUtil.sendDiscordMessageAsync(message);
                        }
                    }

                    if (event.getAction() == InventoryAction.CLONE_STACK) {
                        ItemStack item = event.getCurrentItem();
                        if (item != null && item.getType() != Material.AIR) {
                            String itemName = item.getType().name();
                            int itemAmount = item.getAmount();
                            String containerType = event.getClickedInventory().getType().name();

                            String message = player.getName() + " cloned " + itemAmount + "x " + itemName +
                                    " in " + containerType ;
                            WebhookUtil.sendDiscordMessageAsync(message);
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (monitoredPlayers.contains(player.getName())) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    ItemStack item = event.getOldCursor();
                    if (item != null && item.getType() != Material.AIR) {
                        Map<Integer, ItemStack> newItems = event.getNewItems();
                        int itemAmount = newItems.values().stream().mapToInt(ItemStack::getAmount).sum();

                        String itemName = item.getType().name();
                        String containerType = event.getInventory().getType().name();
                        String message = player.getName() + " moved " + itemAmount + "x " + itemName +
                                " in " + containerType;
                        WebhookUtil.sendDiscordMessageAsync(message);
                    }
                }
            }
        }
    }
}