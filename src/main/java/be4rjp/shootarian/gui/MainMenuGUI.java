package be4rjp.shootarian.gui;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.gui.pagination.CloseMenuPaginationButtonBuilder;
import be4rjp.shootarian.gui.setting.SettingGUI;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.match.MatchManager;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.TaskHandler;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MainMenuGUI {
    
    public static void openMainMenuGUI(ShootarianPlayer shootarianPlayer){
        Player player = shootarianPlayer.getBukkitPlayer();
        if(player == null) return;
    
        Lang lang = shootarianPlayer.getLang();
        String menuName = MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu");
    
        SGMenu menu = Shootarian.getSpiGUI().create(menuName, 5);
        menu.setPaginationButtonBuilder(CloseMenuPaginationButtonBuilder.getPaginationButtonBuilder(lang));
    
        TaskHandler.runAsync(() -> {
    
            for(int index = 0; index < 45; index++){
                menu.setButton(index, new SGButton(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("§r").build()));
            }
            
            boolean isShowAllComponent = GUIManager.isShowAllComponent(shootarianPlayer);
        
            if(isShowAllComponent) {
                if(shootarianPlayer.getShootarianTeam() == Shootarian.getLobbyTeam()) {
                    menu.setButton(10, new SGButton(new ItemBuilder(Material.LIME_STAINED_GLASS)
                            .name(MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-join"))
                            .lore(MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-join-des")).build()).withListener(event -> {
        
                        MatchManager.getMatchManager("conquest").join(shootarianPlayer);
        
                    }));
                } else {
                    menu.setButton(10, new SGButton(new ItemBuilder(Material.GRAY_STAINED_GLASS)
                            .name(MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-leave"))
                            .lore(MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-leave-des")).build()).withListener(event -> {
        
                        shootarianPlayer.reset();
        
                    }));
                }
    
                menu.setButton(12, new SGButton(new ItemBuilder(Material.IRON_HOE)
                        .name(MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-equip"))
                        .lore(MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-equip-des")).build()).withListener(event -> {
        
                    ClassGUI.openClassGUI(shootarianPlayer);
                    shootarianPlayer.playGUIClickSound();
        
                }));
    
                menu.setButton(14, new SGButton(new ItemBuilder(Material.ANVIL)
                        .name(MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-weapon"))
                        .lore(MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-weapon-des")).build()).withListener(event -> {
        
                    WeaponSelectGUI.openWeaponSelectGUI(shootarianPlayer, "gui-select-weapon", true, true, gunStatusData -> {
                        GunCustomGUI.openGunCustomGUI(shootarianPlayer, gunStatusData);
                        shootarianPlayer.playGUIClickSound();
                    }, () -> MainMenuGUI.openMainMenuGUI(shootarianPlayer));
        
                    shootarianPlayer.playGUIClickSound();
        
                }));
    
                menu.setButton(16, new SGButton(new ItemBuilder(Material.LIME_STAINED_GLASS)
                        .name(MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-gear"))
                        .lore(MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-gear-des")).build()).withListener(event -> {
        
                    HeadGearGUI.openHeadGearGUI(shootarianPlayer);
                    shootarianPlayer.playGUIClickSound();
        
                }));
    
    
                menu.setButton(28, new SGButton(new ItemBuilder(Material.REPEATER)
                        .name(MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-setting"))
                        .lore(MessageManager.getText(shootarianPlayer.getLang(), "gui-main-menu-setting-des")).build()).withListener(event -> {
    
                    SettingGUI.openSettingGUI(shootarianPlayer);
                    shootarianPlayer.playGUIClickSound();
        
                }));
            }
        
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    }
}
