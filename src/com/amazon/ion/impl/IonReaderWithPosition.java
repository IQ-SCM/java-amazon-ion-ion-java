// Copyright (c) 2011 Amazon.com, Inc.  All rights reserved.

package com.amazon.ion.impl;

import com.amazon.ion.SpanReader;



/**
 *  This interface is an extension to the basic IonReader which
 *  allows the reader to be repositioned to any value in the
 *  input stream.  This is only supported over input sources that
 *  support seek() or its equivalent or are fully buffered.
 *
 * @deprecated Use {@link SpanReader}.
 */
@Deprecated
public interface IonReaderWithPosition
    extends SpanReader
{
    /**
     * returns an IonReaderPosition based on the current
     * position of the reader in the input.  This can be
     * used to reset the reader to re-read the current
     * value.  Before the first call to next this
     * position represents the entire datagram.
     *
     * The returned position objects can only validly
     * used with the reader which returned them.
     *
     * @return IonReaderPosition
     *
     * @deprecated Use {@link #currentSpan()}.
     */
    @Deprecated
    public IonReaderPosition getCurrentPosition();

    /**
     * this will return the reader to the value that
     * was pending when the getCurrentPosition was called
     * to retrieve the position object.  This loses the
     * state of the reader.
     *
     * @param position retrieved from this reader
     *
     * @deprecated Use {@link #hoist(com.amazon.ion.Span)}
     */
    @Deprecated
    public void seek(IonReaderPosition position);

    /**
     * TODO: some makeAdditionalReader() which would return
     * an IonReaderWithPosition which shares the underlying
     * state necessary to accept the positions associated
     * with the current reader.  This would allow the seek
     * mixed with reading without losing the state of the
     * reader.
     */
}