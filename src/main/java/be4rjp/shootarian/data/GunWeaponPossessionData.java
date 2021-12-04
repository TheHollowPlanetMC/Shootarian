package be4rjp.shootarian.data;


import be4rjp.shootarian.player.ShootarianPlayer;
import be4rjp.shootarian.weapon.attachment.Attachment;
import be4rjp.shootarian.weapon.attachment.Grip;
import be4rjp.shootarian.weapon.attachment.Sight;
import be4rjp.shootarian.weapon.gun.GunStatusData;
import be4rjp.shootarian.weapon.gun.GunWeapon;

public class GunWeaponPossessionData extends SavableByteData{
    
    public GunWeaponPossessionData() {
        super(1024 * 38);
    }
    
    public boolean hasWeapon(int index){
        return bytes[index * 38] != 0;
    }
    
    public void setGunStatusData(GunStatusData gunStatusData){
        int saveIndex = gunStatusData.getGunWeapon().getSaveNumber() * 38;
        
        //1 ~ 2 (最高位のビットは所持しているかどうか、それ以外は予約)
        bytes[saveIndex] |= 0x80;
        bytes[saveIndex + 1] |= 0x00;
        //3 - つけているサイト
        bytes[saveIndex + 2] = (byte) (gunStatusData.getSight() == null ? 0 : gunStatusData.getSight().getSaveNumber() & 0xFF);
        //4 - つけているグリップ
        bytes[saveIndex + 3] = (byte) (gunStatusData.getGrip() == null ? 0 : gunStatusData.getGrip().getSaveNumber() & 0xFF);
        //5 - つけているバレル
        bytes[saveIndex + 4] = 0x00;
        //6 - つけているアクセサリー
        bytes[saveIndex + 5] = 0x00;
        //7 ~ 39 - どのアタッチメントを所持しているかどうか
        int i = 0;
        for(int index = saveIndex + 6; index < saveIndex + 38; index++){
            byte b = 0;
            if(gunStatusData.getAttachmentPossessionData().length > i) b = gunStatusData.getAttachmentPossessionData()[i];
            bytes[index] = b;
            i++;
        }
    }
    
    public GunStatusData getGunStatusData(int saveNumber, ShootarianPlayer shootarianPlayer){
        if(!hasWeapon(saveNumber)) return null;
        
        int saveIndex = saveNumber * 38;
        
        if(GunWeapon.getGunWeaponBySaveNumber(saveNumber) == null) return null;
        
        GunStatusData gunStatusData = new GunStatusData(GunWeapon.getGunWeaponBySaveNumber(saveNumber), shootarianPlayer);
    
        //3 - つけているサイト
        gunStatusData.setSight((Sight) Attachment.getAttachmentBySaveNumber(bytes[saveIndex + 2] & 0xFF));
        //4 - つけているグリップ
        gunStatusData.setGrip((Grip) Attachment.getAttachmentBySaveNumber(bytes[saveIndex + 3] & 0xFF));
        //5 - つけているバレル
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
    
}
