package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.match.MatchSettings;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

class TrainingLoading extends GLScreen {

    TrainingLoading(GLGame game) {
        super(game);
        playMenuMusic = false;

        Team team = navigation.team;
        MatchSettings matchSettings = navigation.matchSettings;

        matchSettings.setup();

        game.unsetMouse();

        Assets.loadStadium(matchSettings);
        Assets.loadCrowd(team);
        Assets.loadBall(matchSettings);
        Assets.loadCornerFlags(matchSettings);
        team.loadImage();
        Assets.loadCoach(team);
        for (Player player : team.players) {
            if (player.role == Player.Role.GOALKEEPER) {
                Assets.loadKeeper(player);
            } else {
                Assets.loadPlayer(player);
            }
            Assets.loadHair(player);
        }
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // TODO
//        game.setScreen(new TrainingScreen(game));
    }
}
