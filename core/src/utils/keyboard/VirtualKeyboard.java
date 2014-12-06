package utils.keyboard;

import net.sourceforge.vietpad.inputmethod.TelexIM;
import net.sourceforge.vietpad.inputmethod.VietKeyInput;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.OnscreenKeyboard;
import com.badlogic.gdx.utils.CharArray;

/**
 * 
 * @author HungHD
 * 
 */
public class VirtualKeyboard implements OnscreenKeyboard, InputProcessor {
	enum Size {
		Small, Medium, Large
	}

	enum State {
		Loading, Running
	}

	final float						MAX_KEY_WIDTH				= 150;
	final float						MAX_KEY_HEIGHT				= 150;

	public static final String[][]	DEFAULT_QWERTY				= new String[][] { { "q", "w", "e", "r", "t", "y", "u", "i", "o", "p" },
			{ "a", "s", "d", "f", "g", "h", "j", "k", "l" }, { "$Shift$", "z", "x", "c", "v", "b", "n", "m", "$Delete$" },
			{ "$Close$", "$Mode$", "$Language$", "$Space$", ".", ",", "$Enter$" } };

	public static final String[][]	DEFAULT_NUMBERS				= new String[][] { { "1", "2", "3", }, { "4", "5", "6", }, { "7", "8", "9", },
			{ "*", "0", "#", }, { "$Mode$", "$Space$", "$Delete$", "$OK$" } };

	public static final String[][]	DEFAULT_NUMBERS_SYMBOLS_1	= new String[][] { { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" },
			{ "!", "@", "#", "$", "%", "&", "*", "?", "/" }, { "_", "\"", "\'", "(", ")", "-", "+", "=", "$Delete$" },
			{ "$Close$", "$Mode1$", ";", "$Space$", ":", "$Mode2$", "$Enter$" } };

	public static final String[][]	DEFAULT_NUMBERS_SYMBOLS_2	= new String[][] { { "€", "¥", "£", "~", "`", "\\", "^", "[", "]", "¡" },
			{ "¿", "{", "}", "<", ">", "|", "§", "«", "»" }, { ":)", ";)", ":(", ":'(", ":o", ":P", ":$", ":S", "$Delete$" },
			{ "$Close$", "$Mode1$", ",", "$Space$", ".", "$Mode2$", "$Enter$" } };

	private static String[]			_languageMode				= { "EN", "VN" };
	private static String[]			_modeLabel					= { "123#", "1/2", "2/2" };

	private BitmapFont				_font;
	private BitmapFont				_modeFont;
	private BitmapFont				_popupFont;
	private NinePatch				_normalButton;
	private NinePatch				_pressedNormalButton;
	private NinePatch				_specialButton;
	private NinePatch				_pressedSpecialButton;
	private NinePatch				_popupButton;
	private TextureRegion			_background;
	private TextureRegion			_closeButton;
	private TextureRegion			_deleteButton;
	private TextureRegion			_enterButton;
	private TextureRegion			_okButton;
	private TextureRegion			_acShiftButton;
	private TextureRegion			_inShiftButton;

	private TextureAtlas			_atlas;
	private TextureAtlas			_largeAtlas;

	private Sound					_deleteFX;
	private Sound					_spaceFX;
	private Sound					_standardFX;

	private int						_language;
	private int						_currentMode;
	private int						_enterButtonDefaultIndex;
	private int						_enterButtonNumberIndex;
	private int						_maxLength					= 1024;
	private int						_maxLine					= 1024;
	private int						_type;
	private int						_mode;

	private KeyButton[]				_currentKeyBoard;
	private KeyButton[]				_defaultQwerty;
	private KeyButton[]				_defaultNumbersSymbols1;
	private KeyButton[]				_defaultNumbersSymbols2;

	public static final int			INSERT_CHAR					= 1;
	public static final int			DELETE_CHAR					= 2;
	public static final int			CHANGE_MODE					= 3;
	public static final int			SHIFT						= 4;
	public static final int			OK							= 5;
	public static final int			SPACE						= 6;
	public static final int			T9							= 7;

	private float					_width;
	private float					_height;
	private float					_iconScale;
	private float					_begin;
	private float					_change;
	private float					_duration;
	private float					_time;
	private float					_screenWidth;
	private float					_screenHeight;

	private int						_shift						= 0;

	private boolean					_animate					= false;
	private boolean					_enableVietKey				= true;

	private Vector2					_pos;
	private EditText				_editText;
	private TextField				_textField;
	private SimpleCharSequence		_textBuffer;
	private OnDoneListener			_doneListener;
	private OnHideListener			_hideListener;
	private OnBackSpaceComma		_backspaceComma;
	private TelexIM					_telex;

	private SpriteBatch				_batch;
	private OrthographicCamera		_camera;
	private Rectangle				_viewport;
	private Size					_size;
	private AssetManager			_assetManager;
	private State					_state;

	public VirtualKeyboard(SpriteBatch batch) {
		_pos = new Vector2();
		_textBuffer = new SimpleCharSequence();
		_screenWidth = Gdx.graphics.getWidth();
		_screenHeight = Gdx.graphics.getHeight();

		int dimension = (int) Math.max(_screenWidth, _screenHeight);
		if (dimension < 500) {
			_size = Size.Small;
		} else if (dimension < 1000) {
			_size = Size.Medium;
		} else {
			_size = Size.Large;
		}

		_width = _screenWidth;
		_height = (_screenWidth / _screenHeight > 1.0f ? (_screenHeight * ((_size == Size.Large) ? .5f : .6f)) : (_screenHeight * ((_size == Size.Large) ? .4f
				: .45f)));
		_duration = 0.1f;
		_telex = new TelexIM();

		_camera = new OrthographicCamera(_screenWidth, _screenHeight);
		_camera.setToOrtho(false, _screenWidth, _screenHeight);
		_batch = batch;
		_viewport = new Rectangle(0, 0, _screenWidth, _screenHeight);

		_state = State.Loading;
		_load();
	}

	public String getText() {
		return _textBuffer.chars.toString("");
	}

	public void setText(String content) {
		clear();
		for (int i = 0; i < content.length(); i++) {
			char character = content.charAt(i);
			if (character == '\r')
				character = '\n';
			// if we get \b, we remove the last inserted character
			if (character == '\b') {
				_textBuffer.backspace();
				return;
			}

			if (character == 127) {
				_textBuffer.delete();
				return;
			}

			if (_language == 1 && isAccent(character) && _textBuffer.getCurrentChar() != 32) {
				String[] temp = _textBuffer.toString().split("\n");
				temp = temp[temp.length - 1].split(" ");
				String currentWord = temp[temp.length - 1];
				if (currentWord.length() > 0) {
					char accent = _telex.getAccentMark(character, '\0', currentWord);
					if (accent >= '0' && accent <= '9') {
						String vietWord = VietKeyInput.toVietWord(currentWord, accent);
						if (!vietWord.equals(currentWord)) {
							for (int j = 0; j < currentWord.length(); j++) {
								_textBuffer.backspace();
							}
							for (int j = 0; j < vietWord.length(); j++) {
								_textBuffer.add(vietWord.charAt(i));
							}
							return;
						}
					}
				}
			}

			// else we just insert the character
			_textBuffer.add(character);
		}
	}

	private void _initDefaultQwerty() {
		_defaultQwerty = new KeyButton[35];

		float w = Math.min(this._width / 10, MAX_KEY_WIDTH);
		float h = Math.min(this._height / 4, MAX_KEY_HEIGHT);
		float px = (this._width - w * 10f) / 9f;
		float px1 = (this._width - w * 9f) / 2f;
		float px2 = (this._width - w * 7f) / 2f;
		float sw = (this._width - w * 7.5f) / 2f;
		float sh = h;
		float paddingY = 0, py = 0;
		float paddingX = 0;
		if (w == MAX_KEY_WIDTH) {
			px = (this._width - w * 10f) / 11f;
			px1 = (this._width - w * 9f - px * 8f) / 2f;
			px2 = (this._width - w * 7f - px * 6f) / 2f;
			sw = 1.5f * MAX_KEY_WIDTH;
			paddingX = px;
		}
		if (h == MAX_KEY_HEIGHT) {
			py = (this._height - h * 4f) / 5f;
			paddingY = py;
		}
		if (w == MAX_KEY_WIDTH || h == MAX_KEY_HEIGHT) {
			_editText.setHeight(_font.getCapHeight() * 3);
			_pos.y = -(_height + 5 + _editText.getHeight());
			_editText.setX(_pos.x + 2);
			_editText.setY(_pos.y + _height);
		}
		int count = 0;

		for (int i = 0; i < DEFAULT_QWERTY[0].length; i++) {
			_defaultQwerty[i] = new KeyButton(DEFAULT_QWERTY[0][i], paddingX + i * (w + px), _pos.y + _height - paddingY - h, w, h, this);
			_defaultQwerty[i].setBackground(_normalButton);
			_defaultQwerty[i].setPressedBackground(_pressedNormalButton);
			_defaultQwerty[i].setPopupBackground(_popupButton);
			_defaultQwerty[i].setPopupFont(_popupFont);
			_defaultQwerty[i].setFont(_font);
		}
		count += DEFAULT_QWERTY[0].length;
		for (int i = 0; i < DEFAULT_QWERTY[1].length; i++) {
			_defaultQwerty[i + count] = new KeyButton(DEFAULT_QWERTY[1][i], px1 + i * (w + px), _pos.y + _height - paddingY - h - (h + py), w, h, this);
			_defaultQwerty[i + count].setBackground(_normalButton);
			_defaultQwerty[i + count].setPressedBackground(_pressedNormalButton);
			_defaultQwerty[i + count].setPopupBackground(_popupButton);
			_defaultQwerty[i + count].setPopupFont(_popupFont);
			_defaultQwerty[i + count].setFont(_font);
		}
		count += DEFAULT_QWERTY[1].length;
		for (int i = 0; i < DEFAULT_QWERTY[2].length; i++) {
			if (i == 0) {
				// shift button
				_defaultQwerty[i + count] = new KeyButton(DEFAULT_QWERTY[2][i], paddingX, _pos.y + _height - paddingY - h - 2 * (h + py), sw, sh, this);
				_defaultQwerty[i + count].setBackground(_specialButton);
				_defaultQwerty[i + count].setPressedBackground(_pressedSpecialButton);
				_defaultQwerty[i + count].setFont(_font);
				_defaultQwerty[i + count].setIcon(_inShiftButton);
				_defaultQwerty[i + count].setSubIcon(_acShiftButton);
				_defaultQwerty[i + count].setIconScale(_iconScale);
			} else if (i == DEFAULT_QWERTY[2].length - 1) {
				// delete button
				_defaultQwerty[i + count] = new KeyButton(DEFAULT_QWERTY[2][i], this._width - sw - paddingX, _pos.y + _height - paddingY - h - 2 * (h + py),
						sw, sh, this);
				_defaultQwerty[i + count].setBackground(_specialButton);
				_defaultQwerty[i + count].setPressedBackground(_pressedSpecialButton);
				_defaultQwerty[i + count].setFont(_font);
				_defaultQwerty[i + count].setIcon(_deleteButton);
				_defaultQwerty[i + count].setIconScale(_iconScale);
			} else {
				_defaultQwerty[i + count] = new KeyButton(DEFAULT_QWERTY[2][i], px2 + (i - 1) * (w + px), _pos.y + _height - paddingY - h - 2 * (h + py), w, h,
						this);
				_defaultQwerty[i + count].setBackground(_normalButton);
				_defaultQwerty[i + count].setPressedBackground(_pressedNormalButton);
				_defaultQwerty[i + count].setPopupBackground(_popupButton);
				_defaultQwerty[i + count].setPopupFont(_popupFont);
				_defaultQwerty[i + count].setFont(_font);
			}
		}
		count += DEFAULT_QWERTY[2].length;
		float tempX = px2;
		for (int i = 0; i < DEFAULT_QWERTY[3].length; i++) {
			if (i == 0) {
				// close button
				_defaultQwerty[i + count] = new KeyButton(DEFAULT_QWERTY[3][i], paddingX, _pos.y + _height - paddingY - h - 3 * (h + py), Math.min(sw / 2 + px2
						/ 2, 1.2f * sw), sh, this);
				_defaultQwerty[i + count].setBackground(_specialButton);
				_defaultQwerty[i + count].setPressedBackground(_pressedSpecialButton);
				_defaultQwerty[i + count].setFont(_font);
				_defaultQwerty[i + count].setIcon(_closeButton);
				// _defaultQwerty[i + count].setIconScale(iconScale);
			} else if (i == 1 || i == 2) {
				// mode button
				_defaultQwerty[i + count] = new KeyButton(DEFAULT_QWERTY[3][i], tempX, _pos.y + _height - paddingY - h - 3 * (h + py), w, h, this);
				_defaultQwerty[i + count].setBackground(_specialButton);
				_defaultQwerty[i + count].setPressedBackground(_pressedSpecialButton);
				_defaultQwerty[i + count].setFont(_modeFont);
				tempX += w + px;
			} else if (i == 3) {
				// space button
				_defaultQwerty[i + count] = new KeyButton(DEFAULT_QWERTY[3][i], px2 + 2 * (w + px), _pos.y + _height - paddingY - h - 3 * (h + py), 3 * w + 2
						* px, h, this);
				_defaultQwerty[i + count].setBackground(_normalButton);
				_defaultQwerty[i + count].setPressedBackground(_pressedSpecialButton);
				_defaultQwerty[i + count].setFont(_font);
				tempX += 3 * w + 3 * px;
			} else if (i == DEFAULT_QWERTY[3].length - 1) {
				// enter button
				_enterButtonDefaultIndex = i + count;
				_defaultQwerty[i + count] = new KeyButton(DEFAULT_QWERTY[3][i], this._width - Math.min(sw / 2 + px2 / 2, 1.2f * sw) - paddingX, _pos.y
						+ _height - paddingY - h - 3 * (h + py), Math.min(sw / 2 + px2 / 2, 1.2f * sw), sh, this);
				_defaultQwerty[i + count].setBackground(_specialButton);
				_defaultQwerty[i + count].setPressedBackground(_pressedSpecialButton);
				_defaultQwerty[i + count].setFont(_font);
				_defaultQwerty[i + count].setIcon(_enterButton);
				_defaultQwerty[i + count].setIconScale(_iconScale);
			} else {
				_defaultQwerty[i + count] = new KeyButton(DEFAULT_QWERTY[3][i], tempX, _pos.y + _height - paddingY - h - 3 * (h + py), w, h, this);
				_defaultQwerty[i + count].setBackground(_normalButton);
				_defaultQwerty[i + count].setPressedBackground(_pressedNormalButton);
				_defaultQwerty[i + count].setPopupBackground(_popupButton);
				_defaultQwerty[i + count].setPopupFont(_popupFont);
				_defaultQwerty[i + count].setFont(_font);
				tempX += w + px;
			}
		}
	}

	private void _initDefaultNumbersSymbols(KeyButton[] keys, String[][] pattern) {

		float w = Math.min(this._width / 10, MAX_KEY_WIDTH);
		float h = Math.min(this._height / 4, MAX_KEY_HEIGHT);
		float px = (this._width - w * 10f) / 9f;
		float px1 = (this._width - w * 9f) / 2f;
		float px2 = (this._width - w * 9.5f) / 2f;
		float px3 = (this._width - w * 7f) / 2f;
		float sw = (this._width - w * 7.5f) / 2f;
		float sh = h;
		float paddingY = 0, py = 0;
		float paddingX = 0;
		if (w == MAX_KEY_WIDTH) {
			px = (this._width - w * 10f) / 11f;
			px1 = (this._width - w * 9f - px * 8f) / 2f;
			px2 = (this._width - w * 9.5f - px * 8f) / 2f;
			px3 = (this._width - w * 7f - px * 6f) / 2f;
			sw = 1.5f * MAX_KEY_WIDTH;
			paddingX = px;
		}
		if (h == MAX_KEY_HEIGHT) {
			py = (this._height - h * 4f) / 5f;
			paddingY = py;
		}
		int count = 0;

		for (int i = 0; i < pattern[0].length; i++) {
			keys[i] = new KeyButton(pattern[0][i], paddingX + i * (w + px), _pos.y + _height - paddingY - h, w, h, this);
			keys[i].setBackground(_normalButton);
			keys[i].setPressedBackground(_pressedNormalButton);
			keys[i].setPopupBackground(_popupButton);
			keys[i].setPopupFont(_popupFont);
			keys[i].setFont(_font);
		}
		count += pattern[0].length;
		for (int i = 0; i < pattern[1].length; i++) {
			keys[i + count] = new KeyButton(pattern[1][i], px1 + i * (w + px), _pos.y + _height - paddingY - h - (h + py), w, h, this);
			keys[i + count].setBackground(_normalButton);
			keys[i + count].setPressedBackground(_pressedNormalButton);
			keys[i + count].setPopupBackground(_popupButton);
			keys[i + count].setPopupFont(_popupFont);
			keys[i + count].setFont(_font);
		}
		count += pattern[1].length;
		for (int i = 0; i < pattern[2].length; i++) {
			if (i == pattern[2].length - 1) {
				// delete button
				keys[i + count] = new KeyButton(pattern[2][i], this._width - 1.5f * w - px2, _pos.y + _height - paddingY - h - 2 * (h + py), 1.5f * w, sh, this);
				keys[i + count].setBackground(_specialButton);
				keys[i + count].setPressedBackground(_pressedSpecialButton);
				keys[i + count].setFont(_font);
				keys[i + count].setIcon(_deleteButton);
				keys[i + count].setIconScale(_iconScale);
			} else {
				keys[i + count] = new KeyButton(pattern[2][i], px2 + i * (w + px), _pos.y + _height - paddingY - h - 2 * (h + py), w, h, this);
				keys[i + count].setBackground(_normalButton);
				keys[i + count].setPressedBackground(_pressedNormalButton);
				keys[i + count].setPopupBackground(_popupButton);
				keys[i + count].setPopupFont(_popupFont);
				keys[i + count].setFont(_font);
			}
		}
		count += pattern[2].length;
		float tempX = px3;
		for (int i = 0; i < pattern[3].length; i++) {
			if (i == 0) {
				// close button
				keys[i + count] = new KeyButton(pattern[3][i], paddingX, _pos.y + _height - paddingY - h - 3 * (h + py), Math.min(sw / 2 + px3 / 2, 1.2f * sw),
						sh, this);
				keys[i + count].setBackground(_specialButton);
				keys[i + count].setPressedBackground(_pressedSpecialButton);
				keys[i + count].setFont(_font);
				keys[i + count].setIcon(_closeButton);
				// _defaultQwerty[i + count].setIconScale(iconScale);
			} else if (i == 1 || i == pattern[3].length - 2) {
				// mode button
				keys[i + count] = new KeyButton(pattern[3][i], tempX, _pos.y + _height - paddingY - h - 3 * (h + py), w, h, this);
				keys[i + count].setBackground(_specialButton);
				keys[i + count].setPressedBackground(_pressedSpecialButton);
				keys[i + count].setFont(_modeFont);
				tempX += w + px;
			} else if (i == 3) {
				// space button
				keys[i + count] = new KeyButton(pattern[3][i], px3 + 2 * (w + px), _pos.y + _height - paddingY - h - 3 * (h + py), 3 * w + 2 * px, h, this);
				keys[i + count].setBackground(_normalButton);
				keys[i + count].setPressedBackground(_pressedSpecialButton);
				keys[i + count].setFont(_font);
				tempX += 3 * w + 3 * px;
			} else if (i == pattern[3].length - 1) {
				// enter button
				_enterButtonNumberIndex = i + count;
				keys[i + count] = new KeyButton(pattern[3][i], this._width - Math.min(sw / 2 + px3 / 2, 1.2f * sw) - paddingX, _pos.y + _height - paddingY - h
						- 3 * (h + py), Math.min(sw / 2 + px3 / 2, 1.2f * sw), sh, this);
				keys[i + count].setBackground(_specialButton);
				keys[i + count].setPressedBackground(_pressedSpecialButton);
				keys[i + count].setFont(_font);
				keys[i + count].setIcon(_enterButton);
				keys[i + count].setIconScale(_iconScale);
			} else {
				keys[i + count] = new KeyButton(pattern[3][i], tempX, _pos.y + _height - paddingY - h - 3 * (h + py), w, h, this);
				keys[i + count].setBackground(_normalButton);
				keys[i + count].setPressedBackground(_pressedNormalButton);
				keys[i + count].setPopupBackground(_popupButton);
				keys[i + count].setPopupFont(_popupFont);
				keys[i + count].setFont(_font);
				tempX += w + px;
			}
		}
	}

	private void _initDefaultNumbersSymbols1() {
		_defaultNumbersSymbols1 = new KeyButton[35];
		_initDefaultNumbersSymbols(_defaultNumbersSymbols1, DEFAULT_NUMBERS_SYMBOLS_1);
	}

	private void _initDefaultNumbersSymbols2() {
		_defaultNumbersSymbols2 = new KeyButton[35];
		_initDefaultNumbersSymbols(_defaultNumbersSymbols2, DEFAULT_NUMBERS_SYMBOLS_2);
	}

	private int _getCurrentMode() {
		return _currentMode;
	}

	private String _getModeLabel() {
		return _modeLabel[_currentMode % 3];
	}

	private String _getLanguageMode() {
		return _languageMode[_language];
	}

	private void _changeLanguageMode() {
		if (_enableVietKey)
			_language = 1 - _language;
	}

	private void _changeMode(int mode) {
		_currentMode = mode;
		if (_currentMode == 1) {
			_currentKeyBoard = _defaultNumbersSymbols1;
		} else if (_currentMode == 2) {
			_currentKeyBoard = _defaultNumbersSymbols2;
		} else {
			_currentKeyBoard = _defaultQwerty;
		}
	}

	private void _load() {
		_assetManager = new AssetManager();
		_assetManager.load("keyboard/image.pack", TextureAtlas.class);
		if (_size == Size.Large)
			_assetManager.load("keyboard/image_large.pack", TextureAtlas.class);
		_assetManager.load("keyboard/fx_delete.wav", Sound.class);
		_assetManager.load("keyboard/fx_spacebar.wav", Sound.class);
		_assetManager.load("keyboard/fx_standard.wav", Sound.class);

		if (_size == Size.Small) {
			_font = new BitmapFont(Gdx.files.internal("keyboard/font-20.fnt"), false);
			_font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			_font.setColor(Color.BLACK);
		} else if (_size == Size.Medium) {
			_font = new BitmapFont(Gdx.files.internal("keyboard/font-30.fnt"), false);
			_font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			_font.setColor(Color.BLACK);
		} else {
			_font = new BitmapFont(Gdx.files.internal("keyboard/font-45.fnt"), false);
			_font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			_font.setColor(Color.BLACK);
		}

		if (_size == Size.Small) {
			_popupFont = new BitmapFont(Gdx.files.internal("keyboard/popup-font-30.fnt"), false);
			_popupFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			_popupFont.setColor(Color.WHITE);
		} else if (_size == Size.Medium) {
			_popupFont = new BitmapFont(Gdx.files.internal("keyboard/popup-font-50.fnt"), false);
			_popupFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			_popupFont.setColor(Color.WHITE);
		} else {
			_popupFont = new BitmapFont(Gdx.files.internal("keyboard/popup-font-60.fnt"), false);
			_popupFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			_popupFont.setColor(Color.WHITE);
		}

		if ((_screenWidth / _screenHeight) > 1) {
			if (_size == Size.Small) {
				_modeFont = new BitmapFont(Gdx.files.internal("keyboard/mode-font-15.fnt"), false);
			} else if (_size == Size.Medium) {
				_modeFont = new BitmapFont(Gdx.files.internal("keyboard/mode-font-26.fnt"), false);
			} else {
				_modeFont = new BitmapFont(Gdx.files.internal("keyboard/mode-font-40.fnt"), false);
			}
		} else {
			if (_size == Size.Small) {
				_modeFont = new BitmapFont(Gdx.files.internal("keyboard/mode-font-10.fnt"), false);
			} else if (_size == Size.Medium) {
				_modeFont = new BitmapFont(Gdx.files.internal("keyboard/mode-font-18.fnt"), false);
			} else {
				_modeFont = new BitmapFont(Gdx.files.internal("keyboard/mode-font-35.fnt"), false);
			}
		}
		_modeFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		_modeFont.setColor(new Color(0.376f, 0.376f, 0.376f, 1));
	}

	private void _unload() {
		_font = null;
		_modeFont = null;
		_popupFont = null;
		_normalButton = null;
		_pressedNormalButton = null;
		_specialButton = null;
		_pressedSpecialButton = null;
		_popupButton = null;
		_closeButton = null;
		_deleteButton = null;
		_enterButton = null;
		_acShiftButton = null;
		_inShiftButton = null;
		_deleteFX = null;
		_spaceFX = null;
		_standardFX = null;
		_atlas.dispose();

		_assetManager.unload("keyboard/image.pack");
		_assetManager.unload("keyboard/fx_delete.wav");
		_assetManager.unload("keyboard/fx_spacebar.wav");
		_assetManager.unload("keyboard/fx_standard.wav");
	}

	private void _init() {
		_atlas = _assetManager.get("keyboard/image.pack", TextureAtlas.class);
		if (_size == Size.Large) {
			_largeAtlas = _assetManager.get("keyboard/image_large.pack", TextureAtlas.class);
		}

		_background = _atlas.findRegion("background");
		if (_size == Size.Large)
			_closeButton = _largeAtlas.findRegion("default_icon_arrow_down");
		else
			_closeButton = _atlas.findRegion("default_icon_arrow_down");
		if (_size == Size.Large)
			_deleteButton = _largeAtlas.findRegion("default_icon_delete");
		else
			_deleteButton = _atlas.findRegion("default_icon_delete");
		if (_size == Size.Large)
			_enterButton = _largeAtlas.findRegion("default_icon_enter");
		else
			_enterButton = _atlas.findRegion("default_icon_enter");
		if (_size == Size.Large)
			_inShiftButton = _largeAtlas.findRegion("default_icon_shift_inactive");
		else
			_inShiftButton = _atlas.findRegion("default_icon_shift_inactive");
		if (_size == Size.Large)
			_acShiftButton = _largeAtlas.findRegion("default_icon_shift_active");
		else
			_acShiftButton = _atlas.findRegion("default_icon_shift_active");
		if (_size == Size.Large)
			_okButton = _largeAtlas.findRegion("default_icon_go");
		else
			_okButton = _atlas.findRegion("default_icon_go");

		_background.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		_closeButton.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		_deleteButton.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		_enterButton.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		_inShiftButton.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		_acShiftButton.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		_okButton.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		_deleteFX = _assetManager.get("keyboard/fx_delete.wav", Sound.class);
		_spaceFX = _assetManager.get("keyboard/fx_spacebar.wav", Sound.class);
		_standardFX = _assetManager.get("keyboard/fx_standard.wav", Sound.class);

		_normalButton = _atlas.createPatch("default_btn_keyboard_key_normal");
		_pressedNormalButton = _atlas.createPatch("default_btn_special_key_normal");
		_specialButton = _atlas.createPatch("default_btn_keyboard_key_pressed");
		_pressedSpecialButton = _atlas.createPatch("default_btn_special_key_pressed");
		_popupButton = _atlas.createPatch("default_btn_popup_key");

		_editText = new EditText(0, 0, "", _font, _camera, _viewport);
		_editText.setBackground(_atlas.createPatch("edit_text"), _width - 4, _font.getCapHeight() * 2);

		_pos.x = 0;
		_pos.y = -(_screenHeight + 5 + _editText.getHeight());
		_editText.setX(_pos.x + 2);
		_editText.setY(_pos.y + _height);

		_iconScale = _font.getCapHeight() / Math.abs(_deleteButton.getRegionHeight());

		_initDefaultQwerty();
		_initDefaultNumbersSymbols1();
		_initDefaultNumbersSymbols2();

		_currentKeyBoard = _defaultQwerty;
	}

	public void reset() {
		_editText.reset();
		_textField = null;
		_textBuffer.clear();
	}

	public void dispose() {
		_unload();
		_assetManager.dispose();
		_assetManager = null;
	}

	public void setMode(int mode) {
		if (_type == KeyboardConfig.NUMBER)
			mode = KeyboardConfig.SINGLE_LINE;
		_mode = mode;
		if (_mode == KeyboardConfig.MULTI_LINE) {
			_defaultQwerty[_enterButtonDefaultIndex].setIcon(_enterButton);
			_defaultNumbersSymbols1[_enterButtonNumberIndex].setIcon(_enterButton);
			_defaultNumbersSymbols2[_enterButtonNumberIndex].setIcon(_enterButton);
			_editText.setMode(KeyboardConfig.MULTI_LINE);
			_editText.setAutoResize(true);
		} else {
			_defaultQwerty[_enterButtonDefaultIndex].setIcon(_okButton);
			_defaultNumbersSymbols1[_enterButtonNumberIndex].setIcon(_okButton);
			_defaultNumbersSymbols2[_enterButtonNumberIndex].setIcon(_okButton);
			_editText.setMode(KeyboardConfig.SINGLE_LINE);
			_editText.setAutoResize(false);
		}
	}

	public void setType(int type) {
		_type = type;
		_editText.setType(type);
		if (_type == KeyboardConfig.NUMBER) {
			_mode = KeyboardConfig.SINGLE_LINE;
			_changeMode(1);
		}
	}

	public float getWidth() {
		return this._width;
	}

	public float getHeight() {
		return this._height;
	}

	public void enableVietKey() {
		_enableVietKey = true;
	}

	public void disableVietKey() {
		_enableVietKey = false;
	}

	public void hide() {
		_begin = _pos.y;
		_change = -(_height + _editText.getHeight() + 5) - _begin;
		_time = 0;
		_animate = true;
		_editText.setActivate(false);
		if (_hideListener != null) {
			_hideListener.hide();
		}
		_hideListener = null;
		_backspaceComma = null;
		_doneListener = null;
	}

	@Override
	public void show(boolean visible) {
		// if (!Gdx.input.isPeripheralAvailable(Peripheral.HardwareKeyboard)) {
		if (visible) {
			_begin = _pos.y;
			_change = -_begin;
			_time = 0;
			_animate = true;
			_editText.setActivate(true);
		} else {
			hide();
		}
		// }
	}

	public boolean isShowing() {
		if (_pos.y > -_height)
			return true;
		else
			return false;
	}

	public int getShift() {
		return _shift;
	}

	public void changeShift() {
		_shift = (_shift + 1) % 3;
	}

	public void resetShift() {
		_shift = 0;
	}

	public void addKeyCode(char character) {
		if (_backspaceComma != null) {
			if (character == ',') {
				return;
			}
		}
		if (character == '\b') {
			if (_backspaceComma != null) {
				if (_textBuffer.cursor > -1) {
					if (_textBuffer.chars.get(_textBuffer.cursor) != ',') {
						Gdx.input.getInputProcessor().keyTyped(character);
						_textBuffer.backspace();
					} else {
						_backspaceComma.backComma();
					}
				}
			} else {
				Gdx.input.getInputProcessor().keyTyped(character);
				_textBuffer.backspace();
			}
			return;
		}
		Gdx.input.getInputProcessor().keyTyped(character);

		if (character == '\r')
			character = '\n';

		// if we get \b, we remove the last inserted character

		if (character == 127) {
			_textBuffer.delete();
			return;
		}

		if (_language == 1 && isAccent(character) && _textBuffer.getCurrentChar() != 32) {
			String[] temp = _textBuffer.toString().split("\n");
			temp = temp[temp.length - 1].split(" ");
			String currentWord = temp[temp.length - 1];
			if (currentWord.length() > 0) {
				char accent = _telex.getAccentMark(character, '\0', currentWord);
				if (accent >= '0' && accent <= '9') {
					String vietWord = VietKeyInput.toVietWord(currentWord, accent);
					if (!vietWord.equals(currentWord)) {
						for (int i = 0; i < currentWord.length(); i++) {
							_textBuffer.backspace();
						}
						for (int i = 0; i < vietWord.length(); i++) {
							_textBuffer.add(vietWord.charAt(i));
						}
						return;
					}
				}
			}
		}

		// else we just insert the character
		_textBuffer.add(character);

	}

	public boolean isAccent(char character) {
		if (character == 'S' || character == 's' || character == 'F' || character == 'f' || character == 'R' || character == 'r' || character == 'X'
				|| character == 'x' || character == 'J' || character == 'j' || character == 'A' || character == 'a' || character == 'E' || character == 'e'
				|| character == 'O' || character == 'o' || character == 'W' || character == 'w' || character == 'D' || character == 'd' || character == 'Z'
				|| character == 'z')
			return true;
		else
			return false;
	}

	public void update(float deltaT) {
		if (_state == State.Loading) {
			if (_assetManager.update()) {
				_assetManager.finishLoading();
				_init();
				_state = State.Running;
			}
			return;
		}

		if (_animate) {
			float delta;
			float y;
			_time += deltaT;
			if (_time > _duration)
				_time = _duration;
			delta = _easeLinear(_time, _begin, _change, _duration) - _pos.y;
			_pos.y += delta;
			for (KeyButton button : _defaultNumbersSymbols1) {
				y = button.getY();
				button.setY(y + delta);
			}
			for (KeyButton button : _defaultNumbersSymbols2) {
				y = button.getY();
				button.setY(y + delta);
			}
			for (KeyButton button : _defaultQwerty) {
				y = button.getY();
				button.setY(y + delta);
			}
			y = _editText.getY();
			_editText.setY(y + delta);
			if (_time == _duration)
				_animate = false;
		}
		_editText.update(deltaT);
	}

	public void draw() {
		if (_state == State.Running) {
			_batch.begin();
			_batch.flush();
			Gdx.gl.glViewport((int) _viewport.x, (int) _viewport.y, (int) _viewport.width, (int) _viewport.height);
			_camera.update();
			_batch.setProjectionMatrix(_camera.combined);
			if (isShowing()) {
				_batch.setColor(1, 1, 1, 1);
				_batch.draw(_background, _pos.x, _pos.y, _width, _height);
				// _editText.draw(_batch);

				for (KeyButton button : _currentKeyBoard) {
					if (button != null) {
						button.render(_batch);

						if (button.getName().equals("$Delete$") && button.isLongPressed())
							addKeyCode('\b');
					}
				}
			}
			_batch.flush();
			_batch.end();
		}
	}

	public void registerTextField(TextField textField, int type, int mode) {
		setType(type);
		setMode(mode);
		_textField = textField;
		if (_textField.getText() != null) {
			CharArray array = new CharArray(_textField.getText().toCharArray());
			Event event = new Event(Action.EDIT);
			event.data = array;
			event.cursor = array.size - 1;
			_textBuffer.clear();
			_textBuffer.setBuffer(array);
			_textBuffer.setCursor(array.size - 1);
			if (_textField.getMessageText() != null)
				_editText.setHint(_textField.getMessageText());
			else
				_editText.setHint("");
			_editText.dispatch(event);
		}
	}

	public void registerTextField(TextField textField, int type, int mode, OnDoneListener _doneListener, OnHideListener _hideListener) {
		setType(type);
		setMode(mode);
		setDoneListener(_doneListener);
		setHideListener(_hideListener);
		_textField = textField;
		if (_textField.getText() != null) {
			CharArray array = new CharArray(_textField.getText().toCharArray());
			Event event = new Event(Action.EDIT);
			event.data = array;
			event.cursor = array.size - 1;
			_textBuffer.clear();
			_textBuffer.setBuffer(array);
			_textBuffer.setCursor(array.size - 1);
			if (_textField.getMessageText() != null)
				_editText.setHint(_textField.getMessageText());
			else
				_editText.setHint("");
			_editText.dispatch(event);
		}
	}

	public void setDoneListener(OnDoneListener listener) {
		_doneListener = listener;
	}

	public void setHideListener(OnHideListener listener) {
		_hideListener = listener;
	}

	public void setBackspaceComma(OnBackSpaceComma listener) {
		_backspaceComma = listener;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (isShowing()) {
			if (keycode == Keys.BACK) {
				if (isShowing()) {
					flush();
					hide();
				}
			}
		}
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
		Vector3 pos = new Vector3(screenX, screenY, 0);
		_camera.unproject(pos, _viewport.x, _viewport.y, _viewport.width, _viewport.height);
		if (isShowing()) {
			if (pos.x >= _pos.x && pos.x <= _pos.x + _width && pos.y >= _pos.y && pos.y <= _pos.y + _height) {
				if (button == 0) {
					for (KeyButton keyButton : _currentKeyBoard) {
						if (keyButton != null)
							keyButton.isPressed(pos.x, pos.y, _viewport.x, _viewport.y, _viewport.width, _viewport.height);
					}
					if (_type != KeyboardConfig.PASSWORD && _editText.contains(pos.x, pos.y, _viewport.x, _viewport.y, _viewport.width, _viewport.height)) {
						Vector2 anchor = _editText.getTextPosition();
						float x = anchor.x + _viewport.x;
						float y = anchor.y + _viewport.y;
						_textBuffer.catchCurrsor(pos.x - x, y - pos.y, 1, 1, _font);
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector3 pos = new Vector3(screenX, screenY, 0);
		_camera.unproject(pos, _viewport.x, _viewport.y, _viewport.width, _viewport.height);
		if (isShowing()) {
			if (pos.x >= _pos.x && pos.x <= _pos.x + _width && pos.y >= _pos.y && pos.y <= _pos.y + _height) {
				for (KeyButton keyButton : _currentKeyBoard) {
					if (keyButton != null)
						keyButton.touchUp();
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector3 pos = new Vector3(screenX, screenY, 0);
		_camera.unproject(pos, _viewport.x, _viewport.y, _viewport.width, _viewport.height);
		if (isShowing())
			if (pos.x >= _pos.x && pos.x <= _pos.x + _width && pos.y >= _pos.y && pos.y <= _pos.y + _height) {
				return true;
			}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (isShowing())
			return true;
		else
			return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (isShowing())
			return true;
		else
			return false;
	}

	public void done() {
		if (_doneListener != null)
			_doneListener.done();
		flush();
		hide();
	}

	public void clear() {
		flush();
		_textBuffer.clear();
		if (_textField != null) {
			_textField.setText("");
		}
	}

	public void flush() {
		if (_editText != null) {
			_editText.reset();
		}
		// _textBuffer.clear();
	}

	private float _easeLinear(double t, float b, float c, double d) {
		return (float) (c * t / d + b);
	}

	private class KeyButton {
		private NinePatch		normalBack;
		private NinePatch		pressedBack;
		private NinePatch		popupBack;
		private TextureRegion	icon;
		private TextureRegion	subIcon;
		private String			name;
		private boolean			isPressed	= false;
		private Vector2			pos;
		private Vector2			dimension;
		private Rectangle		bound;
		private int				keyCode;
		private VirtualKeyboard	parent;
		private BitmapFont		font;
		private BitmapFont		popupFont;
		private float			scale		= 1;
		@SuppressWarnings("unused")
		private String			word		= "";
		private long			timePress;

		public KeyButton(String name, float x, float y, float width, float height, VirtualKeyboard parent) {
			this.name = name;
			pos = new Vector2();
			dimension = new Vector2();
			pos.x = x;
			pos.y = y;
			dimension.x = width;
			dimension.y = height;
			bound = new Rectangle();
			this.parent = parent;
		}

		public String getName() {
			return name;
		}

		public void setBackground(NinePatch patch) {
			normalBack = patch;
		}

		public void setIcon(TextureRegion region) {
			icon = region;
		}

		public void setSubIcon(TextureRegion region) {
			subIcon = region;
		}

		public void setFont(BitmapFont font) {
			this.font = font;
		}

		public void setPopupFont(BitmapFont font) {
			this.popupFont = font;
		}

		public void setPressedBackground(NinePatch patch) {
			pressedBack = patch;
		}

		public void setPopupBackground(NinePatch patch) {
			popupBack = patch;
		}

		public void setIconScale(float scale) {
			this.scale = scale;
		}

		@SuppressWarnings("unused")
		public int getKeyCode() {
			return keyCode;
		}

		@SuppressWarnings("unused")
		public float getX() {
			return pos.x;
		}

		public float getY() {
			return pos.y;
		}

		@SuppressWarnings("unused")
		public void setX(float x) {
			pos.x = x;
		}

		public void setY(float y) {
			pos.y = y;
		}

		public boolean isPressed(float x, float y, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
			bound.x = pos.x + viewportX;
			bound.y = pos.y + viewportY;
			bound.width = dimension.x;
			bound.height = dimension.y;
			if (bound.contains(x, y)) {
				isPressed = true;
				timePress = System.currentTimeMillis();
				if (KeyboardConfig.VIBRATE)
					Gdx.input.vibrate(20);

				if (_textBuffer.length() >= _maxLength && !name.equals("$Delete$"))
					return true;

				if (_type == KeyboardConfig.NUMBER) {
					if (name.length() == 1) {
						if (KeyboardConfig.SOUND)
							_standardFX.play();
						if (name.charAt(0) >= '0' && name.charAt(0) <= '9')
							parent.addKeyCode(name.charAt(0));
					} else {
						if (name.equals("$Delete$")) {
							if (KeyboardConfig.SOUND)
								_deleteFX.play();
							parent.addKeyCode('\b');
						} else if (name.equals("$Close$")) {
							if (KeyboardConfig.SOUND)
								_standardFX.play();
							hide();
						} else if (name.equals("$Enter$")) {
							parent.done();
						} else {
							if (KeyboardConfig.SOUND)
								_standardFX.play();
						}
					}
					return true;
				} else {
					// if (_type != KeyboardConfig.NUMBER || (name.length() == 1
					// && name.charAt(0) >= '0' && name.charAt(0) <= '9')
					// || name.equals("$Delete$") || name.equals("$Enter$")
					// || name.equals("$Close$")) {
					if (name.length() == 1) {
						if (KeyboardConfig.SOUND)
							_standardFX.play();
						parent.addKeyCode(name.charAt(0));
					} else {
						if (!(name.startsWith("$") && name.endsWith("$"))) {
							if (KeyboardConfig.SOUND)
								_standardFX.play();
							for (char ch : name.toCharArray()) {
								parent.addKeyCode(ch);
							}
						} else if (name.equals("$Space$")) {
							if (KeyboardConfig.SOUND)
								_spaceFX.play();
							parent.addKeyCode((char) 32);
						} else if (name.equals("$Delete$")) {
							if (KeyboardConfig.SOUND)
								_deleteFX.play();
							parent.addKeyCode('\b');
						} else if (name.equals("$Enter$")) {
							if (KeyboardConfig.SOUND)
								_standardFX.play();
							if (_mode == KeyboardConfig.MULTI_LINE) {
								if (_textBuffer.getNumberLine() < _maxLine)
									parent.addKeyCode('\n');
							} else {
								parent.done();
							}
						} else if (name.equals("$Shift$")) {
							if (KeyboardConfig.SOUND)
								_standardFX.play();
							parent.changeShift();
							return true;
						} else if (name.equals("$Language$")) {
							if (KeyboardConfig.SOUND)
								_standardFX.play();
							parent._changeLanguageMode();
						} else if (name.equals("$Mode$")) {
							if (KeyboardConfig.SOUND)
								_standardFX.play();
							parent._changeMode(1);
						} else if (name.equals("$Mode1$")) {
							if (KeyboardConfig.SOUND)
								_standardFX.play();
							int current = parent._getCurrentMode();
							parent._changeMode(current % 2 + 1);
						} else if (name.equals("$Mode2$")) {
							if (KeyboardConfig.SOUND)
								_standardFX.play();
							parent._changeMode(0);
						} else if (name.equals("$Close$")) {
							if (KeyboardConfig.SOUND)
								_standardFX.play();
							hide();
						}
					}
					if (parent.getShift() == 1) {
						parent.resetShift();
					}
				}

				return true;
			} else {
				return false;
			}
		}

		public void touchUp() {
			isPressed = false;
			timePress = 0;
		}

		public boolean isLongPressed() {
			if (timePress == 0) {
				return false;
			} else {
				if (System.currentTimeMillis() - timePress > 100) {
					timePress = System.currentTimeMillis();
					return true;
				}
			}
			return false;
		}

		public void render(SpriteBatch batch) {
			if (isPressed) {
				pressedBack.draw(batch, pos.x, pos.y, dimension.x, dimension.y);
				if (popupBack != null) {
					popupBack.draw(batch, ((pos.x - 1.5f * dimension.x / 3 < 0) ? 0 : (pos.x - 1.5f * dimension.x / 3)), pos.y + dimension.y,
							1.5f * dimension.x, 1.5f * dimension.y);
				}
			} else {
				normalBack.draw(batch, pos.x, pos.y, dimension.x, dimension.y);
			}

			if (!(name.length() > 1 && name.startsWith("$") && name.endsWith("$"))) {
				TextBounds bound = font.getBounds(name);
				if (parent.getShift() > 0) {
					name = name.toUpperCase();
				} else {
					name = name.toLowerCase();
				}
				font.draw(batch, name, pos.x + (dimension.x - bound.width) / 2, pos.y + (dimension.y + bound.height) / 2);
				if (isPressed && popupBack != null) {
					bound = popupFont.getBounds(name);
					popupFont.draw(batch, name, ((pos.x - 1.5f * dimension.x / 3 < 0) ? 0 : (pos.x - 1.5f * dimension.x / 3))
							+ (1.5f * dimension.x - bound.width) / 2, pos.y + dimension.y + (1.5f * dimension.y + bound.height) / 2);
				}
			} else {
				if (icon != null) {
					batch.draw(icon, pos.x + (dimension.x - Math.abs(icon.getRegionWidth() * scale)) / 2,
							pos.y + (dimension.y - Math.abs(icon.getRegionHeight() * scale)) / 2, Math.abs(icon.getRegionWidth()) * scale,
							Math.abs(icon.getRegionHeight()) * scale);
					if (name.equals("$Shift$") && parent.getShift() == 2) {
						batch.draw(subIcon, pos.x + (dimension.x - Math.abs(subIcon.getRegionWidth() * scale)) / 2,
								pos.y + (dimension.y - Math.abs(subIcon.getRegionHeight() * scale)) / 2, Math.abs(subIcon.getRegionWidth()) * scale,
								Math.abs(subIcon.getRegionHeight()) * scale);
					}
					return;
				}

				if (name.equals("$Language$")) {
					TextBounds bound = font.getBounds(parent._getLanguageMode());
					font.draw(batch, parent._getLanguageMode(), pos.x + (dimension.x - bound.width) / 2, pos.y + (dimension.y + bound.height) / 2);
					return;
				}

				if (name.equals("$Mode$") || name.equals("$Mode1$")) {
					TextBounds bound = font.getBounds(parent._getModeLabel());
					font.draw(batch, parent._getModeLabel(), pos.x + (dimension.x - bound.width) / 2, pos.y + (dimension.y + bound.height) / 2);
					return;
				}

				if (name.equals("$Mode2$")) {
					TextBounds bound = font.getBounds("ABC");
					font.draw(batch, "ABC", pos.x + (dimension.x - bound.width) / 2, pos.y + (dimension.y + bound.height) / 2);
					return;
				}
			}
		}
	}

	protected class SimpleCharSequence implements CharSequence {
		CharArray	chars	= new CharArray();
		int			cursor	= -1;

		public void add(char c) {
			if (_mode == KeyboardConfig.SINGLE_LINE && c == '\n')
				return;
			cursor++;
			if (cursor == -1)
				chars.add(c);
			else
				chars.insert(cursor, c);
			Event event = new Event(Action.EDIT);
			event.data.addAll(chars);
			event.cursor = cursor;
			if (_textField != null) {
				_textField.setText(chars.toString(""));
				_textField.setCursorPosition(cursor + 1);
			}
			_editText.dispatch(event);
		}

		public void backspace() {
			if (chars.size == 0 || cursor < 0) {
				cursor = -1;
				return;
			}

			chars.removeIndex(cursor);
			cursor--;
			Event event = new Event(Action.EDIT);
			event.data.addAll(chars);
			event.cursor = cursor;
			if (_textField != null) {
				_textField.setText(chars.toString(""));
			}
			_editText.dispatch(event);
		}

		public void delete() {
			if (cursor == chars.size - 1)
				return;
			for (int i = cursor + 1; i < chars.size - 1; i++) {
				chars.set(i, chars.get(i + 1));
			}
			chars.removeIndex(chars.size - 1);
			Event event = new Event(Action.EDIT);
			event.data.addAll(chars);
			event.cursor = cursor;
			if (_textField != null) {
				_textField.setText(chars.toString(""));
			}
			_editText.dispatch(event);
		}

		public void setCursor(int c) {
			cursor = c;
		}

		public synchronized void decreaseCursor() {
			if (cursor >= 0) {
				cursor--;
			}
			Event event = new Event(Action.EDIT);
			event.data.addAll(chars);
			event.cursor = cursor;
			if (_textField != null) {
				_textField.setText(chars.toString(""));
			}
			_editText.dispatch(event);
		}

		public synchronized void increaseCursor() {
			if (cursor < chars.size - 1) {
				cursor++;
			}
			Event event = new Event(Action.EDIT);
			event.data.addAll(chars);
			event.cursor = cursor;
			if (_textField != null) {
				_textField.setText(chars.toString(""));
			}
			_editText.dispatch(event);
		}

		public int getNumberLine() {
			int count = 1;
			for (int i = 0; i < chars.size; i++) {
				if (chars.get(i) == '\n')
					count++;
			}
			return count;
		}

		public Vector3 getCursorInfo(float scaleX, float scaleY, BitmapFont font) {
			int count = 1;
			int start = -1;
			for (int i = 0; i <= cursor; i++) {
				if (chars.get(i) == '\n') {
					count++;
					start = i;
				}
			}
			CharSequence temp = subSequence(start + 1, cursor + 1);
			TextBounds bound = font.getBounds(temp);
			return new Vector3(bound.width * scaleX, font.getCapHeight() * scaleY, count);
		}

		public void catchCurrsor(float deltaX, float deltaY, float scaleX, float scaleY, BitmapFont font) {
			if (deltaY < 0)
				return;
			cursor = -1;
			while (cursor < chars.size - 1) {
				Vector3 temp = getCursorInfo(scaleX, scaleX, font);
				float cY = font.getLineHeight() * (temp.z - 1) * scaleY;
				if (deltaY >= cY && deltaY <= cY + temp.y) {
					cursor++;
					while (cursor < chars.size - 1) {
						temp = getCursorInfo(scaleX, scaleY, font);
						if (deltaX <= temp.x) {
							break;
						}
						cursor++;
						if (chars.get(cursor) == '\n') {
							cursor--;
							break;
						}
					}
					break;
				}
				cursor++;
			}
			Event event = new Event(Action.EDIT);
			event.data.addAll(chars);
			event.cursor = cursor;
			if (_textField != null) {
				_textField.setText(chars.toString(""));
			}
			_editText.dispatch(event);
		}

		public void setBuffer(CharArray buffer) {
			clear();
			chars.addAll(buffer);

		}

		public String toString() {
			return chars.toString("");
		}

		public void clear() {
			chars.clear();
			cursor = -1;
		}

		public char getCurrentChar() {
			return charAt(cursor);
		}

		@Override
		public char charAt(int index) {
			if (index == -1)
				return 0;
			else
				return chars.get(index);
		}

		@Override
		public int length() {
			return chars.size;
		}

		@Override
		public CharSequence subSequence(int arg0, int arg1) {
			return toString().subSequence(arg0, arg1);
		}
	}

	public interface OnDoneListener {
		public void done();
	}

	public interface OnBackSpaceComma {
		public void backComma();
	}

	public interface OnHideListener {
		public void hide();
	}

}
