package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);

        int[][] directions = {
                {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
        };

        for (int[] direction : directions) {
            int newRow = position.getRow() + direction[0];
            int newCol = position.getColumn() + direction[1];

            if (!isInBounds(newRow, newCol)) continue;

            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessPiece targetSquare = board.getPiece(newPosition);

            if (targetSquare == null || targetSquare.getTeamColor() != piece.getTeamColor()) {
                validMoves.add(new ChessMove(position, newPosition, null));
            }


        }
        return validMoves;
    }
}
