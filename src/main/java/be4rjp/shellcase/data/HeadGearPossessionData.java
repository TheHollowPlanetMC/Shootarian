package be4rjp.shellcase.data;

import be4rjp.shellcase.player.costume.HeadGear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeadGearPossessionData extends SavableByteData{
    
    private final List<HeadGear> headGearList = new ArrayList<>();
    
    public HeadGearPossessionData() {
        super(256);
    }
    
    /**
     * 所持しているヘッドギアのデータを返す
     * @param index インデックス (0 ~ 511)
     * @return
     */
    public HeadGear getHeadGear(int index){
        indexCheck(index);
        if(headGearList.size() <= index) return null;
        return headGearList.get(index);
    }
    
    /**
     * SQLに書き込むためのbyte配列を作成する
     */
    public void writeToByteArray(){
        for(int index = 0; index < 256; index++){
            HeadGear headGear = this.getHeadGear(index);
            if(headGear == null) break;
    
            bytes[index] = (byte) (headGear.getSaveNumber() & 0xFF);
        }
    }
    
    /**
     * SQLからロードしたbyte配列から読み込む
     */
    public void loadFromByteArray(){
        this.headGearList.clear();
        
        for(int index = 0; index < 256; index++){
            byte[] data = Arrays.copyOfRange(bytes, index * 5, index * 5 + 5);
            
            if(data[0] == 0 && data[1] == 0) break;
    
            int headGearNumber = (data[1] & 0xFF);
            HeadGear headGear = HeadGear.getHeadGearBySaveNumber(headGearNumber);
            
            this.headGearList.add(headGear);
        }
    }
    
    /**
     * ヘッドギアのデータを追加する。最大で256個まで追加できる
     * @param headGear
     * @return 追加に成功すれば true
     */
    public boolean addHeadGear(HeadGear headGear){
        if(headGearList.size() >= 256) return false;
        
        headGearList.add(headGear);
        return true;
    }
    
    /**
     * ヘッドギアのデータのリストをコピーして返す
     * @return List<HeadGearData>
     */
    public List<HeadGear> getHeadGearList() {
        return new ArrayList<>(headGearList);
    }
    
    private static void indexCheck(int index){
        if (index >= 256) throw new IllegalArgumentException("The index must be less than 256.");
    }
    
    
    @Override
    public byte[] write_to_byte_array() {
        this.writeToByteArray();
        return super.write_to_byte_array();
    }
    
    
    @Override
    public void load_from_byte_array(byte[] data) {
        super.load_from_byte_array(data);
        this.loadFromByteArray();
    }
}
