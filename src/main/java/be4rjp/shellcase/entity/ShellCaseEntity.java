package be4rjp.shellcase.entity;

/**
 * 非同期で処理する独自のエンティティ
 */
public interface ShellCaseEntity {
    
    double ENTITY_DRAW_DISTANCE_SQUARE = 800.0;
    
    /**
     * 非同期で1tickごとに実行する処理
     */
    void tick();
    
    /**
     * エンティティのID
     * @return int EntityID
     */
    int getEntityID();
    
    /**
     * スポーンさせる
     */
    void spawn();
    
    /**
     * デスポーンさせる
     */
    void remove();
    
    /**
     * デスポーンした後かどうか
     */
    boolean isDead();
}
