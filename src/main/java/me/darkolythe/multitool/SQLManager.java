package me.darkolythe.multitool;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLManager {

    private static String host, port, database, username, password;
    private static Connection connection;
    private static Statement statement;
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
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
    }

    public static void createTableIfNotExists() {
        try {
            ResultSet result = statement.executeQuery("SHOW TABLES;");

            while (result.next()) {
                if ("MultitoolPlusProData".equals(result.getString(1))) {
                    return;
                }
            }
            statement.executeUpdate("CREATE TABLE MultitoolPlusProData (" +
                    "UUID VARCHAR(48), " +
                    "sword VARCHAR(2048), " +
                    "pickaxe VARCHAR(2048), " +
                    "axe VARCHAR(2048), " +
                    "shovel VARCHAR(2048), " +
                    "hoe VARCHAR(2048), " +
                    "shears VARCHAR(2048), " +
                    "chestplate VARCHAR(2048), " +
                    "elytra VARCHAR(2048), " +
                    "PRIMARY KEY (UUID));");
        } catch (Exception e) {
            System.out.println("Could not create mySQL table.");
        }
    }

    public static void getPlayerData(Player player, boolean inloop) {
        Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.DISPENSER, main.mtoinv); //create the mv inv
        Inventory winv = Bukkit.getServer().createInventory(null, InventoryType.HOPPER, main.mtwinv); //create the mv inv

        UUID uuid = player.getUniqueId();

        for (int index = 0; index < 9; index++) {
            inv.setItem(index, main.placeholders.get(index));
        }
        main.lastblock.put(uuid, null); //set the default value for last block hit upon player join
        main.toolinv.put(uuid, inv);

        for (int index = 0; index < 5; index++) {
            winv.setItem(index, main.wingholders.get(index));
        }
        main.winginv.put(uuid, winv);


        try {
            ResultSet result = statement.executeQuery("SELECT * FROM MultitoolPlusProData WHERE UUID = '" + uuid + "';");

            result.next();

            inv.setItem(0, deserializeItemStack(result.getString("sword")));
            inv.setItem(1, deserializeItemStack(result.getString("pickaxe")));
            inv.setItem(2, deserializeItemStack(result.getString("axe")));
            inv.setItem(3, deserializeItemStack(result.getString("shovel")));
            inv.setItem(4, deserializeItemStack(result.getString("hoe")));
            inv.setItem(5, deserializeItemStack(result.getString("shears")));
            winv.setItem(1, deserializeItemStack(result.getString("chestplate")));
            winv.setItem(2, deserializeItemStack(result.getString("elytra")));
        } catch (Exception e) {
            e.printStackTrace();
            if (!inloop) {
                try {
                    setPlayerData(player);
                    getPlayerData(player, true);
                } catch (Exception ex) {
                    player.sendMessage(main.prefix + ChatColor.RED + "Failed to load Multitool inventory. Contact and administrator.");
                }
            } else {
                player.sendMessage(main.prefix + ChatColor.RED + "Failed to load Multitool inventory. Contact and administrator.");
            }
        }
    }

    public static void setPlayerData(Player player) {
        try {
            Inventory m_inv = main.toolinv.get(player.getUniqueId());
            Inventory w_inv = main.winginv.get(player.getUniqueId());

            String sword = serializeItemStack(m_inv.getItem(0));
            String pickaxe = serializeItemStack(m_inv.getItem(1));
            String axe = serializeItemStack(m_inv.getItem(2));
            String shovel = serializeItemStack(m_inv.getItem(3));
            String hoe = serializeItemStack(m_inv.getItem(4));
            String shears = serializeItemStack(m_inv.getItem(5));
            String chestplate = serializeItemStack(w_inv.getItem(1));
            String elytra = serializeItemStack(w_inv.getItem(2));

            statement.executeUpdate("INSERT INTO MultitoolPlusProData (UUID, sword, pickaxe, axe, shovel, hoe, shears, chestplate, elytra) " +
                    "VALUES ('" + player.getUniqueId() + "', '" + sword + "', '" + pickaxe + "', '" + axe + "', '" + shovel +
                    "', '" + hoe + "', '" + shears + "', '" + chestplate + "', '" + elytra + "') ON DUPLICATE KEY UPDATE " +
                    "sword = VALUES(sword), " +
                    "pickaxe = VALUES(pickaxe), " +
                    "axe = VALUES(axe), " +
                    "shovel = VALUES(shovel), " +
                    "hoe = VALUES(hoe), " +
                    "shears = VALUES(shears), " +
                    "chestplate = VALUES(chestplate), " +
                    "elytra = VALUES(elytra);");
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
