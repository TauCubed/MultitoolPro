package me.darkolythe.multitool;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

import static me.darkolythe.multitool.SQLManager.createTableIfNotExists;

public class MultitoolUtils implements Listener {

    private Multitool main;

    public MultitoolUtils(Multitool plugin) {
        this.main = plugin; // set it equal to an instance of main
    }

    public void getConfigs() {
        main.reloadConfig();
        main.prefix = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("prefix"));
        Multitool.soulbound = main.getConfig().getStringList("keepondeath");
        Multitool.whitelist = main.getConfig().getStringList("whitelisted_worlds");
        Multitool.blacklistedLores = main.getConfig().getStringList("lore-blacklist");
        Multitool.vanish = main.getConfig().getStringList("loseondeath");
        Multitool.sql = main.getConfig().getBoolean("enable_sql");

        if (Multitool.sql) {
            SQLManager.connect(main);
        }

        main.messages.put("msgcannotput", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgcannotput").replace("%prefix%", main.prefix)));
        main.messages.put("msgdrop", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgdrop").replace("%prefix%", main.prefix)));
        main.messages.put("msgremove", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgremove").replace("%prefix%", main.prefix)));
        main.messages.put("msgtoggleon", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgtoggleon").replace("%prefix%", main.prefix)));
        main.messages.put("msgtoggleoff", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgtoggleoff").replace("%prefix%", main.prefix)));
        main.messages.put("msgdeath", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgdeath").replace("%prefix%", main.prefix)));
        main.messages.put("msginvalid", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msginvalid").replace("%prefix%", main.prefix)));
        main.messages.put("msgnotmultitool", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgnotmultitool").replace("%prefix%", main.prefix)));
        main.messages.put("msgempty", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgempty").replace("%prefix%", main.prefix)));
        main.messages.put("msgnospace", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgnospace").replace("%prefix%", main.prefix)));
        main.messages.put("msggiven", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msggiven").replace("%prefix%", main.prefix)));
        main.messages.put("msgtoolremoved", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgtoolremoved").replace("%prefix%", main.prefix)));
        main.messages.put("msgalreadyhave", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgalreadyhave").replace("%prefix%", main.prefix)));
        main.messages.put("msgnopermission", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgnopermission").replace("%prefix%", main.prefix)));
        main.messages.put("msgnotonline", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgnotonline").replace("%prefix%", main.prefix)));
        main.messages.put("msgitemframe", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgitemframe").replace("%prefix%", main.prefix)));
        main.messages.put("msgarmourstand", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgarmourstand").replace("%prefix%", main.prefix)));
        main.messages.put("msgwrongworld", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("msgwrongworld").replace("%prefix%", main.prefix)));

        main.messages.put("swordhere", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("swordhere").replace("%prefix%", main.prefix)));
        main.messages.put("pickaxehere", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("pickaxehere").replace("%prefix%", main.prefix)));
        main.messages.put("axehere", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("axehere").replace("%prefix%", main.prefix)));
        main.messages.put("shovelhere", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("shovelhere").replace("%prefix%", main.prefix)));
        main.messages.put("hoehere", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("hoehere").replace("%prefix%", main.prefix)));
        main.messages.put("shearshere", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("shearshere").replace("%prefix%", main.prefix)));
        main.messages.put("clickfeather", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("clickfeather").replace("%prefix%", main.prefix)));
        main.messages.put("chestplatehere", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("chestplatehere").replace("%prefix%", main.prefix)));
        main.messages.put("elytrahere", ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("elytrahere").replace("%prefix%", main.prefix)));

        main.mtoinv = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("mtoinv"));
        main.mtwinv = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("mtwinv"));
        main.toollore = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("multitool"));
        main.winglore = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("multiarmour"));
        main.mtLoreList = main.getConfig().getBoolean("tool-list-lore");
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////Player leave and join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!Multitool.sql) {
            main.configmanager.playerLoad(e.getPlayer().getUniqueId(), "toolinv.");
            main.configmanager.playerLoad(e.getPlayer().getUniqueId(), "winginv.");
        } else {
            // prevent data from loading when it is already loaded
            if (!main.toolinv.containsKey(e.getPlayer().getUniqueId()) || !main.winginv.containsKey(e.getPlayer().getUniqueId())) {
                // why is this on the main thread? might fix later IDK.
                SQLManager.getPlayerData(e.getPlayer(), false);
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (!Multitool.sql) {
            main.configmanager.playerSave(event.getPlayer().getUniqueId(), null, "toolinv.");
            main.configmanager.playerSave(event.getPlayer().getUniqueId(), null, "winginv.");

            main.toolinv.remove(event.getPlayer().getUniqueId());
            main.winginv.remove(event.getPlayer().getUniqueId());
        } else {
            List<ItemStack> m_inv = Arrays.asList(main.toolinv.remove(event.getPlayer().getUniqueId()).getContents());
            List<ItemStack> w_inv = Arrays.asList(main.winginv.remove(event.getPlayer().getUniqueId()).getContents());
            m_inv.replaceAll(s -> s == null ? s : s.clone());
            w_inv.replaceAll(s -> s == null ? s : s.clone());

            Bukkit.getServer().getScheduler().runTaskAsynchronously(main, () -> SQLManager.setPlayerData(event.getPlayer().getUniqueId(), m_inv, w_inv));
        }
    }

    public Boolean getToggle(UUID uuid) {
        if (!main.toggle.containsKey(uuid)) {
            main.toggle.put(uuid, true);
        }
        return main.toggle.get(uuid);
    }

    public void setToggle(UUID uuid, Boolean bool) {
        main.toggle.put(uuid, bool);
    }

    public Inventory getToolInv(Player player) {
        if (!main.toolinv.containsKey(player.getUniqueId())) {
            Inventory inv = Bukkit.getServer().createInventory(player, InventoryType.DISPENSER, main.mtoinv); //create the mv inv
            for (int index = 0; index < 9; index++) {
                inv.setItem(index, main.placeholders.get(index)); //if the player data is empty, set main.placeholders until the inv is saved
            }
            main.toolinv.put(player.getUniqueId(), inv);
        }
        return main.toolinv.get(player.getUniqueId());
    }

    public Inventory getWingInv(Player player) {
        if (!main.winginv.containsKey(player.getUniqueId())) {
            Inventory inv = Bukkit.getServer().createInventory(player, InventoryType.HOPPER, main.mtwinv); //create the mv inv
            for (int index = 0; index < 5; index++) {
                inv.setItem(index, main.wingholders.get(index)); //if the player data is empty, set main.placeholders until the inv is saved
            }
            main.winginv.put(player.getUniqueId(), inv);
        }
        return main.winginv.get(player.getUniqueId());
    }

    public boolean hasNBT(ItemStack item, String toolType) {
        NBTItem nbt = new NBTItem(item);
        if (nbt.hasNBTData()) {
            if (nbt.getBoolean("is_" + toolType)) {
                return true;
            }
        }
        return false;
    }

    public ItemStack addMultitoolNBT(ItemStack item) {
        NBTItem nbt = new NBTItem(item);
        nbt.setBoolean("is_multitool", true);
        return nbt.getItem();
    }

    public ItemStack addMultiarmourNBT(ItemStack item) {
        NBTItem nbt = new NBTItem(item);
        nbt.setBoolean("is_multiarmour", true);
        return nbt.getItem();
    }

    public boolean isMultitool(ItemStack item) {
        if (item != null) {
            if (item.hasItemMeta()) {
                if (hasNBT(item, "multitool")) {
                    return true;
                }
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasLore()) {
                    for (String line : meta.getLore()) {
                        if (line.equals(main.toollore)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isMultiArmour(ItemStack item) {
        if (item != null) {
            if (item.hasItemMeta()) {
                if (hasNBT(item, "multiarmour")) {
                    return true;
                }
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasLore()) {
                    for (String line : meta.getLore()) {
                        if (line.equals(main.winglore)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public ItemStack addNBTLore(ItemStack item, Player player) {
        ItemMeta meta = item.getItemMeta();
        updateFullToolLore(meta, player);
        item.setItemMeta(meta);
        return addMultitoolNBT(item);
    }


    public List<String> addLore(ItemMeta meta, String line, boolean top) {
        List<String> newlore = new ArrayList<>();
        if (!top) {
            if (meta.hasLore() && meta.getLore() != null) {
                newlore = meta.getLore();
            } else {
                newlore = new ArrayList<>();
            }
            newlore.add(line);
        } else {
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                newlore.add(line);
                for (String str : lore) {
                    newlore.add(str);
                }
            } else {
                newlore.add(line);
            }
        }
        return newlore;
    }

    public void migrate(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                Map<UUID, Inventory> temptools = new HashMap<>();
                boolean success = false;
                int count = 0;

                File file = new File(main.getDataFolder(), "../MultitoolPlus/config.yml");

                if (file.exists()) {
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                    if (config.contains("toolinv")) {
                        for (String uuid : config.getConfigurationSection("toolinv").getKeys(false)) {
                            Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, main.mtoinv); //create the mv inv

                            if (config.contains("toolinv." + uuid)) {
                                int index = 0;
                                for (String item : config.getConfigurationSection("toolinv." + uuid).getKeys(false)) { //load all the itemstacks from config.yml
                                    if (config.getConfigurationSection("toolinv." + uuid + "." + item) != null) {
                                        inv.setItem(index, main.configmanager.loadItem(config.getConfigurationSection("toolinv." + uuid + "." + item)));
                                    }
                                    if (inv.getItem(index) == null) { //if air is in the inventory, put the glass panes as main.placeholders
                                        inv.setItem(index, main.placeholders.get(index));
                                    }
                                    index += 1;
                                }
                            } else {
                                for (int index = 0; index < 5; index++) {
                                    inv.setItem(index, main.placeholders.get(index)); //if the player data is empty, set main.placeholders until the inv is saved
                                }
                            }

                            boolean isempty = true;
                            for (ItemStack is : inv.getContents()) {
                                Material mat = is.getType();
                                if (mat != Material.GRAY_STAINED_GLASS_PANE && mat != Material.FEATHER && mat != Material.AIR) {
                                    isempty = false;
                                    break;
                                }
                            }
                            if (!isempty) {
                                temptools.put(UUID.fromString(uuid), inv);
                            }
                        }

                        for (UUID uuid : temptools.keySet()) {
                            if (count % Math.max((temptools.size() / 20), 2) == 0) {
                                player.sendMessage(main.prefix + ChatColor.WHITE + "saving player data... " + String.format("%.2f", (((float)count / temptools.size()) * 100)) + "%");
                            }
                            count++;
                            main.configmanager.playerSave(uuid, temptools.get(uuid), "migration");
                        }
                    } else {
                        main.saveDefaultConfig();
                        main.reloadConfig();
                    }
                    main.saveDefaultConfig();
                    main.reloadConfig();
                    success = true;
                }
                if (success) {
                    player.sendMessage(main.prefix + ChatColor.WHITE + "Migration done. Multitools were successfully transferred");
                } else {
                    player.sendMessage(main.prefix + ChatColor.WHITE + "No multitools from the regular version of the plugin were found. Migration done.");
                }
            }
        });
    }


    public void transferToDatabase(Player player) {
        if (Multitool.sql) {
            File file = new File(main.getDataFolder(), "PlayerData.yml");

            if (createTableIfNotExists()) {

                if (file.exists()) {
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                    if (config.contains("toolinv")) {
                        for (String uuid : config.getConfigurationSection("toolinv").getKeys(false)) {
                            main.configmanager.playerLoad(UUID.fromString(uuid), "toolinv.");
                            main.configmanager.playerLoad(UUID.fromString(uuid), "winginv.");
                            SQLManager.setPlayerData(UUID.fromString(uuid),
                                    Arrays.asList(main.toolinv.remove(UUID.fromString(uuid)).getContents()),
                                    Arrays.asList(main.winginv.remove(UUID.fromString(uuid)).getContents()));
                        }
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            SQLManager.getPlayerData(p, false);
                        }
                        player.sendMessage(main.prefix + ChatColor.GREEN + "PlayerData.yml has been migrated.");
                    } else {
                        player.sendMessage(main.prefix + ChatColor.RED + "No players found for migration.");
                    }
                } else {
                    player.sendMessage(main.prefix + ChatColor.RED + "No PlayerData.yml file found.");
                }

            }
        } else {
            player.sendMessage(main.prefix + ChatColor.RED + "SQL is not enabled in the config.");
        }
    }


    public void addPlaceholders() {
        String[] names = new String[]{main.messages.get("swordhere"), main.messages.get("pickaxehere"), main.messages.get("axehere"),
                main.messages.get("shovelhere"), main.messages.get("hoehere"), main.messages.get("shearshere"), "",
                ChatColor.BLUE.toString() + ChatColor.BOLD.toString() + "Multitool", ""};
        String[] wnames = new String[]{"", main.messages.get("chestplatehere"), main.messages.get("elytrahere"), "",
                ChatColor.BLUE.toString() + ChatColor.BOLD.toString() + "Multiarmour"};
        String lore = main.messages.get("clickfeather");

        for (int i = 0; i < 9; i++) {
            ItemStack ph = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1); //create gray stained glass
            ItemMeta phmet = ph.getItemMeta();
            phmet.addItemFlags(ItemFlag.HIDE_PLACED_ON); //make them hideplaceon so that players cant replicate them in their inventory
            phmet.setDisplayName(names[i]); //give them their display names
            if (i == 6 || i == 8) {
                ph.setType(Material.AIR);
            }
            if (i == 7) {
                ph.setType(Material.FEATHER); //if the item is a feather, give it lore
                phmet.setLore(addLore(phmet, lore, true));
            }
            ph.setItemMeta(phmet);
            main.placeholders.add(ph); //add all the items to a list with place holder glass panes
        }

        for (int i = 0; i < 5; i++) {
            ItemStack ph = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1); //create gray stained glass
            ItemMeta phmet = ph.getItemMeta();
            phmet.addItemFlags(ItemFlag.HIDE_PLACED_ON); //make them hideplaceon so that players cant replicate them in their inventory
            phmet.setDisplayName(wnames[i]); //give them their display names
            if (i == 0 || i == 3) {
                ph.setType(Material.AIR);
            }
            if (i == 4) {
                ph.setType(Material.FEATHER); //if the item is a feather, give it lores
                phmet.setLore(addLore(phmet, lore, true));
            }
            ph.setItemMeta(phmet);
            main.wingholders.add(ph); //add all the items to a list with place holder glass panes
        }
    }

    // loops through mt and wing inventories and updates the players items
    public void updateMTItems(Player player) {
        if (isMultitool(player.getInventory().getItemInMainHand())) {
            for (int i = 0; i < 9; i++) {
                ItemStack mt_i = getToolInv(player).getItem(i);
                ItemStack p_i = player.getInventory().getItemInMainHand();
                if (mt_i != null) {
                    if (mt_i.getType() == p_i.getType()) {
                        main.multitooltooldetect.giveStack(mt_i.clone(), player);
                        break;
                    }
                }
            }
        }

        if (isMultiArmour(player.getInventory().getChestplate())) {
            for (int i = 0; i < 4; i++) {
                ItemStack w_i = getWingInv(player).getItem(i);
                ItemStack p_i = player.getInventory().getChestplate();

                if (w_i != null && p_i != null) {
                    if (w_i.getType() == p_i.getType()) {
                        main.wingdetect.giveWings(w_i.clone(), player);
                    }
                }
            }
        }
    }

    public void updateFullToolLore(ItemMeta givemeta, Player player) {
        givemeta.setLore(main.multitoolutils.addLore(givemeta, main.toollore, false));

        if (main.mtLoreList) {
            int index = 0;
            for (ItemStack tool : main.multitoolutils.getToolInv(player)) {
                updateFullLore(givemeta, index, tool, main.placeholders);
                index++;
            }
        }
    }

    public void updateFullWingLore(ItemMeta newchestmeta, Player player) {
        newchestmeta.setLore(main.multitoolutils.addLore(newchestmeta, main.winglore, false));

        if (main.mtLoreList) {
            int index = 0;
            for (ItemStack wing : main.multitoolutils.getWingInv(player)) {
                updateFullLore(newchestmeta, index, wing, main.wingholders);
                index++;
            }
        }
    }

    private void updateFullLore(ItemMeta itemmeta, int index, ItemStack item, List<ItemStack> itemholder) {
        if (item != null && itemholder.get(index).getType() != item.getType()) {
            if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
                itemmeta.setLore(main.multitoolutils.addLore(itemmeta,
                        main.colourKey + "- " + item.getItemMeta().getDisplayName(), false));
            } else {
                itemmeta.setLore(main.multitoolutils.addLore(itemmeta,
                        main.colourKey + "- " + ChatColor.WHITE + WordUtils.capitalize(item.getType()
                                .toString().toLowerCase().replaceAll("_", " ")),
                        false));
            }
        }
    }
}
