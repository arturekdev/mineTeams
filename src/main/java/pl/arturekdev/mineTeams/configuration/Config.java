package pl.arturekdev.mineTeams.configuration;

import com.google.gson.*;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Config {

    private final File configurationFile;

    @Getter
    private JsonObject parsedConfiguration;

    public Config(File configurationFile) {
        this.configurationFile = configurationFile;
    }

    public JsonElement getElement(String location) {
        return Objects.requireNonNull(parsedConfiguration, "Configuration is not loaded yet.")
                .get(location);
    }

    private void readConfiguration() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(configurationFile));
        parsedConfiguration = new JsonParser()
                .parse(reader)
                .getAsJsonObject();
    }

    @SneakyThrows
    public void parseConfiguration(JavaPlugin plugin) {
        File parentFile = configurationFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }

        if (!configurationFile.exists()) {
            plugin.saveResource("config.json", false);
        }

        readConfiguration();
    }

    @SneakyThrows
    public void saveConfiguration() {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();

        Writer outputStreamWriter;
        BufferedWriter bufferedWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(configurationFile), StandardCharsets.UTF_8);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bufferedWriter == null) {
            return;
        }

        gson.toJson(parsedConfiguration, bufferedWriter);

        bufferedWriter.flush();
        bufferedWriter.close();
    }

}