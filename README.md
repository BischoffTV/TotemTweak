# TotemTweak

A Minecraft plugin for version 1.21 that prevents totem of undying drops from various sources and instead adds them to stronghold chests with configurable chances.

## Features

- **Prevents totem of undying drops** from entities and other sources
- **Adds totems to stronghold chests** with configurable drop chance
- **Configurable settings** via config.yml
- **International support** with English messages
- **Reload command** for easy configuration updates
- **Debug mode** for detailed logging
- **bStats integration** for anonymous usage statistics

## Installation

1. Download the compiled JAR file
2. Place it in your server's `plugins` folder
3. Start/restart your server
4. Configure the plugin using the generated `config.yml` file

## Configuration

### config.yml

```yaml
# Totem of Undying drop chance in stronghold chests (0.0 to 1.0)
# 0.0 = 0% chance, 1.0 = 100% chance
totem-drop-chance: 0.3

# Whether to prevent totem of undying drops from other sources
# Set to false to allow normal totem drops
prevent-other-drops: true

# Debug mode - enables additional console logging
debug: false

# Enable bStats metrics collection (anonymously collects plugin usage data)
# You can view the collected data at: https://bstats.org/plugin/bukkit/TotemTweak/26799
bstats-enabled: true
```

### messages.yml

All player and admin messages are stored in `messages.yml` and can be customized. The plugin uses English by default for international compatibility.

## Commands

- `/totemtweak reload` - Reload the plugin configuration
- `/totemtweak help` - Show help information
- `/tt reload` - Short alias for reload command

## Permissions

- `totemtweak.admin` - Access to all TotemTweak commands (default: op)
- `totemtweak.reload` - Permission to reload the plugin (default: op)

## How It Works

1. **Drop Prevention**: The plugin prevents totem of undying drops from entities like evokers, raids, and other sources
2. **Stronghold Integration**: When players open chests in strongholds, the plugin checks if a totem should be added based on the configured chance
3. **Smart Detection**: The plugin detects stronghold chests by checking for stronghold building materials in the surrounding area

## bStats Integration

This plugin uses bStats to collect anonymous usage statistics. This helps us understand how the plugin is being used and improve it over time. The collected data includes:

- Server version and plugin version
- Basic plugin configuration settings
- No personal or server-specific information is collected

You can view the collected data at: https://bstats.org/plugin/bukkit/TotemTweak/26799

To disable bStats, set `bstats-enabled: false` in your `config.yml` file.

## Building from Source

1. Clone the repository
2. Ensure you have Java 21 and Maven installed
3. Run `mvn clean package`
4. The compiled JAR will be in the `target` folder

## Dependencies

- Spigot/Paper 1.21.8 or higher
- Java 21
- bStats (included in the plugin)

## Support

For issues, feature requests, or questions, please create an issue on the project repository.

## License

This project is licensed under the MIT License. 