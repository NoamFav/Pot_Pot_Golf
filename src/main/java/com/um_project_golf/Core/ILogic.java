package com.um_project_golf.Core;

/**
 * The logic interface.
 * This interface is responsible for the logic of the game.
 */
public interface ILogic {
    void init() throws Exception;
    void input();
    void update(MouseInput mouseInput);
    void render();
    void cleanUp();
}
