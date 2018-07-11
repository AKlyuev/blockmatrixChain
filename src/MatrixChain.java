import java.sql.SQLOutput;
import java.security.Security;
import java.util.HashMap;

public class MatrixChain {

    private static BlockMatrix bm;
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<>();

    public static int difficulty = 3;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;

    public static void main(String[] args) {

        bm = new BlockMatrix(5);

        //add our blocks to the BlockMatrix:

        bm.add(new Block("First"));
        /**
        System.out.println("Trying to mine block 1... ");
        bm.getBlock(1).mineBlock(difficulty);
         **/

        bm.add(new Block("Second"));
        /**
        System.out.println("Trying to mine block 2... ");
        bm.getBlock(2).mineBlock(difficulty);
         **/

        bm.add(new Block("Third"));
        /**
        System.out.println("Trying to mine block 3... ");
        bm.getBlock(3).mineBlock(difficulty);
         **/

        bm.deleteBlock(2 /**,difficulty**/);

        System.out.println("\nOur block matrix:\n" + bm);
        System.out.println("\nMatrix is valid: " + isMatrixValid());

    /**
        //Setup Bouncey castle as a Security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //Create the new wallets
        walletA = new Wallet();
        walletB = new Wallet();
        //Test public and private keys
        System.out.println("Private and public keys:");
        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
        //Create a test transaction from WalletA to walletB
        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.privateKey);
        //Verify the signature works and verify it from the public key
        System.out.println("Is signature verified");
        System.out.println(transaction.verifiySignature());
    **/
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
                System.out.println("Hashes for Block " + i + " not equal (first instance of block with unequal hashes, there may be more)");
                return false;
            }
            /**
            //check if hash is solved
            if(!currentBlock.getHash().substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("Block " + i +  " hasn't been mined (first instance of unmined block, there may be more)");
                return false;
            }
             **/
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

        //check if all deletions have been valid
        if (!bm.getDeletionValidity()) {
            System.out.println("One or more deletions were not valid and altered more than one row and column hash");
            return false;
        }

        return true;
    }

}
