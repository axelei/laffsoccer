package com.ysoccer.android.ysdemo;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.gl.Frame;
import com.ysoccer.android.framework.gl.SpriteBatcher;
import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.impl.GLScreen;
import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.ysdemo.match.Match;
import com.ysoccer.android.ysdemo.match.Match.MatchListener;
import com.ysoccer.android.ysdemo.match.MatchRenderer;
import com.ysoccer.android.ysdemo.match.MatchSettings;
import com.ysoccer.android.ysdemo.match.Team;
import com.ysoccer.android.ysdemo.match.Weather;

public class MatchLoading extends GLScreen {

    private final Team[] teams;
    private final MatchSettings matchSettings;
    private final Match match;

    MatchLoading(Game game, Team[] teams, MatchSettings matchSettings) {
        super(game);
        this.teams = teams;
        this.matchSettings = matchSettings;

        SpriteBatcher batcher = new SpriteBatcher(glGraphics, 5000);

        MatchListener matchListener = new MatchListener() {
            public void bounceSound(float volume) {
                Assets.playSound(glGame, Assets.bounceSound, volume);
            }

            public void chantSound(float volume) {
                Assets.playSound(glGame, Assets.chantSound, volume);
            }

            public void deflectSound(float volume) {
                Assets.playSound(glGame, Assets.deflectSound, volume);
            }

            public void endGameSound(float volume) {
                Assets.playSound(glGame, Assets.endGameSound, volume);
            }

            public void holdSound(float volume) {
                Assets.playSound(glGame, Assets.holdSound, volume);
            }

            public void homeGoalSound(float volume) {
                Assets.playSound(glGame, Assets.homeGoalSound, volume);
            }

            public void introSound(float volume) {
                Assets.playSound(glGame, Assets.introSound, volume);
            }

            public void postSound(float volume) {
                Assets.playSound(glGame, Assets.postSound, volume);
            }

            public void kickSound(float volume) {
                Assets.playSound(glGame, Assets.kickSound, volume);
            }

            public void netSound(float volume) {
                Assets.playSound(glGame, Assets.netSound, volume);
            }

            public void whistleSound(float volume) {
                Assets.playSound(glGame, Assets.whistleSound, volume);
            }

            public void crowdSound(float volume) {
                if (volume > 0) {
                    Assets.crowdSound.play();
                }
            }

            public void quitMatch() {
                glGame.setScreen(new MenuReplayMatch(glGame, match.team, match.settings));
            }

        };

        matchSettings.setup();
        if (matchSettings.weatherStrength != Weather.Strength.NONE) {
            switch (matchSettings.weatherEffect) {
                case Weather.WIND:
                    Assets.wind = new Texture(glGame, "images/wind.png");
                    break;

                case Weather.RAIN:
                    Assets.rain = new Texture(glGame, "images/rain.png");
                    for (int i = 0; i < 4; i++) {
                        Assets.rainRegions[i] = new Frame(Assets.rain, i * 30, 0,
                                30, 30);
                    }
                    break;

                case Weather.SNOW:
                    Assets.snow = new Texture(glGame, "images/snow.png");
                    for (int i = 0; i < 3; i++) {
                        Assets.snowRegions[i] = new Frame(Assets.snow, i * 3, 0, 3,
                                3);
                    }
                    break;

                case Weather.FOG:
                    Assets.fog = new Texture(glGame, "images/fog.png");
                    Assets.fogFrame = new Frame(Assets.fog, 0, 0, 256, 256);
                    break;
            }
        }

        if (glGame.settings.displayRadar) {
            Assets.playerTinyNumbers = new Texture(glGame,
                    "images/tiny_number.png");
        }
        Assets.goalTopA = new Texture(glGame, "images/stadium/goal_top_a.png");
        Assets.goalTopB = new Texture(glGame, "images/stadium/goal_top_b.png");
        Assets.goalBottom = new Texture(glGame, "images/stadium/goal_bottom.png");
        Assets.jumper = new Texture(glGame, "images/stadium/jumper.png");
        Assets.time = new Texture(glGame, "images/time.png");
        Assets.score = new Texture(glGame, "images/score.png");
        Assets.replay = new Texture(glGame, "images/replay.png");


        Assets.crowdSound = glGame.getAudio().newMusic("sfx/crowd.ogg");
        Assets.crowdSound.setLooping(true);
        Assets.crowdSound.setVolume(glGame.settings.sfxVolume);

        match = new Match(glGame, teams, matchListener, matchSettings);
        match.renderer = new MatchRenderer(glGraphics, batcher, match);
        match.renderer.loadGraphics(glGame);
        glGraphics.light = 0;

        if (glGame.gamepadInput == null) {
            Assets.joystick = new Texture(glGame, "images/joystick.png");
            for (int i = 0; i < 4; i++) {
                Assets.joystickRegions[i] = new Frame(
                        Assets.joystick,
                        128 * Emath.floor(i / 2f), 128 * (i % 2),
                        128, 128
                );
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        game.setScreen(new MatchScreen(game, match));
    }

}
