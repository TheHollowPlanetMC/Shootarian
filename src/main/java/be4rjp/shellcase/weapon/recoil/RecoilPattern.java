package be4rjp.shellcase.weapon.recoil;

import be4rjp.cinema4c.util.Vec2f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecoilPattern {

    private Map<Integer, Vec2f> recoils = new HashMap<>();

    public RecoilPattern(List<String> lines){
        int index = 0;
        for(String line : lines){
            String[] args = line.replace(" ", "").split(",");
            float x = Float.parseFloat(args[0]);
            float y = Float.parseFloat(args[1]);
            Vec2f vec2f = new Vec2f(x, y);

            recoils.put(index, vec2f);
            index++;
        }
    }

    public Vec2f get(int index){
        Vec2f vec2f = recoils.get(index);
        if(vec2f == null) return new Vec2f(0.0F, 0.0F);
        return vec2f;
    }
}
