
/**
 * Transaction.java This is the class that handles transactions between users 
 * 
 * 01.April.2021
 * @author Tun Pyay Sone LIN
 */

import java.security.*;
import java.util.*;

public class Transactions {
	public String txID; // hash of the transaction
	public PublicKey senderKey; // sender address
	public PublicKey receiverKey; // receiver address
	public double amount; // transaction value
	public byte[] signature; // digital personal signature
	public int txNum = BlockChain.txNum; //transaction number 
	public AList<InputTransactions> inputs = new AList<InputTransactions>(); //inputs list
	public AList<OutputTransactions> outputs = new AList<OutputTransactions>(); //output list 
	

	// Default Constructor
	public Transactions() {

	}

	// Transactions Constructor
	public Transactions(PublicKey sender, PublicKey receiver, double amount, AList<InputTransactions> inputs) {
		this.senderKey = sender;
		this.receiverKey = receiver;
		this.amount = amount;
		this.inputs = inputs;
	}

	/**
	 * This function verifies the signature of transaction and process the transaction. 
	 * 1. verify signature
	 * 2. check if same Tx is in same block 
	 * 3. Loop through inputs list to populate UTXO output
	 * 4. verify Minimum transaction
	 * 5. verify sufficient funds 
	 * 6. If above are verified, add the outputTransactions to UTXOs ledger 
	 * 7. Remove the spent transactions from UTXOs ledger
	 * 
	 * @return - True if transaction is verified
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 */

	public boolean processTransaction() throws InvalidKeyException, SignatureException {
		boolean isVerified = false;

		// verify signature
		if (!verifySignature()) {
			System.out.println("\nSignature is not verified...");
		}

		else { // if signature is verified, process the transaction
			setTxID(calculateTXID()); // calculate and set Transaction ID
			System.out.println("Inputs:" + inputs.getLength());
			for(int i=1; i<=inputs.getLength();i++) {
				System.out.println(inputs.getEntry(i).getOutputTransactionID());
			}
			
			//check if Same Tx in Same Block
			int temp = 1;
			boolean isInputNull = false;
			while(temp <= inputs.getLength() && isInputNull != true) {
				if (BlockChain.UTXOs.getValue(inputs.getEntry(temp).getOutputTransactionID()) == null) {
					System.out.println("*same Tx in same Block(isInputNull set to 'TRUE')\n");
					isInputNull = true; //null means there is Same Tx in Same Block
				}
				temp++;
			}
			
			//if same TX in same Block, re-initiate Transactions input 
			if (isInputNull) {//same as sendFunds functions in Wallet class
				double total = 0;
				String transactionID;
				OutputTransactions outputTransaction;
				Iterator<String> keyIterator = BlockChain.UTXOs.getKeyIterator();
				Iterator<OutputTransactions> valueIterator = BlockChain.UTXOs.getValueIterator();
				AList<InputTransactions> inputsTemp = new AList<InputTransactions>();// create array list for input
				
				// iterate through UTXOs list
				while (keyIterator.hasNext() && valueIterator.hasNext() && total <= amount) {
					transactionID = keyIterator.next();
					outputTransaction = valueIterator.next();

					// add to inputTransaction list if receiver and user public key matches
					if (outputTransaction.getReceiverKey().equals(getSenderKey())) {
						InputTransactions inTX = new InputTransactions(outputTransaction.getOutputTransactionID());
						inputsTemp.add(inTX);
						total += outputTransaction.getAmount(); // loop and add inputs until total = amount
					}
				}
				setInputs(inputsTemp); //set new inputs
			}
			
			// populate UTXO by matching IDs in inputs list from that of UTXOs list
			for (int i = 1; i <= inputs.getLength(); i++) {				
				inputs.getEntry(i).UTXO = BlockChain.UTXOs.getValue(inputs.getEntry(i).getOutputTransactionID());
			}
			//System.out.println(totalInputTransactions() + " - " + getAmount());
			if (getAmount() < BlockChain.minimumTransaction) {//verify Minimum transaction
				System.out.println("Invalid! (Minimum transaction must be: " + BlockChain.minimumTransaction + ")");
			}
			else if (totalInputTransactions() < getAmount()) {//verify Sufficient funds 
				System.out.println("Insufficient funds!");
			} 
			else {	//if verified, continue
				isVerified = true;
				double leftOver = totalInputTransactions() - getAmount(); // remaining change 
				OutputTransactions outputReceiver = new OutputTransactions(getReceiverKey(), getAmount(), getTxID());
				OutputTransactions outputSender = new OutputTransactions(getSenderKey(), leftOver, getTxID());
				
				outputs.add(outputReceiver); // send amount to receiver
				outputs.add(outputSender); // return remaining change to sender

				for (int i = 1; i <= outputs.getLength(); i++) { // add the outputs list to UTXOs list
					BlockChain.UTXOs.add(outputs.getEntry(i).getOutputTransactionID(), outputs.getEntry(i));
				}
				for (int i = 1; i <= inputs.getLength(); i++) { // remove spent transactions from UTXOs list
					BlockChain.UTXOs.remove(inputs.getEntry(i).getOutputTransactionID());
				}
			}
		}
		return isVerified;
	}

	/**
	 * This function gets total input UTXO in a transaction
	 * 
	 * @return - total UTXO
	 */
	public double totalInputTransactions() {
		double totalInputAmount = 0;
		
		for (int i = 1; i <= inputs.getLength(); i++) {
			if(inputs.getEntry(i).UTXO == null) continue; // if Transaction can't be found, skip it
			totalInputAmount += inputs.getEntry(i).UTXO.amount;
		}
		return totalInputAmount;
	}

	/**
	 * This function gets total output UTXO in a transactions
	 * @return
	 */
	public double totalOutputTransactions() {
		double totalOutput = 0;
		
		for (int i = 1; i <= outputs.getLength(); i++) {
			totalOutput += outputs.getEntry(i).getAmount();
		}
		return totalOutput;
	}

	/**
	 * This function calculates hash of txID by using SHA256
	 * @return
	 */
	public String calculateTXID() {
		String txID_hash = "";
		String hash = "";
		
		setTxNum(BlockChain.txNum++); //add txNum after every transaction 
		System.out.println("\nTransaction number: " + txNum);
		hash = Wallet.getStringFromKey(getSenderKey()) + Wallet.getStringFromKey(getReceiverKey())
				+ Double.toString(getAmount()) + Integer.toString(getTxNum());
		txID_hash = Sha256.getSHA(hash); //calculate hash
		
		return txID_hash;
	}

	/**
	 * This function create and set a digital signature using the sender's private
	 * key
	 * 
	 * @param privateKey
	 *            - private key of the sender
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public void generateSignature(PrivateKey privateKey) throws InvalidKeyException, SignatureException {
		ECSignature ECSign = new ECSignature(); // call constructor from ECSignature

		String data = Wallet.getStringFromKey(getSenderKey()) + Wallet.getStringFromKey(getReceiverKey())
				+ Double.toString(getAmount());

		setSignature(ECSign.getECSignature(privateKey, data)); // create and set the digital signature
	}

	/**
	 * This function verifies the digital signature using the sender's public key
	 * 
	 * @param publicKey
	 *            - public key of the sender
	 * @return - true if the digital signature is verified
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public boolean verifySignature() throws InvalidKeyException, SignatureException {
		ECSignature ECSign = new ECSignature();
		String data = Wallet.getStringFromKey(getSenderKey()) + Wallet.getStringFromKey(getReceiverKey())
				+ Double.toString(getAmount());

		return ECSign.VerifyECSignature(getSenderKey(), data, getSignature()); // Verify the digital signature
	}

	/**
	 * ACCESSORS
	 */
	public String getTxID() {
		return this.txID;
	}

	public PublicKey getSenderKey() {
		return this.senderKey;
	}

	public PublicKey getReceiverKey() {
		return this.receiverKey;
	}

	public double getAmount() {
		return this.amount;
	}

	public byte[] getSignature() {
		return this.signature;
	}
	
	public int getTxNum() {
		return txNum;
	}
	
	public AList<InputTransactions> getInputs() {
		return this.inputs;
	}
	
	public AList<OutputTransactions> getOutputs() {
		return this.outputs;
	}

	/**
	 * MUTATORS
	 */
	public void setTxID(String txID) {
		this.txID = txID;
	}

	public void setSender(PublicKey senderKey) {
		this.senderKey = senderKey;
	}

	public void setReceiver(PublicKey receiverKey) {
		this.receiverKey = receiverKey;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
	
	public void setTxNum(int txNum) {
		this.txNum = txNum;
	}
	
	public void setInputs(AList<InputTransactions> inputs) {
		this.inputs = inputs;
	}
	
	public void setOutputs(AList<OutputTransactions> outputs) {
		this.outputs = outputs;
	}
}
