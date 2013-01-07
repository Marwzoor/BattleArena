package mc.alk.arena.listeners;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mc.alk.arena.BattleArena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.api.events.ExperienceChangeEvent;
import com.herocraftonline.heroes.api.events.SkillUseEvent;

public enum HeroesListener implements Listener {
	INSTANCE;

	final Set<String> cancelExpLoss = Collections.synchronizedSet(new HashSet<String>());
	final Set<String> inArena = Collections.synchronizedSet(new HashSet<String>());

	static HashSet<String> disabledSkills = new HashSet<String>();

	/**
	 * Need to be highest to override the standard renames
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void cancelExperienceLoss(ExperienceChangeEvent event) {
		if (event.isCancelled())
			return;
		final String name = event.getHero().getName();
		if (cancelExpLoss.contains(name)){
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled=true)
	public void skillDisabled(SkillUseEvent event){
		if (event.getPlayer() == null){
			return;}
		if (!inArena.contains(event.getPlayer().getName())){
			return;}
		if (!containsHeroesSkill(event.getSkill().getName()))
			return;
		event.setCancelled(true);
	}

	public static void setCancelExpLoss(Player player) {
		/// Register ourself if we are starting to need to listen
		if (INSTANCE.cancelExpLoss.isEmpty() && INSTANCE.inArena.isEmpty()){
			Bukkit.getPluginManager().registerEvents(INSTANCE, BattleArena.getSelf());}
		INSTANCE.cancelExpLoss.add(player.getName());
	}

	public static void removeCancelExpLoss(Player player) {
		if (INSTANCE.cancelExpLoss.remove(player.getName())){
			/// Unregister if we aren't listening for any players
			if (INSTANCE.cancelExpLoss.isEmpty() && INSTANCE.inArena.isEmpty()){
				HandlerList.unregisterAll(INSTANCE);}
		}
	}

	public static void enterArena(Player player) {
		if (INSTANCE.cancelExpLoss.isEmpty() && INSTANCE.inArena.isEmpty()){
			Bukkit.getPluginManager().registerEvents(INSTANCE, BattleArena.getSelf());
		}
		INSTANCE.inArena.add(player.getName());
	}

	public static void leaveArena(Player player) {
		if (INSTANCE.inArena.remove(player.getName())){
			/// Unregister if we aren't listening for any players
			if (INSTANCE.cancelExpLoss.isEmpty() && INSTANCE.inArena.isEmpty()){
				HandlerList.unregisterAll(INSTANCE);}
		}
	}

	public static boolean containsHeroesSkill(String skill) {
		return disabledSkills.contains(skill.toLowerCase());
	}

	public static void addDisabledCommands(Collection<String> disabledCommands) {
		if (disabledSkills == null){
			disabledSkills = new HashSet<String>();}
		for (String s: disabledCommands){
			disabledSkills.add(s.toLowerCase());}
	}

}
