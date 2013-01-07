package mc.alk.arena.listeners;

import mc.alk.arena.BattleArena;
import mc.alk.arena.Defaults;
import mc.alk.arena.controllers.FactionsController;
import mc.alk.arena.controllers.HeroesController;
import mc.alk.arena.controllers.MobArenaInterface;
import mc.alk.arena.controllers.MoneyController;
import mc.alk.arena.controllers.PylamoController;
import mc.alk.arena.controllers.TagAPIController;
import mc.alk.arena.controllers.WorldGuardInterface;
import mc.alk.arena.objects.messaging.AnnouncementOptions;
import mc.alk.arena.util.BTInterface;
import mc.alk.arena.util.DisguiseInterface;
import mc.alk.arena.util.Log;
import mc.alk.tracker.Tracker;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;

import com.dthielke.herochat.Herochat;


/**
 *
 * @author alkarin
 *
 */
public class BAPluginListener implements Listener {

	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		if (event.getPlugin().getName() == "BattleTracker")
			loadBattleTracker();
		else if (event.getPlugin().getName() == "Factions")
			loadFactions();
		else if (event.getPlugin().getName() == "Herochat")
			loadHeroChat();
		else if (event.getPlugin().getName() == "Heroes")
			loadHeroes();
		else if (event.getPlugin().getName() == "DisguiseCraft")
			loadDisguiseCraft();
		else if (event.getPlugin().getName() == "MobArena")
			loadMobArena();
		else if (event.getPlugin().getName() == "MultiInv")
			loadMultiInv();
		else if (event.getPlugin().getName() == "Multiverse-Core")
			loadMultiverseCore();
		else if (event.getPlugin().getName() == "Multiverse-Inventories")
			loadMultiverseInventory();
		else if (event.getPlugin().getName() == "PylamoRestorationSystem")
			loadPylamoRestoration();
		else if (event.getPlugin().getName() == "Register")
			loadRegister();
		else if (event.getPlugin().getName() == "TagAPI")
			loadTagAPI();
		else if (event.getPlugin().getName() == "WorldEdit")
			loadWorldEdit();
		else if (event.getPlugin().getName() == "WorldGuard")
			loadWorldGuard();
		else if (event.getPlugin().getName() == "Vault")
			loadVault();
	}

	public void loadAll(){
		loadBattleTracker();
		loadDisguiseCraft();
		loadFactions();
		loadHeroChat();
		loadHeroes();
		loadMobArena();
		loadMultiInv();
		loadMultiverseCore();
		loadMultiverseInventory();
		loadPylamoRestoration();
		loadRegister();
		loadTagAPI();
		loadWorldEdit();
		loadWorldGuard();
		loadVault();
	}


	public void loadBattleTracker(){
		if (BTInterface.battleTracker == null){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("BattleTracker");
			if (plugin != null) {
				BTInterface.battleTracker = (Tracker) plugin;
			} else {
				Log.info("[BattleArena] BattleTracker not detected, not tracking wins");
			}
		}
	}

	public void loadDisguiseCraft(){
		if (DisguiseInterface.disguiseInterface == null){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("DisguiseCraft");
			if (plugin != null) {
				DisguiseInterface.disguiseInterface = DisguiseCraft.getAPI();
				Log.info("[BattleArena] DisguiseCraft detected, enabling disguises");
			}
		}
	}

	public void loadFactions(){
		if (!FactionsController.enabled()){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("Factions");
			if (plugin != null) {
				if (FactionsController.enableFactions(true)){
					Log.info("[BattleArena] Factions detected. Configurable power loss enabled (default no powerloss)");
				} else {
					Log.info("[BattleArena] Old Factions detected that does not have a PowerLossEvent");
				}
			}
		}
	}

	public void loadHeroChat(){
		if (AnnouncementOptions.hc == null){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("Herochat");
			if (plugin != null) {
				AnnouncementOptions.setHerochat((Herochat) plugin);
				Log.info("[BattleArena] Herochat detected, adding channel options");
			}
		}
	}

	public void loadHeroes(){
		if (!HeroesController.enabled()){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("Heroes");
			if (plugin != null) {
				HeroesController.setHeroes(plugin);
				Log.info("[BattleArena] Heroes detected. Implementing heroes class options");
			}
		}
	}

	public void loadMobArena(){
		if (!MobArenaInterface.hasMobArena()){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("MobArena");
			if (plugin != null) {
				MobArenaInterface.init(plugin);
				Log.info("[BattleArena] MobArena detected.  Implementing no join when in MobArena");
			}
		}
	}

	public void loadMultiInv(){
		if (!Defaults.PLUGIN_MULTI_INV){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("MultiInv");
			if (plugin != null) {
				Defaults.PLUGIN_MULTI_INV=true;
				Log.info("[BattleArena] MultiInv detected.  Implementing teleport/gamemode workarounds");
			}
		}
	}

	public void loadMultiverseCore(){
		if (!Defaults.PLUGIN_MULITVERSE_CORE){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
			if (plugin != null) {
				Defaults.PLUGIN_MULITVERSE_CORE=true;
				Log.info("[BattleArena] Multiverse-Core detected. Implementing teleport/gamemode workarounds");
			}
		}
	}

	public void loadMultiverseInventory(){
		if (!Defaults.PLUGIN_MULITVERSE_INV){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("Multiverse-Inventories");
			if (plugin != null) {
				Defaults.PLUGIN_MULITVERSE_INV=true;
				Log.info("[BattleArena] Multiverse-Inventories detected. Implementing teleport/gamemode workarounds");
			}
		}
	}

	public void loadPylamoRestoration(){
		if (!PylamoController.enabled()){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("PylamoRestorationSystem");
			if (plugin != null){
				PylamoController.setPylamo(plugin);
				Log.info(BattleArena.getPName() +" found PylamoRestorationSystem");
			}
		}
	}

	public void loadRegister(){
		if (!MoneyController.hasVaultEconomy() && !MoneyController.hasRegisterEconomy()){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("Register");
			if (plugin != null){
				MoneyController.setRegisterEconomy();
				Log.info(BattleArena.getPName() +" found economy plugin Register");
			}
		}
	}

	public void loadWorldEdit(){
		if (WorldGuardInterface.hasWorldEdit()){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
			if (plugin != null) {
				if (WorldGuardInterface.setWorldEdit(plugin)){
					Log.info("[BattleArena] WorldEdit detected.");
				}
			}
		}
	}

	public void loadWorldGuard(){
		if (WorldGuardInterface.hasWorldGuard()){
			Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
			if (plugin != null) {
				if (WorldGuardInterface.setWorldGuard(plugin)){
					Log.info("[BattleArena] WorldGuard detected. WorldGuard regions can now be used");
				}
			}
		}
	}

	public void loadTagAPI(){
		if (!TagAPIController.enabled()){
			Plugin plugin = Bukkit.getPluginManager().getPlugin("TagAPI");
			if (plugin != null) {
				TagAPIController.enableTagAPI(true);
				Log.info("[BattleArena] TagAPI detected. Implementing Team colored player names");
			}
		}
	}

	public void loadVault(){
		Plugin plugin = Bukkit.getPluginManager().getPlugin("Vault");
		if (plugin != null ){
			/// Load vault economy
			if (!MoneyController.hasVaultEconomy()){
				RegisteredServiceProvider<Economy> provider = Bukkit.getServer().
						getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
				if (provider==null || provider.getProvider() == null){
					Log.warn(BattleArena.getPName() +" found no economy plugin. Attempts to use money in arenas might result in errors.");
					return;
				} else {
					MoneyController.setEconomy(provider.getProvider());
					Log.info(BattleArena.getPName() +" found economy plugin Vault. [Default]");
				}
			}
			/// Load Vault chat
			if (AnnouncementOptions.chat == null){
				RegisteredServiceProvider<Chat> provider = Bukkit.getServer().
						getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
				if (provider != null && provider.getProvider() != null) {
					AnnouncementOptions.setVaultChat(provider.getProvider());
				} else if (AnnouncementOptions.hc == null){
					Log.info("[BattleArena] Vault chat not detected, ignoring channel options");
				}
			}
		}
	}

}
