package be4rjp.shellcase.data.progress;

import be4rjp.shellcase.data.SavableBitData;

import java.util.BitSet;

public class ProgressData extends SavableBitData {
    
    /**
     * 進捗を達成しているかどうか
     * @return
     */
    public boolean isAchievedProgress(Progress progress){
        return super.getBit(progress.getSaveNumber());
    }
    
    
    /**
     * 進捗を達成させる
     * @param progress
     */
    public void setAchievedProgress(Progress progress){
        super.setBit(progress.getSaveNumber(), true);
    }

}
