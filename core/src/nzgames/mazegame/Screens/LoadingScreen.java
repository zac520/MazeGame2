package nzgames.mazegame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import nzgames.mazegame.Handlers.AnimatedImage;
import nzgames.mazegame.MainGame;

/**
 * Created by zac520 on 11/4/14.
 */
public class LoadingScreen implements Screen {
    OrthographicCamera camera;
    MainGame game;
    SpriteBatch batch;
    BitmapFont font;
    Stage stage;

    private float FRAME_DURATION = 0.2f;
    private Animation loadingAnimation;

    public LoadingScreen(MainGame myGame){


        //todo turn this into an actor, not a screen. This way we can just have a popup that is the bar, not a switch in view.
        //we can then switch views if we want, and just pull in the actor
        game = myGame;

        //initialize camera
        camera = new OrthographicCamera();
        //must use the actual height in this case. This way the camera will go beyond the play area, so ads will not overlap
        camera.setToOrtho(false, game.SCREEN_WIDTH, Gdx.graphics.getHeight());

        //initialize stage
        stage = game.loadingStage;
        stage.getViewport().setCamera(camera);

        //start the sprite batch and font
        batch = game.loadingBatch;
        font = game.font;


        loadingAnimation = new Animation(FRAME_DURATION, game.loadingAtlas.findRegions("blueBar"));
        loadingAnimation.setPlayMode(Animation.PlayMode.LOOP);
        AnimatedImage myAnimatedImage = new AnimatedImage(loadingAnimation);
        myAnimatedImage.setCenterPosition(game.SCREEN_WIDTH / 2, game.SCREEN_HEIGHT / 2);
        stage.addActor(myAnimatedImage);




    }

    @Override
    public void render(float delta) {

        //reset the background color
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
