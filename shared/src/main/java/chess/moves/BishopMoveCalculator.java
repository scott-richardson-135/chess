package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator implements PieceMoveCalculator{
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);
        int[][] directions = {
                {1, -1}, {1, 1}, {-1, 1}, {-1, -1}
        };

        MoveUtils.addSliding(board, position, piece, directions, validMoves);
        return validMoves;
    }

}
