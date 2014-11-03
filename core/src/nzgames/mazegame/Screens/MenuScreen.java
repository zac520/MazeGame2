package nzgames.mazegame.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import nzgames.mazegame.Handlers.SaveManager;
import nzgames.mazegame.MainGame;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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
    private boolean changeToMazeScreen = false;
    private java.util.Timer autoModeTimer;

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


    private int bestEasyScore=0;
    private int bestMediumScore=0;
    private int bestHardScore = 0;
    private int bestRidiculousScore=0;

    private int numberEasyMazesSolved = 0;
    private int numberMediumMazesSolved = 0;
    private int numberHardMazesSolved = 0;
    private int numberRidiculousMazesSolved = 0;

    private float bestEasyTime = 0;
    private float bestMediumTime = 0;
    private float bestHardTime = 0;
    private float bestRidiculousTime = 0;

    private long testTime;
    private static final int MIN_EASY_GAMES_FOR_MEDIUM = 5;
    private static final int MIN_MEDIUM_GAMES_FOR_HARD = 7;
    private static final int MIN_HARD_GAMES_FOR_RIDICULOUS = 10;

    private int mazeType;
    Texture boardGraphic;
    TextButton.TextButtonStyle textButtonStyle;
    Button twoPlayerButton;
    Button onePlayerButton;
    Button extraButton;
    TextureAtlas menuScreenAtlas;
    private TextureRegion background;

    private int textRowHeight;
    private int buttonSize;

    public MenuScreen(MainGame pGame) {

        game = pGame;
        batch = new SpriteBatch();

        //set the height of each line for any text we display
        textRowHeight = Gdx.graphics.getHeight() / 40;
        buttonSize = Gdx.graphics.getHeight() / 4;

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
        camera.setToOrtho(false, game.SCREEN_WIDTH, game.SCREEN_HEIGHT);

        //initialize stage
        stage = new Stage();
        stage.getViewport().setCamera(camera);

        skin = game.skin;
        font = new BitmapFont();
        System.out.println(Gdx.graphics.getDensity());
        font.scale(Gdx.graphics.getDensity()/2);

        //set the background
        background = new TextureRegion(game.atlas.findRegion("MenuBackground"));
        Image backgroundImage = new Image(background);
        backgroundImage.setPosition(0,0);
        backgroundImage.setSize(game.SCREEN_WIDTH,game.SCREEN_HEIGHT);
        stage.addActor(backgroundImage);


        //create menu the old way
        //createOldMenu()
        createButtons();


        // give input to the stage
        Gdx.input.setInputProcessor(stage);
    }
    private float getTextScaling(int pixelsPerTextLine){
        float currentFontSize = 12;
        float ratio = pixelsPerTextLine/currentFontSize;
        return ratio ;

    }
    private void createButtons(){
        //make table for all the buttons
        Table table=new Table();
        table.setSize(game.SCREEN_WIDTH,game.SCREEN_HEIGHT);
        table.padTop(game.SCREEN_HEIGHT/3);

        //add Easy mode button
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("WhiteButtonUpEasy");
        textButtonStyle.down = skin.getDrawable("WhiteButtonDownEasy");
        Button easyButton = new Button(textButtonStyle);
        stage.addActor(easyButton);

        easyButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //set screen
//                blocksWide = getNearestNumberInArray(availableSquareSizes,20);
//                blocksHigh = (int) (blocksWide * heightToWidthRatio);
                blocksWide = getNearestSquareFitWidth(EASY_MAZE_WIDTH);
                blocksHigh = getNearestSquareFitHeight(blocksWide);
                autoModeTimer = new Timer();
                autoModeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //Your code goes Here
                        game.setScreen(new MazeScreen(game,game.EASY_MAZE_TYPE,blocksWide,blocksHigh));

                    }
                }, 0);
                //game.setScreen(new MazeScreen(game,game.EASY_MAZE_TYPE,blocksWide,blocksHigh));

            }
        });
        table.add(easyButton).width(buttonSize).height(buttonSize).padTop(30).padRight(100);
        //table.row();

        //add Easy mode button
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("WhiteButtonUpMedium");
        textButtonStyle.down = skin.getDrawable("WhiteButtonDownMedium");
        Button mediumButton = new Button(textButtonStyle);
        stage.addActor(mediumButton);

        mediumButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //set screen
                blocksWide = getNearestSquareFitWidth(MEDIUM_MAZE_WIDTH);
                blocksHigh = getNearestSquareFitHeight(blocksWide);

                autoModeTimer = new Timer();
                autoModeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //Your code goes Here
                        game.setScreen(new MazeScreen(game,game.MEDIUM_MAZE_TYPE,blocksWide,blocksHigh));

                    }
                }, 0);

                //game.setScreen(new MazeScreen(game,game.MEDIUM_MAZE_TYPE,blocksWide,blocksHigh));
            }
        });
        table.add(mediumButton).width(buttonSize).height(buttonSize).padTop(30);
        table.row();

        //add Easy mode button
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("WhiteButtonUpHard");
        textButtonStyle.down = skin.getDrawable("WhiteButtonDownHard");
        Button hardButton = new Button(textButtonStyle);
        stage.addActor(hardButton);

        hardButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                blocksWide = getNearestSquareFitWidth(HARD_MAZE_WIDTH);
                blocksHigh = getNearestSquareFitHeight(blocksWide);

                autoModeTimer = new Timer();
                autoModeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //Your code goes Here
                        game.setScreen(new MazeScreen(game,game.HARD_MAZE_TYPE,blocksWide,blocksHigh));

                    }
                }, 0);

                //game.setScreen(new MazeScreen(game,game.HARD_MAZE_TYPE,blocksWide,blocksHigh));

            }
        });
        table.add(hardButton).width(buttonSize).height(buttonSize).padTop(30).padRight(100);
        //table.row();


        //add Easy mode button
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("WhiteButtonUpRidiculous");
        textButtonStyle.down = skin.getDrawable("WhiteButtonDownRidiculous");
        Button ridiculousButton = new Button(textButtonStyle);
        stage.addActor(ridiculousButton);

        ridiculousButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                blocksWide = getNearestSquareFitWidth(RIDICULOUS_MAZE_WIDTH);
                blocksHigh = getNearestSquareFitHeight(blocksWide);

                autoModeTimer = new Timer();
                autoModeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //Your code goes Here
                        game.setScreen(new MazeScreen(game,game.RIDICULOUS_MAZE_TYPE,blocksWide,blocksHigh));

                    }
                }, 0);

                //game.setScreen(new MazeScreen(game,game.RIDICULOUS_MAZE_TYPE,blocksWide,blocksHigh));

            }
        });
        table.add(ridiculousButton).width(buttonSize).height(buttonSize).padTop(30);
        //table.row();



        //add table to the stage
        stage.addActor(table);
    }

    private void createSingleButton(){
        
    }

    private void createOldMenu(){
        skin = new Skin(Gdx.files.internal("assets/ui/defaultskin.json"));

        //Make the table
        Table table = new Table();
        //table.setSize(800, 480);

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
                autoModeTimer = new Timer();
                autoModeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //Your code goes Here
                        game.setScreen(new MazeScreen(game,game.EASY_MAZE_TYPE,blocksWide,blocksHigh));

                    }
                }, 0);
                //game.setScreen(new MazeScreen(game,game.EASY_MAZE_TYPE,blocksWide,blocksHigh));
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

                autoModeTimer = new Timer();
                autoModeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //Your code goes Here
                        game.setScreen(new MazeScreen(game,game.MEDIUM_MAZE_TYPE,blocksWide,blocksHigh));

                    }
                }, 0);

                //game.setScreen(new MazeScreen(game,game.MEDIUM_MAZE_TYPE,blocksWide,blocksHigh));
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

                autoModeTimer = new Timer();
                autoModeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //Your code goes Here
                        game.setScreen(new MazeScreen(game,game.HARD_MAZE_TYPE,blocksWide,blocksHigh));

                    }
                }, 0);

                //game.setScreen(new MazeScreen(game,game.HARD_MAZE_TYPE,blocksWide,blocksHigh));
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

                autoModeTimer = new Timer();
                autoModeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //Your code goes Here
                        game.setScreen(new MazeScreen(game,game.RIDICULOUS_MAZE_TYPE,blocksWide,blocksHigh));

                    }
                }, 0);

                //game.setScreen(new MazeScreen(game,game.RIDICULOUS_MAZE_TYPE,blocksWide,blocksHigh));

            }
        });
        table.add(ridiculousMaze).width(200).height(50);
        table.row();


        //add table to the stage
        stage.addActor(table);


    }
    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



        stage.act();
        stage.draw();


        //show frames per second
        batch.begin();
        //draw the number of mazes solved
        font.draw(batch, "Number of Easy Mazes Solved: " + numberEasyMazesSolved, 20, game.SCREEN_HEIGHT - (textRowHeight*7));
        font.draw(batch, "Number of Medium Mazes Solved: " + numberMediumMazesSolved, 20, game.SCREEN_HEIGHT -(textRowHeight*8));
        font.draw(batch, "Number of Hard Mazes Solved: " + numberHardMazesSolved, 20, game.SCREEN_HEIGHT -(textRowHeight*9));
        font.draw(batch, "Number of Ridiculous Mazes Solved: " + numberRidiculousMazesSolved, 20, game.SCREEN_HEIGHT -(textRowHeight*10));

        //draw the best time for each
//        font.draw(batch, "Best score: " + (bestEasyTime > 0 ? convertPlaytimeToReadable(bestEasyTime) : ""), game.SCREEN_WIDTH/1.5f, game.SCREEN_HEIGHT -(textRowHeight*7));
//        font.draw(batch, "Best score: " + (bestMediumTime > 0 ? convertPlaytimeToReadable(bestMediumTime) : ""), game.SCREEN_WIDTH/1.5f, game.SCREEN_HEIGHT -(textRowHeight*8));
//        font.draw(batch, "Best score: " + (bestHardTime > 0 ? convertPlaytimeToReadable(bestHardTime) : ""), game.SCREEN_WIDTH/1.5f, game.SCREEN_HEIGHT -(textRowHeight*9));
//        font.draw(batch, "Best score: " + (bestRidiculousTime > 0 ? convertPlaytimeToReadable(bestRidiculousTime) : ""), game.SCREEN_WIDTH/1.5f, game.SCREEN_HEIGHT -(textRowHeight*10));
        font.draw(batch, "Best score: " + (bestEasyScore > 0 ? bestEasyScore :""), game.SCREEN_WIDTH/1.5f, game.SCREEN_HEIGHT -(textRowHeight*7));
        font.draw(batch, "Best score: " + (bestMediumScore > 0 ? bestMediumScore : ""), game.SCREEN_WIDTH/1.5f, game.SCREEN_HEIGHT -(textRowHeight*8));
        font.draw(batch, "Best score: " + (bestHardScore > 0 ? bestHardScore : ""), game.SCREEN_WIDTH/1.5f, game.SCREEN_HEIGHT -(textRowHeight*9));
        font.draw(batch, "Best score: " + (bestRidiculousScore > 0 ? bestRidiculousScore : ""), game.SCREEN_WIDTH/1.5f, game.SCREEN_HEIGHT -(textRowHeight*10));

        //draw the loading progress, if applicable, and the loading percent, if applicable
        if(!game.loadingProgress.isEmpty()){
            font.draw(batch, game.loadingProgress +"  "+  (game.loadingProgressPercent>-1?(String.valueOf(game.loadingProgressPercent) + "%"):""), 10, textRowHeight);

        }


        batch.end();



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
        SaveManager saveManager = new SaveManager(game.saveEncrypted);

        //load the number of mazes
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

        //load the best times
        if (saveManager.loadDataValue("bestEasyTime", Float.class) != null) {
            bestEasyTime = saveManager.loadDataValue("bestEasyTime", Float.class);
        }
        if (saveManager.loadDataValue("bestMediumTime", Float.class) != null) {
            bestMediumTime = saveManager.loadDataValue("bestMediumTime", Float.class);
        }
        if (saveManager.loadDataValue("bestHardTime", Float.class) != null) {
            bestHardTime = saveManager.loadDataValue("bestHardTime", Float.class);
        }
        if (saveManager.loadDataValue("bestRidiculousTime", Float.class) != null) {
            bestRidiculousTime = saveManager.loadDataValue("bestRidiculousTime", Float.class);
        }

        //load the best scores
        if (saveManager.loadDataValue("bestEasyTime", Float.class) != null) {
            bestEasyScore = saveManager.loadDataValue("bestEasyScore", Float.class);
        }
        if (saveManager.loadDataValue("bestMediumTime", Float.class) != null) {
            bestMediumScore = saveManager.loadDataValue("bestMediumScore", Float.class);
        }
        if (saveManager.loadDataValue("bestHardTime", Float.class) != null) {
            bestHardScore = saveManager.loadDataValue("bestHardScore", Float.class);
        }
        if (saveManager.loadDataValue("bestRidiculousTime", Float.class) != null) {
            bestRidiculousScore = saveManager.loadDataValue("bestRidiculousScore", Float.class);
        }
    }

    //convert the long to a readable string in minutes, seconds
    private String convertPlaytimeToReadable(float time ){

        //convert the float to to a long, then give in milliseconds
        testTime = (long) time;
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

    }
}
