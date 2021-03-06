package me.NickUltracraft.Protect;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.NickUltracraft.Protect.API.UpdaterCheck;
import me.NickUltracraft.Protect.Cache.Arrays;
import me.NickUltracraft.Protect.Cache.Messages;
import me.NickUltracraft.Protect.Cache.Settings;
import me.NickUltracraft.Protect.Comandos.LoginStaff;
import me.NickUltracraft.Protect.Comandos.MudarSenha;
import me.NickUltracraft.Protect.Console.ConsoleLevel;
import me.NickUltracraft.Protect.Database.Conex�o;
import me.NickUltracraft.Protect.Database.Conex�o.ConnectionType;
import me.NickUltracraft.Protect.Hooks.AuthMe;
import me.NickUltracraft.Protect.Hooks.MambaLogin;
import me.NickUltracraft.Protect.Hooks.nLogin;

/**
 * A class Main.java do projeto (PLUGIN - nProtect Rebuilt) pertence ao NickUltracraft
 * Discord: NickUltracraft#4550
 * Mais informa��es: https://nickuc.tk 
 *
 * Rebuild, do not copy
*/

public class Main extends JavaPlugin {
	
	public static Main m;
	public static boolean loginDetectado = false;
	
	public void onEnable() {
		m = this;
		createConfig("config.yml");
		Arrays.getInstance().loadComandos();
		new Messages().loadMessages();
		new Settings().loadSettings();
		new Conex�o(ConnectionType.SQLITE).createDatabase();
		registerListener(new PlayerListeners());
		getCommand("loginstaff").setExecutor(new LoginStaff());
		getCommand("mudarsenhastaff").setExecutor(new MudarSenha());
		setupLoginPlugin();
		if(!new UpdaterCheck().isAtualizado()) {
			new Console(" Uma nova versao do nProtect esta disponivel", ConsoleLevel.ALERTA).sendMessage();
			new Console("", ConsoleLevel.INFO).sendMessage();
		}
		new Console("Inicializacao completa com sucesso", ConsoleLevel.ALERTA).sendMessage();
	}
	public void onDisable() {
		new Conex�o(ConnectionType.SQLITE).closeConnection();
	}
	private void setupLoginPlugin() {
		PluginManager pm = Bukkit.getPluginManager();
		if(pm.getPlugin("nLogin") != null) {
			registerListener(new nLogin());
			new Console("Usando listeners do nLogin", ConsoleLevel.INFO).sendMessage();
			loginDetectado = true;
		} else if(pm.getPlugin("AuthMe") != null) {
			registerListener(new AuthMe());
			new Console("Usando listeners do AuthMe", ConsoleLevel.INFO).sendMessage();
			loginDetectado = true;
		} else if(pm.getPlugin("Login") != null) {
			try {
				Class.forName("rush.login.events.PlayerAuthLoginEvent");
				Class.forName("rush.login.events.PlayerAuthRegisterEvent");
				registerListener(new MambaLogin());
				loginDetectado = true;
			} catch (Exception e) {
			}
		}
		new Console("Nenhum plugin de login detectado.", ConsoleLevel.ALERTA);
	}
	private void createConfig(String config) {
		if(!new File(getDataFolder(), config).exists()) saveResource(config, false);
	}
	private void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, this);
	}
}
