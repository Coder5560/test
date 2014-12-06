package utils.networks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class Request {

	private static Request	INSTANCE;

	public HttpRequest		lastestHttpRequest;

	float					timeout	= 0;

	private Request() {
	}

	public static Request getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Request();
		}
		return INSTANCE;
	}

	public void getFoodList(int filter_type, int filter_cate,
			HttpResponseListener listener) {
		get(CommandRequest.FOOD_GET_LIST,
				ParamsBuilder.builder()
						.add(ExtParamsKey.FILTER_TYPE, filter_type)
						.add(ExtParamsKey.FILTER_CATE, filter_cate).build(),
				listener);
	}

	public void getFoodList(int filter_type, int filter_cate, int start,
			int length, HttpResponseListener listener) {
		get(CommandRequest.FOOD_GET_LIST,
				ParamsBuilder.builder()
						.add(ExtParamsKey.FILTER_TYPE, filter_type)
						.add(ExtParamsKey.START_INDEX, start)
						.add(ExtParamsKey.COUNT, length)
						.add(ExtParamsKey.FILTER_CATE, filter_cate).build(),
				listener);
	}

	/**
	 * Sử dụng HttpRequest và HttpResponse của Libgdx Method GET
	 */
	private void get(String cmd, String params,
			HttpResponseListener httpResponseListener) {
//		if (UserInfo.getInstance().userLogged()) {
//			params = ParamsBuilder.builder().parseParams(params)
//					.add(ExtParamsKey.USER, UserInfo.getInstance().getUserId())
//					.build();
//		}
		params = params.replace(" ", "%20");
		String serviceUrl = ConnectionConfig.MAIN_URL + "/"
				+ ConnectionConfig.serviceName + "/" + cmd + "?" + params;
		HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.GET);
		httpRequest.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		httpRequest.setUrl(serviceUrl);
		lastestHttpRequest = httpRequest;
		Gdx.net.sendHttpRequest(httpRequest, httpResponseListener);
	}

	/**
	 * Sử dụng HttpRequest và HttpResponse của Libgdx Method POST
	 */
	public void post(String cmd, String params,
			HttpResponseListener httpResponseListener) {
//		if (UserInfo.getInstance().userLogged()) {
//			params = ParamsBuilder.builder().parseParams(params)
//					.add(ExtParamsKey.USER, UserInfo.getInstance().getUserId())
//					.build();
//		}
		String serviceUrl = ConnectionConfig.MAIN_URL + "/"
				+ ConnectionConfig.serviceName + "/" + cmd;
		HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.POST);
		httpRequest.setUrl(serviceUrl);
		httpRequest.setContent(params);
		httpRequest.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		lastestHttpRequest = httpRequest;
		Gdx.net.sendHttpRequest(httpRequest, httpResponseListener);
	}

	public void search(String key, HttpResponseListener listener) {
		post(CommandRequest.FOOD_SEARCH,
				ParamsBuilder.builder().add(ExtParamsKey.CONTENT, key).build(),
				listener);
	}

	public void getFoodSearchFull(String content, HttpResponseListener listener) {
		post(CommandRequest.FOOD_SEARCH_FULL,
				ParamsBuilder.builder().add(ExtParamsKey.CONTENT, content)
						.build(), listener);
	}

	public void getFoodDetail(int id, HttpResponseListener listener) {
		get(CommandRequest.FOOD_DETAIL,
				ParamsBuilder.builder().add(ExtParamsKey.ID, id).build(),
				listener);
	}

	public void requestLike(int id, HttpResponseListener listener) {
		get(CommandRequest.FOOD_LIKE,
				ParamsBuilder.builder().add(ExtParamsKey.ID, id)
						.add(ExtParamsKey.VALUE, 1).build(), listener);
	}

	public void getCategoryList(int categoryId, HttpResponseListener listener) {
		get(CommandRequest.FOOD_GET_CATEGORY,
				ParamsBuilder.builder()
						.add(ExtParamsKey.CATEGORY_ID, categoryId).build(),
				listener);
	}

	public void getAllCategory(HttpResponseListener listener) {
		get(CommandRequest.FOOD_GET_ALL_CATEGORY, ParamsBuilder.builder()
				.build(), listener);
	}

	public void userFaceBookLogin(String facebookId, String title,
			String avatar, int gender, String email, String phone,
			String deviceID, String deviceName, int version,
			HttpResponseListener listener) {
		post(CommandRequest.USER_LOGIN_FB,
				ParamsBuilder.builder()
						.add(ExtParamsKey.FACEBOOK_ID, facebookId)
						.add(ExtParamsKey.TITLE, title)
						.add(ExtParamsKey.AVATAR, avatar.replaceAll("&", "|"))
						.add(ExtParamsKey.GENDER, gender)
						.add(ExtParamsKey.EMAIL, email)
						.add(ExtParamsKey.PHONE, phone)
						.add(ExtParamsKey.DEVICES_ID, deviceID)
						.add(ExtParamsKey.DEVICES_NAME, deviceName)
						.add(ExtParamsKey.VERSION, version).build(), listener);
	}

	public void getAllMaterial(HttpResponseListener listener) {
		get(CommandRequest.MATERIAL_GET_ALL, ParamsBuilder.builder().build(),
				listener);
	}

	public void searchAdvance(String key, int cate, int categoryId,
			String include, String exclude, HttpResponseListener listener) {
		ParamsBuilder paramBuiler = ParamsBuilder.builder();
		if (key != null && !key.equalsIgnoreCase("")) {
			paramBuiler.add(ExtParamsKey.CONTENT, key);
		}
		if (cate != -1) {
			paramBuiler.add(ExtParamsKey.FILTER_CATE, cate);
		}
		if (categoryId != -1) {
			paramBuiler.add(ExtParamsKey.CATEGORY_ID, categoryId);
		}
		if (!include.equalsIgnoreCase("")) {
			paramBuiler.add(ExtParamsKey.MATE_LIST_INCLUDE, include);
		}
		if (!exclude.equalsIgnoreCase("")) {
			paramBuiler.add(ExtParamsKey.MATE_LIST_EXCLUDE, exclude);
		}
		post(CommandRequest.FOOD_SEARCH_ADVANCE, paramBuiler.build(), listener);
	}

	public void loadConfig() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				String response = DefaultHttpConnection.get(
						ConnectionConfig.CONFIG, "");
				JsonValue params = (new JsonReader()).parse(response);
				if (params.has("server_primary_host")) {
					String host = params.getString("server_primary_host");
					if (!host.startsWith("http")) {
						host = "http://" + host;
					}
					ConnectionConfig.MAIN_URL = host;
				}
				if (params.has("zone")) {
					ConnectionConfig.serviceName = params.getString("zone");
				}
			}
		});
		thread.start();
	}

	public void requestProfile(HttpResponseListener listener) {
		get(CommandRequest.USER_GET_PROFILE, ParamsBuilder.builder().build(),
				listener);
	}

	public void searchMaterial(String key, HttpResponseListener listener) {
		if (key.equalsIgnoreCase("")) {
			return;
		}
		post(CommandRequest.MATERIAL_SEARCH,
				ParamsBuilder.builder().add(ExtParamsKey.CONTENT, key).build(),
				listener);
	}

	public void updateUserInfo(String title, String birthday, String phone,
			int gender, String content, String email,
			HttpResponseListener listener) {
		get(CommandRequest.USER_UPDATE_INFO,
				ParamsBuilder.builder().add(ExtParamsKey.TITLE, title)
						.add(ExtParamsKey.BIRTH_DAY, birthday)
						.add(ExtParamsKey.PHONE, phone)
						.add(ExtParamsKey.GENDER, gender)
						.add(ExtParamsKey.CONTENT, content)
						.add(ExtParamsKey.EMAIL, email).build(), listener);
	}

}
