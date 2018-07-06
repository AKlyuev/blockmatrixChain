import java.sql.SQLOutput;

public class MatrixChain {

    private static BlockMatrix bm;
    public static int difficulty = 3;

    public static void main(String[] args) {
        bm = new BlockMatrix(5);

        //add our blocks to the BlockMatrix:

        bm.add(new Block("First"));
        System.out.println("Trying to mine block 1... ");
        bm.getBlock(1).mineBlock(difficulty);

        bm.add(new Block("Second"));
        System.out.println("Trying to mine block 2... ");
        bm.getBlock(2).mineBlock(difficulty);

        bm.add(new Block("Third"));
        System.out.println("Trying to mine block 3... ");
        bm.getBlock(3).mineBlock(difficulty);

        System.out.println("\nOur block matrix:\n" + bm);
        System.out.println("\nMatrix is valid: " + isMatrixValid());

    }


    //NOT COMPLETE, just similar to what the Medium tutorial did, probably doesn't definitively decide validity
    public static Boolean isMatrixValid() {
        Block currentBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //loop through matrix to check block hashes:
        for (int i = 1; i < bm.getInputCount(); i++) {
            currentBlock = bm.getBlock(i);
            //compare registered hash and calculated hash:
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Hashes for Block " + i + "not equal (first instance of block with unequal hashes, there may be more)");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.getHash().substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("Block " + i +  " hasn't been mined (first instance of unmined block, there may be more)");
                return false;
            }
        }

        //check if all row hashes are valid
        for (int i = 0; i < bm.getDimension(); i++) {
            if (!bm.calculateRowHash(i).equals(bm.getRowHashes()[i])) {
                System.out.println("Row hashes for row " + i + " not equal (first instance of row with unequal hashes, there may be more");
                return false;
            }
        }

        //check if all column hashes are valid
        for (int i = 0; i < bm.getDimension(); i++) {
            if (!bm.calculateColumnHash(i).equals(bm.getColumnHashes()[i])) {
                System.out.println("Column hashes for row " + i +  " not equal (first instance of column with unequal hashes, there may be more");
                return false;
            }
        }

        return true;
    }

}
