package com.github.incognitojam.oregenerator;

import org.bukkit.plugin.java.JavaPlugin;

public class OreGenerator extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Version " + getDescription().getVersion() + " enabled!");
    }
}
