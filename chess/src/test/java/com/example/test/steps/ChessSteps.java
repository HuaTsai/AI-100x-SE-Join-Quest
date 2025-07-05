package com.example.test.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;
import com.example.model.Game;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ChessSteps {
    
    private Game game;
    private boolean moveResult;
    
    @Given("the board is empty except for a {word} {word} at \\({int}, {int})")
    public void theBoardIsEmptyExceptForAPieceAt(String color, String piece, int row, int col) {
        game = new Game();
        game.placePiece(color, piece, row, col);
    }
    
    @Given("the board has:")
    public void theBoardHas(DataTable dataTable) {
        game = new Game();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String pieceInfo = row.get("Piece");
            String position = row.get("Position");
            
            // 解析棋子資訊 (例如: "Red General")
            String[] parts = pieceInfo.split(" ");
            String color = parts[0];
            String piece = parts[1];
            
            // 解析位置 (例如: "(2, 4)")
            String positionStr = position.substring(1, position.length() - 1); // 移除括號
            String[] coords = positionStr.split(", ");
            int pieceRow = Integer.parseInt(coords[0]);
            int pieceCol = Integer.parseInt(coords[1]);
            
            game.placePiece(color, piece, pieceRow, pieceCol);
        }
    }
    
    @When("{word} moves the {word} from \\({int}, {int}) to \\({int}, {int})")
    public void playerMovesThePieceFromTo(String color, String piece, int fromRow, int fromCol, int toRow, int toCol) {
        moveResult = game.makeMove(color, piece, fromRow, fromCol, toRow, toCol);
    }
    
    @Then("the move is legal")
    public void theMoveIsLegal() {
        assertTrue(moveResult, "Expected the move to be legal");
    }
    
    @Then("the move is illegal")
    public void theMoveIsIllegal() {
        assertFalse(moveResult, "Expected the move to be illegal");
    }
    
    @Then("{word} wins immediately")
    public void playerWinsImmediately(String player) {
        // 檢查遊戲是否結束且獲勝者是指定的玩家
        assertTrue(game.isGameOver(), "Game should be over");
        assertEquals(player, game.getWinner(), "Winner should be " + player);
    }
    
    @Then("the game is not over just from that capture")
    public void theGameIsNotOverJustFromThatCapture() {
        // 檢查遊戲是否還沒結束
        assertFalse(game.isGameOver(), "Game should not be over");
    }
}
