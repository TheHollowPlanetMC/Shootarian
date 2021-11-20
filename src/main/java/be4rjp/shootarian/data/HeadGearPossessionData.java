package be4rjp.shootarian.data;

import be4rjp.shootarian.player.costume.HeadGear;

/**
 * ヘッドギアの所持データ
 * 最大2047
 */
public class HeadGearPossessionData extends SavableBitData{
    
    private static final int MAX_SIZE = 2048;
    
    public boolean hasHeadGear(HeadGear headGear){
        return super.getBit(headGear.getSaveNumber());
    }
    
    public void setHeadGear(HeadGear headGear){
        super.setBit(headGear.getSaveNumber(), true);
    }
    
    
    private static void indexCheck(int index){
        if (index >= MAX_SIZE) throw new IllegalArgumentException("The index must be less than " + MAX_SIZE + ".");
    }
}
