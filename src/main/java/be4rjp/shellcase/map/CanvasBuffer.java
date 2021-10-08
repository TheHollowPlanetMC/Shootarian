package be4rjp.shellcase.map;

import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapFont;

import java.util.Arrays;

public class CanvasBuffer implements Cloneable{
    
    public static CanvasBuffer fromMapCanvas(MapCanvas mapCanvas){
        byte[] buffer = new byte[16384];
        for(int x = 0; x < 128; x++){
            for(int z = 0; z < 128; z++){
                int index = z * 128 + x;
                buffer[index] = mapCanvas.getBasePixel(x, z);
            }
        }
        
        return new CanvasBuffer(buffer);
    }
    
    private final byte[] buffer;
    
    public CanvasBuffer(byte[] buffer){
        this.buffer = buffer;
    }
    
    public byte[] getBuffer() {return buffer;}
    
    @Override
    public CanvasBuffer clone(){
        return new CanvasBuffer(Arrays.copyOf(buffer, buffer.length));
    }
    
    
    public void drawToMapCanvas(MapCanvas mapCanvas){
        for(int x = 0; x < 128; x++){
            for(int z = 0; z < 128; z++){
                int index = z * 128 + x;
                mapCanvas.setPixel(x, z, buffer[index]);
            }
        }
    }
    
    public byte getPixel(int x, int z){
        if (x < 0 || z < 0 || x >= 128 || z >= 128) return 0;
        
        int index = z * 128 + x;
        return buffer[index];
    }
    
    public void setPixel(int x, int z, byte color){
        if (x < 0 || z < 0 || x >= 128 || z >= 128) return;
        
        int index = z * 128 + x;
        buffer[index] = color;
    }
    
    public void drawText(int x, int y, MapFont font, String text) {
        int xStart = x;
        byte color = 57;
        if (!font.isValid(text))
            throw new IllegalArgumentException("text contains invalid characters");
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                x = xStart;
                y += font.getHeight() + 1;
            } else if (ch == '§') {
                int j = text.indexOf(';', i);
                if (j >= 0) {
                    try {
                        color = Byte.parseByte(text.substring(i + 1, j));
                        i = j;
                    } catch (NumberFormatException numberFormatException) {
                        throw new IllegalArgumentException("Text contains unterminated color string");
                    }
                } else {
                    throw new IllegalArgumentException("Text contains unterminated color string");
                }
            } else {
                MapFont.CharacterSprite sprite = font.getChar(text.charAt(i));
                for (int r = 0; r < font.getHeight(); r++) {
                    for (int c = 0; c < sprite.getWidth(); c++) {
                        if (sprite.get(r, c))
                            setPixel(x + c, y + r, color);
                    }
                }
                x += sprite.getWidth() + 1;
            }
        }
    }
}
