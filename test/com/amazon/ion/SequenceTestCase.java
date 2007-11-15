/*
 * Copyright (c) 2007 Amazon.com, Inc.  All rights reserved.
 */

package com.amazon.ion;



public abstract class SequenceTestCase
    extends ContainerTestCase
{
    /**
     * @return a new null sequence.
     */
    protected abstract IonSequence makeNull();

    /**
     * Wrap a single value with a sequence of the class under test.
     */
    protected abstract String wrap(String v);

    public static void checkNullSequence(IonSequence value)
    {
        try
        {
            value.size();
            fail("Expected NullValueException");
        }
        catch (NullValueException e) { }

        try
        {
            value.iterator();
            fail("Expected NullValueException");
        }
        catch (NullValueException e) { }

        try
        {
            value.get(0);
            fail("Expected NullValueException");
        }
        catch (NullValueException e) { }

        try
        {
            value.remove(null);
            fail("Expected NullValueException");
        }
        catch (NullValueException e) { }

        try
        {
            value.isEmpty();
            fail("Expected NullValueException");
        }
        catch (NullValueException e) { }
    }


    public void modifySequence(IonSequence value)
    {
        IonBool nullBool0 = system().newNullBool();
        value.add(nullBool0);
        assertNull(nullBool0.getFieldName());
        assertSame(value, nullBool0.getContainer());
        assertEquals("size", 1, value.size());
        assertSame(nullBool0, value.get(0));

        IonBool nullBool1 = system().newNullBool();
        value.add(nullBool1);
        assertNull(nullBool1.getFieldName());
        assertSame(value, nullBool1.getContainer());
        assertEquals("size", 2, value.size());
        assertSame(nullBool0, value.get(0));
        assertSame(nullBool1, value.get(1));

        try
        {
            value.add(nullBool0);
            fail("Expected ContainedValueException");
        }
        catch (ContainedValueException e) { }
        // Make sure the element hasn't changed
        assertNull(nullBool0.getFieldName());
        assertSame(value, nullBool0.getContainer());

        // Now remove an element
        boolean removed = value.remove(nullBool0);
        assertTrue(removed);
        assertNull(nullBool0.getFieldName());
        assertNull(nullBool0.getContainer());
        assertEquals("size", 1, value.size());
        assertSame(nullBool1, value.get(0));

        // How about insert?
        IonInt nullInt = system().newNullInt();
        value.add(0, nullInt);
        assertSame(value, nullInt.getContainer());
        assertEquals("size", 2, value.size());
        assertSame(nullInt, value.get(0));
        assertSame(nullBool1, value.get(1));

        // Clear the sequence
        testClearContainer(value);
    }


    public void testFreshNullSequence(IonSequence value)
    {
        assertNull(value.getFieldName());
        checkNullSequence(value);
        modifySequence(value);
    }


    public void testEmptySequence(IonSequence value)
    {
        assertFalse(value.isNullValue());
        assertEquals(0, value.size());
        assertFalse(value.iterator().hasNext());
        assertTrue(value.isEmpty());

        try
        {
            value.get(0);
            fail("Expected IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) { }

        modifySequence(value);
    }


    //=========================================================================
    // Test cases

    /**
     * This looks for a subtle encoding problem. If a value has its header
     * widened enough to overlap clean content, we must be careful to not
     * overwrite the content while writing the header.  This can happen when
     * adding annotations.
     *
     * @see StructTest#testModsCausingHeaderOverlap()
     */
    public void testModsCausingHeaderOverlap()
        throws Exception
    {
        IonDatagram dg = values("[\"this is a string to overlap\"]");
        IonList v = (IonList) dg.get(0);
        v.addTypeAnnotation("one");
        v.addTypeAnnotation("two");
        v.addTypeAnnotation("three");
        v.addTypeAnnotation("four");
        v.addTypeAnnotation("five");
        v.addTypeAnnotation("six");

        dg = reload(dg);
        v = (IonList) dg.get(0);
        checkString("this is a string to overlap", v.get(0));
    }

    public void testBadAdds()
    {
        IonSequence value = makeNull();

        try {
            value.add(null);
            fail("Expected NullPointerException");
        }
        catch (NullPointerException e) { }
    }

    public void testBadRemoves()
    {
        IonSequence value = makeNull();
        IonBool nullBool0 = system().newNullBool();
        value.add(nullBool0);

        try {
            value.remove(null);
            fail("Expected NullPointerException");
        }
        catch (NullPointerException e) { }

        IonBool nullBool1 = system().newNullBool();
        assertFalse(value.remove(nullBool1));

        IonSequence otherSeq = makeNull();
        otherSeq.add(nullBool1);

        assertFalse(value.remove(nullBool1));
    }
}