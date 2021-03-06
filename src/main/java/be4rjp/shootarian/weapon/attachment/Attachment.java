package be4rjp.shootarian.weapon.attachment;

import be4rjp.shootarian.item.ShootarianItem;
import be4rjp.shootarian.player.passive.PassiveInfluence;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public abstract class Attachment extends ShootarianItem {
    
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
    //パッシブ効果
    protected List<PassiveInfluence> passiveInfluenceList = new ArrayList<>();
    //SQLに保存するための識別番号
    protected int saveNumber = 0;
    //パッシブ効果のリスト
    
    
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
        
        super.itemLoad(yml);
    
        if(yml.contains("passive")) yml.getStringList("passive").forEach(passiveString -> passiveInfluenceList.add(PassiveInfluence.fromString(passiveString)));
        if(yml.contains("save-number")) this.saveNumber = yml.getInt("save-number");
        
        this.loadDetailsData();
    }
    
    /**
     * ymlファイルから詳細なデータをロードする
     */
    public abstract void loadDetailsData();
    
    public String getId(){return id;}
    
    public List<PassiveInfluence> getPassiveInfluenceList() {return passiveInfluenceList;}
    
    public int getSaveNumber() {return saveNumber;}
    
    
    public enum AttachmentType{
        SIGHT(Sight.class),
        GRIP(Grip.class);
    
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
