import java.util.Date;

public class Block {
    private String hash;
    private String data;
    private long timeStamp; //number of milliseconds since 1/1/1970
    private int nonce;

    //Block Constructor
    public Block(String data) {
        this.data = data;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                        Long.toString(timeStamp) +
                                Integer.toString(nonce) +
                        data
        );
        return calculatedhash;
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty number of 0s
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Block Mined! : " + hash);
    }

    public String getData() {
        return data;
    }

    public String toString() {
        return String.format(data);
    }

    public String getHash() {
        return hash;
    }

}
