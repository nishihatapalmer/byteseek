package net.byteseek.compiler.matcher;

import net.byteseek.utils.ByteUtils;
import net.byteseek.matcher.bytes.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class ByteMatcherCompilerTest {

    static Random random = new Random();

    private ByteMatcherCompiler compiler;

    @BeforeClass
    public static void setUpClass() throws Exception {
        final long seed = System.currentTimeMillis();
        // final long seed = ?
        random.setSeed(seed);
        System.out.println("Seeding random number generator with: " + Long.toString(seed));
        System.out.println("To repeat these exact tests, set the seed to the value above.");
    }

    @Before
    public void setup() {
        compiler = new ByteMatcherCompiler();
    }

    @Test
    public void testCompileFrom() throws Exception {

    }

    @Test
    public void testCompileFrom1() throws Exception {

    }

    @Test
    public void testCompileInvertedFrom() throws Exception {

    }

    @Test
    public void testCompile() throws Exception {
        for (int testNo = 0; testNo < 1000; testNo++) {
            ByteMatcher generated = createRandomByteMatcher();
            String expression = generated.toRegularExpression(false);

            ByteMatcher compiled = compiler.compile(expression);

            byte[] genBytes = generated.getMatchingBytes();
            byte[] comBytes = compiled.getMatchingBytes();
            assertEquals("Matcher " + generated + " matches same as " + compiled, genBytes.length, comBytes.length);
            Set<Byte> comB = ByteUtils.toSet(comBytes);
            for (byte b : genBytes) {
                assertTrue(comB.contains(b));
            }
        }
    }

    @Test
    public void testDoCompile() throws Exception {

    }

    @Test
    public void testJoinExpressions() throws Exception {

    }

    @Test
    public void testCompile1() throws Exception {

    }

    @Test
    public void testCompile2() throws Exception {

    }

    private ByteMatcher createRandomByteMatcher() {
        int matcherType = random.nextInt(9);
        boolean inverted = random.nextBoolean();
        switch (matcherType) {
            case 0:
                return AnyByteMatcher.ANY_BYTE_MATCHER;
            case 1:
                return OneByteMatcher.valueOf((byte) random.nextInt(256));
            case 2:
                return new InvertedByteMatcher((byte) random.nextInt(256));
            case 3:
                return new ByteRangeMatcher(random.nextInt(256), random.nextInt(256), inverted);
            case 4:
                return new SetBinarySearchMatcher(createRandomByteSet(), inverted);
            case 5:
                return new SetBitsetMatcher(createRandomByteSet(), inverted);
            case 6:
                return new TwoByteMatcher((byte) random.nextInt(256), (byte) random.nextInt(256));
            case 7:
                return new AllBitmaskMatcher((byte) random.nextInt(256), inverted);
            case 8:
                return new AnyBitmaskMatcher((byte) random.nextInt(256), inverted);
            default:
                throw new RuntimeException("Case statement doesn't support value " + matcherType);
        }
    }

    private Set<Byte> createRandomByteSet() {
        Set<Byte> bytes = new HashSet<Byte>();
        int numElements = random.nextInt(255) + 1;
        for (int i = 0; i < numElements; i++) {
            byte value = (byte) random.nextInt(256);
            bytes.add(value);
        }
        return bytes;
    }
}