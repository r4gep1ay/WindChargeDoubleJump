package com.doublejump;

import org.bukkit.plugin.java.JavaPlugin;

public class WindChargeDoubleJump extends JavaPlugin {
    @Override
    public void onEnable() {
        // Регистрация listener'а
        getServer().getPluginManager().registerEvents(new DoubleJumpListener(this), this);
        getLogger().info("WindChargeDoubleJump включён");
    }

    @Override
    public void onDisable() {
        getLogger().info("WindChargeDoubleJump выключён");
    }
}