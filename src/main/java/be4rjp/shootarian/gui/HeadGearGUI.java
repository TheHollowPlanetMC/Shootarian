package be4rjp.shootarian.gui;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.player.costume.HeadGear;
import be4rjp.shootarian.util.ShootarianSound;
import be4rjp.shootarian.util.TaskHandler;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HeadGearGUI {
    
    private static final ShootarianSound SOUND = new ShootarianSound(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
    
    public static void openHeadGearGUI(ShootarianPlayer shootarianPlayer){
        Player player = shootarianPlayer.getBukkitPlayer();
        if(player == null) return;
        
        Lang lang = shootarianPlayer.getLang();
        String menuName = String.format(MessageManager.getText(shootarianPlayer.getLang(), "gui-page"), MessageManager.getText(lang, "gui-select-head-gear"));
        
        SGMenu menu = Shootarian.getSpiGUI().create(menuName, 4);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> MainMenuGUI.openMainMenuGUI(shootarianPlayer)));
        
        TaskHandler.runAsync(() -> {
            
            for(int index = 0; index < 2048; index++){
                HeadGear headGear = HeadGear.getHeadGearBySaveNumber(index);
                if(headGear == null) break;
                if(!shootarianPlayer.getHeadGearPossessionData().hasHeadGear(headGear)) continue;
                
                menu.addButton(new SGButton(headGear.getItemStack(lang)).withListener(event -> {
                    shootarianPlayer.setHeadGear(headGear);
                    shootarianPlayer.sendText("gui-head-gear-selected", headGear.getDisplayName(shootarianPlayer.getLang()));
                    shootarianPlayer.playSound(SOUND);
                    MainMenuGUI.openMainMenuGUI(shootarianPlayer);
                }));
            }
            
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    }
    
}
