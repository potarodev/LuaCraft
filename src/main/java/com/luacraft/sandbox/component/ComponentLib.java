package com.luacraft.sandbox.component;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.minimessage.MiniMessageFactory;
import com.luacraft.sandbox.util.ComponentUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ComponentLib extends LuaTable {
    private final Component component;
    public ComponentLib(Component component) {
        this.component = component;
        rawset("Compare", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue comp2) {
                Component otherComponent = ComponentUtils.luaValueToComponent(comp2);
                String json1 = GsonComponentSerializer.gson().serialize(component);
                String json2 = GsonComponentSerializer.gson().serialize(otherComponent);
        
                return LuaValue.valueOf(json1.equals(json2));
            }
        });

        rawset("ToString", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue type) {
                String returnType;

                if (!type.isnil()) {
                    returnType = LuaErrorAssert.checkString(type, "ToString", 1, null);
                } else {
                    returnType = "default";
                }

                switch(returnType) {
                    case "plain":
                        return LuaValue.valueOf(PlainTextComponentSerializer.plainText().serialize(component));
                    case "legacy":
                        return LuaValue.valueOf(LegacyComponentSerializer.legacyAmpersand().serialize(component));
                    case "minimessage":
                        return LuaValue.valueOf(MiniMessage.miniMessage().serialize(component));
                    default:
                        return LuaValue.valueOf(PlainTextComponentSerializer.plainText().serialize(component));
                }
            }
        });

        rawset("Italics", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue bool) {
                Component modified = component.decoration(TextDecoration.ITALIC, bool.checkboolean());
                return new ComponentLib(modified);
            }
        });

        rawset("ClickEvent", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue type, LuaValue value) {
                String clickEventType = type.tojstring();
                String clickEventValue = value.tojstring();
                Component newComponent;

                switch (clickEventType) {
                    case "url":
                        newComponent = component.clickEvent(ClickEvent.openUrl(clickEventValue));
                        return new ComponentLib(newComponent);
                    case "command":
                        newComponent = component.clickEvent(ClickEvent.runCommand(clickEventValue));
                        return new ComponentLib(newComponent);
                    case "suggestcommand":
                        newComponent = component.clickEvent(ClickEvent.suggestCommand(clickEventValue));
                        return new ComponentLib(newComponent);
                    case "callback":
                        LuaFunction func = value.checkfunction();
                        newComponent = component.clickEvent(ClickEvent.callback(clicker -> {
                            func.call();
                        }));
                        return new ComponentLib(newComponent);
                }

                return LuaValue.NIL;
            }
        });

        rawset("AddHoverEvent", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue compon) {
                Component comp = ComponentUtils.luaValueToComponent(compon);
                Component result = component.hoverEvent(HoverEvent.showText(comp));

                return new ComponentLib(result);
            }
        });

        rawset("Colorize", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue type) {
                switch (type.checkjstring().toLowerCase()) {
                    case "minimessage":
                        return MiniMessageFactory.colorize(component);
                    case "component":
                        return ComponentFactory.colorize(component);
                    default:
                        return ComponentFactory.colorize(component);
                }
            }
        });
    }

    public Component getComponent() {
        return component;
    }
}