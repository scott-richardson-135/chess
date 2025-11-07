package ui;

public class ResponseException extends Exception {

    public enum Code {
        OK,
        BadRequest,
        Unauthorized,
        Forbidden,
        NotFound,
        ServerError,
        Other
    }

    private final Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public Code code() {
        return code;
    }

    public static ResponseException fromHttpStatusCode(int statusCode) {
        if (statusCode == 400) {
            return new ResponseException(Code.BadRequest, "Bad Request");
        }
        if (statusCode == 401) {
            return new ResponseException(Code.Unauthorized, "Unauthorized");
        }
        if (statusCode == 403) {
            return new ResponseException(Code.Forbidden, "Forbidden");
        }
        if (statusCode == 404) {
            return new ResponseException(Code.NotFound, "Not Found");
        }
        if (statusCode >= 500) {
            return new ResponseException(Code.ServerError, "Server Error");
        }
        return new ResponseException(Code.Other, "error: " + statusCode);
    }

    public static ResponseException fromJson(String body) {
        return new ResponseException(Code.Other, body);
    }
}
