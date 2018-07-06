

public class MatrixChain {

    private static BlockMatrix bm;
    public static int difficulty = 5;

    public static void main(String[] args) {
        bm = new BlockMatrix(5);
        bm.fillDiagonalZeros();

        //add our blocks to the BlockMatrix:

        bm.add(new Block("First     "));
        System.out.println("Trying to mine block 1... ");
        bm.getBlock(1).mineBlock(difficulty);

        bm.add(new Block("Second    "));
        System.out.println("Trying to mine block 2... ");
        bm.getBlock(2).mineBlock(difficulty);

        bm.add(new Block("Third     "));
        System.out.println("Trying to mine block 3... ");
        bm.getBlock(3).mineBlock(difficulty);

        System.out.println(bm);







    }
}
