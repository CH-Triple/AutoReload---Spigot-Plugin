package chtriple.autoReload;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;


public class ReloadCommand implements CommandExecutor {

    private final AutoReload plugin;

    public ReloadCommand(AutoReload plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Every Command for Members without OP perms

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("§6§l=== §r§bAutoReload §6- Commands §l===");
            sender.sendMessage("\n§e* Member Commands *");
            sender.sendMessage("\n§7/ar help §f- Lists all Commands");
            sender.sendMessage("§7/ar info §f- Shows some Info about the plugin");
            sender.sendMessage("§7/ar time get §f- Shows the remaining time until the next server reload");
            sender.sendMessage("\n§e* Admin Commands *");
            sender.sendMessage("\n§7/ar time <sec|min|hour> set <time> §f- Sets the time between each reload");
            sender.sendMessage("§7/ar activate §f- Activates the timer");
            sender.sendMessage("§7/ar deactivate §f- Deactivates the timer");
            sender.sendMessage("\n§6§l==============================");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("time") && args[1].equalsIgnoreCase("get")) {
            if (!(sender instanceof org.bukkit.entity.Player)) {
                sender.sendMessage("§cOnly players can use this command.");
                return true;
            }

            AutoReload plugin = this.plugin;
            long secondsLeft = plugin.getTimeLeft();

            if (secondsLeft <= 0) {
                sender.sendMessage("§cNo reload timer is currently running.");
                return true;
            }

            String timeStr = formatTime(secondsLeft);
            sender.sendMessage("§aNext reload in: " + timeStr);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
            if (!(sender instanceof org.bukkit.entity.Player)) {
                sender.sendMessage("§cThis command can only be used by players.");
                return true;
            }
            org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;

            sender.sendMessage("§6§l=== §r§bAutoReload §6- Info §l===");
            sender.sendMessage("\n§bGeneral Info: §r§7This plugin was developed for the Minecraft project \"§5§lTripleSMP §r§7(§4Season 4§7)\".");
            sender.sendMessage("§4Important §bInfo: §r§7Please leave the Server §ain the last 10 Seconds §7(This way you avoid bugs)");
            sender.sendMessage("§bDeveloped/Tested versions: §r§a1.20.1");
            sender.sendMessage("\n§6§l=== §r§bCredits §6§l===");
            sender.sendMessage("\n§6Main Developer: §r§4CHTriple");
            sender.sendMessage("§6Help by: §r§c---");
            sender.sendMessage("§6Dev Info: §r§7This plugin was developed entirely by §4CHTriple§7. This is §athe original version §7and may be further developed.");
            sender.sendMessage("\n§6§l=== §r§bLinks §6- §4CHTriple §6§l===");
            // Spigot Link
            TextComponent spigot = new TextComponent("\n§6Spigot: §r§7My ");
            TextComponent spigotLink = new TextComponent("§e§nSpigot");
            spigotLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/members/chtriple.1875215/"));
            spigotLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to open Spigot profile")));
            spigot.addExtra(spigotLink);
            spigot.addExtra(" §7Account");
            player.spigot().sendMessage(spigot);
            // GitHub Link
            TextComponent github = new TextComponent("§6Github: §r§7My ");
            TextComponent githubLink = new TextComponent("§5§nGithub");
            githubLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/CH-Triple"));
            githubLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to open GitHub profile")));
            github.addExtra(githubLink);
            github.addExtra(" §7Account");
            player.spigot().sendMessage(github);
            // Discord Link
            TextComponent discord = new TextComponent("§6Discord: §r§7My ");
            TextComponent discordLink = new TextComponent("§d§nDiscord");
            discordLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.com/users/1151884440939806832"));
            discordLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to open Discord profile")));
            discord.addExtra(discordLink);
            discord.addExtra(" §7Account");
            player.spigot().sendMessage(discord);

            sender.sendMessage("\n§6§l==============================");
            return true;
        }

        // Every Command for Admins (OP)
        if (!sender.isOp()) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        if (args.length == 1) {
            String sub = args[0].toLowerCase();
            if (sub.equals("activate")) {
                plugin.getConfig().set("enabled", true);
                plugin.saveConfig();
                plugin.startAutoReloadTask(); // direkt starten
                sender.sendMessage("§aAutoReload has been activated.");
                return true;
            } else if (sub.equals("deactivate")) {
                plugin.getConfig().set("enabled", false);
                plugin.saveConfig();
                plugin.startAutoReloadTask(); // Task stoppen
                sender.sendMessage("§cAutoReload has been deactivated.");
                return true;
            } else {
                sender.sendMessage("§cUnknown subcommand: /ar " + args[0]);
                return true;
            }
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("time") && args[2].equalsIgnoreCase("set")) {
            String unit = args[1].toLowerCase();
            int value;

            try {
                value = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cTime must be a valid number.");
                return true;
            }

            int seconds;
            switch (unit) {
                case "sec":
                    seconds = value;
                    break;
                case "min":
                    seconds = value * 60;
                    break;
                case "hour":
                    seconds = value * 3600;
                    break;
                default:
                    sender.sendMessage("§cInvalid time unit. Allowed: sec, min, hour");
                    return true;
            }

            FileConfiguration config = plugin.getConfig();
            config.set("reload-interval-seconds", seconds);
            plugin.saveConfig();

            sender.sendMessage("§aReload interval set to " + seconds + " seconds.");
            return true;
        }

        sender.sendMessage("§cUsage:");
        sender.sendMessage("§e/ar time <sec|min|hour> set <time>");
        sender.sendMessage("§e/ar time get");
        sender.sendMessage("§e/ar activate");
        sender.sendMessage("§e/ar deactivate");
        return true;
    }

    private String formatTime(long totalSeconds) {
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");

        return sb.toString().trim();
    }
}
