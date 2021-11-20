package be4rjp.shootarian.data.progress;

/**
 * 最大512
 */
public enum Progress {
    COMPLETE_TUTORIAL(0);

    private final int saveNumber;

    Progress(int saveNumber){
        this.saveNumber = saveNumber;
    }

    public int getSaveNumber() {return saveNumber;}

    public static Progress getBySaveNumber(int saveNumber){
        for(Progress progress : Progress.values()){
            if(progress.getSaveNumber() == saveNumber) return progress;
        }
        return null;
    }
}
