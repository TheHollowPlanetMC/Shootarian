package be4rjp.shellcase.weapon.attachment;

import java.util.Objects;

public class Sight extends Attachment{
    
    private String unicode = "";
    private float fov = 0.1F;
    
    public Sight(String id) {
        super(id);
    }
    
    @Override
    public void loadDetailsData() {
        if(yml.contains("sight-unicode")){
            int[] charArray = new int[]{Integer.parseInt(Objects.requireNonNull(yml.getString("sight-unicode")), 16)};
            this.unicode = new String(charArray, 0, charArray.length);
        }
        if(yml.contains("field-of-view")) this.fov = (float) yml.getDouble("field-of-view");
    }
    
    public String getUnicode() {return unicode;}
    
    public float getFOV() {return fov;}
}
