package be4rjp.shootarian.data;

/**
 * SQLに保存可能なデータ
 */
public interface SavableData {
    
    /**
     * byte配列にデータを書き込みます
     */
    byte[] write_to_byte_array();
    
    /**
     * byte配列からデータを読み込みます
     */
    void load_from_byte_array(byte[] data);
}
