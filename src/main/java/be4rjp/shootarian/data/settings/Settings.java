package be4rjp.shootarian.data.settings;

public enum Settings {

    BULLET_ORBIT_PARTICLE(0x1, true),
    SNIPER_BULLET_ORBIT_PARTICLE(0x2, true);

    private final int bitMask;
    private final boolean defaultSetting;

    Settings(int bitMask, boolean defaultSetting){
        this.bitMask = bitMask;
        this.defaultSetting = defaultSetting;
    }

    public int getBitMask() {return bitMask;}

    public boolean getDefaultSetting() {return defaultSetting;}
}
