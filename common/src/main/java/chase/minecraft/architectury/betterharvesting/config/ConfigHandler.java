package chase.minecraft.architectury.betterharvesting.config;

import chase.minecraft.architectury.betterharvesting.BetterHarvesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;

import java.io.*;
import java.nio.file.Path;

public class ConfigHandler
{
	private final File CONFIG_FILE = Path.of(Platform.getConfigFolder().toString(), "betterharvesting.json").toFile();
	private static ConfigHandler instance = null;
	public BetterHarvestingConfig config;
	
	protected ConfigHandler()
	{
		instance = this;
		config = new BetterHarvestingConfig();
		load();
	}
	
	public void load()
	{
		try
		{
			try (FileReader reader = new FileReader(CONFIG_FILE))
			{
				Gson gson = new Gson();
				config = gson.fromJson(reader, BetterHarvestingConfig.class);
			}
		} catch (FileNotFoundException e)
		{
			try
			{
				if (CONFIG_FILE.createNewFile())
					save();
			} catch (IOException ex)
			{
				BetterHarvesting.log.error("Unable to create config file: {}, {}", CONFIG_FILE.getAbsolutePath(), ex.getMessage());
				save();
			}
		} catch (IOException e)
		{
			BetterHarvesting.log.error("Unable to read config file: {}, {}", CONFIG_FILE.getAbsolutePath(), e.getMessage());
			save();
		}
		if (config == null)
			save();
	}
	
	public void save()
	{
		try (FileWriter writer = new FileWriter(CONFIG_FILE))
		{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(config);
			writer.write(json);
			writer.flush();
		} catch (IOException e)
		{
			BetterHarvesting.log.error("Unable to write file: {}, {}", CONFIG_FILE.getAbsolutePath(), e.getMessage());
		}
	}
	
	public static ConfigHandler getInstance()
	{
		return instance == null ? new ConfigHandler() : instance;
	}
}
