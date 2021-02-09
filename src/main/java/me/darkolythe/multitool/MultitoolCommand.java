package me.darkolythe.multitool;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MultitoolCommand implements CommandExecutor {

	private Multitool main = Multitool.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (Multitool.whitelist.size() > 0 && !Multitool.whitelist.contains(player.getWorld().getName())) {
				sender.sendMessage(main.messages.get("msgwrongworld"));
				return true;
			}
			if (player.hasPermission("multitool.command")) {
				if (cmd.getName().equalsIgnoreCase("Multitool")) {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("Open") || args[0].equalsIgnoreCase("O")) {
							if (player.hasPermission("multitool.use")) {
								player.openInventory(main.multitoolutils.getToolInv(player));
							} else {
								sender.sendMessage(main.messages.get("msgnopermission"));
							}
						} else if (args[0].equalsIgnoreCase("Toggle") || args[0].equalsIgnoreCase("T")) {
							main.multitoolutils.setToggle(player.getUniqueId(), !main.multitoolutils.getToggle(player.getUniqueId()));
							if (main.multitoolutils.getToggle(player.getUniqueId())) {
								sender.sendMessage(main.messages.get("msgtoggleon"));
							} else {
								sender.sendMessage(main.messages.get("msgtoggleoff"));
							}
						} else if (args[0].equalsIgnoreCase("Migrate")) {
							if (player.hasPermission("multitool.migrate")) {
								player.sendMessage(main.prefix + ChatColor.WHITE + "The server may lag depending on the amount of saved multitools...");
								if (main.multitoolutils.migrate()) {
									player.sendMessage(main.prefix + ChatColor.WHITE + "Migration done. Multitools were successfully transferred");
								} else {
									player.sendMessage(main.prefix + ChatColor.WHITE + "No multitools from the regular version of the plugin were found. Migration done.");
								}
							} else {
								sender.sendMessage(main.messages.get("msgnopermission"));
							}
						} else if (args[0].equalsIgnoreCase("Reload")) {
							if (player.hasPermission("multitool.reload")) {
								main.multitoolutils.getConfigs();
								player.sendMessage(main.prefix + ChatColor.GREEN + "Files have been reloaded");
							} else {
								sender.sendMessage(main.messages.get("msgnopermission"));
							}
						} else if (args[0].equalsIgnoreCase("Wings") || args[0].equalsIgnoreCase("W")) {
							if (player.hasPermission("multitool.multiarmour")) {
								player.openInventory(main.multitoolutils.getWingInv(player));
							} else {
								sender.sendMessage(main.messages.get("msgnopermission"));
							}
						} else {
							if (!player.hasPermission("multitool.migrate")) {
								sender.sendMessage(main.messages.get("msginvalid") + " /mt [open, wings, toggle]");
							} else {
								sender.sendMessage(main.messages.get("msginvalid") + " /mt [open, wings, toggle, migrate, reload]");
							}
						}
					} else if (args.length == 2) {
						if (args[0].equalsIgnoreCase("Open") || args[0].equalsIgnoreCase("O")) {
							if (player.hasPermission("multitool.viewothers")) {
								for (Player players : Bukkit.getServer().getOnlinePlayers()) {
									if (args[1].equalsIgnoreCase(players.getName())) {
										player.openInventory(main.multitoolutils.getToolInv(players));
										main.openinv.put(player, players);
										return true;
									}
								}
								sender.sendMessage(main.messages.get("msgnotonline"));
							} else {
								sender.sendMessage(main.messages.get("msgnopermission"));
							}
						} else if (args[0].equals("Wings") || args[0].equalsIgnoreCase("W")) {
							if (player.hasPermission("multitool.viewothers")) {
								for (Player players : Bukkit.getServer().getOnlinePlayers()) {
									if (args[1].equalsIgnoreCase(players.getName())) {
										player.openInventory(main.multitoolutils.getWingInv(players));
										main.openinv.put(player, players);
										return true;
									}
								}
								sender.sendMessage(main.messages.get("msgnotonline"));
							} else {
								sender.sendMessage(main.messages.get("msgnopermission"));
							}
						}
					} else {
						if (!player.hasPermission("multitool.migrate")) {
							sender.sendMessage(main.messages.get("msginvalid") + " /mt [open, wings, toggle]");
						} else {
							sender.sendMessage(main.messages.get("msginvalid") + " /mt [open, wings, toggle, migrate, reload]");
						}
					}
				}
			} else {
				sender.sendMessage(main.messages.get("msgnopermission"));
			}
		}
		return true;
	}
	
}
