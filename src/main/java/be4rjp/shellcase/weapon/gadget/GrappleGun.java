package be4rjp.shellcase.weapon.gadget;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.entity.ShellCaseEntity;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.ShellCaseSound;
import be4rjp.shellcase.util.TaskHandler;
import be4rjp.shellcase.util.particle.NormalParticle;
import be4rjp.shellcase.weapon.gadget.runnable.GrappleRunnable;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Set;

public class GrappleGun extends GadgetWeapon{
    
    private static final double REACH = 20.0;
    
    
    public GrappleGun(Gadget gadget) {
        super(gadget);
    
        super.material = Material.GOLDEN_HOE;
        for(Lang lang : Lang.values()){
            this.displayName.put(lang, MessageManager.getText(lang, "gadget-flag-grenade"));
        }
    }
    
    @Override
    public synchronized void onRightClick(ShellCasePlayer shellCasePlayer) {
        GadgetStatusData gadgetStatusData = (GadgetStatusData) shellCasePlayer.getWeaponStatusData(this);
        if(gadgetStatusData == null) return;
        if(gadgetStatusData.isCoolTime()) return;
    
        ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
        if(shellCaseTeam == null) return;
    
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
        
        if(!gadgetStatusData.consumeBullets(1)){
            return;
        }
        gadgetStatusData.updateDisplayName(shellCasePlayer);
    
        Vector direction = shellCasePlayer.getEyeLocation().getDirection();
        direction.multiply(REACH);
        shellCasePlayer.spawnParticle(new NormalParticle(Particle.CRIT, 0, direction.getX(), direction.getY(), direction.getZ(), 1), shellCasePlayer.getEyeLocation());
    
        RayTraceResult rayTraceResult = player.rayTraceBlocks(REACH);
        if(rayTraceResult == null) return;
        if(rayTraceResult.getHitBlock() == null) return;
        gadgetStatusData.setCoolTime(200);
    
        Block block = rayTraceResult.getHitBlock();
        
        TaskHandler.runAsync(() -> {
            EntitySilverfish silverfish = new EntitySilverfish(EntityTypes.SILVERFISH, ((CraftWorld) block.getWorld()).getHandle());
            silverfish.setPosition(block.getX(), block.getY() - 1.0, block.getZ());
            silverfish.setInvisible(true);
            PacketPlayOutSpawnEntityLiving spawnEntityLiving = new PacketPlayOutSpawnEntityLiving(silverfish);
            PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(silverfish.getId(), silverfish.getDataWatcher(), true);
            PacketPlayOutAttachEntity attachEntity = new PacketPlayOutAttachEntity(silverfish, ((CraftPlayer) player).getHandle());
    
            Set<ShellCasePlayer> matchPlayers = shellCasePlayer.getNearPlayer(ShellCaseEntity.ENTITY_DRAW_DISTANCE);
            for(ShellCasePlayer matchPlayer : matchPlayers) {
                matchPlayer.playSound(new ShellCaseSound(Sound.ITEM_CROSSBOW_HIT, 1F, 1F), block.getLocation());
                matchPlayer.sendPacket(spawnEntityLiving);
                matchPlayer.sendPacket(metadata);
                matchPlayer.sendPacket(attachEntity);
            }
            
            new GrappleRunnable(shellCasePlayer, player, block.getLocation(), silverfish, matchPlayers, gadgetStatusData).runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 1);
        });
    }
    
    @Override
    public synchronized void onLeftClick(ShellCasePlayer shellCasePlayer) {
    
    }
}
