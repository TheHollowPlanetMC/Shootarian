package be4rjp.shootarian.data;

import be4rjp.shootarian.data.progress.ProgressData;
import be4rjp.shootarian.player.ShootarianPlayer;

public class AchievementData {
    
    private final ShootarianPlayer shootarianPlayer;
    
    private int point = 0;
    private int kill = 0;
    private int rank = 0;
    private int coin = 0;
    
    private final GunWeaponPossessionData gunWeaponPossessionData;
    private final HeadGearPossessionData headGearPossessionData;
    private final GadgetPossessionData gadgetPossessionData;
    private final ProgressData progressData = new ProgressData();
    private final QuestProgress questProgress;
    
    public AchievementData(ShootarianPlayer shootarianPlayer){
        this.shootarianPlayer = shootarianPlayer;
        
        this.gunWeaponPossessionData = shootarianPlayer.getWeaponPossessionData();
        this.headGearPossessionData = shootarianPlayer.getHeadGearPossessionData();
        this.gadgetPossessionData = shootarianPlayer.getGadgetPossessionData();
        this.questProgress = shootarianPlayer.getQuestProgress();
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
    
    public GadgetPossessionData getGadgetPossessionData() {return gadgetPossessionData;}
    
    public ProgressData getProgressData() {return progressData;}
    
    public QuestProgress getQuestProgress() {return questProgress;}
    
    public ShootarianPlayer getShootarianPlayer() {return shootarianPlayer;}
}
