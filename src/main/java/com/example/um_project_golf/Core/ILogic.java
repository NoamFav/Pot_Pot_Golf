package com.example.um_project_golf.Core;

public interface ILogic {
    void init() throws Exception;
    void input();
    void update();
    void render();
    void cleanUp();
}
