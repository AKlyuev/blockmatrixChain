package blockmatrix;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    public String transactionId; // also the hash of the transaction.
    public PublicKey sender; //senders address/public key
    public PublicKey recipient; // Recipients address/public key.
    public float value;
    private String info;
    public byte[] signature; // Prevents other people from spending funds in our wallet

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    // Constructor:
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs, String info) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
        this.info = info;
    }

    // This Calculates the transaction hash (which will be used as its Id)
    private String calculateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) + info + sequence
        );
    }

    //Signs all the data we dont wish to be tampered with.
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value)	;
        signature = StringUtil.applyECDSASig(privateKey,data);
    }

    //Verifies the data we signed hasnt been tampered with
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value)	;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction() {

        if (verifySignature() == false) {
            System.out.println("Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        for(TransactionInput i : inputs) {
            i.UTXO = BlockMatrix.UTXOs.get(i.transactionOutputId);
        }

        //check if transaction is valid:
        if(getInputsValue() < BlockMatrix.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //generate transaction outputs:
        float leftOver = getInputsValue() - value; // gets the left over "change"
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId)); // send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); // send the left over "change" back to the sender

        //add outputs to UTXO list
        for (TransactionOutput o : outputs) {
            BlockMatrix.UTXOs.put(o.id, o);
        }

        //remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue; // if transaction can't be found, skip it
            BlockMatrix.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    //returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            total += i.UTXO.value;
        }
        return total;
    }

    //returns sum of outputs:
    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }

    public void clearInfo() {
        this.info = "CLEARED";
        this.transactionId =  calculateHash();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nTransaction Details");
        sb.append("\ntransactionId: ");
        sb.append(transactionId);
        sb.append("\nsender: ");
        sb.append(sender);
        sb.append("\nrecipient: ");
        sb.append(recipient);
        sb.append("\nvalue: ");
        sb.append(value);
        sb.append("\ninfo: ");
        sb.append(info);
        sb.append("\nsignature: ");
        sb.append(signature);
        return sb.toString();
    }

}