package com.ysoccer.android.framework;

public interface Audio {
    public Music newMusic(String filename);

    public Sound newSound(String filename);

    public void pauseSfx();

    public void resumeSfx();
}
