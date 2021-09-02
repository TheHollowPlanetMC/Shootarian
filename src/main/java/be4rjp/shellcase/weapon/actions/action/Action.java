package be4rjp.shellcase.weapon.actions.action;

import be4rjp.shellcase.player.ShellCasePlayer;

public interface Action {
    
    /**
     * リロードアクション
     * @param reloadPlayer リロードをしているプレイヤー
     * @param tick 再生するtick
     */
    void play(ShellCasePlayer reloadPlayer, int tick);
    
}
