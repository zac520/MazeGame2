package nzgames.mazegame.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import nzgames.mazegame.MainGame;

/**
 * Created by zac520 on 8/10/14.
 */
public class MenuScreen implements Screen {


    BitmapFont font;
    MainGame game;
    OrthographicCamera camera;
    Stage stage;
    SpriteBatch batch;
    Skin skin;

    public MenuScreen(MainGame pGame) {

        game = pGame;
        batch = new SpriteBatch();

        //initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        //initialize stage
        stage = new Stage();
        stage.getViewport().setCamera(camera);

        skin = new Skin(Gdx.files.internal("assets/ui/defaultskin.json"));
        font = new BitmapFont();

        //Make the table
        Table table = new Table();
        table.setSize(800, 480);

        //add the start game button
        TextButton easyMaze = new TextButton("Easy Maze", skin);
        easyMaze.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //set screen
                game.setScreen(new MazeScreen(game,8,6));
            }
        });
        table.add(easyMaze).width(200).height(50);
        table.row();

        //add the start game button
        TextButton mediumMaze = new TextButton("Medium Maze", skin);
        mediumMaze.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //set screen
                game.setScreen(new MazeScreen(game,40,30));
            }
        });
        table.add(mediumMaze).width(200).height(50);
        table.row();

        //add the start game button
        TextButton hardMaze = new TextButton("Hard Maze", skin);
        hardMaze.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //set screen
                game.setScreen(new MazeScreen(game,90,60));
            }
        });
        table.add(hardMaze).width(200).height(50);
        table.row();

        //add the start game button
        TextButton ridiculousMaze = new TextButton("Ridiculous Maze", skin);
        ridiculousMaze.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //set screen
                game.setScreen(new MazeScreen(game,180,120));
            }
        });
        table.add(ridiculousMaze).width(200).height(50);
        table.row();

        //add table to the stage
        stage.addActor(table);

        // give input to the stage
        Gdx.input.setInputProcessor(stage);
    }
    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //show frames per second
        batch.begin();
        //font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 20, game.SCREEN_HEIGHT -30);
        batch.end();

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
