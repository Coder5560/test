package utils.screen;

import utils.keyboard.VirtualKeyboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.coder5560.game.enums.Constants;
import com.coder5560.game.enums.GameState;

public abstract class AbstractGameScreen implements Screen, InputProcessor,
		GestureListener {
	public GameCore					parent;
	public Viewport					viewport;
	public OrthographicCamera		camera;
	public SpriteBatch				batch;
	public Stage					stage;
	public GameState				gameState;
	public static VirtualKeyboard	keyboard;

	public AbstractGameScreen(GameCore game) {
		this.parent = game;
	}

	public abstract void resize(int width, int height);

	public void show() {
		gameState = GameState.INITIAL;
		camera = new OrthographicCamera(Constants.WIDTH_SCREEN,
				Constants.HEIGHT_SCREEN);
		viewport = new StretchViewport(Constants.WIDTH_SCREEN,
				Constants.HEIGHT_SCREEN, camera);
		stage = new Stage(viewport);
		batch = new SpriteBatch();
		keyboard = new VirtualKeyboard(batch);

		parent.inputMultiplexer = new InputMultiplexer(this, keyboard, stage,
				new GestureDetector(this));
		Gdx.input.setInputProcessor(getInputProcessor());
	}

	public abstract void update(float delta);

	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update(delta);

		stage.act(delta);
		stage.draw();

		keyboard.update(delta);
		keyboard.draw();
	}

	public void pause() {
	}

	public void hide() {
	}

	public void resume() {
	}

	public InputProcessor getInputProcessor() {
		return parent.inputMultiplexer;
	}

	public void dispose() {
		keyboard.dispose();
		stage.dispose();
		batch.dispose();
	}

	public void switchScreen(AbstractGameScreen screen,
			ScreenTransition transition) {
		if (transition == null)
			parent.setScreen(screen);
		else
			parent.setScreen(screen, transition);
	}

	// ===================== input method =======================
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {

		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {

		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {

		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {

		return false;
	}

	@Override
	public boolean keyDown(int keycode) {

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {

		return false;
	}

	@Override
	public boolean keyTyped(char character) {

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {

		return false;
	}

	@Override
	public boolean scrolled(int amount) {

		return false;
	}

}
