package com.github.incognitojam.oregenerator;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class OreGenerator extends JavaPlugin {
    private OreConfig oreConfig;

    @Override
    public void onEnable() {
        final long seed = getServer().getWorlds().get(0).getSeed();

        oreConfig = new OreConfig(seed);
        loadConfig();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new OreListener(oreConfig), this);


        final Recipe furnaceSandstone = new FurnaceRecipe(
                new NamespacedKey(this, "furnace_sandstone"),
                new ItemStack(Material.SANDSTONE),
                Material.STONE,
                0.0f,
                150
        );
        getServer().addRecipe(furnaceSandstone);
        pluginManager.registerEvents(new HopperListener(oreConfig), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("oregenerator")) {
            // Sender ran the /oregenerator command

            if (args.length == 0) {
                // No arguments provided
                sender.sendMessage(ChatColor.RED + "Missing required arguments.");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                // Sender ran the reload command
                loadConfig();
            }

            return true;
        }

        return super.onCommand(sender, command, label, args);
    }

    private void loadConfig() {
        // Make sure data folder exists
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();

        // Make sure config file exists
        File configFile = new File(getDataFolder(), "config.json");
        if (!configFile.exists()) {
            // this code makes me sad :(
            try {
                InputStream inputStream = getResource("config.json");
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();

                OutputStream outputStream = new FileOutputStream(configFile);
                outputStream.write(buffer);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Attempt to read config file from disk
        FileReader reader = null;
        try {
            reader = new FileReader(configFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Load config file into memory
        oreConfig.loadConfig(reader);
    }
}
