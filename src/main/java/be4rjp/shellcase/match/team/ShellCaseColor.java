package be4rjp.shellcase.match.team;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum ShellCaseColor {
    
    BLUE("Blue", ChatColor.BLUE, Color.BLUE, Material.BLUE_WOOL, Material.BLUE_CONCRETE, Material.BLUE_STAINED_GLASS),
    ORANGE("Orange", ChatColor.GOLD, Color.ORANGE, Material.ORANGE_WOOL, Material.ORANGE_CONCRETE, Material.ORANGE_STAINED_GLASS);
    
    private final String displayName;
    private final ChatColor chatColor;
    private final Color bukkitColor;
    private final Material wool;
    private final Material concrete;
    private final Material glass;
    
    ShellCaseColor(String displayName, ChatColor chatColor, Color bukkitColor, Material wool, Material concrete, Material glass){
        this.displayName = chatColor + displayName + ChatColor.RESET;
        this.chatColor = chatColor;
        this.bukkitColor = bukkitColor;
        this.wool = wool;
        this.concrete = concrete;
        this.glass = glass;
    }
    
    
    public ChatColor getChatColor() {return chatColor;}
    
    public Color getBukkitColor() {return bukkitColor;}
    
    public Material getConcrete() {return concrete;}
    
    public Material getWool() {return wool;}
    
    public String getDisplayName() {return displayName;}

    public Material getGlass() {return glass;}

    /**
     * ランダムにShellCaseColorを取得します
     * @return ShellCaseColor
     */
    public static ShellCaseColor getRandomColor(){
        ShellCaseColor[] ShellCaseColors = ShellCaseColor.values();
        int length = ShellCaseColors.length;
        return ShellCaseColors[new Random().nextInt(length)];
    }
    
    /**
     * ランダムにShellCaseColorのペアを取得します。
     * @return ShellCaseColor[] (length = 2)
     */
    public static ShellCaseColor[] getRandomColorPair(){
        ShellCaseColor[] ShellCaseColors = ShellCaseColor.values();
        List<ShellCaseColor> colorList = Arrays.asList(ShellCaseColors);
        Collections.shuffle(colorList);
        return new ShellCaseColor[]{colorList.get(0), colorList.get(1)};
    }
}
