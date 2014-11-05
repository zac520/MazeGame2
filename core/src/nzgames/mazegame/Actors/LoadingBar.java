package nzgames.mazegame.Actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import nzgames.mazegame.Handlers.AnimatedImage;
import nzgames.mazegame.Handlers.Box2DVars;
import nzgames.mazegame.MainGame;

/**
 * Created by zac520 on 11/5/14.
 */
public class LoadingBar extends Image {

    MainGame game;
    Animation loadingBarAnimation;
    float FRAME_DURATION;
    TextureRegionDrawable myDrawable;
    protected float stateTime;
    Group graphicsGroup;
    Image loadingBarFrame;
    Image loadingBackground;
    private int loadingBarFullSize;
    Label gameProgressMessageLabel;

    public LoadingBar(MainGame myGame) {
        //super(new TextureRegion(myGameScreen.atlas.findRegion("MainCharLeft")));
        this.game = myGame;

        //set the size the full loading bar will be
        loadingBarFullSize = game.SCREEN_WIDTH/2;

        //set the blue bar's initial size and location
        this.setSize(0, game.SCREEN_HEIGHT/15);//start with 0 width
        this.setCenterPosition(-loadingBarFullSize/2, 0);

        //load the animations
        loadingBarAnimation = new Animation(FRAME_DURATION, game.loadingAtlas.findRegions("blueBar"));

        //create the frame to hold the bar
        loadingBarFrame = new Image(game.loadingAtlas.findRegion("BlackContainer"));
        loadingBarFrame.setSize(loadingBarFullSize,this.getHeight()*1.15f);
        loadingBarFrame.setCenterPosition(0,0);

        //create the background to cover the entire screen
        loadingBackground = new Image(game.loadingAtlas.findRegion("background"));
        loadingBackground.setSize(game.SCREEN_WIDTH,game.SCREEN_HEIGHT);
        loadingBackground.setCenterPosition(0,0);

        //set the current drawable to the animation
        myDrawable = new TextureRegionDrawable(loadingBarAnimation.getKeyFrame(this.getStateTime(), true));

        //create a label to show progress
        gameProgressMessageLabel = new Label(game.loadingProgress, game.popupSkin);
        gameProgressMessageLabel.setWrap(true);
        gameProgressMessageLabel.setFontScale(game.font.getScaleX());
        gameProgressMessageLabel.setWidth(game.SCREEN_WIDTH/2);//half the screen width
        gameProgressMessageLabel.setCenterPosition(0,- game.SCREEN_HEIGHT/10);
        gameProgressMessageLabel.setAlignment(Align.center);

        //add this class to a graphics group so that we can append to it later
        graphicsGroup = new Group();
        graphicsGroup.addActor(loadingBackground);
        graphicsGroup.addActor(this);
        graphicsGroup.addActor(loadingBarFrame);
        graphicsGroup.addActor(gameProgressMessageLabel);
        graphicsGroup.setCenterPosition(
                game.SCREEN_WIDTH/2,
                game.SCREEN_HEIGHT/2);
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

        this.setWidth(loadingBarFullSize * game.loadingProgressPercent);

        myDrawable.setRegion(loadingBarAnimation.getKeyFrame(getStateTime(), true));

        //update the label
        gameProgressMessageLabel.setText(game.loadingProgress);

        this.setDrawable(myDrawable);


    }
    public Group getGroup(){
        return graphicsGroup;
    }
    public float getStateTime(){
        return stateTime;
    }
}
