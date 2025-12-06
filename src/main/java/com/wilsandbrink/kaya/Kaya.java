package com.wilsandbrink.kaya;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class Kaya extends JavaPlugin implements Listener {

    // list of valid power plans
    // currentPowerPlan when plugin is launched
    private PowerScheme highPerf;
    private PowerScheme balanced;
    //private PowerScheme powerSave;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        highPerf = new PowerScheme(getConfig().getString("highPerfGUID"), "High Performance");
        balanced = new PowerScheme(getConfig().getString("balancedGUID"), "Balanced");
        //powerSave = new PowerScheme(getConfig().getString("powerSaveGUID"), "Power Saver");
        // otherwise just validate whenever they are supposed to change
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // restore power plan
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                Runtime.getRuntime().exec(new String[]{"powercfg", "/setactive", highPerf.guid()});
                getLogger().info("Player joined, turning on high performance");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Bukkit.getScheduler().runTask(this, () -> {
            if (Bukkit.getOnlinePlayers().isEmpty()) {
                try {
                    Runtime.getRuntime().exec(new String[]{"powercfg", "/setactive", balanced.guid()});
                    getLogger().info("No players, saving power");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
