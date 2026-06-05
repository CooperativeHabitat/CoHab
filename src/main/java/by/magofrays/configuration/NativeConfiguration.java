package by.magofrays.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(NativeHints.class)
public class NativeConfiguration {
}