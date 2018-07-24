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

        //Create wallets:
        walletA = new Wallet();

        bm.generate(walletA, 200f);
        System.out.println(bm);
        walletB = new Wallet();

        //testing
        Block block2 = new Block();
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 40f, "Here is 40 coins!"));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        System.out.println(bm.getBlockTransactions(2));

        Block block3 = new Block();
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block3.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f, "This might be too many..."));
        addBlock(block3);
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
        addBlock(block5);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block6 = new Block();
        //Transaction tr = walletA.sendFunds(walletB.publicKey, 30f, "Here is 30 coins back.");
        block6.addTransaction(walletA.sendFunds(walletB.publicKey, 40f, "Here is another 40 coins."));
        addBlock(block6);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());


        System.out.println(bm.getBlocksWithModifiedData()); // Tells you which blocks have been modified

        System.out.println("\nMatrix is Valid: " + bm.isMatrixValid());

        System.out.println(bm.toString());

    }


    //NOT COMPLETE, just similar to what the Medium tutorial did, probably doesn't definitively decide validity
    public static Boolean isMatrixValid() {
        Block currentBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //loop through matrix to check block hashes:
        for (int i = 2; i < bm.getInputCount(); i++) { // start at 1 because we want to skip the genesis transaction
            currentBlock = bm.getBlock(i);
            HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
            tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
            //compare registered hash and calculated hash:
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("currentBlock.getHash() = " + currentBlock.getHash());
                System.out.println("currentBlock.calculateHash() = " + currentBlock.calculateHash());
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

            //loop through blockchains transactions:
            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.getTransactions().size(); t++) {
                Transaction currentTransaction = currentBlock.getTransactions().get(t);

                if(!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are not equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") in Block(" + i + ") is Missing");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
                    System.out.println("#Transaction(" + t + ") output recipient is not who it should be");
                    return false;
                }
                if( currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sent to sender.");
                    return false;
                }

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

        //check if all deletions have been valid
        if (!bm.getDeletionValidity()) {
            System.out.println("One or more deletions were not valid and altered more than one row and column hash");
            return false;
        }

        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock();
        bm.add(newBlock);
    }

}
