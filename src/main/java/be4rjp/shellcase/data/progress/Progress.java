package be4rjp.shellcase.data.progress;

public enum Progress {
    COMPLETED_TUTORIAL(0);

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
