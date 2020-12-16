package xyz.nkomarn.harbor;

import com.earth2me.essentials.Essentials;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.command.ForceSkipCommand;
import xyz.nkomarn.harbor.command.HarborCommand;
import xyz.nkomarn.harbor.listener.BedListener;
import xyz.nkomarn.harbor.task.AccelerateNightTask;
import xyz.nkomarn.harbor.task.Checker;
import xyz.nkomarn.harbor.task.TimeAlter;
import xyz.nkomarn.harbor.util.Config;
import xyz.nkomarn.harbor.util.Messages;
import xyz.nkomarn.harbor.util.Metrics;
import xyz.nkomarn.harbor.util.PlayerManager;

import java.util.Arrays;
import java.util.Optional;

public class Harbor extends JavaPlugin {

    private Config config;
    private Checker checker;
    private Messages messages;
    private PlayerManager playerManager;
    private Essentials essentials;
    
    public boolean skippingNight = false;

    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        config = new Config(this);
        checker = new Checker(this);
        messages = new Messages(this);
        playerManager = new PlayerManager(this);
        essentials = (Essentials) pluginManager.getPlugin("Essentials");

        Arrays.asList(
                messages,
                playerManager,
                new BedListener(this)
        ).forEach(listener -> pluginManager.registerEvents(listener, this));

        getCommand("harbor").setExecutor(new HarborCommand(this));
        getCommand("forceskip").setExecutor(new ForceSkipCommand(this));

        if (essentials == null) {
            getLogger().info("Essentials not present- registering fallback AFK detection system.");
            playerManager.registerFallbackListeners();
        }

        if (config.getBoolean("metrics")) {
            new Metrics(this);
        }
        
        for (World world : getServer().getWorlds()) {
            new TimeAlter(this, world);
        }
    }

    @Override
    public void onDisable() {
        for (World world : getServer().getWorlds()) {
            messages.clearBar(world);
        }
    }

    @NotNull
    public String getVersion() {
        return getDescription().getVersion();
    }

    @NotNull
    public Config getConfiguration() {
        return config;
    }

    @NotNull
    public Checker getChecker() {
        return checker;
    }

    @NotNull
    public Messages getMessages() {
        return messages;
    }

    @NotNull
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    @NotNull
    public Optional<Essentials> getEssentials() {
        return Optional.ofNullable(essentials);
    }
}
