package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.requests.*;
import model.results.*;
import service.ClearService;
import service.UserService;
import service.UserService.*;
import service.exceptions.*;
import org.jetbrains.annotations.NotNull;



public class Handlers {
    private static final Gson serializer = new Gson();



    public static class RegisterHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            //deserialize body
            RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);

            //pass to service
            UserService service = new UserService();
            RegisterResult result = service.register(request);

            //return response
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(result));
        }
    }


    public static class ClearHandler implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            ClearService service = new ClearService();
            service.clear();

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(new ClearResult()));


        }
    }
}
