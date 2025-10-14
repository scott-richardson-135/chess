package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import model.results.ErrorResponse;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;

public class Server {

    private final Javalin javalin;
    private static final Gson serializer = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", new Handlers.RegisterHandler());



        //exception handlers
        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(new ErrorResponse("Error: " + e.getMessage())));
        });

        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(new ErrorResponse("Error: " + e.getMessage())));
        });

        javalin.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(new ErrorResponse("Error: " + e.getMessage())));
        });

        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(serializer.toJson(new ErrorResponse("Error: " + e.getMessage())));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
