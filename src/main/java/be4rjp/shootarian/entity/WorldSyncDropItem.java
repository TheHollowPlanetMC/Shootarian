package be4rjp.shootarian.entity;

import be4rjp.shootarian.match.Match;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.LocationUtil;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class WorldSyncDropItem implements ShootarianEntity {

    protected final Match match;
    protected final EntityItem entityItem;
    protected Set<ShootarianPlayer> showPlayer = new HashSet<>();

    protected int tick = 0;
    protected boolean isDead = false;

    public WorldSyncDropItem(Match match, Location location, ItemStack itemStack){
        this.match = match;

        this.entityItem = new EntityItem(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());
        this.setItemStack(itemStack);
    }

    public Location getLocation(){return new Location(entityItem.getWorld().getWorld(), entityItem.locX(), entityItem.locY(), entityItem.locZ());}

    @Override
    public void tick() {

        //対象のプレイヤーにスポーンパケットやデスポーンパケットを送信
        if(tick % 20 == 0 && tick != 0){
            Set<ShootarianPlayer> players = this.getInRangePlayer();
            for(ShootarianPlayer shootarianPlayer : match.getPlayers()){
                if(!showPlayer.contains(shootarianPlayer) && players.contains(shootarianPlayer)){
                    this.sendSpawnPacket(shootarianPlayer);
                }

                if(showPlayer.contains(shootarianPlayer) && !players.contains(shootarianPlayer)){
                    this.sendDestroyPacket(shootarianPlayer);
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
        
        tick++;
    }

    @Override
    public int getEntityID() {
        return entityItem.getId();
    }

    @Override
    public void spawn() {
        showPlayer = this.getInRangePlayer();
        showPlayer.forEach(this::sendSpawnPacket);

        match.getShootarianEntities().add(this);
    }

    @Override
    public void remove() {
        this.isDead = true;
        
        this.showPlayer.forEach(this::sendDestroyPacket);
        this.match.getShootarianEntities().remove(this);
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    protected Set<ShootarianPlayer> getInRangePlayer(){
        Set<ShootarianPlayer> players = new HashSet<>();
        for(ShootarianPlayer shootarianPlayer : match.getPlayers()){
            Player player = shootarianPlayer.getBukkitPlayer();
            if(player == null) continue;

            if(LocationUtil.distanceSquaredSafeDifferentWorld(shootarianPlayer.getLocation(), this.getLocation()) > ENTITY_DRAW_DISTANCE_SQUARE) continue;

            players.add(shootarianPlayer);
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
    
    public void setItemStack(ItemStack itemStack){
        this.entityItem.setItemStack(CraftItemStack.asNMSCopy(itemStack));
    }

    public void sendSpawnPacket(ShootarianPlayer shootarianPlayer){
        PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity(entityItem);
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(entityItem.getId(), entityItem.getDataWatcher(), true);
        shootarianPlayer.sendPacket(spawnEntity);
        shootarianPlayer.sendPacket(metadata);
    }

    public void sendTeleportPacket(ShootarianPlayer shootarianPlayer){
        PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(entityItem);
        shootarianPlayer.sendPacket(teleport);
    }

    public void sendVelocityPacket(ShootarianPlayer shootarianPlayer){
        PacketPlayOutEntityVelocity velocity = new PacketPlayOutEntityVelocity(entityItem);
        shootarianPlayer.sendPacket(velocity);
    }

    public void sendDestroyPacket(ShootarianPlayer shootarianPlayer){
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entityItem.getId());
        shootarianPlayer.sendPacket(destroy);
    }
}
