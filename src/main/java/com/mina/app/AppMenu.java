package com.mina.app;
import com.mina.engine.Input;
import com.mina.engine.Output;
import com.mina.engine.Survey;
import com.mina.engine.Test;
import java.util.Scanner;

public class AppMenu {
    private final Scanner sc;
    private Survey survey;
    private Test test;

    public AppMenu(Scanner sc) {
        this.sc = sc;
    }

    public void run() {
        while (true) {
            Output.printMenu1ChooseSurveyTest();
            int choice = Input.checkInt(sc, 1, 3);

            switch (choice) {
                case 1 -> survey = new SurveyMenu(sc, survey).run();
                case 2 -> test = new TestMenu(sc, test).run();
                case 3 -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
            }
        }
    }
}
