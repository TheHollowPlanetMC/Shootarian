package be4rjp.shellcase.data;

import be4rjp.shellcase.data.progress.ProgressData;
import be4rjp.shellcase.player.ShellCasePlayer;

public class AchievementData {
    
    private final ShellCasePlayer ShellCasePlayer;
    
    private int point = 0;
    private int kill = 0;
    private int rank = 0;
    private int coin = 0;
    
    private final GunWeaponPossessionData gunWeaponPossessionData;
    private final HeadGearPossessionData headGearPossessionData;
    private final ProgressData progressData = new ProgressData();
    
    public AchievementData(ShellCasePlayer ShellCasePlayer){
        this.ShellCasePlayer = ShellCasePlayer;
        
        this.gunWeaponPossessionData = ShellCasePlayer.getWeaponPossessionData();
        this.headGearPossessionData = ShellCasePlayer.getHeadGearPossessionData();
    }
    
    public int getKill() {return kill;}
    
    public int getPoint() {return point;}
    
    public int getRank() {return rank;}
    
    public synchronized int getCoin() {return coin;}
    
    public void addKill(int kill){this.kill += kill;}
    
    public void addPoint(int point){this.point += point;}
    
    public void addRank(int rank){this.rank += rank;}
    
    public synchronized void addCoin(int coin){this.coin += coin;}
    
    public void setKill(int kill) {this.kill = kill;}
    
    public void setPoint(int point) {this.point = point;}
    
    public void setRank(int rank) {this.rank = rank;}
    
    public synchronized void setCoin(int coin) {this.coin = coin;}
    
    public HeadGearPossessionData getHeadGearPossessionData() {return headGearPossessionData;}
    
    public GunWeaponPossessionData getWeaponPossessionData() {return gunWeaponPossessionData;}

    public ProgressData getProgressData() {return progressData;}

    public ShellCasePlayer getShellCasePlayer() {return ShellCasePlayer;}
}
