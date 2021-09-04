package be4rjp.shellcase.weapon;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.attachment.Attachment;
import be4rjp.shellcase.weapon.attachment.Sight;
import be4rjp.shellcase.weapon.gadget.GadgetWeapon;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.gun.GunWeapon;
import be4rjp.shellcase.weapon.recoil.Recoil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Objects;

public class WeaponManager {
    
    public static ShellCaseWeapon getShellCaseWeaponByItem(ItemStack itemStack){
        if(itemStack == null) return null;
        net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if(nmsItemStack.getTag() == null) return null;
    
        String id = nmsItemStack.getTag().getString("swid");
        if(id == null) return null;
        
        return ShellCaseWeapon.getShellCaseWeapon(id);
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
        ShellCaseWeapon.initialize();
        GunWeapon.initialize();
        ShellCase.getPlugin().getLogger().info("Loading weapons...");
        File dir = new File("plugins/ShellCase/weapon");
    
        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if(files.length == 0){
            ShellCase.getPlugin().saveResource("weapon/scar-h.yml", false);
            files = dir.listFiles();
        }
    
        if(files != null) {
            for (File file : files) {
                ShellCase.getPlugin().getLogger().info(file.getName());
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
        ShellCase.getPlugin().getLogger().info("Loading attachments...");
        File dir = new File("plugins/ShellCase/attachment");
    
        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files.length == 0) {
            ShellCase.getPlugin().saveResource("attachment/rds.yml", false);
            files = dir.listFiles();
        }
    
        if (files != null) {
            for (File file : files) {
                ShellCase.getPlugin().getLogger().info(file.getName());
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
        ShellCase.getPlugin().getLogger().info("Loading recoils...");
        File dir = new File("plugins/ShellCase/recoil");

        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files.length == 0) {
            ShellCase.getPlugin().saveResource("recoil/scar-h-recoil.yml", false);
            files = dir.listFiles();
        }

        if (files != null) {
            for (File file : files) {
                ShellCase.getPlugin().getLogger().info(file.getName());
                String id = file.getName().replace(".yml", "");
                try {
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                    Recoil recoil = new Recoil(id);
                    recoil.loadData(yml);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }


    public static ItemStack writeNBTTag(ShellCaseWeapon ShellCaseWeapon, ItemStack itemStack){
        net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Objects.requireNonNull(nmsItemStack.getTag()).setString("swid", ShellCaseWeapon.getID());
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }
    
    
    public static void switchADS(ShellCasePlayer shellCasePlayer, GunStatusData gunStatusData, boolean isADS){
        if(isADS){
            if(gunStatusData != null) {
                shellCasePlayer.setWalkSpeed(gunStatusData.getGunWeapon().getADSWalkSpeed());
    
                Sight sight = gunStatusData.getSight();
                if(sight != null) {
                    shellCasePlayer.sendIconTitle(sight.getUnicode(), "", 0, Integer.MAX_VALUE, 0);
                    shellCasePlayer.setFOV(sight.getFOV());
                }
            }
        }else{
            shellCasePlayer.setWalkSpeed(0.2F);
            shellCasePlayer.setFOV(0.1F);
            shellCasePlayer.resetTitle();
        }
    }
    
}
