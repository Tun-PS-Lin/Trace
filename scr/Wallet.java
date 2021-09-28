
/**
 * Wallet.java This is the class that holds private and public keys 
 * That are necessary for transactions
 * 
 * 01.April.2021
 * @author Tun Pyay Sone LIN
 */

import java.security.*;
import java.util.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {
	private PrivateKey privateKey;
	public PublicKey publicKey;

	// Default Constructor
	public Wallet() throws Exception { // calls generateKeypair()
		generateKeyPair();
	}

	/**
	 * This function generate a private & public key for each wallet using
	 * secp256r1
	 * 
	 * @throws Exception
	 */
	public void generateKeyPair() throws Exception {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ECparam = new ECGenParameterSpec("secp256r1");
			
			keyGen.initialize(ECparam, random);
			KeyPair pair = keyGen.generateKeyPair();
			
			privateKey = pair.getPrivate();
			publicKey = pair.getPublic();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This function loop through the UTXOs list and get the wallet's total balance
	 * 
	 * @return - total balance of the wallet 
	 */
	public double getBalance() {
		double totalBalance = 0;
		String transactionID;
		OutputTransactions outputTransaction;
		Iterator<String> keyIterator = BlockChain.UTXOs.getKeyIterator();
		Iterator<OutputTransactions> valueIterator = BlockChain.UTXOs.getValueIterator();

		while (keyIterator.hasNext() && valueIterator.hasNext()) { // iterate through UTXOs list
			transactionID = keyIterator.next();
			outputTransaction = valueIterator.next();
			if (outputTransaction.getReceiverKey().equals(getPublicKey())) {
				totalBalance += outputTransaction.getAmount(); // calculate total balance if key matches
			}
		}
		return totalBalance;
	}

	/**
	 * This function send funds to another user. 
	 * 1. Iterates through the UTXOs list and
	 * 2. Add to inputs list if output UTXO key matches (until total <= amount) 
	 * 3. Create Transaction object 
	 * 
	 * @param senderKey
	 *            - public key of the recipient
	 * @param amount
	 *            - value of fund
	 * @return - Transaction object that would be added to blocks before mining
	 */
	public Transactions sendFunds(PublicKey receiverKey, double amount) {
		double total = 0;
		String transactionID;
		OutputTransactions outputTransaction;
		Iterator<String> keyIterator = BlockChain.UTXOs.getKeyIterator();
		Iterator<OutputTransactions> valueIterator = BlockChain.UTXOs.getValueIterator();

		AList<InputTransactions> inputs = new AList<InputTransactions>();// create array list for input
		
		// iterate through UTXOs list
		while (keyIterator.hasNext() && valueIterator.hasNext() && total <= amount) {
			transactionID = keyIterator.next();
			outputTransaction = valueIterator.next();

			// add to inputTransaction list if receiver and user public key matches
			if (outputTransaction.getReceiverKey().equals(getPublicKey())) {
				InputTransactions inTX = new InputTransactions(outputTransaction.getOutputTransactionID());
				inputs.add(inTX);
				total += outputTransaction.getAmount(); // loop and add inputs until total = amount
			}
		}
		// create new transaction object
		Transactions transaction = new Transactions(getPublicKey(), receiverKey, amount, inputs);
		return transaction;
	}

	/**
	 * this function convert publicKey & privateKey to String
	 * 
	 * @param key
	 *            - key from KeyGenerator
	 * @return - return the String format of Key
	 */
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	/**
	 * ACCESSORS
	 */
	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	/**
	 * MUTATORS
	 */
	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

}
