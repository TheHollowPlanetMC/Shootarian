package be4rjp.shellcase.weapon.attachment;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.player.passive.Passive;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class Attachment {
    
    private static final Map<String, Attachment> attachmentMap = new HashMap<>();
    
    public static Attachment getAttachment(String id){return attachmentMap.get(id);}
    
    public static Attachment getAttachmentBySaveNumber(int saveNumber){
        for(Attachment attachment : attachmentMap.values()){
            if(saveNumber == attachment.getSaveNumber()){
                return attachment;
            }
        }
        return null;
    }
    
    
    //アタッチメントの識別名
    protected final String id;
    //設定ファイル
    protected YamlConfiguration yml;
    //アタッチメントの表示名
    protected Map<Lang, String> displayName = new HashMap<>();
    //マテリアル
    protected Material material = Material.BARRIER;
    //CustomModelDataのID
    protected int modelID = 0;
    //パッシブ効果とその効果倍率のマップ
    protected Map<Passive, Float> passiveInfluenceMap = new HashMap<>();
    //SQLに保存するための識別番号
    protected int saveNumber = 0;
    
    
    public Attachment(String id){
        this.id = id;
        attachmentMap.put(id, this);
    }
    
    /**
     * ymlファイルからロードする
     * @param yml
     */
    public void loadData(YamlConfiguration yml) {
        this.yml = yml;
    
        if (yml.contains("display-name")) {
            for (String languageName : yml.getConfigurationSection("display-name").getKeys(false)) {
                Lang lang = Lang.valueOf(languageName);
                String name = yml.getString("display-name." + languageName);
                this.displayName.put(lang, ChatColor.translateAlternateColorCodes('&', name));
            }
        }
        if (yml.contains("material")) this.material = Material.getMaterial(Objects.requireNonNull(yml.getString("material")));
        if (yml.contains("custom-model-data")) this.modelID = yml.getInt("custom-model-data");
        if (yml.contains("passive")){
            List<String> passiveList = yml.getStringList("passive");
            for(String line : passiveList){
                String[] args = line.replace(" ", "").split(",");
                Passive passive = Passive.valueOf(args[0]);
                float influence = Float.parseFloat(args[1]);
                
                passiveInfluenceMap.put(passive, influence);
            }
        }
        if(yml.contains("save-number")) this.saveNumber = yml.getInt("save-number");
        
        this.loadDetailsData();
    }
    
    /**
     * ymlファイルから詳細なデータをロードする
     */
    public abstract void loadDetailsData();
    
    
    /**
     * 表示名を取得する
     * @return String
     */
    public String getDisplayName(Lang lang) {
        String name = displayName.get(lang);
        if(name == null){
            return "No name.";
        }else{
            return name;
        }
    }
    
    public String getId(){return id;}
    
    public int getModelID() {return modelID;}
    
    public Map<Passive, Float> getPassiveInfluenceMap() {return passiveInfluenceMap;}
    
    public Material getMaterial() {return material;}
    
    public int getSaveNumber() {return saveNumber;}
    
    /**
     * アイテムを取得する
     * @param lang 言語
     * @return
     */
    public ItemStack getItemStack(Lang lang){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(this.getDisplayName(lang));
        itemMeta.setCustomModelData(modelID);
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
    
    
    public enum AttachmentType{
        SIGHT(Sight.class);
    
        private final Class<? extends Attachment> attachmentClass;
    
        AttachmentType(Class<? extends Attachment> attachmentClass){this.attachmentClass = attachmentClass;}
    
        public Attachment createAttachmentInstance(String id){
            try{
                return attachmentClass.getConstructor(String.class).newInstance(id);
            }catch (Exception e){e.printStackTrace();}
            return null;
        }
    }
}
