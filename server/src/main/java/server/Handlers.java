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
    private static final Gson serializer = new Gson();
    private static final UserService userService = new UserService();
    private static final GameService gameService = new GameService();
    private static final ClearService clearService = new ClearService();



    public static class RegisterHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            //deserialize body
            RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);

            //pass to service
            RegisterResult result = userService.register(request);

            //return response
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(result));
        }
    }

    public static class LoginHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            LoginRequest request = serializer.fromJson(ctx.body(), LoginRequest.class);

            LoginResult result = userService.login(request);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(result));

        }
    }

    public static class LogoutHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            String authToken = getAuthToken(ctx);

            LogoutRequest request = new LogoutRequest(authToken);

            LogoutResult result = userService.logout(request);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(result));
        }
    }

    public static class ListHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws UnauthorizedException, BadRequestException, DataAccessException {
            String authToken = getAuthToken(ctx);

            ListRequest request = new ListRequest(authToken);

            ListResult result =  gameService.list(request);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(result));

        }
    }

    public static class CreateHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws UnauthorizedException, BadRequestException, DataAccessException {
            String authToken = getAuthToken(ctx);
            var body = serializer.fromJson(ctx.body(), Map.class);
            String gameName = (String) body.get("gameName"); //best way I could find to extract the body

            CreateRequest request = new CreateRequest(authToken, gameName);

            CreateResult result = gameService.create(request);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(result));
        }
    }

    public static class JoinHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws UnauthorizedException, BadRequestException, DataAccessException, AlreadyTakenException {
            String authToken = getAuthToken(ctx);
            JoinBody body = serializer.fromJson(ctx.body(), JoinBody.class);
            if (body == null || body.playerColor() == null || body.playerColor().isEmpty() || body.gameID() == null) {
                throw new BadRequestException("bad request");
            }

            JoinRequest request = new JoinRequest(authToken, body.playerColor(), body.gameID());

            JoinResult result = gameService.join(request);


            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(result));
        }
    }


    public static class ClearHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            clearService.clear();

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(new ClearResult()));
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
