/**
 * MerkelRoot.java 
 * This is the class for creating a hash of the list of
 * transactions in a block by using Merkel Root algorithm
 * 
 * 01.April.2021
 * @author Tun Pyay Sone LIN
 */

public class MerkelRoot {

	// Default Constructor
	MerkelRoot() {

	}

	/**
	 * This function takes in list of transactions and finds a merkelRoot (block data) 
	 * @param transactions - list of transactions
	 * @return merkel root hash 
	 */
	public static String getMerkelRoot(AList<Transactions> transactions) {
		AList<String> TreeLayer = new AList<String>();

		// populate merkelRootLayer with TxID from each transactions
		for (int i = 1; i <= transactions.getLength(); i++) {
			TreeLayer.add(transactions.getEntry(i).getTxID());
		}
		do { //loop until 1 root node remains
			AList<String> merkelRootLayer = new AList<String>();

			if (TreeLayer.getLength() % 2 == 1) { // if odd length, make it even
				TreeLayer.add(TreeLayer.getEntry(TreeLayer.getLength())); // duplicate the last transaction
			}
			for (int i = 1; i <= TreeLayer.getLength(); i += 2) { //hash 2 TxID into 1 hash using Sha256
				String hashTransaction = Sha256.getSHA(TreeLayer.getEntry(i) + TreeLayer.getEntry(i + 1));
				merkelRootLayer.add(hashTransaction);
			}
			TreeLayer = merkelRootLayer;
		}while (TreeLayer.getLength() != 1);
		return TreeLayer.getEntry(1);
	}

}
