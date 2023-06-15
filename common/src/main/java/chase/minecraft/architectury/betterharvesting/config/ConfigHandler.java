package chase.minecraft.architectury.betterharvesting.config;

import chase.minecraft.architectury.betterharvesting.BetterHarvesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;

public class ConfigHandler
{
	private final File CONFIG_FILE = Path.of(Platform.getConfigFolder().toString(), "betterharvesting.json").toFile();
	private static ConfigHandler instance = null;
	private BetterHarvestingConfig config;
	
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
			}
		} catch (IOException e)
		{
			BetterHarvesting.log.error("Unable to read config file: {}, {}", CONFIG_FILE.getAbsolutePath(), e.getMessage());
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
	
	public String[] suggestions()
	{
		return Arrays.stream(config.getClass().getDeclaredFields()).map(Field::getName).toArray(String[]::new);
	}
	
	public void set(String name, Object value)
	{
		try
		{
			Field field = config.getClass().getDeclaredField(name);
			field.set(config, value);
			save();
		} catch (NoSuchFieldException | IllegalAccessException e)
		{
			BetterHarvesting.log.error("Unable to set config field: '{}' to '{}', {}", name, value, e.getMessage());
		}
	}
	
	public @Nullable Object get(String name)
	{
		try
		{
			Field field = config.getClass().getDeclaredField(name);
			return field.get(config);
			
		} catch (NoSuchFieldException | IllegalAccessException e)
		{
			BetterHarvesting.log.error("Unable to get config field: '{}' , {}", name, e.getMessage());
		}
		return null;
	}
	
	public @Nullable Component getAsComponent(String name)
	{
		@Nullable Object value = get(name);
		if (value != null)
		{
			if (value instanceof Boolean bool)
			{
				return Component.literal("%s%s%s is %s%s".formatted(ChatFormatting.GOLD, name, ChatFormatting.RESET, bool ? ChatFormatting.GREEN : ChatFormatting.RED, bool ? "ENABLED" : "DISABLED"));
			}
			return Component.literal("%s%s%s is %s%s".formatted(ChatFormatting.GOLD, name, ChatFormatting.RESET, ChatFormatting.GREEN, value));
		}
		return null;
	}
	
	public boolean exists(String name)
	{
		return get(name) != null;
	}
	
	public Component getAll()
	{
		MutableComponent component = Component.literal("Better Harvesting Configuration:\n").withStyle(ChatFormatting.AQUA);
		for (Component field : Arrays.stream(suggestions()).map(this::getAsComponent).toArray(Component[]::new))
		{
			component.append(field);
			component.append("\n");
		}
		return component;
	}
	
	public static ConfigHandler getInstance()
	{
		return instance == null ? new ConfigHandler() : instance;
	}
	
	public static BetterHarvestingConfig getConfig()
	{
		return getInstance().config;
	}
}
