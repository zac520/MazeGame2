package nzgames.mazegame.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import nzgames.mazegame.Actors.Goal;
import nzgames.mazegame.Actors.Player;
import nzgames.mazegame.Actors.VisitedSquare;
import nzgames.mazegame.Handlers.*;
import nzgames.mazegame.MainGame;
import nzgames.mazegame.Screens.MenuScreen;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;


/**
 * Created by zac520 on 10/22/14.
 */
public class MazeScreen implements Screen {
    //TODO add timer as a HUD
    // add saving best scores for each category
    // add a path behind you for each block traveled
    OrthographicCamera camera;
    MainGame game;
    private World world;
    OrthographicCamera box2DCam;
    Box2DDebugRenderer box2DRenderer;
    SpriteBatch batch;
    BitmapFont font;

    private boolean debug = false;
    private int lineWidth = 10;
    private int lineHeight = 10;

    private int blocksWide = 48;
    private int blocksHigh = 48;

    private Body[][] verticalWalls;
    private Body[][] horizontalWalls;

    private Actor[][] verticalWallActor;
    private Actor[][] horizontalWallActor;

    private int [][] visitedSquares;

    private float accelx;
    private float accely;

    private Array<Vector2> positionStack;

    Player player;
    private Stage stage;

    private TextureRegion horizontalMazeWall;

    private float playTime = 0;
    private long testTime;

    private float originalZoomLevelX = 1;
    private float originalZoomLevelY = 1;

    private float currentZoomLevelX = 1;
    private float currentZoomLevelY = 1;

    public boolean flinging = false;
    public float velX;
    public float velY;

    public float x_left_limit;
    public float x_right_limit;
    public float y_bottom_limit;
    public float y_top_limit;

    public boolean initialZoomTouchdown = true;
    private float currentCameraZoom = 1;

    private boolean movingBlockBeyondBorders = false;
    private int lastMovementDirection =1;

    private int longestDistance = 0;
    private int currentDistance = 0;
    private Vector2 longestDistanceLocation;

    private MyContactListener cl;
    private Goal goal;

    private int mazeType;

    Actor [][] playerPath;
    private int score;
    /**box2d variables**/
    private float accum = 0f;
    private final float step = 1f / 80f;
    private final float maxAccum = 1f / 20f;


    //used for loading progress percent
    private int totalSquaresToVisit = 0;
    private int totalVisitedSquares = 0;

    private int xBlockValue;
    private int yBlockValue;

    private boolean newRecord = true;

    public MazeScreen(MainGame myGame, int type, int width, int height) {
        game = myGame;

        //batch = new SpriteBatch();
        //font = new BitmapFont();

        batch = game.batch;
        font = game.font;

        //save the type of maze so we know what to save when it is done
        mazeType = type;

        //set up the block height and width
        blocksHigh = height;
        blocksWide = width;

        //set the camera up
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.SCREEN_WIDTH, Gdx.graphics.getHeight());

        //set the limits of the camera
        setStageLimits();

        //start the stage with the camera
        //stage= new Stage();
        //stage.getViewport().setCamera(camera);
        stage = game.stage;
        stage.getViewport().setCamera(camera);



        //set up box2d renderer
        //box2DRenderer = new Box2DDebugRenderer();
        box2DRenderer = game.box2DRenderer;

        //set up box2dcam
        box2DCam = new OrthographicCamera();
        box2DCam.setToOrtho(false, game.SCREEN_WIDTH / Box2DVars.PPM, game.SCREEN_HEIGHT / Box2DVars.PPM);

        //add the world for the box2d bodies
        world = new World(new Vector2(0, 0), true);
        cl = new MyContactListener();
        world.setContactListener(cl);

        //divide the world into equal squares
        lineWidth = game.SCREEN_WIDTH / blocksWide;
        lineHeight =  game.SCREEN_HEIGHT / blocksHigh;

        //initialize the visited array
        game.loadingProgress = "Initializing visited Array";
        visitedSquares = new int[blocksWide][blocksHigh];
        zeroOutVisitedArray();


        horizontalMazeWall = new TextureRegion(game.atlas.findRegion("Wall"));

        //add up all the walls (we destroy them as we make the maze, leaving only the usable maze walls)
        game.loadingProgress = "Adding all maze walls";
        addAllMazeWalls();

        //add the floor sensors--this was too slow
        //addAllMazeSquareSensors();

        //create the maze border
        game.loadingProgress = "Drawing maze border";
        drawBorder();

        //create a circle at the starting point
        createPlayer(0, blocksHigh-1);


        //need a multiplexor so that the user can touch the level, or the user interface
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new GestureDetector(new MyGestureListener()));
        //multiplexer.addProcessor(new MyInputProcessor());
        multiplexer.addProcessor(game.myInputProcessor); //input processor uses a Stage, which we cannot create asynchronously
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);


        //create maze via recursion
//        //put us at position 0,0, and mark that square as visited
//        Vector2 currentPosition = new Vector2(0, 0);
//        visitedSquares[0][0] = 1;
        //createDFSMaze(currentPosition);

        //test
        game.loadingProgress = "Creating the actual maze";
        createMazeWithoutRecursion();

        //zero out the visited array (this time the player will visit as maze is traversed)
        game.loadingProgress = "Resetting visited areas";
        zeroOutVisitedArray();

        //create blank actors
        //this did not add any runtime savings
        //createBlankActorsForPath();

        //reset the loading progess message to nothing
        game.loadingProgress = "";

    }

    private void zeroOutVisitedArray(){
        //initialize the visited array
        for (int x = 0; x < blocksWide; x++) {
            for (int y = 0; y < blocksHigh; y++) {
                //make all as zero to represent false, or unvisited
                visitedSquares[x][y] = 0;
            }
        }
    }
    private void createMazeWithoutRecursion(){
        //had to do it without recursion because stack is way too large to make a maze


        //calculate the total number of squares to visit
        totalSquaresToVisit = blocksHigh * blocksWide;

        //put us at position 0,0, and mark that square as visited
        Vector2 currentPosition = new Vector2(0, blocksHigh-1);
        //visitedSquares[0][blocksHigh-1] = 1;

        positionStack = new Array<Vector2>();
        int nextSquareDirection;
        int biasDecider;
        positionStack.add(currentPosition);
        while (positionStack.size > 0) {

            //to make longer walls, will randomly give a bias for using the last direction
            biasDecider = game.rand.nextInt((6 - 1) + 1) + 1;//1,2,3, or 4 or 5

            if(biasDecider<5){
                nextSquareDirection = lastMovementDirection;
            }
            else {
                //choose a random direction
                nextSquareDirection = game.rand.nextInt((5 - 1) + 1) + 1;//1,2,3, or 4
            }
            switch (nextSquareDirection) {
                case 1:
                    //if it's too high, or we have already visited that square then check the next direction
                    if ((currentPosition.y + 1 > blocksHigh - 1) || (visitedSquares[(int) currentPosition.x][(int) currentPosition.y + 1] == 1)) {
                        break;
                    }
                    //if it isn't too high, then add to the stack, and check everything again from there
                    else {
                        //break down the wall
                        world.destroyBody(horizontalWalls[(int) currentPosition.x][(int) currentPosition.y + 1]);
                        stage.getRoot().removeActor(horizontalWallActor[(int)currentPosition.x][(int)currentPosition.y +1]);

                        //travel to that spot now that we can get there
                        currentPosition.y += 1;

                        //add to the current distance
                        currentDistance +=1;

                        //add the current position to the stack
                        positionStack.add(new Vector2(currentPosition));

                        //add to the total squares visited
                        totalVisitedSquares +=1;

                        //save our direction for use in tweaking the maze
                        lastMovementDirection = 1;
                    }

                    break;
                case 2:

                    //if it's too high, or we have already visited that square then check the next direction
                    if ((currentPosition.x + 1 > blocksWide - 1) || (visitedSquares[(int) currentPosition.x + 1][(int) currentPosition.y] == 1)) {
                        break;
                    }
                    //if it isn't too high, then add to the stack, and check everything again from there
                    else {
                        //break down the wall
                        world.destroyBody(verticalWalls[(int) currentPosition.x + 1][(int) currentPosition.y]);
                        stage.getRoot().removeActor(verticalWallActor[(int) currentPosition.x + 1][(int) currentPosition.y]);

                        //travel to that spot now that we can get there
                        currentPosition.x += 1;

                        //add to the current distance
                        currentDistance +=1;

                        //add the current position to the stack
                        positionStack.add(new Vector2(currentPosition));

                        //add to the total squares visited
                        totalVisitedSquares +=1;

                        //save our direction for use in tweaking the maze
                        lastMovementDirection = 2;
                    }


                    break;
                case 3:

                    //if it's too low, or we have already visited that square then check the next direction
                    if ((currentPosition.y - 1 < 0) || (visitedSquares[(int) currentPosition.x][(int) currentPosition.y - 1] == 1)) {
                        break;
                    }
                    //if it isn't too high, then add to the stack, and check everything again from there
                    else {
                        //break down the wall
                        world.destroyBody(horizontalWalls[(int) currentPosition.x][(int) currentPosition.y]);
                        stage.getRoot().removeActor(horizontalWallActor[(int) currentPosition.x][(int) currentPosition.y]);

                        //travel to that spot now that we can get there
                        currentPosition.y -= 1;

                        //add to the current distance
                        currentDistance +=1;

                        //add the current position to the stack
                        positionStack.add(new Vector2(currentPosition));

                        //add to the total squares visited
                        totalVisitedSquares +=1;

                        //save our direction for use in tweaking the maze
                        lastMovementDirection = 3;
                    }

                    break;
                case 4:

                    //if it's too high, or we have already visited that square then check the next direction
                    if ((currentPosition.x - 1 < 0) || (visitedSquares[(int) currentPosition.x - 1][(int) currentPosition.y] == 1)) {
                        break;
                    }
                    //if it isn't too high, then add to the stack, and check everything again from there
                    else {
                        //break down the wall
                        world.destroyBody(verticalWalls[(int) currentPosition.x][(int) currentPosition.y]);
                        stage.getRoot().removeActor(verticalWallActor[(int) currentPosition.x][(int) currentPosition.y]);

                        //travel to that spot now that we can get there
                        currentPosition.x -= 1;

                        //add to the current distance
                        currentDistance +=1;

                        //add the current position to the stack
                        positionStack.add(new Vector2(currentPosition));

                        //add to the total squares visited
                        totalVisitedSquares +=1;

                        //save our direction for use in tweaking the maze
                        lastMovementDirection = 4;
                    }

                    break;
                default:
                    break;
            }


            visitedSquares[(int)currentPosition.x][(int)currentPosition.y] = 1;


            //now that we have checked our random integer, check all of the other directions. If they all pass, pop off stack
            if (deadEndCheck(currentPosition)) {

                //check to see if this is the longest current spot, if so, make a note of it
                if (currentDistance > longestDistance){
                    longestDistance = currentDistance;
                    longestDistanceLocation = currentPosition;
                }

                //remove one from the current distance
                currentDistance -=1;
                //go back to the previous position
                currentPosition = positionStack.pop();

            }

            game.loadingProgressPercent = (int) (((float)totalVisitedSquares / (float)totalSquaresToVisit)*100);

        }

        //create the end at the longest recorded location
        createEnd((int)longestDistanceLocation.x, (int) longestDistanceLocation.y);

        //reset the loading progress to -1 so we don't print it anywhere.
        game.loadingProgressPercent = -1;

    }

    private void createEnd(int xPosition, int yPosition){

        xPosition *= lineWidth;
        yPosition *= lineHeight;

        //define body
        BodyDef bdef = new BodyDef();
        bdef.position.set(
                (xPosition + lineWidth/2) / Box2DVars.PPM,
                (yPosition + lineHeight/2) / Box2DVars.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        //create body
        Body body = world.createBody(bdef);

        //define Fixture
        PolygonShape shape = new PolygonShape();
        //shape.setRadius(lineWidth<lineHeight ?(lineWidth/2f)/Box2DVars.PPM:(lineHeight/2f)/Box2DVars.PPM);
        shape.setAsBox(
                0.9f*(lineWidth/2)/(Box2DVars.PPM),
                0.9f*(lineHeight/2)/(Box2DVars.PPM));


        FixtureDef fdef = new FixtureDef();
        fdef.isSensor = true;
        fdef.shape = shape;
        body.setSleepingAllowed(false);
        body.createFixture(fdef).setUserData("end");//a tag to identify this later

        goal = new Goal(
                game,
                body,
                lineWidth<lineHeight ? (lineWidth)/Box2DVars.PPM : (lineHeight)/Box2DVars.PPM  ,
                lineWidth<lineHeight ? (lineWidth)/Box2DVars.PPM  : (lineHeight)/Box2DVars.PPM );
        stage.addActor(goal.getGroup());
        //move the tile to the back so we don't draw on top of player
        stage.getActors().get(stage.getActors().size -1).toBack();

    }

    public boolean deadEndCheck(Vector2 currentPosition){

        //check the surrounding areas. If any are reachable, then return false. Else return true;
        if ((currentPosition.y + 1 < blocksHigh) && (visitedSquares[(int) currentPosition.x][(int) currentPosition.y + 1] == 0)) {
            return false;
        }
        if ((currentPosition.x + 1 < blocksWide) && (visitedSquares[(int) currentPosition.x + 1][(int) currentPosition.y] == 0)) {
            return false;
        }
        if ((currentPosition.y - 1 > 0) && (visitedSquares[(int) currentPosition.x][(int) currentPosition.y - 1] == 0)) {
            return false;
        }
        if ((currentPosition.x - 1 > 0) && (visitedSquares[(int) currentPosition.x - 1][(int) currentPosition.y] == 0)) {
            return false;
        }
        return true;
    }

    @Override
    public void render(float delta) {

        //reset the background color
        Gdx.gl.glClearColor(0,.5f,.5f,1);//teal
        //Gdx.gl.glClearColor(1,.5f,.5f,1);nice pink color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //check if the player hit the end of the maze
        if(cl.checkEndMaze()){
            goBackToMenu();
        }





        //slow down the camera if we are flinging
        if (flinging) {
            flingCamera();
        }

        camera.update();



        //get the accelerometer input
        accelx = -Gdx.input.getAccelerometerX();
        accely = -Gdx.input.getAccelerometerY();


        if((accelx >1)|| (MyInput.isDown(MyInput.MOVE_RIGHT))) {
            if(player.getBody().getLinearVelocity().x < player.PLAYER_MAX_SPEED) {
                if(accelx !=0) {
                    player.getBody().setLinearVelocity(
                            player.getBody().getLinearVelocity().x + accelx * 0.5f,
                            player.getBody().getLinearVelocity().y);
                }
                else{
                    player.getBody().setLinearVelocity(
                            player.getBody().getLinearVelocity().x + player.FORWARD_FORCE * 0.5f,
                            player.getBody().getLinearVelocity().y);
                }
                player.facingRight = true;

            }
        }
        else if ((accelx <-1) || (MyInput.isDown(MyInput.MOVE_LEFT))){
            if(player.getBody().getLinearVelocity().x > -player.PLAYER_MAX_SPEED) {
                if(accelx!=0) {
                    player.getBody().setLinearVelocity(
                            player.getBody().getLinearVelocity().x + accelx * 0.5f,
                            player.getBody().getLinearVelocity().y);
                }
                else{
                    player.getBody().setLinearVelocity(
                            player.getBody().getLinearVelocity().x - player.FORWARD_FORCE * 0.5f,
                            player.getBody().getLinearVelocity().y);
                }
                player.facingRight = false;
            }
        }

        if((accely >1)|| (MyInput.isDown(MyInput.MOVE_UP))) {
            if(player.getBody().getLinearVelocity().y < player.PLAYER_MAX_SPEED) {

                if(accely!=0) {
                    player.getBody().setLinearVelocity(
                            player.getBody().getLinearVelocity().x,
                            player.getBody().getLinearVelocity().y + accely * 0.5f);
                }
                else{
                    player.getBody().setLinearVelocity(
                            player.getBody().getLinearVelocity().x,
                            player.getBody().getLinearVelocity().y + player.FORWARD_FORCE * 0.5f);
                }
                player.facingRight = true;

            }
        }
        else if ((accely <-1)|| (MyInput.isDown(MyInput.MOVE_DOWN))) {
            if(player.getBody().getLinearVelocity().y > -player.PLAYER_MAX_SPEED) {

                if(accely !=0) {
                    player.getBody().setLinearVelocity(
                            player.getBody().getLinearVelocity().x,
                            player.getBody().getLinearVelocity().y + accely * 0.5f);
                }
                else{
                    player.getBody().setLinearVelocity(
                            player.getBody().getLinearVelocity().x,
                            player.getBody().getLinearVelocity().y - player.FORWARD_FORCE * 0.5f);
                }
                player.facingRight = false;
            }
        }

        //determine if moving or not
        if((Math.abs(player.getBody().getLinearVelocity().x) >1) ||(Math.abs(player.getBody().getLinearVelocity().y) >1) ){
            player.isMoving = true;
        }
        else{
            player.isMoving =false;
        }


        //reposition player based on input
        //player.getBody().applyForceToCenter(accelx,accely,true);

        //use this accumulator to make sure if there is graphics lag we don't get a giant physics movement, just several
        //small ones
        accum += delta;
        accum = Math.min(accum, maxAccum);
        while (accum > step) {
            world.step(step, 60, 10);
            accum -= step;

            //run a path behind the player
            updatePlayerPath();
        }
        world.step(accum, 60, 10);
        accum = 0;
        //world.step(delta,6,3);

        player.update(delta);


        stage.act(delta);
        stage.draw();


        //update the playtime
        playTime+=delta;
        //draw the current playtime
        batch.begin();
        font.draw(batch,convertPlaytimeToReadable(playTime),
                10,
                game.SCREEN_HEIGHT -30);
        batch.end();


        //draw box2d world
        if(debug) {

            //not currently working

            box2DCam.position.set(
                    game.SCREEN_WIDTH/2,
                    game.SCREEN_HEIGHT /2,
                    0
            );
            box2DRenderer.render(world, box2DCam.combined);

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
    private void createBlankActorsForPath(){
        playerPath = new Actor[blocksWide][blocksHigh];
        for(int x = 0; x< blocksWide; x++){
            for(int y = 0; y< blocksHigh; y++){
                //create an image in the appropriate spot and add it to the stage
                //TextureRegion myTextureRegion = game.atlas.findRegion("Wall");
                Image pathMarkerImage = new Image(horizontalMazeWall);
                pathMarkerImage.setSize(lineWidth ,lineHeight);
                pathMarkerImage.setPosition(
                        x * lineWidth,
                        y * lineHeight
                );
                pathMarkerImage.setColor(Color.TEAL);
                stage.addActor(pathMarkerImage);
                playerPath[x][y] = stage.getActors().get(stage.getActors().size -1);//add to our array of the path actors
                //move the tile to the back so we don't draw on top of player
                stage.getActors().get(stage.getActors().size -1).toBack();
            }
        }

    }
    private void updatePlayerPath(){
        //remove add to visited path if we have a new floor tile to check
        xBlockValue = (int) (player.getBody().getPosition().x * Box2DVars.PPM)/ lineWidth;
        yBlockValue = (int) (player.getBody().getPosition().y * Box2DVars.PPM) / lineHeight;


        //System.out.println("x: " + xBlockValue + "y: " + yBlockValue);

        if(visitedSquares[xBlockValue][yBlockValue] ==0){//if we have never visited this square, add an actor to mark path

            //create an image in the appropriate spot and add it to the stage
            //TextureRegion myTextureRegion = game.atlas.findRegion("Wall");
            Image pathMarkerImage = new Image(new TextureRegion(game.atlas.findRegion("Tracks")));
            pathMarkerImage.setSize(lineWidth *0.9f ,lineHeight*0.9f);
            pathMarkerImage.setPosition(
                    xBlockValue * lineWidth,
                    yBlockValue * lineHeight
            );
            //pathMarkerImage.setColor(Color.CYAN);
            //set the origin so we can rotate by the center
            pathMarkerImage.setOrigin(pathMarkerImage.getWidth()/2, pathMarkerImage.getHeight()/2);

            //rotate to the angle the player is facing
            pathMarkerImage.setRotation(calculateAngleWeAreMoving()-90);

            stage.addActor(pathMarkerImage);
            //move the tile to the back so we don't draw on top of player
            stage.getActors().get(stage.getActors().size -1).toBack();
            //stage.getRoot().removeActor(playerPath[xBlockValue][yBlockValue]);

            //now mark that spot as visited
            visitedSquares[xBlockValue][yBlockValue] =1;
        }
    }
    private void createDFSMaze(Vector2 currentPosition){


//        if(debug){
//            for(int x = 0; x< blocksWide; x++){
//                for(int y = 0; y< blocksHigh; y++){
//                    System.out.print(visitedSquares[x][y]);
//                }
//                System.out.print('\n');
//            }
//        }
//        System.out.print('\n');



        //mark the current position as visited
        visitedSquares[(int)currentPosition.x][(int)currentPosition.y] = 1;


        //1 = North, 2 = East, 3 = South, 4 = West
//        int nextSquareDirection = game.rand.nextInt((5 - 1) + 1) + 1;//1,2,3, or 4

        //create a randomized direction array
        ArrayList<Integer> directions = new ArrayList<Integer>();
        for(int i=1;i<=4;i++)
        {
            directions.add(i);
        }
        Collections.shuffle(directions);






        //make sure our next direction is within the confines of the outer maze edge
        for(int x = 0; x< 4; x++){
            switch(directions.get(x)){
                case 1:
                    //if it's too high, or we have already visited that square then check the next direction
                    if((currentPosition.y + 1 > blocksHigh-1) || (visitedSquares[(int)currentPosition.x][(int)currentPosition.y +1] ==1)){
                        break;
                    }
                    //if it isn't too high, then add to the stack, and check everything again from there
                    else{
                        //break down the wall
                        world.destroyBody(horizontalWalls[(int)currentPosition.x][(int)currentPosition.y +1]);

                        //travel to that spot now that we can get there
                        createDFSMaze(new Vector2(currentPosition.x, currentPosition.y + 1));
                    }

                    break;
                case 2:

                    //if it's too high, or we have already visited that square then check the next direction
                    if((currentPosition.x + 1 > blocksWide-1) || (visitedSquares[(int)currentPosition.x+1][(int)currentPosition.y ] ==1)){
                        break;
                    }
                    //if it isn't too high, then add to the stack, and check everything again from there
                    else{
                        //break down the wall
                        world.destroyBody(verticalWalls[(int)currentPosition.x +1][(int)currentPosition.y]);

                        //travel to that spot now that we can get there
                        createDFSMaze(new Vector2(currentPosition.x +1, currentPosition.y));
                    }


                    break;
                case 3:

                    //if it's too low, or we have already visited that square then check the next direction
                    if((currentPosition.y - 1 < 0) || (visitedSquares[(int)currentPosition.x][(int)currentPosition.y -1 ] ==1)){
                        break;
                    }
                    //if it isn't too high, then add to the stack, and check everything again from there
                    else{
                        //break down the wall
                        world.destroyBody(horizontalWalls[(int)currentPosition.x][(int)currentPosition.y ]);

                        //travel to that spot now that we can get there
                        createDFSMaze(new Vector2(currentPosition.x, currentPosition.y-1));
                    }

                    break;
                case 4:

                    //if it's too high, or we have already visited that square then check the next direction
                    if((currentPosition.x - 1 <0) || (visitedSquares[(int)currentPosition.x-1][(int)currentPosition.y ] ==1)){
                        break;
                    }
                    //if it isn't too high, then add to the stack, and check everything again from there
                    else{
                        //break down the wall
                        world.destroyBody(verticalWalls[(int)currentPosition.x ][(int)currentPosition.y]);

                        //travel to that spot now that we can get there
                        createDFSMaze(new Vector2(currentPosition.x -1, currentPosition.y));
                    }

                    break;
                default:
                    break;
            }
        }


    }

    private void addAllMazeWalls(){
        //initialize the array to hold all of the verticalWalls and horizontalWalls
        verticalWalls = new Body[blocksWide][blocksHigh];
        horizontalWalls = new Body[blocksWide][blocksHigh];

        verticalWallActor = new Actor[blocksWide][blocksHigh];
        horizontalWallActor = new Actor[blocksWide][blocksHigh];


        //create all of the walls
        for(int x = 0; x< blocksWide; x++){
            for(int y = 0; y< blocksHigh; y++){
                createLine(x, y, true);
                createLine(x, y, false);
            }
        }

    }
//    private void addAllMazeSquareSensors(){
//        //create all of the walls
//        for(int x = 0; x< blocksWide; x++){
//            for(int y = 0; y< blocksHigh; y++){
//                addSquareSensor(x, y);
//                addSquareSensor(x, y);
//            }
//        }
//    }
//    private void addSquareSensor(int xPosition, int yPosition){
//
//        xPosition *= lineWidth;
//        yPosition *= lineHeight;
//
//        //define body
//        BodyDef bdef = new BodyDef();
//
//        bdef.position.set(
//                (xPosition + lineWidth/2) / Box2DVars.PPM,
//                (yPosition + lineHeight/2) / Box2DVars.PPM);
//        bdef.type = BodyDef.BodyType.DynamicBody;
//
//        //create body
//        Body body = world.createBody(bdef);
//
//
//        //define Fixture
//        PolygonShape shape = new PolygonShape();
//        //shape.setRadius(lineWidth<lineHeight ?(lineWidth/2f)/Box2DVars.PPM:(lineHeight/2f)/Box2DVars.PPM);
//        shape.setAsBox(
//                0.9f*(lineWidth/2)/(Box2DVars.PPM),
//                0.9f*(lineHeight/2)/(Box2DVars.PPM));
//
//
//        FixtureDef fdef = new FixtureDef();
//        fdef.isSensor = true;
//        fdef.shape = shape;
//        //it is a sensor, and can only hit the player
//        fdef.filter.categoryBits = Box2DVars.BIT_SENSOR;
//        fdef.filter.maskBits = Box2DVars.BIT_PLAYER;
//        body.setSleepingAllowed(false);
//        body.createFixture(fdef).setUserData("floorSensor");//a tag to identify this later
//
//    }

    private void drawBorder(){
        //we cannot be contained within a box with box 2d. We need 4 separate lines

        //bottom border
        //define body
        BodyDef bdef = new BodyDef();
        bdef.position.set((game.SCREEN_WIDTH/ Box2DVars.PPM) /2,0);
        bdef.type = BodyDef.BodyType.StaticBody;
        //create body
        Body body = world.createBody(bdef);
        //define Fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(game.SCREEN_WIDTH/2 / Box2DVars.PPM, 0.1f / Box2DVars.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        body.createFixture(fdef).setUserData("border");//a tag to identify this later

        //now add a matching actor for that
        Image wall = new Image(horizontalMazeWall);
        wall.setSize(game.SCREEN_WIDTH,1.25f);
        wall.setColor(Color.BLACK);
        wall.setCenterPosition(
                body.getPosition().x * Box2DVars.PPM,
                body.getPosition().y * Box2DVars.PPM);
        stage.addActor(wall);

        // top border
        //define body
        bdef = new BodyDef();
        bdef.position.set((game.SCREEN_WIDTH/ Box2DVars.PPM) /2,(game.SCREEN_HEIGHT)/Box2DVars.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        //create body
        body = world.createBody(bdef);
        //define Fixture
        shape = new PolygonShape();
        shape.setAsBox(game.SCREEN_WIDTH/2 / Box2DVars.PPM, 0.1f / Box2DVars.PPM);
        fdef = new FixtureDef();
        fdef.shape = shape;
        body.createFixture(fdef).setUserData("border");//a tag to identify this later

        //add the actor
        wall = new Image(horizontalMazeWall);
        wall.setSize(game.SCREEN_WIDTH,1.25f);
        wall.setColor(Color.BLACK);
        wall.setCenterPosition(
                body.getPosition().x * Box2DVars.PPM,
                body.getPosition().y * Box2DVars.PPM);
        stage.addActor(wall);


        // left border
        //define body
        bdef = new BodyDef();
        bdef.position.set(0,(game.SCREEN_HEIGHT/2)/Box2DVars.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        //create body
        body = world.createBody(bdef);
        //define Fixture
        shape = new PolygonShape();
        shape.setAsBox( 0.1f / Box2DVars.PPM, game.SCREEN_HEIGHT/2);
        fdef = new FixtureDef();
        fdef.shape = shape;
        body.createFixture(fdef).setUserData("border");//a tag to identify this later

        //add the actor
        wall = new Image(horizontalMazeWall);
        wall.setSize(1.25f,game.SCREEN_HEIGHT);
        wall.setColor(Color.BLACK);
        wall.setCenterPosition(
                body.getPosition().x * Box2DVars.PPM,
                body.getPosition().y * Box2DVars.PPM);
        stage.addActor(wall);

        // right border
        //define body
        bdef = new BodyDef();
        bdef.position.set(game.SCREEN_WIDTH / Box2DVars.PPM,(game.SCREEN_HEIGHT/2)/Box2DVars.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        //create body
        body = world.createBody(bdef);
        //define Fixture
        shape = new PolygonShape();
        shape.setAsBox( 0.1f / Box2DVars.PPM, game.SCREEN_HEIGHT/2);
        fdef = new FixtureDef();
        fdef.shape = shape;
        body.createFixture(fdef).setUserData("border");//a tag to identify this later

        //add the actor
        wall = new Image(horizontalMazeWall);
        wall.setSize(1.25f,game.SCREEN_HEIGHT);
        wall.setColor(Color.BLACK);
        wall.setCenterPosition(
                body.getPosition().x * Box2DVars.PPM,
                body.getPosition().y * Box2DVars.PPM);
        stage.addActor(wall);

    }

    private void createPlayer(int xPosition, int yPosition){

        xPosition *= lineWidth;
        yPosition *= lineHeight;

        //define body
        BodyDef bdef = new BodyDef();
        bdef.position.set((xPosition + lineWidth/2) / Box2DVars.PPM,  (yPosition+lineHeight/2) /Box2DVars.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        //create body
        Body body = world.createBody(bdef);

        //define Fixture
        CircleShape shape = new CircleShape();
        shape.setRadius(lineWidth<lineHeight ?(lineWidth/4.6f)/Box2DVars.PPM:(lineHeight/4.6f)/Box2DVars.PPM);



        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = Box2DVars.BIT_PLAYER;
        body.setSleepingAllowed(false);
        body.createFixture(fdef).setUserData("player");//a tag to identify this later

        player = new Player(
                game,
                body,
                lineWidth<lineHeight ? (lineWidth)/Box2DVars.PPM : (lineHeight)/Box2DVars.PPM  ,
                lineWidth<lineHeight ? (lineWidth)/Box2DVars.PPM  : (lineHeight)/Box2DVars.PPM );
        stage.addActor(player.getGroup());
    }


    private void createLine(int xPosition, int yPosition, boolean horizontal){


        xPosition *= lineWidth;
        yPosition *= lineHeight;

        //define body
        BodyDef bdef = new BodyDef();
        if(horizontal) {
            bdef.position.set((xPosition + lineWidth/2) / Box2DVars.PPM, yPosition / Box2DVars.PPM);
        }
        else{
            bdef.position.set(xPosition / Box2DVars.PPM, (yPosition+lineHeight/2) / Box2DVars.PPM);

        }
        bdef.type = BodyDef.BodyType.StaticBody;

        ///adding chainshape to make it so we don't get stuck on the separating lines/////
        //use chainShape to prevent getting stuck between boxes
        ChainShape cs = new ChainShape();

        Vector2[] v = new Vector2[5];
        if(horizontal) {
            //we are using 3 corners of the box
            v[0] = new Vector2(
                    -(lineWidth / 2) / Box2DVars.PPM ,
                    -0.003f );
            v[1] = new Vector2(
                    -(lineWidth / 2) / Box2DVars.PPM,
                    .003f);
            v[2] = new Vector2(
                    (lineWidth / 2) / Box2DVars.PPM,
                    .003f);
            v[3] = new Vector2(
                    (lineWidth / 2) / Box2DVars.PPM,
                    -0.003f);
            v[4] = new Vector2(
                    -(lineWidth / 2) / Box2DVars.PPM,
                    -0.003f);
            cs.createChain(v);
        }
        else{//vertical
            //we are using 3 corners of the box
            v[0] = new Vector2(
                    -0.003f,
                    -(lineHeight / 2) / Box2DVars.PPM
                    );
            v[1] = new Vector2(
                    .003f,
                    -(lineHeight / 2) / Box2DVars.PPM
                    );
            v[2] = new Vector2(
                    .003f,
                    (lineHeight / 2) / Box2DVars.PPM
                    );
            v[3] = new Vector2(
                    -0.003f,
                    (lineHeight / 2) / Box2DVars.PPM
                    );
            v[4] = new Vector2(
                    -0.003f,
                    (-lineHeight / 2) / Box2DVars.PPM
                    );
            cs.createChain(v);
        }
        ///////////////////////////////////////
        //create body
        Body body = world.createBody(bdef);

        //define Fixture
//        PolygonShape shape = new PolygonShape();
//        if(horizontal) {
//            shape.setAsBox(lineWidth/2 / Box2DVars.PPM, 0.1f / Box2DVars.PPM);
//        }
//        else{
//            shape.setAsBox(0.1f / Box2DVars.PPM, lineHeight/2 / Box2DVars.PPM);
//        }


        FixtureDef fdef = new FixtureDef();
        fdef.shape = cs;//changing from shape to cs
        fdef.friction = 0;
        fdef.filter.categoryBits = Box2DVars.BIT_WALL;
        body.createFixture(fdef).setUserData("wall");//a tag to identify this later
        cs.dispose();

        //add the new body to the array of bodies
        if(horizontal) {
            horizontalWalls[xPosition / lineWidth][yPosition / lineHeight] = body;
        }
        else{
            verticalWalls[xPosition / lineWidth][yPosition / lineHeight] = body;
        }

        //now add the line to the stage
        Image wall = new Image(horizontalMazeWall);
        if(horizontal){
            wall.setSize(lineWidth,1.25f);
        }
        else {
            wall.setSize(1.25f,lineHeight);
        }
        wall.setCenterPosition(
                body.getPosition().x * Box2DVars.PPM,
                body.getPosition().y * Box2DVars.PPM);
        wall.setColor(Color.BLACK);
        stage.addActor(wall);
        //add the actor we just put in my using the size
        //wallsArray.add(stage.getActors().get(stage.getActors().size-1));
        if(horizontal) {
            horizontalWallActor[xPosition / lineWidth][yPosition / lineHeight] = stage.getActors().get(stage.getActors().size-1);
        }
        else{
            verticalWallActor[xPosition / lineWidth][yPosition / lineHeight] = stage.getActors().get(stage.getActors().size-1);
        }
    }

    private void flingCamera(){
        velX *= 0.95f;
        velY *= 0.95f;

        //update camera to new spot
        camera.position.set(
                camera.position.x -velX * Gdx.graphics.getDeltaTime(),
                camera.position.y + velY * Gdx.graphics.getDeltaTime(),
                0);
        //push back into limits
        pushCameraBackIntoLimits();

        //slow down,and stop flinging if too slow
        if (Math.abs(velX) < 0.25f) velX = 0;//if velocities are below a threshold, then set to zero
        if (Math.abs(velY) < 0.25f) velY = 0;
        if ((velX == 0) && (velY == 0)) {//if both velocities are zero, stop running this flinging
            flinging = false;
        }

    }
    private void setStageLimits(){
        x_left_limit = (camera.viewportWidth *camera.zoom)/2;
        x_right_limit = game.SCREEN_WIDTH - (camera.viewportWidth*camera.zoom) / 2;
        y_bottom_limit = (camera.viewportHeight*camera.zoom) / 2;
        y_top_limit = (game.SCREEN_HEIGHT - (camera.viewportHeight*camera.zoom) /2);
    }
    @Override
    public void resize(int width, int height) {
        originalZoomLevelX = camera.viewportWidth/width;
        originalZoomLevelY = camera.viewportHeight/height;
        currentZoomLevelX = originalZoomLevelX;
        currentZoomLevelY = originalZoomLevelY;
    }
    private boolean isInStageLimits(Vector3 newPosition){

        //if out of bounds in x or y, return false. else it's good.
        if(newPosition.x < x_left_limit ||newPosition.x > x_right_limit){
            return false;
        }
        if (newPosition.y < y_bottom_limit || newPosition.y > y_top_limit){
            return false;
        }
        else{
            return true;
        }

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

    class MyGestureListener implements GestureDetector.GestureListener {
        //gesture listener x and y values are local to the screen. ie, in the middle of the world, still
        //the x value will be 0 on the left, and the screen width value on the right.
        Vector3 newPosition;

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            initialZoomTouchdown = true;
            flinging = false;
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {

//            TiledMapTileLayer.Cell cell = layer.getCell(col, row);
            //System.out.println("x: " + x);
//            camera.zoom = 0.75f;
//            currentZoomLevelX = originalZoomLevelX * camera.zoom;
//            currentZoomLevelY = originalZoomLevelY * camera.zoom;

            return false;
        }

        @Override
        public boolean longPress(float x, float y) {

            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            //System.out.println("flinging");
            flinging = true;
            velX = currentZoomLevelX * velocityX;
            velY = currentZoomLevelY * velocityY;
            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            System.out.println("panning");

            //get the new position
            newPosition = new Vector3(
                    camera.position.x - (deltaX * currentZoomLevelX),
                    camera.position.y + (deltaY * currentZoomLevelY),
                    0

            );
            camera.position.set(newPosition);
            pushCameraBackIntoLimits();




            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            System.out.println("stopping panning");
            movingBlockBeyondBorders = false;
            return false;
        }

        @Override
        public boolean zoom (float originalDistance, float currentDistance){

            if(initialZoomTouchdown){
                currentCameraZoom = camera.zoom;
                initialZoomTouchdown = false;
            }

            float ratio = originalDistance / currentDistance;
            if((currentCameraZoom * ratio <1)&&(currentCameraZoom*ratio>0.2)) {
                camera.zoom = currentCameraZoom * ratio;
                currentZoomLevelX = originalZoomLevelX * camera.zoom;
                currentZoomLevelY = originalZoomLevelY * camera.zoom;

                //reset the window limits based on the new zoom
                setStageLimits();

                //if we have zoomed beyond the stage limits, move back in
                pushCameraBackIntoLimits();

            }

            return false;
        }

        @Override
        public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer){
            System.out.println("pinching");

            return false;
        }
    }

    private void goBackToMenu(){

        //easy maze is * 1, medium is  * 2, hard is * 4, and ridiculous is * 8
        score = (int) (100 * longestDistance/playTime) * mazeType;
        System.out.println(score);

        //save the end of game
        saveNumberOfGamesCompleted();

        //reset all of our shared variables
        game.resetSharedVariables();



        //run the victory animation, then return to the menu
        game.setScreen(new VictoryScreen(game, playTime, score, newRecord));
    }
    //moving directly to the right is zero degrees
    private float calculateAngleWeAreMoving(){



        //body is moving predominantly left or right
        if (Math.abs(player.getBody().getLinearVelocity().x) > Math.abs(player.getBody().getLinearVelocity().y)){
            if(player.getBody().getLinearVelocity().x > 0){
                return 0;
            }
            else{
                return 180;
            }
        }

        else{
            if(player.getBody().getLinearVelocity().y>0){
                return 90;
            }
            else{
                return 270;
            }
        }

//        //http://stackoverflow.com/questions/22421054/determine-movement-vectors-direction-from-velocity
//
//        float addDeg = 0;
//        if(body.getLinearVelocity().x<0) {
//            addDeg = body.getLinearVelocity().y > 0 ? 180 : 270;
//        }
//        else if(body.getLinearVelocity().y<=0){
//            addDeg = 360;
//        }
//
//        return ((float)Math.abs(
//                Math.abs(
//                        Math.atan(
//                                body.getLinearVelocity().y/body.getLinearVelocity().x)*180/Math.PI)-addDeg));
    }
    //save the fact that the player solved the maze
    void saveNumberOfGamesCompleted(){

        //add one to the total number of games played
        SaveManager saveManager = new SaveManager(game.saveEncrypted);
        int numGamesPlayed = 0;
        float bestTime = playTime;

        int bestScore = score;



        if(mazeType == game.EASY_MAZE_TYPE ){
            //check if we have any previous data to add to
            if(saveManager.loadDataValue("numberOfEasyMazesSolved",Integer.class)!=null){
                numGamesPlayed = saveManager.loadDataValue("numberOfEasyMazesSolved",Integer.class);
            }
            //check against previous data
            if(saveManager.loadDataValue("bestEasyTime",Float.class)!=null){
                if(bestTime > (Float) saveManager.loadDataValue("bestEasyTime",Float.class)) {
                    bestTime = saveManager.loadDataValue("bestEasyTime", Float.class);
                }

            }
            //check against previous score
            if(saveManager.loadDataValue("bestEasyScore",Integer.class)!=null){
                if(score > (Integer) saveManager.loadDataValue("bestEasyScore",Integer.class)) {
                    bestScore = score;
                }
                else{
                    bestScore = saveManager.loadDataValue("bestEasyScore", Integer.class);
                    newRecord = false;
                }
            }

            //actually save the data
            saveManager.saveDataValue("bestEasyScore",bestScore);
            saveManager.saveDataValue("numberOfEasyMazesSolved",numGamesPlayed +1);
            saveManager.saveDataValue("bestEasyTime",bestTime);

        }
        else if(mazeType == game.MEDIUM_MAZE_TYPE){
            if(saveManager.loadDataValue("numberOfMediumMazesSolved",Integer.class)!=null){
                numGamesPlayed = saveManager.loadDataValue("numberOfMediumMazesSolved",Integer.class);
            }

            //check against previous data
            if(saveManager.loadDataValue("bestMediumTime",Float.class)!=null){
                if(bestTime > (Float) saveManager.loadDataValue("bestMediumTime",Float.class)) {
                    bestTime = saveManager.loadDataValue("bestMediumTime", Float.class);
                }

            }
            //check against previous score
            if(saveManager.loadDataValue("bestMediumScore",Integer.class)!=null){
                if(score > (Integer) saveManager.loadDataValue("bestMediumScore",Integer.class)) {
                    newRecord = true;
                    bestScore = score;
                }
                else{
                    bestScore = saveManager.loadDataValue("bestMediumScore", Integer.class);
                    newRecord = false;

                }
            }

            //actually save the data
            saveManager.saveDataValue("bestMediumScore",bestScore);
            saveManager.saveDataValue("numberOfMediumMazesSolved",numGamesPlayed +1);
            saveManager.saveDataValue("bestMediumTime",bestTime);

        }
        else if(mazeType == game.HARD_MAZE_TYPE){
            if(saveManager.loadDataValue("numberOfHardMazesSolved",Integer.class)!=null){
                numGamesPlayed = saveManager.loadDataValue("numberOfHardMazesSolved",Integer.class);
            }

            //check against previous data
            if(saveManager.loadDataValue("bestHardTime",Float.class)!=null){
                if(bestTime > (Float) saveManager.loadDataValue("bestHardTime",Float.class)) {
                    bestTime = saveManager.loadDataValue("bestHardTime", Float.class);
                }

            }
            //check against previous score
            if(saveManager.loadDataValue("bestHardScore",Integer.class)!=null){
                if(score > (Integer) saveManager.loadDataValue("bestHardScore",Integer.class)) {
                    newRecord = true;
                    bestScore = score;
                }
                else{
                    bestScore = saveManager.loadDataValue("bestHardScore", Integer.class);
                    newRecord = false;

                }
            }

            //actually save the data
            saveManager.saveDataValue("bestHardScore",bestScore);
            saveManager.saveDataValue("numberOfHardMazesSolved",numGamesPlayed +1);
            saveManager.saveDataValue("bestHardTime",bestTime);

        }
        else if(mazeType == game.RIDICULOUS_MAZE_TYPE){

            if(saveManager.loadDataValue("numberOfRidiculousMazesSolved",Integer.class)!=null){
                numGamesPlayed = saveManager.loadDataValue("numberOfRidiculousMazesSolved",Integer.class);
            }

            //check against previous data
            if(saveManager.loadDataValue("bestRidiculousTime",Float.class)!=null){
                if(bestTime > (Float) saveManager.loadDataValue("bestRidiculousTime",Float.class)) {
                    bestTime = saveManager.loadDataValue("bestRidiculousTime", Float.class);
                }

            }
            //check against previous score
            if(saveManager.loadDataValue("bestRidiculousScore",Integer.class)!=null){
                if(score > (Integer) saveManager.loadDataValue("bestRidiculousScore",Integer.class)) {
                    newRecord = true;
                    bestScore = score;
                }
                else{
                    bestScore = saveManager.loadDataValue("bestRidiculousScore", Integer.class);
                    newRecord = false;

                }
            }

            //actually save the data
            saveManager.saveDataValue("bestRidiculousScore",bestScore);
            saveManager.saveDataValue("numberOfRidiculousMazesSolved",numGamesPlayed +1);
            saveManager.saveDataValue("bestRidiculousTime",bestTime);

        }

    }

    private void pushCameraBackIntoLimits(){
        if(camera.position.x < x_left_limit){
            camera.position.x = x_left_limit;
        }
        else if(camera.position.x > x_right_limit){
            camera.position.x = x_right_limit;
        }
        if(camera.position.y < y_bottom_limit){
            camera.position.y = y_bottom_limit;
        }
        else if(camera.position.y > y_top_limit){
            camera.position.y = y_top_limit;
        }
    }
}
