package be4rjp.shellcase.language;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public enum Lang {
    ja_JP(0, "§f§n日本語", "http://textures.minecraft.net/texture/8043ae9bbfa8b8bbb5c964bbce45fbe79a3ad742be07b56607c68c8e11164"),
    en_US(1, "§f§nEnglish", "http://textures.minecraft.net/texture/fcbc32cb24d57fcdc031e851235da2daad3e1914b87043bd012633e6f32c7");
    
    //SQLに保存する時の識別番号
    private final int saveNumber;
    //表示名
    private final String displayName;
    //国旗のアイテムヘッド
    private final ItemStack flagHead;
    
    Lang(int saveNumber, String displayName, String skullURL){
        this.saveNumber = saveNumber;
        this.displayName = displayName;
    
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setDisplayName(displayName);
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", skullURL).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (Exception e) {e.printStackTrace();}
        head.setItemMeta(headMeta);
        
        this.flagHead = head;
    }
    
    public int getSaveNumber() {return saveNumber;}
    
    public String getDisplayName() {return displayName;}
    
    public ItemStack getFlagHead() {return flagHead;}
    
    public static Lang getLangByID(int id){
        for(Lang lang : Lang.values()){
            if(lang.getSaveNumber() == id) return lang;
        }
        return null;
    }
}
