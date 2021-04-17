const {BlockChain, Transaction} = require("./blockchain"); //import blockchain.js
const EC = require("elliptic").ec;
const ec = new EC("secp256k1");

const myKey = ec.keyFromPrivate("e7637f88e049226cd1732e69098fdc6e66def25e7f5ad33efea2aff04c260e8e");
const myWalletAddress = myKey.getPublic("hex");

let fakeCoin = new BlockChain();

const tx1 = new Transaction(myWalletAddress, "public key1 goes here", 5);
tx1.signTransaction(myKey);
fakeCoin.addTransaction(tx1);

const tx2 = new Transaction(myWalletAddress, "public key2 goes here", 20);
tx2.signTransaction(myKey);
fakeCoin.addTransaction(tx2);

console.log("\nStart transaction mining...");
fakeCoin.minePendingTransactions(myWalletAddress);

console.log('\nBalance of tun is ', fakeCoin.getBalanceOfAddress(myWalletAddress));

// console.log("\nStart transaction mining 2...");
// fakeCoin.minePendingTransactions("tun-address");

// console.log('\nBalance of tun is ', fakeCoin.getBalanceOfAddress("tun-address"));

fakeCoin.chain[1].transactions[0].amount = 1;

// console.log(JSON.stringify(fakeCoin, null, 4));
console.log("\nIs blockchain valid? " + fakeCoin.isChainValid());
