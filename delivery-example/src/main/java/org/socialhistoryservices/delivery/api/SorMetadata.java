package org.socialhistoryservices.delivery.api;

import java.util.Map;

/**
 * Maps some of the data obtained from the metadata API from the Shared Object Repository.
 */
public class SorMetadata {
    private static final int TIFF_MASTER_DPI = 300;

    private boolean isMaster;
    private String contentType;
    private Map<String, String> content;

    public SorMetadata(boolean isMaster, String contentType, Map<String, String> content) {
        this.isMaster = isMaster;
        this.contentType = contentType;
        this.content = content;
    }

    /**
     * Whether the file is a master file.
     *
     * @return Whether the file is a master file.
     */
    public boolean isMaster() {
        return isMaster;
    }

    /**
     * The content type of the file.
     *
     * @return The content type of the file.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * The content metadata.
     *
     * @return The content metadata.
     */
    public Map<String, String> getContent() {
        return content;
    }

    /**
     * Try to determine if this file is a TIFF with the valid DPI.
     *
     * @return True if we can determine that the file is a TIFF with the valid DPI.
     */
    public boolean isTiff() {
        try {
            if (isMaster && contentType.equals("image/tiff")) {
                if (content.containsKey("x-resolution")) {
                    String xResolution = content.get("x-resolution");

                    // Try to obtain the number such that we can determine the DPI value
                    xResolution = xResolution.replaceAll("([^\\d]*)([\\d]*)(.*)", "$2");
                    int dpi = Integer.parseInt(xResolution);

                    return (dpi >= TIFF_MASTER_DPI);
                }
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
