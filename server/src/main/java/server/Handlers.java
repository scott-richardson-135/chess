package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.requests.*;
import model.results.*;
import org.eclipse.jetty.util.log.Log;
import service.ClearService;
import service.UserService;
import service.UserService.*;
import service.exceptions.*;
import org.jetbrains.annotations.NotNull;



public class Handlers {
    private static final Gson serializer = new Gson();
    private static final UserService userService = new UserService();
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
            String authToken = ctx.header("Authorization");
            if (authToken == null || authToken.isEmpty()) {
                throw new UnauthorizedException("Not logged in");
            }

            LogoutRequest request = new LogoutRequest(authToken);

            LogoutResult result = userService.logout(request);

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
}
