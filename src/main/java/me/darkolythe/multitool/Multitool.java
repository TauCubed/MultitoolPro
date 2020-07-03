package me.darkolythe.multitool;

import java.util.*;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Multitool extends JavaPlugin implements Listener {

	public Map<UUID, Inventory> toolinv = new HashMap<>();
	public Map<UUID, Inventory> winginv = new HashMap<>();
	public List<ItemStack> placeholders = new ArrayList<>();
	public List<ItemStack> wingholders = new ArrayList<>();
	public Map<UUID, Boolean> toggle = new HashMap<>();
	public Map<UUID, Material> lastblock = new HashMap<>();
	public String prefix = ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.BLUE.toString() + "Multitool" + ChatColor.WHITE.toString() + ChatColor.BOLD.toString() + "] ";
	public String toollore = ChatColor.BLUE.toString() + ChatColor.BOLD.toString() + "Multitool";
	public String winglore = ChatColor.BLUE.toString() + ChatColor.BOLD.toString() + "Multiarmour";
	public Map<Player, Player> openinv = new HashMap<>();
	public Map<String, String> messages = new HashMap<>();
	public Map<UUID, Boolean> dowarning = new HashMap<>();
	public Map<UUID, Integer> warningpercent = new HashMap<>();

	public static List<String> soulbound = new ArrayList<>();
	public static List<String> vanish = new ArrayList<>();
	public static boolean dropondeath = false;
	public static Multitool plugin;
	public MultitoolInventory multitoolinventory;
	public WingInventory winginventory;
	public WingDetect wingdetect;
	public MultitoolListener multitoolevents;
	public MultitoolToolDetect multitooltooldetect;
	public ConfigManager configmanager;
	public MultitoolUtils multitoolutils;
	
	public void onEnable() {///////////////////////////////////////////////////////////////////////////////////////////////Enable Disable
		plugin = this;
		getServer().getPluginManager().registerEvents(this, this);
		
		multitoolevents = new MultitoolListener(plugin);
		multitoolinventory = new MultitoolInventory(plugin);
		winginventory = new WingInventory(plugin);
		wingdetect = new WingDetect(plugin);
		multitooltooldetect = new MultitoolToolDetect(plugin);
		configmanager = new ConfigManager(plugin);
		multitoolutils = new MultitoolUtils(plugin);
		getCommand("multitool").setExecutor(new MultitoolCommand());

		saveDefaultConfig();

		configmanager.setup();

		multitoolutils.getConfigs();

		multitoolutils.addPlaceholders();

		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(multitoolevents, this);
		getServer().getPluginManager().registerEvents(multitoolinventory, this);
		getServer().getPluginManager().registerEvents(winginventory, this);
		getServer().getPluginManager().registerEvents(wingdetect, this);
		getServer().getPluginManager().registerEvents(multitooltooldetect, this);
		getServer().getPluginManager().registerEvents(multitoolutils, this);

		dropondeath = getConfig().getBoolean("dropondeath");
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			configmanager.playerLoad(player.getUniqueId(), "toolinv."); //load all the players on the server (on boot, there will be none, on reload, this is necessary)
			configmanager.playerLoad(player.getUniqueId(), "winginv."); //load all the players on the server (on boot, there will be none, on reload, this is necessary)
		}

		Metrics metrics = new Metrics(plugin);

		saveDefaultConfig();

		System.out.println(prefix + ChatColor.GREEN + "Diverse Multitool enabled!");
	}
	
	public void onDisable() {

		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			configmanager.playerSave(player.getUniqueId(), null, "toolinv."); //this saves all the player mt inv information if the server is reloading
			configmanager.playerSave(player.getUniqueId(), null, "winginv."); //this saves all the player mt inv information if the server is reloading
		}
		
		System.out.println(prefix + ChatColor.RED + "Diverse Multitool disabled!");
	}

	public static Multitool getInstance() {
		return plugin;
	}
}
