package be4rjp.shellcase.player.passive;


public enum Passive {
    
    NONE("nothing"), //効果なし
    RUN_SPEED("passive-run-speed"), //スピードアップ
    HORIZONTAL_RECOIL("passive-horizontal-recoil"), //横反動
    VERTICAL_RECOIL("passive-vertical-recoil"); //縦反動
    
    public final String displayName;
    
    Passive(String displayName){
        this.displayName = displayName;
    }
    
}
