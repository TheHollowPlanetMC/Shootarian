package be4rjp.shellcase.gui;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.TaskHandler;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ClassGUI {
    
    public static void openClassGUI(ShellCasePlayer shellCasePlayer){
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
        
        Lang lang = shellCasePlayer.getLang();
        String menuName = MessageManager.getText(shellCasePlayer.getLang(), "gui-main-menu-equip");
        
        SGMenu menu = ShellCase.getSpiGUI().create(menuName, 1);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> {
            MainMenuGUI.openMainMenuGUI(shellCasePlayer);
        }));
        
        TaskHandler.runAsync(() -> {
            
            ItemStack mainWeapon = shellCasePlayer.getWeaponClass().getMainWeapon().getItemStack(lang);
            ItemMeta mainWeaponMeta = mainWeapon.getItemMeta();
            mainWeaponMeta.setDisplayName(MessageManager.getText(lang, "gui-class-main-weapon-change"));
            mainWeaponMeta.setLore(Arrays.asList("", String.format(MessageManager.getText(lang, "gui-class-weapon-choice"),
                    shellCasePlayer.getWeaponClass().getMainWeapon().getGunWeapon().getDisplayName(lang))));
            mainWeapon.setItemMeta(mainWeaponMeta);
            menu.setButton(1, new SGButton(mainWeapon).withListener(event -> {
                WeaponSelectGUI.openWeaponSelectGUI(shellCasePlayer, gunStatusData -> {
                    shellCasePlayer.getWeaponClass().setMainWeapon(gunStatusData);
                    shellCasePlayer.giveItems();
                    shellCasePlayer.sendText("gui-class-main-weapon-changed", gunStatusData.getGunWeapon().getDisplayName(lang));
                    ClassGUI.openClassGUI(shellCasePlayer);
                });
                shellCasePlayer.playGUIClickSound();
            }));
    
            ItemStack subWeapon = shellCasePlayer.getWeaponClass().getSubWeapon().getItemStack(lang);
            ItemMeta subWeaponMeta = subWeapon.getItemMeta();
            subWeaponMeta.setDisplayName(MessageManager.getText(lang, "gui-class-sub-weapon-change"));
            subWeaponMeta.setLore(Arrays.asList("", String.format(MessageManager.getText(lang, "gui-class-weapon-choice"),
                    shellCasePlayer.getWeaponClass().getSubWeapon().getGunWeapon().getDisplayName(lang))));
            subWeapon.setItemMeta(subWeaponMeta);
            menu.setButton(3, new SGButton(subWeapon).withListener(event -> {
                WeaponSelectGUI.openWeaponSelectGUI(shellCasePlayer, gunStatusData -> {
                    shellCasePlayer.getWeaponClass().setSubWeapon(gunStatusData);
                    shellCasePlayer.giveItems();
                    shellCasePlayer.sendText("gui-class-sub-weapon-changed", gunStatusData.getGunWeapon().getDisplayName(lang));
                    ClassGUI.openClassGUI(shellCasePlayer);
                });
                shellCasePlayer.playGUIClickSound();
            }));
    
            ItemStack mainGadget = shellCasePlayer.getWeaponClass().getMainGadget().getItemStack(lang);
            ItemMeta mainGadgetMeta = mainGadget.getItemMeta();
            mainGadgetMeta.setDisplayName(MessageManager.getText(lang, "gui-class-main-gadget-change"));
            mainGadgetMeta.setLore(Arrays.asList("", String.format(MessageManager.getText(lang, "gui-class-gadget-choice"),
                    shellCasePlayer.getWeaponClass().getMainGadget().getGadgetWeapon().getDisplayName(lang))));
            mainGadget.setItemMeta(mainGadgetMeta);
            menu.setButton(5, new SGButton(mainGadget).withListener(event -> {
                shellCasePlayer.playGUIClickSound();
            }));
    
            ItemStack subGadget = shellCasePlayer.getWeaponClass().getSubGadget().getItemStack(lang);
            ItemMeta subGadgetMeta = subGadget.getItemMeta();
            subGadgetMeta.setDisplayName(MessageManager.getText(lang, "gui-class-sub-gadget-change"));
            subGadgetMeta.setLore(Arrays.asList("", String.format(MessageManager.getText(lang, "gui-class-gadget-choice"),
                    shellCasePlayer.getWeaponClass().getMainGadget().getGadgetWeapon().getDisplayName(lang))));
            subGadget.setItemMeta(subGadgetMeta);
            menu.setButton(7, new SGButton(subGadget).withListener(event -> {
                shellCasePlayer.playGUIClickSound();
            }));
            
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    }
    
}
