package be4rjp.shootarian.entity;

/**
 * 非同期で処理する独自のエンティティ
 */
public interface ShootarianEntity {
    
    double ENTITY_DRAW_DISTANCE = 40.0;
    double ENTITY_DRAW_DISTANCE_SQUARE = ENTITY_DRAW_DISTANCE * ENTITY_DRAW_DISTANCE;
    
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
