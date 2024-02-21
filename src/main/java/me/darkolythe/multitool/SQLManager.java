package me.darkolythe.multitool;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SQLManager {

    private static Object mutex = new Object();

    private static String host, port, database, username, password;
    private static Connection connection;
    private static Multitool main;

    public static void connect(Multitool multitool) {
        main = multitool;
        host = main.getConfig().getString("host");
        port = main.getConfig().getString("port");
        database = main.getConfig().getString("database");
        username = main.getConfig().getString("username");
        password = main.getConfig().getString("password");

        try {
            openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            try {
                openConnection();
                Statement statement = connection.createStatement();

                statement.executeQuery("SHOW TABLES;");

                statement.close();
            } catch (Exception e) {
                main.getLogger().log(Level.INFO, ("Could not connect to table."));
            }
        }, 432000L, 432000L);
    }

    private static void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?allowPublicKeyRetrieval=true&useSSL=false", username, password);
    }

    public static boolean createTableIfNotExists() {
        try {
            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery("SHOW TABLES;");

            while (result.next()) {
                if ("multitoolplusprodata".equals(result.getString(1))) {
                    return true;
                }
            }
            statement.executeUpdate("CREATE TABLE multitoolplusprodata (" +
                    "UUID VARCHAR(48), " +
                    "sword TEXT(4096), " +
                    "pickaxe TEXT(4096), " +
                    "axe TEXT(4096), " +
                    "shovel TEXT(4096), " +
                    "hoe TEXT(4096), " +
                    "shears TEXT(4096), " +
                    "chestplate TEXT(4096), " +
                    "elytra TEXT(4096), " +
                    "PRIMARY KEY (UUID));");

            statement.close();

            return true;
        } catch (Exception e) {
            main.getLogger().log(Level.INFO, ("Could not create mySQL table."));
        }
        return false;
    }

    public static void getPlayerData(Player player, boolean inloop) {
        Inventory inv = Bukkit.getServer().createInventory(player, InventoryType.DISPENSER, main.mtoinv); //create the mv inv
        Inventory winv = Bukkit.getServer().createInventory(player, InventoryType.HOPPER, main.mtwinv); //create the mv inv

        UUID uuid = player.getUniqueId();

        for (int index = 0; index < 9; index++) {
            inv.setItem(index, main.placeholders.get(index));
        }
        main.toolinv.put(uuid, inv);

        for (int index = 0; index < 5; index++) {
            winv.setItem(index, main.wingholders.get(index));
        }
        main.winginv.put(uuid, winv);


        try {
            openConnection();
            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery("SELECT * FROM multitoolplusprodata WHERE UUID = '" + uuid + "';");

            if (result.next()) {
                inv.setItem(0, deserializeItemStack(result.getString("sword")));
                inv.setItem(1, deserializeItemStack(result.getString("pickaxe")));
                inv.setItem(2, deserializeItemStack(result.getString("axe")));
                inv.setItem(3, deserializeItemStack(result.getString("shovel")));
                inv.setItem(4, deserializeItemStack(result.getString("hoe")));
                inv.setItem(5, deserializeItemStack(result.getString("shears")));
                winv.setItem(1, deserializeItemStack(result.getString("chestplate")));
                winv.setItem(2, deserializeItemStack(result.getString("elytra")));
            }

            statement.close();
        } catch (Exception e) {
            if (!inloop) {
                try {
                    getPlayerData(player, true);
                } catch (Exception ex) {
                    player.sendMessage(main.prefix + ChatColor.RED + "Failed to load Multitool inventory. Contact an administrator.");
                    Multitool.getInstance().getLogger().log(Level.SEVERE, "Failed to load Multitool inventory for " + player.getName(), ex);
                }
            } else {
                player.sendMessage(main.prefix + ChatColor.RED + "Failed to load Multitool inventory. Contact an administrator.");
                Multitool.getInstance().getLogger().log(Level.SEVERE, "Failed to load Multitool inventory for " + player.getName(), e);
            }
        }
    }

    public static void setPlayerData(UUID uuid, List<ItemStack> m_inv, List<ItemStack> w_inv) {
        try {
            openConnection();
            Statement statement = connection.createStatement();

            String sword = serializeItemStack(m_inv.get(0));
            String pickaxe = serializeItemStack(m_inv.get(1));
            String axe = serializeItemStack(m_inv.get(2));
            String shovel = serializeItemStack(m_inv.get(3));
            String hoe = serializeItemStack(m_inv.get(4));
            String shears = serializeItemStack(m_inv.get(5));
            String chestplate = serializeItemStack(w_inv.get(1));
            String elytra = serializeItemStack(w_inv.get(2));

            statement.executeUpdate("INSERT INTO multitoolplusprodata (UUID, sword, pickaxe, axe, shovel, hoe, shears, chestplate, elytra) " +
                    "VALUES ('" + uuid.toString() + "', '" + sword + "', '" + pickaxe + "', '" + axe + "', '" + shovel +
                    "', '" + hoe + "', '" + shears + "', '" + chestplate + "', '" + elytra + "') ON DUPLICATE KEY UPDATE " +
                    "sword = VALUES(sword), " +
                    "pickaxe = VALUES(pickaxe), " +
                    "axe = VALUES(axe), " +
                    "shovel = VALUES(shovel), " +
                    "hoe = VALUES(hoe), " +
                    "shears = VALUES(shears), " +
                    "chestplate = VALUES(chestplate), " +
                    "elytra = VALUES(elytra);");

            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String serializeItemStack(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    private static ItemStack deserializeItemStack(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack item = (ItemStack) dataInput.readObject();

            dataInput.close();
            return item;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
