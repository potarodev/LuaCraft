package com.luacraft.sandbox.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Color;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Math;
import org.joml.Vector3f;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.LuaErrorAssert;
import com.luacraft.sandbox.color.ColorLib;
import com.luacraft.sandbox.util.ComponentUtils;
import com.luacraft.sandbox.vector.VectorLib;

import net.kyori.adventure.text.Component;

public class TextDisplayLib extends EntityLib {
    private final TextDisplay display;
    private final Map<String, Consumer<LuaValue>> configMap;

    public TextDisplayLib(TextDisplay display) {
        super(display);
        this.display = display;

        this.configMap = new HashMap<>();
            configMap.put("text", val -> display.text(ComponentUtils.luaValueToComponent(val)));
            configMap.put("shadow", val -> display.setShadowed(LuaErrorAssert.checkBoolean(val, "SpawnEntity", 3, null)));
            configMap.put("seeThrough", val -> display.setSeeThrough(LuaErrorAssert.checkBoolean(val, "SpawnEntity", 3, null)));
            configMap.put("textOpacity", val -> display.setTextOpacity((byte) Math.clamp(0, 255, LuaErrorAssert.checkInt(val, "SpawnEntity", 3, null))));
            configMap.put("lineWidth", val -> display.setLineWidth(LuaErrorAssert.checkInt(val, "SpawnEntity", 3, null)));
            configMap.put("alignment", val -> display.setAlignment(TextAlignment.valueOf(LuaErrorAssert.checkString(val, "SpawnEntity", 3, null))));
            configMap.put("backgroundColor", val -> {
                if (val.isnil()) {
                    display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
                }

                ColorLib colorLib = (ColorLib) val.touserdata();
                Color newColor = colorLib.getBukkitColor();

                display.setBackgroundColor(newColor);
            });
            configMap.put("defaultBackground", val -> display.setDefaultBackground(LuaErrorAssert.checkBoolean(val, "SpawnEntity", 3, null)));
            configMap.put("textSize", val -> {
                Vector vector = ((VectorLib) val).getVector();

                Vector3f vector3f = new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());

                display.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), vector3f, new AxisAngle4f()));
            });
            configMap.put("billboard", val -> display.setBillboard(Billboard.valueOf(LuaErrorAssert.checkString(val, "SpawnEntity", 3, null))));
            configMap.put("offset", val -> {
                Transformation existing = display.getTransformation();
                Vector vector = ((VectorLib) val).getVector();

                display.setTransformation(new Transformation(
                    new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ()),
                    existing.getLeftRotation(),
                    existing.getScale(),
                    existing.getRightRotation()
                ));
            });

        rawset("SetText", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue text) {
                Component component = ComponentUtils.luaValueToComponent(text);

                display.text(component);

                return TextDisplayLib.this;
            }
        });

        rawset("SetShadowed", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue shadowed) {
                Boolean bool = LuaErrorAssert.checkBoolean(shadowed, "SetShadowed", 1, null);

                display.setShadowed(bool);

                return TextDisplayLib.this;
            }
        });

        rawset("SetSeeThrough", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue seethrough) {
                Boolean bool = LuaErrorAssert.checkBoolean(seethrough, "SetSeeThrough", 1, null);

                display.setSeeThrough(bool);

                return TextDisplayLib.this;
            }
        });

        rawset("SetTextOpacity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue opacity) {
                byte newOpacity = (byte) Math.clamp(0, 255, LuaErrorAssert.checkInt(opacity, "SetTextOpacity", 1, null));

                display.setTextOpacity(newOpacity);

                return TextDisplayLib.this;
            }
        });

        rawset("SetLineWidth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue width) {
                int lineWidth = LuaErrorAssert.checkInt(width, "SetLineWidth", 1, null);

                display.setLineWidth(lineWidth);

                return TextDisplayLib.this;
            }
        });

        rawset("SetAlignment", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue align) {
                String alignment = LuaErrorAssert.checkString(align, "SetAlignment", 1, null);

                display.setAlignment(TextAlignment.valueOf(alignment));

                return TextDisplayLib.this;
            }
        });

        rawset("SetBackgroundColor", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue color) {
                if (color.isnil()) {
                    display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));

                    return TextDisplayLib.this;
                }

                ColorLib colorLib = (ColorLib) color.touserdata();
                Color newColor = colorLib.getBukkitColor();

                display.setBackgroundColor(newColor);

                return TextDisplayLib.this;
            }
        });

        rawset("SetDefaultBackground", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue has) {
                display.setDefaultBackground(LuaErrorAssert.checkBoolean(has, "SetDefaultBackground", 1, null));

                return TextDisplayLib.this;
            }
        });

        rawset("SetTextSize", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue vec) {
                Vector vector = ((VectorLib) vec).getVector();

                Vector3f vector3f = new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());

                display.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), vector3f, new AxisAngle4f()));

                return TextDisplayLib.this;
            }
        });

        rawset("SetBillboard", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue style) {
                Billboard billboard = Billboard.valueOf(LuaErrorAssert.checkString(style, "SetBillboard", 1, null));

                display.setBillboard(billboard);

                return TextDisplayLib.this;
            }
        });

        rawset("SetOffset", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue vec) {
                Transformation existing = display.getTransformation();
                Vector vector = ((VectorLib) vec).getVector();

                display.setTransformation(new Transformation(
                    new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ()),
                    existing.getLeftRotation(),
                    existing.getScale(),
                    existing.getRightRotation()
                ));

                return TextDisplayLib.this;
            }
        });
    }

    public TextDisplay getTextDisplay() {
        return display;
    }

    @Override
    public Map<String, Consumer<LuaValue>> getConfigMap() {
        return configMap;
    }
}
