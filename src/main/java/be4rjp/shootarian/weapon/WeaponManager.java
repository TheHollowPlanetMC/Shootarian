package be4rjp.shootarian.weapon;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.attachment.Attachment;
import be4rjp.shootarian.weapon.attachment.Sight;
import be4rjp.shootarian.weapon.gadget.GadgetWeapon;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import be4rjp.shootarian.weapon.gun.GunWeapon;
import be4rjp.shootarian.weapon.recoil.Recoil;
import net.minecraft.server.v1_15_R1.PacketPlayOutSetSlot;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Objects;

public class WeaponManager {
    
    public static ShootarianWeapon getShootarianWeaponByItem(ItemStack itemStack){
        if(itemStack == null) return null;
        net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if(nmsItemStack.getTag() == null) return null;
    
        String id = nmsItemStack.getTag().getString("swid");
        if(id == null) return null;
        
        return ShootarianWeapon.getShootarianWeapon(id);
    }
    
    public static GunWeapon getGunWeaponByItem(ItemStack itemStack){
        if(itemStack == null) return null;
        net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if(nmsItemStack.getTag() == null) return null;
    
        String id = nmsItemStack.getTag().getString("swid");
        if(id == null) return null;
        
        return GunWeapon.getGunWeapon(id);
    }

    public static GadgetWeapon getGadgetWeaponByItem(ItemStack itemStack){
        if(itemStack == null) return null;
        net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if(nmsItemStack.getTag() == null) return null;

        String id = nmsItemStack.getTag().getString("swid");
        if(id == null) return null;

        return GadgetWeapon.getGadgetWeapon(id);
    }
    
    
    public static void loadAllWeapon(){
        ShootarianWeapon.initialize();
        GunWeapon.initialize();
        Shootarian.getPlugin().getLogger().info("Loading weapons...");
        File dir = new File("plugins/Shootarian/weapon");
    
        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if(files.length == 0){
            Shootarian.getPlugin().saveResource("weapon/scar-h.yml", false);
            files = dir.listFiles();
        }
    
        if(files != null) {
            for (File file : files) {
                Shootarian.getPlugin().getLogger().info(file.getName());
                String id = file.getName().replace(".yml", "");
                try {
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                    GunWeapon.GunWeaponType type = GunWeapon.GunWeaponType.valueOf(yml.getString("type"));
                    GunWeapon gunWeapon = type.createGunWeaponInstance(id);
                    gunWeapon.loadData(yml);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }
    
    
    public static void loadAllAttachment() {
        Shootarian.getPlugin().getLogger().info("Loading attachments...");
        File dir = new File("plugins/Shootarian/attachment");
    
        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files.length == 0) {
            Shootarian.getPlugin().saveResource("attachment/rds.yml", false);
            files = dir.listFiles();
        }
    
        if (files != null) {
            for (File file : files) {
                Shootarian.getPlugin().getLogger().info(file.getName());
                String id = file.getName().replace(".yml", "");
                try {
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                    Attachment.AttachmentType type = Attachment.AttachmentType.valueOf(yml.getString("type"));
                    Attachment attachment = type.createAttachmentInstance(id);
                    attachment.loadData(yml);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }


    public static void loadAllRecoils() {
        Shootarian.getPlugin().getLogger().info("Loading recoils...");
        File dir = new File("plugins/Shootarian/recoil");

        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files.length == 0) {
            Shootarian.getPlugin().saveResource("recoil/scar-h-recoil.yml", false);
            files = dir.listFiles();
        }

        if (files != null) {
            for (File file : files) {
                Shootarian.getPlugin().getLogger().info(file.getName());
                String id = file.getName().replace(".yml", "");
                try {
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                    Recoil recoil = new Recoil(id);
                    recoil.loadData(yml);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }


    public static ItemStack writeNBTTag(ShootarianWeapon ShootarianWeapon, ItemStack itemStack){
        net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Objects.requireNonNull(nmsItemStack.getTag()).setString("swid", ShootarianWeapon.getID());
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }
    
    
    public static void switchADS(ShootarianPlayer shootarianPlayer, GunStatusData gunStatusData, boolean isADS){
        if(isADS){
            if(gunStatusData != null) {
                shootarianPlayer.setWalkSpeed(gunStatusData.getGunWeapon().getADSWalkSpeed());
    
                Sight sight = gunStatusData.getSight();
                if(sight != null) {
                    shootarianPlayer.setFOV(sight.getFOV());
                    
                    Player player = shootarianPlayer.getBukkitPlayer();
                    if(player != null) {
                        ItemStack itemStack = sight.isHasSightItem() ? sight.getSightItemStack(shootarianPlayer.getLang()) : sight.getItemStack(shootarianPlayer.getLang());
                        PacketPlayOutSetSlot slot = new PacketPlayOutSetSlot(-2, player.getInventory().getHeldItemSlot(), CraftItemStack.asNMSCopy(itemStack));
                        shootarianPlayer.setSlotPacket(slot);
                        shootarianPlayer.sendPacket(slot);
                    }
                }
            }
        }else{
            shootarianPlayer.setWalkSpeed(0.2F);
            shootarianPlayer.setFOV(0.1F);
            shootarianPlayer.setSlotPacket(null);
            if(gunStatusData != null) gunStatusData.updateDisplayName(shootarianPlayer);
            Player player = shootarianPlayer.getBukkitPlayer();
            if(player != null) {
                player.updateInventory();
            }
            //shootarianPlayer.resetTitle();
        }
    }
    
}
