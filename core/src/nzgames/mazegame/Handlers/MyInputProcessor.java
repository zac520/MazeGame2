package nzgames.mazegame.Handlers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by zac520 on 7/17/14.
 */
public class MyInputProcessor extends Stage {

    public boolean keyDown(int k){
        if(k == Input.Keys.UP){
            MyInput.setKey(MyInput.MOVE_UP, true);
        }

        if(k == Input.Keys.DOWN){
            MyInput.setKey(MyInput.MOVE_DOWN, true);

        }

        if(k == Input.Keys.RIGHT){
            MyInput.setKey(MyInput.MOVE_RIGHT, true);

        }
        if(k == Input.Keys.LEFT){
            MyInput.setKey(MyInput.MOVE_LEFT, true);

        }
        return true;
    }

    public boolean keyUp(int k){
        if(k == Input.Keys.DOWN){
            MyInput.setKey(MyInput.MOVE_DOWN, false);
        }

        if(k == Input.Keys.UP){
            MyInput.setKey(MyInput.MOVE_UP, false);

        }
        if(k == Input.Keys.RIGHT){
            MyInput.setKey(MyInput.MOVE_RIGHT, false);

        }
        if(k == Input.Keys.LEFT){
            MyInput.setKey(MyInput.MOVE_LEFT, false);

        }
        return true;
    }
    @Override
         public boolean touchDown(int x, int y, int pointer, int button) {

//        if(x< Gdx.graphics.getWidth()/2) {
//            MyInput.setKey(MyInput.MAGIC, true);
//        }
//        else{
//            MyInput.setKey(MyInput.JUMP, true);
//
//        }

        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
//        MyInput.setKey(MyInput.JUMP, false);
//        MyInput.setKey(MyInput.MAGIC, false);


        return true;
    }


}

