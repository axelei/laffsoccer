package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.match.Kit;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.match.Training;

import static com.ygames.ysoccer.match.Player.Role.GOALKEEPER;

class TrainingLoading extends GLScreen {

    Training training;

    TrainingLoading(GLGame game) {
        super(game);
        playMenuMusic = false;

        Team team = navigation.team;
        MatchSettings matchSettings = navigation.matchSettings;

        matchSettings.setup();

        training = new Training(team);
        training.init(game, matchSettings);
        setPlayersInputDevices();

        game.unsetMouse();

        Assets.loadStadium(matchSettings);
        Assets.loadCrowd(team);
        Assets.loadBall(matchSettings);
        Assets.loadCornerFlags(matchSettings);
        team.loadImage();
        Assets.loadCoach(team);
        Kit trainingKit = new Kit("PLAIN", 0xBFED6C, 0x264293, 0x264293, 0x264293, 0xBFED6C);
        for (Player player : team.players) {
            if (player.role == GOALKEEPER) {
                Assets.loadKeeper(player);
            } else {
                Assets.loadPlayer(player, trainingKit);
            }
            Assets.loadHair(player);
        }
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        game.setScreen(new TrainingScreen(game, training));
    }

    private void setPlayersInputDevices() {
        int assigned_devices = 0;
        int i = 10;
        while (i < training.team.lineup.size()) {
            Player ply = training.team.lineup.get(i);
            if (assigned_devices < game.inputDevices.size()) {
                if (ply.role != GOALKEEPER) {
                    ply.setInputDevice(game.inputDevices.get(assigned_devices));
                    assigned_devices = assigned_devices + 1;
                }
            } else {
                ply.setInputDevice(ply.ai);
            }
            if (i <= 10) {
                i = i - 1;
                if (i == 0) {
                    i = 11;
                }
            } else {
                i = i + 1;
            }
        }
    }
}
