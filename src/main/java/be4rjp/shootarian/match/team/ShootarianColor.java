package be4rjp.shootarian.match.team;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum ShootarianColor {
    
    BLUE("Blue", ChatColor.BLUE, Color.BLUE, Material.BLUE_WOOL, Material.BLUE_CONCRETE, Material.BLUE_STAINED_GLASS, (byte) 48),
    ORANGE("Orange", ChatColor.GOLD, Color.ORANGE, Material.ORANGE_WOOL, Material.ORANGE_CONCRETE, Material.ORANGE_STAINED_GLASS, (byte) 60);
    
    private final String displayName;
    private final ChatColor chatColor;
    private final Color bukkitColor;
    private final Material wool;
    private final Material concrete;
    private final Material glass;
    private final byte canvasColor;
    
    ShootarianColor(String displayName, ChatColor chatColor, Color bukkitColor, Material wool, Material concrete, Material glass, byte canvasColor){
        this.displayName = chatColor + displayName + ChatColor.RESET;
        this.chatColor = chatColor;
        this.bukkitColor = bukkitColor;
        this.wool = wool;
        this.concrete = concrete;
        this.glass = glass;
        this.canvasColor = canvasColor;
    }
    
    
    public ChatColor getChatColor() {return chatColor;}
    
    public Color getBukkitColor() {return bukkitColor;}
    
    public Material getConcrete() {return concrete;}
    
    public Material getWool() {return wool;}
    
    public String getDisplayName() {return displayName;}

    public Material getGlass() {return glass;}
    
    public byte getCanvasColor() {return canvasColor;}
    
    @Override
    public String toString() {
        return this.chatColor.toString();
    }

    /**
     * ランダムにShootarianColorを取得します
     * @return ShootarianColor
     */
    public static ShootarianColor getRandomColor(){
        ShootarianColor[] shootarianColors = ShootarianColor.values();
        int length = shootarianColors.length;
        return shootarianColors[new Random().nextInt(length)];
    }
    
    /**
     * ランダムにShootarianColorのペアを取得します。
     * @return ShootarianColor[] (length = 2)
     */
    public static ShootarianColor[] getRandomColorPair(){
        ShootarianColor[] shootarianColors = ShootarianColor.values();
        List<ShootarianColor> colorList = Arrays.asList(shootarianColors);
        Collections.shuffle(colorList);
        return new ShootarianColor[]{colorList.get(0), colorList.get(1)};
    }
}
