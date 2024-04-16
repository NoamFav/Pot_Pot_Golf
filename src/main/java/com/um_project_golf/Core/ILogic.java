package com.um_project_golf.Core;

public interface ILogic {
    void init() throws Exception;
    void input();
    void update(MouseInput mouseInput);
    void render();
    void cleanUp();
}
