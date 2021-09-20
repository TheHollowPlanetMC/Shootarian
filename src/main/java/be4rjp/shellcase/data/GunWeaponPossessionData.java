package be4rjp.shellcase.data;


import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.weapon.attachment.Attachment;
import be4rjp.shellcase.weapon.attachment.Sight;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.gun.GunWeapon;

public class GunWeaponPossessionData extends SavableByteData{
    
    public GunWeaponPossessionData() {
        super(1024 * 38);
    }
    
    public boolean hasWeapon(int index){
        return bytes[index] != 0;
    }
    
    public void setGunStatusData(GunStatusData gunStatusData){
        int saveIndex = gunStatusData.getGunWeapon().getSaveNumber() * 38;
        
        //1 ~ 2 (最高位のビットは所持しているかどうか、それ以外は予約)
        bytes[saveIndex] |= 0x80;
        bytes[saveIndex + 1] |= 0x00;
        //3 - つけているサイト
        bytes[saveIndex + 2] = (byte) (gunStatusData.getSight().getSaveNumber() & 0xFF);
        //4 - つけているバレル
        bytes[saveIndex + 3] = 0x00;
        //5 - つけているグリップ
        bytes[saveIndex + 4] = 0x00;
        //6 - つけているアクセサリー
        bytes[saveIndex + 5] = 0x00;
        //7 ~ 39 - どのアタッチメントを所持しているかどうか
        for(int index = saveIndex + 6; index < saveIndex + 38; index++){
            bytes[index] = gunStatusData.getAttachmentPossessionData()[index];
        }
    }
    
    public GunStatusData getGunStatusData(int saveNumber, ShellCasePlayer shellCasePlayer){
        int saveIndex = saveNumber * 38;
        
        GunStatusData gunStatusData = new GunStatusData(GunWeapon.getGunWeaponBySaveNumber(saveNumber), shellCasePlayer);
    
        //3 - つけているサイト
        gunStatusData.setSight((Sight) Attachment.getAttachmentBySaveNumber(bytes[saveIndex + 2] & 0xFF));
        //4 - つけているバレル
        //5 - つけているグリップ
        //6 - つけているアクセサリー
        //7 ~ 39 - どのアタッチメントを所持しているかどうか
        byte[] temp = new byte[32];
        int i = 0;
        for(int index = saveIndex + 6; index < saveIndex + 38; index++){
            temp[i] = bytes[index];
            i++;
        }
        gunStatusData.setAttachmentPossessionData(temp);
        
        return gunStatusData;
    }
    
    
    
    /**
     * 指定されたクラスを所持しているかどうかを返す
     * @param weaponClass
     * @return boolean
     */
    //public boolean hasWeaponClass(WeaponClass weaponClass){return getBit(weaponClass.getSaveNumber());}
    
    /**
     * 指定されたクラスを所持させる
     * @param weaponClass
     */
    //public void giveWeaponClass(WeaponClass weaponClass){setBit(weaponClass.getSaveNumber(), true);}
}
