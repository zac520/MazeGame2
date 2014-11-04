package nzgames.mazegame.Handlers;


import nzgames.mazegame.Screens.MenuScreen;

public interface IActivityRequestHandler {

    public void sendScore(int score);
    public String getScore(MenuScreen menuScreen);

}