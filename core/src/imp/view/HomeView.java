package imp.view;

import utils.elements.GalleryViewHorizontal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.coder5560.game.assets.Assets;
import com.coder5560.game.listener.OnCompleteListener;
import com.coder5560.game.ui.UIUtils;
import com.coder5560.game.views.View;

public class HomeView extends View {
	GalleryViewHorizontal	galleryViewHorizontal;
	GalleryViewHorizontal	galleryTopBar;
	public HomeView() {
	}

	public HomeView buildComponent() {
//		Table tbContent = new Table();
//		tbContent.setSize(Constants.WIDTH_SCREEN, Constants.HEIGHT_SCREEN - Constants.HEIGHT_ACTIONBAR);
//
//		Table tbTopBar = new Table();
//		tbContent.setSize(Constants.WIDTH_SCREEN, Constants.HEIGHT_SCREEN - Constants.HEIGHT_ACTIONBAR);
		
		
		
		galleryViewHorizontal = new GalleryViewHorizontal(this, 1);
		for (int i = 0; i < 10; i++) {
			Table page = galleryViewHorizontal.newPage();
			page.setBackground(new NinePatchDrawable(new NinePatch(
					Assets.instance.ui.reg_ninepatch)));
			Label lb = UIUtils.getLabel("View " + (i + 1), Color.BLACK);
			lb.setAlignment(Align.center);
			page.add(lb).expand().fill().center();
		}
		return this;
	}

	@Override
	public void show(OnCompleteListener listener) {
		super.show(listener);
	}

	@Override
	public void hide(OnCompleteListener listener) {
		super.hide(listener);
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void destroyComponent() {
	}

	@Override
	public void back() {
	}
}
