package be4rjp.shellcase.player.passive;

import be4rjp.shellcase.weapon.gun.GunStatusData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PassiveInfluence {
    
    private final Map<Passive, Float> passiveInfluenceMap;
    
    public PassiveInfluence(){
        passiveInfluenceMap = new ConcurrentHashMap<>();
        
        for(Passive passive : Passive.values()){
            passiveInfluenceMap.put(passive, 0.0F);
        }
    }
    
    /*
    public void createPassiveInfluence(ShellCasePlayer ShellCasePlayer){
        List<Passive> passiveList = new ArrayList<>();
        
        //プレイヤーについているギアのパッシブ効果を取得
        ShellCasePlayer.getGearList().forEach(gear -> passiveList.add(gear.getPassive()));
    
        //メイン武器のパッシブ効果を取得
        WeaponClass weaponClass = ShellCasePlayer.getWeaponClass();
        if(weaponClass != null){
            if(weaponClass.getMainWeapon() != null){
                passiveList.addAll(weaponClass.getMainWeapon().getPassiveList());
            }
        }
    }*/
    
    public void createPassiveInfluence(GunStatusData gunStatusData){
        if(gunStatusData.getSight() != null) this.addPassive(gunStatusData.getSight().getPassiveInfluenceMap());
    }

    private void addPassive(Map<Passive, Float> passiveFloatMap){
        for(Map.Entry<Passive, Float> entry : passiveFloatMap.entrySet()){
            Passive passive = entry.getKey();
            float influence = passiveInfluenceMap.get(passive);
            influence += passiveFloatMap.get(passive);
            passiveInfluenceMap.put(passive, influence);
        }
    }

    
    /**
     * 指定されたパッシブの影響倍率を取得します
     * @param passive 取得したいパッシブ効果
     * @return パッシブの影響倍率
     */
    public float getInfluence(Passive passive){
        return passiveInfluenceMap.get(passive);
    }
}
