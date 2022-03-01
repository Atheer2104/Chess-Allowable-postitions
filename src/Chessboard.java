import sun.applet.Main;

import java.util.Random;

public class Chessboard {
    public static class Field {
        // a, b, c, d, e, f, g
        private char row;
        // 0, 1, 2, 3, 4, 5, 6, 7
        private byte column;
        private Chesspiece piece = null;
        private boolean marked = false;

        public Field (char row, byte column) {
            this.row = row;
            this.column = column;
        }

        public void put (Chesspiece piece) {
            this.piece = piece;
            this.piece.row = row;
            this.piece.column = column;
        }

        public Chesspiece take () {
            this.piece = null;
            return null;
        }

        public void mark () {
            this.marked = true;
        }

        public void unmark () {
            this.marked = false;
        }

        public String toString () {
            String s = (marked)? "xx" : "--";
            return (piece == null)? s : piece.toString (); }
    }

    public static final int NUMBER_OF_ROWS = 8;
    public static final int NUMBER_OF_COLUMNS = 8;
    public static final int FIRST_ROW = 'a';
    public static final int FIRST_COLUMN = 1;

    private Field[][] fields;

    public Chessboard () {
        fields = new Field[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        char row = 0;
        byte column = 0;

        for (int r = 0; r < NUMBER_OF_ROWS; r++) {
            row = (char) (FIRST_ROW + r);
            column = FIRST_COLUMN;
            for (int c = 0; c < NUMBER_OF_COLUMNS; c++) {
                fields[r][c] = new Field(row, column);
                column++;
            }
        }
    }

    public String toString () {
        char row = 0;

        System.out.print("  ");
        for (int i = 1; i <= NUMBER_OF_COLUMNS; i++) {
            System.out.print(i + "  ");
        }
        System.out.println("");

        for (int r = 0; r < NUMBER_OF_ROWS; r++) {
            row = (char) (FIRST_ROW + r);

            System.out.print(row + " ");
            for (int c = 0; c < NUMBER_OF_COLUMNS; c++) {
               System.out.print(fields[r][c] + " ");
            }
            System.out.println("");
        }

        System.out.println("\n");
        return "";
    }

    public boolean isValidField (char row, byte column) {
        if (row < 'a' || row > 'h') {
            return false;
        }

        if (column < 0 || column > 7) {
            return false;
        }

        return true;
    }

    public void presentPieces(Chesspiece[] pieces) {
        Random random = new Random();

        for (int i = 0; i < pieces.length; i++) {
            int randomRow = random.nextInt(8);
            int randomColumn = random.nextInt(8);
            Field initalField = fields[randomRow][randomColumn];

            initalField.put(pieces[i]);
            initalField.piece.markReachableFields();
            Chessboard.this.toString();
            try {
                Thread.sleep(2000);
            }catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            initalField.piece.unmarkReachableFields();
            initalField.take();

        }

    }

    public static void main(String[] args) {

        Chessboard chessboard = new Chessboard();
        Chessboard.Chesspiece pawn = chessboard.new Pawn('w', 'P');
        Chessboard.Chesspiece rook = chessboard.new Rook('w', 'R');
        Chessboard.Chesspiece knight = chessboard.new Knight('w', 'K');
        Chessboard.Chesspiece bishop = chessboard.new Bishop('w', 'B');
        Chessboard.Chesspiece queen = chessboard.new Queen('w', 'Q');
        Chessboard.Chesspiece king = chessboard.new King('w', 'K');
        Field a3 = chessboard.fields[4][4];
        //a3.put(pawn);
        //a3.put(rook);
        //a3.put(knight);
        //a3.put(bishop);
        a3.put(queen);
        //a3.put(king);
        //a3.piece.markReachableFields();
        System.out.println(chessboard);

        //a3.piece.unmarkReachableFields();
        //System.out.println(chessboard);
/*
        try {
            a3.piece.moveTo('a', (byte) 1);
        } catch (NotValidFieldException error) {
            System.out.println(error.getMessage());
        }*/

        System.out.println(chessboard);

        //a3.piece.moveOut();

        //System.out.println(chessboard);

    }

    public class NotValidFieldException extends Exception {
        public NotValidFieldException(String message) {
            super(message);
        }
    }

    public abstract class Chesspiece {
        private char color;
        // w - white, b - black

        private char name;
        // K - King, Q - Queen, R - Rook, B - Bishop, N - Knight, P - Pawn

        protected char row = 0;
        protected byte column = -1;

        protected Chesspiece(char color, char name) {
            this.color = color;
            this.name = name;

        }

        public String toString() {
            return "" + color + name;
        }

        public boolean isOnBoard() {
            return Chessboard.this.isValidField(row, column);
        }

        public void moveTo(char row, byte column) throws NotValidFieldException {

            if (!Chessboard.this.isValidField(row, column)) {
                throw new NotValidFieldException("bad field: " + row + column);
            }

            Chessboard.this.fields[this.row - FIRST_ROW][this.column - FIRST_COLUMN].take();

            this.row = row;
            this.column = column;

            int r = row - FIRST_ROW;
            int c = column - FIRST_COLUMN;

            Chessboard.this.fields[r][c].put(this);
        }

        public void moveOut() {
            if (isOnBoard()) {
                Chessboard.this.fields[row - FIRST_ROW][column - FIRST_COLUMN].take();
            }
        }

        public abstract void markReachableFields();

        public abstract void unmarkReachableFields();

    }

    public class Pawn extends Chesspiece {
        public Pawn(char color, char name) {
            super(color, name);
        }

        public void markReachableFields() {
            int counter = 2;
            int index = 1;
            while(true) {
                char Nextrow = (char) (row + index);
                byte targetColumn = (byte) (column -1);
                if (Chessboard.this.isValidField(Nextrow, targetColumn) && counter > 0) {
                    Chessboard.this.fields[Nextrow - 'a'][targetColumn].mark();
                    index++;
                    counter--;
                } else {
                    break;
                }
            }
        }

        public void unmarkReachableFields() {
            int counter = 2;
            int index = 1;
            while(true) {
                char Nextrow = (char) (row + index);
                byte targetColumn = (byte) (column -1);
                if (Chessboard.this.isValidField(Nextrow, targetColumn) && counter > 0) {
                    Chessboard.this.fields[Nextrow - 'a'][targetColumn].unmark();
                    index++;
                    counter--;
                } else {
                    break;
                }
            }
        }
    }

    public class Rook extends Chesspiece {
        public Rook(char color, char name) {
            super(color, name);
        }

        public void markReachableFields() {
            int currentRow = (row - 'a');
            int verticalColumn = column - 1;
            for (int r = 0; r < Chessboard.NUMBER_OF_ROWS; r++) {
                for (int c = 0; c < Chessboard.NUMBER_OF_COLUMNS; c++) {
                    if (c == verticalColumn || r == currentRow) {
                        char NextRow = (char) (r + 'a');
                        byte nextColumn = (byte) c;
                        if (Chessboard.this.isValidField(NextRow, nextColumn)) {
                            Chessboard.this.fields[r][c].mark();
                        }

                    }
                }
            }
        }

        public void unmarkReachableFields() {
            int currentRow = (row - 'a');
            int verticalColumn = column - 1;
            for (int r = 0; r < Chessboard.NUMBER_OF_ROWS; r++) {
                for (int c = 0; c < Chessboard.NUMBER_OF_COLUMNS; c++) {
                    if (c == verticalColumn || r == currentRow) {
                        char NextRow = (char) (r + 'a');
                        byte nextColumn = (byte) c;
                        if (Chessboard.this.isValidField(NextRow, nextColumn)) {
                            Chessboard.this.fields[r][c].unmark();
                        }

                    }
                }
            }
        }

    }

    public class Knight extends Chesspiece {
        public Knight(char color, char name) {
            super(color, name);
        }

        public void markReachableFields() {

            int currentRow = (row - 'a');
            for (int r = 0; r < Chessboard.NUMBER_OF_ROWS; r++) {
                for (int c = 0; c < Chessboard.NUMBER_OF_COLUMNS; c++) {
                    if (r == currentRow) {
                        char targetUpperRow = (char) ((currentRow - 2) + 'a');
                        char targetLowerRow = (char) ((currentRow + 2) + 'a');
                        byte targetColumn = (byte) (column - 2);

                        // upper left square
                        if (Chessboard.this.isValidField(targetUpperRow, column)) {
                            Chessboard.this.fields[currentRow - 2][column].mark();
                        }

                        // upper right square
                        if (Chessboard.this.isValidField(targetUpperRow, targetColumn)) {
                            Chessboard.this.fields[currentRow - 2][targetColumn].mark();
                        }

                        // lower left square
                        if (Chessboard.this.isValidField(targetLowerRow, column)) {
                            Chessboard.this.fields[currentRow + 2][column].mark();
                        }

                        // lower right square
                        if (Chessboard.this.isValidField(targetLowerRow, targetColumn)) {
                            Chessboard.this.fields[currentRow + 2][targetColumn].mark();
                        }
                    }
                }
            }

            currentRow = (row - 'a');
            for (int r = 0; r < Chessboard.NUMBER_OF_ROWS; r++) {
                for (int c = 0; c < Chessboard.NUMBER_OF_COLUMNS; c++) {
                    if (r == currentRow ) {
                        char targetLowerMidRow = (char) ((currentRow - 1) + 'a');
                        char targetUpperMidRow = (char) ((currentRow + 1) + 'a');
                        byte targetRightHortizontalColumn = (byte) (column + 1);
                        byte targetLeftHortizontalColumn = (byte) (column - 3);

                        // upper mid Row right column
                        if (Chessboard.this.isValidField(targetLowerMidRow, targetRightHortizontalColumn)) {
                            Chessboard.this.fields[r - 1][targetRightHortizontalColumn].mark();
                        }

                        // upper mid Row left column
                        if (Chessboard.this.isValidField(targetLowerMidRow, targetLeftHortizontalColumn)) {
                            Chessboard.this.fields[r - 1][targetLeftHortizontalColumn].mark();
                        }

                        // lower mid Row right column
                        if (Chessboard.this.isValidField(targetUpperMidRow, targetRightHortizontalColumn)) {
                            Chessboard.this.fields[r + 1][targetRightHortizontalColumn].mark();
                        }

                        // lower mid Row left column
                        if (Chessboard.this.isValidField(targetUpperMidRow, targetLeftHortizontalColumn)) {
                            Chessboard.this.fields[r + 1][targetLeftHortizontalColumn].mark();
                        }
                    }
                }
            }
        }

        public void unmarkReachableFields() {

            int currentRow = (row - 'a');
            for (int r = 0; r < Chessboard.NUMBER_OF_ROWS; r++) {
                for (int c = 0; c < Chessboard.NUMBER_OF_COLUMNS; c++) {
                    if (r == currentRow) {
                        char targetUpperRow = (char) ((currentRow - 2) + 'a');
                        char targetLowerRow = (char) ((currentRow + 2) + 'a');
                        byte targetColumn = (byte) (column - 2);

                        // upper left square
                        if (Chessboard.this.isValidField(targetUpperRow, column)) {
                            Chessboard.this.fields[currentRow - 2][column].unmark();
                        }

                        // upper right square
                        if (Chessboard.this.isValidField(targetUpperRow, targetColumn)) {
                            Chessboard.this.fields[currentRow - 2][targetColumn].unmark();
                        }

                        // lower left square
                        if (Chessboard.this.isValidField(targetLowerRow, column)) {
                            Chessboard.this.fields[currentRow + 2][column].unmark();
                        }

                        // lower right square
                        if (Chessboard.this.isValidField(targetLowerRow, targetColumn)) {
                            Chessboard.this.fields[currentRow + 2][targetColumn].unmark();
                        }
                    }
                }
            }

            currentRow = (row - 'a');
            for (int r = 0; r < Chessboard.NUMBER_OF_ROWS; r++) {
                for (int c = 0; c < Chessboard.NUMBER_OF_COLUMNS; c++) {
                    if (r == currentRow ) {
                        char targetLowerMidRow = (char) ((currentRow - 1) + 'a');
                        char targetUpperMidRow = (char) ((currentRow + 1) + 'a');
                        byte targetRightHortizontalColumn = (byte) (column + 1);
                        byte targetLeftHortizontalColumn = (byte) (column - 3);

                        // upper mid Row right column
                        if (Chessboard.this.isValidField(targetLowerMidRow, targetRightHortizontalColumn)) {
                            Chessboard.this.fields[r - 1][targetRightHortizontalColumn].unmark();
                        }

                        // upper mid Row left column
                        if (Chessboard.this.isValidField(targetLowerMidRow, targetLeftHortizontalColumn)) {
                            Chessboard.this.fields[r - 1][targetLeftHortizontalColumn].unmark();
                        }

                        // lower mid Row right column
                        if (Chessboard.this.isValidField(targetUpperMidRow, targetRightHortizontalColumn)) {
                            Chessboard.this.fields[r + 1][targetRightHortizontalColumn].unmark();
                        }

                        // lower mid Row left column
                        if (Chessboard.this.isValidField(targetUpperMidRow, targetLeftHortizontalColumn)) {
                            Chessboard.this.fields[r + 1][targetLeftHortizontalColumn].unmark();
                        }
                    }
                }
            }

        }
    }

    public class Bishop extends Chesspiece {
        public Bishop(char color, char name) {
            super(color, name);
        }

        public void markReachableFields() {
            int currentRow = (row - 'a');
            int currentColumn = column - 1;
            int index = 1;

            // lower right diagonal
            while(true) {
                char targetRow = (char) ((currentRow + index) + 'a');
                byte targetColumn = (byte) (currentColumn + index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    System.out.println(targetColumn);
                    Chessboard.this.fields[targetRow - 'a'][targetColumn].mark();
                    currentRow++;
                    currentColumn++;
                } else {
                    break;
                }
            }

            // lower left diagonal
            currentRow = (row - 'a');
            currentColumn = column - 1;
            while(true) {
                char targetRow = (char) ((currentRow + index) + 'a');
                byte targetColumn = (byte) (currentColumn - index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    Chessboard.this.fields[targetRow  - 'a'][targetColumn].mark();
                    currentRow++;
                    currentColumn--;
                } else {
                    break;
                }
            }


            // upper right diagonal
            currentRow = (row - 'a');
            currentColumn = column - 1;
            while(true) {
                char targetRow = (char) ((currentRow - index) + 'a');
                byte targetColumn = (byte) (currentColumn + index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    Chessboard.this.fields[targetRow - 'a'][targetColumn].mark();
                    currentRow--;
                    currentColumn++;
                } else {
                    break;
                }
            }

            //upper left diagonal
            currentRow = (row - 'a');
            currentColumn = column - 1;
            while(true) {
                char targetRow = (char) ((currentRow - index) + 'a');
                byte targetColumn = (byte) (currentColumn - index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    Chessboard.this.fields[targetRow - 'a'][targetColumn].mark();
                    currentRow--;
                    currentColumn--;
                } else {
                    break;
                }
            }
        }

        public void unmarkReachableFields() {
            int currentRow = (row - 'a');
            int currentColumn = column - 1;
            int index = 1;

            // lower right diagonal
            while(true) {
                char targetRow = (char) ((currentRow + index) + 'a');
                byte targetColumn = (byte) (currentColumn + index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    System.out.println(targetColumn);
                    Chessboard.this.fields[targetRow - 'a'][targetColumn].unmark();
                    currentRow++;
                    currentColumn++;
                } else {
                    break;
                }
            }

            // lower left diagonal
            currentRow = (row - 'a');
            currentColumn = column - 1;
            while(true) {
                char targetRow = (char) ((currentRow + index) + 'a');
                byte targetColumn = (byte) (currentColumn - index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    Chessboard.this.fields[targetRow  - 'a'][targetColumn].unmark();
                    currentRow++;
                    currentColumn--;
                } else {
                    break;
                }
            }


            // upper right diagonal
            currentRow = (row - 'a');
            currentColumn = column - 1;
            while(true) {
                char targetRow = (char) ((currentRow - index) + 'a');
                byte targetColumn = (byte) (currentColumn + index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    Chessboard.this.fields[targetRow - 'a'][targetColumn].unmark();
                    currentRow--;
                    currentColumn++;
                } else {
                    break;
                }
            }

            //upper left diagonal
            currentRow = (row - 'a');
            currentColumn = column - 1;
            while(true) {
                char targetRow = (char) ((currentRow - index) + 'a');
                byte targetColumn = (byte) (currentColumn - index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    Chessboard.this.fields[targetRow - 'a'][targetColumn].unmark();
                    currentRow--;
                    currentColumn--;
                } else {
                    break;
                }
            }
        }
    }

    public class Queen extends Chesspiece {
        public Queen(char color, char name) {
            super(color, name);
        }

        public void markReachableFields() {
            int currentRow = (row - 'a');
            int currentColumn = column - 1;
            int index = 1;

            char targetLowerRow = (char) (row + 1);
            char targetUpperrRow = (char) (row - 1);
            byte targetRightColumn = (byte) column;
            byte targetLeftColumn = (byte) (column - 2);
            byte curColumn = (byte) currentColumn;

            // KING MOVES

            // lower row
            if (Chessboard.this.isValidField(targetLowerRow, targetRightColumn)) {
                Chessboard.this.fields[targetLowerRow - 'a'][targetRightColumn].mark();
            }

            if (Chessboard.this.isValidField(targetLowerRow, targetLeftColumn)) {
                Chessboard.this.fields[targetLowerRow - 'a'][targetLeftColumn].mark();
            }

            if (Chessboard.this.isValidField(targetLowerRow, curColumn)) {
                Chessboard.this.fields[targetLowerRow - 'a'][curColumn].mark();
            }

            // upper row
            if (Chessboard.this.isValidField(targetUpperrRow, targetRightColumn)) {
                Chessboard.this.fields[targetUpperrRow - 'a'][targetRightColumn].mark();
            }

            if (Chessboard.this.isValidField(targetUpperrRow, targetLeftColumn)) {
                Chessboard.this.fields[targetUpperrRow - 'a'][targetLeftColumn].mark();
            }

            if (Chessboard.this.isValidField(targetUpperrRow, curColumn)) {
                Chessboard.this.fields[targetUpperrRow - 'a'][curColumn].mark();
            }

            // right square of king
            if (Chessboard.this.isValidField(row, targetRightColumn)) {
                Chessboard.this.fields[row - 'a'][targetRightColumn].mark();
            }

            // left square of king
            if (Chessboard.this.isValidField(row, targetLeftColumn)) {
                Chessboard.this.fields[row - 'a'][targetLeftColumn].mark();
            }

            // END KING MOVES

            // ROOK MOVES
            int verticalColumn = column - 1;
            for (int r = 0; r < Chessboard.NUMBER_OF_ROWS; r++) {
                for (int c = 0; c < Chessboard.NUMBER_OF_COLUMNS; c++) {
                    if (c == verticalColumn || r == currentRow) {
                        char NextRow = (char) (r + 'a');
                        byte nextColumn = (byte) c;
                        if (Chessboard.this.isValidField(NextRow, nextColumn)) {
                            Chessboard.this.fields[r][c].mark();
                        }

                    }
                }
            }

            // END ROOK MOVES

            // BISHOPS MOVES

            // lower right diagonal
            while(true) {
                char targetRow = (char) ((currentRow + index) + 'a');
                byte targetColumn = (byte) (currentColumn + index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    System.out.println(targetColumn);
                    Chessboard.this.fields[targetRow - 'a'][targetColumn].mark();
                    currentRow++;
                    currentColumn++;
                } else {
                    break;
                }
            }

            // lower left diagonal
            currentRow = (row - 'a');
            currentColumn = column - 1;
            while(true) {
                char targetRow = (char) ((currentRow + index) + 'a');
                byte targetColumn = (byte) (currentColumn - index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    Chessboard.this.fields[targetRow  - 'a'][targetColumn].mark();
                    currentRow++;
                    currentColumn--;
                } else {
                    break;
                }
            }


            // upper right diagonal
            currentRow = (row - 'a');
            currentColumn = column - 1;
            while(true) {
                char targetRow = (char) ((currentRow - index) + 'a');
                byte targetColumn = (byte) (currentColumn + index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    Chessboard.this.fields[targetRow - 'a'][targetColumn].mark();
                    currentRow--;
                    currentColumn++;
                } else {
                    break;
                }
            }

            //upper left diagonal
            currentRow = (row - 'a');
            currentColumn = column - 1;
            while(true) {
                char targetRow = (char) ((currentRow - index) + 'a');
                byte targetColumn = (byte) (currentColumn - index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    Chessboard.this.fields[targetRow - 'a'][targetColumn].mark();
                    currentRow--;
                    currentColumn--;
                } else {
                    break;
                }
            }

            // END BISHOP MOVES

        }

        public void unmarkReachableFields() {
            int currentRow = (row - 'a');
            int currentColumn = column - 1;
            int index = 1;

            char targetLowerRow = (char) (row + 1);
            char targetUpperrRow = (char) (row - 1);
            byte targetRightColumn = (byte) column;
            byte targetLeftColumn = (byte) (column - 2);
            byte curColumn = (byte) currentColumn;

            // KING MOVES

            // lower row
            if (Chessboard.this.isValidField(targetLowerRow, targetRightColumn)) {
                Chessboard.this.fields[targetLowerRow - 'a'][targetRightColumn].unmark();
            }

            if (Chessboard.this.isValidField(targetLowerRow, targetLeftColumn)) {
                Chessboard.this.fields[targetLowerRow - 'a'][targetLeftColumn].unmark();
            }

            if (Chessboard.this.isValidField(targetLowerRow, curColumn)) {
                Chessboard.this.fields[targetLowerRow - 'a'][curColumn].unmark();
            }

            // upper row
            if (Chessboard.this.isValidField(targetUpperrRow, targetRightColumn)) {
                Chessboard.this.fields[targetUpperrRow - 'a'][targetRightColumn].unmark();
            }

            if (Chessboard.this.isValidField(targetUpperrRow, targetLeftColumn)) {
                Chessboard.this.fields[targetUpperrRow - 'a'][targetLeftColumn].unmark();
            }

            if (Chessboard.this.isValidField(targetUpperrRow, curColumn)) {
                Chessboard.this.fields[targetUpperrRow - 'a'][curColumn].unmark();
            }

            // right square of king
            if (Chessboard.this.isValidField(row, targetRightColumn)) {
                Chessboard.this.fields[row - 'a'][targetRightColumn].unmark();
            }

            // left square of king
            if (Chessboard.this.isValidField(row, targetLeftColumn)) {
                Chessboard.this.fields[row - 'a'][targetLeftColumn].unmark();
            }

            // END KING MOVES

            // ROOK MOVES
            int verticalColumn = column - 1;
            for (int r = 0; r < Chessboard.NUMBER_OF_ROWS; r++) {
                for (int c = 0; c < Chessboard.NUMBER_OF_COLUMNS; c++) {
                    if (c == verticalColumn || r == currentRow) {
                        char NextRow = (char) (r + 'a');
                        byte nextColumn = (byte) c;
                        if (Chessboard.this.isValidField(NextRow, nextColumn)) {
                            Chessboard.this.fields[r][c].unmark();
                        }

                    }
                }
            }

            // END ROOK MOVES

            // BISHOPS MOVES

            // lower right diagonal
            while(true) {
                char targetRow = (char) ((currentRow + index) + 'a');
                byte targetColumn = (byte) (currentColumn + index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    System.out.println(targetColumn);
                    Chessboard.this.fields[targetRow - 'a'][targetColumn].unmark();
                    currentRow++;
                    currentColumn++;
                } else {
                    break;
                }
            }

            // lower left diagonal
            currentRow = (row - 'a');
            currentColumn = column - 1;
            while(true) {
                char targetRow = (char) ((currentRow + index) + 'a');
                byte targetColumn = (byte) (currentColumn - index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    Chessboard.this.fields[targetRow  - 'a'][targetColumn].unmark();
                    currentRow++;
                    currentColumn--;
                } else {
                    break;
                }
            }


            // upper right diagonal
            currentRow = (row - 'a');
            currentColumn = column - 1;
            while(true) {
                char targetRow = (char) ((currentRow - index) + 'a');
                byte targetColumn = (byte) (currentColumn + index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    Chessboard.this.fields[targetRow - 'a'][targetColumn].unmark();
                    currentRow--;
                    currentColumn++;
                } else {
                    break;
                }
            }

            //upper left diagonal
            currentRow = (row - 'a');
            currentColumn = column - 1;
            while(true) {
                char targetRow = (char) ((currentRow - index) + 'a');
                byte targetColumn = (byte) (currentColumn - index);
                if (Chessboard.this.isValidField(targetRow, targetColumn)) {
                    Chessboard.this.fields[targetRow - 'a'][targetColumn].unmark();
                    currentRow--;
                    currentColumn--;
                } else {
                    break;
                }
            }

            // END BISHOP MOVES
        }
    }

    public class King extends Chesspiece {
        public King(char color, char name) {
            super(color, name);
        }

        public void markReachableFields() {
            int currentColumn = column - 1;
            char targetLowerRow = (char) (row + 1);
            char targetUpperrRow = (char) (row - 1);
            byte targetRightColumn = (byte) column;
            byte targetLeftColumn = (byte) (column - 2);
            byte curColumn = (byte) currentColumn;

            // lower row
            if (Chessboard.this.isValidField(targetLowerRow, targetRightColumn)) {
                Chessboard.this.fields[targetLowerRow - 'a'][targetRightColumn].mark();
            }

            if (Chessboard.this.isValidField(targetLowerRow, targetLeftColumn)) {
                Chessboard.this.fields[targetLowerRow - 'a'][targetLeftColumn].mark();
            }

            if (Chessboard.this.isValidField(targetLowerRow, curColumn)) {
                Chessboard.this.fields[targetLowerRow - 'a'][curColumn].mark();
            }

            // upper row
            if (Chessboard.this.isValidField(targetUpperrRow, targetRightColumn)) {
                Chessboard.this.fields[targetUpperrRow - 'a'][targetRightColumn].mark();
            }

            if (Chessboard.this.isValidField(targetUpperrRow, targetLeftColumn)) {
                Chessboard.this.fields[targetUpperrRow - 'a'][targetLeftColumn].mark();
            }

            if (Chessboard.this.isValidField(targetUpperrRow, curColumn)) {
                Chessboard.this.fields[targetUpperrRow - 'a'][curColumn].mark();
            }

            // right square of king
            if (Chessboard.this.isValidField(row, targetRightColumn)) {
                Chessboard.this.fields[row - 'a'][targetRightColumn].mark();
            }

            // left square of king
            if (Chessboard.this.isValidField(row, targetLeftColumn)) {
                Chessboard.this.fields[row - 'a'][targetLeftColumn].mark();
            }

        }

        public void unmarkReachableFields() {
            int currentColumn = column - 1;
            char targetLowerRow = (char) (row + 1);
            char targetUpperrRow = (char) (row - 1);
            byte targetRightColumn = (byte) column;
            byte targetLeftColumn = (byte) (column - 2);
            byte curColumn = (byte) currentColumn;

            // lower row
            if (Chessboard.this.isValidField(targetLowerRow, targetRightColumn)) {
                Chessboard.this.fields[targetLowerRow - 'a'][targetRightColumn].unmark();
            }

            if (Chessboard.this.isValidField(targetLowerRow, targetLeftColumn)) {
                Chessboard.this.fields[targetLowerRow - 'a'][targetLeftColumn].unmark();
            }

            if (Chessboard.this.isValidField(targetLowerRow, curColumn)) {
                Chessboard.this.fields[targetLowerRow - 'a'][curColumn].unmark();
            }

            // upper row
            if (Chessboard.this.isValidField(targetUpperrRow, targetRightColumn)) {
                Chessboard.this.fields[targetUpperrRow - 'a'][targetRightColumn].unmark();
            }

            if (Chessboard.this.isValidField(targetUpperrRow, targetLeftColumn)) {
                Chessboard.this.fields[targetUpperrRow - 'a'][targetLeftColumn].unmark();
            }

            if (Chessboard.this.isValidField(targetUpperrRow, curColumn)) {
                Chessboard.this.fields[targetUpperrRow - 'a'][curColumn].unmark();
            }

            // right square of king
            if (Chessboard.this.isValidField(row, targetRightColumn)) {
                Chessboard.this.fields[row - 'a'][targetRightColumn].unmark();
            }

            // left square of king
            if (Chessboard.this.isValidField(row, targetLeftColumn)) {
                Chessboard.this.fields[row - 'a'][targetLeftColumn].unmark();
            }
        }
    }
}



