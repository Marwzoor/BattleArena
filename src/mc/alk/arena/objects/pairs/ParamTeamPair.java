package mc.alk.arena.objects.pairs;

import mc.alk.arena.objects.MatchParams;
import mc.alk.arena.objects.teams.Team;

public class ParamTeamPair {
	public MatchParams q;
	public Team team;
	public ParamTeamPair(){q=null; team =null;}
	public ParamTeamPair(MatchParams q, Team t){this.q=q;this.team=t;}
}
