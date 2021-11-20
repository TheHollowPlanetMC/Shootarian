package be4rjp.shootarian.gui;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.TaskHandler;
import be4rjp.shootarian.weapon.gadget.GadgetStatusData;
import be4rjp.shootarian.weapon.gadget.GadgetWeapon;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ClassGUI {
    
    public static void openClassGUI(ShootarianPlayer shootarianPlayer){
        Player player = shootarianPlayer.getBukkitPlayer();
        if(player == null) return;
        
        Lang lang = shootarianPlayer.getLang();
        String menuName = MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-equip");
        
        SGMenu menu = Shootarian.getSpiGUI().create(menuName, 1);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> {
            MainMenuGUI.openMainMenuGUI(shootarianPlayer);
        }));
        
        TaskHandler.runAsync(() -> {
            
            ItemStack mainWeapon = shootarianPlayer.getWeaponClass().getMainWeapon().getItemStack(lang);
            ItemMeta mainWeaponMeta = mainWeapon.getItemMeta();
            mainWeaponMeta.setDisplayName(MessageManager.getText(lang, "gui-class-main-weapon-change"));
            mainWeaponMeta.setLore(Arrays.asList("", String.format(MessageManager.getText(lang, "gui-class-weapon-choice"),
                    shootarianPlayer.getWeaponClass().getMainWeapon().getGunWeapon().getDisplayName(lang))));
            mainWeapon.setItemMeta(mainWeaponMeta);
            menu.setButton(1, new SGButton(mainWeapon).withListener(event -> {
                WeaponSelectGUI.openWeaponSelectGUI(shootarianPlayer, "gui-select-main-weapon", true, false, gunStatusData -> {
                    shootarianPlayer.getWeaponClass().setMainWeapon(gunStatusData);
                    shootarianPlayer.giveItems();
                    shootarianPlayer.sendText("gui-class-main-weapon-changed", gunStatusData.getGunWeapon().getDisplayName(lang));
                    ClassGUI.openClassGUI(shootarianPlayer);
                }, () -> ClassGUI.openClassGUI(shootarianPlayer));
                shootarianPlayer.playGUIClickSound();
            }));
    
            ItemStack subWeapon = shootarianPlayer.getWeaponClass().getSubWeapon().getItemStack(lang);
            ItemMeta subWeaponMeta = subWeapon.getItemMeta();
            subWeaponMeta.setDisplayName(MessageManager.getText(lang, "gui-class-sub-weapon-change"));
            subWeaponMeta.setLore(Arrays.asList("", String.format(MessageManager.getText(lang, "gui-class-weapon-choice"),
                    shootarianPlayer.getWeaponClass().getSubWeapon().getGunWeapon().getDisplayName(lang))));
            subWeapon.setItemMeta(subWeaponMeta);
            menu.setButton(3, new SGButton(subWeapon).withListener(event -> {
                WeaponSelectGUI.openWeaponSelectGUI(shootarianPlayer, "gui-select-sub-weapon", false, false, gunStatusData -> {
                    shootarianPlayer.getWeaponClass().setSubWeapon(gunStatusData);
                    shootarianPlayer.giveItems();
                    shootarianPlayer.sendText("gui-class-sub-weapon-changed", gunStatusData.getGunWeapon().getDisplayName(lang));
                    ClassGUI.openClassGUI(shootarianPlayer);
                }, () -> ClassGUI.openClassGUI(shootarianPlayer));
                shootarianPlayer.playGUIClickSound();
            }));
    
            ItemStack mainGadget = shootarianPlayer.getWeaponClass().getMainGadget().getItemStack(lang);
            ItemMeta mainGadgetMeta = mainGadget.getItemMeta();
            mainGadgetMeta.setDisplayName(MessageManager.getText(lang, "gui-class-main-gadget-change"));
            mainGadgetMeta.setLore(Arrays.asList("", String.format(MessageManager.getText(lang, "gui-class-gadget-choice"),
                    shootarianPlayer.getWeaponClass().getMainGadget().getGadgetWeapon().getDisplayName(lang))));
            mainGadget.setItemMeta(mainGadgetMeta);
            menu.setButton(5, new SGButton(mainGadget).withListener(event -> {
                GadgetSelectGUI.openGadgetSelectGUI(shootarianPlayer, "gui-select-main-gadget", true, gadget -> {
                    GadgetWeapon gadgetWeapon = gadget.getInstance();
                    shootarianPlayer.getWeaponClass().setMainGadget(new GadgetStatusData(gadgetWeapon, shootarianPlayer));
                    shootarianPlayer.giveItems();
                    shootarianPlayer.sendText("gui-class-main-gadget-changed", gadget.getInstance().getDisplayName(lang));
                    ClassGUI.openClassGUI(shootarianPlayer);
                });
                shootarianPlayer.playGUIClickSound();
            }));
    
            ItemStack subGadget = shootarianPlayer.getWeaponClass().getSubGadget().getItemStack(lang);
            ItemMeta subGadgetMeta = subGadget.getItemMeta();
            subGadgetMeta.setDisplayName(MessageManager.getText(lang, "gui-class-sub-gadget-change"));
            subGadgetMeta.setLore(Arrays.asList("", String.format(MessageManager.getText(lang, "gui-class-gadget-choice"),
                    shootarianPlayer.getWeaponClass().getMainGadget().getGadgetWeapon().getDisplayName(lang))));
            subGadget.setItemMeta(subGadgetMeta);
            menu.setButton(7, new SGButton(subGadget).withListener(event -> {
                GadgetSelectGUI.openGadgetSelectGUI(shootarianPlayer, "gui-select-sub-gadget", false, gadget -> {
                    GadgetWeapon gadgetWeapon = gadget.getInstance();
                    shootarianPlayer.getWeaponClass().setSubGadget(new GadgetStatusData(gadgetWeapon, shootarianPlayer));
                    shootarianPlayer.giveItems();
                    shootarianPlayer.sendText("gui-class-sub-gadget-changed", gadget.getInstance().getDisplayName(lang));
                    ClassGUI.openClassGUI(shootarianPlayer);
                });
                shootarianPlayer.playGUIClickSound();
            }));
            
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    }
    
}
