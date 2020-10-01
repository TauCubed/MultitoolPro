package me.darkolythe.multitool;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ConfigManager {

    public Multitool main;
    public ConfigManager(Multitool plugin) {
        this.main = plugin;
    }
    public Multitool plugin = Multitool.getPlugin(Multitool.class);

    private FileConfiguration playerDataConfig;
    private File playerData;

    public void setup() {
        playerData = new File(plugin.getDataFolder(), "PlayerData.yml");

        if (!playerData.exists()) {
            try {
                playerData.createNewFile();
                System.out.println(main.prefix + ChatColor.GREEN + "PlayerData.yml has been created");
            } catch (IOException e) {
                System.out.println(main.prefix + ChatColor.RED + "Could not create PlayerData.yml");
            }
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(playerData);
    }

    public void playerLoad(UUID uuid, String invdir) {

        if (invdir.equals("toolinv.")) {
            Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.DISPENSER, main.mtoinv); //create the mv inv
            if (playerDataConfig.contains("toolinv." + uuid)) {
                int index = 0;
                for (String item : playerDataConfig.getConfigurationSection("toolinv." + uuid).getKeys(false)) { //load all the itemstacks from config.yml
                    if (playerDataConfig.getConfigurationSection("toolinv." + uuid + "." + item) != null) {
                        inv.setItem(index, loadItem(playerDataConfig.getConfigurationSection("toolinv." + uuid + "." + item)));
                    }
                    if (inv.getItem(index) == null || (index == 4 && inv.getItem(index).getType() == Material.FEATHER)) {
                        inv.setItem(index, main.placeholders.get(index));
                    }
                    index += 1;
                }
            }
            for (int index = 0; index < 9; index++) {
                if (inv.getItem(index) == null || (index == 4 && inv.getItem(index).getType() == Material.FEATHER)) {
                    inv.setItem(index, main.placeholders.get(index));
                }
            }
            main.lastblock.put(uuid, null); //set the default value for last block hit upon player join
            main.toolinv.put(uuid, inv);
        } else {
            Inventory winv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, main.mtwinv); //create the mv inv
            if (playerDataConfig.contains("winginv." + uuid)) {
                int index = 0;
                for (String item : playerDataConfig.getConfigurationSection("winginv." + uuid).getKeys(false)) { //load all the itemstacks from config.yml
                    if (playerDataConfig.getConfigurationSection("winginv." + uuid + "." + item) != null) {
                        winv.setItem(index, loadItem(playerDataConfig.getConfigurationSection("winginv." + uuid + "." + item)));
                    }
                    if (winv.getItem(index) == null || (index == 4 && winv.getItem(index).getType() == Material.FEATHER)) {
                        winv.setItem(index, main.wingholders.get(index));
                    }
                    index += 1;
                }
            }
            for (int index = 0; index < 5; index++) {
                if (winv.getItem(index) == null || (index == 4 && winv.getItem(index).getType() == Material.FEATHER)) {
                    winv.setItem(index, main.wingholders.get(index));
                }
            }
            main.winginv.put(uuid, winv);
        }
        if (playerDataConfig.contains("warningdata." + uuid)) {
            main.dowarning.put(uuid, playerDataConfig.getBoolean("warningdata." + uuid + ".dowarning"));
            main.warningpercent.put(uuid, playerDataConfig.getInt("warningdata." + uuid + ".warningpercent"));
        }
    }
    
    public void playerSave(UUID uuid, Inventory inv, String invdir) {

        if (!playerDataConfig.contains(invdir + uuid)) {
            playerDataConfig.createSection(invdir + uuid); //if the player's mt inv doesnt exist in config.yml ,create it
        }

        playerDataConfig.set(invdir + uuid, null);

        char c = 'a';

        if (inv == null) {
            if (invdir.equals("toolinv.")) {
                inv = main.toolinv.get(uuid);
            } else {
                inv = main.winginv.get(uuid);
            }
        }

        if ((main.toolinv.containsKey(uuid) && invdir.equals("toolinv.")) || (main.winginv.containsKey(uuid) && invdir.equals("winginv."))) {
            for (ItemStack itemstack : inv) { //save the player's mt inventory
                if (itemstack != null) {
                    saveItem(playerDataConfig.createSection(invdir + uuid + "." + c++), itemstack);
                } else {
                    ItemStack airstack = new ItemStack(Material.AIR, 0);
                    saveItem(playerDataConfig.createSection(invdir + uuid + "." + c++), airstack); //if there's nothing in a slot, save it as air
                }
            }
        }

        if (main.dowarning.containsKey(uuid) && main.warningpercent.containsKey(uuid)) {
            playerDataConfig.createSection("warningdata." + uuid);
            playerDataConfig.set("warningdata." + uuid + ".dowarning", main.dowarning.get(uuid));
            playerDataConfig.set("warningdata." + uuid + ".warningpercent", main.warningpercent.get(uuid));
        }

        try {
            playerDataConfig.save(playerData);
        } catch (IOException e) {

        }
        playerLoad(uuid, invdir);
    }

    private void saveItem(ConfigurationSection section, ItemStack itemstack) {/////////////////////////////////////////////Save Load Inv
        section.set("itemstack", itemstack);
    }

    public ItemStack loadItem(ConfigurationSection section) {
        ItemStack itemstack = new ItemStack(section.getItemStack("itemstack"));

        return itemstack;
    }
}
