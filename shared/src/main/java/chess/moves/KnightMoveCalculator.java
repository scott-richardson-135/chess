package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] possibleMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        MoveUtils.addStepMove(board, position, piece, possibleMoves, validMoves);
        return validMoves;
    }
}
