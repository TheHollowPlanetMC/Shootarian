package be4rjp.shellcase.gui;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.player.costume.HeadGear;
import be4rjp.shellcase.util.ShellCaseSound;
import be4rjp.shellcase.util.TaskHandler;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HeadGearGUI {
    
    private static final ShellCaseSound SOUND = new ShellCaseSound(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
    
    public static void openHeadGearGUI(ShellCasePlayer shellCasePlayer){
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
        
        Lang lang = shellCasePlayer.getLang();
        String menuName = MessageManager.getText(shellCasePlayer.getLang(), "gui-head-gear-select");
        
        SGMenu menu = ShellCase.getSpiGUI().create(menuName, 4);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> MainMenuGUI.openMainMenuGUI(shellCasePlayer)));
        
        TaskHandler.runAsync(() -> {
            
            for(int index = 0; index < 2048; index++){
                HeadGear headGear = HeadGear.getHeadGearBySaveNumber(index);
                if(headGear == null) break;
                if(!shellCasePlayer.getHeadGearPossessionData().hasHeadGear(headGear)) continue;
                
                menu.addButton(new SGButton(headGear.getItemStack(lang)).withListener(event -> {
                    shellCasePlayer.setHeadGear(headGear);
                    shellCasePlayer.sendText("gui-head-gear-selected", headGear.getDisplayName(shellCasePlayer.getLang()));
                    shellCasePlayer.playSound(SOUND);
                    MainMenuGUI.openMainMenuGUI(shellCasePlayer);
                }));
            }
            
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    }
    
}
