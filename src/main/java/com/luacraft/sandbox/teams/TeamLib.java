package com.luacraft.sandbox.teams;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.component.ComponentLib;
import com.luacraft.sandbox.entity.EntityLib;
import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TeamLib extends LuaTable {
    public TeamLib(Team team) {
        rawset("SetColor", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue color) {
                String textColor = LuaErrorAssert.checkString(color, "SetColor", 1, null);
                NamedTextColor newColor = NamedTextColor.NAMES.value(textColor.toLowerCase());

                team.color(newColor);

                return LuaValue.NIL;
            }
        });

        rawset("AddPlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue player) {
                PlayerLib lib = (PlayerLib) player;
                Player ply = lib.getPlayer();

                team.addPlayer(ply);

                return LuaValue.NIL;
            }
        });

        rawset("AddEntity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue entity) {
                EntityLib lib = (EntityLib) entity;
                Entity ent = lib.getEntity();

                team.addEntity(ent);

                return LuaValue.NIL;
            }
        });

        rawset("RemoveEntity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue entity) {
                EntityLib lib = (EntityLib) entity;
                Entity ent = lib.getEntity();

                team.removeEntity(ent);

                return LuaValue.NIL;
            }
        });

        rawset("RemovePlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue player) {
                PlayerLib lib = (PlayerLib) player;
                Player ply = lib.getPlayer();

                team.removePlayer(ply);

                return LuaValue.NIL;
            }
        });

        rawset("Remove", new ZeroArgFunction() {
            @Override
            public LuaValue call() {

                team.unregister();

                return LuaValue.NIL;
            }
        });

        rawset("SetPrefix", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue prefix) {
                Component newPrefix = ComponentUtils.luaValueToComponent(prefix);

                team.prefix(newPrefix);

                return LuaValue.NIL;
            }
        });

        rawset("GetPrefix", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Component prefix = team.prefix();

                return new ComponentLib(prefix);
            } 
        });

        rawset("SetSuffix", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue suffix) {
                Component newSuffix = ComponentUtils.luaValueToComponent(suffix);

                team.suffix(newSuffix);

                return LuaValue.NIL;
            }
        });

        rawset("GetSuffix", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Component suffix = team.suffix();

                return new ComponentLib(suffix);
            } 
        });

        rawset("SetOption", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue option, LuaValue value) {
                Team.Option newOption = Team.Option.valueOf(option.checkjstring().toUpperCase());
                Team.OptionStatus newOptionStatus = Team.OptionStatus.valueOf(value.checkjstring().toUpperCase());

                team.setOption(newOption, newOptionStatus);

                return LuaValue.NIL;
            }
        });
    }
}
