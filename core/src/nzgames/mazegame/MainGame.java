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
import nzgames.mazegame.Screens.MenuScreen;
import nzgames.mazegame.Screens.VictoryScreen;

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


        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();


        rand = new Random();//seed this for any variable thing to use later
        atlas = new TextureAtlas(Gdx.files.internal("assets/graphics/Maze.txt"));

        //need to learn about filters. For now, this allows me to add two actors side by side without a feathering effect between them
        //(makes it seamless)
        Texture myTexture = atlas.getTextures().first();
        myTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);


        setScreen(new MenuScreen(this));
    }


}
