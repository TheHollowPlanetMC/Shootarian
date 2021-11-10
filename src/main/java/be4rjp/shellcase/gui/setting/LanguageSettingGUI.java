package be4rjp.shellcase.gui.setting;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.gui.MainMenuGUI;
import be4rjp.shellcase.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.TaskHandler;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class LanguageSettingGUI {
    
    public static void openLanguageSettingGUI(ShellCasePlayer shellCasePlayer) {
        Player player = shellCasePlayer.getBukkitPlayer();
        if (player == null) return;
    
        Lang lang = shellCasePlayer.getLang();
        String menuName = MessageManager.getText(shellCasePlayer.getLang(), "gui-setting-lang");
    
        SGMenu menu = ShellCase.getSpiGUI().create(menuName, 1);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> {
            SettingGUI.openSettingGUI(shellCasePlayer);
            shellCasePlayer.playGUIClickSound();
        }));
    
        TaskHandler.runAsync(() -> {
            
            for(Lang language : Lang.values()){
                menu.addButton(new SGButton(language.getFlagHead()).withListener(event -> {
                    shellCasePlayer.setLang(language);
                    shellCasePlayer.sendText("gui-setting-lang-changed", language.getDisplayName());
                    SettingGUI.openSettingGUI(shellCasePlayer);
                    shellCasePlayer.playGUIClickSound();
                }));
            }
    
            Inventory inventory = menu.getInventory();
            TaskHandler.runSync(() -> player.openInventory(inventory));
        });
    }
}
