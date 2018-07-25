package blockmatrix;

import java.security.Security;
import java.util.HashMap;

public class MatrixChain {

    private static BlockMatrix bm;
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    public static int difficulty = 3;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {

        bm = new BlockMatrix(5);
        bm.setUpSecurity();
        bm.setMinimumTransaction(3f);
        System.out.println(bm.minimumTransaction);

        //Create wallets:
        walletA = new Wallet();

        bm.generate(walletA, 200f);
        bm.generate(walletA, 100f);
        System.out.println(bm);
        walletB = new Wallet();

        //testing
        Block block2 = new Block();
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 40f, "Here is 40 coins!"));
        bm.addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        System.out.println(bm.getBlockTransactions(2));

        Block block3 = new Block();
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block3.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f, "This might be too many..."));
        bm.addBlock(block3);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        /**
        Block block4 = new Block();
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block4.addTransaction(walletB.sendFunds( walletA.publicKey, 20f, "This is for the bananas!"));
        addBlock(block4);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        **/

        Block block5 = new Block();
        //Transaction tr = walletA.sendFunds(walletB.publicKey, 30f, "Here is 30 coins back.");
        block5.addTransaction(walletA.sendFunds(walletB.publicKey, 30f, "Here is 30 coins back."));
        bm.addBlock(block5);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block6 = new Block();
        //Transaction tr = walletA.sendFunds(walletB.publicKey, 30f, "Here is 30 coins back.");
        block6.addTransaction(walletA.sendFunds(walletB.publicKey, 40f, "Here is another 40 coins."));
        bm.addBlock(block6);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());


        System.out.println(bm.getBlocksWithModifiedData()); // Tells you which blocks have been modified

        System.out.println("\nMatrix is Valid: " + bm.isMatrixValid());

        System.out.println(bm.toString());

    }



}
