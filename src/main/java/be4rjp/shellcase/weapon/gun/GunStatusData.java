package be4rjp.shellcase.weapon.gun;

import be4rjp.shellcase.language.Lang;
import be4rjp.shellcase.language.MessageManager;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.player.passive.PassiveInfluence;
import be4rjp.shellcase.player.passive.PlayerPassiveInfluence;
import be4rjp.shellcase.weapon.WeaponManager;
import be4rjp.shellcase.weapon.WeaponStatusData;
import be4rjp.shellcase.weapon.attachment.Attachment;
import be4rjp.shellcase.weapon.attachment.Grip;
import be4rjp.shellcase.weapon.attachment.Sight;
import be4rjp.shellcase.weapon.recoil.RecoilPattern;
import be4rjp.shellcase.weapon.actions.ReloadActionRunnable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class GunStatusData extends WeaponStatusData {
    
    private final GunWeapon gunWeapon;
    private final ShellCasePlayer shellCasePlayer;
    
    private Sight sight;
    private Grip grip;
    
    private int maxBullets = 20;
    private long coolTime = 0;
    private long lastClickTime = 0;
    private long clickTick = 0;
    
    private RecoilPattern adsRecoil;
    private RecoilPattern normalRecoil;
    
    private BitSet attachmentPossessionData = new BitSet(64 * 8);
    
    public GunStatusData(GunWeapon gunWeapon, ShellCasePlayer shellCasePlayer){
        super(gunWeapon, shellCasePlayer);
        this.gunWeapon = gunWeapon;
        this.shellCasePlayer = shellCasePlayer;
        
        this.maxBullets = gunWeapon.getDefaultBullets();
        this.bullets = gunWeapon.getDefaultBullets();
        
        this.sight = gunWeapon.getDefaultSight();
        
        this.adsRecoil = gunWeapon.getADSRecoil().getRandomPattern();
        this.normalRecoil = gunWeapon.getNormalRecoil().getRandomPattern();
    }
    
    public Sight getSight() {return sight;}
    
    public void setSight(Sight sight) {this.sight = sight;}
    
    public Grip getGrip() {return grip;}
    
    public void setGrip(Grip grip) {this.grip = grip;}
    
    public GunWeapon getGunWeapon() {return gunWeapon;}
    
    public int getMaxBullets() {return maxBullets;}
    
    public void setMaxBullets(int maxBullets) {this.maxBullets = maxBullets;}
    
    public boolean isCoolTime(){return System.currentTimeMillis() < coolTime;}
    
    public void setCoolTime(long tick){coolTime = System.currentTimeMillis() + (tick * 50L);}
    
    public RecoilPattern getNormalRecoil() {return normalRecoil;}
    
    public RecoilPattern getAdsRecoil() {return adsRecoil;}
    
    public long getLastClickTime() {return lastClickTime;}
    
    public void setLastClickTime(long lastClickTime) {this.lastClickTime = lastClickTime;}
    
    public long getClickTick() {return clickTick;}
    
    public void setClickTick(long clickTick) {this.clickTick = clickTick;}
    
    public void addAttachment(Attachment attachment){this.attachmentPossessionData.set(attachment.getSaveNumber(), true);}
    
    public boolean hasAttachment(Attachment attachment){return this.attachmentPossessionData.get(attachment.getSaveNumber());}
    
    public void setAttachmentPossessionData(byte[] bytes){this.attachmentPossessionData = BitSet.valueOf(bytes);}
    
    public byte[] getAttachmentPossessionData() {return attachmentPossessionData.toByteArray();}
    
    public List<PassiveInfluence> getAllPassiveInfluences(){
        List<PassiveInfluence> passiveInfluenceList = new ArrayList<>(gunWeapon.getPassiveInfluenceList());
        if(sight != null) passiveInfluenceList.addAll(sight.getPassiveInfluenceList());
        if(grip != null) passiveInfluenceList.addAll(grip.getPassiveInfluenceList());
        
        return passiveInfluenceList;
    }
    
    public void resetRecoil() {
        this.adsRecoil = gunWeapon.getADSRecoil().getRandomPattern();
        this.normalRecoil = gunWeapon.getNormalRecoil().getRandomPattern();
    }
    
    public void reload(){
        if(this.isReloading) return;
        if(shellCasePlayer == null) return;
        this.isReloading = true;
        new ReloadActionRunnable(shellCasePlayer, this).start();
        this.resetRecoil();
    }

    @Override
    public void updateDisplayName(ShellCasePlayer shellCasePlayer){
        Player player = shellCasePlayer.getBukkitPlayer();
        if(player == null) return;
        
        for(int index = 0; index < 9; index++){
            ItemStack itemStack = player.getInventory().getItem(index);
            if(itemStack == null) continue;
            
            GunWeapon gunWeapon = WeaponManager.getGunWeaponByItem(itemStack);
            if(gunWeapon == null) continue;
            if(gunWeapon != this.gunWeapon) continue;
            
            if(shellCasePlayer.isADS()) {
                player.sendActionBar(this.getItemStack(shellCasePlayer.getLang()).getItemMeta().getDisplayName());
            } else {
                player.getInventory().setItem(index, this.getItemStack(shellCasePlayer.getLang()));
            }
            break;
        }
    }
    
    
    @Override
    public ItemStack getItemStackFlexible(Lang lang){
        ItemStack itemStack = gunWeapon.getItemStack(lang);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if(lore == null) lore = new ArrayList<>();
        lore.add("");
        lore.add(String.format(MessageManager.getText(lang, "weapon-attachment-sight"), sight == null ? MessageManager.getText(lang, "nothing") : sight.getDisplayName(lang)));
        lore.add(String.format(MessageManager.getText(lang, "weapon-attachment-grip"), grip == null ? MessageManager.getText(lang, "nothing") : grip.getDisplayName(lang)));
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        
        return itemStack;
    }
}
