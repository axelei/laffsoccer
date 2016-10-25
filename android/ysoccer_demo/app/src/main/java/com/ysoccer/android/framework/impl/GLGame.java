package com.ysoccer.android.framework.impl;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.ysoccer.android.framework.Audio;
import com.ysoccer.android.framework.FileIO;
import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.Graphics;
import com.ysoccer.android.framework.Input;
import com.ysoccer.android.framework.Screen;
import com.ysoccer.android.ysdemo.Settings;
import com.ysoccer.android.ysdemo.match.GamepadInput;
import com.ysoccer.android.ysdemo.match.TouchInput;

import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public abstract class GLGame extends Activity implements Game, Renderer {
    enum GLGameState {
        Initialized, Running, Paused, Finished, Idle
    }

    public static final int SUBFRAMES = 8;
    public static final int VIRTUAL_REFRESH_RATE = 64;
    public static final int SUBFRAMES_PER_SECOND = VIRTUAL_REFRESH_RATE * SUBFRAMES;
    public static final float SUBFRAME_DURATION = 1.0f / SUBFRAMES_PER_SECOND;

    GLSurfaceView glView;
    protected GLGraphics glGraphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;
    public Settings settings;
    GLGameState state = GLGameState.Initialized;
    Object stateChanged = new Object();
    long startTime = System.nanoTime();
    WakeLock wakeLock;
    FPSCounter fpsCounter;
    public Random random;
    private float deltaTime;
    public TouchInput touchInput;
    public GamepadInput gamepadInput;
    ArrayList gameControllerDeviceIds;

    public GLGame() {
        random = new Random(System.currentTimeMillis());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        glView = new GLSurfaceView(this);
        glView.setRenderer(this);
        setContentView(glView);

        glGraphics = new GLGraphics(glView);
        glGraphics.light = 0;
        fileIO = new AndroidFileIO(this);
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, glView, 1, 1);
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "GLGame");
        fpsCounter = new FPSCounter();
        touchInput = new TouchInput(glGraphics);

        if (getInput().hasGamepadHandler()) {
            gameControllerDeviceIds = getGameControllerIds();
            if (gameControllerDeviceIds.size() > 0) {
                gamepadInput = new GamepadInput(getInput());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        glView.onResume();
        wakeLock.acquire();
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glGraphics.setGL((GL11) gl);

        synchronized (stateChanged) {
            if (state == GLGameState.Initialized) {
                screen = getStartScreen();
            }
            state = GLGameState.Running;
            screen.resume();
            startTime = System.nanoTime();
        }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    public void onDrawFrame(GL10 gl) {
        GLGameState state = null;

        synchronized (stateChanged) {
            state = this.state;
        }

        if (state == GLGameState.Running) {
            deltaTime += (System.nanoTime() - startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            int subframes = (int) (deltaTime / SUBFRAME_DURATION);

            screen.update(subframes * SUBFRAME_DURATION);

            screen.present(subframes * SUBFRAME_DURATION);

            deltaTime -= subframes * SUBFRAME_DURATION;
            //fpsCounter.logFrame();
        }

        if (state == GLGameState.Paused) {
            screen.pause();
            synchronized (stateChanged) {
                this.state = GLGameState.Idle;
                stateChanged.notifyAll();
            }
        }

        if (state == GLGameState.Finished) {
            screen.pause();
            screen.dispose();
            synchronized (stateChanged) {
                this.state = GLGameState.Idle;
                stateChanged.notifyAll();
            }
        }
    }

    @Override
    public void onPause() {
        synchronized (stateChanged) {
            if (isFinishing())
                state = GLGameState.Finished;
            else
                state = GLGameState.Paused;
            while (true) {
                try {
                    stateChanged.wait();
                    break;
                } catch (InterruptedException e) {
                }
            }
        }
        settings.save();
        wakeLock.release();
        glView.onPause();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (screen.keyBack() == true) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public GLGraphics getGLGraphics() {
        return glGraphics;
    }

    public Input getInput() {
        return input;
    }

    public FileIO getFileIO() {
        return fileIO;
    }

    public Graphics getGraphics() {
        throw new IllegalStateException("We are using OpenGL!");
    }

    public Audio getAudio() {
        return audio;
    }

    public void setScreen(Screen newScreen) {
        if (newScreen == null)
            throw new IllegalArgumentException("Screen must not be null");
        this.screen.pause();
        this.screen.dispose();
        newScreen.resume();
        // newScreen.update(0);
        this.screen = newScreen;
    }

    public Screen getCurrentScreen() {
        return screen;
    }

    public String translate(String name) {
        name = name.replaceAll(" ", "_");
        name = name.replaceAll("'", "");
        int resId = getResources().getIdentifier(name, "string",
                getPackageName());
        if (resId != 0) {
            name = getResources().getString(resId);
        }
        return name;
    }

    private ArrayList getGameControllerIds() {
        ArrayList gameControllerDeviceIds = new ArrayList();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();

            boolean isGamepad = ((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD);
            boolean isJoystick = ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK);
            if (isGamepad || isJoystick) {
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                }
            }
        }
        return gameControllerDeviceIds;
    }

}
