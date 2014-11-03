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

import java.util.concurrent.TimeUnit;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class VictoryScreen implements Screen {
//character
    //http://untamed.wild-refuge.net/rmxpresources.php?characters
// accelerometer http://steigert.blogspot.com/2012/04/10-libgdx-tutorial-accelerator-and.html

    TextureRegion tree;
    TextureRegion uprightMouse;
    //TextureRegion ball;
    TextureRegion cheese;
    Stage stage;
    SpriteBatch batch;
    BitmapFont font;
    OrthographicCamera camera;
    MainGame game;
    TextureAtlas atlas;

    private boolean newRecord = false;
    private float playTime;
    private int score;
    private int numberOfJumps;
    public VictoryScreen(MainGame pGame, float timeToFinish,int myScore, boolean newRecordStatus) {
        game = pGame;

        playTime = timeToFinish;
        newRecord = newRecordStatus;
        score = myScore;
        numberOfJumps = myScore /100;
        //texture packer puts all the graphics in a single file with an "atlas"
        //to find their coordinates in the file. This locates them.
        atlas = new TextureAtlas(Gdx.files.internal("assets/graphics/Maze.txt"));
        uprightMouse=atlas.findRegion("UprightMouse");
        //ball=atlas.findRegion("ball");
        cheese=atlas.findRegion("Cheese");

        //get the spritebatch
        batch = new SpriteBatch();

        //create font
        //font = new BitmapFont(Gdx.files.internal("assets/ui/test.fnt"), false);
        font = new BitmapFont();
        font.scale(Gdx.graphics.getDensity());

        // create viewport
        camera=new OrthographicCamera();
        camera.setToOrtho(false, game.SCREEN_WIDTH, game.SCREEN_HEIGHT);
        stage=new Stage();
        stage.getViewport().setCamera(camera);


        // our christmas tree
        Image mouse = new Image(uprightMouse);
        //mouse.setSize(game.SCREEN_WIDTH/6, game.SCREEN_HEIGHT/10); // scale the tree to the right size
        mouse.setHeight(game.SCREEN_HEIGHT/10);
        mouse.setWidth(mouse.getHeight());
        mouse.setPosition(-mouse.getWidth(), 0);
        mouse.addAction(moveTo(game.SCREEN_WIDTH/2, 0, 1f));
        mouse.setZIndex(0);


        mouse.addAction(
                sequence(delay(1),
                            repeat(numberOfJumps, sequence(moveBy(0, 100, 0.15f), moveBy(0, -100, 0.15f))),

                        delay(4f),
                        new Action() {
                            // custom action to switch to the menu screen
                            @Override
                            public boolean act(float delta) {
                                game.setScreen(new MenuScreen(game));
                                return false;
                            }
                        }));




        stage.addActor(mouse);


        // create the cheese
        if(newRecord) { //create a bunch of little cheeses if new record
            for (int i = 0; i < (score); i++) {
                spawnSmallCheese();
            }
        }
        for (int i = 0; i < (score/100); i++) {//always create the larger cheeses
            spawnCheese();
        }

    }

    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClearColor(0, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // let the stage act and draw
        stage.act(delta);
        stage.draw();

        // draw our text
        batch.begin();
        if(newRecord) {
            font.draw(batch, "New Record!!!! " + "You got " + score + " points!!", 50, 80);
        }
        else{
            font.draw(batch, "Nice try, " + "You got " + score + " points", 50, 80);
        }

        batch.end();

    }

    public void spawnCheese() {
        final Image cheeseImage = new Image(cheese);
        cheeseImage.setOrigin(cheeseImage.getWidth()/2, cheeseImage.getHeight()/2);
        int x = (int) (Math.random() * game.SCREEN_WIDTH);
        cheeseImage.setPosition(x, game.SCREEN_HEIGHT);
        cheeseImage.setScale((float) (Math.random() * 0.8f + 0.2f));
        cheeseImage.addAction(parallel(
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
                                cheeseImage.remove(); // delete this snowflake
                                spawnCheese(); // spawn a new snowflake
                                return false;
                            }
                        })));
        stage.addActor(cheeseImage);
    }
    public void spawnSmallCheese() {
        final Image cheeseImage = new Image(cheese);
        cheeseImage.setOrigin(cheeseImage.getWidth()/2, cheeseImage.getHeight()/2);
        int x = (int) (Math.random() * game.SCREEN_WIDTH);
        cheeseImage.setPosition(x, game.SCREEN_HEIGHT);
        cheeseImage.setScale((float) (Math.random() * 0.08f + 0.02f));
        cheeseImage.addAction(parallel(
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
                                cheeseImage.remove(); // delete this snowflake
                                spawnCheese(); // spawn a new snowflake
                                return false;
                            }
                        })));
        stage.addActor(cheeseImage);
    }
    private String convertPlaytimeToReadable(float time ){

        //convert the float to to a long, then give in milliseconds
        long testTime = (long) time;
        String returnString = String.format("%d min, %d sec",
                TimeUnit.SECONDS.toMinutes(testTime),
                TimeUnit.SECONDS.toSeconds(testTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(testTime))
        );

        return returnString;
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
