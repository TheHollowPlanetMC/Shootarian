package be4rjp.shootarian.gui.setting;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.gui.MainMenuGUI;
import be4rjp.shootarian.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.TaskHandler;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingGUI {

    public static void openSettingGUI(ShootarianPlayer shootarianPlayer){
        Player player = shootarianPlayer.getBukkitPlayer();
        if(player == null) return;
        
        Lang lang = shootarianPlayer.getLang();
        String menuName = MessageManager.getText(shootarianPlayer.getLang(), "gui-setting");
        
        SGMenu menu = Shootarian.getSpiGUI().create(menuName, 3);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> {
            MainMenuGUI.openMainMenuGUI(shootarianPlayer);
            shootarianPlayer.playGUIClickSound();
        }));
        
        TaskHandler.runAsync(() -> {
            
            for(int index = 0; index < 27; index++){
                menu.setButton(index, new SGButton(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§r").build()));
            }
    
            menu.setButton(10, new SGButton(new ItemBuilder(Material.IRON_HOE)
                    .name(MessageManager.getText(shootarianPlayer.getLang(), "gui-setting-video")).build()).withListener(event -> {
                    
                shootarianPlayer.playGUIClickSound();
            }));
    
    
            ItemStack flagHead = shootarianPlayer.getLang().getFlagHead().clone();
            ItemMeta itemMeta = flagHead.getItemMeta();
            itemMeta.setDisplayName(MessageManager.getText(shootarianPlayer.getLang(), "gui-setting-lang"));
            flagHead.setItemMeta(itemMeta);
    
            menu.setButton(12, new SGButton(flagHead).withListener(event -> {
        
                LanguageSettingGUI.openLanguageSettingGUI(shootarianPlayer);
                shootarianPlayer.playGUIClickSound();
            }));
    
            Inventory inventory = menu.getInventory();
            TaskHandler.runSync(() -> player.openInventory(inventory));
            
        });
    }
}
