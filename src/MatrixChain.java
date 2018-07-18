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
    public static Transaction genesisTransaction;

    public static void main(String[] args) {

        bm = new BlockMatrix(5);

        //add our blocks to the blockchain ArrayList:
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider

        //Create wallets:
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        //create genesis transaction, which sends 100 NoobCoin to walletA:
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction
        genesisTransaction.transactionId = "0"; //manually set the transaction id
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

        System.out.println("Creating and Mining Genesis block... ");
        //Block genesis = new Block("0");
        Block genesis = new Block(true);
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //testing
        //Block block1 = new Block(genesis.hash);
        Block block1 = new Block();
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        //Block block2 = new Block(block1.hash);
        Block block2 = new Block();
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        //Block block3 = new Block(block2.hash);
        Block block3 = new Block();
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        System.out.println(isMatrixValid());

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

    public static void addBlock(Block newBlock) {
        //newBlock.mineBlock(difficulty);
        bm.add(newBlock);
    }

}
