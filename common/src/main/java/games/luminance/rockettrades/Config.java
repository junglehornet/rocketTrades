package games.luminance.rockettrades;

import dev.architectury.platform.Platform;

import java.nio.file.Path;

public class Config {
    private static Path configPath;

    public Config() {
        configPath = Path.of(Platform.getConfigFolder().toString() + "/rockettrades.toml");
        if (!configPath.toFile().exists()) {
            try {
                configPath.toFile().createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void registerInt(String key, String description, int defaultValue, int min, int max) {
        String confStr = configPath.toFile().toString();
        System.out.println("----------------------------------------------------------------confstr");
        System.out.println(confStr);
    }
}
