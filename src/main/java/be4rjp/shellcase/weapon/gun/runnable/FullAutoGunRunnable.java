package be4rjp.shellcase.weapon.gun.runnable;

import be4rjp.cinema4c.util.Vec2f;
import be4rjp.shellcase.entity.BulletEntity;
import be4rjp.shellcase.match.team.ShellCaseTeam;
import be4rjp.shellcase.player.ShellCasePlayer;
import be4rjp.shellcase.util.ShellCaseSound;
import be4rjp.shellcase.weapon.gun.GunStatusData;
import be4rjp.shellcase.weapon.ShellCaseWeapon;
import be4rjp.shellcase.weapon.gun.FullAutoGun;
import be4rjp.shellcase.weapon.recoil.RecoilPattern;
import net.minecraft.server.v1_15_R1.PacketPlayOutPosition;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class FullAutoGunRunnable extends GunWeaponRunnable {
    
    private static final ShellCaseSound NO_BULLET_SOUND = new ShellCaseSound(Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, 0.8F, 1.2F);
    private static final Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> teleportFlags;
    
    static {
        teleportFlags = new HashSet<>(Arrays.asList(PacketPlayOutPosition.EnumPlayerTeleportFlags.values()));
    }
    
    private final FullAutoGun fullAutoGun;
    private final GunStatusData gunStatusData;
    private final Timer timer;
    private boolean setup = false;

    private RecoilPattern adsRecoilPattern;
    private RecoilPattern normalRecoilPattern;
    
    
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
    
    private int shootIndex = 0;
    
    private int soundTaskTick = 0;
    private boolean using = false;
    private boolean beforeUsing = false;
    private boolean noBullet = false;
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
                        gunStatusData.resetRecoil();
                        shootIndex = 0;
                    }
                    beforeUsing = using;
                    
                    if(taskTick % fullAutoGun.getShootTick() == 0){
                        if(using && !gunStatusData.isReloading()) {
                            Player player = shellCasePlayer.getBukkitPlayer();
                            if (player == null) return;
                            
                            if(gunStatusData.consumeBullets(1)) {
                                noBullet = false;
                                
                                gunStatusData.updateDisplayName(shellCasePlayer);
                                
                                //射撃
                                Vector direction = player.getEyeLocation().getDirection();
                                Location origin = player.getEyeLocation();
    
                                BulletEntity bulletEntity = new BulletEntity(shellCasePlayer.getShellCaseTeam(), origin, fullAutoGun);
                                double range = fullAutoGun.getHipShootingRecoil().getShootRandomRange(clickTick);
                                Vector randomVector = new Vector(Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2);
                                if(shellCasePlayer.isADS()) randomVector = new Vector(0.0, 0.0, 0.0);
                                bulletEntity.shootInitialize(shellCasePlayer, direction.clone().add(randomVector).multiply(fullAutoGun.getShootSpeed()), fullAutoGun.getFallTick());
                                bulletEntity.spawn();
                                
                                //反動
                                if(shellCasePlayer.isADS()){
                                    gunStatusData.getAdsRecoil().sendRecoil(shellCasePlayer, shootIndex);
                                }else{
                                    gunStatusData.getNormalRecoil().sendRecoil(shellCasePlayer, shootIndex);
                                    
                                    clickTick += fullAutoGun.getShootTick();
                                }
                                shootIndex++;
                                
                            }else{
                                noBullet = true;
                                gunStatusData.reload();
                            }
                        }else{
                            noClickTick += fullAutoGun.getShootTick();
                            if(noClickTick >= fullAutoGun.getHipShootingRecoil().getResetTick()) {
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
                if(noBullet){
                    if(!gunStatusData.isReloading()) shellCasePlayer.playSound(NO_BULLET_SOUND);
                }else{
                    ShellCaseTeam shellCaseTeam = shellCasePlayer.getShellCaseTeam();
                    if(shellCaseTeam != null && !gunStatusData.isReloading()){
                        shellCaseTeam.getMatch().playSound(gunStatusData.getGunWeapon().getShootSound());
                    }
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
