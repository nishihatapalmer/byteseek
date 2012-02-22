/*
 * Copyright Matt Palmer 2012, All rights reserved.
 * 
 * This code is licensed under a standard 3-clause BSD license:
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.domesdaybook.searcher.sequence;

import java.util.List;
import net.domesdaybook.reader.Reader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Matt Palmer
 */
public class BoyerMooreHorspoolSearcherTest {
    
    public BoyerMooreHorspoolSearcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of searchForwards method, of class BoyerMooreHorspoolSearcher.
     */
    @Test
    public void testSearchForwards() {
        System.out.println("searchForwards");
        byte[] bytes = null;
        int fromPosition = 0;
        int toPosition = 0;
        BoyerMooreHorspoolSearcher instance = null;
        List expResult = null;
        List result = instance.searchForwards(bytes, fromPosition, toPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doSearchForwards method, of class BoyerMooreHorspoolSearcher.
     */
    @Test
    public void testDoSearchForwards() throws Exception {
        System.out.println("doSearchForwards");
        Reader reader = null;
        long fromPosition = 0L;
        long toPosition = 0L;
        BoyerMooreHorspoolSearcher instance = null;
        List expResult = null;
        List result = instance.doSearchForwards(reader, fromPosition, toPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of searchBackwards method, of class BoyerMooreHorspoolSearcher.
     */
    @Test
    public void testSearchBackwards() {
        System.out.println("searchBackwards");
        byte[] bytes = null;
        int fromPosition = 0;
        int toPosition = 0;
        BoyerMooreHorspoolSearcher instance = null;
        List expResult = null;
        List result = instance.searchBackwards(bytes, fromPosition, toPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of doSearchBackwards method, of class BoyerMooreHorspoolSearcher.
     */
    @Test
    public void testDoSearchBackwards() throws Exception {
        System.out.println("doSearchBackwards");
        Reader reader = null;
        long fromPosition = 0L;
        long toPosition = 0L;
        BoyerMooreHorspoolSearcher instance = null;
        List expResult = null;
        List result = instance.doSearchBackwards(reader, fromPosition, toPosition);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of prepareForwards method, of class BoyerMooreHorspoolSearcher.
     */
    @Test
    public void testPrepareForwards() {
        System.out.println("prepareForwards");
        BoyerMooreHorspoolSearcher instance = null;
        instance.prepareForwards();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of prepareBackwards method, of class BoyerMooreHorspoolSearcher.
     */
    @Test
    public void testPrepareBackwards() {
        System.out.println("prepareBackwards");
        BoyerMooreHorspoolSearcher instance = null;
        instance.prepareBackwards();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
