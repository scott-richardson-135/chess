package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.requests.*;
import model.results.*;
import org.jetbrains.annotations.NotNull;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import java.util.Map;


public class Handlers {
    private static final Gson SERIALIZER = new Gson();
    private static final UserService USER_SERVICE = new UserService();
    private static final GameService GAME_SERVICE = new GameService();
    private static final ClearService CLEAR_SERVICE = new ClearService();



    public static class RegisterHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            //deserialize body
            RegisterRequest request = SERIALIZER.fromJson(ctx.body(), RegisterRequest.class);

            //pass to service
            RegisterResult result = USER_SERVICE.register(request);

            //return response
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(SERIALIZER.toJson(result));
        }
    }

    public static class LoginHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            LoginRequest request = SERIALIZER.fromJson(ctx.body(), LoginRequest.class);

            LoginResult result = USER_SERVICE.login(request);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(SERIALIZER.toJson(result));

        }
    }

    public static class LogoutHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            String authToken = getAuthToken(ctx);

            LogoutRequest request = new LogoutRequest(authToken);

            LogoutResult result = USER_SERVICE.logout(request);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(SERIALIZER.toJson(result));
        }
    }

    public static class ListHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws UnauthorizedException, BadRequestException, DataAccessException {
            String authToken = getAuthToken(ctx);

            ListRequest request = new ListRequest(authToken);

            ListResult result =  GAME_SERVICE.list(request);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(SERIALIZER.toJson(result));

        }
    }

    public static class CreateHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws UnauthorizedException, BadRequestException, DataAccessException {
            String authToken = getAuthToken(ctx);
            var body = SERIALIZER.fromJson(ctx.body(), Map.class);
            String gameName = (String) body.get("gameName"); //best way I could find to extract the body

            CreateRequest request = new CreateRequest(authToken, gameName);

            CreateResult result = GAME_SERVICE.create(request);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(SERIALIZER.toJson(result));
        }
    }

    public static class JoinHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws UnauthorizedException, BadRequestException, DataAccessException, AlreadyTakenException {
            String authToken = getAuthToken(ctx);
            JoinBody body = SERIALIZER.fromJson(ctx.body(), JoinBody.class);
            if (body == null || body.playerColor() == null || body.playerColor().isEmpty() || body.gameID() == null) {
                throw new BadRequestException("handler bad request");
            }

            JoinRequest request = new JoinRequest(authToken, body.playerColor(), body.gameID());

            JoinResult result = GAME_SERVICE.join(request);


            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(SERIALIZER.toJson(result));
        }
    }


    public static class ClearHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            CLEAR_SERVICE.clear();

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(SERIALIZER.toJson(new ClearResult()));
        }
    }

    private static String getAuthToken(Context ctx) throws UnauthorizedException {
        String authToken = ctx.header("Authorization");
        if (authToken == null || authToken.isEmpty()) {
            throw new UnauthorizedException("Not logged in");
        }
        return authToken;
    }
}
