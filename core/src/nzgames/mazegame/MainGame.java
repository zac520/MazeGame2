package nzgames.mazegame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import nzgames.mazegame.Screens.MazeScreen;

import java.util.Random;

public class MainGame extends Game {
    public int SCREEN_WIDTH = 480;
    public int SCREEN_HEIGHT = 360;
    public Random rand;
    /** shared textures **/
    public TextureAtlas atlas;
    public Skin skin;

    @Override
    public void create () {
        rand = new Random();//seed this for any variable thing to use later
        atlas = new TextureAtlas(Gdx.files.internal("assets/graphics/TicTacToe.txt"));
        setScreen(new MazeScreen(this));

    }


}
