package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuMusic {

    private enum State {STOPPED, PLAYING, PAUSED}

    private State state;

    public static final int ALL = -2;
    public static final int SHUFFLE = -1;

    private Music music;
    private int volume;
    private int mode;
    private List<FileHandle> fileList;
    public List<FileHandle> playList;
    private int currentTrack;

    MenuMusic(String folder) {
        fileList = new ArrayList<FileHandle>();
        playList = new ArrayList<FileHandle>();
        currentTrack = -1;
        state = State.STOPPED;

        FileHandle fileHandle = Gdx.files.internal(folder);
        for (FileHandle file : fileHandle.list()) {
            if (file.extension().toLowerCase().equals("ogg")
                    || file.extension().toLowerCase().equals("wav")
                    || file.extension().toLowerCase().equals("mp3")) {
                fileList.add(file);
            }
        }
        Collections.sort(fileList, Assets.fileComparatorByName);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        currentTrack = -1;

        if (state == State.PLAYING) {
            music.stop();
        }

        playList.clear();

        switch (mode) {
            case ALL:
                playList.addAll(fileList);
                break;

            case SHUFFLE:
                playList.addAll(fileList);
                Collections.shuffle(playList);
                break;

            // SINGLE TRACK
            default:
                int track = Math.min(mode, fileList.size() - 1);
                playList.add(fileList.get(track));
                break;
        }

        if (playList.size() > 0) {
            state = State.PLAYING;
        }
    }

    public void update(int targetVolume) {
        switch (state) {
            case STOPPED:
                break;

            case PLAYING:
                // track not loaded or finished
                if (music == null || !music.isPlaying()) {

                    currentTrack = EMath.rotate(currentTrack, 0, playList.size() - 1, +1);
                    music = Gdx.audio.newMusic(playList.get(currentTrack));

                    if (music == null) {
                        Gdx.app.log("Cannot load track", playList.get(currentTrack).name());
                        state = State.STOPPED;
                        return;
                    }

                    music.setLooping(playList.size() == 1);
                    music.play();

                    if (!music.isPlaying()) {
                        Gdx.app.log("Cannot play track", playList.get(currentTrack).name());
                        state = State.STOPPED;
                    }
                }

                // move volume toward target
                if (volume > targetVolume) {
                    volume = Math.max(volume - 5, 0);
                } else if (volume < targetVolume) {
                    volume = Math.min(volume + 2, 100);
                }

                // set volume or pause
                if (volume == 0) {
                    music.stop();
                    state = State.PAUSED;
                } else {
                    if (!music.isPlaying()) {
                        music.play();
                    }
                    music.setVolume(volume / 100.0f);
                }
                break;

            case PAUSED:
                if (targetVolume > 0) {
                    state = State.PLAYING;
                }
                break;
        }
    }

    public String getCurrentTrackName() {
        if (state == State.STOPPED || currentTrack == -1) {
            return "";
        }
        return playList.get(currentTrack).nameWithoutExtension().toUpperCase();
    }

    public int getModeMin() {
        return ALL;
    }

    public int getModeMax() {
        return fileList.size() - 1;
    }

    boolean isPlaying() {
        return state == State.PLAYING && music.isPlaying();
    }
}
