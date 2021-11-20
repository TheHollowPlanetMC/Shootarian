package be4rjp.shootarian.data.progress;

import be4rjp.shootarian.data.SavableBitData;

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
