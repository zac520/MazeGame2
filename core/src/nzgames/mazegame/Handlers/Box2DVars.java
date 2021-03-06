package nzgames.mazegame.Handlers;

/**
 * Created by zac520 on 7/17/14.
 */
public class Box2DVars {
    //pixel per meter
    public static final float PPM = 10;

    //category bits
    //use only powers of 2
    public static final short BIT_PLAYER = 2;
    public static final short BIT_WALL = 4;
    public static final short BIT_SENSOR = 8;
    public static final short BIT_BLUE = 16;
    public static final short BIT_CRYSTAL = 32;
    public static final short BIT_AWAKE = 64;
    public static final short BIT_ENEMY = 128;

}
