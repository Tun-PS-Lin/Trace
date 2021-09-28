import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Iterator;

/**
 * BlockChain.java
 * This is the class to create and run the blockchain
 * 
 * 01.April.2021
 * @author Tun Pyay Sone LIN
 */

public class BlockChain {

	AList<Block> blockchain = new AList<Block>(); //AList BlockChain
	public static LinkedDictionary<String, OutputTransactions> UTXOs = new LinkedDictionary<String, OutputTransactions>(); //UTXOs Ledger
	String genesisData = "Genesis block"; //initial block data
	String genesisHash = "0"; //initial block hash
	int genesisBlockHeight = 0; //initial block number

	int miningDifficulty = 5; // number of initial '0' in each hash, target to be met when mining blocks
	double miningReward = 5; // reward for each mined block
	static int txNum = 0; //transaction number 
	static double minimumTransaction = 0.1; // lowest amount of coins required to perform a transaction

	// Default Constructor
	public BlockChain() {

	}

	/**
	 * This function creates a Genesis block and runs the blockchain
	 * 
	 * @throws Exception
	 */
	public void main() throws Exception {

		// create Wallets
		Wallet coinBase = new Wallet();
		Wallet walletA = new Wallet();
		Wallet walletB = new Wallet();
		Wallet walletC = new Wallet();

		// ***GENESIS TRANSACTION***
		System.out.println("Transaction Generation: ");
		Transactions transactionGeneration = new Transactions(coinBase.getPublicKey(), walletA.getPublicKey(), 100,
				null);
		transactionGeneration.setTxID("0"); // manually set genesis transaction ID
		transactionGeneration.generateSignature(coinBase.getPrivateKey()); // generate digital signature

		OutputTransactions outputs = new OutputTransactions(transactionGeneration.getReceiverKey(),
				transactionGeneration.getAmount(), transactionGeneration.getTxID());
		transactionGeneration.outputs.add(outputs);
		UTXOs.add(transactionGeneration.outputs.getEntry(1).getOutputTransactionID(),
				transactionGeneration.outputs.getEntry(1));

		// ***SENDING FUNDS GENESIS BLOCK***
		System.out.print("\nTransaction AB: ");
		Transactions transactionAB = new Transactions();
		transactionAB = walletA.sendFunds(walletB.publicKey, 15);
		transactionAB.generateSignature(walletA.getPrivateKey());

		// *TEST* send funds from CoinBase
		System.out.println("Test: CoinBase");
		System.out.println("\nWallet A funds: " + walletA.getBalance());
		System.out.println("Wallet B funds: " + walletB.getBalance());
		System.out.println("Wallet C funds: " + walletC.getBalance() + "\n");

		System.out.println("\n*** Launching FakeCoin Program ***\n");
		Block genesisBlock = new Block(1, genesisData, genesisHash); // create a genesis block
		blockchain.add(genesisBlock); // add genesis block to blockchain
		genesisBlock.addTransactions(transactionAB);

		System.out.print("\nTransaction AB2: ");
		Transactions transactionAB2 = new Transactions();
		transactionAB2 = walletA.sendFunds(walletB.publicKey, 15);
		transactionAB2.generateSignature(walletA.getPrivateKey());
		genesisBlock.addTransactions(transactionAB2);

		System.out.print("\nTransaction BC: ");
		Transactions transactionBC = new Transactions();
		transactionBC = walletB.sendFunds(walletC.publicKey, 5);
		transactionBC.generateSignature(walletB.getPrivateKey());
		genesisBlock.addTransactions(transactionBC);

		genesisBlock.setData(MerkelRoot.getMerkelRoot(genesisBlock.getTransactionsList()));

		System.out.println("\nTest: Genesis Block");
		System.out.println("Wallet A funds: " + walletA.getBalance());
		System.out.println("Wallet B funds: " + walletB.getBalance());
		System.out.println("Wallet C funds: " + walletC.getBalance() + "\n");

		genesisBlock.mineBlock(miningDifficulty, miningReward, coinBase, walletA); // mine block with five '0'sF

		System.out.println("\nTest: Genesis Block 2");
		System.out.println("Wallet A funds: " + walletA.getBalance());
		System.out.println("Wallet B funds: " + walletB.getBalance());
		System.out.println("Wallet C funds: " + walletC.getBalance() + "\n");

		// ***SENDING FUNDS SECOND BLOCK***
		AList<Transactions> secondBlockList = new AList<Transactions>();

		System.out.print("\nTransaction CB: ");
		Transactions transactionCB = new Transactions();
		transactionCB = walletC.sendFunds(walletB.publicKey, 5);
		transactionCB.generateSignature(walletC.getPrivateKey());
		secondBlockList.add(transactionCB);

		System.out.print("\nTransaction AC: ");
		Transactions transactionAC = new Transactions();
		transactionAC = walletA.sendFunds(walletC.publicKey, 20);
		transactionAC.generateSignature(walletA.getPrivateKey());
		secondBlockList.add(transactionAC);

		createBlock(secondBlockList, coinBase, walletA); // mine first before making more transaction
		System.out.println("\n\nTest: Second Block");
		System.out.println("Wallet A funds: " + walletA.getBalance());
		System.out.println("Wallet B funds: " + walletB.getBalance());
		System.out.println("Wallet C funds: " + walletC.getBalance());

		// ***SENDING FUNDS THRID BLOCK***
		AList<Transactions> thirdBlockList = new AList<Transactions>();

		System.out.print("\nTransaction CA: ");
		Transactions transactionCA = new Transactions();
		transactionCA = walletC.sendFunds(walletA.publicKey, 5);
		transactionCA.generateSignature(walletC.getPrivateKey());
		thirdBlockList.add(transactionCA);

		System.out.print("\nTransaction BC: ");
		Transactions transactionBC1 = new Transactions();
		transactionBC1 = walletB.sendFunds(walletC.publicKey, 5);
		transactionBC1.generateSignature(walletB.getPrivateKey());
		thirdBlockList.add(transactionBC1);

		// *TEST* Same Tx in Same Block
		System.out.print("\nTransaction BC2: ");
		Transactions transactionBC2 = new Transactions();
		transactionBC2 = walletB.sendFunds(walletC.publicKey, 5);
		transactionBC2.generateSignature(walletB.getPrivateKey());
		thirdBlockList.add(transactionBC2);

		// *TEST* Minimum transaction INVALID!!
		System.out.print("\nTransaction AC1: ");
		Transactions transactionAC1 = new Transactions();
		transactionAC1 = walletA.sendFunds(walletC.publicKey, 0.05);
		transactionAC1.generateSignature(walletA.getPrivateKey());
		thirdBlockList.add(transactionAC1);

		// *TEST* Insufficient funds INVALID!!
		System.out.print("\nTransaction CB: ");
		Transactions transactionCB2 = new Transactions();
		transactionCB2 = walletC.sendFunds(walletB.publicKey, 200);
		transactionCB2.generateSignature(walletC.getPrivateKey());
		thirdBlockList.add(transactionCB2);

		// Temp Test
		System.out.println("\n\nWallet A funds: " + walletA.getBalance() + "\n");
		System.out.println("Wallet B funds: " + walletB.getBalance() + "\n");
		System.out.println("Wallet C funds: " + walletC.getBalance() + "\n");

		/**
		 * Error for same wallet transactions twice Cause: iteration doesn't reset after
		 * addTransaction, lead to null pointer exception
		 */

		createBlock(thirdBlockList, coinBase, walletC);
		System.out.println("\n\nTest: Third Block");
		System.out.println("Wallet A funds: " + walletA.getBalance());
		System.out.println("Wallet B funds: " + walletB.getBalance());
		System.out.println("Wallet C funds: " + walletC.getBalance());

		validateBlockChain(); // iterate and check validity of BlockChain
		System.out.println("\nTotal blocks in the system: " + blockchain.getLength() + "\n");
		for (int i = 1; i <= blockchain.getLength(); i++) { // print out the info of BlockChain
			System.out.println("Height: " + blockchain.getEntry(i).getHeight());
			System.out.println("timeStamp: " + blockchain.getEntry(i).getTimeStamp() + "\n");
			System.out.println("Data: " + blockchain.getEntry(i).getData());
			System.out.println("nonce: " + blockchain.getEntry(i).getNonce());
			System.out.println("Hash: " + blockchain.getEntry(i).getHash());
			System.out.println("PreviousHash: " + blockchain.getEntry(i).getPreviousHash());

		}

		// *TEST* Final Balance display
		System.out.println("\nWallet A funds: " + walletA.getBalance());
		System.out.println("Wallet B funds: " + walletB.getBalance());
		System.out.println("Wallet C funds: " + walletC.getBalance() + "\n");

		String transactionID;
		OutputTransactions outputTransaction;
		Iterator<String> keyIterator = BlockChain.UTXOs.getKeyIterator();
		Iterator<OutputTransactions> valueIterator = BlockChain.UTXOs.getValueIterator();

		// *TEST* print UTXOs Ledger
		System.out.println("\nUTXOs Ledger Size: " + UTXOs.getSize());
		while (keyIterator.hasNext() && valueIterator.hasNext()) {
			transactionID = keyIterator.next();
			outputTransaction = valueIterator.next();

			System.out.print(transactionID + ":%:");
			System.out.print((Wallet.getStringFromKey(outputTransaction.getReceiverKey()) + ":%:"));
			System.out.print(outputTransaction.getAmount() + ":%:");
			System.out.print(outputTransaction.getParentTxID() + ":%:\n");
		}

		writeUTXOsLedgerToFile(); // write UTXOs Ledger to masterUTXOsLedger.txt
		writeBlockChainToFile(); // write BlockChain to masterBlockChian.txt
	}

	/**
	 * This function mine and add a new block to the block chain
	 * 
	 * @param data
	 *            - the information of the current block (eg: transaction)
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 */
	public void createBlock(String data, Wallet coinBase, Wallet minerWallet)
			throws InvalidKeyException, SignatureException {
		String previousHash;
		int height = blockchain.getLength() + 1;
		previousHash = blockchain.getEntry(blockchain.getLength()).getHash(); // set previous hash

		Block block = new Block(height, data, previousHash); // construct a new block
		block.mineBlock(miningDifficulty, miningReward, coinBase, minerWallet);

		blockchain.add(block); // add a new block blockchain
	}

	public void createBlock(AList<Transactions> transactionsList, Wallet coinBase, Wallet minerWallet)
			throws InvalidKeyException, SignatureException {
		String previousHash;
		int height = blockchain.getLength() + 1;
		previousHash = blockchain.getEntry(blockchain.getLength()).getHash(); // set previous hash

		Block block = new Block(height, transactionsList, previousHash); // construct a new block

		// for(int i=1;i<=transactionsList.getLength();i++) {
		// block.addTransactions(transactionsList.getEntry(i)); //process each
		// transactions in the block
		// }
		block.addTransactions(transactionsList); // process all the transactions in the block

		block.setData(MerkelRoot.getMerkelRoot(transactionsList)); // calculate Merkel Root
		block.setHash(block.calculateHash()); // calculate hash

		System.out.println("\nMerkel root: " + block.getData());
		block.mineBlock(miningDifficulty, miningReward, coinBase, minerWallet);

		blockchain.add(block); // add a new block BlockChain
	}

	public void validateBlockChain() {
		boolean isValid = true;
		System.out.println("\nValidating blockchain...");
		for (int i = 1; i <= blockchain.getLength(); i++) {
			// block hash & calculated hash
			if (!blockchain.getEntry(i).getHash().equals(blockchain.getEntry(i).calculateHash())) {
				isValid = false;
				System.out.println("Blockchain is invalid!!!");
				System.out.println("Block " + blockchain.getEntry(i) + " hash is not equal...\n");
			}
			// block previousHash & previous block hash
			if (i > 1 && !blockchain.getEntry(i).getPreviousHash().equals(blockchain.getEntry(i - 1).getHash())) {
				isValid = false;
				System.out.println("Blockchain is invalid!!!");
				System.out.println("Block " + blockchain.getEntry(i - 1) + " and " + blockchain.getEntry(i)
						+ " previousHash are not equal...\n");
			}
		}
		if (isValid)
			System.out.println("\nValidation successful!! Blockchain is valid\n");
	}

	/**
	 * write the blockchain to masterBlockChain.txt
	 * 
	 * @throws IOException
	 */
	public void writeBlockChainToFile() throws IOException {
		try {
			FileWriter writePost = new FileWriter("masterBlockChain.txt");
			PrintWriter out = new PrintWriter(writePost);

			for (int i = 1; i <= blockchain.getLength(); i++) {
				out.print(blockchain.getEntry(i).getHeight() + ":#:");
				out.print(blockchain.getEntry(i).getTimeStamp() + ":#:");
				out.print(blockchain.getEntry(i).getData() + ":#:");
				out.print(blockchain.getEntry(i).getNonce() + ":#:");
				out.print(blockchain.getEntry(i).getHash() + ":#:");
				out.print(blockchain.getEntry(i).getPreviousHash() + ":#:\n");
			}
			writePost.close();// successfully close program and update the masterList
		} catch (IOException e) {
			System.out.println("Error saving BlockChain!");
		}
	}

	/**
	 * write the blockchain to masterUTXOsLedger.txt
	 * 
	 * @throws IOException
	 */
	public void writeUTXOsLedgerToFile() throws IOException {
		String transactionID;
		OutputTransactions outputTransaction;

		String KeytoString;

		try {
			FileWriter writePost = new FileWriter("masterUTXOsLedger.txt");
			PrintWriter out = new PrintWriter(writePost);
			Iterator<String> keyIterator = BlockChain.UTXOs.getKeyIterator();
			Iterator<OutputTransactions> valueIterator = BlockChain.UTXOs.getValueIterator();

			while (keyIterator.hasNext() && valueIterator.hasNext()) {
				transactionID = keyIterator.next();
				outputTransaction = valueIterator.next();

				KeytoString = Wallet.getStringFromKey(outputTransaction.getReceiverKey());

				out.print(transactionID + ":%:");
				out.print(KeytoString + ":%:");
				out.print(outputTransaction.getAmount() + ":%:");
				out.print(outputTransaction.getParentTxID() + ":%:\n");
			}
			writePost.close();
		} catch (IOException e) {
			System.out.println("Error saving UTXOs Ledger!");
		}
	}
}
