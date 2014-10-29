package nzgames.mazegame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import nzgames.mazegame.Handlers.MyInput;
import nzgames.mazegame.Handlers.MyInputProcessor;
import nzgames.mazegame.Screens.MazeScreen;
import nzgames.mazegame.Screens.MenuScreen;
import nzgames.mazegame.Screens.VictoryScreen;

import java.util.Random;

public class MainGame extends Game {

    public boolean saveEncrypted = true;

    public int SCREEN_WIDTH = 480;
    public int SCREEN_HEIGHT = 360;

    public int EASY_MAZE_TYPE = 0;
    public int MEDIUM_MAZE_TYPE = 1;
    public int HARD_MAZE_TYPE = 2;
    public int RIDICULOUS_MAZE_TYPE = 3;

    public Random rand;
    /** shared textures **/
    public TextureAtlas atlas;
    public Skin skin;

    /** shared variables **/
    public Stage stage;
    public SpriteBatch batch;
    public BitmapFont font;
    public Box2DDebugRenderer box2DRenderer;
    public MyInputProcessor myInputProcessor;
    public String loadingProgress;
    public int loadingProgressPercent = -1;

    @Override
    public void create () {

        //set the screen dimensions
        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();

        //start up all of the shared variables (needed for async loading)
        box2DRenderer = new Box2DDebugRenderer();
        stage = new Stage();
        batch = new SpriteBatch();
        font = new BitmapFont();
        myInputProcessor = new MyInputProcessor();
        loadingProgress = new String();

        //seed the randomizer
        rand = new Random();//seed this for any variable thing to use later

        //start up the atlas
        atlas = new TextureAtlas(Gdx.files.internal("assets/graphics/Maze.txt"));

        //need to learn about filters. For now, this allows me to add two actors side by side without a feathering effect between them
        //(makes it seamless)
        Texture myTexture = atlas.getTextures().first();
        myTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);


        setScreen(new MenuScreen(this));
    }


}
