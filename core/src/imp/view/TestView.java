package imp.view;

import java.util.HashMap;

import utils.factory.StringSystem;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.coder5560.game.enums.Constants;
import com.coder5560.game.listener.OnResponseListener;
import com.coder5560.game.ui.DialogInput;
import com.coder5560.game.views.IViewController;
import com.coder5560.game.views.View;

public class TestView extends View {

	@Override
	public void build(Stage stage, IViewController viewController,
			String viewName, Rectangle bound) {
		super.build(stage, viewController, StringSystem.VIEW_DIALOG_INPUT,
				bound);
		DialogInput input = new DialogInput();
		input.build(stage, viewController, viewName, bound);
		input.buildComponent();
		input.setOnResponseListener(new HashMap<String, String>(),
				new OnResponseListener() {

					@Override
					public void onOk(String name, String quality) {

					}

					@Override
					public void onOk() {

					}

					@Override
					public void onCancel() {

					}
				});
		input.show();
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void destroyComponent() {
		super.destroyComponent();
	}

	@Override
	public void back() {
		hide();
	}

}
