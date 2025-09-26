package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor currentTurn;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //get the preliminary moves from this position.

        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        //loop through each move, and then make the move on a copy of the board. If isInCheck on that copy, remove this move from the list
        for (ChessMove move : possibleMoves) {
            ChessBoard boardCopy = board.copy();

            //This is pretty much what happens in the movePiece function but I can't figure out how to get it to work on a different board
            //maybe someday I can fix that
            ChessPiece pieceToMove = boardCopy.getPiece(move.getStartPosition());
            boardCopy.addPiece(move.getEndPosition(), pieceToMove);
            boardCopy.addPiece(move.getStartPosition(), null);

            //run isInCheck on the board copy
            ChessGame tempGame = new ChessGame();
            tempGame.setBoard(boardCopy);
            tempGame.setTeamTurn(this.currentTurn);

            //add to valid moves if tempGame is not in check for the protagonist team
            if (!tempGame.isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }

        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());


        if (!legalMoves.contains(move) || currentTurn != piece.getTeamColor()) {
            throw new InvalidMoveException();
        }


        movePiece(move);


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);

        TeamColor enemyColor = (teamColor == TeamColor.WHITE)? TeamColor.BLACK : TeamColor.WHITE;


        //next up, get a list of positions for each enemy piece
        //then loop through those and check every piece to see if any of them have moves that hit the king
        Collection<ChessPosition> enemyPositions = findPositions(enemyColor);

        for (ChessPosition enemyPos : enemyPositions) {
            ChessPiece enemyPiece = board.getPiece(enemyPos);
            Collection<ChessMove> possibleMoves = enemyPiece.pieceMoves(board, enemyPos);

            for (ChessMove move : possibleMoves) {
                if (move.getEndPosition().equals(kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    //finds the position of king with given color
    public ChessPosition findKing(TeamColor color) {

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color) {
                    return position;
                }
            }
        }

        throw new RuntimeException("Can't find king for " + color);
    }


    //finds every position where a piece of given color is at
    public Collection<ChessPosition> findPositions(TeamColor color) {
        Collection<ChessPosition> enemyPositions = new ArrayList<>();


        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == color) {
                    enemyPositions.add(position);
                }
            }
        }

        return enemyPositions;
    }


    private void movePiece(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece pieceToMove = board.getPiece(startPosition);

        if (move.getPromotionPiece() != null) {
            pieceToMove.setPieceType(move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(), pieceToMove);
        board.addPiece(startPosition, null);

        //update current turn
        TeamColor turnToSwitchTo = (currentTurn == TeamColor.WHITE)? TeamColor.BLACK : TeamColor.WHITE;
        setTeamTurn(turnToSwitchTo);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurn);
    }
}
