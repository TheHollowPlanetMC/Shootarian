package be4rjp.shootarian.data;

/**
 * クエストの進捗
 * IDで管理され、最大数は 16384 - 1
 */
public class QuestProgress extends SavableBitData{
    
    private static final int MAX_SIZE = 16383;
    
    private static void indexCheck(int index){
        if (index >= MAX_SIZE) throw new IllegalArgumentException("The index must be less than " + MAX_SIZE + ".");
    }
    
    
    public boolean getFlag(int questFlagID){
        indexCheck(questFlagID);
        return getBit(questFlagID);
    }
    
    public void setFlag(int questFlagID, boolean flag){
        indexCheck(questFlagID);
        setBit(questFlagID, flag);
    }
    
}
