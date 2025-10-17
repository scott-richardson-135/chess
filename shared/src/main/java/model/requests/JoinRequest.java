package model.requests;

public record JoinRequest(String authToken, String playerColor, int ID) {
}
