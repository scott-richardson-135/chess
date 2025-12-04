package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.requests.CreateRequest;
import model.requests.JoinBody;
import model.requests.LoginRequest;
import model.results.CreateResult;
import model.results.ListResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthData register(UserData user) throws ResponseException {
        var request = buildRequest("POST", "/user", user, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(LoginRequest reqBody) throws ResponseException {
        var request = buildRequest("POST", "/session", reqBody, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public Collection<GameData> list(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        ListResult result =  handleResponse(response, ListResult.class);
        return result.games();
    }

    public int create(String authToken, String gameName) throws ResponseException {
        CreateRequest reqBody = new CreateRequest(authToken, gameName);

        var request = buildRequest("POST", "/game", reqBody, authToken);
        var response = sendRequest(request);
        CreateResult result =  handleResponse(response, CreateResult.class);
        return result.gameID();
    }

    public void join(String authToken, int gameId, String playerColor) throws ResponseException {
        JoinBody reqBody = new JoinBody(playerColor, gameId);

        var request = buildRequest("PUT", "/game", reqBody, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }



    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));

        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }

        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        }
        else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!(status / 100 == 2)) { //if not success code
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }
            throw ResponseException.fromHttpStatusCode(status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    public String getServerUrl() {
        return serverUrl;
    }

}
