package io.github.joemama.tomb.joestombs;

import io.github.joemama.tomb.joestombs.common.register.ModRegistry;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

public class JoesTombs implements ModInitializer {
    public static final String MODID = "joestombs";

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

    @Override
    public void onInitialize() {
        ModRegistry.init();
    }
}
