package com.ygames.ysoccer.match;

import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.annotation.HiddenCommand;

public class ConsoleCommandExecutor extends CommandExecutor {

    @HiddenCommand
    public void gravity() {
        console.log("gravity " + Const.GRAVITY);
    }

    public void gravity(float f) {
        Const.GRAVITY = f;
    }

    @HiddenCommand
    public void airFriction() {
        console.log("airFriction " + Const.AIR_FRICTION);
    }

    public void airFriction(float f) {
        Const.AIR_FRICTION = f;
    }

    @HiddenCommand
    public void spinFactor() {
        console.log("spinFactor " + Const.SPIN_FACTOR);
    }

    public void spinFactor(float f) {
        Const.SPIN_FACTOR = f;
    }

    @HiddenCommand
    public void spinDampening() {
        console.log("spinDampening " + Const.SPIN_DAMPENING);
    }

    public void spinDampening(float f) {
        Const.SPIN_DAMPENING = f;
    }

    @HiddenCommand
    public void bounce() {
        console.log("bounce " + Const.BOUNCE);
    }

    public void bounce(float f) {
        Const.BOUNCE = f;
    }

    @HiddenCommand
    public void passingThreshold() {
        console.log("passingThreshold " + Const.PASSING_THRESHOLD);
    }

    public void passingThreshold(float f) {
        Const.PASSING_THRESHOLD = f;
    }

    @HiddenCommand
    public void passingSpeedFactor() {
        console.log("passingSpeedFactor " + Const.PASSING_SPEED_FACTOR);
    }

    public void passingSpeedFactor(float f) {
        Const.PASSING_SPEED_FACTOR = f;
    }
}
