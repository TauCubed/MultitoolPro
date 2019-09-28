package me.darkolythe.multitool;

import java.util.ArrayList;
import java.util.List;

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
			if (event.getInventory().getType() == InventoryType.DISPENSER)
			if ((event.getView().getTitle().equals(ChatColor.GREEN + "Multitools"))) {
				for (int i: event.getRawSlots()) {
					if (i <= 8) {
						event.setCancelled(true);
						break;
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
		if (event.getClickedInventory() != null) { //if the user clicks an inventory
			if (event.getClickedInventory() != player.getInventory() && event.getClickedInventory().getType() == InventoryType.DISPENSER) {
				if (view.getTitle().equals(ChatColor.GREEN + "Multitools")) {
					if (event.isShiftClick()) {
						event.setCancelled(true);
					}
					if (player.getItemOnCursor().getType() != Material.AIR) { //if the cursor has an item in it
						Material cursorstack = player.getItemOnCursor().getType();
						if (event.getCurrentItem() != null) {
							ItemStack clickstack = event.getCurrentItem().clone();
							if (clickstack.getType() == Material.GRAY_STAINED_GLASS_PANE) { //if the clicked item is a glass pane
								if (clickstack.getItemMeta().getDisplayName().contains("Sword")) {
									switch (cursorstack) {
										case DIAMOND_SWORD:
										case IRON_SWORD:
										case STONE_SWORD:
										case WOODEN_SWORD:
										case GOLDEN_SWORD:
											inv.setItem(0, player.getItemOnCursor());
											player.setItemOnCursor(null);
											break;
										default:
											break;
									}
								} else if (clickstack.getItemMeta().getDisplayName().contains("Pickaxe")) {
									switch (cursorstack) {
										case DIAMOND_PICKAXE:
										case IRON_PICKAXE:
										case STONE_PICKAXE:
										case WOODEN_PICKAXE:
										case GOLDEN_PICKAXE:
											inv.setItem(1, player.getItemOnCursor());
											player.setItemOnCursor(null);
											break;
										default:
											break;
									}
								} else if (clickstack.getItemMeta().getDisplayName().contains("Axe")) {
									switch (cursorstack) {
										case DIAMOND_AXE:
										case IRON_AXE:
										case STONE_AXE:
										case WOODEN_AXE:
										case GOLDEN_AXE:
											inv.setItem(2, player.getItemOnCursor());
											player.setItemOnCursor(null);
											break;
										default:
											break;
									}
								} else if (clickstack.getItemMeta().getDisplayName().contains("Shovel")) {
									switch (cursorstack) {
										case DIAMOND_SHOVEL:
										case IRON_SHOVEL:
										case STONE_SHOVEL:
										case WOODEN_SHOVEL:
										case GOLDEN_SHOVEL:
											inv.setItem(3, player.getItemOnCursor());
											player.setItemOnCursor(null);
											break;
										default:
											break;
									}
								} else if (clickstack.getItemMeta().getDisplayName().contains("Hoe")) {
									switch (cursorstack) {
										case DIAMOND_HOE:
										case IRON_HOE:
										case STONE_HOE:
										case WOODEN_HOE:
										case GOLDEN_HOE:
											inv.setItem(4, player.getItemOnCursor());
											player.setItemOnCursor(null);
											break;
										default:
											break;
									}
								} else if (clickstack.getItemMeta().getDisplayName().contains("Shears")) {
									switch (cursorstack) {
										case SHEARS:
											inv.setItem(5, player.getItemOnCursor());
											player.setItemOnCursor(null);
											break;
										default:
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
							switch (clickstack.getType()) {
								case DIAMOND_SWORD:
								case IRON_SWORD:
								case STONE_SWORD:
								case WOODEN_SWORD:
								case GOLDEN_SWORD:
									inv.setItem(0, main.placeholders.get(0));
									player.setItemOnCursor(clickstack);
									removemt = true;
									break;
								case DIAMOND_PICKAXE:
								case IRON_PICKAXE:
								case STONE_PICKAXE:
								case WOODEN_PICKAXE:
								case GOLDEN_PICKAXE:
									inv.setItem(1, main.placeholders.get(1));
									player.setItemOnCursor(clickstack);
									removemt = true;
									break;
								case DIAMOND_AXE:
								case IRON_AXE:
								case STONE_AXE:
								case WOODEN_AXE:
								case GOLDEN_AXE:
									inv.setItem(2, main.placeholders.get(2));
									player.setItemOnCursor(clickstack);
									removemt = true;
									break;
								case DIAMOND_SHOVEL:
								case IRON_SHOVEL:
								case STONE_SHOVEL:
								case WOODEN_SHOVEL:
								case GOLDEN_SHOVEL:
									inv.setItem(3, main.placeholders.get(3));
									player.setItemOnCursor(clickstack);
									removemt = true;
									break;
								case DIAMOND_HOE:
								case IRON_HOE:
								case STONE_HOE:
								case WOODEN_HOE:
								case GOLDEN_HOE:
									inv.setItem(4, main.placeholders.get(4));
									player.setItemOnCursor(clickstack);
									removemt = true;
									break;
								case SHEARS:
									inv.setItem(5, main.placeholders.get(5));
									player.setItemOnCursor(clickstack);
									removemt = true;
									break;
								case FEATHER:
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
											main.lastblock.put(player.getUniqueId(), Material.AIR);
										} else if (!giveitem) {
											player.sendMessage(main.messages.get("msgnospace"));
										} else {
											player.sendMessage(main.messages.get("msgalreadyhave"));
										}
									}
									event.setCancelled(true);
									player.closeInventory();
									break;
								default:
									if (clickstack.getType() != Material.GRAY_STAINED_GLASS_PANE  && clickstack.getType() != Material.AIR) {
										inv.setItem(event.getRawSlot(), main.placeholders.get(event.getRawSlot()));
										player.setItemOnCursor(clickstack);
										removemt = true;
									}
									break;
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
			} else {
				if (view.getTitle().equals(ChatColor.GREEN + "Multitools")) {
					if (event.isShiftClick()) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
