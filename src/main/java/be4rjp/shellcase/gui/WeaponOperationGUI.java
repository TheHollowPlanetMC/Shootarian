package be4rjp.shellcase.gui;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.TaskHandler;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WeaponOperationGUI {
    
    /*
    public static void openWeaponOperationGUI(ShellCasePlayer shellCasePlayer, GunStatusData gunStatusData){
    
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
    
        Lang lang = shellCasePlayer.getLang();
        String menuName = MessageManager.getText(shellCasePlayer.getLang(), "gui-class-select");
    
        SGMenu menu = ShellCase.getSpiGUI().create(menuName, 3);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> WeaponSelectGUI.openWeaponSelectGUI(shellCasePlayer, gun -> {
        
        })));
    
        TaskHandler.runAsync(() -> {
    
            menu.setButton(11, new SGButton(new ItemBuilder(Material.ANVIL)
                    .name(MessageManager.getText(shellCasePlayer.getLang(), "")).build()).withListener(event -> {
                    
            }));
            menu.setButton(15, );
    
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    
    }*/
    
}
