package me.darkolythe.multitool;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WingDetect implements Listener {

    private Multitool main;

    public WingDetect(Multitool plugin) {
        this.main = plugin; // set it equal to an instance of main
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(player.getWorld().getName())) {
            return;
        }
        
        if (player.hasPermission("multitool.multiarmour")) {
            if (player.getInventory().getChestplate() != null) {
                ItemStack chest = player.getInventory().getChestplate().clone();
                if (main.multitoolutils.isMultiArmour(chest)) {
                    Inventory inv = main.multitoolutils.getWingInv(player);
                    Material block = event.getPlayer().getLocation().getBlock().getType();
                    if (!player.isOnGround() && (block != Material.WATER) && (block != Material.LADDER) && (block != Material.VINE) &&
                            (block != Material.LAVA) && (block != Material.COBWEB)) {
                        if (chest.getType().name().contains("CHESTPLATE")) {
                            if (inv.getItem(2).getType() != Material.GRAY_STAINED_GLASS_PANE) {
                                setMAItem(chest, 1, player, inv, true);
                            } else {
                                setMAItem(chest, 1, player, inv, false);
                            }
                        }
                    } else if (!player.isGliding() || player.isSneaking()) {
                        if (chest.getType() == Material.ELYTRA) {
                            if (main.multitoolutils.getWingInv(player).getItem(1).getType() != Material.GRAY_STAINED_GLASS_PANE) {
                                setMAItem(chest, 2, player, inv, true);
                            } else {
                                setMAItem(chest, 2, player, inv, false);
                            }
                        }
                    }
                }
            }
        }
    }

    public void setMAItem(ItemStack item, int index, Player player, Inventory inv, boolean swap) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        for (String line : meta.getLore()) {
            if (!line.equals(main.winglore) && !line.contains(main.colourKey + "- ")) {
                lore.add(line);
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(index, item);
        main.winginv.put(player.getUniqueId(), inv);

        if (swap) {
            ItemStack newchest;
            if (index == 2) {
                newchest = inv.getItem(1).clone();
            } else {
                newchest = inv.getItem(2).clone();
            }
            giveWings(newchest, player);
        }
    }

    public void giveWings(ItemStack item, Player player) {
        ItemMeta newchestmeta = item.getItemMeta();
        main.multitoolutils.updateFullWingLore(newchestmeta, player);
        item.setItemMeta(newchestmeta);
        main.multitoolutils.addMultiarmourNBT(item);
        player.getInventory().setChestplate(item);
    }
}
