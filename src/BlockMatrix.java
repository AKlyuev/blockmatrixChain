import com.google.gson.GsonBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BlockMatrix {

    private int dimension; // block matrix is size dimension^2
    private int inputCount = 0; // how many Blocks have been added to the block matrix
    private boolean deletionValidity; // whether or not all deletions have been valid. Used to check blockmatrix validity
    private Block[][] blockData;
    private String[] rowHashes;
    private String[] columnHashes;
    private ArrayList<Integer> blocksWithModifiedData;

    public BlockMatrix(int dimension) {
        if (dimension < 2) {
            throw new IllegalArgumentException("BlockMatrix must have dimensions of at least 2x2.");
        }
        this.dimension = dimension;
        this.blockData = new Block[dimension][dimension];
        this.rowHashes = new String[dimension];
        this.columnHashes = new String[dimension];
        this.deletionValidity = true;
        blocksWithModifiedData = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            updateRowHash(i);
            updateColumnHash(i);
        }
    }

    //adds a block to our blockmatrix
    public void add(Block block) {
        inputCount++;
        if (inputCount > (dimension* dimension) - dimension) { //no more space in the matrix
            inputCount--;
            System.out.println("Error: Addition of " + block.toString() + " to BlockMatrix failed, no more space");
            return;
        }

        //Insertion location code gotten from block matrix paper
        if (inputCount % 2 == 0) { // Block count is even
            int s = (int) Math.floor(Math.sqrt(inputCount));
            int i = (inputCount <= s*s + s) ? s : s + 1;
            int j = (inputCount - (i*i - i + 2))/2;
            blockData[i][j] = block;
            updateRowHash(i);
            updateColumnHash(j);
            
        } else { // Block count is odd
            int s = (int) Math.floor(Math.sqrt(inputCount + 1));
            int j = (inputCount < s*s + s) ? s: s + 1;
            int i = (inputCount - (j*j - j + 1))/2;
            blockData[i][j] = block;
            updateRowHash(i);
            updateColumnHash(j);
        }
    }

    public Block getBlock(int blockNumber) {
        return blockData[getBlockRowIndex(blockNumber)][getBlockColumnIndex(blockNumber)];
    }

    //gets all transactions in a certain block
    public ArrayList<Transaction> getBlockTransactions(int blockNumber) {
        return getBlock(blockNumber).getTransactions();
    }

    public String getBlockData(int blockNumber) {
        String transactionsJson = new GsonBuilder().setPrettyPrinting().create().toJson(getBlock(blockNumber).getTransactions());
        return transactionsJson;
    }

    //the "delete" function, which will overwrite any message info passed in along with the transaction for every transaction in the block
    public void clearInfoInBlock(int blockNumber) {
        this.blocksWithModifiedData.add(blockNumber);
        int row = getBlockRowIndex(blockNumber);
        int column = getBlockColumnIndex(blockNumber);
        getBlock(blockNumber).clearInfoInTransactionsInBlock();
        String[] prevRowHashes = this.getRowHashes().clone();
        String[] prevColumnHashes = this.getColumnHashes().clone();
        updateRowHash(row);
        updateColumnHash(column);
        String[] newRowHashes = this.getRowHashes().clone();
        String[] newColumnHashes = this.getColumnHashes().clone();
        if (!checkValidDeletion(prevRowHashes, prevColumnHashes, newRowHashes, newColumnHashes)) {
            System.out.println("Bad deletion, more than one row and column hash affected");
            deletionValidity = false; // This might be better as something that throws an exception.
        }
    }


    //Uses data in each block in the row except those that are null and those in the diagonal
    private void updateRowHash(int row) {
        rowHashes[row] =  calculateRowHash(row);
    }

    //Uses data in each block in the column except those that are null and those in the diagonal
    private void updateColumnHash(int column) {
        columnHashes[column] = calculateColumnHash(column);
    }

    public String calculateRowHash(int row) {
        StringBuilder sb = new StringBuilder();
        for (int column = 0; column < dimension; column++) {
            if (row != column && blockData[row][column] != null) {
                sb.append(blockData[row][column].getHash());
            }
        }
        return StringUtil.applySha256(sb.toString());
    }

    public String calculateColumnHash(int column) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < dimension; row++) {
            if (row != column && blockData[row][column] != null) {
                sb.append(blockData[row][column].getHash());
            }
        }

        return StringUtil.applySha256(sb.toString());
    }

    // helper method to get the row of a block given a block number
    private int getBlockRowIndex(int blockNumber) {
        if (blockNumber % 2 == 0) { // Block number is even
            int s = (int) Math.floor(Math.sqrt(blockNumber));
            int row = (blockNumber <= s*s + s) ? s : s + 1;
            return row;
        } else { // Block count is odd
            int s = (int) Math.floor(Math.sqrt(blockNumber + 1));
            int column = (blockNumber < s*s + s) ? s: s + 1;
            int row = (blockNumber - (column*column - column + 1))/2;
            return row;
        }
    }

    //helper method to get the column of a block given a block number
    private int getBlockColumnIndex(int blockNumber) {
        if (blockNumber % 2 == 0) { // Block number is even
            int s = (int) Math.floor(Math.sqrt(blockNumber));
            int row = (blockNumber <= s*s + s) ? s : s + 1;
            int column = (blockNumber - (row*row - row + 2))/2;
            return column;
        } else { // Block number is odd
            int s = (int) Math.floor(Math.sqrt(blockNumber + 1));
            int column = (blockNumber < s*s + s) ? s: s + 1;
            return column;
        }
    }

    //tests to make sure only one row hash and one column hash have been modified. If not, then integrity is likely compromised
    private boolean checkValidDeletion(String[] prevRow, String[] prevCol, String[] newRow, String[] newCol) {
        int numRowChanged = 0;
        int numColChanged = 0;
        for (int i = 0; i < dimension; i++) {
            if (!prevRow[i].equals(newRow[i])) {
                numRowChanged++;
            }
            if (!prevCol[i].equals(newCol[i])) {
                numColChanged++;
            }
        }
        System.out.println("numRowChanged = " + numRowChanged);
        System.out.println("numColChanged = " + numColChanged);
        if (numRowChanged != 1 || numColChanged != 1) {
            return false;
        }
        return true;
    }

    //gets the number of blocks that have been entered
    public int getInputCount() {
        return inputCount;
    }

    public String[] getRowHashes() {
        return rowHashes;
    }

    public String[] getColumnHashes() {
        return columnHashes;
    }

    public int getDimension() {
        return dimension;
    }

    public boolean getDeletionValidity() {
        return this.deletionValidity;
    }

    //returns a list of blocks for which data has been modified
    public ArrayList<Integer> getBlocksWithModifiedData() {
        Collections.sort(this.blocksWithModifiedData);
        return this.blocksWithModifiedData;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Block[] row: blockData) {
            sb.append(String.format("%5s", Arrays.toString(row)));
            sb.append("\n");
        }
        return sb.toString();
    }

    public void printRowHashes() {
        System.out.println("\nRow hashes:");
        System.out.println("----------------------------------------------------------------");
        for (int i = 0; i < rowHashes.length; i++) {
            System.out.println(rowHashes[i]);
        }
        System.out.println("----------------------------------------------------------------\n");
    }

    public void printColumnHashes() {
        System.out.println("\nColumn hashes:");
        System.out.println("----------------------------------------------------------------");
        for (int i = 0; i < columnHashes.length; i++) {
            System.out.println(columnHashes[i]);
        }
        System.out.println("----------------------------------------------------------------\n");
    }

    public void printHashes() {
        printRowHashes();
        printColumnHashes();
    }

}
