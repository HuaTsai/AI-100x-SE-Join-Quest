package com.example.model;

public class Game {
    
    private String[][] board;
    private static final int ROWS = 10;
    private static final int COLS = 9;
    private boolean gameOver;
    private String winner;
    
    public Game() {
        // 初始化 10x9 的棋盤
        board = new String[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = null;
            }
        }
    }
    
    public void placePiece(String color, String piece, int row, int col) {
        // 將棋子放置在指定位置 (row, col)
        // 注意：我們使用 1-based 索引，所以需要轉換為 0-based
        board[row - 1][col - 1] = color + " " + piece;
    }
    
    public boolean makeMove(String color, String piece, int fromRow, int fromCol, int toRow, int toCol) {
        // 檢查移動是否合法
        boolean isLegal = isMoveLegal(color, piece, fromRow, fromCol, toRow, toCol);
        
        if (isLegal) {
            // 檢查是否是攻擊
            String targetPiece = board[toRow - 1][toCol - 1];
            boolean isCapture = targetPiece != null;
            boolean capturedGeneral = false;
            
            if (isCapture && targetPiece.endsWith("General")) {
                capturedGeneral = true;
            }
            
            // 執行移動
            board[fromRow - 1][fromCol - 1] = null;
            board[toRow - 1][toCol - 1] = color + " " + piece;
            
            // 設定遊戲狀態
            if (capturedGeneral) {
                this.gameOver = true;
                this.winner = color;
            }
        }
        
        return isLegal;
    }
    
    // 根據 BDD.md 的要求，所有判定邏輯都存放在 Game 中
    public boolean isMoveLegal(String color, String piece, int fromRow, int fromCol, int toRow, int toCol) {
        // 檢查基本邊界
        if (fromRow < 1 || fromRow > ROWS || fromCol < 1 || fromCol > COLS ||
            toRow < 1 || toRow > ROWS || toCol < 1 || toCol > COLS) {
            return false;
        }
        
        // 檢查起始位置是否有對應的棋子
        String pieceAt = board[fromRow - 1][fromCol - 1];
        if (pieceAt == null || !pieceAt.equals(color + " " + piece)) {
            return false;
        }
        
        // 根據棋子類型檢查移動規則
        if (piece.equals("General")) {
            return isGeneralMoveLegal(color, fromRow, fromCol, toRow, toCol);
        } else if (piece.equals("Guard")) {
            return isGuardMoveLegal(color, fromRow, fromCol, toRow, toCol);
        } else if (piece.equals("Rook")) {
            return isRookMoveLegal(color, fromRow, fromCol, toRow, toCol);
        } else if (piece.equals("Horse")) {
            return isHorseMoveLegal(color, fromRow, fromCol, toRow, toCol);
        } else if (piece.equals("Cannon")) {
            return isCannonMoveLegal(color, fromRow, fromCol, toRow, toCol);
        } else if (piece.equals("Elephant")) {
            return isElephantMoveLegal(color, fromRow, fromCol, toRow, toCol);
        } else if (piece.equals("Soldier")) {
            return isSoldierMoveLegal(color, fromRow, fromCol, toRow, toCol);
        }
        
        return false;
    }
    
    private boolean isGeneralMoveLegal(String color, int fromRow, int fromCol, int toRow, int toCol) {
        // 將（帥）的移動規則：
        // 1. 只能在九宮格內移動
        // 2. 每次只能移動一格
        // 3. 只能水平或垂直移動
        // 4. 將帥不能照面（同一直線上且中間無子）
        
        // 檢查是否在九宮格內
        if (color.equals("Red")) {
            // 紅方九宮格：row 1-3, col 4-6
            if (fromRow < 1 || fromRow > 3 || fromCol < 4 || fromCol > 6 ||
                toRow < 1 || toRow > 3 || toCol < 4 || toCol > 6) {
                return false;
            }
        } else {
            // 黑方九宮格：row 8-10, col 4-6
            if (fromRow < 8 || fromRow > 10 || fromCol < 4 || fromCol > 6 ||
                toRow < 8 || toRow > 10 || toCol < 4 || toCol > 6) {
                return false;
            }
        }
        
        // 檢查是否只移動一格
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        
        // 只能水平或垂直移動一格
        if (!((rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1))) {
            return false;
        }
        
        // 檢查將帥照面規則
        if (wouldGeneralsFaceEachOther(color, toRow, toCol)) {
            return false;
        }
        
        return true;
    }
    
    private boolean wouldGeneralsFaceEachOther(String movingColor, int newRow, int newCol) {
        // 找到另一個將的位置
        String otherColor = movingColor.equals("Red") ? "Black" : "Red";
        int[] otherGeneralPos = findGeneralPosition(otherColor);
        
        if (otherGeneralPos == null) {
            return false; // 如果沒有找到另一個將，則不會照面
        }
        
        int otherRow = otherGeneralPos[0];
        int otherCol = otherGeneralPos[1];
        
        // 檢查是否在同一列（同一file）
        if (newCol == otherCol) {
            // 檢查兩將之間是否有其他棋子
            int startRow = Math.min(newRow, otherRow) + 1;
            int endRow = Math.max(newRow, otherRow) - 1;
            
            for (int r = startRow; r <= endRow; r++) {
                if (board[r - 1][newCol - 1] != null) {
                    return false; // 中間有棋子，不會照面
                }
            }
            return true; // 同一列且中間無子，會照面
        }
        
        return false; // 不在同一列，不會照面
    }
    
    private int[] findGeneralPosition(String color) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                String piece = board[row][col];
                if (piece != null && piece.equals(color + " General")) {
                    return new int[]{row + 1, col + 1}; // 返回 1-based 座標
                }
            }
        }
        return null;
    }
    
    private boolean isGuardMoveLegal(String color, int fromRow, int fromCol, int toRow, int toCol) {
        // 士的移動規則：
        // 1. 只能在九宮格內移動
        // 2. 每次只能斜著移動一格
        
        // 檢查是否在九宮格內
        if (color.equals("Red")) {
            // 紅方九宮格：row 1-3, col 4-6
            if (fromRow < 1 || fromRow > 3 || fromCol < 4 || fromCol > 6 ||
                toRow < 1 || toRow > 3 || toCol < 4 || toCol > 6) {
                return false;
            }
        } else {
            // 黑方九宮格：row 8-10, col 4-6
            if (fromRow < 8 || fromRow > 10 || fromCol < 4 || fromCol > 6 ||
                toRow < 8 || toRow > 10 || toCol < 4 || toCol > 6) {
                return false;
            }
        }
        
        // 檢查是否斜著移動一格
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        
        // 必須是斜著移動一格
        if (rowDiff == 1 && colDiff == 1) {
            return true;
        }
        
        return false;
    }
    
    private boolean isRookMoveLegal(String color, int fromRow, int fromCol, int toRow, int toCol) {
        // 車的移動規則：
        // 1. 只能水平或垂直移動
        // 2. 路徑上不能有其他棋子
        
        // 檢查是否是水平或垂直移動
        if (fromRow != toRow && fromCol != toCol) {
            return false; // 不是直線移動
        }
        
        // 檢查路徑是否暢通
        return isPathClear(fromRow, fromCol, toRow, toCol);
    }
    
    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        // 檢查從起點到終點的路徑是否暢通（不包括起點和終點）
        
        if (fromRow == toRow) {
            // 水平移動
            int startCol = Math.min(fromCol, toCol) + 1;
            int endCol = Math.max(fromCol, toCol) - 1;
            
            for (int col = startCol; col <= endCol; col++) {
                if (board[fromRow - 1][col - 1] != null) {
                    return false; // 路徑被阻擋
                }
            }
        } else {
            // 垂直移動
            int startRow = Math.min(fromRow, toRow) + 1;
            int endRow = Math.max(fromRow, toRow) - 1;
            
            for (int row = startRow; row <= endRow; row++) {
                if (board[row - 1][fromCol - 1] != null) {
                    return false; // 路徑被阻擋
                }
            }
        }
        
        return true; // 路徑暢通
    }

    private boolean isHorseMoveLegal(String color, int fromRow, int fromCol, int toRow, int toCol) {
        // 馬的移動規則：
        // 1. 走日字形（L形）
        // 2. 馬腳不能被擋住
        
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        
        // 檢查是否是L形移動
        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) {
            return false;
        }
        
        // 檢查馬腳是否被擋住
        int blockRow, blockCol;
        
        if (rowDiff == 2) {
            // 垂直移動較大，馬腳在垂直方向
            blockRow = fromRow + (toRow - fromRow) / 2;
            blockCol = fromCol;
        } else {
            // 水平移動較大，馬腳在水平方向
            blockRow = fromRow;
            blockCol = fromCol + (toCol - fromCol) / 2;
        }
        
        // 檢查馬腳位置是否有棋子
        if (board[blockRow - 1][blockCol - 1] != null) {
            return false; // 馬腳被擋住
        }
        
        return true;
    }

    private boolean isCannonMoveLegal(String color, int fromRow, int fromCol, int toRow, int toCol) {
        // 炮的移動規則：
        // 1. 只能水平或垂直移動
        // 2. 不攻擊時：路徑必須暢通（像車一樣）
        // 3. 攻擊時：必須跳過恰好一個棋子（作為炮台）
        
        // 檢查是否是水平或垂直移動
        if (fromRow != toRow && fromCol != toCol) {
            return false; // 不是直線移動
        }
        
        // 檢查目標位置是否有棋子（決定是移動還是攻擊）
        String targetPiece = board[toRow - 1][toCol - 1];
        
        if (targetPiece == null) {
            // 移動：路徑必須暢通
            return isPathClear(fromRow, fromCol, toRow, toCol);
        } else {
            // 攻擊：必須跳過恰好一個棋子
            return hasExactlyOneScreenInPath(fromRow, fromCol, toRow, toCol);
        }
    }
    
    private boolean hasExactlyOneScreenInPath(int fromRow, int fromCol, int toRow, int toCol) {
        // 計算路徑上的棋子數量（不包括起點和終點）
        int screenCount = 0;
        
        if (fromRow == toRow) {
            // 水平移動
            int startCol = Math.min(fromCol, toCol) + 1;
            int endCol = Math.max(fromCol, toCol) - 1;
            
            for (int col = startCol; col <= endCol; col++) {
                if (board[fromRow - 1][col - 1] != null) {
                    screenCount++;
                }
            }
        } else {
            // 垂直移動
            int startRow = Math.min(fromRow, toRow) + 1;
            int endRow = Math.max(fromRow, toRow) - 1;
            
            for (int row = startRow; row <= endRow; row++) {
                if (board[row - 1][fromCol - 1] != null) {
                    screenCount++;
                }
            }
        }
        
        return screenCount == 1; // 恰好一個炮台
    }

    private boolean isElephantMoveLegal(String color, int fromRow, int fromCol, int toRow, int toCol) {
        // 象的移動規則：
        // 1. 只能斜著移動兩格
        // 2. 不能過河
        // 3. 象眼不能被擋住
        
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        
        // 檢查是否是斜著移動兩格
        if (rowDiff != 2 || colDiff != 2) {
            return false;
        }
        
        // 檢查是否過河
        if (color.equals("Red")) {
            // 紅方不能過河到 row > 5
            if (toRow > 5) {
                return false;
            }
        } else {
            // 黑方不能過河到 row < 6
            if (toRow < 6) {
                return false;
            }
        }
        
        // 檢查象眼是否被擋住
        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;
        
        if (board[midRow - 1][midCol - 1] != null) {
            return false; // 象眼被擋住
        }
        
        return true;
    }
    
    private boolean isSoldierMoveLegal(String color, int fromRow, int fromCol, int toRow, int toCol) {
        // 兵的移動規則：
        // 1. 過河前只能向前走一格
        // 2. 過河後可以向前或左右走一格，但不能後退
        
        int rowDiff = toRow - fromRow;
        int colDiff = Math.abs(toCol - fromCol);
        
        if (color.equals("Red")) {
            // 紅兵向上（增加 row）移動
            if (fromRow <= 5) {
                // 未過河：只能向前一格
                return rowDiff == 1 && colDiff == 0;
            } else {
                // 已過河：可以向前或左右一格
                return ((rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1));
            }
        } else {
            // 黑兵向下（減少 row）移動
            if (fromRow >= 6) {
                // 未過河：只能向前一格
                return rowDiff == -1 && colDiff == 0;
            } else {
                // 已過河：可以向前或左右一格
                return ((rowDiff == -1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1));
            }
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }
    
    public String getWinner() {
        return winner;
    }
}
