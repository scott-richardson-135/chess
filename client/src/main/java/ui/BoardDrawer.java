package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

import static ui.EscapeSequences.*;

public class BoardDrawer {

    public static void drawBoard(ChessGame game, boolean isWhite, Collection<ChessPosition> highlights) {
        if (game == null || game.getBoard() == null) {
            System.out.println("No board available");
            return;
        }

        System.out.print(ERASE_SCREEN);

        ChessBoard board = game.getBoard();

        //if white, counts up. If black, counts down
        //Draw from the top, so rows are backwards
        int[] rows = isWhite ? new int[]{8,7,6,5,4,3,2,1} : new int[]{1,2,3,4,5,6,7,8};
        int[] cols = isWhite ? new int[]{1,2,3,4,5,6,7,8} : new int[]{8,7,6,5,4,3,2,1};

        System.out.println("\n");
        System.out.print("    ");

        //draw top column label
        for (int col : cols) {
            System.out.print(getLetter(col) + "  ");
        }

        System.out.println();

        for (int row : rows) {
            // label on the left
            System.out.print(" " + row + " ");

            //draw the squares and pieces for the row
            for (int col : cols) {
                boolean lightSquare = (row + col) % 2 == 1;
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                String symbol = getSymbol(piece);

                boolean isHighlighted = highlights != null && highlights.contains(position);
                String bgColor;

                if (isHighlighted) {
                    bgColor = SET_BG_COLOR_GREEN;
                } else {
                    bgColor = lightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                }
                System.out.print(bgColor + symbol + RESET_BG_COLOR);
            }

            //right label
            System.out.print(" " + row);
            System.out.println();
        }

        System.out.print("    ");
        for (int col : cols) {
            System.out.print(getLetter(col) + "  ");
        }
        System.out.println();
    }

    public static void drawBoard(ChessGame game, boolean isWhite) {
        drawBoard(game, isWhite, null);
    }

    private static String getLetter(int col) {
        return switch (col) {
            case 1 -> "a";
            case 2 -> "b";
            case 3 -> "c";
            case 4 -> "d";
            case 5 -> "e";
            case 6 -> "f";
            case 7 -> "g";
            case 8 -> "h";
            default -> "bad";
        };
    }

    private static String getSymbol(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }

        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        String color = isWhite ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK;


        String symbol = switch(piece.getPieceType()) {
            case PAWN -> isWhite ? "P" : "p";
            case ROOK -> isWhite ? "R" : "r";
            case KNIGHT -> isWhite ? "N" : "n";
            case BISHOP -> isWhite ? "B" : "b";
            case QUEEN -> isWhite ? "Q" : "q";
            case KING -> isWhite ? "K" : "k";
        };

        return color + " " + symbol + " " + RESET_TEXT_COLOR;
    }
}
