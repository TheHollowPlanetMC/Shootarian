package be4rjp.shootarian.ai;

import be4rjp.shootarian.match.ConquestMatch;
import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.player.costume.HeadGear;
import be4rjp.shootarian.util.RayTrace;
import be4rjp.shootarian.util.TaskHandler;
import be4rjp.shootarian.weapon.attachment.Attachment;
import be4rjp.shootarian.weapon.gadget.Gadget;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import be4rjp.shootarian.weapon.gun.GunWeapon;
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
    
    public static CompletableFuture<ShootarianPlayer> createAIPlayer(Location location, Match match, AIType aiType, AILevel aiLevel){
        CompletableFuture<ShootarianPlayer> completableFuture = new CompletableFuture<>();
        
        TaskHandler.supplySync(() -> {
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "AI");
            npc.spawn(location);
            npc.getNavigator().getLocalParameters().useNewPathfinder(true);
            //npc.getNavigator().getLocalParameters().range(10.0F);
            npc.getNavigator().getLocalParameters().speedModifier(1.0F);
            return npc;
        }).thenAccept(npc -> {
            Player npcPlayer = (Player) npc.getEntity();
            npcPlayer.setWalkSpeed(0.2F);
            npcPlayer.getInventory().clear();
    
            ShootarianPlayer shootarianPlayer = ShootarianPlayer.getShootarianPlayer(npcPlayer);
            shootarianPlayer.updateBukkitPlayer(npcPlayer, true);
            shootarianPlayer.setCitizensNPC(npc);
            shootarianPlayer.sendSkinRequest();
    
            for(int i = 0; i < 4; i++) {
                GunStatusData gunStatusData = new GunStatusData(GunWeapon.getGunWeaponBySaveNumber(i), shootarianPlayer);
                gunStatusData.addAttachment(Attachment.getAttachmentBySaveNumber(1));
                gunStatusData.addAttachment(Attachment.getAttachmentBySaveNumber(2));
                gunStatusData.addAttachment(Attachment.getAttachmentBySaveNumber(3));
                gunStatusData.addAttachment(Attachment.getAttachmentBySaveNumber(4));
                shootarianPlayer.getWeaponPossessionData().setGunStatusData(gunStatusData);
            }
            shootarianPlayer.getGadgetPossessionData().setGadget(Gadget.FLAG_GRENADE);
            shootarianPlayer.getGadgetPossessionData().setGadget(Gadget.GRAPPLE_GUN);
            shootarianPlayer.getGadgetPossessionData().setGadget(Gadget.RPG_7);
    
            shootarianPlayer.getWeaponClass().setMainWeapon(shootarianPlayer.getWeaponPossessionData().getGunStatusData(2, shootarianPlayer));
            shootarianPlayer.getWeaponClass().setSubWeapon(shootarianPlayer.getWeaponPossessionData().getGunStatusData(1, shootarianPlayer));
            
            shootarianPlayer.setHeadGear(HeadGear.getHeadGearBySaveNumber(0));
            
            shootarianPlayer.giveItems();
            
            TaskHandler.runSync(() -> {
                if(aiType == AIType.CONQUEST){
                    npc.getDefaultGoalController().addGoal(new ConquestRunAroundGoal(shootarianPlayer, (ConquestMatch) match, aiLevel), 1);
                    npc.getDefaultGoalController().addGoal(new ShootGunGoal<>(shootarianPlayer, (ConquestMatch) match, aiLevel), 2);
                }
    
                completableFuture.complete(shootarianPlayer);
            });
        });
        
        return completableFuture;
    }
    
    
    public static boolean peek(ShootarianPlayer ai, ShootarianPlayer target){
        boolean peek = true;
        Location aiLocation = ai.getEyeLocation();
        Location targetLocation = target.getEyeLocation();
        Vector direction = new Vector(targetLocation.getX() - aiLocation.getX(), targetLocation.getY() - aiLocation.getY(), targetLocation.getZ() - aiLocation.getZ());
    
        RayTrace rayTrace = new RayTrace(aiLocation.toVector(), direction.normalize());
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
