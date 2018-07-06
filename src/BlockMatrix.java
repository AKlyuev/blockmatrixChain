import java.util.Arrays;

public class BlockMatrix {

    private int dimension;
    private int inputCount = 0;
    private Block[][] blockData;
    private String[] rowHashes;
    private String[] columnHashes;

    public BlockMatrix(int dimension) {
        this.dimension = dimension;
        this.blockData = new Block[dimension][dimension];
        this.rowHashes = new String[dimension];
        this.columnHashes = new String[dimension];
        for (int i = 0; i < dimension; i++) {
            updateRowHash(i);
            updateColumnHash(i);
        }
    }

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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Block[] row: blockData) {
            sb.append(String.format("%5s", Arrays.toString(row)));
            sb.append("\n");
        }
        return sb.toString();
    }

    public void fillDiagonalZeros() {
        for (int i = 0; i < dimension; i++) {
            blockData[i][i] = new Block("0");
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
                sb.append(blockData[row][column].getData());
            }
        }
        return StringUtil.applySha256(sb.toString());
    }

    public String calculateColumnHash(int column) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < dimension; row++) {
            if (row != column && blockData[row][column] != null) {
                sb.append(blockData[row][column].getData());
            }
        }
        return StringUtil.applySha256(sb.toString());
    }

    public void printRowHashes() {
        System.out.println("----------------------------------------------------------------");
        for (int i = 0; i < rowHashes.length; i++) {
            System.out.println(rowHashes[i]);
        }
        System.out.println("----------------------------------------------------------------\n");
    }

    public void printColumnHashes() {
        System.out.println("----------------------------------------------------------------");
        for (int i = 0; i < columnHashes.length; i++) {
            System.out.println(columnHashes[i]);
        }
        System.out.println("----------------------------------------------------------------\n");
    }

    public Block getBlock(int blockNumber) {
        return blockData[getBlockRowIndex(blockNumber)][getBlockColumnIndex(blockNumber)];
    }

    public String getBlockData(int blockNumber) {
        return getBlock(blockNumber).getData();
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

    public void deleteBlock(int blockNumber, int difficulty) {
        int row = getBlockRowIndex(blockNumber);
        int column = getBlockColumnIndex(blockNumber);
        Block deleteBlock = new Block("DELETED");
        deleteBlock.mineBlock(difficulty);
        blockData[row][column]  = deleteBlock;
        updateRowHash(row);
        updateColumnHash(column);
    }
}
