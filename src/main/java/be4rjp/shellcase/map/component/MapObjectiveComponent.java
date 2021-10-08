package be4rjp.shellcase.map.component;

import be4rjp.shellcase.map.CanvasBuffer;

import java.util.Random;

public class MapObjectiveComponent extends MapTextComponent {
    
    public MapObjectiveComponent(String text, boolean drawGrayBack, int x, int z, Runnable clickRunnable) {
        super(text, drawGrayBack, x, z, clickRunnable);
    }
    
    
    @Override
    public void setPixels(CanvasBuffer canvasBuffer) {
        if(drawGrayBack) {
            
            int width = minecraftFont.getWidth(rawText);
            int height = minecraftFont.getHeight();
            
            int startXPixel = x - 1;
            int startZPixel = z - 2;
            
            int endXPixel = startXPixel + width + 1;
            int endZPixel = startZPixel + height + 2;
            
            int random = new Random().nextInt((endXPixel - startXPixel) * (endZPixel - startZPixel));
            int index = 0;
            for(int pixelZ = startZPixel; pixelZ <= endZPixel; pixelZ++) {
                for (int pixelX = startXPixel; pixelX <= endXPixel; pixelX++) {
                    int color = canvasBuffer.getPixel(pixelX, pixelZ);
                    color = color >> 2 << 2 | 3;
                    if(pixelX == startXPixel || pixelX == endXPixel || pixelZ == startZPixel || pixelZ == endZPixel){
                        canvasBuffer.setPixel(pixelX, pixelZ, (byte) 56);
                    }else if(index < random) {
                        canvasBuffer.setPixel(pixelX, pixelZ, (byte) 19);
                    } else {
                        canvasBuffer.setPixel(pixelX, pixelZ, (byte) color);
                    }
                    index++;
                }
            }
        }
        
        canvasBuffer.drawText(x, z, minecraftFont, text);
    }
}
