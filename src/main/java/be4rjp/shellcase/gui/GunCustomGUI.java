package be4rjp.shellcase.gui;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shellcase.gui.pagination.CloseMenuPaginationButtonBuilder;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.TaskHandler;
import be4rjp.shellcase.weapon.attachment.Grip;
import be4rjp.shellcase.weapon.attachment.Sight;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class GunCustomGUI {
    
    public static void openGunCustomGUI(ShellCasePlayer shellCasePlayer, GunStatusData gunStatusData){
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
    
        Lang lang = shellCasePlayer.getLang();
        String menuName = MessageManager.getText(shellCasePlayer.getLang(), "gui-weapon-custom-attachment");
    
        SGMenu menu = ShellCase.getSpiGUI().create(String.format(menuName, gunStatusData.getGunWeapon().getDisplayName(lang)), 3);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> {
            WeaponSelectGUI.openWeaponSelectGUI(shellCasePlayer, "gui-select-weapon", true, gun -> {
                GunCustomGUI.openGunCustomGUI(shellCasePlayer, gun);
            });
        }));
    
        TaskHandler.runAsync(() -> {
            
            for(int index = 0; index < 27; index++){
                menu.setButton(index, new SGButton(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§r").build()));
            }
    
            Sight sight = gunStatusData.getSight();
            ItemStack sightItem;
            if(sight == null){
                sightItem = new ItemStack(Material.BARRIER);
                ItemMeta sightMeta = sightItem.getItemMeta();
                sightMeta.setDisplayName(MessageManager.getText(lang, "gui-weapon-custom-change-sight"));
                sightMeta.setLore(Arrays.asList("", String.format(MessageManager.getText(lang, "gui-weapon-custom-current-sight"), MessageManager.getText(lang, "nothing"))));
                sightItem.setItemMeta(sightMeta);
            }else{
                sightItem = sight.getItemStack(lang);
                ItemMeta sightMeta = sightItem.getItemMeta();
                sightMeta.setDisplayName(MessageManager.getText(lang, "gui-weapon-custom-change-sight"));
                sightMeta.setLore(Arrays.asList("", String.format(MessageManager.getText(lang, "gui-weapon-custom-current-sight"), sight.getDisplayName(lang))));
                sightItem.setItemMeta(sightMeta);
            }
            menu.setButton(10, new SGButton(sightItem).withListener(event -> {
                ChangeAttachmentGUI.openAttachmentGUI(shellCasePlayer, Sight.class, gunStatusData);
                shellCasePlayer.playGUIClickSound();
            }));
    
    
            Grip grip = gunStatusData.getGrip();
            ItemStack gripItem;
            if(grip == null){
                gripItem = new ItemStack(Material.BARRIER);
                ItemMeta gripMeta = gripItem.getItemMeta();
                gripMeta.setDisplayName(MessageManager.getText(lang, "gui-weapon-custom-change-grip"));
                gripMeta.setLore(Arrays.asList("", String.format(MessageManager.getText(lang, "gui-weapon-custom-current-grip"), MessageManager.getText(lang, "nothing"))));
                gripItem.setItemMeta(gripMeta);
            }else{
                gripItem = grip.getItemStack(lang);
                ItemMeta gripMeta = gripItem.getItemMeta();
                gripMeta.setDisplayName(MessageManager.getText(lang, "gui-weapon-custom-change-grip"));
                gripMeta.setLore(Arrays.asList("", String.format(MessageManager.getText(lang, "gui-weapon-custom-current-grip"), grip.getDisplayName(lang))));
                gripItem.setItemMeta(gripMeta);
            }
            menu.setButton(12, new SGButton(gripItem).withListener(event -> {
                ChangeAttachmentGUI.openAttachmentGUI(shellCasePlayer, Grip.class, gunStatusData);
                shellCasePlayer.playGUIClickSound();
            }));
            
        
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    }
    
}
