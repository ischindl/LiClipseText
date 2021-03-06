/******************************************************************************
* Copyright (C) 2015 Brainwy Software Ltda
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Fabio Zadrozny <fabiofz@gmail.com> - initial API and implementation
******************************************************************************/
package org.brainwy.liclipsetext.shared_core.document;

import java.util.Arrays;
import java.util.HashMap;

import org.brainwy.liclipsetext.shared_core.log.Log;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitionerExtension2;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IDocumentRewriteSessionListener;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.TypedRegion;

/**
 * Partial implementation of a document to be used as a throw-away copy
 * (things which change the document should not be implemented).
 */
public class DocCopy implements IDocument, IDocumentExtension4, IDocumentExtension3 {

    private String contents;
    private IDocument document;
    private HashMap<String, Position[]> categoryToPos;
    private long modificationStamp;
    private ILineTracker fLineTracker;
    private String managingPositionCategory;

    public DocCopy(IDocument document) {
        this.contents = document.get();
        this.document = document;
        categoryToPos = new HashMap<>();

        String[] positionCategories = document.getPositionCategories();
        for (String string : positionCategories) {
            try {
                categoryToPos.put(string, document.getPositions(string));
            } catch (BadPositionCategoryException e) {
                Log.log(e);
            }
        }

        IDocumentExtension4 doc4 = (IDocumentExtension4) document;
        modificationStamp = doc4.getModificationStamp();

        IDocumentPartitionerExtension2 documentPartitioner = (IDocumentPartitionerExtension2) document
                .getDocumentPartitioner();
        if (documentPartitioner != null) {
            String[] managingPositionCategories = documentPartitioner.getManagingPositionCategories();
            Assert.isTrue(managingPositionCategories.length == 1);
            this.managingPositionCategory = managingPositionCategories[0];
            Assert.isNotNull(this.managingPositionCategory);

            Assert.isTrue(categoryToPos.containsKey(this.managingPositionCategory));

            Position[] positions = this.categoryToPos.get(this.managingPositionCategory);
            // Check that positions are sorted
            int len = positions.length;
            int checkedOffset = -1;
            for (int i = 0; i < len; i++) {
                Position position = positions[i];
                if (checkedOffset > position.offset) {
                    Log.log("Expected positions to be sorted at this point (as they're not, sorting now).");
                    Arrays.sort(positions, (Position a, Position b) -> {
                        return Integer.compare(a.getOffset(), b.getOffset());
                    });
                    break;
                }
                checkedOffset = position.offset;
            }
        }

    }

    private ILineTracker getLineTracker() {
        if (fLineTracker == null) {
            fLineTracker = new DefaultLineTracker();
            fLineTracker.set(this.contents);
        }
        return fLineTracker;
    }

    public void dispose() {
        contents = null;
        document = null;
        categoryToPos = null;
        fLineTracker = null;
    }

    @Override
    public char getChar(int offset) throws BadLocationException {
        return contents.charAt(offset);
    }

    @Override
    public int getLength() {
        return this.contents.length();
    }

    @Override
    public String get() {
        return this.contents;
    }

    @Override
    public String get(int offset, int length) throws BadLocationException {
        try {
            return this.contents.substring(offset, offset + length);
        } catch (StringIndexOutOfBoundsException e) {
            throw new BadLocationException(
                    "Bad location. Start Offset: " + offset + " Final offset: " + (offset + length)
                            + " len: " + length + " doc len: " + this.contents.length());
        }
    }

    @Override
    public void set(String text) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void replace(int offset, int length, String text) throws BadLocationException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void addDocumentListener(IDocumentListener listener) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void removeDocumentListener(IDocumentListener listener) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void addPrenotifiedDocumentListener(IDocumentListener documentAdapter) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void removePrenotifiedDocumentListener(IDocumentListener documentAdapter) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void addPositionCategory(String category) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void removePositionCategory(String category) throws BadPositionCategoryException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String[] getPositionCategories() {
        return this.categoryToPos.entrySet().toArray(new String[this.categoryToPos.size()]);
    }

    @Override
    public boolean containsPositionCategory(String category) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void addPosition(Position position) throws BadLocationException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void removePosition(Position position) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void addPosition(String category, Position position) throws BadLocationException,
            BadPositionCategoryException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void removePosition(String category, Position position) throws BadPositionCategoryException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Position[] getPositions(String category) throws BadPositionCategoryException {
        return this.categoryToPos.get(category);
    }

    @Override
    public boolean containsPosition(String category, int offset, int length) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public int computeIndexInCategory(String category, int offset) throws BadLocationException,
            BadPositionCategoryException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void addPositionUpdater(IPositionUpdater updater) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void removePositionUpdater(IPositionUpdater updater) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void insertPositionUpdater(IPositionUpdater updater, int index) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public IPositionUpdater[] getPositionUpdaters() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String[] getLegalContentTypes() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String getContentType(int offset) throws BadLocationException {
        throw new RuntimeException("not implemented");
    }

    public static int binarySearch(Position[] a, int offset) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            Position midPos = a[mid];
            if (offset < midPos.offset) {
                hi = mid - 1;
            } else if (offset > midPos.offset) {
                if (offset < midPos.offset + midPos.length) {
                    // Check if in range
                    return mid;
                }
                lo = mid + 1;
            } else {
                // offset == midPos.offset
                return mid;
            }
        }
        return -1;
    }

    @Override
    public ITypedRegion getPartition(int offset) throws BadLocationException {
        Position[] positions = this.categoryToPos.get(this.managingPositionCategory);

        int binarySearch = binarySearch(positions, offset);
        if (binarySearch == -1) {
            return null;
        }
        TypedPosition position = (TypedPosition) positions[binarySearch];
        return new TypedRegion(position.offset, position.length, position.getType());
    }

    @Override
    public ITypedRegion[] computePartitioning(int offset, int length) throws BadLocationException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void addDocumentPartitioningListener(IDocumentPartitioningListener listener) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void removeDocumentPartitioningListener(IDocumentPartitioningListener listener) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setDocumentPartitioner(IDocumentPartitioner partitioner) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public IDocumentPartitioner getDocumentPartitioner() {
        return document.getDocumentPartitioner();
    }

    @Override
    public int getLineLength(int line) throws BadLocationException {
        return getLineTracker().getLineLength(line);
    }

    @Override
    public int getLineOfOffset(int pos) throws BadLocationException {
        return getLineTracker().getLineNumberOfOffset(pos);
    }

    @Override
    public int getLineOffset(int line) throws BadLocationException {
        return getLineTracker().getLineOffset(line);
    }

    @Override
    public IRegion getLineInformation(int line) throws BadLocationException {
        return getLineTracker().getLineInformation(line);
    }

    @Override
    public IRegion getLineInformationOfOffset(int offset) throws BadLocationException {
        return getLineTracker().getLineInformationOfOffset(offset);
    }

    @Override
    public int getNumberOfLines() {
        return getLineTracker().getNumberOfLines();
    }

    @Override
    public int getNumberOfLines(int offset, int length) throws BadLocationException {
        return getLineTracker().getNumberOfLines(offset, length);
    }

    @Override
    public int computeNumberOfLines(String text) {
        return getLineTracker().computeNumberOfLines(text);
    }

    @Override
    public String[] getLegalLineDelimiters() {
        return getLineTracker().getLegalLineDelimiters();
    }

    @Override
    public String getLineDelimiter(int line) throws BadLocationException {
        return getLineTracker().getLineDelimiter(line);
    }

    @Override
    public int search(int startOffset, String findString, boolean forwardSearch, boolean caseSensitive,
            boolean wholeWord) throws BadLocationException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public DocumentRewriteSession startRewriteSession(DocumentRewriteSessionType sessionType)
            throws IllegalStateException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void stopRewriteSession(DocumentRewriteSession session) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public DocumentRewriteSession getActiveRewriteSession() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void addDocumentRewriteSessionListener(IDocumentRewriteSessionListener listener) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void removeDocumentRewriteSessionListener(IDocumentRewriteSessionListener listener) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void replace(int offset, int length, String text, long modificationStamp) throws BadLocationException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void set(String text, long modificationStamp) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public long getModificationStamp() {
        return modificationStamp;
    }

    @Override
    public String getDefaultLineDelimiter() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setInitialLineDelimiter(String lineDelimiter) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String[] getPartitionings() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String[] getLegalContentTypes(String partitioning) throws BadPartitioningException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String getContentType(String partitioning, int offset, boolean preferOpenPartitions)
            throws BadLocationException, BadPartitioningException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public ITypedRegion getPartition(String partitioning, int offset, boolean preferOpenPartitions)
            throws BadLocationException, BadPartitioningException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public ITypedRegion[] computePartitioning(String partitioning, int offset, int length,
            boolean includeZeroLengthPartitions) throws BadLocationException, BadPartitioningException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setDocumentPartitioner(String partitioning, IDocumentPartitioner partitioner) {
        ((IDocumentExtension3) document).setDocumentPartitioner(partitioning, partitioner);
    }

    @Override
    public IDocumentPartitioner getDocumentPartitioner(String partitioning) {
        return ((IDocumentExtension3) document).getDocumentPartitioner(partitioning);
    }

}
