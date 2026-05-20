package com.luacraft.sandbox.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.teams.TeamLib;
import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;

public class ScoreboardLib extends LuaTable {
    private final Scoreboard scoreboard;

    public ScoreboardLib(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;

        rawset("NewObjective", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue name, LuaValue displayName) {
                String objName = name.tojstring();
                Component objDisplayName = ComponentUtils.luaValueToComponent(displayName);

                Objective obj = scoreboard.registerNewObjective(objName, Criteria.DUMMY, objDisplayName);

                return new ObjectiveLib(obj);
            }
        });

        rawset("AddToPlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue player) {
                PlayerLib lib = (PlayerLib) player;
                Player ply = lib.getPlayer();

                ply.setScoreboard(scoreboard);

                return LuaValue.NIL;
            }
        });

        rawset("NewTeam", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue name) {
                Boolean exists = scoreboard.getTeam(LuaErrorAssert.checkString(name, "NewTeam", 1, null)) != null;
                Team team;

                String teamName = LuaErrorAssert.checkString(name, "NewTeam", 1, null);
                
                if (!exists) {
                    team = scoreboard.registerNewTeam(teamName);
                } else {
                    team = scoreboard.getTeam(teamName);
                }

                return new TeamLib(team);
            }
        });
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
