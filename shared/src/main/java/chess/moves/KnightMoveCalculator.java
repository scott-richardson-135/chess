package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] possibleMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        ChessPiece piece = board.getPiece(position);

        for (int[] move : possibleMoves) {
            int newRow = position.getRow() + move[0];
            int newCol = position.getColumn() + move[1];

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
