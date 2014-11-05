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
    public boolean showAds = false;//default to false, and we will set from the android screen for now
    public boolean needCameraResize = false;

    public int SCREEN_WIDTH = 360;
    public int SCREEN_HEIGHT = 480;
    private int BANNER_PIXEL_HEIGHT=50;

    public int EASY_MAZE_TYPE = 1;
    public int MEDIUM_MAZE_TYPE = 2;
    public int HARD_MAZE_TYPE = 4;
    public int RIDICULOUS_MAZE_TYPE = 8;

    public Random rand;
    /** shared textures **/
    public TextureAtlas atlas;
    public Skin skin;
    public TextureAtlas loadingAtlas;
    public Skin popupSkin;

    /** shared variables **/
    public Stage stage;
    public SpriteBatch batch;
    public BitmapFont font;
    public Box2DDebugRenderer box2DRenderer;
    public MyInputProcessor myInputProcessor;
    public String loadingProgress;
    public float loadingProgressPercent = 0;
    public int textRowHeight;
    public int BANNER_DIP_HEIGHT;

    /**loading screen variables**/
    public Stage loadingStage;
    public SpriteBatch loadingBatch;


    @Override
    public void create () {

        //set up the ad space
        setScreenDimensionsForAds();



        //start up the atlas
        atlas = new TextureAtlas(Gdx.files.internal("assets/graphics/Maze.txt"));
        loadingAtlas = new TextureAtlas(Gdx.files.internal("assets/graphics/LoadingGraphics.txt"));

        //start up all of the shared variables (needed for async loading)
        resetSharedVariables();

        //seed the randomizer
        rand = new Random();//seed this for any variable thing to use later



        //need to learn about filters. For now, this allows me to add two actors side by side without a feathering effect between them
        //(makes it seamless)
//        Texture myTexture = atlas.getTextures().first();
//        myTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);


        setScreen(new MenuScreen(this));
        //setScreen(new VictoryScreen(this, 100, 100, true));
    }


    public void resetLoadingVariables(){
        loadingStage = new Stage();
        loadingBatch = new SpriteBatch();
    }

    //set up shared variables
    public void resetSharedVariables(){
        //set the screen dimensions
        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight()-BANNER_DIP_HEIGHT;

        box2DRenderer = new Box2DDebugRenderer();
        stage = new Stage();
        batch = new SpriteBatch();
        //font = new BitmapFont();
        font = new BitmapFont(Gdx.files.internal("assets/userinterface/Source_Sans_Pro.fnt"));
        textRowHeight = Gdx.graphics.getHeight() / 40;
        font.setScale(getTextScaling(textRowHeight));
        myInputProcessor = new MyInputProcessor();
        loadingProgress = new String();
        //set up our own skin to make on the fly
        skin = new Skin();
        skin.addRegions(atlas);

        //set up this skin for popups
        popupSkin = new Skin(Gdx.files.internal("assets/userinterface/defaultskin.json"));

    }
    private float getTextScaling(int pixelsPerTextLine){

        //get the line height of our font
        float currentFontSize = font.getData().lineHeight;

        //compare to the line height we want
        float ratio = pixelsPerTextLine/currentFontSize;

        return ratio;

    }

    public void setScreenDimensionsForAds(){
        //Determine the platform that the application is running on.
        switch (Gdx.app.getType()){
            case Desktop:
                //System.out.println("I'm running on a Desktop.");
                break;

            case Android:
                if(showAds){
                    BANNER_PIXEL_HEIGHT = 50;
                }
                else{
                    BANNER_PIXEL_HEIGHT = 0;
                }
                float SCREEN_DENSITY = Gdx.graphics.getDensity();
                BANNER_DIP_HEIGHT = (int) (SCREEN_DENSITY * BANNER_PIXEL_HEIGHT);
                SCREEN_HEIGHT = Gdx.graphics.getHeight() - BANNER_DIP_HEIGHT; //Draw the Board without the banner
                break;

        }

    }

}
