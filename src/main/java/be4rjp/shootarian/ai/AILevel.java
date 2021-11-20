package be4rjp.shootarian.ai;

public enum AILevel {
    
    EASY(30),
    NORMAL(50),
    HARD(80),
    VERY_HARD(120),
    EXTREME(200);
    
    private final double enemyFindRange;
    
    AILevel(double enemyFindRange){
        this.enemyFindRange = enemyFindRange;
    }
    
    public double getEnemyFindRange() {return enemyFindRange;}
}
