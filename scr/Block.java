import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

/**
 * Block.java 
 * This is the class for blocks that will be added to the blockchain
 * 
 * 01.April.2021
 * @author Tun Pyay Sone LIN
 * 
 */

public class Block {
	private int height; // block number
	private String timeStamp; // shows when the block is mined
	private String data; // Merkel Root of transactions for each block
	private int nonce; // unique number required to mine blocks
	private String hash; // Sha256 using timeStamp + data + nonce
	private String previousHash;
	private AList<Transactions> transactionsList = new AList<Transactions>(); // transactions are stored in arrays

	// Default Constructor
	public Block() {

	}

	// Block Constructor (Single transactions)
	public Block(int height, String data, String previousHash) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();

		this.height = height;
		this.timeStamp = dtf.format(now); // set timeStamp when block is created
		this.data = data;
		this.previousHash = previousHash;
		this.hash = calculateHash(); // calculate and set the hash of block
	}

	// Block Constructor (Multiple transactions)
	public Block(int height, AList<Transactions> transactionsList, String previousHash) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();

		this.height = height;
		this.timeStamp = dtf.format(now);
		// this.data = ;
		this.previousHash = previousHash;
		// this.hash = calculateHash(); // calculate and set hash
		this.transactionsList = transactionsList;
	}

	/**
	 * This function takes previous hash + nonce + data + timeStamp to calculates
	 * hash by calling Sha256 algorithm
	 * 
	 * @return currentHash of the block
	 */
	public String calculateHash() {
		String hash = getPreviousHash() + Integer.toString(getNonce()) + getData() + getTimeStamp();
		String currentHash = Sha256.getSHA(hash);

		return currentHash;
	}

	/**
	 * This function adds single transaction to the block before it is mined
	 * 
	 * @param transactions
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public void addTransactions(Transactions transactions) throws InvalidKeyException, SignatureException {
		if (transactions.processTransaction()) { // process the transaction
			transactionsList.add(transactions);
			System.out.println("Transaction Successful!");
		} else {
			System.out.println("Invalid transaction...");
		}
	}

	/**
	 * This function adds multiple transactions to the block before it is mined
	 * 
	 * @param transactionsList-
	 *            a list of transactions to be added
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public void addTransactions(AList<Transactions> transactionsList) throws InvalidKeyException, SignatureException {
		// System.out.println("Transactions list length: +
		// transactionsList.getLength());
		for (int i = 1; i <= transactionsList.getLength(); i++) { // process all transactions in the list
			if (transactionsList.getEntry(i).processTransaction()) {
				System.out.println("Transaction Successful!");
			} else {
				System.out.println("Invalid transacton...");
			}
		}
	}

	/**
	 * This function mine blocks according to set difficulty by finding the unique
	 * nonce number. Then call mineReward function a block is successfully mined
	 * 
	 * @param difficulty-
	 *            target to be met
	 * @param reward-
	 *            given to miner
	 * @param coinBase
	 * @param minerWallet
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 */
	public void mineBlock(int difficulty, double reward, Wallet coinBase, Wallet minerWallet)
			throws InvalidKeyException, SignatureException {
		int tempNonce = getNonce();
		String target = new String(new char[difficulty]).replace('\0', '0'); // target to be met when mining

		System.out.print("Mining block...");

		while (!hash.substring(0, difficulty).equals(target)) {
			tempNonce++; // add nonce to change the hash
			setNonce(tempNonce);
			setHash(calculateHash()); // recalculate hash until target is met
		}
		System.out.println("Block mined!");
		System.out.println("(Mined by: " + minerWallet.getPublicKey() + ")");
		mineReward(reward, coinBase, minerWallet); // give mine reward to miner
	}

	/**
	 * This function creates transactionReward given to the miner. Add transaction
	 * to UTXOs ledger
	 * 
	 * @param reward
	 * @param coinBase
	 * @param minerWallet
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public void mineReward(double reward, Wallet coinBase, Wallet minerWallet)
			throws InvalidKeyException, SignatureException {
		Transactions transactionReward = new Transactions(coinBase.getPublicKey(), minerWallet.getPublicKey(), reward,
				null);
		OutputTransactions outputs = new OutputTransactions(transactionReward.getReceiverKey(),
				transactionReward.getAmount(), transactionReward.getTxID());

		transactionReward.generateSignature(coinBase.getPrivateKey()); //generate signature of transaction
		transactionReward.outputs.add(outputs);
		BlockChain.UTXOs.add(transactionReward.outputs.getEntry(1).getOutputTransactionID(),
				transactionReward.outputs.getEntry(1)); //add transaction to UTXOs ledger
//		System.out.println("TxID: " + transactionReward.getTxID()); //will always be null, since inputs set to null
		System.out.println("OutputID: " + transactionReward.outputs.getEntry(1).getOutputTransactionID());
		System.out.println("Reward: " + reward + " coins added to wallet");

	}

	/**
	 * ACCESSORS
	 */

	public int getHeight() {
		return this.height;
	}

	public String getTimeStamp() {
		return this.timeStamp;
	}

	public String getData() {
		return this.data;
	}

	public int getNonce() {
		return this.nonce;
	}

	public String getHash() {
		return this.hash;
	}

	public String getPreviousHash() {
		return this.previousHash;
	}

	public AList<Transactions> getTransactionsList() {
		return transactionsList;
	}

	public void printTransactionsList() {
		System.out.println(transactionsList.getLength());
		for (int i = 1; i <= transactionsList.getLength(); i++) {
			System.out.print(transactionsList.getEntry(i).getTxID() + ", ");
		}
	}

	/**
	 * MUTATORS
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}

	public void setHash(String previousHash) {
		this.hash = previousHash; // set current block hash using previous hash
	}

	public void setTransactionsList(AList<Transactions> transactionsList) {
		this.transactionsList = transactionsList;
	}
}
