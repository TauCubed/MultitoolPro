package me.darkolythe.multitool;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class MultitoolListener implements Listener {

	private Multitool main;
	public MultitoolListener(Multitool plugin) {
		this.main = plugin; // set it equal to an instance of main
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////Make sure player isnt removing MT from inv
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();

		if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(player.getWorld().getName())) {
			return;
		}

		Item dropitem = event.getItemDrop();
		ItemStack dropstack = dropitem.getItemStack();
		if (main.multitoolutils.isTool(dropstack, main.toollore) || main.multitoolutils.isTool(dropstack, main.winglore)) {
			dropitem.remove();
			player.sendMessage(main.messages.get("msgdrop"));
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player)event.getPlayer();

		if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(player.getWorld().getName())) {
			return;
		}

		main.openinv.remove(player);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Player player = (Player)event.getPlayer();

		if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(player.getWorld().getName())) {
			return;
		}

		Inventory inv = main.multitoolutils.getToolInv(player);
		inv.setItem(8, new ItemStack(Material.AIR));
		inv.setItem(6, new ItemStack(Material.AIR));
	}
	
	@EventHandler
	public void onItemBreak(PlayerItemBreakEvent event) {
		ItemStack brokenitem = event.getBrokenItem();

		if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(event.getPlayer().getWorld().getName())) {
			return;
		}

		if (main.multitoolutils.isTool(brokenitem, main.toollore)) {
			Inventory mtinv = main.multitoolutils.getToolInv(event.getPlayer()); //create inventory of mtinv
			for (int i = 0; i < 4; i++) {
				if (mtinv.getItem(i).getType() == brokenitem.getType()) {
					mtinv.setItem(i, main.placeholders.get(i));
				}
			}
		} else if (main.multitoolutils.isTool(brokenitem, main.winglore)) {
			Inventory winginv = main.multitoolutils.getWingInv(event.getPlayer()); //create inventory of mtinv
			for (int i = 1; i < 3; i++) {
				if (winginv.getItem(i).getType() == brokenitem.getType()) {
					winginv.setItem(i, main.placeholders.get(i));
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryCheck(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(event.getWhoClicked().getWorld().getName())) {
			return;
		}

		if ((event.getClickedInventory() != player.getInventory()) || (event.isShiftClick() && player.getOpenInventory().getType() != InventoryType.CRAFTING)) {
			if ((player.getItemOnCursor().getType() != Material.AIR)) {
				ItemStack cursorstack = player.getItemOnCursor();
				if (main.multitoolutils.isTool(cursorstack, main.toollore) || main.multitoolutils.isTool(cursorstack, main.winglore)) {
					player.setItemOnCursor(null);
					event.setCancelled(true);
					player.sendMessage(main.messages.get("msgremove"));
					return;
				}
			} else if (event.isShiftClick() && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				ItemStack clickstack = event.getCurrentItem();
				if (main.multitoolutils.isTool(clickstack, main.toollore) || main.multitoolutils.isTool(clickstack, main.winglore)) {
					event.setCurrentItem(null);
					event.setCancelled(true);
					player.sendMessage(main.messages.get("msgremove"));
					return;
				}
			} else if (event.getClick() == ClickType.NUMBER_KEY) {
				ItemStack item = player.getInventory().getItem(event.getHotbarButton());
				if (item != null && item.getType() != Material.AIR) {
					if (main.multitoolutils.isTool(item, main.toollore) || main.multitoolutils.isTool(item, main.winglore)) {
						player.getInventory().setItem(event.getHotbarButton(), null);
						event.setCancelled(true);
						player.sendMessage(main.messages.get("msgremove"));
						return;
					}
				}
			} else if (main.multitoolutils.isTool(event.getCurrentItem(), main.toollore)) {
				event.setCurrentItem(null);
				event.setCancelled(true);
				player.sendMessage(main.prefix + ChatColor.RED + "Your Multitool was outside your inventory. It has been removed!");
				return;
			}
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(event.getWhoClicked().getWorld().getName())) {
			return;
		}

		if (event.getOldCursor().getType() != Material.AIR) {
			if (event.getInventory().getType() != InventoryType.CRAFTING) {
				ItemStack clickstack = event.getOldCursor();
				if (main.multitoolutils.isTool(clickstack, main.toollore) || main.multitoolutils.isTool(clickstack, main.winglore)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onItemSwap(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();

		if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(player.getWorld().getName())) {
			return;
		}

		if (player.isSneaking()) {
			if (main.multitoolutils.isTool(event.getOffHandItem(), main.toollore)) {
				main.multitoolutils.setToggle(player.getUniqueId(), !main.multitoolutils.getToggle(player.getUniqueId()));
				if (main.multitoolutils.getToggle(player.getUniqueId())) {
					player.sendMessage(main.messages.get("msgtoggleon"));
				} else {
					player.sendMessage(main.messages.get("msgtoggleoff"));
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(player.getWorld().getName())) {
			return;
		}

		if (!event.getKeepInventory()) {
			if (Multitool.dropondeath) {
				if (main.toolinv.containsKey(player.getUniqueId())) {
					dropItems(player, main.toolinv.get(player.getUniqueId()));
				}
				if (main.winginv.containsKey(player.getUniqueId())) {
					dropItems(player, main.winginv.get(player.getUniqueId()));
				}
			}
			List<ItemStack> drops = event.getDrops();
			for (ItemStack i : drops) {
				if (i != null && main.multitoolutils.isTool(i, main.toollore) || main.multitoolutils.isTool(i, main.winglore)) {
					event.getDrops().remove(i);
					player.sendMessage(main.messages.get("msgdeath"));
					return;
				}
			}
		}
	}

	private void dropItems(Player player, Inventory inv) {
		int index = 0;
		for (ItemStack i : inv.getContents()) {
			boolean vanish = false;
			boolean soulbound = false;
			if (i != null && i.getType() != Material.FEATHER && i.getType() != Material.GRAY_STAINED_GLASS_PANE) {
				if (i.containsEnchantment(Enchantment.VANISHING_CURSE))
					vanish = true;
				if (i.getItemMeta() != null && i.getItemMeta().getLore() != null) {
					for (String l : i.getItemMeta().getLore()) {
						for (String s : Multitool.vanish) {
							if (l.toLowerCase().contains(s.toLowerCase())) {
								vanish = true;
								break;
							}
						}
						for (String s : Multitool.soulbound) {
							if (l.toLowerCase().contains(s.toLowerCase())) {
								soulbound = true;
								break;
							}
						}
						if (soulbound || vanish)
							break;
					}
				}
				if (!vanish && !soulbound) {
					player.getWorld().dropItemNaturally(player.getLocation(), i);
				}
				if (!soulbound) {
					inv.setItem(index, main.placeholders.get(index));
				}
			}
			index++;
		}
	}

	@EventHandler
	private void playerMendEvent(PlayerItemMendEvent event) {
		Player player = event.getPlayer();
		ItemStack mendItem = event.getItem();
		int amt = event.getExperienceOrb().getExperience() * 2;

		if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(player.getWorld().getName())) {
			return;
		}

		List<ItemStack> items = new ArrayList<>();

		if (main.multitoolutils.isTool(mendItem, main.toollore)) {
			for (ItemStack item : main.toolinv.get(player.getUniqueId())) {
				if (item == null) {
					continue;
				}
				if (!item.containsEnchantment(Enchantment.MENDING)) {
					continue;
				}
				if (item.getDurability() == 0) {
					continue;
				}
				items.add(item);
			}
		} else if (main.multitoolutils.isTool(mendItem, main.winglore)) {
			for (ItemStack item : main.winginv.get(player.getUniqueId())) {
				if (item == null) {
					continue;
				}
				if (!item.containsEnchantment(Enchantment.MENDING)) {
					continue;
				}
				if (item.getDurability() == 0) {
					continue;
				}
				items.add(item);
			}
		}

		if (items.size() > 0) {
			event.setRepairAmount(0);
			event.getExperienceOrb().setExperience(0);

			int count = 0;
			for (ItemStack item : items) {
				int per_amt = amt / (items.size() - count);
				count++;

				int dura_left = Math.min(item.getDurability(), per_amt);

				item.setDurability((short) (item.getDurability() - dura_left));
				amt -= dura_left;
			}
			player.giveExp(amt);
			main.multitoolutils.updateMTItems(player);
		}
	}

	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		ItemStack handitem = player.getInventory().getItemInMainHand();
		boolean isTool = main.multitoolutils.isTool(handitem, main.toollore);
		boolean isWing = main.multitoolutils.isTool(handitem, main.winglore);

		if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(player.getWorld().getName())) {
			return;
		}

		if (isTool || isWing) {
			Entity ent = event.getRightClicked();
			if (ent.getType() == EntityType.ITEM_FRAME) {
				event.setCancelled(true);
				player.sendMessage(main.messages.get("msgitemframe"));
			}
		}
	}

	@EventHandler
	public void onArmourStandEdit(PlayerArmorStandManipulateEvent event) {
		Player player = event.getPlayer();
		ItemStack handItem = player.getInventory().getItemInMainHand();
		boolean isTool = main.multitoolutils.isTool(handItem, main.toollore);
		boolean isWing = main.multitoolutils.isTool(handItem, main.winglore);

		if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(player.getWorld().getName())) {
			return;
		}

		if (isTool || isWing) {
			event.setCancelled(true);
			player.sendMessage(main.messages.get("msgarmourstand"));
		}
	}
}
