package com.crashcringle.barterplus.commands;

import com.crashcringle.barterplus.BarterPlus;
import com.crashcringle.barterplus.barterkings.BarterKings;
import com.crashcringle.barterplus.barterkings.trades.Trade;
import com.crashcringle.barterplus.barterkings.trades.TradeRequest;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.logging.Level;

public class CommandTrade implements CommandExecutor {

    // TODO - END CURRENT BARTER GAME IF ONE IS IN PROGRESS
    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param cmd Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand( CommandSender sender,  Command cmd,  String label,  String[] args) {
        if (cmd.getName().equalsIgnoreCase("barter")) {
            if (args.length == 0) {
                if (sender.hasPermission("barterplus.help")) {
                    sender.sendMessage(ChatColor.GREEN + "Right click a player to initiate a trade with them!");
                    sender.sendMessage(ChatColor.GRAY + "Commands:");
                    sender.sendMessage(ChatColor.GREEN + "/barter trade <player> <Item You wish to Offer> <amount> <Item You're Requesting> <amount> - Request a trade with a player");
                    sender.sendMessage(ChatColor.GREEN + "/barter trade <player> <Item You're Requesting> <amount> - Request a trade with a player offering the item in your hand");
                    sender.sendMessage(ChatColor.GREEN + "/barter accept [player] - Accept a trade request from a player");
                    sender.sendMessage(ChatColor.GREEN + "/barter deny [player] - Deny a trade request from a player");
                    sender.sendMessage(ChatColor.GREEN + "/barter cancel [player] - Cancel a trade request to a player");
                    sender.sendMessage(ChatColor.GREEN + "/barter list - List all trade requests");
                    sender.sendMessage(ChatColor.GREEN + "/barter openTrade <player> - Opens an insecure trade with a player");
                    sender.sendMessage(ChatColor.GREEN + "/help - Display this help message");
                    sender.sendMessage(ChatColor.GREEN + "/barter readyUp - Ready up for the game");
                    sender.sendMessage(ChatColor.GREEN + "/barter unready - Unready for the game");
                    sender.sendMessage(ChatColor.GREEN + "/barter start - Start the game");
                    sender.sendMessage(ChatColor.GREEN + "/barter join - Join the game");
                    sender.sendMessage(ChatColor.GREEN + "/barter leave - Leave the game");
                }
                return true;    // Return true because the command was executed successfully
            } else {
                if (args[0].equalsIgnoreCase("admin-trade")) {
                    BarterPlus.inst().getLogger().log(Level.INFO, "Admin Trade Command");
                    if (args.length == 7) {
                        Player requester = null, requested = null;
                        for (NPC npc : CitizensAPI.getNPCRegistry()) {
                            BarterPlus.inst().getLogger().log(Level.INFO, "NPC: " + npc.getName());

                            if (npc.getName().equalsIgnoreCase(args[6]))
                                requested = (Player) npc.getEntity();
                            if (npc.getName().equalsIgnoreCase(args[1]))
                                requester = (Player) npc.getEntity();
                        }
                        if (requester == null || requested == null) {
                            BarterPlus.inst().getLogger().info("NPCs not found");
                        }
                        if (requested == null) {
                            try {
                                requested = Bukkit.getPlayer(args[6]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (requester == null) {
                            try {
                                requester = Bukkit.getPlayer(args[1]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (requested != null && requester != null) {
                            try {
                                if (requester.getInventory().containsAtLeast(new ItemStack(Material.getMaterial(args[2].toUpperCase())), Integer.parseInt(args[3]))) {
                                    BarterKings.controller.sendTradeRequest(requester, requested, new Trade(new ItemStack(Material.getMaterial(args[2].toUpperCase())), Integer.parseInt(args[3]), new ItemStack(Material.getMaterial(args[4].toUpperCase())), Integer.parseInt(args[5])));
                                } else {
                                    BarterPlus.inst().getLogger().log(Level.INFO, "You do not have enough of that item to trade");
                                }
                            } catch (IllegalArgumentException e) {
                                BarterPlus.inst().getLogger().log(Level.INFO, "Invalid Item");
                                return false;
                            }
                        } else {
                            BarterPlus.inst().getLogger().log(Level.INFO, "Player is null");
                            try {
                                BarterKings.controller.sendTradeRequest(args[6], args[1], new Trade(new ItemStack(Material.getMaterial(args[2].toUpperCase())), Integer.parseInt(args[3]), new ItemStack(Material.getMaterial(args[4].toUpperCase())), Integer.parseInt(args[5])));
                            } catch (IllegalArgumentException e) {
                                BarterPlus.inst().getLogger().log(Level.INFO, "Invalid Item");
                                return false;
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("admin-accept")) {
                    if (args.length == 3) {
                        String requester = args[1];
                        String requested = args[2];
                        if (requester != null) {
                            BarterKings.controller.acceptTrade(requester, requested);
                        }
                    } else if (args.length == 2) {
                        String requested = args[1];
                        if (requested != null) {
                                BarterKings.controller.acceptRecentTrade(requested);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("admin-deny")) {
                    if (args.length == 3) {
                        String requester = args[1];
                        String requested = args[2];
                        if (requester != null) {
                            BarterKings.controller.denyTrade(requester, requested);
                        }
                    } else if (args.length == 2) {
                        String requested = args[1];
                        if (requested != null) {
                            BarterKings.controller.denyRecentTrade(requested);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("admin-cancel")) {
                    if (args.length == 3) {
                        String requested = args[1];
                        String requester = args[2];
                        if (requested != null) {
                        }
                    } else if (args.length == 2) {
                        String requested = args[1];
                        if (requested != null) {
                            BarterKings.controller.cancelRecentTrade(requested);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("admin-unready")) {
                    if (args.length == 2) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if (player != null) {
                            if (player.isOnline()) {
                                if (player != sender) {
                                    if (sender instanceof Player) {
                                        BarterKings.barterGame.unready(player);
                                    }
                                }
                            }
                        }
                    }
                    } else if (args[0].equalsIgnoreCase("readyUp")) {
                    if (sender.hasPermission("barterplus.ready")) {
                        if (BarterKings.barterGame.inProgress() ) {
                            sender.sendMessage(ChatColor.RED + "A game is already in progress");
                            return false;
                        }

                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            if (!BarterKings.barterGame.isParticipant(player)) {
                                sender.sendMessage(ChatColor.RED + "You are not a participant");
                                return false;
                            }
                            if (args.length == 1) {
                                if(BarterKings.barterGame.isReady(player)) {
                                    BarterKings.barterGame.unready(player);
                                    sender.sendMessage(ChatColor.GREEN + "You are no longer ready");
                                } else {
                                    BarterKings.barterGame.readyUp(player);
                                    sender.sendMessage(ChatColor.GREEN + "You are now ready");
                                }
                                return true;
                            } else {
                                sender.sendMessage(ChatColor.RED + "Usage: /barter readyUp");
                                return false;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                        return false;
                    }
                } else if (args[0].equalsIgnoreCase("join")) {
                    if (sender.hasPermission("barterplus.join")) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            if (BarterKings.barterGame.inProgress() ) {
                                sender.sendMessage(ChatColor.RED + "A game is already in progress");
                                return false;
                            }

                            if (!BarterKings.barterGame.isParticipant(player)) {
                                if (args.length == 1) {
                                    if (!BarterKings.barterGame.inProgress() && BarterKings.barterGame.getParticipants().size() < 1 ) {
                                        // Broadcast that the player is trying to start the game
                                        BarterKings.startNewGame();
                                        Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " is trying to start a Barter Game! Do \"/barter join\" to join!");
                                    }
                                    BarterKings.barterGame.addParticipant(player);
                                    BarterKings.barterGame.readyUp(player);
                                    sender.sendMessage(ChatColor.GREEN + "You are now a participant");
                                    sender.sendMessage(ChatColor.GREEN + "Type \"/barter readyUp\" to toggle readiness for the game");
                                    return true;
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Usage: /barter join");
                                    return false;
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "You are already a participant");
                                return false;
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (sender.hasPermission("barterplus.leave")) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            if (BarterKings.barterGame.isParticipant(player)) {
                                if (args.length == 1) {
                                    BarterKings.barterGame.removeParticipant(player);
                                    sender.sendMessage(ChatColor.GREEN + "You are no longer participant");
                                    return true;
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Usage: /barter leave");
                                    return false;
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "You were never a participant");
                                return false;
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("start")) {
                    if (sender.hasPermission("barterplus.start")) {
                        if (args.length == 1) {
                            if (BarterKings.barterGame.inProgress()) {
                                sender.sendMessage(ChatColor.RED + "A game is already in progress");
                                return false;
                            }
                            BarterKings.barterGame.attemptStart();
                            return true;
                        } else if (args.length == 2) {
                            try {
                                BarterKings.barterGame.attemptStart(Integer.parseInt(args[1]));
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + "Usage: /barter start [Time]");
                                return false;
                            }
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /barter start");
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                        return false;
                    }
                } else if (args[0].equalsIgnoreCase("end")) {
                    if (sender.hasPermission("barterplus.end")) {
                        if (args.length == 1) {
                            if (!BarterKings.barterGame.inProgress()) {
                                sender.sendMessage(ChatColor.RED + "There is no game in progress");
                                return false;
                            }
                            BarterKings.barterGame.attemptEnd();
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /barter end");
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                        return false;
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    if (sender.hasPermission("barterplus.help")) {
                        sender.sendMessage(ChatColor.GRAY + "Commands:");
                        sender.sendMessage(ChatColor.GREEN + "/barter trade <player> <offeredItem> <amount> <requestedItem> <amount> - Request a trade with a player");
                        sender.sendMessage(ChatColor.GREEN + "/barter accept [player] - Accept a trade request from a player");
                        sender.sendMessage(ChatColor.GREEN + "/barter deny [player] - Deny a trade request from a player");
                        sender.sendMessage(ChatColor.GREEN + "/barter cancel [player] - Cancel a trade request to a player");
                        sender.sendMessage(ChatColor.GREEN + "/barter openTrade <player> - Opens an insecure trade with a player");
                        sender.sendMessage(ChatColor.GREEN + "/help - Display this help message");
                    }
                    return true;    // Return true because the command was executed successfully
                }  else if (args[0].equalsIgnoreCase("score")) {
                    if (args.length >= 2 && args[1].length() > 1) {
                        if (!(sender instanceof Player)) {
//                            Player player = Bukkit.getPlayer(args[1]);
                            Player player = BarterKings.barterGame.getParticipant(args[1]).getPlayer();
                            if (player != null) {
                                if (BarterKings.barterGame.isParticipant(player)) {
                                    BarterPlus.inst().getLogger().log(Level.INFO, ChatColor.GRAY + "Player: " + player.getName() + "(" + BarterKings.barterGame.getParticipant(player).getProfession().getName()+") score is: " + BarterKings.barterGame.getParticipant(player).getCalculatedScore2());
                                    return true;
                                } else {
                                    player.sendMessage(ChatColor.RED + "They are not in the game");
                                    return false;
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "That player is not online");
                                return false;
                            
                            }
                        }
                    } else if (sender.hasPermission("barterplus.score")) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            if (BarterKings.barterGame.isParticipant(player)) {
                                player.sendMessage(ChatColor.GRAY + "Your current score is " + BarterKings.barterGame.getParticipant(player).getCalculatedScore());
                                return true;
                            } else {
                                player.sendMessage(ChatColor.RED + "You are not in the game");
                                return false;
                            }
                        }
                    }
                    
                }
                else if (args[0].equalsIgnoreCase("trade")) {
                    if (sender.hasPermission("barterplus.trade.request")) {
                        // Trade where only the item requested is specified. The item offered is the item in the player's hand
                        if (args.length == 4) {
                            String requested = args[1];
                            if (requested != null) {
                                if (sender instanceof Player) {
                                    Player requester = (Player) sender;
                                    try {
                                        BarterKings.controller.sendTradeRequest(requester.getName(), requested, new Trade(requester.getInventory().getItemInMainHand(),  Integer.parseInt(args[3]), new ItemStack(Material.getMaterial(args[2].toUpperCase())))) ;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        sender.sendMessage(ChatColor.RED + "Error, Try again!");
                                    }
                                }
                            }
                        }
                        else if (args.length == 6) {
                            String requested = args[1];
                            if (requested != null) {
                                if (sender instanceof Player) {
                                    Player requester = (Player) sender;
                                    try {
                                        if (requester.getInventory().containsAtLeast(new ItemStack(Material.getMaterial(args[2].toUpperCase())), Integer.parseInt(args[3]))) {
                                            BarterKings.controller.sendTradeRequest(requester.getName(), requested, new Trade(new ItemStack(Material.getMaterial(args[2].toUpperCase())), Integer.parseInt(args[3]), new ItemStack(Material.getMaterial(args[4].toUpperCase())), Integer.parseInt(args[5])));
                                        } else {
                                            requester.sendMessage(ChatColor.RED + "You do not have enough of that item to trade");
                                        }
                                    } catch (IllegalArgumentException e) {
                                        requester.sendMessage(ChatColor.DARK_RED + " Invalid Item");
                                        return false;
                                    }

                                }
                            }
                        }   else {
                            sender.sendMessage(ChatColor.RED + "/barter trade <player> <Item You wish to Offer> <amount> <Item You're Requesting> <amount>");
                            sender.sendMessage(ChatColor.RED + "You must specify a player to trade with, the item you want to offer, the amount of that item, the item you want to receive, and the amount of that item you want to receive");
                        }

                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("deny")) {
                    if (sender.hasPermission("barterplus.trade.deny")) {
                        if (args.length == 2) {
                            String requester = args[1];
                            if (requester != null) {
                                if (requester != sender.getName()) {
                                    if (sender instanceof Player) {
                                        Player requested = (Player) sender;
                                        BarterKings.controller.denyTrade(requested.getName(), requester);
                                    }
                                }
                            }
                        } else {
                            if (sender instanceof Player) {
                                BarterKings.controller.denyRecentTrade((Player) sender);
                            }

                        }

                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("accept")) {

                    if (sender.hasPermission("barterplus.trade.accept")) {
                        if (args.length == 2) {
                            String requester = args[1];
                            if (requester != null) {
                                if (requester != sender.getName()) {
                                    if (sender instanceof Player) {
                                        Player requested = (Player) sender;
                                        BarterKings.controller.acceptTrade(requested.getName(), requester);
                                    }
                                }
                            }
                        } else {
                            if (sender instanceof Player) {
                                BarterKings.controller.acceptRecentTrade((Player) sender);
                            }
                        }
                    }
                    return true;
                 } else if (args[0].equalsIgnoreCase("cancel")) {
                    if (sender.hasPermission("barterplus.trade.cancel")) {
                        if (args.length == 2) {
                            String requested = args[1];
                            if (requested != null) {
                                if (requested != sender.getName()) {
                                    if (sender instanceof Player) {
                                        Player requester = (Player) sender;
                                        BarterKings.controller.cancelTrade(requester.getName(), requested);
                                    }
                                }
                            }
                        } else {
                            if (sender instanceof Player) {
                                BarterKings.controller.cancelRecentTrade((Player) sender);
                            }
                        }
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("list")) {
                    if (sender.hasPermission("barterplus.trade.list")) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            List<TradeRequest> trades = BarterKings.controller.getAllPlayerTradeRequests(player);
                            if (trades.size() > 0) {
                                player.sendMessage(ChatColor.BLUE + "Trades:");
                                for (TradeRequest traderequest : trades) {
                                    if (traderequest.getTrade() != null)
                                        player.sendMessage(ChatColor.BLUE+ traderequest.toString());
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You have no trades");
                            }
                        }
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("autorun")) {
                    if (sender.hasPermission("barterplus.autorun")) {
                        if (args.length == 1) {
                            if (BarterKings.barterGame.inProgress()) {
                                sender.sendMessage(ChatColor.RED + "A game is already in progress, please restart server and run this at the beginning.");
                                return false;
                            }
                            BarterKings.startGamesService();
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /barter autorun");
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                        return false;
                    }
                }
            else {
                    sender.sendMessage(ChatColor.RED + "Invalid command");
                    return false;
                }
            }
        }

        return false;
    }

}
