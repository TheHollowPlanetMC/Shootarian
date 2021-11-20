package be4rjp.shootarian.gui;

public class WeaponOperationGUI {
    
    /*
    public static void openWeaponOperationGUI(ShootarianPlayer shootarianPlayer, GunStatusData gunStatusData){
    
        Player player = shootarianPlayer.getBukkitPlayer();
        if(player == null) return;
    
        Lang lang = shootarianPlayer.getLang();
        String menuName = MessageManager.getText(shootarianPlayer.getLang(), "gui-class-select");
    
        SGMenu menu = Shootarian.getSpiGUI().create(menuName, 3);
        menu.setPaginationButtonBuilder(new BackMenuPaginationButtonBuilder(lang, () -> WeaponSelectGUI.openWeaponSelectGUI(shootarianPlayer, gun -> {
        
        })));
    
        TaskHandler.runAsync(() -> {
    
            menu.setButton(11, new SGButton(new ItemBuilder(Material.ANVIL)
                    .name(MessageManager.getText(shootarianPlayer.getLang(), "")).build()).withListener(event -> {
                    
            }));
            menu.setButton(15, );
    
            TaskHandler.runSync(() -> player.openInventory(menu.getInventory()));
        });
    
    }*/
    
}
