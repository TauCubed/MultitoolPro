package me.darkolythe.multitool;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MultitoolToolDetect implements Listener {
	
private Multitool main;
	
	public MultitoolToolDetect(Multitool plugin) {
		this.main = plugin; // set it equal to an instance of main
	}

	private ToolMap map = new ToolMap();

	private ToolMap.ToolType getToolType(Material material) {
		String mat = material.toString();

		return map.map.getOrDefault(mat, ToolMap.ToolType.SWORD);
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (event.getEntity() instanceof LivingEntity) {
				setItem(player, null, true, false);
			}
		}
	}

	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		String type = event.getRightClicked().getType().toString();
		if ((type.contains("COW") || type.equals("SHEEP")) && !type.equals("COW") || type.equals("SNOW_GOLEM")) {
			setItem(player, null, true, true);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		//Get player action
		Action action = event.getAction();
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		//Check if the action is a left click before advancing
		if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {
			//Set the player equal to a variable

			if (action.equals(Action.LEFT_CLICK_AIR) || block.getType().toString().contains("BAMBOO") || block.getType() == Material.COBWEB) {
				setItem(player, block, true, false);
				return;
			}
			setItem(player, block, false, false);
		} else if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
			if (event.getPlayer().isSneaking()) {
				setItem(player, block, false, true);
			} else {
				setItem(player, block, false, false);
			}
		}
	}

	private void setItem(Player player, Block block, boolean isEntity, boolean isShifting) {
		if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(player.getWorld().getName())) {
			return;
		}
		if (player.hasPermission("multitool.use")) { //If the player has permission, continue

			if (main.multitoolutils.getToggle(player.getUniqueId())) {
				//Get item in player's hand
				ItemStack handitem = player.getInventory().getItemInMainHand();

				if (main.multitoolutils.isMultitool(handitem)) {
					ItemStack givestack = new ItemStack(Material.AIR, 0);
					setMTItem(player, true);
					if (isEntity && !isShifting) { //if the block is air, make it a sword, else, continue
						giveSword(player);
					} else if (isEntity) {
						if (main.multitoolutils.getToolInv(player).getItem(5) != null) {
							if (main.multitoolutils.getToolInv(player).getItem(5).getType() != Material.GRAY_STAINED_GLASS_PANE) {
								givestack = main.multitoolutils.getToolInv(player).getItem(5).clone();
								giveStack(givestack, player);
								return;
							}
						}
					} else {
						//if the air was not clicked, continue down the checklist
						//Check what material it is, and change the tool
						if (block != null) {
							Material blocktype = block.getType();

							ToolMap.ToolType tooltype = getToolType(blocktype);

							if (isShifting && tooltype == ToolMap.ToolType.SHOVEL) {
								tooltype = ToolMap.ToolType.AXE;
							}

							if (main.multitoolutils.getToolInv(player).getItem(tooltype.index()) != null) {
								if (main.multitoolutils.getToolInv(player).getItem(tooltype.index()).getType() != Material.GRAY_STAINED_GLASS_PANE) {

									givestack = main.multitoolutils.getToolInv(player).getItem(tooltype.index()).clone();
									giveStack(givestack, player);
								}
							}
						}
					}
				}
			} else {
				//Get item in player's hand
				ItemStack handitem = player.getInventory().getItemInMainHand();
				if (main.multitoolutils.isMultitool(handitem)) {
					setMTItem(player, false);
				}
			}
		}
	}

	public boolean giveSword(Player player) {
		if (main.multitoolutils.getToolInv(player).getItem(0) != null) {
			if (main.multitoolutils.getToolInv(player).getItem(0).getType() != Material.GRAY_STAINED_GLASS_PANE) {
				ItemStack givestack = main.multitoolutils.getToolInv(player).getItem(0).clone();
				giveStack(givestack, player);
				return true;
			}
		}
		return false;
	}
	
	public void giveStack(ItemStack givestack, Player player) {
		if (givestack.getType() != Material.AIR) { //if the block being hit changed, update the held item
			givestack = main.multitoolutils.addNBTLore(givestack, player);

			player.getInventory().setItemInMainHand(givestack);
		}
	}

	public void setMTItem(Player player, boolean changeitem) {
		for (int i = 0; i < 9; i++) { //this loops through the mt inv, checks which index the current item being used is in, and then updates it
			if (main.multitoolutils.getToolInv(player).getItem(i) != null) {
				if (main.multitoolutils.getToolInv(player).getItem(i).getType() == player.getInventory().getItemInMainHand().getType()) {
					Inventory mtinv = main.multitoolutils.getToolInv(player); //create inventory of mtinv
					ItemStack handstack = player.getInventory().getItemInMainHand().clone();
					ItemMeta stackmeta = handstack.getItemMeta();
					List<String> lore = new ArrayList<>();
					for (String line : stackmeta.getLore()) {
						if (!line.equals(main.toollore) && !line.contains(main.colourKey + "- ")) {
							lore.add(line);
						}
					}
					stackmeta.setLore(lore);
					handstack.setItemMeta(stackmeta);

					NBTItem nbt = new NBTItem(handstack);
					nbt.setBoolean("is_multitool", false);
					handstack = nbt.getItem();

					mtinv.setItem(i, handstack); //replace old item with used item

					if (changeitem) {
						main.toolinv.put(player.getUniqueId(), mtinv); //replace old inv with new inv
					}
				}
			}
		}
	}
}
