///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// MarineStandalone is a minecraft server software and API.
// Copyright (C) MarineMC (marinemc.org)
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

package org.marinemc;

import org.marinemc.game.CommandManager;
import org.marinemc.game.PlayerManager;
import org.marinemc.game.WorldManager;
import org.marinemc.game.chat.ChatColor;
import org.marinemc.game.chat.ChatComponent;
import org.marinemc.game.chat.ChatMessage;
import org.marinemc.game.command.Command;
import org.marinemc.game.command.CommandProvider;
import org.marinemc.game.command.CommandSender;
import org.marinemc.game.commands.*;
import org.marinemc.game.scheduler.Scheduler;
import org.marinemc.net.NetworkManager;
import org.marinemc.net.play.clientbound.KickPacket;
import org.marinemc.player.Gamemode;
import org.marinemc.player.Player;
import org.marinemc.plugins.PluginLoader;
import org.marinemc.plugins.PluginManager;
import org.marinemc.server.Marine;
import org.marinemc.server.MarineServer;
import org.marinemc.server.Server;
import org.marinemc.settings.JSONFileHandler;
import org.marinemc.settings.ServerSettings;
import org.marinemc.util.Location;
import org.marinemc.world.Difficulty;
import org.marinemc.world.Identifiers;

import java.io.File;

/**
 * StandaloneServer - Housing of the main loop
 *
 * @author Fozie
 * @author Citymonstret
 */
@SuppressWarnings("unused")
public class StandaloneServer implements CommandProvider {

    // Final values
    public final int skipTime;
    private final int port;
    private final int targetTickRate;

    // Managers and handlers
    private final PlayerManager players;
    private final WorldManager worlds;
    private final Server server;
    private final Scheduler scheduler;
    private final PluginLoader pluginLoader;
    // Dynamic values
    public int ticks;
    NetworkManager network;
    private JSONFileHandler jsonHandler;
    // Settings:
    private String standard_motd = "MarineStandalone | Development";
    private int standard_maxplayers = 99;
    private Difficulty standard_difficulty = Difficulty.PEACEFUL;
    private Gamemode standard_gamemode = Gamemode.SURVIVAL;
    private boolean shouldRun;
    private String newMOTD = null;
    private boolean initialized = false;
    private CommandSender console;

    /**
     * Constructor
     *
     * @param settings Startup settings
     * @throws Throwable If anything goes wrong
     */
    public StandaloneServer(final MainComponent.StartSettings settings) throws Throwable {
        this.port = settings.port;
        this.skipTime = 1000000000 / settings.tickrate; // nanotime
        this.targetTickRate = settings.tickrate;
        this.worlds = new WorldManager(this);
        this.players = new PlayerManager(this);
        this.server = new Server(this);
        this.jsonHandler = new JSONFileHandler(this, new File("./settings"), new File("./storage"));
        // Set the static standalone server
        Marine.setStandalone(this);
        // Server the server
        Marine.setServer(this.server);
        // Register commands
        registerDefaultCommands();
        // Create a new scheduler instance
        this.scheduler = new Scheduler();
        // Set it to run 20 times per second
        this.scheduler.start(1000 / targetTickRate);
        // Make the plugin loader
        this.pluginLoader = new PluginLoader(new PluginManager());
        // Activate the identifier
        Identifiers.init();
    }

    /**
     * Get the internal scheduler
     *
     * @return scheduler
     */
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    /**
     * Load all plugins
     */
    private void loadPlugins() {
        final File pluginFolder = new File("./plugins");
        Logging.getLogger().log("Plugin Folder: " + pluginFolder.getPath());
        if (!pluginFolder.exists()) {
            if (!pluginFolder.mkdir()) {
                Logging.getLogger().error("Could not create plugin folder");
                return;
            }
        }
        Logging.getLogger().log("Loading Plugins...");
        this.pluginLoader.loadAllPlugins(pluginFolder);
        Logging.getLogger().log("Enabling Plugins...");
        this.pluginLoader.enableAllPlugins();
    }

    /**
     * Register default (internal) commands
     */
    private void registerDefaultCommands() {
        CommandManager.getInstance().registerCommand(this, new Info());
        CommandManager.getInstance().registerCommand(this, new Help());
        CommandManager.getInstance().registerCommand(this, new Test());
        CommandManager.getInstance().registerCommand(this, new Say());
        CommandManager.getInstance().registerCommand(this, new Stop());
        CommandManager.getInstance().registerCommand(this, new Plugins());
        CommandManager.getInstance().registerCommand(this, new SendAboveActionBarMessage());
        CommandManager.getInstance().registerCommand(this, new Teleport());
        CommandManager.getInstance().registerCommand(this, new Tellraw());
        CommandManager.getInstance().registerCommand(this, new List());
        CommandManager.getInstance().registerCommand(this, new Me());
    }

    /**
     * Get the MarineServer implementation
     *
     * @return server API
     */
    public MarineServer getServer() {
        return this.server;
    }

    /**
     * Get the internal player manager
     *
     * @return player manager
     */
    public PlayerManager getPlayerManager() {
        return players;
    }

    /**
     * Init. the server
     */
    private void init() {
        // Start the networking stuffz
        if (this.network == null) {
            this.network = new NetworkManager(this, port, ServerSettings.getInstance().useHasing);
            this.network.openConnection();
        }
        //TODO World loading
        // Open connection
        // Load in and enable plugins
        this.loadPlugins();
        initialized = true;
    }

    /**
     * Run!
     */
    protected void run() throws Exception {
        if (!this.initialized)
            init();
        players.updateThemAll();
        network.tryConnections();
        this.players.tickAllPlayers();
        this.worlds.tick();
        this.scheduler.tickSync();
        // Should really not be static
        // TODO Fix this
        ServerProperties.tick();
    }

    /**
     * Stop the server
     */
    public void stop() {
        Logging.getLogger().info("Shutting down...");
        for (final Player player : players.getPlayers()) {
            player.getClient().sendPacket(new KickPacket(ChatColor.red + ChatColor.bold + "Server stopped"));
        }
        Logging.getLogger().info("Plugin Handler Shutting Down");
        // Disable all plugins
        pluginLoader.disableAllPlugins();
        // Should not run, smart stuff
        MainComponent.mainTimer.cancel();
        // Save all json configs
        Logging.getLogger().info("Saving JSON Files");
        jsonHandler.saveAll();
        // Logging stop
        Logging.getLogger().saveLog();
        // When finished
        System.exit(0);
    }

    /**
     * Get the internal network manager
     *
     * @return internal network manager
     */
    public NetworkManager getNetwork() {
        return this.network;
    }

    public WorldManager getWorldManager() {
        return this.worlds;
    }

    public String getMOTD() {
        if (newMOTD != null)
            return newMOTD;
        try {
            newMOTD = ChatColor.transform('&', ServerSettings.getInstance().motd);
        } catch (Throwable e) {
            return standard_motd;
        }
        return newMOTD;
    }

    public void setMOTD(final String motd) {
        this.standard_motd = motd;
    }

    /**
     * Get max players
     *
     * @return Max players
     */
    public int getMaxPlayers() {
        return this.standard_maxplayers;
    }

    /**
     * Set default max players
     *
     * @param maxplayers Standard Max Players
     */
    public void setMaxPlayers(final int maxplayers) {
        this.standard_maxplayers = maxplayers;
    }

    /**
     * Get the default difficulty
     *
     * @return Standard difficulty
     */
    public Difficulty getDifficulty() {
        return this.standard_difficulty;
    }

    /**
     * Set the default difficulty
     *
     * @param difficulty Standard Difficulty
     */
    public void setDifficulty(final Difficulty difficulty) {
        this.standard_difficulty = difficulty;
    }

    /**
     * Get the default GameMode
     *
     * @return Default GameMode
     */
    public Gamemode getGamemode() {
        return this.standard_gamemode;
    }

    /**
     * Set the default GameMode
     *
     * @param gm Standard GameMode
     */
    public void setGameMode(final Gamemode gm) {
        this.standard_gamemode = gm;
    }

    /**
     * Get the internal plugin loader
     *
     * @return Plugin Loader
     */
    public PluginLoader getPluginLoader() {
        return this.pluginLoader;
    }

    /**
     * Get the console command sender
     *
     * @return Console Command Sender (creates on first use)
     */
    public CommandSender getConsole() {
        if (this.console == null) {
            this.console = new CommandSender() {

                private Location location = new Location(Marine.getServer().getWorlds().get(0), 0, 0, 0);

                @Override
                public void executeCommand(String command) {
                    this.executeCommand(command, new String[]{});
                }

                @Override
                public void executeCommand(String command, String[] arguments) {
                    Command c = CommandManager.getInstance().getCommand(command.toLowerCase().substring(1));
                    if (c == null) {
                        sendMessage("There is no such command");
                    } else {
                        this.executeCommand(c, arguments);
                    }
                }

                @Override
                public void executeCommand(Command command, String[] arguments) {
                    command.execute(this, arguments);
                }

                @Override
                public Location getLocation() {
                    return location;
                }

                @Override
                public boolean hasPermission(String permission) {
                    return true;
                }

                @Override
                public void sendMessage(String message) {
                    Logging.getLogger().log(message);
                }

                @Override
                public void sendMessage(ChatMessage message) {
                    Logging.getLogger().log(message.toString());
                }

                @Override
                public void sendMessage(ChatComponent message) {
                    // do nothing...
                }
            };
        }
        return this.console;
    }

    @Override
    public String getProviderName() {
        return "marine";
    }

    @Override
    public byte getProviderPriority() {
        return 0x02;
    }
}