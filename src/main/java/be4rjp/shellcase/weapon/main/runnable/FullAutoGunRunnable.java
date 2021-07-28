package be4rjp.shellcase.weapon.main.runnable;

import be4rjp.shellcase.entity.BulletEntity;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.ShellCaseSound;
import be4rjp.shellcase.weapon.GunStatusData;
import be4rjp.shellcase.weapon.main.FullAutoGun;
import be4rjp.shellcase.weapon.recoil.Recoil;
import be4rjp.shellcase.weapon.recoil.RecoilPattern;
import be4rjp.shellcase.weapon.reload.ReloadRunnable;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Timer;
import java.util.TimerTask;

public class FullAutoGunRunnable extends GunWeaponRunnable {
    
    private static final ShellCaseSound NO_BULLET_SOUND = new ShellCaseSound(Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, 0.8F, 1.2F);
    
    private final FullAutoGun fullAutoGun;
    private final GunStatusData gunStatusData;
    private final Timer timer;
    private boolean setup = false;

    private RecoilPattern adsRecoilPattern;
    
    
    public FullAutoGunRunnable(FullAutoGun fullAutoGun, GunStatusData gunStatusData, ShellCasePlayer shellCasePlayer){
        super(fullAutoGun, shellCasePlayer);
        this.fullAutoGun = fullAutoGun;
        this.gunStatusData = gunStatusData;
        this.timer = new Timer(true);

        this.adsRecoilPattern = fullAutoGun.getADSRecoil().getRandomPattern();
        
        soundTick = fullAutoGun.getShootTick() % 2 == 0 ? fullAutoGun.getShootTick() / 2 : fullAutoGun.getShootTick() / 2 + 1;
    }
    
    private int taskTick = 0;
    private int clickTick = 0;
    private int noClickTick = 0;
    
    private int soundTaskTick = 0;
    private boolean using = false;
    private boolean beforeUsing = false;
    private boolean noInk = false;
    private final int soundTick;
    
    @Override
    public void run() {
        if(!setup){
            setup = true;
            
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    using = playerTick <= 12;

                    if(beforeUsing != using){
                        adsRecoilPattern = fullAutoGun.getADSRecoil().getRandomPattern();
                    }
                    beforeUsing = using;
                    
                    if(taskTick % fullAutoGun.getShootTick() == 0){
                        if(using && !gunStatusData.isReloading()) {
                            Player player = shellCasePlayer.getBukkitPlayer();
                            if (player == null) return;
                            
                            if(gunStatusData.consumeBullets(1)) {
                                ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
                                if(shellCaseTeam != null){
                                    shellCaseTeam.getMatch().playSound(fullAutoGun.getShootSound(), shellCasePlayer.getLocation());
                                }
                                gunStatusData.updateGunDisplayName(shellCasePlayer);
                                
                                //射撃
                                Vector direction = player.getEyeLocation().getDirection();
                                Location origin = player.getEyeLocation();
    
                                BulletEntity bulletEntity = new BulletEntity(shellCasePlayer.getShellCaseTeam(), origin, fullAutoGun);
                                double range = fullAutoGun.getRecoil().getShootRandomRange(clickTick);
                                Vector randomVector = new Vector(Math.random() * range - range / 2, 0, Math.random() * range - range / 2);
                                bulletEntity.shootInitialize(shellCasePlayer, direction.multiply(fullAutoGun.getShootSpeed()).add(randomVector), fullAutoGun.getFallTick());
                                bulletEntity.spawn();
                            }
                        }else{
                            noClickTick += fullAutoGun.getShootTick();
                            if(noClickTick >= fullAutoGun.getRecoil().getResetTick()) {
                                noClickTick = 0;
                                clickTick = 0;
                            }
                        }
                    }
                    
                    
                    playerTick++;
                    taskTick++;
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 25);
        }
        
        if(soundTaskTick % soundTick == 0) {
            if (using) {
                if(noInk && !gunStatusData.isReloading()){
                    shellCasePlayer.playSound(NO_BULLET_SOUND);
                    gunStatusData.reload();
                }else{
                    shellCasePlayer.playSound(fullAutoGun.getShootSound());
                }
            }
        }
        soundTaskTick++;
    }
    
    
    @Override
    public synchronized void cancel() throws IllegalStateException {
        try {
            timer.cancel();
        }catch (Exception e){e.printStackTrace();}
        super.cancel();
    }
}
