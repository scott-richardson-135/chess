package chess.moves;

import chess.*;

import java.util.Collection;

public class MoveUtils {

    public static boolean isInBounds(int row, int col) {
        return (row >= 1 && row <= 8 && col >= 1 && col <= 8);
    }



    public static void addSliding(
            ChessBoard board,
            ChessPosition position,
            ChessPiece piece,
            int[][] directions,
            Collection<ChessMove> validMoves
    ) {
        for (int[] direction : directions) {
            //start where we are, then in each direction keep adding to position unitl we can't move anymore
            int newRow = position.getRow();
            int newCol = position.getColumn();

            while (true) {
                newRow += direction[0];
                newCol += direction[1];

                if (!isInBounds(newRow, newCol)) break;

                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece targetSquare = board.getPiece(newPosition);

                if (targetSquare == null) {
                    //nothing at the square, it is a valid move
                    validMoves.add(new ChessMove(position, newPosition, null));
                }
                else {
                    //if it is an enemy piece, we can capture and then we stop moving
                    if (targetSquare.getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(position,newPosition, null));
                    }
                    break;
                }

            }
        }
    }


    public static void addStepMove(
            ChessBoard board,
            ChessPosition position,
            ChessPiece piece,
            int[][] offsets,
            Collection<ChessMove> validMoves
    ) {
        for (int[] offset : offsets) {
            int newRow = position.getRow() + offset[0];
            int newCol = position.getColumn() + offset[1];

            if (!isInBounds(newRow, newCol)) continue;

            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessPiece targetSquare = board.getPiece(newPosition);

            if (targetSquare == null || targetSquare.getTeamColor() != piece.getTeamColor()) {
                validMoves.add(new ChessMove(position, newPosition, null));
            }
        }
    }

}

