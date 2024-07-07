package com.pot_pot_golf.Core;

/**
 * The logic interface.
 * This interface is responsible for the logic of the game.
 */
public interface ILogic {
    void init(MouseInput mouseInput) throws Exception;
    void input();
    void update();
    void render();
    void cleanUp();
}
