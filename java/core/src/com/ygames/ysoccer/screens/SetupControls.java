package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.framework.InputDeviceConfig;
import com.ygames.ysoccer.framework.JoystickConfig;
import com.ygames.ysoccer.framework.KeyboardConfig;
import com.ygames.ysoccer.gui.Button;
import com.ygames.ysoccer.gui.Widget;
import com.ygames.ysoccer.framework.EMath;

import java.util.ArrayList;
import java.util.Arrays;

import static com.ygames.ysoccer.framework.Assets.font14;
import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.framework.Font.Align.CENTER;

class SetupControls extends GLScreen {

    private enum ConfigParam {KEY_LEFT, KEY_RIGHT, KEY_UP, KEY_DOWN, BUTTON_1, BUTTON_2}

    private final ConfigParam[] buttonParams = {ConfigParam.BUTTON_1, ConfigParam.BUTTON_2};
    private final ConfigParam[] axisParams = {ConfigParam.KEY_LEFT, ConfigParam.KEY_RIGHT, ConfigParam.KEY_UP, ConfigParam.KEY_DOWN};

    private InputDeviceButton selectedInputDeviceButton;
    private final ArrayList<JoystickConfig> joystickConfigs;
    private final ArrayList<KeyboardConfig> keyboardConfigs;
    private final InputProcessor inputProcessor;
    private final JoystickListener joystickListener;
    private ConfigButton listeningConfigButton;

    SetupControls(GLGame game) {
        super(game);
        background = new Texture("images/backgrounds/menu_controls.jpg");

        inputProcessor = new SetupInputProcessor();
        joystickConfigs = new ArrayList<>();
        joystickListener = new JoystickListener();
        Controllers.addListener(joystickListener);
        listeningConfigButton = null;

        Widget w;

        w = new TitleBar(gettext("CONTROLS"), 0x83079C);
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

            InputDeviceButton inputDeviceButton = new InputDeviceButton(joystickConfig, port, pos);
            widgets.add(inputDeviceButton);

            w = new ResetJoystickButton(inputDeviceButton);
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

    private class InputDeviceButton extends Button {

        private final InputDeviceConfig config;
        private final int port;

        InputDeviceButton(InputDeviceConfig config, int port, int pos) {
            this.config = config;
            this.port = port;
            setGeometry(game.gui.WIDTH / 2 - 560, 180 + 46 * pos, 240, 42);
            switch (config.type) {
                case KEYBOARD:
                    setText(gettext("KEYBOARD") + " " + (port + 1), CENTER, font14);
                    break;

                case JOYSTICK:
                    setText(gettext("JOYSTICK") + " " + (port + 1), CENTER, font14);
                    break;
            }
        }

        @Override
        public void refresh() {
            setColor(this == selectedInputDeviceButton ? 0x2F2F5E : 0x484891);
            switch (config.type) {
                case KEYBOARD:
                    break;

                case JOYSTICK:
                    if (!((JoystickConfig) config).isConfigured()) {
                        setColor(this == selectedInputDeviceButton ? 0x800000 : 0xB40000);
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

    private class ResetJoystickButton extends Button {

        final InputDeviceButton inputDeviceButton;

        ResetJoystickButton(InputDeviceButton inputDeviceButton) {
            this.inputDeviceButton = inputDeviceButton;
            setGeometry(inputDeviceButton.x + inputDeviceButton.w + 2, inputDeviceButton.y, 38, 42);
            setColor(0xB40000);
            setText("" + (char) 19, CENTER, font14);
        }

        @Override
        public void refresh() {
            setVisible(inputDeviceButton == selectedInputDeviceButton && ((JoystickConfig) inputDeviceButton.config).isConfigured());
        }

        @Override
        public void onFire1Down() {
            ((JoystickConfig) inputDeviceButton.config).reset();
            refreshAllWidgets();
        }
    }

    private class InputDeviceLabel extends Button {

        InputDeviceLabel() {
            setGeometry((game.gui.WIDTH - 760) / 2, 100, 760, 40);
            setColor(0x404040);
            setText("", CENTER, font14);
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

            if (keyCode != Input.Keys.ESCAPE && selectedInputDeviceButton.config.type == InputDevice.Type.KEYBOARD) {
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
            }
            setKeyboardConfigs();
            quitEntryMode();
        }

        void setJoystickConfigParam(int axisIndex, int buttonIndex) {
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
            if (joystickConfig.isConfigured()) {
                setJoystickConfigs();
            }
            quitEntryMode();
        }

        private void quitEntryMode() {
            entryMode = false;
            listeningConfigButton = null;
            game.reloadInputDevices();
            Gdx.input.setInputProcessor(null);
            refreshAllWidgets();
        }

        @Override
        public void onFire1Down() {
            if (!entryMode) {
                entryMode = true;
                listeningConfigButton = this;
                game.inputDevices.clear();
                Gdx.input.setInputProcessor(inputProcessor);
                refreshAllWidgets();
            }
        }
    }

    private void setKeyboardConfigs() {
        game.settings.setKeyboardConfigs(keyboardConfigs);
    }

    private void setJoystickConfigs() {
        ArrayList<JoystickConfig> configuredConfigs = new ArrayList<>();
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
                Input.Keys.SPACE, Input.Keys.R, Input.Keys.P, Input.Keys.H,
                Input.Keys.F1, Input.Keys.F2, Input.Keys.F3, Input.Keys.F4,
                Input.Keys.F5, Input.Keys.F6, Input.Keys.F7, Input.Keys.F8,
                Input.Keys.F9, Input.Keys.F10, Input.Keys.F11, Input.Keys.F12
        };
        return Arrays.asList(reservedKeyCodes).contains(keyCode);
    }

    private class LeftLabel extends Button {

        LeftLabel() {
            setGeometry((game.gui.WIDTH - 200) / 2 - 150, 340, 200, 40);
            setText(gettext("CONTROLS.LEFT"), CENTER, font14);
            setColor(0x404040);
            setActive(false);
        }
    }

    private class LeftButton extends ConfigButton {

        LeftButton() {
            configParam = ConfigParam.KEY_LEFT;
            setGeometry((game.gui.WIDTH - 200) / 2 - 150, 380, 200, 46);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    KeyboardConfig keyboardConfig = (KeyboardConfig) selectedInputDeviceButton.config;
                    if (entryMode) {
                        setText("?");
                        setColor(0xEB9532);
                    } else {
                        setText(Input.Keys.toString(keyboardConfig.keyLeft).toUpperCase());
                        setColor(0x548854);
                    }
                    break;

                case JOYSTICK:
                    JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
                    int xAxisIndex = joystickConfig.xAxis;
                    if (entryMode) {
                        setText("?");
                        setColor(0xEB9532);
                    } else if (xAxisIndex == -1) {
                        setText(gettext("CONTROLS.UNKNOWN"));
                        setColor(0xB40000);
                    } else {
                        setText(gettext("CONTROLS.AXIS") + " " + xAxisIndex);
                        setColor(0x548854);
                    }
                    break;
            }
        }
    }

    private class RightLabel extends Button {

        RightLabel() {
            setGeometry((game.gui.WIDTH - 200) / 2 + 150, 340, 200, 40);
            setText(gettext("CONTROLS.RIGHT"), CENTER, font14);
            setColor(0x404040);
            setActive(false);
        }
    }

    private class RightButton extends ConfigButton {

        RightButton() {
            configParam = ConfigParam.KEY_RIGHT;
            setGeometry((game.gui.WIDTH - 200) / 2 + 150, 380, 200, 46);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    KeyboardConfig keyboardConfig = (KeyboardConfig) selectedInputDeviceButton.config;
                    if (entryMode) {
                        setText("?");
                        setColor(0xEB9532);
                    } else {
                        setText(Input.Keys.toString(keyboardConfig.keyRight).toUpperCase());
                        setColor(0x548854);
                    }
                    break;

                case JOYSTICK:
                    JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
                    int xAxisIndex = joystickConfig.xAxis;
                    if (entryMode) {
                        setText("?");
                        setColor(0xEB9532);
                    } else if (xAxisIndex == -1) {
                        setText(gettext("CONTROLS.UNKNOWN"));
                        setColor(0xB40000);
                    } else {
                        setText(gettext("CONTROLS.AXIS") + " " + xAxisIndex);
                        setColor(0x548854);
                    }
                    break;
            }
        }
    }

    private class UpLabel extends Button {

        UpLabel() {
            setGeometry((game.gui.WIDTH - 200) / 2, 180, 200, 40);
            setText(gettext("CONTROLS.UP"), CENTER, font14);
            setColor(0x404040);
            setActive(false);
        }
    }

    private class UpButton extends ConfigButton {

        UpButton() {
            configParam = ConfigParam.KEY_UP;
            setGeometry((game.gui.WIDTH - 200) / 2, 220, 200, 46);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    KeyboardConfig keyboardConfig = (KeyboardConfig) selectedInputDeviceButton.config;
                    if (entryMode) {
                        setText("?");
                        setColor(0xEB9532);
                    } else {
                        setText(Input.Keys.toString(keyboardConfig.keyUp).toUpperCase());
                        setColor(0x548854);
                    }
                    break;

                case JOYSTICK:
                    JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
                    int yAxisIndex = joystickConfig.yAxis;
                    if (entryMode) {
                        setText("?");
                        setColor(0xEB9532);
                    } else if (yAxisIndex == -1) {
                        setText(gettext("CONTROLS.UNKNOWN"));
                        setColor(0xB40000);
                    } else {
                        setText(gettext("CONTROLS.AXIS") + " " + yAxisIndex);
                        setColor(0x548854);
                    }
                    break;
            }
        }
    }

    private class DownLabel extends Button {

        DownLabel() {
            setGeometry((game.gui.WIDTH - 200) / 2, 500, 200, 40);
            setText(gettext("CONTROLS.DOWN"), CENTER, font14);
            setColor(0x404040);
            setActive(false);
        }
    }

    private class DownButton extends ConfigButton {

        DownButton() {
            configParam = ConfigParam.KEY_DOWN;
            setGeometry((game.gui.WIDTH - 200) / 2, 540, 200, 46);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    KeyboardConfig keyboardConfig = (KeyboardConfig) selectedInputDeviceButton.config;
                    if (entryMode) {
                        setText("?");
                        setColor(0xEB9532);
                    } else {
                        setText(Input.Keys.toString(keyboardConfig.keyDown).toUpperCase());
                        setColor(0x548854);
                    }
                    break;

                case JOYSTICK:
                    JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
                    int yAxisIndex = joystickConfig.yAxis;
                    if (entryMode) {
                        setText("?");
                        setColor(0xEB9532);
                    } else if (yAxisIndex == -1) {
                        setText(gettext("CONTROLS.UNKNOWN"));
                        setColor(0xB40000);
                    } else {
                        setText(gettext("CONTROLS.AXIS") + " " + yAxisIndex);
                        setColor(0x548854);
                    }
                    break;
            }
        }
    }

    private class FireLabel extends Button {

        FireLabel(int buttonNumber) {
            setGeometry((game.gui.WIDTH - 200) / 2 + 420, 240 + (210 * (buttonNumber - 1)), 200, 40);
            setText(gettext("CONTROLS.BUTTON") + " " + ((buttonNumber == 1) ? "A" : "B"), CENTER, font14);
            setColor(0x404040);
            setActive(false);
        }
    }

    private class FireButton extends ConfigButton {

        final int buttonNumber;

        FireButton(int buttonNumber) {
            this.buttonNumber = buttonNumber;
            configParam = (buttonNumber == 1) ? ConfigParam.BUTTON_1 : ConfigParam.BUTTON_2;
            setGeometry((game.gui.WIDTH - 200) / 2 + 420, 280 + (210 * (buttonNumber - 1)), 200, 46);
            setText("", CENTER, font14);
        }

        @Override
        public void refresh() {
            switch (selectedInputDeviceButton.config.type) {
                case KEYBOARD:
                    KeyboardConfig keyboardConfig = (KeyboardConfig) selectedInputDeviceButton.config;
                    int value = (buttonNumber == 1) ? keyboardConfig.button1 : keyboardConfig.button2;
                    if (entryMode) {
                        setText("?");
                        setColor(0xEB9532);
                    } else {
                        setText(Input.Keys.toString(value).toUpperCase());
                        setColor(0x548854);
                    }
                    break;

                case JOYSTICK:
                    JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
                    int index = (buttonNumber == 1) ? joystickConfig.button1 : joystickConfig.button2;
                    if (entryMode) {
                        setText("?");
                        setColor(0xEB9532);
                    } else if (index == -1) {
                        setText(gettext("CONTROLS.UNKNOWN"));
                        setColor(0xB40000);
                    } else {
                        setText(index);
                        setColor(0x548854);
                    }
                    break;
            }
        }
    }

    private class ExitButton extends Button {

        ExitButton() {
            setColor(0xC84200);
            setGeometry((game.gui.WIDTH - 180) / 2, 660, 180, 36);
            setText(gettext("EXIT"), CENTER, font14);
        }

        @Override
        public void onFire1Up() {
            Controllers.removeListener(joystickListener);
            game.settings.save();
            game.setScreen(new Main(game));
        }
    }

    private class JoystickListener extends ControllerAdapter {

        @Override
        public boolean buttonUp(Controller controller, int buttonIndex) {
            if (selectedInputDeviceButton.config.type != InputDevice.Type.JOYSTICK || listeningConfigButton == null) {
                return false;
            }

            if (EMath.isAmong(listeningConfigButton.configParam, buttonParams)) {
                JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
                if (controller.getName().equals(joystickConfig.name)) {
                    listeningConfigButton.setJoystickConfigParam(-1, buttonIndex);
                }
            }
            return false;
        }

        @Override
        public boolean axisMoved(Controller controller, int axisIndex, float value) {
            if (selectedInputDeviceButton.config.type != InputDevice.Type.JOYSTICK || listeningConfigButton == null) {
                return false;
            }

            if (EMath.isAmong(listeningConfigButton.configParam, axisParams)) {
                JoystickConfig joystickConfig = (JoystickConfig) selectedInputDeviceButton.config;
                if (controller.getName().equals(joystickConfig.name)) {
                    listeningConfigButton.setJoystickConfigParam(axisIndex, -1);
                }
            }
            return false;
        }
    }

    private class SetupInputProcessor extends InputAdapter {

        public boolean keyUp(int keycode) {
            listeningConfigButton.setKeyCode(keycode);
            return false;
        }
    }
}

