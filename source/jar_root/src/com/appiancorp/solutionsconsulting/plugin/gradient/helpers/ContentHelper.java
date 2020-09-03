package com.appiancorp.solutionsconsulting.plugin.gradient.helpers;

import com.appiancorp.suiteapi.common.Constants;
import com.appiancorp.suiteapi.common.exceptions.AppianException;
import com.appiancorp.suiteapi.content.*;
import com.appiancorp.suiteapi.content.exceptions.InvalidContentException;
import com.appiancorp.suiteapi.content.exceptions.InvalidTypeMaskException;
import com.appiancorp.suiteapi.knowledge.Document;
import org.apache.log4j.Logger;

/**
 * Class with helper functions.
 */
public class ContentHelper {
    private static final Logger LOG = Logger.getLogger(ContentHelper.class);

    /**
     * Looks for a Document by name (case sensitive) and extension (case insensitive) within a parent Folder
     *
     * @param contentService the Appian injected ContentService instance
     * @param documentName   the name of the document to search for (case sensitive)
     * @param extension      the extension of the document to search for (case insensitive)
     * @param parentFolder   the Long ID of the parent Folder
     * @return The Long ID of the Document found, or null if not found
     */
    public static Long findDocumentInFolder(ContentService contentService, String documentName, String extension, Long parentFolder) {
        try {
            ContentFilter contentFilter = new ContentFilter(ContentConstants.TYPE_DOCUMENT);
            contentFilter.setName(documentName);

            Content[] childDocuments = (Content[]) contentService.getChildrenPaging(
                    parentFolder,
                    contentFilter,
                    ContentConstants.GC_MOD_NORMAL,
                    0,
                    Constants.COUNT_ALL,
                    ContentConstants.COLUMN_NAME,
                    Constants.SORT_ORDER_ASCENDING
            ).getResults();

            for (Content child : childDocuments) {
                if (child.getName().matches(documentName)) {
                    if (((Document) child).getExtension().toUpperCase().equals(extension.toUpperCase())) {
                        return child.getId(); // Found an existing document with this name, return it
                    }
                }
            }

        } catch (InvalidContentException | InvalidTypeMaskException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Creates a Document object that can then be stored in Appian's ContentService
     *
     * @param name        the Document name, without the extension
     * @param description the Document description
     * @param folderId    the Folder ID (Long) where the document should be stored
     * @param extension   the Document extension
     * @return the new Document
     */
    public static Document createNewDocument(String name, String description, Long folderId, String extension) {
        Document document = new Document();
        document.setName(name);
        document.setDescription(description);
        document.setSize(0);
        document.setParent(folderId);
        document.setExtension(extension);
        document.setId(null);
        return document;
    }

    /**
     * Takes a Document instance (such as created by createNewDocument) and uploads it to Appian's ContentService,
     * returning a ContentOutputStream (extension of java.io.FileOutputStream) suitable for writing data to using
     * methods found in java.io.* and other libraries that use this standard.
     *
     * @param contentService the Appian injected ContentService instance
     * @param document       the Document
     * @return The open ContentOutputStream to the physical file, ready for writing
     * @throws AppianException Any exception
     */
    public static ContentOutputStream uploadDocumentForWriting(ContentService contentService, Document document) throws AppianException {
        return contentService.upload(document, ContentConstants.UNIQUE_NONE);
    }
}
