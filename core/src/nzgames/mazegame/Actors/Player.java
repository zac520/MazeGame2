package nzgames.mazegame.Actors;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import nzgames.mazegame.Handlers.Box2DVars;
import nzgames.mazegame.MainGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by zac520 on 8/10/14.
 */
public class Player extends GenericActor {

    public static  int PLAYER_MAX_SPEED = 2000;
    public int FORWARD_FORCE = 1;//will be reset based on player weight
    public static  float RUNNING_FRAME_DURATION = 0.15f;

    private Animation swordSlashAnimationRight;
    private Animation swordSlashAnimationLeft;
    public boolean isSlashingSword = false;
    public boolean ableToSlash = true;
    public boolean isMoving = true;
    Fixture swordSlashRight;
    Fixture swordSlashLeft;
    Filter enemiesHitable;
    Filter enemiesNotHitable;

    public Animation spellAnimation; //I think we will put these in their own class later.

    public Player(MainGame myGame, Body body, float myWidth, float myHeight){

        //super(new TextureRegion(myGameScreen.atlas.findRegion("MainCharLeft")));

        this.body = body;
        this.worldHeight = myHeight * Box2DVars.PPM;
        this.worldWidth = myWidth * Box2DVars.PPM;

        this.game = myGame;

        //set the forward force to be multipled by player mass for consistency
        this.FORWARD_FORCE =  FORWARD_FORCE * (int) this.body.getMass();

        //load the animations
        leftAnimation = new Animation(RUNNING_FRAME_DURATION, game.atlas.findRegions("Mouse"));

        //set the current drawable to the animation
        myDrawable = new TextureRegionDrawable(leftAnimation.getKeyFrame(this.getStateTime(), true));

        //get the size to match the body
        this.setSize(worldWidth, worldHeight);
        //set the origin so we can rotate by the center
        this.setOrigin(this.getWidth()/2, this.getHeight()/2);


        //add this class to a graphics group so that we can append to it later
        graphicsGroup = new Group();
        graphicsGroup.addActor(this);
        graphicsGroup.setCenterPosition(
                body.getPosition().x * Box2DVars.PPM,
                body.getPosition().y * Box2DVars.PPM);

    }
    public void update(float delta) {
        stateTime += delta;
    }

    @Override
    public void act(float delta) {

        //allow the movement, etc that is set on creation elsewhere to run
        super.act(delta);

        //update the time for this class
        this.update(delta);



        if (isMoving) {
            myDrawable.setRegion(facingRight ? leftAnimation.getKeyFrame(getStateTime(), true) : leftAnimation.getKeyFrame(getStateTime(), true));
        } else {
            myDrawable.setRegion(facingRight ? leftAnimation.getKeyFrame(0, true) : leftAnimation.getKeyFrame(0, true));
        }

        this.setDrawable(myDrawable);

        //update the image position to match the box2d position
        graphicsGroup.setPosition(
                body.getPosition().x * Box2DVars.PPM - (worldWidth / 2),
                body.getPosition().y * Box2DVars.PPM - (worldHeight / 2));

        //update the rotation
        if(calculateAngleWeAreMoving()>=0){
            //System.out.println(calculateAngleWeAreMoving());
            this.setRotation(calculateAngleWeAreMoving()-90);

        }

    }

    //moving directly to the right is zero degrees
    private float calculateAngleWeAreMoving(){



        //body is moving predominantly left or right
        if (Math.abs(body.getLinearVelocity().x) > Math.abs(body.getLinearVelocity().y)){
            if(body.getLinearVelocity().x > 0){
                return 0;
            }
            else{
                return 180;
            }
        }

        else{
            if(body.getLinearVelocity().y>0){
                return 90;
            }
            else{
                return 270;
            }
        }

//        //http://stackoverflow.com/questions/22421054/determine-movement-vectors-direction-from-velocity
//
//        float addDeg = 0;
//        if(body.getLinearVelocity().x<0) {
//            addDeg = body.getLinearVelocity().y > 0 ? 180 : 270;
//        }
//        else if(body.getLinearVelocity().y<=0){
//            addDeg = 360;
//        }
//
//        return ((float)Math.abs(
//                Math.abs(
//                        Math.atan(
//                                body.getLinearVelocity().y/body.getLinearVelocity().x)*180/Math.PI)-addDeg));
    }


    public Group getGroup(){
        return graphicsGroup;
    }
    public Body getBody(){
        return body;
    }
    public boolean isFacingLeft(){
        return facingRight;
    }
    public boolean getIsWalking(){
        return isMoving;
    }
    public int getHitPoints(){
        return hitPoints;
    }
    public float getStateTime(){
        return stateTime;
    }
}