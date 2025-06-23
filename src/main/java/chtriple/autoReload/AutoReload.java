package chtriple.autoReload;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class AutoReload extends JavaPlugin {

    private int taskId = -1; // Scheduler task ID
    private long timeLeft; // Time for a reload in seconds

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("ar").setExecutor(new ReloadCommand(this));

        getLogger().info(ChatColor.GREEN + "AutoReload Plugin loaded (activated).");

        startAutoReloadTask();
    }

    @Override
    public void onDisable() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        getLogger().info(ChatColor.RED + "AutoReload Plugin unloaded (deactivated).");
    }

    public void startAutoReloadTask() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }

        if (!getConfig().getBoolean("enabled", false)) {
            getLogger().info(ChatColor.YELLOW + "AutoReload is disabled in config.");
            return;
        }

        timeLeft = getConfig().getInt("reload-interval-seconds", 600);
        if (timeLeft <= 0) {
            getLogger().warning(ChatColor.RED + "Invalid reload-interval-seconds in config.");
            return;
        }

        getLogger().info(ChatColor.GREEN + "AutoReload enabled. Reloading server every " + timeLeft + " seconds.");

        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (!getConfig().getBoolean("enabled", false)) {
                    // Stop task if disabled in runtime
                    Bukkit.getScheduler().cancelTask(taskId);
                    taskId = -1;
                    getLogger().info(ChatColor.YELLOW + "AutoReload disabled during runtime, task stopped.");
                    return;
                }

                if (timeLeft <= 0) {
                    // Reload the server
                    Bukkit.broadcastMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Reloading... Please wait a few seconds :)");
                    Bukkit.getLogger().info(ChatColor.YELLOW + "Reloading server now.");
                    Bukkit.reload();
                    // Reset timer
                    timeLeft = getConfig().getInt("reload-interval-seconds", 600);
                    return;
                }

                // Reminder messages
                sendReminders(timeLeft);

                // Countdown in the last 10 secs
                if (timeLeft <= 10 && timeLeft > 0) {
                    sendCountdownMessage((int) timeLeft);
                }

                timeLeft--;
            }
        }.runTaskTimer(this, 20L, 20L).getTaskId();
    }

    private void sendReminders(long secondsLeft) {
        // Hours Reminder
        if (secondsLeft % 3600 == 0 && secondsLeft >= 3600) {
            long hours = secondsLeft / 3600;
            Bukkit.broadcastMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "A server reload will take place in " + hours + " hour" + (hours > 1 ? "s" : ""));
        }
        // 30 Min Reminder
        else if (secondsLeft == 1800) {
            Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "A server reload will take place in 30 Minutes");
        }
        // 10 Min Reminder
        else if (secondsLeft == 600) {
            Bukkit.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "A server reload will take place in 10 Minutes - Please leave the Server §ain the last 10 Seconds §r(This way you avoid bugs)");
        }
    }

    private void sendCountdownMessage(int secondsLeft) {
        ChatColor color;
        if (secondsLeft >= 5 && secondsLeft <= 10) {
            // 10 and 5-6 in green and orange
            if (secondsLeft == 10) color = ChatColor.GREEN;
            else color = ChatColor.GOLD; // orange
        } else {
            // 4 to 1 in red
            color = ChatColor.RED;
        }

        Bukkit.broadcastMessage(color.toString() + ChatColor.BOLD + secondsLeft);
    }

    public long getTimeLeft() {
        return timeLeft;
    }
}
