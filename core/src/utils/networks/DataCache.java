package utils.networks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;

public class DataCache {
	public static int SIZE_CACHE = 1000;
	public static long TIME_DIE = 1 * 24 * 3600000;

	// public static long TIME_DIE = 6000;

	public DataCache() {

	}

	public static void saveFile(Texture tex, String fileName) {
		Pixmap pixmap = tex.getTextureData().consumePixmap();
		saveScreenshot(pixmap, fileName);
	}

	public static void saveFile(Pixmap pixmap, String fileName) {
		saveScreenshot(pixmap, fileName);
	}

	public static void saveScreenshot(Pixmap pixmap, String fileName) {
		try {
			FileHandle fh;
			fh = Gdx.files.external(fileName);
			PixmapIO.writePNG(fh, pixmap);
			// pixmap.dispose();
		} catch (Exception e) {
			System.out.println("Error save file " + e.toString());
		}
	}

	public static Pixmap getPixmapFromByte(String fileName) {
		Preferences prefs = Gdx.app.getPreferences("HichefData" + fileName);
		byte[] data = new byte[prefs.getInteger("length")];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) prefs.getInteger(i + "");
		}
		return new Pixmap(data, 0, prefs.getInteger("length"));
	}

	public static void deleteFile(String filename) {
		try {
			FileHandle fh;
			fh = Gdx.files.external(filename);
			if (fh.exists()) {
				fh.delete();
			}
		} catch (Exception e) {
			System.out.println("Error delete file: " + e.toString());
		}
	}
}
