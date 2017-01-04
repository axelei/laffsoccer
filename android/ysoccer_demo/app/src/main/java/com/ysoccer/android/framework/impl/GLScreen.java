package com.ysoccer.android.framework.impl;

import android.opengl.GLU;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.Input.TouchEvent;
import com.ysoccer.android.framework.Screen;
import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.math.Vector2;
import com.ysoccer.android.ysdemo.Assets;
import com.ysoccer.android.ysdemo.Settings;
import com.ysoccer.android.ysdemo.gui.Widget;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public abstract class GLScreen extends Screen {
    protected final GLGraphics glGraphics;
    protected final GLGame glGame;

    Widget selectedWidget;

    public Widget getSelectedWidget() {
        return selectedWidget;
    }

    public void setSelectedWidget(Widget selectedWidget) {
        this.selectedWidget = selectedWidget;
    }

    private List<Widget> widgets;

    public List<Widget> getWidgets() {
        return widgets;
    }

    final static int NONE = -1;
    final static int FIRE1_UP = 0;
    final static int FIRE1_HOLD = 1;
    protected final static int FIRE1_DOWN = 2;

    public static final int MENU_GAME_OPTIONS = 2;

    private int guiEventType;

    private Texture background;

    public Texture getBackground() {
        return background;
    }

    public void setBackground(Texture background) {
        this.background = background;
    }

    Vector2 touchPoint;

    protected boolean keyBackHw;

    public GLScreen(Game game) {
        super(game);
        glGame = (GLGame) game;
        glGraphics = glGame.getGLGraphics();
        selectedWidget = null;
        widgets = new ArrayList<Widget>();
        touchPoint = new Vector2();
        keyBackHw = false;
    }

    @Override
    public void update(float deltaTime) {
        Assets.music.setVolume(glGame.settings.musicVolume);

        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();

        glGame.touchInput.touchEvents = touchEvents;
        glGame.touchInput.readInput();
        if (glGame.gamepadInput != null) {
            glGame.gamepadInput.readInput();
        }

        guiEventType = NONE;

        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            touchPoint.set(event.x, event.y);
            touchToGui(touchPoint);
            if (event.type == TouchEvent.TOUCH_UP) {
                guiEventType = FIRE1_UP;
            }
            if (event.type == TouchEvent.TOUCH_DOWN) {
                guiEventType = FIRE1_DOWN;
            }

            if (selectedWidget == null || selectedWidget.entryMode == false) {
                int len2 = widgets.size();
                for (int j = 0; j < len2; j++) {
                    Widget widget = widgets.get(j);
                    if (widget.isPointInside(touchPoint)
                            && widget.isVisible && widget.isActive) {
                        selectedWidget = widget;
                    }
                }
            }

            if (selectedWidget != null && !selectedWidget.isPointInside(touchPoint)) {
                guiEventType = NONE;
            }
        }

        readMenuInput();

        for (Widget widget : widgets) {
            if (widget.getDirty()) {
                widget.refresh();
            }
            widget.setDirty(false);
        }

        int ws = widgets.size();
        for (int i = 0; i < ws; i++) {
            Widget widget = widgets.get(i);
            widget.update();
        }

        if (selectedWidget != null && selectedWidget.isActive) {
            switch (guiEventType) {
                case FIRE1_DOWN:
                    selectedWidget.onFire1Down();
                    break;
                case FIRE1_UP:
                    selectedWidget.onFire1Up();
                    break;
                case NONE:
                    //do nothing
                    break;
            }
        }

        if (keyBackHw == true) {
            onKeyBackHw();
        }
    }

    public void onKeyBackHw() {
    }

    private Widget readMenuInput() {

        int len = widgets.size();
        for (int i = 0; i < len; i++) {
            Widget widget = widgets.get(i);
            widget.isSelected = false;
        }
        /*
        'fire a delay
		If menu_input.fa = 1
			If menu_input.fa0 = 0
				menu_input.tfa = 10
			Else If menu_input.tfa = 0
				menu_input.tfa = 4
			EndIf
		Else
			If menu_input.fa0 = 0
				menu_input.tfa = 0
			EndIf
		EndIf
	
		If menu_input.tfa > 0 Then menu_input.tfa = menu_input.tfa - 1
	
		'fire b delay
		If menu_input.fb = 1
			If menu_input.fb0 = 0
				menu_input.tfb = 10
			Else If menu_input.tfb = 0
				menu_input.tfb = 4
			EndIf
		Else
			If menu_input.fb0 = 0
				menu_input.tfb = 0
			EndIf
		EndIf
	
		If menu_input.tfb > 0 Then menu_input.tfb = menu_input.tfb - 1
	
		'old values
		menu_input.x0 = menu_input.x
		menu_input.y0 = menu_input.y
		menu_input.fa0 = menu_input.fa
		menu_input.fb0 = menu_input.fb
	
		menu_input.x = 0
		menu_input.y = 0
		menu_input.fa = 0
		menu_input.fb = 0
		For Local i:t_input = EachIn input_devices
			''x movement
			Local x:Int = i.get_x()
			If (x <> 0)
				menu_input.x = x
			EndIf
			
			''y movement
			Local y:Int = i.get_y()
			If (y <> 0)
				menu_input.y = y
			EndIf
			
			''fire 1
			Local f1:Int = i.get_fire1()
			If (f1 <> 0)
				menu_input.fa = 1
			EndIf
			
			''fire 2
			Local f2:Int = i.get_fire2()
			If (f2 <> 0)
				menu_input.fb = 1
			EndIf
		Next
		
		If (game_settings.mouse_enabled)
			If (mouse.button1 = 1)
				menu_input.fa = 1
			EndIf
			If (mouse.button2 = 1)
				menu_input.fb = 1
			EndIf
		EndIf
		
		''entry mode
		If (Self.selected_widget <> Null)
			If (Self.selected_widget.entry_mode = True)
				Self.selected_widget.selected = True
				Return Self.selected_widget
			EndIf
		EndIf
	
		Local bias:Int = 7
	
		'up/down
		If (menu_input.y = -1 And menu_input.ty = 0)
			Local button:t_widget = Self.selected_widget
			Local dist_min:Float, distance:Float
			dist_min = 50000
			For Local w:t_widget = EachIn Self.widgets
				If (w <> button)
					If (w <> Null)
						If (w.visible And w.active)
							If ((w.y + w.h) <= button.y)
								distance = hypo(bias * ((w.x +0.5*w.w) -(button.x +0.5*button.w)), (w.y +0.5*w.h) -(button.y +0.5*button.h))
								If (distance < dist_min)
									dist_min = distance
									Self.selected_widget = w
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			Next
		Else If (menu_input.y = 1 And menu_input.ty = 0)
			Local button:t_widget = Self.selected_widget
			Local dist_min:Float, distance:Float
			dist_min = 50000
			For Local w:t_widget = EachIn Self.widgets
				If (w <> button)
					If (w <> Null)
						If (w.visible And w.active)
							If (w.y => (button.y + button.h))
								distance = hypo(bias * ((w.x +0.5*w.w) -(button.x +0.5*button.w)), (w.y +0.5*w.h) -(button.y +0.5*button.h))
								If (distance < dist_min)
									dist_min = distance
									Self.selected_widget = w
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			Next
		EndIf
		
		'left/right
		If (menu_input.x = -1 And menu_input.tx = 0) 'And menu_input.f = 0
			Local button:t_widget = Self.selected_widget
			Local dist_min:Float, distance:Float
			dist_min = 50000
			For Local w:t_widget = EachIn Self.widgets
				If (w <> button)
					If (w <> Null)
						If (w.visible And w.active)
							If ((w.x + w.w) <= button.x)
								distance = hypo((w.x +0.5*w.w) -(button.x +0.5*button.w), bias * ((w.y +0.5*w.h) -(button.y +0.5*button.h)))
								If (distance < dist_min)
									dist_min = distance
									Self.selected_widget = w
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			Next
		Else If (menu_input.x = 1 And menu_input.tx = 0) 'And menu_input.f = 0
			Local button:t_widget = Self.selected_widget
			Local dist_min:Float, distance:Float
			dist_min = 50000
			For Local w:t_widget = EachIn Self.widgets
				If (w <> button)
					If (w <> Null)
						If (w.visible And w.active)
							If (w.x => (button.x + button.w))
								distance = hypo((w.x +0.5*w.w) -(button.x +0.5*button.w), bias * ((w.y +0.5*w.h) -(button.y +0.5*button.h)))
								If (distance < dist_min)
									dist_min = distance
									Self.selected_widget = w
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			Next
		EndIf
	
		'x-y delays
		If menu_input.x <> 0
			If menu_input.x0 = 0
				menu_input.tx = 8
			Else If menu_input.tx = 0
				menu_input.tx = 2
			EndIf
		Else
			menu_input.tx = 0
		EndIf
		If menu_input.y <> 0
			If menu_input.y0 = 0
				menu_input.ty = 8
			Else If menu_input.ty = 0
				menu_input.ty = 2
			EndIf
		Else
			menu_input.ty = 0
		EndIf
	
		If menu_input.tx > 0 Then menu_input.tx = menu_input.tx - 1
		If menu_input.ty > 0 Then menu_input.ty = menu_input.ty - 1
	*/
        if (selectedWidget != null) {
            selectedWidget.isSelected = true;
        }

        return selectedWidget;
    }

    @Override
    public void present(float deltaTime) {
        GL10 gl = glGraphics.getGL();

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, 0, Settings.screenWidth, Settings.screenHeight, 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glEnable(GL10.GL_TEXTURE_2D);
        glGraphics.setColor(255, 255, 255);

        if (background != null) {
            glGraphics.drawTextureRect(background, 0, 0, Settings.screenWidth,
                    Settings.screenHeight);
        }

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(Settings.guiOriginX, Settings.guiOriginY, 0);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        int s = widgets.size();
        for (int i = 0; i < s; i++) {
            Widget widget = widgets.get(i);
            widget.render(glGraphics);
        }
        /*
		 * If (game_settings.mouse_enabled) draw_image img_arrow, mouse.x,
		 * mouse.y EndIf
		 */

        gl.glDisable(GL10.GL_BLEND);
    }

    protected void touchToGui(Vector2 touch) {
        touch.setX(touch.x * 100.0f / Settings.guiZoom - Settings.guiOriginX);
        touch.setY(touch.y * 100.0f / Settings.guiZoom - Settings.guiOriginY);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean keyBack() {
        keyBackHw = true;
        return true;
    }

    public String gettext(int id) {
        return glGame.getResources().getString(id);
    }

}
