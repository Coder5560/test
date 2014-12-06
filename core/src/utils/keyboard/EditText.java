package utils.keyboard;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

/**
 * 
 * @author HungHD
 *
 */

public class EditText implements KeyboardObserver{
	
	private TextureRegion _imgBackground = null;
	private NinePatch _patch = null;
	private BitmapFont _font = null;
	private Color _originalColor;
	private Color _color;
	private Color _hintColor;
	
	private int _maxLength;
	private int _maxLine;
	private int _numberLine;
	private float _width;
	private float _height;
	private float _paddingLeft;
	private float _paddingRight;
	private float _paddingBottom;
	private float _paddingTop;
	private float _delay;
	private float _pwdDelay;
	private float _deltaX;
	private boolean _activate;
	private boolean _visible;
	private boolean _autoResize;
	private String _text;
	private String _password;
	private String _hint;
	
	private Vector2 _pos;
	private Vector3 _cursorPos;
	private Rectangle _touchedArea;
	private Rectangle _displayArea;
	private int _type;
	private int _mode;
	private TextBounds _bound;
	private ShapeRenderer _shapeRenderer;
	private OnCompleteInputListener _completeListener;
	private OnClickedListener _clickedListener;
	private Event _event;
	private Camera _camera;
	private Rectangle _viewport;
		
	public EditText(float x, float y, String text, BitmapFont font, Camera camera, Rectangle viewport) {
		_pos = new Vector2(x, y);
		_camera = camera;
		_viewport = viewport;
		_text = text;
		_hint = "";
		_password = "";
		_type = KeyboardConfig.NORMAL;
		_mode = KeyboardConfig.SINGLE_LINE;
		_visible = true;
		_font = font;
		_color = Color.BLACK;
		_hintColor = new Color(_color.r, _color.g, _color.b, 0.4f);
		_maxLength = 1024;
		_maxLine = 1024;
		_paddingLeft = 5;
		_paddingRight = 5;
		_paddingTop = 5;
		_paddingBottom = 2;
		_pwdDelay = -1;
		_numberLine = 1;
		_shapeRenderer = new ShapeRenderer();
		_cursorPos = new Vector3(0, _font.getCapHeight(), 1);
		_displayArea = new Rectangle();
	}
	
	public void draw(SpriteBatch batch) {
		if (_visible) {
			// draw
//			_deltaX = 0;
			if (_type == KeyboardConfig.PASSWORD) {
				_bound = _font.getBounds(_password);
				if (_bound.width > _width - _paddingLeft - _paddingRight)
					_deltaX = _bound.width - _width + _paddingLeft + _paddingRight + 2;
				else
					_deltaX = 0;
			} else {
				if (_cursorPos.x > _width - _paddingLeft - _paddingRight)
					_deltaX = _cursorPos.x - _width + _paddingLeft + _paddingRight + 2;
				else 
					_deltaX = 0;
			}
			if (_patch != null) {
				if (_autoResize) {
					_patch.draw(batch, _pos.x, _pos.y, _width, _height + _font.getLineHeight() * (_numberLine - 1));
				} else {
					_patch.draw(batch, _pos.x, _pos.y, _width, _height);
				}
			}
			if (_imgBackground != null)
				batch.draw(_imgBackground, _pos.x, _pos.y);
			if (_font != null) {
				batch.flush();
				
				_originalColor = _font.getColor();
				_font.setColor(_color);
				_bound = _font.getBounds(_text);
				
				Rectangle scissors = new Rectangle();
				Rectangle clipBounds = new Rectangle(_pos.x +_paddingLeft, _pos.y + _paddingTop, 
						_width - _paddingLeft - _paddingRight, getHeight() - _paddingTop - _paddingBottom);
				ScissorStack.calculateScissors(_camera, _viewport.x, _viewport.y, _viewport.width, _viewport.height, batch.getTransformMatrix(), clipBounds, scissors);
				ScissorStack.pushScissors(scissors);
				if (_text.length() <= 0) {
					_font.setColor(_hintColor);
					_font.draw(batch, _hint, _pos.x + _paddingLeft, _pos.y + _height / 2 + _font.getCapHeight() / 2);
				} else {
					if (_type == KeyboardConfig.PASSWORD) {
						_font.drawMultiLine(batch, _password, _pos.x + _paddingLeft - _deltaX, _pos.y + (_height + _font.getCapHeight()) / 2 + _font.getLineHeight() * (_numberLine - 1));
					} else {
						_font.drawMultiLine(batch, _text, _pos.x + _paddingLeft - _deltaX, _pos.y + (_height + _font.getCapHeight()) / 2 + _font.getLineHeight() * (_numberLine - 1));
					}
				}
				batch.flush();
				ScissorStack.popScissors();
				
				_font.setColor(_originalColor);
			}
			
			if (_activate) {
				if (_delay > 0.4f) {
					if (_delay > 1)	_delay = 0;
					
					batch.end();
					
					_shapeRenderer.setProjectionMatrix(_camera.combined);
					_shapeRenderer.begin(ShapeType.Line);
					_shapeRenderer.setColor(_color);
					if (_type == KeyboardConfig.PASSWORD) {
						_bound = _font.getBounds(_password);
						_shapeRenderer.line(_pos.x + _paddingLeft + _bound.width - _deltaX, _pos.y + _height / 2 - 0.6f * _font.getCapHeight(), 
								_pos.x + _paddingLeft + _bound.width - _deltaX, _pos.y + _height / 2 + 0.6f * _font.getCapHeight());
					} else {
						_shapeRenderer.line(_pos.x + _paddingLeft + _cursorPos.x - _deltaX, _pos.y + _height / 2 + _font.getLineHeight() * (_numberLine - _cursorPos.z) - 0.6f * _font.getCapHeight(), 
								            _pos.x + _paddingLeft + _cursorPos.x - _deltaX, _pos.y + _height / 2 + _font.getLineHeight() * (_numberLine - _cursorPos.z) + 0.6f * _font.getCapHeight());
					}
					_shapeRenderer.end();
					
					batch.begin();
				}
			}
		}
	}
	
	public void setVisible(boolean flag) {
		_visible = flag;
	}
	
	@Override
	public String getHint() {
		return _hint;
	}
	
	@Override
	public int getMode() {
		return _mode;
	}
	
	@Override
	public int getType() {
		return _type;
	}
	
	@Override
	public int getMaxLength() {
		return _maxLength;
	}
	
	@Override
	public int getMaxLine() {
		return _maxLine;
	}
	
	public float getWidth() {
		return this._width;
	}
	
	public float getHeight() {
		if (_autoResize)
			return (_height + _font.getLineHeight() * (_numberLine - 1));
		else 
			return _height;
	}
	
	public void dispose() {
		_imgBackground = null;
		_patch = null;
		_font = null;
	}

	public void update(double deltaT) {
		if (_visible && _activate) {
			_delay += deltaT;
			if (_pwdDelay >= 0) {
				_pwdDelay += deltaT;
				
				if (_pwdDelay > 0.5f) {
					_password = "";
					for (int i = 0; i < _text.length(); i++)
						_password += "*";
					_pwdDelay = -1;
				}
			}
		}
	}

	public void setX(float x) {
		_pos.x = x;
	}

	public void setY(float y) {
		_pos.y = y;
	}

	public void setDeltaX(float delta) {
		_pos.x += delta;
	}

	public void setDeltaY(float delta) {
		_pos.y += delta;
	}
	
	public float getX() {
		return _pos.x;
	}

	public float getY() {
		return _pos.y;
	}

	@Override
	public void dispatch(Event event) {
		_event = event;
		
		Action action = event.action;
		int count = 1;
		int start = -1;
		for (int i = 0; i <= _event.cursor; i++) {
			if (_event.data.get(i) == '\n') {
				count++;
				start = i;
			}
		}
		CharSequence temp = _event.data.toString("").subSequence(start + 1, _event.cursor + 1);
		TextBounds bound = _font.getBounds(temp);
		_cursorPos.set(bound.width, _font.getCapHeight(), count);
		setText(_event.data.toString(""));
		if (action == Action.DONE && _completeListener != null) {
			_completeListener.onComplete(_text);
		}
	}
	
	@Override
	public Event getLatestEvent() {
		return _event;
	}
	
	public void setClicked(boolean flag) {
		if (_clickedListener != null)
			_clickedListener.onClicked();
	}
	
	public boolean contains(float x, float y, float viewportX, float viewportY, float viewportWidth,
			float viewportHeight) {
		if (_touchedArea == null) {
			float h = _height;
			if (_autoResize)	h = _height + _font.getLineHeight() * (_numberLine - 1);
			_displayArea.x = _pos.x + viewportX;
			_displayArea.y =  _pos.y + viewportY;
			_displayArea.width = _width;
			_displayArea.height = h;
			return _displayArea.contains(x, y);
		} else {
			_displayArea.x = _touchedArea.x + viewportX;
			_displayArea.y =  _touchedArea.y + viewportY;
			_displayArea.width = _touchedArea.width;
			_displayArea.height = _touchedArea.height;
			return _displayArea.contains(x, y);
		}
	}
	
	public void setBackground(TextureRegion texture) {
		_imgBackground = texture;
		if (_imgBackground != null) {
			_width = Math.abs(_imgBackground.getRegionWidth());
			_height = Math.abs(_imgBackground.getRegionHeight());
		}
	}
	
	public void setBackground(NinePatch patch, float width, float height) {
		_patch = patch;
		_width = width;
		_height = height;
	}
	
	public void setWidth(float width) {
		_width = width;
	}
	
	public void setHeight(float height) {
		_height = height;
	}
	
	public void setMode(int mode) {
		_mode = mode;
	}
	
	public void setText(String text) {
		if (_type == KeyboardConfig.PASSWORD) {
			if (text.length() > _text.length()) {
				_password = "";
				for (int i = 0; i < text.length() - 1; i++)
					_password += "*";
				_password += text.charAt(text.length() - 1);
				_pwdDelay = 0;
			} else if (text.length() < _text.length()) {
				_password = "";
				for (int i = 0; i < text.length(); i++)
					_password += "*";
			}
		}
		_text = text;
		_updateNumberLine();
	}
	
	public void setHint(String hint) {
		_hint = hint;
	}
	
	public void setFont(BitmapFont font) {
		_font = font;
	}
	
	public void setFontColor(Color color) {
		_color = color;
		_hintColor = new Color(_color.r, _color.g, _color.b, 0.4f);
	}
	
	public void setType(int type) {
		_type = type;
	}
	
	public void setAutoResize(boolean flag) {
		_autoResize = flag;
	}
	
	public void setMaxLength(int length) {
		_maxLength = length;
	}
	
	public void setMaxLine(int number) {
		_maxLine = number;
	}
	
	public void setPaddingLeft(float padding) {
		_paddingLeft = padding;
	}
	
	public float getPaddingLeft() {
		return _paddingLeft;
	}
	
	public void setPaddingRight(float padding) {
		_paddingRight = padding;
	}
	
	public float getPaddingRight() {
		return _paddingRight;
	}
	
	public void setPaddingTop(float padding) {
		_paddingTop = padding;
	}
	
	public float getPaddingTop() {
		return _paddingTop;
	}
	
	public void setPaddingBottom(float padding) {
		_paddingBottom = padding;
	}
	
	public float getPaddingBottom() {
		return _paddingBottom;
	}
	
	public float getNumberLines() {
		return _numberLine;
	}
	
	public void setCompleteListener(OnCompleteInputListener listener) {
		_completeListener = listener;
	}
	
	public void setClickedListener(OnClickedListener listener) {
		_clickedListener = listener;
	}
	
	public void setActivate(boolean flag) {
		_activate = flag;
	}
	
	public Vector2 getTextPosition() {
		return new Vector2(_pos.x + _paddingLeft - _deltaX, _pos.y + (_height + _font.getCapHeight()) / 2 + _font.getLineHeight() * (_numberLine - 1));
	}

	public void setTouchedArea(float x, float y, float width, float height) {
		_touchedArea = new Rectangle(x, y, width, height);
	}
	
	public BitmapFont getFont() {
		return _font;
	}
	
	public String getText() {
		return _text;
	}
	
	public void reset() {
		_event = new Event(Action.EDIT);
		_text = "";
		_password = "";
		_cursorPos.set(0, _font.getCapHeight(), 1);
	}
	
	public interface OnCompleteInputListener {
		public void onComplete(String input);
	}
	
	public interface OnClickedListener {
		public void onClicked();
	}
	
	private void _updateNumberLine() {
		_numberLine = 1;
		for (int i = 0; i < _text.length(); i++) {
			if (_text.charAt(i) == '\n')
				_numberLine++;
		}
	}
}
