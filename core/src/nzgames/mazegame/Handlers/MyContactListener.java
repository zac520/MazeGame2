package nzgames.mazegame.Handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

/**
 * Created by zac520 on 7/10/14.
 */
public class MyContactListener implements ContactListener {

    private boolean endMaze = false;
    public MyContactListener(){
        super();
    }
    //called when two fixtures begin to collide
    public void beginContact (Contact c){
        //System.out.println("Begin Contact");
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        //System.out.println("contact between " + fa.getUserData() + " and " + fb.getUserData());

        //if "end" and "player" collide, then end the maze
        if(fa.getUserData() != null && fa.getUserData().equals("end")){
            if(fb.getUserData() != null && fb.getUserData().equals("player")) {
                endMaze = true;
            }

        }
        if(fb.getUserData() != null && fb.getUserData().equals("end")){
            if(fa.getUserData() != null && fa.getUserData().equals("player")) {
                endMaze = true;
            }
        }

    }

    //called when two fixtures no longer collide
    public void endContact (Contact c) {



    }

    //collision detection
    //collision handling
    public void preSolve (Contact c, Manifold m) {}

    //whatever happens after
    public void postSolve (Contact c, ContactImpulse ci) {}

    public boolean checkEndMaze(){
        return endMaze;
    }
}
