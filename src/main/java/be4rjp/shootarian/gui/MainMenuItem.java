package be4rjp.shootarian.gui;

import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.ShootarianWeapon;
import org.bukkit.Material;

public class MainMenuItem extends ShootarianWeapon {
    
    public static MainMenuItem mainMenuItem;
    
    public MainMenuItem() {
        super("main-menu-nw");
        mainMenuItem = this;
        
        for(Lang lang : Lang.values()){
            super.displayName.put(lang, MessageManager.getText(lang, "gui-main-menu-item"));
        }
        
        super.material = Material.COMPASS;
    }
    
    @Override
    public void onRightClick(ShootarianPlayer shootarianPlayer) {
        MainMenuGUI.openMainMenuGUI(shootarianPlayer);
    }
    
    @Override
    public void onLeftClick(ShootarianPlayer shootarianPlayer) {
        MainMenuGUI.openMainMenuGUI(shootarianPlayer);
    }
}
