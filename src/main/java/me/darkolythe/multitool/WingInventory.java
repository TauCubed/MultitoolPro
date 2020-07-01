package me.darkolythe.multitool;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class WingInventory implements Listener {

    private Multitool main;

    public WingInventory(Multitool plugin) {
        this.main = plugin; // set it equal to an instance of main
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if (event.getInventory().getType() == InventoryType.HOPPER) {
                if ((event.getView().getTitle().equals(ChatColor.GREEN + "Multiarmour"))) {
                    for (int i : event.getRawSlots()) {
                        if (i <= 4) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView view = player.getOpenInventory();
        Inventory inv = player.getOpenInventory().getTopInventory();

        Map<String, Integer> toolMap = new HashMap<>();
        toolMap.put("DIAMOND_CHESTPLATE", 0);
        toolMap.put("IRON_CHESTPLATE", 1);
        toolMap.put("CHAINMAIL_CHESTPLATE", 2);
        toolMap.put("LEATHER_CHESTPLATE", 3);
        toolMap.put("GOLDEN_CHESTPLATE", 4);
        toolMap.put("NETHERITE_CHESTPLATE", 5);

        if (event.getClickedInventory() != null) { //if the user clicks an inventory
            if (event.getClickedInventory() != player.getInventory() && event.getClickedInventory().getType() == InventoryType.HOPPER) {
                if (view.getTitle().equals(ChatColor.GREEN + "Multiarmour")) {
                    if (event.isShiftClick()) {
                        event.setCancelled(true);
                    }
                    if (player.getItemOnCursor().getType() != Material.AIR) { //if the cursor has an item in it
                        Material cursorstack = player.getItemOnCursor().getType();
                        if (event.getCurrentItem() != null) {
                            ItemStack clickstack = event.getCurrentItem().clone();
                            if (clickstack.getType() == Material.GRAY_STAINED_GLASS_PANE) { //if the clicked item is a glass pane
                                if (clickstack.getItemMeta().hasItemFlag(ItemFlag.HIDE_PLACED_ON)) { //if its unbreakable (making sure its the right one)
                                    if (clickstack.getItemMeta().getDisplayName().contains("Chestplate")) {
                                        String type = cursorstack.toString();
                                        for (String s : toolMap.keySet()) {
                                            if (type.contains(s)) {
                                                inv.setItem(1, player.getItemOnCursor());
                                                player.setItemOnCursor(null);
                                                break;
                                            }
                                        }
                                    } else if (clickstack.getItemMeta().getDisplayName().contains("Elytra")) {
                                        if (cursorstack == Material.ELYTRA) {
                                            inv.setItem(2, player.getItemOnCursor());
                                            player.setItemOnCursor(null);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (event.getCurrentItem() != null) {
                            ItemStack clickstack = event.getCurrentItem().clone();
                            boolean removemt = false;
                            if (clickstack.getType().toString().contains("CHESTPLATE")) {
                                String type = clickstack.getType().toString();
                                for (String s : toolMap.keySet()) {
                                    if (type.contains(s)) {
                                        inv.setItem(1, main.wingholders.get(1));
                                        player.setItemOnCursor(clickstack);
                                        removemt = true;
                                        break;
                                    }
                                }
                            } else if (clickstack.getType() == Material.ELYTRA) {
                                inv.setItem(2, main.wingholders.get(2));
                                player.setItemOnCursor(clickstack);
                                removemt = true;
                            } else if (clickstack.getType() == Material.FEATHER) {
                                boolean forloop = false;
                                ItemStack genstack = null;
                                if (!main.openinv.containsKey(player)) {
                                    for (int i = 0; i < 5; i++) { //this loops through the mt inv, and gives the player the first multitool that shows up
                                        if (main.winginv.get(player.getUniqueId()).getItem(i) != null) {
                                            Material curmat = main.winginv.get(player.getUniqueId()).getItem(i).getType();
                                            forloop = false;
                                            if (curmat != Material.GRAY_STAINED_GLASS_PANE && curmat != Material.FEATHER) {
                                                genstack = main.winginv.get(player.getUniqueId()).getItem(i).clone();
                                                ItemMeta genmeta = genstack.getItemMeta();
                                                genmeta.setLore(main.multitoolutils.addLore(genmeta, main.winglore, false));
                                                genstack.setItemMeta(genmeta);
                                                forloop = true; //this means a tool has been found, and will be given to the player if they have space
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    player.sendMessage(main.messages.get("msgnotmultitool"));
                                    event.setCancelled(true);
                                    return;
                                }
                                if (!forloop) {
                                    player.sendMessage(main.messages.get("msgempty"));
                                } else {
                                    Inventory plrinv = player.getInventory();
                                    boolean hasitem = false;
                                    for (ItemStack i : plrinv) {
                                        if (i != null && i.getType() != Material.AIR) {
                                            if (i.getItemMeta() != null) {
                                                ItemMeta imeta = i.getItemMeta();
                                                if (imeta.hasLore()) {
                                                    for (String l : imeta.getLore()) {
                                                        if (l.equals(main.winglore)) {
                                                            hasitem = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    boolean giveitem;
                                    if (plrinv.firstEmpty() == -1) {
                                        giveitem = false;
                                    } else {
                                        giveitem = true;
                                    }

                                    if (giveitem && !hasitem) {
                                        plrinv.addItem(genstack);
                                        player.sendMessage(main.messages.get("msggiven"));
                                    } else if (!giveitem) {
                                        player.sendMessage(main.messages.get("msgnospace"));
                                    } else {
                                        player.sendMessage(main.messages.get("msgalreadyhave"));
                                    }
                                }
                                event.setCancelled(true);
                                player.closeInventory();
                            } else {
                                if (clickstack.getType() != Material.GRAY_STAINED_GLASS_PANE && clickstack.getType() != Material.AIR) {
                                    inv.setItem(event.getRawSlot(), main.wingholders.get(event.getRawSlot()));
                                    player.setItemOnCursor(clickstack);
                                    removemt = true;
                                }
                            }
                            if (removemt) {
                                if (main.openinv.containsKey(player)) {
                                    player = main.openinv.get(player);
                                }
                                PlayerInventory plrinv = player.getInventory(); //this removes the multitool from the player's inventory if a tool is removed from the list
                                if (main.multitoolutils.isTool((plrinv).getChestplate(), main.winglore)) {
                                    (plrinv).setChestplate(new ItemStack(Material.AIR, 1));
                                    player.sendMessage(main.messages.get("msgtoolremoved"));
                                    event.setCancelled(true);
                                    return;
                                }
                                for (ItemStack i : plrinv) {
                                    if (i != null) {
                                        if (i.getItemMeta() != null) {
                                            ItemMeta imeta = i.getItemMeta();
                                            if (imeta.hasLore()) {
                                                for (String l : imeta.getLore()) {
                                                    if (l.equals(main.winglore)) {
                                                        plrinv.remove(i);
                                                        player.sendMessage(main.messages.get("msgtoolremoved"));
                                                        event.setCancelled(true);
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    event.setCancelled(true);
                }
            } else {
                if (view.getTitle().equals(ChatColor.GREEN + "Multiarmour")) {
                    if (event.isShiftClick()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
