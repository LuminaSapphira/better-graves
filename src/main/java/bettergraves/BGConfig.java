package bettergraves;

import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class BGConfig {

    @Comment("Add handler keys in here to disable them\nInsert \"bg-trinkets\" to disable default trinkets compatibility")
    public ArrayList<String> disabledCompat = new ArrayList<>();

    static BGConfig getConfig(Path folder) {
        try {

            Jankson jankson = Jankson.builder().build();
            if (Files.notExists(folder)) {
                Files.createDirectories(folder);
            }
            Path configPath = folder.resolve("bettergraves.json5");

            if (Files.notExists(configPath)) {
                saveDefaultConfig(configPath, jankson);
            }

            JsonObject object = jankson.load(configPath.toFile());
            return jankson.fromJson(object, BGConfig.class);

        } catch (Exception ex) {
            throw new CrashException(CrashReport.create(ex, "Loading Better Graves config file"));
        }

    }

    private static void saveDefaultConfig(Path configPath, Jankson jankson) throws Exception {
        BGConfig config = new BGConfig();
        String result = jankson.toJson(config).toJson(true, true);
        Files.write(configPath, result.getBytes(), StandardOpenOption.CREATE);
    }

    private BGConfig() {

    }

}
