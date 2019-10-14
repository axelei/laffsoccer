package com.ygames.ysoccer.match;

import com.strongjoshua.console.annotation.HiddenCommand;
import com.ygames.ysoccer.framework.EMath;

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

    @HiddenCommand
    public void ballSpeed() {
        console.log("(" + training.ball.v * EMath.cos(training.ball.a) + ", " + training.ball.v * EMath.sin(training.ball.a) + ", " + training.ball.vz + ")");
    }

    public void ballSpeed(float vx, float vy, float vz) {
        training.ball.v = EMath.hypo(vx, vy);
        training.ball.a = EMath.aTan2(vy, vx);
        training.ball.vz = vz;
    }
}
