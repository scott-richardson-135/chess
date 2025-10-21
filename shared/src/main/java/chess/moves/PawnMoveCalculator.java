package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int startingPosition;
        int direction;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            //white, starts on 2 and goes up
            startingPosition = 2;
            direction = 1;
        }
        else {
            startingPosition = 7;
            direction = -1;
        }


        //1 step forward
        int oneStepRow = position.getRow() + direction;
        int oneStepCol = position.getColumn();
        ChessPosition oneStepPosition = new ChessPosition(oneStepRow, oneStepCol);

        if (MoveUtils.isInBounds(oneStepRow, oneStepCol)) {
            ChessPiece targetSquare = board.getPiece(oneStepPosition);
            if (targetSquare == null) {
                checkPromotion(piece, oneStepRow, validMoves, position, oneStepPosition);
            }
        }

        //2 steps forward
        if (position.getRow() == startingPosition) {
            int twoStepRow = position.getRow() + 2 * direction;
            int twoStepCol = position.getColumn();
            ChessPosition twoStepPosition = new ChessPosition(twoStepRow, twoStepCol);

            if (MoveUtils.isInBounds(twoStepRow, twoStepCol)) {
                ChessPiece oneStepSquare = board.getPiece(oneStepPosition);
                ChessPiece twoStepSquare = board.getPiece(twoStepPosition);

                if (oneStepSquare == null && twoStepSquare == null) {
                    checkPromotion(piece, twoStepRow, validMoves, position, twoStepPosition);
                }
            }
        }


        //capturing
        int[][] captureDirections = {
                {direction, -1}, {direction, 1}
        };

        for (int[] dir : captureDirections) {
            int captureRow = position.getRow() + dir[0];
            int captureCol = position.getColumn() + dir[1];

            if (!MoveUtils.isInBounds(captureRow, captureCol)) {
                continue;
            }

            ChessPosition capturePosition = new ChessPosition(captureRow, captureCol);
            ChessPiece targetSquare = board.getPiece(capturePosition);

            if (targetSquare != null && targetSquare.getTeamColor() != piece.getTeamColor()) {
                checkPromotion(piece, captureRow, validMoves, position, capturePosition);
            }
        }


        return validMoves;
    }


    private void checkPromotion(ChessPiece piece,
                                int newRow,
                                Collection<ChessMove> validMoves,
                                ChessPosition startPosition,
                                ChessPosition endPosition) {


        if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8)
                || (piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1)) {
            validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
        }
        else {
            validMoves.add(new ChessMove(startPosition, endPosition, null));
        }
    }
}
