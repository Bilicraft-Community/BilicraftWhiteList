package com.bilicraft.bilicraftwhitelist;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class WhoInvite implements CommandExecutor {
    private final BilicraftWhiteList plugin;
    public WhoInvite(BilicraftWhiteList plugin){
        this.plugin = plugin;
    }
    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1){
            sender.sendMessage(ChatColor.RED+"用法: /whoinvite <玩家ID>");
            return true;
        }
        sender.sendMessage(ChatColor.BLUE+"正在查询，请稍后...");
        new BukkitRunnable(){
            @Override
            public void run() {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                String inviterUUID = plugin.getConfig().getString("invites." + offlinePlayer.getUniqueId());
                if(StringUtils.isEmpty(inviterUUID)){
                    sender.sendMessage(ChatColor.RED+"没有人邀请此玩家");
                    return;
                }
                OfflinePlayer inviter = Bukkit.getOfflinePlayer(UUID.fromString(inviterUUID));
                sender.sendMessage(ChatColor.GREEN+"查询结果: "+ChatColor.YELLOW+inviter.getName());
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }
}
