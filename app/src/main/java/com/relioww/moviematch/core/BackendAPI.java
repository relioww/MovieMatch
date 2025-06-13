package com.relioww.moviematch.core;

import android.util.Log;

import com.relioww.moviematch.callbacks.JSONCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BackendAPI {
    private final String TAG = "BackendAPI";

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build();

    private final String baseUrl = "http://85.209.2.120:8899";
    private String accessToken;

    public BackendAPI () {}

    public BackendAPI(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void search(String query, JSONCallback callback) {
        String url = baseUrl +
                "/films/search?query=" + query;
        getRequest(url, callback);
    }

    public void getFilm(int id, JSONCallback callback) {
        String url = baseUrl + "/films/" + id;
        getRequest(url, callback);
    }

    public void register(String username, String password,
                         JSONCallback callback) {
        JSONObject data = new JSONObject();
        try {
            data.put("username", username);
            data.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return;
        }

        String url = baseUrl + "/auth/register";
        postRequest(url, data, callback);

    }

    public void login(String username, String password,
                         JSONCallback callback) {
        JSONObject data = new JSONObject();
        try {
            data.put("username", username);
            data.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return;
        }
        String url = baseUrl + "/auth/login";
        postRequest(url, data, callback);

    }

    public void getMe(JSONCallback callback) {
        String url = baseUrl + "/auth/getme";
        getRequest(url, callback);
    }

    public void getFriends(JSONCallback callback) {
        String url = baseUrl + "/friends/get_friends";
        getRequest(url, callback);
    }

    public void deleteFriend(int friendRequestId,
                             JSONCallback callback) {
        String url = baseUrl + "/friends/delete_friend";
        JSONObject data = new JSONObject();
        try {
            data.put("friend_request_id", friendRequestId);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return;
        }
        deleteRequest(url, data, callback);
    }

    public void sendFriendRequest(String username, JSONCallback callback) {
        JSONObject data = new JSONObject();
        try {
            data.put("username", username);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return;
        }
        String url = baseUrl + "/friends/send_friend_request";
        postRequest(url, data, callback);
    }

    public void getFriendRequests(JSONCallback callback) {
        String url = baseUrl + "/friends/get_friend_requests";
        getRequest(url, callback);
    }

    public void acceptFriendRequest(int friendRequestId,
                                    JSONCallback callback) {
        String url = baseUrl + "/friends/accept_friend_request";
        JSONObject data = new JSONObject();
        try {
            data.put("friend_request_id", friendRequestId);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return;
        }
        patchRequest(url, data, callback);
    }

    public void leaveReview(int kinopoiskId, float rating,
                            String commentary, JSONCallback callback) {
        String url = baseUrl + "/reviews/leave_review";
        JSONObject data = new JSONObject();
        try {
            data.put("kinopoisk_id", kinopoiskId);
            data.put("rating", rating);
            data.put("commentary", commentary);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return;
        }
        postRequest(url, data, callback);
    }

    public void getFavorites(JSONCallback callback) {
        String url = baseUrl + "/favorites/get_favorites";
        getRequest(url, callback);
    }

    public void addToFavorites(int filmId, JSONCallback callback) {
        String url = baseUrl + "/favorites/add_to_favorites";
        JSONObject data = new JSONObject();
        try {
            data.put("film_id", filmId);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return;
        }
        postRequest(url, data, callback);
    }

    public void removeFromFavorites(int filmId, JSONCallback callback) {
        String url = baseUrl + "/favorites/remove_from_favorites";
        JSONObject data = new JSONObject();
        try {
            data.put("film_id", filmId);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return;
        }
        deleteRequest(url, data, callback);
    }

    public void pickFilms(ArrayList<Integer> usersList,
                          String commentary, JSONCallback callback) {
        String url = baseUrl + "/picker/pick_films";
        JSONObject data = new JSONObject();
        JSONArray usersJSONArray = new JSONArray(usersList);
        try {
            data.put("users_list", usersJSONArray);
            data.put("commentary", commentary);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return;
        }
        postRequest(url, data, callback);
    }

    private void newCall(Request request, JSONCallback callback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        callback.onFailure(
                                new IOException("Ошибка при выполнении http запроса"));
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(responseBody.string());
                        callback.onSuccess(json);
                    }  catch (JSONException e) {
                        callback.onFailure(
                                new IOException("Ошибка парсинга JSON", e));
                    }
                }
            }
        });
    }

    private void getRequest(String url, JSONCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        newCall(request, callback);
    }

    private void postRequest(String url, JSONObject data, JSONCallback callback) {
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonType, data.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer " + accessToken)
                .post(body)
                .build();
        newCall(request, callback);
    }

    private void deleteRequest(String url, JSONObject data, JSONCallback callback) {
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonType, data.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer " + accessToken)
                .delete(body)
                .build();
        newCall(request, callback);
    }

    private void patchRequest(String url, JSONObject data, JSONCallback callback) {
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonType, data.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer " + accessToken)
                .patch(body)
                .build();
        newCall(request, callback);
    }
}