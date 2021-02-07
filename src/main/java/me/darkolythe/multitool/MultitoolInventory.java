package me.darkolythe.multitool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MultitoolInventory implements Listener {
	
	private Multitool main;
	
	public MultitoolInventory(Multitool plugin) {
		this.main = plugin; // set it equal to an instance of main
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			if (event.getInventory().getType() == InventoryType.DISPENSER) {
				if ((event.getView().getTitle().equals(main.mtoinv))) {
					for (int i : event.getRawSlots()) {
						if (i <= 8) {
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
		toolMap.put("SWORD", 0);
		toolMap.put("PICKAXE", 1);
		toolMap.put("AXE", 2);
		toolMap.put("SHOVEL", 3);
		toolMap.put("HOE", 4);
		toolMap.put("SHEARS", 5);

		if (event.getClickedInventory() != null) { //if the user clicks an inventory
			if (player.getOpenInventory().getTopInventory().getType() == InventoryType.DISPENSER) {
				if (view.getTitle().equals(main.mtoinv)) {
					if (event.getClickedInventory().equals(player.getInventory()) && event.isShiftClick()) {
						String type = event.getCurrentItem().clone().getType().toString();
						for (String s : toolMap.keySet()) {
							if (type.contains("_" + s) || s.equals(type)) {
								if (inv.getItem(toolMap.get(s)).getType() == Material.GRAY_STAINED_GLASS_PANE) {
									inv.setItem(toolMap.get(s), event.getCurrentItem().clone());
									event.getCurrentItem().setAmount(0);
									break;
								}
							}
						}
						event.setCancelled(true);
					} else if (event.getClickedInventory().getType() == InventoryType.DISPENSER) {
						if (player.getItemOnCursor().getType() != Material.AIR) { //if the cursor has an item in it
							Material cursorstack = player.getItemOnCursor().getType();
							if (event.getCurrentItem() != null) {
								ItemStack clickstack = event.getCurrentItem().clone();
								if (clickstack.getType() == Material.GRAY_STAINED_GLASS_PANE) { //if the clicked item is a glass pane
									String type = cursorstack.toString();
									for (String s : toolMap.keySet()) {
										if (type.contains("_" + s) || s.equals(type)
												&& player.hasPermission("multitool.tool." + s.toLowerCase())
												&& inv.getItem(toolMap.get(s)).getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
											inv.setItem(toolMap.get(s), player.getItemOnCursor());
											player.setItemOnCursor(null);
											break;
										}
									}
									event.setCancelled(true);
								}
							}
						} else {
							if (event.getCurrentItem() != null) {
								ItemStack clickstack = event.getCurrentItem().clone();
								boolean removemt = false;
								String type = clickstack.getType().toString();
								for (String s : toolMap.keySet()) {
									if (type.contains("_" + s) || s.equals(type)) {
										inv.setItem(toolMap.get(s), main.placeholders.get(toolMap.get(s)));
										player.setItemOnCursor(clickstack);
										removemt = true;
										break;
									}
								}
								if (type.contains("FEATHER")) {
									boolean forloop = false;
									ItemStack genstack = null;
									if (!main.openinv.containsKey(player)) {
										for (int i = 0; i < 9; i++) { //this loops through the mt inv, and gives the player the first multitool that shows up
											if (main.toolinv.get(player.getUniqueId()).getItem(i) != null) {
												Material curmat = main.toolinv.get(player.getUniqueId()).getItem(i).getType();
												forloop = false;
												if (curmat != Material.GRAY_STAINED_GLASS_PANE && curmat != Material.FEATHER) {
													genstack = main.toolinv.get(player.getUniqueId()).getItem(i).clone();
													ItemMeta genmeta = genstack.getItemMeta();
													genmeta.setLore(main.multitoolutils.addLore(genmeta, main.toollore, false));
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
															if (l.equals(main.toollore)) {
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
										inv.setItem(event.getRawSlot(), main.placeholders.get(event.getRawSlot()));
										player.setItemOnCursor(clickstack);
										removemt = true;
									}
								}
								if (removemt) {
									if (main.openinv.containsKey(player)) {
										player = main.openinv.get(player);
									}
									Inventory plrinv = player.getInventory(); //this removes the multitool from the player's inventory if a tool is removed from the list
									for (ItemStack i : plrinv) {
										if (i != null) {
											if (i.getItemMeta() != null) {
												ItemMeta imeta = i.getItemMeta();
												if (imeta.hasLore()) {
													for (String l : imeta.getLore()) {
														if (l.equals(main.toollore)) {
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
				}
			}
		}
	}
}
