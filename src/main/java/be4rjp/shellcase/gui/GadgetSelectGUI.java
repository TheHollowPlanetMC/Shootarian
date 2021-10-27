package be4rjp.shellcase.gui;

import be4rjp.shellcase.ShellCase;
import be4rjp.shellcase.gui.pagination.BackMenuPaginationButtonBuilder;
import be4rjp.shellcase.gui.pagination.CloseMenuPaginationButtonBuilder;
import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.ShellCaseSound;
import be4rjp.shellcase.util.TaskHandler;
import be4rjp.shellcase.weapon.gadget.Gadget;
import be4rjp.shellcase.weapon.gadget.GadgetWeapon;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GadgetSelectGUI {

    private static final ShellCaseSound SOUND = new ShellCaseSound(Sound.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F);

    public static void openGadgetSelectGUI(ShellCasePlayer shellCasePlayer, String guiName, boolean isMain, GadgetRunnable gadgetRunnable) {
        Player player = shellCasePlayer.getBukkitPlayer();
        if (player == null) return;

        Lang lang = shellCasePlayer.getLang();
        String menuName = String.format(MessageManager.getText(shellCasePlayer.getLang(), "gui-page"), MessageManager.getText(lang, guiName));

        SGMenu menu = ShellCase.getSpiGUI().create(menuName, 5);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> ClassGUI.openClassGUI(shellCasePlayer)));

        TaskHandler.runAsync(() -> {

            for(int index = 0; index < 512; index++){
                Gadget gadget = Gadget.getGadgetBySaveNumber(index);
                if(gadget == null) break;
                if(!shellCasePlayer.getGadgetPossessionData().hasGadget(gadget)) continue;

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

                }else if(shellCasePlayer.getWeaponClass().getWeaponStatusData(gadgetWeapon) != null){

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
                        shellCasePlayer.playSound(SOUND);
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
