package com.luacraft.sandbox.skript;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import com.luacraft.sandbox.entity.PlayerLib;
import com.luacraft.sandbox.util.EntityTypeUtil;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class LuaFuncToSkript implements AddonModule {

    private Map<String, Globals> allGlobals = new HashMap<>();

    public LuaFuncToSkript(Map<String, Globals> allGlobals) {
        this.allGlobals = allGlobals;
    }

    @Override
    public void load(SkriptAddon addon) {
        var builder = SyntaxInfo.Expression.builder(LuaFuncToSkriptSyntax.class, Object.class);
        builder.addPattern("results from [a] lua function named %string% (with|using) [the] (args|arguments) %objects%");
        builder.addPattern("results from [a] lua function named %string% (with|using) [the] (args|arguments) from %objects%");
        
        builder.supplier(LuaFuncToSkriptSyntax::new);

        addon.syntaxRegistry().register(SyntaxRegistry.EXPRESSION, builder.build());
    }

    @Override
    public String name() {
        return "LuaCraft";
    }
    
    private class LuaFuncToSkriptSyntax extends SimpleExpression<Object> {

        private Expression<String> functionNameExpr;
        private Expression<?> argsExpr;
        private int indexPattern;

        @Override
        public Class<?> getReturnType() {
            return Object.class;
        }

        @Override
        public boolean isSingle() {
            return false;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean arg2, ParseResult arg3) {
            functionNameExpr = (Expression<String>) exprs[0];

            argsExpr = exprs[1];

            indexPattern = matchedPattern;

            return true;
        }

        @Override
        public String toString(@Nullable Event e, boolean debug) {
            return "call lua function " + (functionNameExpr != null ? functionNameExpr.toString(e, debug) : "?")
            + " with args " + (argsExpr != null ? argsExpr.toString(e, debug) : "none");
        }

        @Override
        protected Object @Nullable [] get(Event e) {
            if (functionNameExpr == null || argsExpr == null) return null;

            String funcName = functionNameExpr.getSingle(e);
            if (funcName == null) return null;

            switch (indexPattern) {
                case 0:
                    Object[] args = argsExpr.getAll(e);
                    LuaValue[] luaArgs = new LuaValue[args.length];

                    for (int i = 0; i < args.length; i++) {
                        luaArgs[i] = convertSkriptToLua(args[i]);
                    }

                    for (Globals globals : allGlobals.values()) {
                        LuaValue func = globals.get(funcName);
                        if (!func.isnil()) {
                            Varargs result = func.invoke(LuaValue.varargsOf(luaArgs));
                            return new Object[] { result.arg1() };
                        }
                    }
                    break;
            }
            return null;
        }
    }

    private LuaValue convertSkriptToLua(Object obj) {
        return switch (obj) {
            case Player player -> new PlayerLib(player);
            case Entity entity -> EntityTypeUtil.wrapEntity(entity);
            case String string -> LuaValue.valueOf(string);
            default -> LuaValue.NIL;
        };
    }
}