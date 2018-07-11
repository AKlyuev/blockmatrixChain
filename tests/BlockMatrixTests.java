import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BlockMatrixTests {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenDimensionLessThanTwo() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("BlockMatrix must have dimensions of at least 2x2.");
        BlockMatrix bm = new BlockMatrix(1);
    }

    @Test
    public void testGetBlockGetsCorrectBlockUsedBlock() {
        BlockMatrix bm = new BlockMatrix(5);
        Block b = new Block("one");
        bm.add(b);
        Assert.assertEquals(b, bm.getBlock(1));
    }

    @Test
    public void testGetBlockGetsCorrectBlockNullBlock() {
        BlockMatrix bm = new BlockMatrix(5);
        Assert.assertEquals(null, bm.getBlock(1));
    }

    @Test
    public void testInputCountAtStart() {
        BlockMatrix bm = new BlockMatrix(5);
        Assert.assertEquals(0, bm.getInputCount());
    }

    @Test
    public void testInputCount() {
        BlockMatrix bm = new BlockMatrix(5);
        bm.add(new Block("One"));
        bm.add(new Block("Two"));
        bm.add(new Block("Three"));
        Assert.assertEquals(3, bm.getInputCount());
    }

    @Test
    public void testGetBlockDataGetsCorrectDataSingle() {
        BlockMatrix bm = new BlockMatrix(5);
        bm.add(new Block("Input"));
        Assert.assertEquals("Input", bm.getBlockData(1));
    }

    @Test
    public void testGetBlockDataGetsCorrectDataMultiple() {
        BlockMatrix bm = new BlockMatrix(5);
        bm.add(new Block("One"));
        bm.add(new Block("Two"));
        bm.add(new Block("Three"));
        Assert.assertEquals("One", bm.getBlockData(1));
        Assert.assertEquals("Two", bm.getBlockData(2));
        Assert.assertEquals("Three", bm.getBlockData(3));
    }

    @Test
    public void testDeleteBlockSingle() {
        BlockMatrix bm = new BlockMatrix(5);
        bm.add(new Block("One"));
        bm.add(new Block("Two"));
        bm.add(new Block("Three"));
        bm.deleteBlock(2);
        Assert.assertEquals("One", bm.getBlockData(1));
        Assert.assertEquals("DELETED", bm.getBlockData(2));
        Assert.assertEquals("Three", bm.getBlockData(3));
    }

    @Test
    public void testDeleteBlockMultiple() {
        BlockMatrix bm = new BlockMatrix(5);
        bm.add(new Block("One"));
        bm.add(new Block("Two"));
        bm.add(new Block("Three"));
        bm.add(new Block("Four"));
        bm.add(new Block("Five"));
        bm.deleteBlock(2);
        bm.deleteBlock(4);
        Assert.assertEquals("One", bm.getBlockData(1));
        Assert.assertEquals("DELETED", bm.getBlockData(2));
        Assert.assertEquals("Three", bm.getBlockData(3));
        Assert.assertEquals("DELETED", bm.getBlockData(4));
        Assert.assertEquals("Five", bm.getBlockData(5));
    }

}
