# ⚙️ AutoReload - Spigot Plugin

AutoReload is a simple Minecraft plugin for Spigot 1.20.1+ that automatically reloads your server after a configurable time interval. It helps keep your server fresh and running smoothly without manual restarts.

---

## 🛠️ Commands

### Member Commands (no OP required)
- `/ar help`  
  Shows a list of all available commands.

- `/ar info`  
  Shows plugin info and useful links.

- `/ar time get`  
  Displays how much time is left until the next automatic reload.

### Admin Commands (OP only)
- `/ar time <sec|min|hour> set <value>`  
  Sets the reload interval (in seconds, minutes, or hours).

- `/ar activate`  
  Starts the automatic reload timer.

- `/ar deactivate`  
  Stops the automatic reload timer.

---

## 📃 Command Documentation

- **`/ar help`**  
  Prints a command overview for both members and admins.

- **`/ar time get`**  
  Shows the remaining time until the next reload in a friendly format (days, hours, minutes, seconds).

- **`/ar info`**  
  Provides general plugin info, credits, and clickable links to developer profiles.

- **`/ar time <unit> set <value>`**  
  Change the reload timer interval. Units can be seconds (`sec`), minutes (`min`), or hours (`hour`). Only accessible to server operators.

- **`/ar activate` and `/ar deactivate`**  
  Enable or disable the auto-reload feature instantly.

---

## 🔓 Why was this plugin made?

This plugin was developed specifically for the Minecraft project **TripleSMP (Season 4)**. It aims to simplify server management by automating reloads on a configurable timer.

**Important:** Please leave the server during the last 10 seconds before reload to avoid bugs or disconnects.

Tested and developed for Minecraft _1.20.1_

---

## 👑 Credits

- **Main Developer:** CHTriple  
- **Help by:** ---  
- This plugin was developed entirely by **CHTriple**. This is the original version and may be further developed.

---

## 🔗 Links

- [Spigot Profile](https://www.spigotmc.org/members/chtriple.1875215/)  
- [Discord Profile](https://discord.com/users/1151884440939806832)  

---

Thank you for using AutoReload!
