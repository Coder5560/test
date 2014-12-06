package utils.networks;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;

import javax.net.ssl.HttpsURLConnection;

import utils.factory.AppPreference;
import utils.screen.GameCore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.StreamUtils;

/**
 * 
 * @author HungHD
 * 
 */

public class ImageDownloader {
	public static boolean				isCacheSDCard	= true;
	public static boolean				useOneThread	= false;
	private Vector<DummyImage>			queue;
	private WeakHashMap<Image, String>	imageManager;
	private Vector<DownloadedPixmap>	pixQueue;

	private GameCore					game;
	private volatile Thread				thread1, thread2, thread3;
	private static ImageDownloader		INSTANCE;

	public static ImageDownloader getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ImageDownloader();
		}
		return INSTANCE;
	}

	public ImageDownloader() {
		queue = new Vector<DummyImage>();
		imageManager = new WeakHashMap<Image, String>();
		pixQueue = new Vector<DownloadedPixmap>();
	}

	public ImageDownloader(GameCore game) {
		this();
		this.game = game;
	}

	public void setGameCore(GameCore game) {
		this.game = game;
	}

	public void download(String url, Image container) {
		if (url != null && container != null) {
			DummyImage dummy = new DummyImage(url, container);
			queue.add(dummy);
			imageManager.put(dummy.image, dummy.id);
		}
	}

	public void reload() {
		Set<Entry<Image, String>> set = imageManager.entrySet();
		for (Entry<Image, String> e : set) {
			Image image = e.getKey();
			String url = e.getValue();
			download(url, image);
		}
	}

	public void update() {
		if (queue.size() > 0) {
			if (useOneThread) {
				if (thread1 == null || (thread1 != null && !thread1.isAlive())) {
					thread1 = new Thread(new DownloadTask());
					thread1.start();
				}
			} else {
				if (thread1 == null || (thread1 != null && !thread1.isAlive())) {
					thread1 = new Thread(new DownloadTask());
					thread1.start();
				} else if (thread2 == null
						|| (thread2 != null && !thread2.isAlive())) {
					thread2 = new Thread(new DownloadTask());
					thread2.start();
				} else if (thread3 == null
						|| (thread3 != null && !thread3.isAlive())) {
					thread3 = new Thread(new DownloadTask());
					thread3.start();
				}
			}
		}

		if (pixQueue.size() > 0) {
			DownloadedPixmap temp = pixQueue.remove(0);
			Texture texture = new Texture(temp.pixmap);
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			TextureRegion region = new TextureRegion(texture);
			temp.container.image.setDrawable(new TextureRegionDrawable(region));
			region = null;
			texture = null;
			temp.pixmap = null;
			temp.container.image = null;
			temp = null;
		}
	}

	public void clear() {
		thread1 = null;
		thread2 = null;
		thread3 = null;
		queue.clear();
		pixQueue.clear();
	}

	public void dispose() {
		queue.clear();
		pixQueue.clear();
		imageManager.clear();
	}

	class DownloadedPixmap {
		public int			width;
		public int			height;
		public Pixmap		pixmap;
		public DummyImage	container;

		public DownloadedPixmap(Pixmap pixmap, DummyImage container, int width,
				int height) {
			this.pixmap = pixmap;
			this.container = container;
			this.width = width;
			this.height = height;
		}
	}

	class DummyImage {
		public String	id;
		public int		downloadedCount;
		public Image	image;

		public DummyImage(String id, Image image) {
			this.id = id;
			this.image = image;
		}
	}

	Preferences	pref	= AppPreference.instance.getPreferences();

	class DownloadTask implements Runnable {
		Texture	tex		= null;
		boolean	isError	= false;

		private Pixmap download(String url) {
			InputStream in = null;
			String fileName = "";
			try {
				fileName = MD5Good.hash(url);
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			try {
				if (isCacheSDCard) {
					if (pref.getString(fileName, "") != "") {
						long time = Long.parseLong(pref.getString(fileName));
						if (Math.abs(System.currentTimeMillis() - time) < DataCache.TIME_DIE) {
							final String file = fileName;
							isError = false;
							Gdx.app.postRunnable(new Runnable() {
								public void run() {
									try {
										tex = new Texture(Gdx.files
												.external("\\Hichef\\" + file));
									} catch (Exception e) {
										isError = true;
									}
								}
							});
							while (tex == null && !isError) {
								Thread.sleep(10);
							}
							if (!isError) {
								tex.getTextureData().prepare();
								Pixmap pix = tex.getTextureData()
										.consumePixmap();
								System.out
										.println("Load from SD card || Url : "
												+ url);
								return pix;
							}
						}
					}
				}
				if (url.startsWith("https")) {
					HttpsURLConnection conn = null;
					conn = (HttpsURLConnection) new URL(url).openConnection();
					conn.setDoInput(true);
					conn.setDoOutput(false);
					HttpsURLConnection.setFollowRedirects(true);
					conn.setUseCaches(true);
					conn.connect();
					in = conn.getInputStream();
				} else {
					HttpURLConnection conn = null;
					conn = (HttpURLConnection) new URL(url).openConnection();
					conn.setDoInput(true);
					conn.setDoOutput(false);
					HttpURLConnection.setFollowRedirects(true);
					conn.setUseCaches(true);
					conn.connect();
					in = conn.getInputStream();
				}
				Pixmap pixmap = game.flatformResolver.formatBitmap(in);
				if (isCacheSDCard) {
					pref.putString(fileName, System.currentTimeMillis() + "");
					DataCache.saveFile(pixmap, "\\Hichef\\" + fileName);
					pref.flush();
				}
				System.out.println("Download from internet || Url : " + url);
				return pixmap;
			} catch (Exception ex) {
				// ex.printStackTrace();
				return null;
			} finally {
				StreamUtils.closeQuietly(in);
			}
		}

		@Override
		public void run() {
			if (queue.size() > 0
					&& (Thread.currentThread() == thread1
							|| Thread.currentThread() == thread2 || Thread
							.currentThread() == thread3)) {
				DummyImage dummy = queue.remove(0);
				dummy.downloadedCount++;
				if (dummy.id.startsWith("http")) {
					// String url = dummy.id.replaceFirst("https",
					// "http").replaceAll(" ", "%20");
					String url = dummy.id.replaceAll(" ", "%20");
					Pixmap pixmap = download(url);
					if (pixmap != null) {
						int width = pixmap.getWidth();
						int height = pixmap.getHeight();
						Pixmap potPixmap = new Pixmap(width, height,
								pixmap.getFormat());
						potPixmap.drawPixmap(pixmap, 0, 0, 0, 0,
								pixmap.getWidth(), pixmap.getHeight());
						DownloadedPixmap downloadedPixmap = new DownloadedPixmap(
								potPixmap, dummy, width, height);
						pixQueue.add(downloadedPixmap);
						pixmap.dispose();
					} else if (dummy.downloadedCount <= 3) {
						queue.add(dummy);
					}
				}
			}
		}
	}
}
