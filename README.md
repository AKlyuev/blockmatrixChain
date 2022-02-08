# blockmatrix

## Description

This java package serves as implementation of a transaction system implemented similarly to a
blockchain, but which uses a “blockmatrix” data structure to allow deletion. It allows local use of a
blockchain which uses a blockmatrix.

Users can:
* Create wallets
* Pass funds along with a message to other user’s wallets
* Modify messages passed along with transactions after blocks have been published

I highly encourage viewing of the [manual](docs/Manual.pdf) in the docs folder for information on the idea behind this package, and more information on how to use it. This project was [presented](docs/BlockMatrices.pdf) to NIST, and was later presented to the 2018 IEEE Blockchain Summit (in slightly altered form). The link to the NIST block matrix repo can be found [here](https://github.com/usnistgov/blockmatrix).

*For any questions regarding the package, please contact:*

Arsen Klyuev: akliouev1@gmail.com

## Setup 
### Installation
To install the BlockMatrix package, download the
JAR file from the [blockmatrix_jar](out/artifacts/blockmatrix_jar) folder. Include this jar file as an external library for your project.

#### Dependencies
You will need to import [Bouncy Castle](https://www.bouncycastle.org/download/bcprov-jdk15on-159.jar).

Once you have done this, import the package with 
> import blockmatrix.*;

at the top of your Java file to be able to use the program.

### First Steps
1. Create a BlockMatrix blockchain, initializing it with the constructor:
> public BlockMatrix(int dimension)

The **dimension** parameter is where you can specify the size of the BlockMatrix you would
like. A **dimension** of N will create an NxN BlockMatrix that will be able to hold NxN – N
blocks total (due to the empty diagonal). This value cannot be changed, so it is
recommended to make sure the BlockMatrix has more than enough space when you
initialize it.

2. Set up the security provider. Suppose the name of the BlockMatrix object we initialized is
bm. Set up the security provider like so:
>bm.setUpSecurity();

This will add Bouncy Castle as a provider to the Java Security API we will be using, allowing
us to create Elliptic-Curve KeyPairs for our wallets for the wallets Public and Private keys. 

3. Create your initial wallet, initializing it with the constructor: 
>public Wallet();

4. Start up our blockchain by creating the genesis transaction which will give our initial wallet a
specified amount of funds. 
>public void generate(Wallet wallet, float value)

The **wallet** parameter is the wallet we want to be the recipient of our genesis transaction to
be. This **wallet** will receive **value**, a prespecified amount of the asset (or “coin”) you want
this wallet to start with. 

Once this is done, you have completed all the initial steps of setting up your blockmatrix blockchain!
You are free to create wallets, create transactions, create blocks, add transactions to blocks, and add
blocks to the block matrix at your own discretion. **Note**: The steps in the above setup should only 
be completed once. It is not possible to have multiple blockchains in one program using the
package.

Again, for more information, I highly recommend viewing the [manual](docs/Manual.pdf).
