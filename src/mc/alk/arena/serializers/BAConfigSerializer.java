package mc.alk.arena.serializers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mc.alk.arena.BattleArena;
import mc.alk.arena.Defaults;
import mc.alk.arena.controllers.APIRegistrationController;
import mc.alk.arena.controllers.HeroesController;
import mc.alk.arena.controllers.OptionSetController;
import mc.alk.arena.objects.MatchState;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.arenas.ArenaType;
import mc.alk.arena.objects.exceptions.InvalidOptionException;
import mc.alk.arena.objects.messaging.AnnouncementOptions;
import mc.alk.arena.objects.messaging.AnnouncementOptions.AnnouncementOption;
import mc.alk.arena.objects.options.TransitionOption;
import mc.alk.arena.objects.options.TransitionOptions;
import mc.alk.arena.util.DisabledCommandsUtil;
import mc.alk.arena.util.KeyValue;
import mc.alk.arena.util.Log;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class BAConfigSerializer extends ConfigSerializer{
	public void loadDefaults(){
		try {config.load(file);} catch (Exception e){e.printStackTrace();}

		parseDefaultOptions(config.getConfigurationSection("defaultOptions"));
		if (!Defaults.MONEY_SET)
			Defaults.MONEY_STR = config.getString("moneyName",Defaults.MONEY_STR);
		Defaults.AUTO_UPDATE = config.getBoolean("autoUpdate", Defaults.AUTO_UPDATE);
		Defaults.TELEPORT_Y_OFFSET = config.getDouble("teleportYOffset", Defaults.TELEPORT_Y_OFFSET);
		Defaults.NUM_INV_SAVES = config.getInt("numberSavedInventories", Defaults.NUM_INV_SAVES);
		Defaults.ITEMS_IGNORE_STACKSIZE = config.getBoolean("ignoreMaxStackSize", Defaults.ITEMS_IGNORE_STACKSIZE);
		Defaults.ITEMS_UNSAFE_ENCHANTMENTS = config.getBoolean("unsafeItemEnchants", Defaults.ITEMS_UNSAFE_ENCHANTMENTS);
		Defaults.USE_ARENAS_ONLY_IN_ORDER = config.getBoolean("useArenasOnlyInOrder", Defaults.USE_ARENAS_ONLY_IN_ORDER);
		Defaults.ENABLE_TELEPORT_FIX = config.getBoolean("enableInvisibleTeleportFix", Defaults.ENABLE_TELEPORT_FIX);
		parseOptionSets(config.getConfigurationSection("optionSets"));
		DisabledCommandsUtil.addAll(config.getStringList("disabledCommands"));
		if (HeroesController.enabled()){
			List<String> disabled = config.getStringList("disabledHeroesSkills");
			if (disabled != null && !disabled.isEmpty()){
				HeroesController.addDisabledCommands(disabled);
			}
		}
	}

	public void loadCompetitions(){
		try {config.load(file);} catch (Exception e){e.printStackTrace();}
		Set<String> defaultMatchTypes = new HashSet<String>(Arrays.asList(new String[] {"arena","skirmish","colliseum","battleground"}));
		Set<String> defaultEventTypes = new HashSet<String>(Arrays.asList(new String[] {"freeForAll","deathMatch"}));
		Set<String> exclude = new HashSet<String>(Arrays.asList(new String[] {"defaultOptions","tourney","optionSets"}));

		Set<String> allTypes = new HashSet<String>(defaultMatchTypes);
		allTypes.addAll(defaultEventTypes);
		JavaPlugin plugin = BattleArena.getSelf();

		APIRegistrationController api = new APIRegistrationController();
		/// For legacy reasons
		ArenaType.register("ffa", Arena.class, BattleArena.getSelf());

		/// Setup tournament solo as it's a bit different
		try {
			setTypeConfig(plugin,"tourney",config.getConfigurationSection("tourney"), false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/// Now initialize the specific types
		for (String defaultType: allTypes){
			boolean isMatch = defaultMatchTypes.contains(defaultType);
			try {
				api.registerCompetition(plugin, defaultType, defaultType, Arena.class, null, this.getFile(), isMatch,true);
			} catch (Exception e) {
				Log.err("Couldnt configure arenaType " + defaultType+". " + e.getMessage());
				e.printStackTrace();
			}
		}

		/// Initialize custom matches or events
		Set<String> keys = config.getKeys(false);
		for (String key: keys){
			ConfigurationSection cs = config.getConfigurationSection(key);
			if (cs == null || allTypes.contains(key) || exclude.contains(key))
				continue;
			try {
				String cmd = config.getString(key+".command",null);
				cmd = config.getString(key+".cmd",cmd);
				if (cmd==null || cmd.isEmpty()){
					throw new InvalidOptionException("For a custom competition you must specify a command:");}
				api.registerCompetition(plugin, key, cmd, Arena.class, null, this.getFile(), true,true);
			} catch (Exception e) {
				Log.err("Couldnt configure arenaType " + key+". " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	protected static void parseDefaultOptions(ConfigurationSection cs) {
		if (cs == null){
			Log.err("[BA Error] defaultConfig section not found!!! Using default settings" );
			return;
		}
		Defaults.SECONDS_TILL_MATCH = cs.getInt("secondsTillMatch", Defaults.SECONDS_TILL_MATCH);
		Defaults.SECONDS_TO_LOOT = cs.getInt("secondsToLoot", Defaults.SECONDS_TO_LOOT);
		Defaults.MATCH_TIME = cs.getInt("matchTime", Defaults.MATCH_TIME);
		Defaults.MATCH_UPDATE_INTERVAL = cs.getInt("matchUpdateInterval", Defaults.MATCH_UPDATE_INTERVAL);
//		Defaults.MATCH_FORCESTART_ENABLED = cs.getBoolean("matchEnableForceStart", Defaults.MATCH_FORCESTART_ENABLED);
		Defaults.MATCH_FORCESTART_TIME = cs.getLong("matchForceStartTime", Defaults.MATCH_FORCESTART_TIME);
		Defaults.TIME_BETWEEN_CLASS_CHANGE = cs.getInt("timeBetweenClassChange", Defaults.TIME_BETWEEN_CLASS_CHANGE);

		Defaults.DUEL_ALLOW_RATED = cs.getBoolean("allowRatedDuels", Defaults.DUEL_ALLOW_RATED);
		Defaults.DUEL_CHALLENGE_INTERVAL = cs.getInt("challengeInterval", Defaults.DUEL_CHALLENGE_INTERVAL);

		Defaults.AUTO_EVENT_COUNTDOWN_TIME = cs.getInt("eventCountdownTime",Defaults.AUTO_EVENT_COUNTDOWN_TIME);
		Defaults.ANNOUNCE_EVENT_INTERVAL = cs.getInt("eventCountdownInterval", Defaults.ANNOUNCE_EVENT_INTERVAL);
		Defaults.ALLOW_PLAYER_EVENT_CREATION = cs.getBoolean("allowPlayerCreation", Defaults.ALLOW_PLAYER_EVENT_CREATION);
		Defaults.TIME_BETWEEN_SCHEDULED_EVENTS = cs.getInt("timeBetweenScheduledEvents", Defaults.TIME_BETWEEN_SCHEDULED_EVENTS);
		Defaults.SCHEDULER_ANNOUNCE_TIMETILLNEXT = cs.getBoolean("announceTimeTillNextEvent", Defaults.SCHEDULER_ANNOUNCE_TIMETILLNEXT);

		Defaults.ENABLE_PLAYER_READY_BLOCK = cs.getBoolean("enablePlayerReadyBlock", Defaults.ENABLE_PLAYER_READY_BLOCK);
		int value = cs.getInt("readyBlockType", Defaults.READY_BLOCK.getId());
		Defaults.READY_BLOCK = value > 0 && value < Material.values().length ? Material.values()[value] : Defaults.READY_BLOCK;

		parseOnServerStartOptions(cs);
		AnnouncementOptions an = new AnnouncementOptions();
		parseAnnouncementOptions(an,true,cs.getConfigurationSection("announcements"), true);
		parseAnnouncementOptions(an,false,cs.getConfigurationSection("eventAnnouncements"),true);
		AnnouncementOptions.setDefaultOptions(an);
	}

	private static void parseOnServerStartOptions( ConfigurationSection cs) {
		if (cs ==null || !cs.contains("onServerStart")){
			Log.warn(BattleArena.getPluginName() +" No onServerStart options found");
			return;
		}
		List<String> options = cs.getStringList("onServerStart");
		for (String op : options){
			if (op.equalsIgnoreCase("startContinuous")) Defaults.START_CONTINUOUS = true;
			else if (op.equalsIgnoreCase("startNext")) Defaults.START_NEXT = true;
		}
	}

	private void parseOptionSets(ConfigurationSection cs) {
		if (cs != null){
			Set<String> keys = cs.getKeys(false);
			if (keys != null){
				for (String key : keys){
					/// dont let people override defaults
					if (key.equalsIgnoreCase("storeAll") || key.equalsIgnoreCase("restoreAll")){
						Log.err("You can't override the default 'storeAll' and 'restoreAll'");
						continue;
					}
					try {
						TransitionOptions to = ConfigSerializer.getTransitionOptions(cs.getConfigurationSection(key));
						if (to != null){
							OptionSetController.addOptionSet(key, to);}
					} catch (Exception e) {
						Log.err("Couldn't parse optionSet=" + key);
						e.printStackTrace();
					}
				}
			}
		}

		try{
			TransitionOptions tops = new TransitionOptions();
			tops.addOption(TransitionOption.STOREEXPERIENCE);
			tops.addOption(TransitionOption.STOREGAMEMODE);
			tops.addOption(TransitionOption.STOREHEROCLASS);
			tops.addOption(TransitionOption.STOREHEALTH);
			tops.addOption(TransitionOption.STOREHUNGER);
			tops.addOption(TransitionOption.STOREMAGIC);
//			tops.addOption(TransitionOption.CLEARINVENTORYONFIRSTENTER);
			tops.addOption(TransitionOption.CLEARINVENTORY);
			tops.addOption(TransitionOption.CLEAREXPERIENCE);
			tops.addOption(TransitionOption.STOREITEMS);
			tops.addOption(TransitionOption.DEENCHANT);
			OptionSetController.addOptionSet("storeAll", tops);

			tops = new TransitionOptions();
			tops.addOption(TransitionOption.RESTOREEXPERIENCE);
			tops.addOption(TransitionOption.RESTOREGAMEMODE);
			tops.addOption(TransitionOption.RESTOREHEROCLASS);
			tops.addOption(TransitionOption.RESTOREHEALTH);
			tops.addOption(TransitionOption.RESTOREHUNGER);
			tops.addOption(TransitionOption.RESTOREMAGIC);
			tops.addOption(TransitionOption.RESTOREITEMS);
			tops.addOption(TransitionOption.CLEARINVENTORY);
			tops.addOption(TransitionOption.DEENCHANT);
			OptionSetController.addOptionSet("restoreAll", tops);
		} catch (Exception e){
			Log.err("Couldn't set default setOptions");
		}
	}
	public static AnnouncementOptions parseAnnouncementOptions(AnnouncementOptions an , boolean match, ConfigurationSection cs, boolean warn) {
		if (cs == null){
			if (warn)
				Log.err((match? "match" : "event" ) + " announcements are null. cs= ");
			return null;
		}
		Set<String> keys = cs.getKeys(false);
		for (String key: keys){
			MatchState ms = MatchState.fromName(key);
			if (ms == null){
				Log.err("Couldnt recognize matchstate " + key +" in the announcement options");
				continue;
			}
			List<String> list = cs.getStringList(key);
			for (String s: list){
				KeyValue<String,String> kv = KeyValue.split(s,"=");
				AnnouncementOption bo = AnnouncementOption.fromName(kv.key);
				if (bo == null){
					Log.err("Couldnt recognize AnnouncementOption " + s);
					continue;
				}
				an.setBroadcastOption(match, ms, bo,kv.value);
			}
		}
		return an;
	}

}
