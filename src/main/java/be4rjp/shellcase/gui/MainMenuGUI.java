package be4rjp.shellcase.gui;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shellcase.gui.pagination.CloseMenuPaginationButtonBuilder;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.TaskHandler;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MainMenuGUI {
    
    public static void openMainMenuGUI(ShellCasePlayer shellCasePlayer){
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
    
        Lang lang = shellCasePlayer.getLang();
        String menuName = MessageManager.getText(shellCasePlayer.getLang(), "gui-main-menu");
    
        SGMenu menu = ShellCase.getSpiGUI().create(menuName, 5);
        menu.setPaginationButtonBuilder(CloseMenuPaginationButtonBuilder.getPaginationButtonBuilder(lang));
    
        TaskHandler.runAsync(() -> {
    
            for(int index = 0; index < 45; index++){
                menu.setButton(index, new SGButton(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§r").build()));
            }
        
            menu.setButton(10, new SGButton(new ItemBuilder(Material.LIME_STAINED_GLASS)
                    .name(MessageManager.getText(shellCasePlayer.getLang(), "gui-main-menu-join"))
                    .lore(MessageManager.getText(shellCasePlayer.getLang(), "gui-main-menu-join-des")).build()).withListener(event -> {
            
            }));
    
            menu.setButton(12, new SGButton(new ItemBuilder(Material.IRON_HOE)
                    .name(MessageManager.getText(shellCasePlayer.getLang(), "gui-main-menu-equip"))
                    .lore(MessageManager.getText(shellCasePlayer.getLang(), "gui-main-menu-equip-des")).build()).withListener(event -> {
    
                ClassGUI.openClassGUI(shellCasePlayer);
                shellCasePlayer.playGUIClickSound();
                
            }));
    
            menu.setButton(14, new SGButton(new ItemBuilder(Material.ANVIL)
                    .name(MessageManager.getText(shellCasePlayer.getLang(), "gui-main-menu-weapon"))
                    .lore(MessageManager.getText(shellCasePlayer.getLang(), "gui-main-menu-weapon-des")).build()).withListener(event -> {
                    
                WeaponSelectGUI.openWeaponSelectGUI(shellCasePlayer, gunStatusData -> {
                    GunCustomGUI.openGunCustomGUI(shellCasePlayer, gunStatusData);
                });
                
            }));
        
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    }
    
}
