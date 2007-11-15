/*
 * Copyright (c) 2007 Amazon.com, Inc.  All rights reserved.
 */

package com.amazon.ion.util;

import com.amazon.ion.IonTestCase;

/**
 *
 */
public class TextTest
    extends IonTestCase
{

    public void testSymbolNeedsQuoting()
    {
        unquotedAnywhere("hello");
        unquotedAnywhere("$hello");
        unquotedAnywhere("$123");
        unquotedAnywhere("$$123");

        quotedEverywhere("hi there");
        quotedEverywhere("'hi there'");
        quotedEverywhere("\"hi there\"");
        quotedEverywhere("123");
        quotedEverywhere("hi!");
        quotedEverywhere("hi:");
        
        // Keywords
        quotedEverywhere("true");
        quotedEverywhere("false");
        quotedEverywhere("null");
        quotedEverywhere("null.int");
        
        // Operators
        unquotedInSexp("!");
        unquotedInSexp("<");
        unquotedInSexp("<===");
        
        quotedEverywhere("<abc");        
        quotedEverywhere("<abc>");
        quotedEverywhere("abc>");
        quotedEverywhere("< ");
        quotedEverywhere("<12");
        quotedEverywhere("<{");
        quotedEverywhere("{");
        quotedEverywhere("}");
        quotedEverywhere("[");
        quotedEverywhere("]");
        quotedEverywhere(",");
        quotedEverywhere("'");
        quotedEverywhere("\"");
        quotedEverywhere(":");
        quotedEverywhere("::");
        quotedEverywhere(":a");
    }


    private void unquotedAnywhere(String symbol)
    {
        // unquoted in sexp
        assertFalse(Text.symbolNeedsQuoting(symbol, false));
        // unquoted elsewhere
        assertFalse(Text.symbolNeedsQuoting(symbol, true));
    }
    
    private void quotedEverywhere(String symbol)
    {
        // Quoted in sexp
        assertTrue(Text.symbolNeedsQuoting(symbol, false));
        // Quoted elsewhere
        assertTrue(Text.symbolNeedsQuoting(symbol, true));
    }

    private void unquotedInSexp(String symbol)
    {
        // unquoted in sexp
        assertFalse(Text.symbolNeedsQuoting(symbol, false));
        // quoted elsewheres
        assertTrue(Text.symbolNeedsQuoting(symbol, true));
    }
}