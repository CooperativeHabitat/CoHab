package by.magofrays.configuration;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import java.util.UUID;

public class NativeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(UUID[].class);
        hints.resources().registerPattern("db/**");
        hints.resources().registerPattern("db/changelog/**");
        hints.resources().registerPattern("db/1.0/**");
    }
}