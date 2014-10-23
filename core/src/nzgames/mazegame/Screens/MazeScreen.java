package nzgames.mazegame.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import nzgames.mazegame.Actors.Player;
import nzgames.mazegame.Handlers.Box2DVars;
import nzgames.mazegame.MainGame;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by zac520 on 10/22/14.
 */
public class MazeScreen implements Screen {
    OrthographicCamera camera;
    MainGame game;
    private World world;
    OrthographicCamera box2DCam;
    Box2DDebugRenderer box2DRenderer;
    private boolean debug = true;
    private int lineWidth = 10;
    private int lineHeight = 10;

    private int blocksWide = 48;
    private int blocksHigh = 36;

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

    public MazeScreen(MainGame myGame, int height, int width) {
        game = myGame;

        //set up the block height and width
        blocksHigh = height;
        blocksWide = width;

        //set the camera up
        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.SCREEN_WIDTH, game.SCREEN_HEIGHT);

        stage= new Stage();
        stage.getViewport().setCamera(camera);

        //set up box2d renderer
        box2DRenderer = new Box2DDebugRenderer();

        //set up box2dcam
        box2DCam = new OrthographicCamera();
        box2DCam.setToOrtho(false, game.SCREEN_WIDTH / Box2DVars.PPM, game.SCREEN_HEIGHT / Box2DVars.PPM);

        //add the world for the box2d bodies
        world = new World(new Vector2(0, 0), true);

        //divide the world into equal squares (48 wide and 36 tall for now)
        lineWidth = game.SCREEN_WIDTH / blocksWide;
        lineHeight = game.SCREEN_HEIGHT / blocksHigh;

        //initialize the visited array
        visitedSquares = new int[blocksWide][blocksHigh];
        for (int x = 0; x < blocksWide; x++) {
            for (int y = 0; y < blocksHigh; y++) {
                //make all as zero to represent false, or unvisited
                visitedSquares[x][y] = 0;
            }
        }


        horizontalMazeWall = new TextureRegion(game.atlas.findRegion("EmptySelectionUp"));

        //add up all the walls (we destroy them as we make the maze, leaving only the usable maze walls)
        addAllMazeWalls();

        //create the maze border
        drawBorder();

        //create a circle at the starting point
        createPlayer(0, 0);

        //create maze via recursion
//        //put us at position 0,0, and mark that square as visited
//        Vector2 currentPosition = new Vector2(0, 0);
//        visitedSquares[0][0] = 1;
        //createDFSMaze(currentPosition);

        //test
        createMazeWithoutRecursion();

    }
    private void createMazeWithoutRecursion(){
        //had to do it without recursion because stack is way too large to make a maze

        //put us at position 0,0, and mark that square as visited
        Vector2 currentPosition = new Vector2(0, 0);
        visitedSquares[0][0] = 1;

        positionStack = new Array<Vector2>();
        int nextSquareDirection;
        positionStack.add(currentPosition);
        while (positionStack.size > 0) {

            //choose a random direction
            nextSquareDirection = game.rand.nextInt((5 - 1) + 1) + 1;//1,2,3, or 4

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

                        //add the current position to the stack
                        positionStack.add(new Vector2(currentPosition));

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

                        //add the current position to the stack
                        positionStack.add(new Vector2(currentPosition));
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

                        //add the current position to the stack
                        positionStack.add(new Vector2(currentPosition));


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

                        //add the current position to the stack
                        positionStack.add(new Vector2(currentPosition));

                    }

                    break;
                default:
                    break;
            }

            //now that we have checked our random integer, check all of the other directions. If they all pass, pop off stack
            if (deadEndCheck(currentPosition)) {
                currentPosition = positionStack.get(positionStack.size -1);
                positionStack.pop();
            }
            visitedSquares[(int)currentPosition.x][(int)currentPosition.y] = 1;

        }
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

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //get the accelerometer input
        accelx = Gdx.input.getAccelerometerY();
        accely = -Gdx.input.getAccelerometerX();


        if(accelx >1) {
            if(player.getBody().getLinearVelocity().x < player.PLAYER_MAX_SPEED) {
                player.getBody().setLinearVelocity(
                        player.getBody().getLinearVelocity().x + accelx *0.05f,
                        player.getBody().getLinearVelocity().y);

                player.facingRight = true;

            }
        }
        else if (accelx <-1) {
            if(player.getBody().getLinearVelocity().x > -player.PLAYER_MAX_SPEED) {
                //player.getBody().applyForceToCenter(-player.FORWARD_FORCE, 0, true);
                player.getBody().setLinearVelocity(
                        player.getBody().getLinearVelocity().x + accelx *0.05f,
                        player.getBody().getLinearVelocity().y);

                player.facingRight = false;
            }
        }

        if(accely >1) {
            if(player.getBody().getLinearVelocity().y < player.PLAYER_MAX_SPEED) {
                player.getBody().setLinearVelocity(
                        player.getBody().getLinearVelocity().x ,
                        player.getBody().getLinearVelocity().y+ accely *0.05f);

                player.facingRight = true;

            }
        }
        else if (accely <-1) {
            if(player.getBody().getLinearVelocity().y > -player.PLAYER_MAX_SPEED) {
                //player.getBody().applyForceToCenter(-player.FORWARD_FORCE, 0, true);
                player.getBody().setLinearVelocity(
                        player.getBody().getLinearVelocity().x,
                        player.getBody().getLinearVelocity().y+ accely *0.05f);

                player.facingRight = false;
            }
        }

        //reposition player based on input
        //player.getBody().applyForceToCenter(accelx,accely,true);

        world.step(delta,6,3);

        player.update(delta);

        stage.act(delta);
        stage.draw();



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


        //create all of the verticalWalls
        for(int x = 0; x< blocksWide; x++){
            for(int y = 0; y< blocksHigh; y++){
                createLine(x, y, true);
                createLine(x, y, false);
            }
        }

    }
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
        wall.setCenterPosition(
                body.getPosition().x * Box2DVars.PPM,
                body.getPosition().y * Box2DVars.PPM);
        stage.addActor(wall);

    }

    private void createPlayer(int xPosition, int yPosition){
        //define body
        BodyDef bdef = new BodyDef();
        bdef.position.set(xPosition + lineWidth/2 /Box2DVars.PPM, yPosition+lineHeight/2 / Box2DVars.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        //create body
        Body body = world.createBody(bdef);

        //define Fixture
        CircleShape shape = new CircleShape();
        shape.setRadius(lineWidth<lineHeight ?(lineWidth/4)/Box2DVars.PPM:(lineHeight/4)/Box2DVars.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        body.createFixture(fdef).setUserData("player");//a tag to identify this later

        player = new Player(game,body,shape.getRadius()*3,shape.getRadius()*3);
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

        //create body
        Body body = world.createBody(bdef);

        //define Fixture
        PolygonShape shape = new PolygonShape();
        if(horizontal) {
            shape.setAsBox(lineWidth/2 / Box2DVars.PPM, 0.1f / Box2DVars.PPM);
        }
        else{
            shape.setAsBox(0.1f / Box2DVars.PPM, lineHeight/2 / Box2DVars.PPM);
        }
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        body.createFixture(fdef).setUserData("wall");//a tag to identify this later

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
