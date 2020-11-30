package pl.arturekdev.mineTeams.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import pl.arturekdev.mineUtiles.utils.MessageUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Messages {

    private static HashMap<String, String> messages;
    private static File messagesFile;

    private final JavaPlugin javaPlugin;

    public Messages(JavaPlugin plugin) {
        messagesFile = new File(plugin.getDataFolder() + "/messages.json");
        this.javaPlugin = plugin;
    }

    public static String get(String path, String defaultMessage) {

        if (!messages.containsKey(path)) {
            messages.put(path, defaultMessage);
            saveMessages();
            return MessageUtil.fixColor(defaultMessage);
        } else {
            return MessageUtil.fixColor(messages.get(path));
        }

    }

    public static List<String> getList(String path, String defaultMessage) {

        if (!messages.containsKey(path)) {
            messages.put(path, defaultMessage);
            saveMessages();
            defaultMessage = MessageUtil.fixColor(defaultMessage);
            return new ArrayList<>(Arrays.asList(defaultMessage.split(";")));
        } else {
            String message = MessageUtil.fixColor(messages.get(path));
            return new ArrayList<>(Arrays.asList(message.split(";")));
        }

    }

    @SneakyThrows
    private static void saveMessages() {

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        String jsonConfig = gson.toJson(messages);

        FileWriter writer;
        try {
            writer = new FileWriter(messagesFile);
            writer.write(jsonConfig);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public void loadMessages() {

        if (!messagesFile.exists()) {
            javaPlugin.saveResource("messages.json", false);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(messagesFile)));
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();

        messages = gson.fromJson(reader, type);

        if (messages == null) messages = new HashMap<>();
    }

    @SneakyThrows
    public void reloadMessages() {
        loadMessages();
    }
}
