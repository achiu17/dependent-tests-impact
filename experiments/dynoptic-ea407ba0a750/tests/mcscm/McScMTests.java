package mcscm;


public class McScMTests {
    //
    //    McScM mcscm;
    //    String verifyPath;
    //    String scmFilePrefix;
    //
    //    List<ChannelId> cids;
    //    ChannelId cid0, cid1;
    //
    //    DistEventType cExEType;
    //
    //    protected static Logger logger;
    //
    //    @Before
    //    public void setUp() {
    //        // NOTE: We assume the tests are run from synoptic/mcscm-bridge/
    //        verifyPath = DynopticTest.getMcPath();
    //        scmFilePrefix = "./tests/mcscm/";
    //
    //        logger = Logger.getLogger("TestMcScM");
    //        logger.setLevel(Level.INFO);
    //        mcscm = new McScM(verifyPath);
    //
    //        cids = Util.newList();
    //        cid0 = new ChannelId(1, 2, 0);
    //        cid1 = new ChannelId(1, 2, 1);
    //        cids.add(cid0);
    //        cids.add(cid1);
    //
    //        cExEType = DistEventType.SendEvent("i", cid1);
    //    }
    //
    //    /**
    //     * Reads a text file from the current directory and returns it as a single
    //     * string.
    //     *
    //     * @throws IOException
    //     */
    //    public String readScmFile(String filename) throws IOException {
    //        String filePath = scmFilePrefix + filename;
    //        BufferedReader in = new BufferedReader(new FileReader(filePath));
    //
    //        StringBuilder everything = new StringBuilder();
    //        String line;
    //        while ((line = in.readLine()) != null) {
    //            everything.append(line);
    //            everything.append("\n");
    //        }
    //        return everything.toString();
    //    }
    //
    //    /**
    //     * Empty scm input should result in a syntax error.
    //     *
    //     * @throws IOException
    //     */
    //    @Test(expected = ScmSyntaxException.class)
    //    public void testEmptyScmInput() throws IOException {
    //        try {
    //            mcscm.verify("", 60);
    //        } catch (Exception e) {
    //            logger.info("Verify threw an exception: " + e.toString());
    //            fail("Verify should not fail.");
    //        }
    //
    //        mcscm.getVerifyResult(cids);
    //        fail("getVerifyResult should have thrown an exception.");
    //    }
    //
    //    /**
    //     * Test verify on a safe model.
    //     *
    //     * @throws IOException
    //     */
    //    @Test
    //    public void testSafeScmInput() throws IOException {
    //        String scmStr = readScmFile("ABP_safe.scm");
    //
    //        try {
    //            mcscm.verify(scmStr, 60);
    //        } catch (Exception e) {
    //            logger.info("Verify threw an exception: " + e.toString());
    //            fail("Verify should not fail.");
    //        }
    //
    //        VerifyResult result = mcscm.getVerifyResult(cids);
    //        assertTrue(result.modelIsSafe());
    //    }
    //
    //    /**
    //     * Test verify on an unsafe model with c-example of len 1.
    //     *
    //     * @throws IOException
    //     */
    //    @Test
    //    public void testUnsafeScmInputLen1() throws IOException {
    //        String scmStr = readScmFile("ABP_unsafe_cexample_len1.scm");
    //
    //        try {
    //            mcscm.verify(scmStr, 60);
    //        } catch (Exception e) {
    //            logger.info("Verify threw an exception: " + e.toString());
    //            fail("Verify should not fail.");
    //        }
    //
    //        VerifyResult result = mcscm.getVerifyResult(cids);
    //        assertTrue(!result.modelIsSafe());
    //        assertEquals(result.getCExample().getEvents().size(), 1);
    //        assertEquals(result.getCExample().getEvents().get(0), cExEType);
    //    }
    //
    //    /**
    //     * Test verify on an unsafe model with c-example of len 2.
    //     *
    //     * @throws IOException
    //     */
    //    @Test
    //    public void testUnsafeScmInputLen2() throws IOException {
    //        String scmStr = readScmFile("ABP_unsafe_cexample_len2.scm");
    //
    //        try {
    //            mcscm.verify(scmStr, 60);
    //        } catch (Exception e) {
    //            logger.info("Verify threw an exception: " + e.toString());
    //            fail("Verify should not fail.");
    //        }
    //
    //        VerifyResult result = mcscm.getVerifyResult(cids);
    //        assertTrue(!result.modelIsSafe());
    //        assertTrue(result.getCExample() != null);
    //        assertTrue(result.getCExample().getEvents() != null);
    //        assertEquals(result.getCExample().getEvents().size(), 2);
    //        assertEquals(result.getCExample().getEvents().get(0), cExEType);
    //        assertEquals(result.getCExample().getEvents().get(1), cExEType);
    //    }
}
