package be4rjp.shootarian.gui;

import be4rjp.shootarian.Shootarian;
import be4rjp.shootarian.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shootarian.language.Lang;
import be4rjp.shootarian.language.MessageManager;
import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.util.ShootarianSound;
import be4rjp.shootarian.util.TaskHandler;
import be4rjp.shootarian.weapon.gadget.Gadget;
import be4rjp.shootarian.weapon.gadget.GadgetWeapon;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GadgetSelectGUI {

    private static final ShootarianSound SOUND = new ShootarianSound(Sound.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F);

    public static void openGadgetSelectGUI(ShootarianPlayer shootarianPlayer, String guiName, boolean isMain, GadgetRunnable gadgetRunnable) {
        Player player = shootarianPlayer.getBukkitPlayer();
        if (player == null) return;

        Lang lang = shootarianPlayer.getLang();
        String menuName = String.format(MessageManager.getText(shootarianPlayer.getLang(), "gui-page"), MessageManager.getText(lang, guiName));

        SGMenu menu = Shootarian.getSpiGUI().create(menuName, 5);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> ClassGUI.openClassGUI(shootarianPlayer)));

        TaskHandler.runAsync(() -> {

            for(int index = 0; index < 512; index++){
                Gadget gadget = Gadget.getGadgetBySaveNumber(index);
                if(gadget == null) break;
                if(!shootarianPlayer.getGadgetPossessionData().hasGadget(gadget)) continue;

                GadgetWeapon gadgetWeapon = gadget.getInstance();

                if(!isMain && gadgetWeapon.isMain()){
                    //サブ武器選択画面 & メイン専用武器
                    ItemStack original = gadgetWeapon.getItemStack(lang);
                    String name = original.getItemMeta().getDisplayName();
                    List<String> lore = original.getItemMeta().getLore();

                    name += MessageManager.getText(lang, "gui-gadget-main-only");

                    ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(name);
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);

                    menu.addButton(new SGButton(itemStack));

                }else if(shootarianPlayer.getWeaponClass().getWeaponStatusData(gadgetWeapon) != null){

                    //選択済み
                    ItemStack original = gadgetWeapon.getItemStack(lang);
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
                    menu.addButton(new SGButton(gadgetWeapon.getItemStack(lang)).withListener(event -> {
                        gadgetRunnable.run(gadget);
                        shootarianPlayer.playSound(SOUND);
                    }));

                }
            }
            
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));

        });
    }


    public interface GadgetRunnable{
        void run(Gadget gadget);
    }
}
