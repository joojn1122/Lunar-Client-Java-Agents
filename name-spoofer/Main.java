package net.joojn;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {

    public static List<String> config;

    public static void loadConfig() throws IOException {

        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        File file = new File(decodedPath).getParentFile();

        File configFile = new File(file, "config.txt");
        if(!configFile.exists()) throw new IOException("Config file not found!");

        config = FileUtils.readLines(configFile, StandardCharsets.UTF_8);

        if(config.size() < 2) throw new IOException("Config file doesn't contain enough params!");
    }


    public static void main(String[] args){}

    public static void premain(String agentArgs, Instrumentation inst) {

        try
        {
            loadConfig();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        inst.addTransformer(new LunarClassLoader());

        //inst.addTransformer(new TestTransformer());
    }

    public static void agentmain(String agentArgs, Instrumentation inst)
    {
        System.out.println("Agent Main");
    }
}
