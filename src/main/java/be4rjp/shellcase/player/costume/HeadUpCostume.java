package be4rjp.shellcase.player.costume;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.entity.ShellCaseEntity;
import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.LocationUtil;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class HeadUpCostume extends BukkitRunnable {

    private static Field a;
    private static Field b;

    static {
        try {
            a = PacketPlayOutMount.class.getDeclaredField("a");
            b = PacketPlayOutMount.class.getDeclaredField("b");
            a.setAccessible(true);
            b.setAccessible(true);
        }catch (Exception e){e.printStackTrace();}
    }

    private final ShellCasePlayer ShellCasePlayer;
    private final ShellCaseTeam ShellCaseTeam;
    private final Match match;
    private final ItemStack itemStack;
    private final Set<ShellCasePlayer> showPlayer;
    private final EntityArmorStand armorStand;

    public HeadUpCostume(ShellCasePlayer ShellCasePlayer, ShellCaseTeam ShellCaseTeam, ItemStack itemStack){
        this.ShellCasePlayer = ShellCasePlayer;
        this.ShellCaseTeam = ShellCaseTeam;
        this.match = ShellCaseTeam.getMatch();
        this.itemStack = itemStack;
        this.showPlayer = new HashSet<>();

        Location loc = ShellCasePlayer.getLocation();
        this.armorStand = new EntityArmorStand(((CraftWorld)loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ());
        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.setNoGravity(true);
    }


    @Override
    public void run() {
        for(ShellCasePlayer matchPlayer : match.getPlayers()){
            if(LocationUtil.distanceSquaredSafeDifferentWorld(ShellCasePlayer.getLocation(), matchPlayer.getLocation()) < ShellCaseEntity.ENTITY_DRAW_DISTANCE_SQUARE){
                if(!showPlayer.contains(matchPlayer)){
                    this.sendSpawnPacket(matchPlayer);
                    showPlayer.add(matchPlayer);
                }
            }else{
                if(showPlayer.contains(matchPlayer)){
                    this.sendDestroyPacket(matchPlayer);
                    showPlayer.remove(matchPlayer);
                }
            }
        }
    }


    public void sendSpawnPacket(ShellCasePlayer target){
        PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(armorStand);
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
        PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(armorStand.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(itemStack));
        PacketPlayOutMount mount = new PacketPlayOutMount();
        try {
            a.set(mount, ShellCasePlayer.getEntityID());
            b.set(mount, new int[]{armorStand.getId()});
        }catch (Exception e){e.printStackTrace();}

        target.sendPacket(spawn);
        target.sendPacket(metadata);
        target.sendPacket(equipment);
        target.sendPacket(mount);
    }

    public void sendDestroyPacket(ShellCasePlayer target){
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(armorStand.getId());
        target.sendPacket(destroy);
    }


    public void spawn(){
        try {
            this.runTaskTimerAsynchronously(ShellCase.getPlugin(), 0, 60);
        }catch (Exception e){/**/}
    }


    public void remove(){
        try{
            this.cancel();
        }catch (Exception e){/**/}
        this.showPlayer.forEach(this::sendDestroyPacket);
    }
}
