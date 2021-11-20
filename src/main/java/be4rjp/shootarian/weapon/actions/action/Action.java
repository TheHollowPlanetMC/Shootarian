package be4rjp.shootarian.weapon.actions.action;

import be4rjp.shootarian.player.ShootarianPlayer;

public interface Action {
    
    /**
     * リロードアクション
     * @param reloadPlayer リロードをしているプレイヤー
     * @param tick 再生するtick
     */
    void play(ShootarianPlayer reloadPlayer, int tick);
    
}
