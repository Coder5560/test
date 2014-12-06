package utils.factory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.coder5560.game.enums.Direct;

public class Factory {

	public static int getNext(int curentValue, int min, int max) {
		curentValue = clamp(curentValue, min, max);
		if (curentValue == max)
			return min;

		return (curentValue + 1);
	}

	public static int getPrevious(int curentValue, int min, int max) {
		curentValue = clamp(curentValue, min, max);
		if (curentValue == min)
			return max;

		return (curentValue - 1);
	}

	public static int clamp(int curentValue, int min, int max) {
		if (curentValue <= min)
			curentValue = min;
		if (curentValue >= max)
			curentValue = max;
		return curentValue;
	}

	public static Vector2 getPosition(Rectangle bounds, Direct direct) {
		switch (direct) {
			case TOP_LEFT:
				return new Vector2(bounds.x, bounds.y + bounds.height);
			case TOP_RIGHT:
				return new Vector2(bounds.x + bounds.width, bounds.y
						+ bounds.height);
			case TOP:
				return new Vector2(bounds.x + bounds.width / 2, bounds.y
						+ bounds.height);
			case BOTTOM:
				return new Vector2(bounds.x + bounds.width / 2, bounds.y);
			case BOTTOM_LEFT:
				return new Vector2(bounds.x, bounds.y);
			case BOTTOM_RIGHT:
				return new Vector2(bounds.x + bounds.width, bounds.y);
			case MIDDLE:
				return new Vector2(bounds.x + bounds.width / 2, bounds.y
						+ bounds.height / 2);
			case MIDDLE_LEFT:
				return new Vector2(bounds.x, bounds.y + bounds.height / 2);
			case MIDDLE_RIGHT:
				return new Vector2(bounds.x + bounds.width, bounds.y
						+ bounds.height / 2);
			default:
				return new Vector2();
		}
	}

	public static TextureRegion[] getArrayTextureRegion(
			TextureRegion textureRegion, int FRAME_COLS, int FRAME_ROWS) {
		float width = textureRegion.getRegionWidth() / FRAME_COLS;
		float height = textureRegion.getRegionHeight() / FRAME_ROWS;

		TextureRegion[] textureRegions = new TextureRegion[FRAME_COLS
				* FRAME_ROWS];
		TextureRegion[][] temp = textureRegion.split((int) width, (int) height);
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				textureRegions[index++] = temp[i][j];
			}
		}
		return textureRegions;
	}

	// ====================Common Checker=====================
	public static boolean validEmail(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

	public static boolean validDate(String dateToValidate, String dateFromat) {
		if (dateToValidate == null) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		sdf.setLenient(false);
		try {
			// if not valid, it will throw ParseException
			@SuppressWarnings("unused")
			Date date = sdf.parse(dateToValidate);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean isNumeric(String str) {
		return str.matches("\\d+");
	}

	public static boolean validPhone(String phoneNumber) {
		if (!(phoneNumber.startsWith("0") || phoneNumber.startsWith("84") || phoneNumber
				.startsWith("+84")))
			return false;
		String phone = "";
		if (phoneNumber.startsWith("+84"))
			phone = phoneNumber.substring(3);
		if (phoneNumber.startsWith("0"))
			phone = phoneNumber.substring(1);
		if (phoneNumber.startsWith("84"))
			phone = phoneNumber.substring(2);

		System.out.println("Phone : " + phone);
		if (phone.length() == 9 || phone.length() == 10) {
			return isNumeric(phone);
		}
		return false;

	}
}
