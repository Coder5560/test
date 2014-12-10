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

public class TopBarView extends View {
	GalleryViewHorizontal	galleryViewHorizontal;

	public TopBarView() {
	}

	public TopBarView buildComponent() {
		setBackground(new NinePatchDrawable(new NinePatch(
				Assets.instance.ui.reg_ninepatch, Color.GRAY)));
		galleryViewHorizontal = new GalleryViewHorizontal(this, 4);
		for (int i = 0; i < 10; i++) {
			Table page = galleryViewHorizontal.newPage();
			page.setBackground(new NinePatchDrawable(new NinePatch(
					Assets.instance.ui.reg_ninepatch, Color.RED)));
			Label lb = UIUtils.getLabel("Tab " + (i + 1), Color.WHITE);
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
