package be4rjp.shootarian.weapon.gadget;

import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.WeaponManager;
import be4rjp.shootarian.weapon.WeaponStatusData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GadgetStatusData extends WeaponStatusData {

    private final GadgetWeapon gadgetWeapon;
    private final ShootarianPlayer shootarianPlayer;

    private int bullets = 5;
    private boolean isReloading = false;

    public GadgetStatusData(GadgetWeapon gadgetWeapon, ShootarianPlayer shootarianPlayer){
        super(gadgetWeapon, shootarianPlayer);
        this.gadgetWeapon = gadgetWeapon;
        this.shootarianPlayer = shootarianPlayer;
    }
    
    public GadgetWeapon getGadgetWeapon() {return gadgetWeapon;}
    
    @Override
    public void updateDisplayName(ShootarianPlayer shootarianPlayer){
        Player player = shootarianPlayer.getBukkitPlayer();
        if(player == null) return;

        for(int index = 0; index < 9; index++){
            ItemStack itemStack = player.getInventory().getItem(index);
            if(itemStack == null) continue;

            GadgetWeapon gadgetWeapon = WeaponManager.getGadgetWeaponByItem(itemStack);
            if(gadgetWeapon == null) continue;
            if(gadgetWeapon != this.gadgetWeapon) continue;

            player.getInventory().setItem(index, this.getItemStack(shootarianPlayer.getLang()));
            break;
        }
    }
    
    @Override
    public ItemStack getItemStackFlexible(Lang lang){
        ItemStack itemStack = gadgetWeapon.getItemStack(lang);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if(lore == null) lore = new ArrayList<>();
        lore.add("");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
}
