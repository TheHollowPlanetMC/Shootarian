package be4rjp.shellcase.gui;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.TaskHandler;
import be4rjp.shellcase.weapon.attachment.Attachment;
import be4rjp.shellcase.weapon.attachment.Grip;
import be4rjp.shellcase.weapon.attachment.Sight;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.entity.Player;

public class ChangeAttachmentGUI {
    
    public static void openAttachmentGUI(ShellCasePlayer shellCasePlayer, Class<? extends Attachment> clazz, GunStatusData gunStatusData){
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
    
        Lang lang = shellCasePlayer.getLang();
        String menuName = MessageManager.getText(shellCasePlayer.getLang(), "gui-weapon-custom-sight");
    
        SGMenu menu = ShellCase.getSpiGUI().create(menuName, 2);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> {
            GunCustomGUI.openGunCustomGUI(shellCasePlayer, gunStatusData);
        }));
    
        TaskHandler.runAsync(() -> {
        
            for(Attachment attachment : gunStatusData.getGunWeapon().getAttachments()){
                if(attachment.getClass() != clazz) continue;
                if(!gunStatusData.hasAttachment(attachment)) continue;
                
                menu.addButton(new SGButton(attachment.getItemStack(lang)).withListener(event -> {
                    
                    if(clazz == Sight.class){
                        gunStatusData.setSight((Sight) attachment);
                        shellCasePlayer.sendText("gui-weapon-custom-sight-changed", gunStatusData.getGunWeapon().getDisplayName(lang), attachment.getDisplayName(lang));
                    }
                    if(clazz == Grip.class){
                        gunStatusData.setGrip((Grip) attachment);
                        shellCasePlayer.sendText("gui-weapon-custom-grip-changed", gunStatusData.getGunWeapon().getDisplayName(lang), attachment.getDisplayName(lang));
                    }
    
                    shellCasePlayer.getWeaponPossessionData().setGunStatusData(gunStatusData);
                    shellCasePlayer.getWeaponClass().loadGunStatusData(shellCasePlayer);
                    shellCasePlayer.giveItems();
                    shellCasePlayer.playGUIClickSound();
                    
                    GunCustomGUI.openGunCustomGUI(shellCasePlayer, gunStatusData);
                }));
            }
        
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    }
    
    public static interface AttachmentChangeRunnable{
        void run(Attachment attachment);
    }
}
