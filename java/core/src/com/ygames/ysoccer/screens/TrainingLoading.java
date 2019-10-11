package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.match.Kit;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.SceneSettings;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.match.Training;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Player.Role.GOALKEEPER;

class TrainingLoading extends GLScreen {

    private final Training training;

    TrainingLoading(GLGame game, SceneSettings sceneSettings) {
        super(game);
        playMenuMusic = false;
        usesMouse = false;

        Team trainingTeam = navigation.team;

        sceneSettings.setup();

        training = new Training(trainingTeam);
        training.init(game, sceneSettings);
        assignInputDevices();

        game.disableMouse();

        Assets.loadStadium(sceneSettings);
        Assets.loadCrowd(trainingTeam);
        Assets.loadBall(sceneSettings);
        Assets.loadCornerFlags(sceneSettings);
        Assets.loadCoach(trainingTeam);
        Kit[] trainingKits = {
                new Kit("PLAIN", 0xBFED6C, 0x264293, 0x264293, 0x264293, 0xBFED6C),
                new Kit("PLAIN", 0xED7B5D, 0xEEEEEC, 0xEEEEEC, 0xEEEEEC, 0xED7B5D)
        };
        for (int t = HOME; t <= AWAY; t++) {
            Team team = training.team[t];
            for (Player player : team.lineup) {
                if (player.role == GOALKEEPER) {
                    Assets.loadKeeper(player);
                } else {
                    Assets.loadPlayer(player, trainingKits[t]);
                }
                Assets.loadHair(player);
            }
        }
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        game.setScreen(new TrainingScreen(game, training));
    }

    private void assignInputDevices() {
        int assigned_devices = 0;
        int[] i = {training.team[HOME].lineup.size() - 1, training.team[AWAY].lineup.size() - 1};
        int t = HOME;
        while (i[HOME] >= 0 || i[AWAY] >= 0) {
            if (i[t] >= 0) {
                Player ply = training.team[t].lineup.get(i[t]);
                if (assigned_devices < game.inputDevices.size()) {
                    if (ply.role != GOALKEEPER) {
                        ply.setInputDevice(game.inputDevices.get(assigned_devices));
                        assigned_devices = assigned_devices + 1;
                    }
                } else {
                    ply.setInputDevice(ply.ai);
                }
                i[t]--;
            }
            t = 1 - t;
        }
    }
}
