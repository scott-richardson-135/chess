package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMoveCalculator{
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);
        int startingRow;
        int[] direction;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            //white piece, starts on row 2 and moves up
            startingRow = 2;
            direction = new int[]{1, 0};
        }
        else {
            //black piece, starts on row 7 and moves down
            startingRow = 7;
            direction = new int[]{-1, 0};
        }

        //1 step forward
        int newRow = position.getRow() + direction[0];
        int newCol = position.getColumn();
        ChessPosition newPosition = new ChessPosition(newRow, newCol);

        if (MoveUtils.isInBounds(newRow, newCol) && board.getPiece(newPosition) == null) {
            //validMoves.add(new ChessMove(position, newPosition, null));
            checkPromotion(piece, validMoves, newRow, position, newPosition);
        }

        //2 step forward (possible if on starting row)
        if (position.getRow() == startingRow) {
            int twoStep = position.getRow() + (2 * direction[0]);
            ChessPosition twoStepPosition = new ChessPosition(twoStep, newCol);
            if (MoveUtils.isInBounds(twoStep, newCol) && board.getPiece(newPosition) == null && board.getPiece(twoStepPosition) == null) {
                //validMoves.add(new ChessMove(position, twoStepPosition, null));
                checkPromotion(piece, validMoves, newRow, position, twoStepPosition);
            }
        }

        //diagonals for captures
        int[][] diagonalDirections = {
                {direction[0], -1}, {direction[0], 1}
        };
        for (int[] direct : diagonalDirections) {
            int captureRow = position.getRow() + direct[0];
            int captureCol = position.getColumn() + direct[1];

            if (!MoveUtils.isInBounds(captureRow, captureCol)) break;

            ChessPosition capturePosition = new ChessPosition(captureRow, captureCol);
            ChessPiece targetSquare = board.getPiece(capturePosition);

            if (targetSquare != null && piece.getTeamColor() != targetSquare.getTeamColor()) {
                //validMoves.add(new ChessMove(position, capturePosition, null));
                checkPromotion(piece, validMoves, newRow, position, capturePosition);
            }
        }

        return validMoves;
    }

    private void checkPromotion(ChessPiece piece, Collection<ChessMove> validMoves, int newRow, ChessPosition position, ChessPosition newPosition) {
        if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8) || (piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1)) {
            validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
            validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
        }
        else {
            validMoves.add(new ChessMove(position, newPosition, null));
        }
    }
}
