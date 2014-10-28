package nzgames.mazegame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import nzgames.mazegame.MainGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class VictoryScreen implements Screen {
//character
    //http://untamed.wild-refuge.net/rmxpresources.php?characters
// accelerometer http://steigert.blogspot.com/2012/04/10-libgdx-tutorial-accelerator-and.html

    TextureRegion tree;
    TextureRegion christmasTree;
    TextureRegion ball;
    TextureRegion snow;
    Stage stage;
    SpriteBatch batch;
    BitmapFont font;
    OrthographicCamera camera;
    MainGame game;
    TextureAtlas atlas;

    public VictoryScreen(MainGame pGame) {
        game = pGame;

        //texture packer puts all the graphics in a single file with an "atlas"
        //to find their coordinates in the file. This locates them.
        atlas = new TextureAtlas(Gdx.files.internal("assets/textures/testPack.txt"));
        christmasTree=atlas.findRegion("tree");
        ball=atlas.findRegion("ball");
        snow=atlas.findRegion("snow");

        //get the spritebatch
        batch = new SpriteBatch();

        //create font
        font = new BitmapFont(Gdx.files.internal("assets/ui/test.fnt"), false);

        // create viewport
        camera=new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        stage=new Stage();
        stage.getViewport().setCamera(camera);


        // our christmas tree
        Image ctree = new Image(christmasTree);
        ctree.setSize(296, 480); // scale the tree to the right size
        ctree.setPosition(-300, 0);
        ctree.addAction(moveTo(400 - 148, 0, 1f));
        ctree.setZIndex(0);
        stage.addActor(ctree);

        //the ornament that rotates in
        Image ballImage = new Image(ball);
        ballImage.setPosition(400 - 148 + 60, 170);

        ballImage.setOrigin(32, 32);
        ballImage.setColor(1, 1, 1, 0);
        ballImage.addAction(
                sequence(delay(1),
                        parallel(
                                fadeIn(1),
                                rotateBy(360, 1)),
                        delay(2f),
                        new Action() {
                            // custom action to switch to the menu screen
                            @Override
                            public boolean act(float delta) {
                                game.setScreen(new MenuScreen(game));
                                return false;
                            }
                        }));

        stage.addActor(ballImage);

        // create the snowflakes
        for (int i = 0; i < 10; i++) {
            spawnSnowflake();
        }
    }

    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // let the stage act and draw
        stage.act(delta);
        stage.draw();

        // draw our text
        batch.begin();
        font.draw(batch, "You did it!!!!", 50, 80);
        batch.end();

    }

    public void spawnSnowflake() {
        final Image snowflake = new Image(snow);
        snowflake.setOrigin(64, 64);
        int x = (int) (Math.random() * 800);
        snowflake.setPosition(x, 480);
        snowflake.setScale((float) (Math.random() * 0.8f + 0.2f));
        snowflake.addAction(parallel(
                forever(rotateBy(360, (float) (Math.random() * 6))),
                sequence(moveTo(x, 0, (float) (Math.random() * 15)),
                        fadeOut((float) (Math.random() * 1)), new Action() { // we
                            // can
                            // define
                            // custom
                            // actions
                            // :)

                            @Override
                            public boolean act(float delta) {
                                snowflake.remove(); // delete this snowflake
                                spawnSnowflake(); // spawn a new snowflake
                                return false;
                            }
                        })));
        stage.addActor(snowflake);
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
        atlas.dispose();

        stage.dispose();
        font.dispose();
    }
}
