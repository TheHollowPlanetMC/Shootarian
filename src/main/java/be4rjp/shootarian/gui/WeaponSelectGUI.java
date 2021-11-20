package be4rjp.shootarian.gui;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.ShootarianSound;
import be4rjp.shootarian.util.TaskHandler;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class WeaponSelectGUI {

    private static final ShootarianSound SOUND = new ShootarianSound(Sound.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F);

    /**
     * 武器選択画面を開く
     * @param shootarianPlayer player
     * @param isMain メイン武器を表示するかどうか
     * @param weaponSelectRunnable クリックイベント
     */
    public static void openWeaponSelectGUI(ShootarianPlayer shootarianPlayer, String guiName, boolean isMain, boolean isCustom, WeaponSelectRunnable weaponSelectRunnable, Runnable backRunnable){
        Player player = shootarianPlayer.getBukkitPlayer();
        if(player == null) return;
    
        Lang lang = shootarianPlayer.getLang();
        String menuName = String.format(MessageManager.getText(shootarianPlayer.getLang(), "gui-page"), MessageManager.getText(lang, guiName));
    
        SGMenu menu = Shootarian.getSpiGUI().create(menuName, 4);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, backRunnable));
    
        TaskHandler.runAsync(() -> {
            
            for(int index = 0; index < 1024; index++){
                GunStatusData gunStatusData = shootarianPlayer.getWeaponPossessionData().getGunStatusData(index, shootarianPlayer);
                if(gunStatusData == null) continue;

                if(!isMain && gunStatusData.getGunWeapon().isMain()){
                    //サブ武器選択画面 & メイン専用武器
                    ItemStack original = gunStatusData.getItemStack(lang);
                    String name = original.getItemMeta().getDisplayName();
                    List<String> lore = original.getItemMeta().getLore();

                    name += MessageManager.getText(lang, "gui-weapon-main-only");

                    ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(name);
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);

                    menu.addButton(new SGButton(itemStack));

                }else if(shootarianPlayer.getWeaponClass().getWeaponStatusData(gunStatusData.getGunWeapon()) != null && !isCustom){

                    //選択済み
                    ItemStack original = gunStatusData.getItemStack(lang);
                    String name = original.getItemMeta().getDisplayName();
                    List<String> lore = original.getItemMeta().getLore();

                    name += MessageManager.getText(lang, "gui-weapon-chosen");

                    ItemStack itemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(name);
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);

                    menu.addButton(new SGButton(itemStack));

                }else {
                    //通常
                    menu.addButton(new SGButton(gunStatusData.getItemStack(lang)).withListener(event -> {
                        weaponSelectRunnable.run(gunStatusData);
                        shootarianPlayer.playSound(SOUND);
                    }));

                }
            }
            
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    }
    
    
    
    public interface WeaponSelectRunnable{
        void run(GunStatusData gunStatusData);
    }
}
