import java.util.ArrayList;
import java.util.Date;

public class Block {

    private String hash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    private long timeStamp; //number of milliseconds since 1/1/1970
    private int nonce;
    private boolean genesis; // whether or not this block is the genesis block, by default it is not

    //Block Constructor
    public Block() {
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
        this.genesis = false;
    }

    public Block(boolean genesis) {
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
        this.genesis = genesis;
    }

    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                        Long.toString(timeStamp) +
                                Integer.toString(nonce) +
                                merkleRoot
                        );
        return calculatedhash;
    }

    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDifficultyString(difficulty); //Create a string with difficulty * "0"
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    //Add transactions to this block
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if (transaction == null) {
            return false;
        }
        if(!genesis) {
            if((transaction.processTransaction() != true)) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }


    /*public String getData() {
        return data;
    }

    public String toString() {
        return String.format(data);
    }*/



    public String getHash() {
        return hash;
    }

    public void delete() {
        //data = "DELETED";
        hash = calculateHash();
    }

}
