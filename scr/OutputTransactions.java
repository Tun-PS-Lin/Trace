/**
 * OutputTransactions.java 
 * This is the class that holds the transactions amount
 * 	sent to each users 
 * 
 * 01.April.2021
 * @author Tun Pyay Sone LIN
 */

import java.security.*;

public class OutputTransactions {
	String outputID;
	public PublicKey publicKey;
	public double amount;
	public String ParentTxID;
	
	//Default Constructor
	OutputTransactions(){
		
	}
	
	//OutputTransactions Constructor
	OutputTransactions(PublicKey receiverKey, double amount, String ParentTxID){
		this.publicKey = receiverKey;
		this.amount = amount;
		this.ParentTxID = ParentTxID;
		this.outputID = calculateHashID();
	}
	
	/**
	 * This function checks if the transaction is directed toward me(the user)
	 * @param publicKey - publicKey of the sender
	 * @return - true or false
	 */
	public boolean isMine(PublicKey publicKey) {
		return (this.publicKey == publicKey); //return true if receiverKey is equal to publicKey input
	}
	
	/**
	 * this function calculate the hash of output transaction ID
	 * @return - hash ID
	 */
	public String calculateHashID(){	
		return (Sha256.getSHA(Wallet.getStringFromKey(getReceiverKey()) + Double.toString(getAmount()) + getParentTxID()));
	}
	
	/**
	 * ACCESSORS
	 */	
	public String getOutputTransactionID() {
		return this.outputID;
	}
	
	public PublicKey getReceiverKey() {
		return this.publicKey;
	}
	
	public double getAmount() {
		return this.amount;
	}
	
	public String getParentTxID() {
		return ParentTxID;
	}
	
	/**
	 * MUTATORS
	 */
	public void setOutputTransactionID(String outputID) {
		this.outputID = outputID;
	}
	
	public void setReceiverKey(PublicKey receiverKey) {
		this.publicKey = receiverKey;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public void setParentTxID(String ParentTxID) {
		this.ParentTxID = ParentTxID;
	}
}
