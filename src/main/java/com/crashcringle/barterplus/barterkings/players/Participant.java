package com.crashcringle.barterplus.barterkings.players;

import com.crashcringle.barterplus.BarterPlus;
//import data.com.crashcringle.barterplus.PlayerConverter;
//import crashcringle.barterplus.data.ProfessionConverter;
//import data.com.crashcringle.barterplus.SessionFactoryMaker;

import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import lombok.Data;


import java.util.logging.Level;

import static com.crashcringle.barterplus.barterkings.players.BarterGame.fm;

//@Entity
//@NamedQueries(
//    @NamedQuery(name = "Participant.findByUUID", query = "select pd from PlayerData pd where pd.uuid=?1")
//)
@Data
public class Participant  {

   // @Id
    private String uuid;
   // @Column
    String name;

//    @Convert(converter = ProfessionConverter.class)
//    @Column
    Profession profession;

   // @Convert(converter = PlayerConverter.class)
   // @Column
    Player player;
    int score = 0;
    int starterScore = 0;
    Player clickedPlayer;
    boolean ready;

    ChatColor color = ChatColor.WHITE;

    public Participant(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId().toString();
        this.name = player.getName();
        this.clickedPlayer = player;
        BarterPlus.inst().getLogger().log(Level.INFO, "Participant created for " + player.getName());
    }

    public Participant(String uuid) {
        this.player = Bukkit.getPlayer(uuid);
        this.uuid = uuid;
        this.name = player.getName();
        this.clickedPlayer = player;
        BarterPlus.inst().getLogger().log(Level.INFO, "Participant created for " + player.getName());
    }

    public Player getPlayer() {
        if (player != null) {
            boolean isCitizensNPC = player.hasMetadata("NPC");
            if (isCitizensNPC)
                return player;
        }
        if (player == Bukkit.getPlayer(name)) {
            return player;
        } else {
            return Bukkit.getPlayer(name);
        }
    }

//    public static Participant getParticipantData(String uuid) {
//        Participant pd;
//        SessionFactory sessionFactory = SessionFactoryMaker.getFactory();
//
//        try (Session session = sessionFactory.openSession()) {
//            pd = session.createNamedQuery("Participant.findByUUID", Participant.class)
//                    .setParameter(1, uuid).getSingleResultOrNull();
//
//            if (pd == null) {
//                Transaction tx = session.beginTransaction();
//                pd = new Participant(uuid);
//                session.merge(pd);
//                tx.commit();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            pd = null;
//        }
//
//        return pd;
//    }

    public boolean isReady() {
        return ready;
    }

    public void readyUp() {
        BarterPlus.inst().getLogger().log(Level.INFO, "Player " + player.getName() + " is ready!");
        ready = true;
    }

    public void unready() {
        BarterPlus.inst().getLogger().log(Level.INFO, "Player " + player.getName() + " is not ready!");
        ready = false;
    }

    
    /**
     * This method takes a parameter of participant and returns the score 
     * of the participant based on the amount of tier 1, 2, and 3 items they have in their inventory.
     * @param
     * @return
     */
    public void calculateScore() {
        int score = 0;
        if (this.getProfession() == null) {
            BarterPlus.inst().getLogger().log(Level.WARNING, "Profession is null for " + getPlayer().getName());
            return;
        }
        getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Your Score Breakdown:");
        BarterPlus.inst().getLogger().log(Level.INFO, "Score Breakdown " + getPlayer().getName());
        for (ItemStack item : getPlayer().getInventory().getContents()) {
            if (item != null) {
                int addedScore = 0;
                ItemStack item2 = new ItemStack(item.getType());
                item2.setAmount(1);
                if (this.getProfession().getTier1Items().contains(item2)) {
                    addedScore += item.getAmount();
                } else if (this.getProfession().getTier2Items().contains(item2)) {
                    addedScore += 3 * item.getAmount();
                } else if (this.getProfession().getTier3Items().contains(item2)) {
                    addedScore += 10 * item.getAmount();
                }
                if (addedScore > 0) {

                     String message = String.format("%s%-25s x%-4d = %4d", ChatColor.GOLD, fm(item.getType()), item.getAmount(), addedScore);
                     getPlayer().sendMessage(message);
                     BarterPlus.inst().getLogger().log(Level.INFO, fm(item.getType()) + " x" + item.getAmount() + " = " + addedScore);
                     score += addedScore;
                 }
            }
        }

        setScore(score);
        BarterPlus.inst().getLogger().log(Level.INFO, "Score for " + getPlayer().getName() + " is " + score);
        BarterPlus.inst().getLogger().log(Level.INFO, "*********************************************");
    }

    public String getScoreBreakdown() {
        String breakdown = "Current Score Breakdown: ";
        int score = 0;
        for (ItemStack item : getPlayer().getInventory().getContents()) {
            if (item != null) {
                int addedScore = 0;
                ItemStack item2 = new ItemStack(item.getType());
                item2.setAmount(1);
                if (this.getProfession().getTier1Items().contains(item2)) {
                    addedScore += item.getAmount();
                } else if (this.getProfession().getTier2Items().contains(item2)) {
                    addedScore += 3 * item.getAmount();
                } else if (this.getProfession().getTier3Items().contains(item2)) {
                    addedScore += 10 * item.getAmount();
                }
                 String message = item.getAmount() + " " + fm(item.getType()) + " = " + addedScore + "pts";
                 breakdown += message + " , ";
                 score += addedScore;
            }
        }
        // Include the total score
        breakdown += "Total Score: " + score;
        return breakdown;
    }

     /**
     * This method takes a parameter of participant and returns the score 
     * of the participant based on the amount of tier 1, 2, and 3 items they have in their inventory.
     * @param
     * @return
     */
    public void calculateSilentScore() {
        int score = 0;
        if (this.getProfession() == null) {
            BarterPlus.inst().getLogger().log(Level.WARNING, "Profession is null for " + getPlayer().getName());
            return;
        }
        BarterPlus.inst().getLogger().log(Level.INFO, "Score Breakdown " + getPlayer().getName());
        for (ItemStack item : getPlayer().getInventory().getContents()) {
            if (item != null) {
                int addedScore = 0;
                ItemStack item2 = new ItemStack(item.getType());
                item2.setAmount(1);
                BarterPlus.inst().getLogger().log(Level.INFO, "Item: " + item.getType() + " x" + item.getAmount() + " = " + addedScore);
                if (this.getProfession().getTier1Items().contains(item2)) {
                    addedScore += item.getAmount();
                } else if (this.getProfession().getTier2Items().contains(item2)) {
                    addedScore += 3 * item.getAmount();
                } else if (this.getProfession().getTier3Items().contains(item2)) {
                    addedScore += 10 * item.getAmount();
                }
                 if (addedScore > 0) {

                     String message = item.getAmount() + " " + fm(item.getType()) + " = " + addedScore + "pts";
                     BarterPlus.inst().getLogger().log(Level.INFO, fm(item.getType()) + " x" + item.getAmount() + " = " + addedScore);
                     score += addedScore;
                 }
            }
        }

        setScore(score);
        BarterPlus.inst().getLogger().log(Level.INFO, "Score for " + getPlayer().getName() + " is " + score);
        BarterPlus.inst().getLogger().log(Level.INFO, "*********************************************");
    }

    public void calculateTrueSilentScore() {
        int score = 0;
        if (this.getProfession() == null) {
            BarterPlus.inst().getLogger().log(Level.WARNING, "Profession is null for " + getPlayer().getName());
            return;
        }
        for (ItemStack item : getPlayer().getInventory().getContents()) {
            if (item != null) {
                int addedScore = 0;
                ItemStack item2 = new ItemStack(item.getType());
                item2.setAmount(1);
                if (this.getProfession().getTier1Items().contains(item2)) {
                    addedScore += item.getAmount();
                } else if (this.getProfession().getTier2Items().contains(item2)) {
                    addedScore += 3 * item.getAmount();
                } else if (this.getProfession().getTier3Items().contains(item2)) {
                    addedScore += 10 * item.getAmount();
                }
                 if (addedScore > 0) {
                     score += addedScore;
                 }
            }
        }
        setScore(score);
        BarterPlus.inst().getLogger().log(Level.INFO, "Score for " + getPlayer().getName() + " is " + score);
    }

    public String getCalculatedScore2() {
        calculateSilentScore();
        if (score >= 120) {
            return ChatColor.GREEN + "" + score;
        }
        return "" +score;
    }

    public String getCalculatedScore() {
        calculateScore();
        if (score >= 120) {
            return ChatColor.GREEN + "" + score;
        }
        return "" +score;
    }

    // public int getScore() {
    //     return score;
    // }


}
