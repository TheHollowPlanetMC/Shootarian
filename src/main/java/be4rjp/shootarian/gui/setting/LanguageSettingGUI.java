package be4rjp.shootarian.gui.setting;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.TaskHandler;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class LanguageSettingGUI {
    
    public static void openLanguageSettingGUI(ShootarianPlayer shootarianPlayer) {
        Player player = shootarianPlayer.getBukkitPlayer();
        if (player == null) return;
    
        Lang lang = shootarianPlayer.getLang();
        String menuName = MessageManager.getText(shootarianPlayer.getLang(), "gui-setting-lang");
    
        SGMenu menu = Shootarian.getSpiGUI().create(menuName, 1);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> {
            SettingGUI.openSettingGUI(shootarianPlayer);
            shootarianPlayer.playGUIClickSound();
        }));
    
        TaskHandler.runAsync(() -> {
            
            for(Lang language : Lang.values()){
                menu.addButton(new SGButton(language.getFlagHead()).withListener(event -> {
                    shootarianPlayer.setLang(language);
                    shootarianPlayer.sendText("gui-setting-lang-changed", language.getDisplayName());
                    SettingGUI.openSettingGUI(shootarianPlayer);
                    shootarianPlayer.playGUIClickSound();
                }));
            }
    
            Inventory inventory = menu.getInventory();
            TaskHandler.runSync(() -> player.openInventory(inventory));
        });
    }
}
