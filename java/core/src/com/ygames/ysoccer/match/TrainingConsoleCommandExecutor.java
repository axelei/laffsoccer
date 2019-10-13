package com.ygames.ysoccer.match;

import com.strongjoshua.console.annotation.HiddenCommand;

public class TrainingConsoleCommandExecutor extends ConsoleCommandExecutor {

    private Training training;

    public TrainingConsoleCommandExecutor(Training training) {
        this.training = training;
    }

    @HiddenCommand
    public void ballPosition() {
        console.log("(" + training.ball.x + ", " + training.ball.y + ", " + training.ball.z + ")");
    }

    public void ballPosition(float x, float y, float z) {
        training.ball.setPosition(x, y, z);
    }
}
