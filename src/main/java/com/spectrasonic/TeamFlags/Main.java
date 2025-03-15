package com.spectrasonic.TeamFlags;

import co.aikar.commands.PaperCommandManager;
import com.spectrasonic.TeamFlags.Commands.TeamCommand;
import com.spectrasonic.TeamFlags.Events.InventoryListener;
import com.spectrasonic.TeamFlags.Menu.TeamMenu;
import com.spectrasonic.TeamFlags.Utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        TeamMenu.init(this);

        registerCommands();
        registerEvents();
        MessageUtils.sendStartupMessage(this);

    }

    @Override
    public void onDisable() {
        MessageUtils.sendShutdownMessage(this);
    }

    public void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new TeamCommand(this));
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
    }

    public void loadConfig() {
        reloadConfig();
    }
}
