package com.example.um_project_golf.UserInterface;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MyController implements Initializable {

    @FXML
    private Label title;

    @FXML
    private ChoiceBox<String> solver;
    private String[] solvers = {"Euler solver", "other solver"};


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        solver.getItems().addAll(solvers);
    }
}

