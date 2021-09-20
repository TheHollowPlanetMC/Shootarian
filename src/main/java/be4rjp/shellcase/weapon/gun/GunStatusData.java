package be4rjp.shellcase.weapon.gun;

import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.player.passive.PassiveInfluence;
import be4rjp.shellcase.weapon.WeaponManager;
import be4rjp.shellcase.weapon.WeaponStatusData;
import be4rjp.shellcase.weapon.attachment.Attachment;
import be4rjp.shellcase.weapon.attachment.Sight;
import be4rjp.shellcase.weapon.recoil.RecoilPattern;
import be4rjp.shellcase.weapon.actions.ReloadActionRunnable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.BitSet;

public class GunStatusData extends WeaponStatusData {
    
    private final GunWeapon gunWeapon;
    private final ShellCasePlayer shellCasePlayer;
    
    private Sight sight;
    
    private final PassiveInfluence passiveInfluence = new PassiveInfluence();
    
    private int maxBullets = 20;
    private long coolTime = 0;
    private long lastClickTime = 0;
    private long clickTick = 0;
    
    private RecoilPattern adsRecoil;
    private RecoilPattern normalRecoil;
    
    private BitSet attachmentPossessionData = new BitSet(32);
    
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

    public void createPassiveInfluence(){this.passiveInfluence.createPassiveInfluence(this);}
    
    public Sight getSight() {return sight;}
    
    public void setSight(Sight sight) {this.sight = sight;}
    
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
    
    public void addAttachment(Attachment attachment){this.attachmentPossessionData.set(attachment.getSaveNumber(), 1);}
    
    public boolean hasAttachment(Attachment attachment){return this.attachmentPossessionData.get(attachment.getSaveNumber());}
    
    public void setAttachmentPossessionData(byte[] bytes){this.attachmentPossessionData = BitSet.valueOf(bytes);}
    
    public byte[] getAttachmentPossessionData() {return attachmentPossessionData.toByteArray();}
    
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
            
            player.getInventory().setItem(index, this.getItemStack(shellCasePlayer.getLang()));
            break;
        }
    }
}
