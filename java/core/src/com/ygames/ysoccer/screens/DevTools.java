package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;

class DevTools extends GLScreen {

    DevTools(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_game_options.jpg");

        Widget w;

        w = new TitleBar("DEVELOPMENT TOOLS", 0x191FB0);
        widgets.add(w);

        w = new PlayerTestButton();
        widgets.add(w);

        setSelectedWidget(w);

        w = new ExitButton();
        widgets.add(w);

        //convert("arrow.png", "arrow2.png");
    }

    private class PlayerTestButton extends Button {

        PlayerTestButton() {
            setColors(0x427AA1);
            setGeometry((game.gui.WIDTH - 260) / 2, 360, 260, 36);
            setText("PLAYER TEST", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Down() {
            game.setScreen(new TestPlayer(game));
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200);
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
