package be4rjp.shootarian.gui;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.TaskHandler;
import be4rjp.shootarian.weapon.attachment.Attachment;
import be4rjp.shootarian.weapon.attachment.Grip;
import be4rjp.shootarian.weapon.attachment.Sight;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.entity.Player;

public class ChangeAttachmentGUI {
    
    public static void openAttachmentGUI(ShootarianPlayer shootarianPlayer, Class<? extends Attachment> clazz, GunStatusData gunStatusData){
        Player player = shootarianPlayer.getBukkitPlayer();
        if(player == null) return;
    
        Lang lang = shootarianPlayer.getLang();
        String menuName = MessageManager.getText(shootarianPlayer.getLang(), "gui-weapon-custom-sight");
    
        SGMenu menu = Shootarian.getSpiGUI().create(menuName, 2);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> {
            GunCustomGUI.openGunCustomGUI(shootarianPlayer, gunStatusData);
        }));
    
        TaskHandler.runAsync(() -> {
        
            for(Attachment attachment : gunStatusData.getGunWeapon().getAttachments()){
                if(attachment.getClass() != clazz) continue;
                if(!gunStatusData.hasAttachment(attachment)) continue;
                
                menu.addButton(new SGButton(attachment.getItemStack(lang)).withListener(event -> {
                    
                    if(clazz == Sight.class){
                        gunStatusData.setSight((Sight) attachment);
                        shootarianPlayer.sendText("gui-weapon-custom-sight-changed", gunStatusData.getGunWeapon().getDisplayName(lang), attachment.getDisplayName(lang));
                    }
                    if(clazz == Grip.class){
                        gunStatusData.setGrip((Grip) attachment);
                        shootarianPlayer.sendText("gui-weapon-custom-grip-changed", gunStatusData.getGunWeapon().getDisplayName(lang), attachment.getDisplayName(lang));
                    }
    
                    shootarianPlayer.getWeaponPossessionData().setGunStatusData(gunStatusData);
                    shootarianPlayer.getWeaponClass().loadGunStatusData(shootarianPlayer);
                    shootarianPlayer.giveItems();
                    shootarianPlayer.playGUIClickSound();
                    
                    GunCustomGUI.openGunCustomGUI(shootarianPlayer, gunStatusData);
                }));
            }
        
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    }
    
    public static interface AttachmentChangeRunnable{
        void run(Attachment attachment);
    }
}
