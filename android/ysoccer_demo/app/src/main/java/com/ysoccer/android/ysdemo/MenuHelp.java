package com.ysoccer.android.ysdemo;

import com.ysoccer.android.framework.Game;
import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.impl.GLScreen;
import com.ysoccer.android.ysdemo.gui.Button;
import com.ysoccer.android.ysdemo.gui.Widget;

public class MenuHelp extends GLScreen {

	class BackButton extends Button {
		public BackButton() {
			setGeometry(-Settings.guiOriginX, -Settings.guiOriginY,
					Settings.GUI_WIDTH + 2*Settings.guiOriginX,
					Settings.GUI_HEIGHT + 2*Settings.guiOriginY);
		}

		public void onFire1Down() {
			game.setScreen(new MenuMain(game));
		}
	}

	public MenuHelp(Game game) {
		super(game);
		
		Assets.helpMenuBackground = new Texture(glGame,
				"images/backgrounds/menu_help.png");
		
		setBackground(Assets.helpMenuBackground);

		Widget w;

		w = new BackButton();
		getWidgets().add(w);
	}

	@Override
	public void resume() {
		super.resume();
		Assets.helpMenuBackground.reload();
	}

	@Override
	public void onKeyBackHw() {
		game.setScreen(new MenuMain(game));
	}
	
}
