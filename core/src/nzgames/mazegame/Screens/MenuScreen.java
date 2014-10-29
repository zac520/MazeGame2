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
import com.badlogic.gdx.utils.Array;
import nzgames.mazegame.Handlers.SaveManager;
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


    private int blocksWide;
    private int blocksHigh;
    private float heightToWidthRatio;

    Array<Integer> commonMultiples;
    Array<Integer> availableSquareSizes;

    private boolean perfectSquares = true;

    private final int EASY_MAZE_WIDTH = 20;
    private final int MEDIUM_MAZE_WIDTH = 40;
    private final int HARD_MAZE_WIDTH = 64;
    private final int RIDICULOUS_MAZE_WIDTH = 128;



    private int numberEasyMazesSolved = 0;
    private int numberMediumMazesSolved = 0;
    private int numberHardMazesSolved = 0;
    private int numberRidiculousMazesSolved = 0;

    public MenuScreen(MainGame pGame) {

        game = pGame;
        batch = new SpriteBatch();

        //set the ratio of width to height
        heightToWidthRatio = (float) game.SCREEN_HEIGHT / (float) game.SCREEN_WIDTH;

        //find the common multiples to determine all sizes of each squares based on screen dimensions
        long greatestCommonDenominator = gcd((long) game.SCREEN_HEIGHT, (long)game.SCREEN_WIDTH);
        commonMultiples = new Array<Integer>();
        fillCommonMultiples(greatestCommonDenominator);//these options allow for a square maze given the screen dimensions. They are in pixels wide for each square

        //convert to blocks in width
        availableSquareSizes = new Array<Integer>();
        for(int x = 0; x< commonMultiples.size; x++){
            availableSquareSizes.add(Gdx.graphics.getWidth()/commonMultiples.get(x));
        }

        if(availableSquareSizes.size<4){
            perfectSquares = false; //the screen ratio will not support perfect squares. use rectangles.
        }


        //load the saved data (how many of each maze has been solved)
        loadData();

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
//                blocksWide = getNearestNumberInArray(availableSquareSizes,20);
//                blocksHigh = (int) (blocksWide * heightToWidthRatio);
                blocksWide = getNearestSquareFitWidth(EASY_MAZE_WIDTH);
                blocksHigh = getNearestSquareFitHeight(blocksWide);

                game.setScreen(new MazeScreen(game,game.EASY_MAZE_TYPE,blocksWide,blocksHigh));
            }
        });
        table.add(easyMaze).width(200).height(50);
        table.row();

        //add the start game button
        TextButton mediumMaze = new TextButton("Medium Maze", skin);
        mediumMaze.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                blocksWide = getNearestSquareFitWidth(MEDIUM_MAZE_WIDTH);
                blocksHigh = getNearestSquareFitHeight(blocksWide);

                game.setScreen(new MazeScreen(game,game.MEDIUM_MAZE_TYPE,blocksWide,blocksHigh));
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
                blocksWide = getNearestSquareFitWidth(HARD_MAZE_WIDTH);
                blocksHigh = getNearestSquareFitHeight(blocksWide);

                game.setScreen(new MazeScreen(game,game.HARD_MAZE_TYPE,blocksWide,blocksHigh));
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

                blocksWide = getNearestSquareFitWidth(RIDICULOUS_MAZE_WIDTH);
                blocksHigh = getNearestSquareFitHeight(blocksWide);

                game.setScreen(new MazeScreen(game,game.RIDICULOUS_MAZE_TYPE,blocksWide,blocksHigh));
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
        font.draw(batch, "Number of Easy Mazes Solved: " + numberEasyMazesSolved, 20, game.SCREEN_HEIGHT -30);
        font.draw(batch, "Number of Medium Mazes Solved: " + numberMediumMazesSolved, 20, game.SCREEN_HEIGHT -50);
        font.draw(batch, "Number of Hard Mazes Solved: " + numberHardMazesSolved, 20, game.SCREEN_HEIGHT -70);
        font.draw(batch, "Number of Ridiculous Mazes Solved: " + numberRidiculousMazesSolved, 20, game.SCREEN_HEIGHT -90);

        batch.end();

        stage.act();
        stage.draw();
    }

    //get the common denominator
    private static long gcd(long a, long b)
    {
        while (b > 0)
        {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }
    private void fillCommonMultiples(long originalNumber){

        int testNumber = (int) originalNumber;

        while(testNumber >0){
            if(originalNumber%testNumber == 0){ //if no remainder, then it is an acceptable multiple
                commonMultiples.add(testNumber);
            }
            testNumber--;

        }


    }

    private int getNearestNumberInArray(Array myArray, int myNumber) {
        //http://stackoverflow.com/questions/13318733/get-closest-value-to-a-number-in-array
        int distance = Math.abs((Integer)myArray.get(0) - myNumber);
        int idx = 0;
        for (int c = 1; c < myArray.size; c++) {
            int cdistance = Math.abs((Integer)myArray.get(c) - myNumber);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        return (Integer)myArray.get(idx);
    }

    private int getNearestSquareFitWidth(int number){
        //this function will get the closest fit to the width in blocks that we can evenly divide the screen width in
        while(number >1){
            if(game.SCREEN_WIDTH%number ==0){
                return number;
            }
            number -=1;
        }
        return number;
    }
    private int getNearestSquareFitHeight(int blockWidth){
        //this function will try to fill up the height with as close to a square based on the width as possible
        int pixelsPerWidth = game.SCREEN_WIDTH / blockWidth;
        int closestFit=1;
        int tempFit = 0;

        //find the best fit in one direction
        while(pixelsPerWidth >1){
            if(game.SCREEN_HEIGHT%pixelsPerWidth ==0){
                closestFit = game.SCREEN_HEIGHT/pixelsPerWidth;//number of blocks high (same as blockWidth)
                break;
            }
            pixelsPerWidth -=1;
        }

        //reset the pixels per width
        pixelsPerWidth = game.SCREEN_WIDTH / blockWidth;

        //find the best fit in the other direction
        while(pixelsPerWidth <100){
            if(game.SCREEN_HEIGHT%pixelsPerWidth ==0){
                tempFit = game.SCREEN_HEIGHT/pixelsPerWidth;
                if(Math.abs(tempFit-blockWidth) < Math.abs(closestFit - blockWidth)) {
                    closestFit = tempFit;
                    break;
                }
            }
            pixelsPerWidth +=1;
        }
        return closestFit;
    }
    private void loadData() {
        //get the number of games played
        SaveManager saveManager = new SaveManager(false);

        if (saveManager.loadDataValue("numberOfEasyMazesSolved", Integer.class) != null) {
            numberEasyMazesSolved = saveManager.loadDataValue("numberOfEasyMazesSolved", Integer.class);
        }
        if (saveManager.loadDataValue("numberOfMediumMazesSolved", Integer.class) != null) {
            numberMediumMazesSolved = saveManager.loadDataValue("numberOfMediumMazesSolved", Integer.class);
        }
        if (saveManager.loadDataValue("numberOfHardMazesSolved", Integer.class) != null) {
            numberHardMazesSolved = saveManager.loadDataValue("numberOfHardMazesSolved", Integer.class);
        }
        if (saveManager.loadDataValue("numberOfRidiculousMazesSolved", Integer.class) != null) {
            numberRidiculousMazesSolved = saveManager.loadDataValue("numberOfRidiculousMazesSolved", Integer.class);
        }

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
