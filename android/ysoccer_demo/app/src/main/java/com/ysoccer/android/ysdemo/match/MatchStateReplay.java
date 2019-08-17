package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.ysdemo.Assets;
import com.ysoccer.android.ysdemo.R;
import com.ysoccer.android.ysdemo.Settings;

class MatchStateReplay extends MatchState {

    private int subframe0;
    private boolean paused;
    private boolean slowMotion;
    boolean keyPause;
    private int position;
    private InputDevice controllingDevice;

    MatchStateReplay(Match match) {
        super(match);
        id = MatchFsm.STATE_REPLAY;
    }

    @Override
    void entryActions() {

        match.renderer.displayControlledPlayer = false;
        match.renderer.displayBallOwner = false;
        match.renderer.displayGoalScorer = false;
        match.renderer.displayTime = false;
        match.renderer.displayWindVane = true;
        match.renderer.displayScore = false;
        match.renderer.displayStatistics = false;
        match.renderer.displayRadar = false;
        match.renderer.displayControls = true;

        subframe0 = match.subframe;

        match.subframe = (subframe0 + 1) % Const.REPLAY_SUBFRAMES;

        paused = false;

        //control keys
//		keyPause = KeyDown(KEY_P);

        //position of current frame inside the replay vector
        position = 0;

        controllingDevice = null;

    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        //toggle pause
//		if (KeyDown(KEY_P) && keyPause == false) {
//			paused = !paused;
//		}
//		keyPause = KeyDown(KEY_P);

        slowMotion = false;
        if (match.glGame.touchInput.fire21) {
            slowMotion = true;
        }

        //set/unset controlling device
//		if (controllingDevice == null) { 
//			for( Local d:t_input = EachIn input_devices) {
//				if (d.fire2_down()) {
//					controllingDevice = d;
//				}
//			}
//		} else {
//			if (controllingDevice.fire2Down()) {
//				controllingDevice = null;
//			}
//		}

        //set speed
        int speed;
        if (controllingDevice != null) {
            InputDevice d = controllingDevice;
            speed = 12 * d.x1 - 2 * d.y1 + 8 * Math.abs(d.x1) * d.y1;
        } else if (slowMotion) {
            speed = GLGame.SUBFRAMES / 2;
        } else {
            speed = GLGame.SUBFRAMES;
        }

        //set position
        if (!paused) {
            position += speed;

            //limits
            position = Math.max(position, 1);
            position = Math.min(position, Const.REPLAY_SUBFRAMES);

            match.subframe = (subframe0 + position) % Const.REPLAY_SUBFRAMES;
        }
    }

    @Override
    void checkConditions() {
        //quit on 'ESC'
//		if (KeyDown(KEY_ESCAPE)) {
//			quit();
//			return;
//		}

        //quit on fire button
        if ((match.team[Match.HOME].fire1Down() != null)
                || (match.team[Match.AWAY].fire1Down() != null)) {
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_STARTING_POSITIONS);
            return;
        }

        //quit on last position
        if ((position == Const.REPLAY_SUBFRAMES) && (controllingDevice == null)) {
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_STARTING_POSITIONS);
            return;
//			quit();
//			return;
        }

    }

    @Override
    void render() {
        super.render();

        if ((match.subframe / GLGame.SUBFRAMES) % 32 > 16) {
            match.renderer.glGraphics.text14u(
                    match.glGame.getResources().getString(R.string.AUTO_REPLAY),
                    -Settings.guiOriginX + 34, -Settings.guiOriginY + 28, Assets.ucode14, +1);
        }
//		if (controllingDevice != null) {
//			int frameX = 1 + controllingDevice.x1;
//			int frameY = 1 + controllingDevice.y1;
//			match.renderer.glGraphics.drawSubTextureRect(Assets.speed, Settings.screenWidth -50, Settings.screenHeight -50, 29, 29, 29 * frameX, 29 * frameY, 29, 29);
//		}

    }

    private void quit() {
        //if final frame is different from starting frame then fade out
//		if (position != Const.REPLAY_SUBFRAMES) {
//			game_action_queue.push(AT_FADE_OUT);
//		}

//		game_action_queue.push(AT_RESTORE_FOREGROUND);

        //if final frame is different from starting frame then fade in
//		if (position != Const.REPLAY_SUBFRAMES) {
//			game_action_queue.push(AT_FADE_IN);
//		}
    }

}
