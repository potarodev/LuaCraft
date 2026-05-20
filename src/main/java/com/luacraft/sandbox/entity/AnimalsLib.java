package com.luacraft.sandbox.entity;


import org.bukkit.entity.Animals;
import org.bukkit.entity.Pig;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.luacraft.LuaErrorAssert;

public class AnimalsLib extends MobLib {
    private final Animals animal;

    public AnimalsLib(Animals animal) {
        super(animal);
        this.animal = animal;
        
        rawset("SetSaddle", new OneArgFunction() {
            public LuaValue call(LuaValue arg) {
                if (animal instanceof Pig pig) {
                    pig.setSaddle(LuaErrorAssert.checkBoolean(arg, "SetSaddle", 1, null));
                }
                
                return LuaValue.NIL;
            }
        });
    }

    public Animals getAnimal() {
        return animal;
    }
}
