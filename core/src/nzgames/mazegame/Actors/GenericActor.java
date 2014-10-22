package nzgames.mazegame.Actors;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import nzgames.mazegame.MainGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by zac520 on 8/13/14.
 */
public class GenericActor extends Image {

    /**
     * Select enemy variables*
     */
    public boolean selected = false;
    public Group graphicsGroup;//used to link in the arrow later
    TextureRegion downArrow;
    Image downArrowImage;
    public final int TYPE_HIT_POINTS = 0;
    public final int TYPE_MAGIC = 1;
    public final int TYPE_MONEY = 2;


    /**
     * How GenericEnemy relates to world*
     */
    protected float worldWidth;
    protected float worldHeight;
    protected float stateTime;
    protected Body body;
    protected Array<Fixture> actorFixtures;
    public boolean facingRight = false;
    protected MainGame game;

    /**
     * Animation*
     */
    protected Animation rightAnimation;
    protected Animation leftAnimation;
    protected TextureRegionDrawable myDrawable;
    protected TextureRegion downFacingFrame; //used for turning animation
    protected TextureRegion damage;
    public Image currentHPImage;
    public Image hpBarImage;


    /**
     * Movement *
     */
    float previousPositionX;
    Vector2 dir = new Vector2();
    float dist = 0;
    float maxDist = 0;
    float forwardForce = 0;
    float timeSpentTryingDirection = 0;
    float timeInterval = 0;
    boolean playDownFrame = false;

    /**
     * Reference to self*
     */
    GenericActor genericActor;

    /**
     * Character Attributes *
     */
    protected int hitPoints;
    protected int maxHitPoints;
    protected int magicPoints;
    protected int maxMagicPoints;
    protected int contactDamage; //damage player gets when he contacts enemy
    public float percentHitPointsRemaining;
    public float percentMagicPointsRemaining;
    protected float maxHPImageWidth;
    protected int money = 0;
    public boolean canCauseDamage = true;

    public GenericActor() {
        //make the actor a button for the user to select for targeting
        genericActor = this;

    }

    public float getStateTime() {
        return stateTime;
    }

    public Body getBody() {
        return body;
    }
}
