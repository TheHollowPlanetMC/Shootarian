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
import org.bukkit.entity.Player;

public class WeaponSelectGUI {
    
    public static void openWeaponSelectGUI(ShellCasePlayer shellCasePlayer, WeaponSelectRunnable weaponSelectRunnable){
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
    
        Lang lang = shellCasePlayer.getLang();
        String menuName = MessageManager.getText(shellCasePlayer.getLang(), "gui-class-select");
    
        SGMenu menu = ShellCase.getSpiGUI().create(menuName, 4);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> MainMenuGUI.openMainMenuGUI(shellCasePlayer)));
    
        TaskHandler.runAsync(() -> {
            
            for(int index = 0; index < 1024; index++){
                GunStatusData gunStatusData = shellCasePlayer.getWeaponPossessionData().getGunStatusData(index, shellCasePlayer);
                if(gunStatusData == null) continue;
                
                menu.addButton(new SGButton(gunStatusData.getItemStack(lang)).withListener(event -> {
                    weaponSelectRunnable.run(gunStatusData);
                    shellCasePlayer.playGUIClickSound();
                }));
            }
            
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    }
    
    
    
    public static interface WeaponSelectRunnable{
        void run(GunStatusData gunStatusData);
    }
}
