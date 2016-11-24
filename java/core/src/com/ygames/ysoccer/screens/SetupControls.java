package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.InputDeviceConfig;
import com.ygames.ysoccer.framework.JoystickConfig;
import com.ygames.ysoccer.framework.KeyboardConfig;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;

import java.util.ArrayList;
import java.util.Arrays;

class SetupControls extends GLScreen {

    private enum ConfigParam {KEY_LEFT, KEY_RIGHT, KEY_UP, KEY_DOWN, BUTTON_1, BUTTON_2}

    private InputDeviceButton selectedInputDeviceButton;
    private ArrayList<JoystickConfig> joystickConfigs;
    private ArrayList<KeyboardConfig> keyboardConfigs;
    private InputProcessor inputProcessor;
    private JoystickListener joystickListener;

    SetupControls(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_controls.jpg");

        inputProcessor = new SetupInputProcessor();
        joystickConfigs = new ArrayList<JoystickConfig>();
        joystickListener = new JoystickListener();

        Widget w;
        w = new TitleButton();
        widgets.add(w);

        int pos = 0;
        keyboardConfigs = game.settings.getKeyboardConfigs();
        for (int port = 0; port < 2; port++) {
            InputDeviceButton inputDeviceButton = new InputDeviceButton(keyboardConfigs.get(port), port, pos);
            widgets.add(inputDeviceButton);

            if (port == 0) {
                selectedInputDeviceButton = inputDeviceButton;
                setSelectedWidget(inputDeviceButton);
            }
            pos++;
        }

        int port = 0;
        for (Controller controller : Controllers.getControllers()) {

            // search previously saved configuration
            JoystickConfig joystickConfig = game.settings.getJoystickConfigByName(controller.getName());
            if (joystickConfig == null) {
                joystickConfig = new JoystickConfig(controller.getName());
            }

            joystickConfigs.add(joystickConfig);

            w = new InputDeviceButton(joystickConfig, port, pos);
            widgets.add(w);

            port++;
            pos++;
        }

        w = new InputDeviceLabel();
        widgets.add(w);

        w = new LeftLabel();
        widgets.add(w);

        w = new LeftButton();
        widgets.add(w);

        w = new RightLabel();
        widgets.add(w);

        w = new RightButton();
        widgets.add(w);

        w = new UpLabel();
        widgets.add(w);

        w = new UpButton();
        widgets.add(w);

        w = new DownLabel();
        widgets.add(w);

        w = new DownButton();
        widgets.add(w);

        w = new FireLabel(1);
        widgets.add(w);

        w = new FireButton(1);
        widgets.add(w);

        w = new FireLabel(2);
        widgets.add(w);

        w = new FireButton(2);
        widgets.add(w);

        w = new ExitButton();
        widgets.add(w);
    }

    private class TitleButton extends Button {

        TitleButton() {
            setColors(0xA905A3);
            setGeometry((game.gui.WIDTH - 400) / 2, 20, 400, 40);
            setText(Assets.strings.get("CONTROLS"), Font.Align.CENTER, Assets.font14);
            setActive(false);
        }
    }

    private class InputDeviceButton extends Button {

        private InputDeviceConfig config;
        private int port;

        InputDeviceButton(InputDeviceConfig config, int port, int pos) {
            this.config = config;
            this.port = port;
            setGeometry(game.gui.WIDTH / 2 - 560, 180 + 45 * pos, 220, 40);
            switch (config.type) {
                case KEYBOARD:
                    setText(Assets.strings.get("KEYBOARD") + " " + (port + 1), Font.Align.CENTER, Assets.font14);
                    break;

                case JOYSTICK:
                    setText(Assets.strings.get("JOYSTICK") + " " + (port + 1), Font.Align.CENTER, Assets.font14);
                    break;
            }
        }

        @Override
        public void refresh() {
            setColors(this == selectedInputDeviceButton ? 0x2F2F5E : 0x484891);
            switch (config.type) {
                case KEYBOARD:
                    break;

                case JOYSTICK:
                    if (!((JoystickConfig) config).isConfigured()) {
                        setColors(this == selectedInputDeviceButton ? 0x800000 : 0xB40000);
                    }
                    break;
            }
        }

        @Override
        public void onFire1Down() {
            selectedInputDeviceButton = this;
            for (Widget widget : widgets) {
                widget.setDirty(true);
            }
        }
    }

    private class InputDeviceLabel extends Button {

        InputDeviceLabel() {
            setGeometry(game.gui.WIDTH / 2 - 250, 140, 760, 40);
            setColors(0x404040);
            setText("", Font.Align.CENTER, Assets.font14);
            setActive(false);
        }

        @Override
        public void refresh() {
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    setText("");
                    setVisible(false);
                    break;

                case JOYSTICK:
                    setText(((JoystickConfig) selectedInputDeviceButton.config).name.toUpperCase());
                    setVisible(true);
                    break;
            }
        }
    }

    private abstract class ConfigButton extends Button {

        ConfigParam configParam;

        void setKeyCode(int keyCode) {
            if (isKeyCodeReserved(keyCode)) return;
            KeyboardConfig keyboardConfig = (KeyboardConfig) selectedInputDeviceButton.config;
            switch (configParam) {
                case KEY_LEFT:
                    if (isKeyCodeAssigned(keyCode, configParam, selectedInputDeviceButton.port)) {
                        return;
                    }
                    keyboardConfig.keyLeft = keyCode;
                    break;

                case KEY_RIGHT:
                    if (isKeyCodeAssigned(keyCode, configParam, selectedInputDeviceButton.port)) {
                        return;
                    }
                    keyboardConfig.keyRight = keyCode;
                    break;

                case KEY_UP:
                    if (isKeyCodeAssigned(keyCode, configParam, selectedInputDeviceButton.port)) {
                        return;
                    }
                    keyboardConfig.keyUp = keyCode;
                    break;

                case KEY_DOWN:
                    if (isKeyCodeAssigned(keyCode, configParam, selectedInputDeviceButton.port)) {
                        return;
                    }
                    keyboardConfig.keyDown = keyCode;
                    break;

                case BUTTON_1:
                    if (isKeyCodeAssigned(keyCode, configParam, selectedInputDeviceButton.port)) {
                        return;
                    }
                    keyboardConfig.button1 = keyCode;
                    break;

                case BUTTON_2:
                    if (isKeyCodeAssigned(keyCode, configParam, selectedInputDeviceButton.port)) {
                        return;
                    }
                    keyboardConfig.button2 = keyCode;
                    break;
            }
            entryMode = false;
            Gdx.input.setInputProcessor(null);
            setKeyboardConfigs();
            game.reloadInputDevices();
            updateAllWidgets();
        }

        void setConfigParamIndex(int axisIndex, int buttonIndex) {
            JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
            switch (configParam) {
                case KEY_LEFT:
                case KEY_RIGHT:
                    if (axisIndex == -1) return;
                    if (axisIndex == joystickConfig.yAxis) return;
                    joystickConfig.xAxis = axisIndex;
                    break;

                case KEY_UP:
                case KEY_DOWN:
                    if (axisIndex == -1) return;
                    if (axisIndex == joystickConfig.xAxis) return;
                    joystickConfig.yAxis = axisIndex;
                    break;

                case BUTTON_1:
                    if (buttonIndex == -1) return;
                    if (buttonIndex == joystickConfig.button2) return;
                    joystickConfig.button1 = buttonIndex;
                    break;

                case BUTTON_2:
                    if (buttonIndex == -1) return;
                    if (buttonIndex == joystickConfig.button1) return;
                    joystickConfig.button2 = buttonIndex;
                    break;
            }
            entryMode = false;
            Controllers.removeListener(joystickListener);
            setJoystickConfigs();
            game.reloadInputDevices();
            updateAllWidgets();
        }

        @Override
        public void onFire1Up() {
            entryMode = true;
            game.inputDevices.clear();
            updateAllWidgets();
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    Gdx.input.setInputProcessor(inputProcessor);
                    break;

                case JOYSTICK:
                    Controllers.addListener(joystickListener);
                    break;
            }
        }
    }

    private void setKeyboardConfigs() {
        game.settings.setKeyboardConfigs(keyboardConfigs);
    }

    private void setJoystickConfigs() {
        ArrayList<JoystickConfig> configuredConfigs = new ArrayList<JoystickConfig>();
        for (JoystickConfig joystickConfig : joystickConfigs) {
            if (joystickConfig.isConfigured()) {
                configuredConfigs.add(joystickConfig);
            }
        }
        game.settings.setJoystickConfigs(configuredConfigs);
    }

    private boolean isKeyCodeAssigned(int keyCode, ConfigParam configParam, int port) {
        for (int i = 0; i < 2; i++) {
            KeyboardConfig config = keyboardConfigs.get(i);
            if (config.keyLeft == keyCode && (configParam != ConfigParam.KEY_LEFT || port != i))
                return true;
            if (config.keyRight == keyCode && (configParam != ConfigParam.KEY_RIGHT || port != i))
                return true;
            if (config.keyUp == keyCode && (configParam != ConfigParam.KEY_UP || port != i))
                return true;
            if (config.keyDown == keyCode && (configParam != ConfigParam.KEY_DOWN || port != i))
                return true;
            if (config.button1 == keyCode && (configParam != ConfigParam.BUTTON_1 || port != i))
                return true;
            if (config.button2 == keyCode && (configParam != ConfigParam.BUTTON_2 || port != i))
                return true;
        }
        return false;
    }


    private boolean isKeyCodeReserved(int keyCode) {
        Integer[] reservedKeyCodes = {
                Input.Keys.ESCAPE, Input.Keys.R, Input.Keys.P, Input.Keys.H,
                Input.Keys.F1, Input.Keys.F2, Input.Keys.F3, Input.Keys.F4,
                Input.Keys.F5, Input.Keys.F6, Input.Keys.F7, Input.Keys.F8,
                Input.Keys.F9, Input.Keys.F10, Input.Keys.F11, Input.Keys.F12
        };
        return Arrays.asList(reservedKeyCodes).contains(keyCode);
    }

    private class LeftLabel extends Button {

        LeftLabel() {
            setGeometry((game.gui.WIDTH - 220) / 2 - 150, 370, 220, 40);
            setText(Assets.strings.get("CONTROLS.LEFT"), Font.Align.CENTER, Assets.font14);
            setColors(0x404040);
            setActive(false);
        }
    }

    private class LeftButton extends ConfigButton {

        LeftButton() {
            configParam = ConfigParam.KEY_LEFT;
            setGeometry((game.gui.WIDTH - 220) / 2 - 150, 410, 220, 48);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    KeyboardConfig keyboardConfig = (KeyboardConfig) selectedInputDeviceButton.config;
                    if (entryMode) {
                        setText("?");
                        setColors(0xEB9532);
                    } else {
                        setText(Input.Keys.toString(keyboardConfig.keyLeft).toUpperCase());
                        setColors(0x548854);
                    }
                    break;

                case JOYSTICK:
                    JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
                    int xAxisIndex = joystickConfig.xAxis;
                    if (entryMode) {
                        setText("?");
                        setColors(0xEB9532);
                    } else if (xAxisIndex == -1) {
                        setText(Assets.strings.get("CONTROLS.UNKNOWN"));
                        setColors(0xB40000);
                    } else {
                        setText(Assets.strings.get("CONTROLS.AXIS") + " " + xAxisIndex);
                        setColors(0x548854);
                    }
                    break;
            }
        }
    }

    private class RightLabel extends Button {

        RightLabel() {
            setGeometry((game.gui.WIDTH - 220) / 2 + 150, 370, 220, 40);
            setText(Assets.strings.get("CONTROLS.RIGHT"), Font.Align.CENTER, Assets.font14);
            setColors(0x404040);
            setActive(false);
        }
    }

    private class RightButton extends ConfigButton {

        RightButton() {
            configParam = ConfigParam.KEY_RIGHT;
            setGeometry((game.gui.WIDTH - 220) / 2 + 150, 410, 220, 48);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    KeyboardConfig keyboardConfig = (KeyboardConfig) selectedInputDeviceButton.config;
                    if (entryMode) {
                        setText("?");
                        setColors(0xEB9532);
                    } else {
                        setText(Input.Keys.toString(keyboardConfig.keyRight).toUpperCase());
                        setColors(0x548854);
                    }
                    break;

                case JOYSTICK:
                    JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
                    int xAxisIndex = joystickConfig.xAxis;
                    if (entryMode) {
                        setText("?");
                        setColors(0xEB9532);
                    } else if (xAxisIndex == -1) {
                        setText(Assets.strings.get("CONTROLS.UNKNOWN"));
                        setColors(0xB40000);
                    } else {
                        setText(Assets.strings.get("CONTROLS.AXIS") + " " + xAxisIndex);
                        setColors(0x548854);
                    }
                    break;
            }
        }
    }

    private class UpLabel extends Button {

        UpLabel() {
            setGeometry((game.gui.WIDTH - 220) / 2, 240, 220, 40);
            setText(Assets.strings.get("CONTROLS.UP"), Font.Align.CENTER, Assets.font14);
            setColors(0x404040);
            setActive(false);
        }
    }

    private class UpButton extends ConfigButton {

        UpButton() {
            configParam = ConfigParam.KEY_UP;
            setGeometry((game.gui.WIDTH - 220) / 2, 280, 220, 48);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    KeyboardConfig keyboardConfig = (KeyboardConfig) selectedInputDeviceButton.config;
                    if (entryMode) {
                        setText("?");
                        setColors(0xEB9532);
                    } else {
                        setText(Input.Keys.toString(keyboardConfig.keyUp).toUpperCase());
                        setColors(0x548854);
                    }
                    break;

                case JOYSTICK:
                    JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
                    int yAxisIndex = joystickConfig.yAxis;
                    if (entryMode) {
                        setText("?");
                        setColors(0xEB9532);
                    } else if (yAxisIndex == -1) {
                        setText(Assets.strings.get("CONTROLS.UNKNOWN"));
                        setColors(0xB40000);
                    } else {
                        setText(Assets.strings.get("CONTROLS.AXIS") + " " + yAxisIndex);
                        setColors(0x548854);
                    }
                    break;
            }
        }
    }

    private class DownLabel extends Button {

        DownLabel() {
            setGeometry((game.gui.WIDTH - 220) / 2, 500, 220, 40);
            setText(Assets.strings.get("CONTROLS.DOWN"), Font.Align.CENTER, Assets.font14);
            setColors(0x404040);
            setActive(false);
        }
    }

    private class DownButton extends ConfigButton {

        DownButton() {
            configParam = ConfigParam.KEY_DOWN;
            setGeometry((game.gui.WIDTH - 220) / 2, 540, 220, 48);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    KeyboardConfig keyboardConfig = (KeyboardConfig) selectedInputDeviceButton.config;
                    if (entryMode) {
                        setText("?");
                        setColors(0xEB9532);
                    } else {
                        setText(Input.Keys.toString(keyboardConfig.keyDown).toUpperCase());
                        setColors(0x548854);
                    }
                    break;

                case JOYSTICK:
                    JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
                    int yAxisIndex = joystickConfig.yAxis;
                    if (entryMode) {
                        setText("?");
                        setColors(0xEB9532);
                    } else if (yAxisIndex == -1) {
                        setText(Assets.strings.get("CONTROLS.UNKNOWN"));
                        setColors(0xB40000);
                    } else {
                        setText(Assets.strings.get("CONTROLS.AXIS") + " " + yAxisIndex);
                        setColors(0x548854);
                    }
                    break;
            }
        }
    }

    private class FireLabel extends Button {

        FireLabel(int buttonNumber) {
            setGeometry((game.gui.WIDTH - 240) / 2 + 420, 280 + (180 * (buttonNumber - 1)), 220, 40);
            setText(Assets.strings.get("CONTROLS.BUTTON") + " " + ((buttonNumber == 1) ? "A" : "B"), Font.Align.CENTER, Assets.font14);
            setColors(0x404040);
            setActive(false);
        }
    }

    private class FireButton extends ConfigButton {

        int buttonNumber;

        FireButton(int buttonNumber) {
            this.buttonNumber = buttonNumber;
            configParam = (buttonNumber == 1) ? ConfigParam.BUTTON_1 : ConfigParam.BUTTON_2;
            setGeometry((game.gui.WIDTH - 240) / 2 + 420, 320 + (180 * (buttonNumber - 1)), 220, 48);
            setText("", Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void refresh() {
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    KeyboardConfig keyboardConfig = (KeyboardConfig) selectedInputDeviceButton.config;
                    int value = (buttonNumber == 1) ? keyboardConfig.button1 : keyboardConfig.button2;
                    if (entryMode) {
                        setText("?");
                        setColors(0xEB9532);
                    } else {
                        setText(Input.Keys.toString(value).toUpperCase());
                        setColors(0x548854);
                    }
                    break;

                case JOYSTICK:
                    JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
                    int index = (buttonNumber == 1) ? joystickConfig.button1 : joystickConfig.button2;
                    if (entryMode) {
                        setText("?");
                        setColors(0xEB9532);
                    } else if (index == -1) {
                        setText(Assets.strings.get("CONTROLS.UNKNOWN"));
                        setColors(0xB40000);
                    } else {
                        setText(index);
                        setColors(0x548854);
                    }
                    break;
            }
        }

        @Override
        public void onFire1Up() {
            entryMode = true;
            game.inputDevices.clear();
            updateAllWidgets();
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    Gdx.input.setInputProcessor(inputProcessor);
                    break;

                case JOYSTICK:
                    Controllers.addListener(joystickListener);
                    break;
            }
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColors(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(Assets.strings.get("EXIT"), Font.Align.CENTER, Assets.font14);
        }

        @Override
        public void onFire1Up() {
            game.settings.save();
            game.setScreen(new Main(game));
        }
    }

    private class JoystickListener extends ControllerAdapter {

        @Override
        public boolean buttonUp(Controller controller, int buttonIndex) {
            JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
            if (controller.getName().equals(joystickConfig.name)) {
                ((ConfigButton) selectedWidget).setConfigParamIndex(-1, buttonIndex);
            }
            return false;
        }

        @Override
        public boolean axisMoved(Controller controller, int axisIndex, float value) {
            JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
            if (controller.getName().equals(joystickConfig.name)) {
                ((ConfigButton) selectedWidget).setConfigParamIndex(axisIndex, -1);
            }
            return false;
        }
    }

    private class SetupInputProcessor extends InputAdapter {

        public boolean keyUp(int keycode) {
            ((ConfigButton) selectedWidget).setKeyCode(keycode);
            return false;
        }
    }
}

