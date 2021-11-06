package be4rjp.shellcase.ai;

import be4rjp.shellcase.match.ConquestMatch;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.player.costume.HeadGear;
import be4rjp.shellcase.util.RayTrace;
import be4rjp.shellcase.util.TaskHandler;
import be4rjp.shellcase.weapon.attachment.Attachment;
import be4rjp.shellcase.weapon.gadget.Gadget;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.gun.GunWeapon;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AIManager {
    
    public static CompletableFuture<ShellCasePlayer> createAIPlayer(Location location, Match match, AIType aiType, AILevel aiLevel){
        CompletableFuture<ShellCasePlayer> completableFuture = new CompletableFuture<>();
        
        TaskHandler.supplySync(() -> {
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "AI");
            npc.spawn(location);
            npc.getNavigator().getLocalParameters().useNewPathfinder(true);
            //npc.getNavigator().getLocalParameters().range(10.0F);
            npc.getNavigator().getLocalParameters().speedModifier(1.4F);
            return npc;
        }).thenAccept(npc -> {
            Player npcPlayer = (Player) npc.getEntity();
            npcPlayer.setWalkSpeed(0.2F);
            npcPlayer.getInventory().clear();
    
            ShellCasePlayer shellCasePlayer = ShellCasePlayer.getShellCasePlayer(npcPlayer);
            shellCasePlayer.updateBukkitPlayer(npcPlayer, true);
            shellCasePlayer.setCitizensNPC(npc);
            shellCasePlayer.sendSkinRequest();
    
            for(int i = 0; i < 4; i++) {
                GunStatusData gunStatusData = new GunStatusData(GunWeapon.getGunWeaponBySaveNumber(i), shellCasePlayer);
                gunStatusData.addAttachment(Attachment.getAttachmentBySaveNumber(1));
                gunStatusData.addAttachment(Attachment.getAttachmentBySaveNumber(2));
                gunStatusData.addAttachment(Attachment.getAttachmentBySaveNumber(3));
                gunStatusData.addAttachment(Attachment.getAttachmentBySaveNumber(4));
                shellCasePlayer.getWeaponPossessionData().setGunStatusData(gunStatusData);
            }
            shellCasePlayer.getGadgetPossessionData().setGadget(Gadget.FLAG_GRENADE);
            shellCasePlayer.getGadgetPossessionData().setGadget(Gadget.GRAPPLE_GUN);
            shellCasePlayer.getGadgetPossessionData().setGadget(Gadget.RPG_7);
    
            shellCasePlayer.getWeaponClass().setMainWeapon(shellCasePlayer.getWeaponPossessionData().getGunStatusData(2, shellCasePlayer));
            shellCasePlayer.getWeaponClass().setSubWeapon(shellCasePlayer.getWeaponPossessionData().getGunStatusData(1, shellCasePlayer));
            
            shellCasePlayer.setHeadGear(HeadGear.getHeadGearBySaveNumber(0));
            
            shellCasePlayer.giveItems();
            
            TaskHandler.runSync(() -> {
                if(aiType == AIType.CONQUEST){
                    npc.getDefaultGoalController().addGoal(new ConquestRunAroundGoal(shellCasePlayer, (ConquestMatch) match, aiLevel), 1);
                    npc.getDefaultGoalController().addGoal(new ShootGunGoal<>(shellCasePlayer, (ConquestMatch) match, aiLevel), 2);
                }
    
                completableFuture.complete(shellCasePlayer);
            });
        });
        
        return completableFuture;
    }
    
    
    public static boolean peek(ShellCasePlayer ai, ShellCasePlayer target){
        boolean peek = true;
        Location aiLocation = ai.getEyeLocation();
        Location targetLocation = target.getEyeLocation();
        Vector direction = new Vector(targetLocation.getX() - aiLocation.getX(), targetLocation.getY() - aiLocation.getY(), targetLocation.getZ() - aiLocation.getZ());
    
        RayTrace rayTrace = new RayTrace(aiLocation.toVector(), direction);
        if(targetLocation.getWorld() == aiLocation.getWorld()) {
            List<Vector> positions = rayTrace.traverse(targetLocation.distance(aiLocation), 1);
            for (Vector position : positions){
                if(position.toLocation(aiLocation.getWorld()).getBlock().getType() != Material.AIR){
                    peek = false;
                    break;
                }
            }
        }
        
        return peek;
    }
    
}
