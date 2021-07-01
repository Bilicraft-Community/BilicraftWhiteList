package com.bilicraft.bilicraftwhitelist;

import com.google.common.base.Charsets;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public final class BilicraftWhiteList extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("whoinvite").setExecutor(new WhoInvite(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("该命令仅限玩家使用");
            return true;
        }
        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "命令输入有误，正确输入：/invite <游戏ID>");
            return true;
        }
        if (args.length == 1) {
            sender.sendMessage(ChatColor.AQUA + "您正在邀请玩家 " + ChatColor.YELLOW + ChatColor.AQUA + args[0] + " 加入 Bilicraft");
            sender.sendMessage(ChatColor.AQUA + "邀请成功后，您邀请的玩家将会自动获得 Bilicraft 白名单");
            sender.sendMessage(ChatColor.YELLOW + "注意：如果您邀请的玩家发生了违规行为，您将会承担连带责任");
            sender.sendMessage(ChatColor.GREEN + "确认邀请请输入 " + ChatColor.GOLD + "/invite " + args[0] + " confirm");
            return true;
        }
        if (args.length == 2) {
            sender.sendMessage(ChatColor.BLUE + "正在处理，请稍等...");
            new BukkitRunnable() {
                @Override
                public void run() {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    String username = offlinePlayer.getName();
                    UUID cracked = UUID.nameUUIDFromBytes(("OfflinePlayer:" + args[0]).getBytes(Charsets.UTF_8));
                    if (offlinePlayer.getUniqueId().equals(cracked) || !args[0].equalsIgnoreCase(username)) {
                        sender.sendMessage(ChatColor.RED + "该玩家不存在或网络连接失败，请检查拼写是否正确并稍后重试");
                        return;
                    }
                    if (Bukkit.getWhitelistedPlayers().contains(offlinePlayer)) {
                        sender.sendMessage(ChatColor.RED + "您邀请的玩家已在白名单中，无需重复邀请");
                        return;
                    }
                    getConfig().set("invites." + offlinePlayer.getUniqueId(), player.getUniqueId().toString());
                    saveConfig();
                    offlinePlayer.setWhitelisted(true);
                    sender.sendMessage(ChatColor.GREEN + "邀请成功");
                    getLogger().info("玩家 " + sender.getName() + " 邀请了 " + username);
                }
            }.runTaskAsynchronously(this);
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPlayedBefore()) {
            return;
        }
        String uuid = getConfig().getString("invites." + event.getPlayer().getUniqueId());
        if (StringUtils.isEmpty(uuid)) {
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(ChatColor.GREEN + "玩家 " + ChatColor.YELLOW + event.getPlayer().getName() + ChatColor.GREEN + " 由 " + ChatColor.YELLOW + offlinePlayer.getName() + ChatColor.GREEN + " 邀请");
            }
        }.runTaskAsynchronously(this);
    }
}
