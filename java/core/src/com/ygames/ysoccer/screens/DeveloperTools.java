package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.competitions.TestMatch;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.match.Team;

import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class DeveloperTools extends GLScreen {

    DeveloperTools(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_game_options.jpg");

        Widget w;

        w = new TitleBar("DEVELOPER TOOLS", 0x191FB0);
        widgets.add(w);

        w = new PlayerButton();
        widgets.add(w);

        setSelectedWidget(w);

        w = new KeeperButton();
        widgets.add(w);

        w = new MatchTestButton();
        widgets.add(w);

        w = new OptionsButton();
        widgets.add(w);

        w = new InfoButton();
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);

        //convert("arrow.png", "arrow2.png");
    }

    private class PlayerButton extends Button {

        PlayerButton() {
            setColor(0x7D42A1);
            setGeometry((game.gui.WIDTH - 260) / 2, 250, 260, 36);
            setText("PLAYER", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new DeveloperPlayer(game));
        }
    }

    private class KeeperButton extends Button {

        KeeperButton() {
            setColor(0x7D42A1);
            setGeometry((game.gui.WIDTH - 260) / 2, 300, 260, 36);
            setText("KEEPER", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new DeveloperKeeper(game));
        }
    }

    private class MatchTestButton extends Button {

        MatchTestButton() {
            setColor(0x427AA1);
            setGeometry((game.gui.WIDTH - 260) / 2, 350, 260, 36);
            setText("QUICK MATCH", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setState(GLGame.State.FRIENDLY, null);

            FileHandle homeTeamFile = Gdx.files.local("/data/teams/1964-65/CLUB_TEAMS/EUROPE/ITALY/team.inter_milan.json");
            Team homeTeam = Assets.json.fromJson(Team.class, homeTeamFile.readString("UTF-8"));
            homeTeam.path = Assets.getRelativeTeamPath(homeTeamFile);
            homeTeam.controlMode = Team.ControlMode.PLAYER;

            FileHandle awayTeamFile = Gdx.files.local("/data/teams/1964-65/CLUB_TEAMS/EUROPE/ITALY/team.ac_milan.json");
            Team awayTeam = Assets.json.fromJson(Team.class, awayTeamFile.readString("UTF-8"));
            awayTeam.path = Assets.getRelativeTeamPath(awayTeamFile);
            awayTeam.controlMode = Team.ControlMode.COMPUTER;

            // reset input devices
            game.inputDevices.setAvailability(true);
            homeTeam.setInputDevice(null);
            homeTeam.releaseNonAiInputDevices();
            awayTeam.setInputDevice(null);
            awayTeam.releaseNonAiInputDevices();
            for (InputDevice id : game.inputDevices) {
                if (id.type == InputDevice.Type.JOYSTICK) {
                    homeTeam.inputDevice = id;
                    break;
                }
            }
            if (homeTeam.inputDevice == null) {
                homeTeam.inputDevice = game.inputDevices.assignFirstAvailable();
            }

            Competition testMatch = new TestMatch();
            testMatch.getMatch().setTeam(HOME, homeTeam);
            testMatch.getMatch().setTeam(AWAY, awayTeam);

            navigation.competition = testMatch;
            game.setScreen(new MatchSetup(game));
        }
    }

    private class OptionsButton extends Button {

        OptionsButton() {
            setColor(0x8AA142);
            setGeometry((game.gui.WIDTH - 260) / 2, 400, 260, 36);
            setText("OPTIONS", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new DeveloperOptions(game));
        }
    }

    private class InfoButton extends Button {

        InfoButton() {
            setColor(0x8AA142);
            setGeometry((game.gui.WIDTH - 260) / 2, 450, 260, 36);
            setText("INFO", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new DeveloperInfo(game));
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColor(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new Main(game));
        }
    }

    static void convert(String origFilename, String destFilename) {
        FileHandle inputFileHandle = Gdx.files.internal("images/" + origFilename);
        PngReader pngr = new PngReader(inputFileHandle.read());
        System.out.println(pngr.toString());
        int channels = pngr.imgInfo.channels;
//        if (channels < 3 || pngr.imgInfo.bitDepth != 8)
//            throw new RuntimeException("This method is for RGB8/RGBA8 images");

        FileHandle outputFileHandle = Gdx.files.local("images/" + destFilename);
        PngWriter pngw = new PngWriter(outputFileHandle.write(false), pngr.imgInfo);
        pngw.copyChunksFrom(pngr.getChunksList(), ChunkCopyBehaviour.COPY_ALL);
//        pngw.getMetadata().setText(PngChunkTextVar.KEY_Description, "Decreased red and increased green");
        for (int row = 0; row < pngr.imgInfo.rows; row++) { // also: while(pngr.hasMoreRows())
            IImageLine l1 = pngr.readRow();
            int[] scanLine = ((ImageLineInt) l1).getScanline(); // to save typing
            for (int j = 0; j < scanLine.length; j++) {
                scanLine[j] = Assets.random.nextInt(255);
            }
//            for (int j = 0; j < pngr.imgInfo.cols; j++) {
//                scanLine[j * channels] /= 2;
//                scanLine[j * channels + 1] = ImageLineHelper.clampTo_0_255(scanLine[j * channels + 1] + 20);
//            }
            pngw.writeRow(l1);
        }
        pngr.end(); // it's recommended to end the reader first, in case there are trailing chunks to read
        pngw.end();
    }
}
