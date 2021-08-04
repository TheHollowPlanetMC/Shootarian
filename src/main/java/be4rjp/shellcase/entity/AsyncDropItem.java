package be4rjp.shellcase.entity;

import be4rjp.shellcase.match.Match;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.LocationUtil;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class AsyncDropItem implements ShellCaseEntity{

    protected final Match match;
    protected final EntityItem entityItem;
    protected Set<ShellCasePlayer> showPlayer = new HashSet<>();

    protected int tick = 0;
    protected boolean isDead = false;

    public AsyncDropItem(Match match, Location location){
        this.match = match;

        this.entityItem = new EntityItem(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());
    }

    public Location getLocation(){return new Location(entityItem.getWorld().getWorld(), entityItem.locX(), entityItem.locY(), entityItem.locZ());}

    @Override
    public void tick() {

        //対象のプレイヤーにスポーンパケットやデスポーンパケットを送信
        if(tick % 20 == 0 && tick != 0){
            Set<ShellCasePlayer> players = this.getInRangePlayer();
            for(ShellCasePlayer shellCasePlayer : match.getPlayers()){
                if(!showPlayer.contains(shellCasePlayer) && players.contains(shellCasePlayer)){
                    this.sendSpawnPacket(shellCasePlayer);
                }

                if(showPlayer.contains(shellCasePlayer) && !players.contains(shellCasePlayer)){
                    this.sendDestroyPacket(shellCasePlayer);
                }
            }
        }

        //アイテムの移動通知パケット
        showPlayer.forEach(this::sendVelocityPacket);
        if(tick % 15 == 0 && tick != 0){
            showPlayer.forEach(this::sendTeleportPacket);
        }

        //ブロックの当たり判定等の実行
        try {
            entityItem.tick();
        }catch (Exception e){
            this.remove();
        }
    }

    @Override
    public int getEntityID() {
        return entityItem.getId();
    }

    @Override
    public void spawn() {
        showPlayer = this.getInRangePlayer();
        showPlayer.forEach(this::sendSpawnPacket);

        match.getShellCaseEntities().add(this);
    }

    @Override
    public void remove() {
        this.isDead = true;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    protected Set<ShellCasePlayer> getInRangePlayer(){
        Set<ShellCasePlayer> players = new HashSet<>();
        for(ShellCasePlayer shellCasePlayer : match.getPlayers()){
            Player player = shellCasePlayer.getBukkitPlayer();
            if(player == null) continue;

            if(LocationUtil.distanceSquaredSafeDifferentWorld(shellCasePlayer.getLocation(), this.getLocation()) > ENTITY_DRAW_DISTANCE_SQUARE) continue;

            players.add(shellCasePlayer);
        }
        return players;
    }


    public void setLocation(Location location){
        this.entityItem.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), getLocation().getPitch());
    }

    public void setVelocity(Vector velocity){
        Vec3D vec3D = new Vec3D(velocity.getX(), velocity.getY(), velocity.getZ());
        this.entityItem.setMot(vec3D);
    }

    public void sendSpawnPacket(ShellCasePlayer shellCasePlayer){
        PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity(entityItem);
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(entityItem.getId(), entityItem.getDataWatcher(), true);
        shellCasePlayer.sendPacket(spawnEntity);
        shellCasePlayer.sendPacket(metadata);
    }

    public void sendTeleportPacket(ShellCasePlayer shellCasePlayer){
        PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(entityItem);
        shellCasePlayer.sendPacket(teleport);
    }

    public void sendVelocityPacket(ShellCasePlayer shellCasePlayer){
        PacketPlayOutEntityVelocity velocity = new PacketPlayOutEntityVelocity(entityItem);
        shellCasePlayer.sendPacket(velocity);
    }

    public void sendDestroyPacket(ShellCasePlayer shellCasePlayer){
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entityItem.getId());
        shellCasePlayer.sendPacket(destroy);
    }
}
