package be4rjp.shellcase.weapon.gadget;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.WeaponManager;
import be4rjp.shellcase.weapon.WeaponStatusData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GadgetStatusData extends WeaponStatusData {

    private final GadgetWeapon gadgetWeapon;
    private final ShellCasePlayer shellCasePlayer;

    private int bullets = 5;
    private boolean isReloading = false;

    public GadgetStatusData(GadgetWeapon gadgetWeapon, ShellCasePlayer shellCasePlayer){
        super(gadgetWeapon, shellCasePlayer);
        this.gadgetWeapon = gadgetWeapon;
        this.shellCasePlayer = shellCasePlayer;
    }
    
    public GadgetWeapon getGadgetWeapon() {return gadgetWeapon;}
    
    @Override
    public void updateDisplayName(ShellCasePlayer shellCasePlayer){
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;

        for(int index = 0; index < 9; index++){
            ItemStack itemStack = player.getInventory().getItem(index);
            if(itemStack == null) continue;

            GadgetWeapon gadgetWeapon = WeaponManager.getGadgetWeaponByItem(itemStack);
            if(gadgetWeapon == null) continue;
            if(gadgetWeapon != this.gadgetWeapon) continue;

            player.getInventory().setItem(index, this.getItemStack(shellCasePlayer.getLang()));
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
