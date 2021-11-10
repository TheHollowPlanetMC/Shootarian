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
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingGUI {

    public static void openSettingGUI(ShellCasePlayer shellCasePlayer){
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
        
        Lang lang = shellCasePlayer.getLang();
        String menuName = MessageManager.getText(shellCasePlayer.getLang(), "gui-setting");
        
        SGMenu menu = ShellCase.getSpiGUI().create(menuName, 3);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> {
            MainMenuGUI.openMainMenuGUI(shellCasePlayer);
            shellCasePlayer.playGUIClickSound();
        }));
        
        TaskHandler.runAsync(() -> {
            
            for(int index = 0; index < 27; index++){
                menu.setButton(index, new SGButton(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§r").build()));
            }
    
            menu.setButton(10, new SGButton(new ItemBuilder(Material.IRON_HOE)
                    .name(MessageManager.getText(shellCasePlayer.getLang(), "gui-setting-video")).build()).withListener(event -> {
                    
                shellCasePlayer.playGUIClickSound();
            }));
    
    
            ItemStack flagHead = shellCasePlayer.getLang().getFlagHead().clone();
            ItemMeta itemMeta = flagHead.getItemMeta();
            itemMeta.setDisplayName(MessageManager.getText(shellCasePlayer.getLang(), "gui-setting-lang"));
            flagHead.setItemMeta(itemMeta);
    
            menu.setButton(12, new SGButton(flagHead).withListener(event -> {
        
                LanguageSettingGUI.openLanguageSettingGUI(shellCasePlayer);
                shellCasePlayer.playGUIClickSound();
            }));
    
            Inventory inventory = menu.getInventory();
            TaskHandler.runSync(() -> player.openInventory(inventory));
            
        });
    }
}
