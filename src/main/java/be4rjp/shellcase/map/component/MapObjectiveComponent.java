package be4rjp.shellcase.map.component;

import be4rjp.shellcase.map.CanvasBuffer;
import be4rjp.shellcase.match.map.area.FlagAreaData;
import be4rjp.shellcase.match.team.ShellCaseTeam;

import java.util.Random;

public class MapObjectiveComponent extends MapTextComponent {
    
    private final FlagAreaData flagAreaData;
    
    public MapObjectiveComponent(FlagAreaData flagAreaData, boolean drawGrayBack, int x, int z, MapClickRunnable clickRunnable) {
        super(" " + flagAreaData.getFlagArea().getDisplayName().toCharArray()[0] + " ", drawGrayBack, x, z, clickRunnable);
        this.flagAreaData = flagAreaData;
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
            
            int progress = (int) (((double) Math.abs(flagAreaData.getTerritory()) / 100.0) * (double) ((endXPixel - startXPixel) * (endZPixel - startZPixel))) + Math.abs(endXPixel - startXPixel);
            int index = 0;
    
            ShellCaseTeam shellCaseTeam = flagAreaData.getTeam();
            byte teamColor = shellCaseTeam == null ? (byte) 32 : shellCaseTeam.getShellCaseColor().getCanvasColor();
            
            for(int pixelZ = startZPixel; pixelZ <= endZPixel; pixelZ++) {
                for (int pixelX = startXPixel; pixelX <= endXPixel; pixelX++) {
                    int color = canvasBuffer.getPixel(pixelX, pixelZ);
                    color = color >> 2 << 2 | 3;
                    if(pixelX == startXPixel || pixelX == endXPixel || pixelZ == startZPixel || pixelZ == endZPixel){
                        canvasBuffer.setPixel(pixelX, pixelZ, (byte) 56);
                    }else if(index < progress) {
                        canvasBuffer.setPixel(pixelX, pixelZ, teamColor);
                    } else {
                        canvasBuffer.setPixel(pixelX, pixelZ, (byte) color);
                    }
                    index++;
                }
            }
        }
        
        canvasBuffer.drawText(x, z, minecraftFont, text);
    }
    
    public FlagAreaData getFlagAreaData() {return flagAreaData;}
}
