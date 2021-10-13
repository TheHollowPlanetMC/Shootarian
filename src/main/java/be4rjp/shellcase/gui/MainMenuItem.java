package be4rjp.shellcase.gui;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.ShellCaseWeapon;
import org.bukkit.Material;

public class MainMenuItem extends ShellCaseWeapon {
    
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
    public void onRightClick(ShellCasePlayer shellCasePlayer) {
        MainMenuGUI.openMainMenuGUI(shellCasePlayer);
    }
    
    @Override
    public void onLeftClick(ShellCasePlayer shellCasePlayer) {
        MainMenuGUI.openMainMenuGUI(shellCasePlayer);
    }
}
