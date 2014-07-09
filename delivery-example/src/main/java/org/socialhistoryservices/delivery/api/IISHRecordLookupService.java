/**
 * Copyright (C) 2013 International Institute of Social History
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.socialhistoryservices.delivery.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.socialhistoryservices.delivery.record.entity.ExternalHoldingInfo;
import org.socialhistoryservices.delivery.record.entity.ExternalRecordInfo;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Represents the api.socialhistoryservices.nl lookup service.
 */
public class IISHRecordLookupService implements RecordLookupService {

    private XPathExpression xpSearch, xpAll;

    // TODO: Remove duplicate Search vs non-search xpath expressions.
    private XPathExpression xpSearch245aTitle, xpSearch500aTitle, xpSearch600aTitle, xpSearch610aTitle, xpSearch650aTitle, xpSearch651aTitle, xpSearch245kTitle, xpSearch245bSubTitle;
    private XPathExpression xpSearchIdent;
    private XPathExpression xpSearchMeta, xpAuthor, xpAltAuthor, xpAlt2Author, xpAlt3Author;
    private XPathExpression xp245aTitle, xp500aTitle, xp600aTitle, xp610aTitle, xp650aTitle, xp651aTitle, xp245kTitle;
    private XPathExpression xp245bSubTitle, xpYear, xpSerialNumbers, xpSignatures, xpLeader;
    private XPathExpression xpNumberOfRecords;
    private static final Log logger = LogFactory.getLog(IISHRecordLookupService.class);

    private Properties properties;

    /**
     * Set the properties info.
     * @param p The properties to set.
     */
    public void setProperties(Properties p) {
        properties = p;
    }

    private class MARCNamespaceContext implements NamespaceContext {
        public String getNamespaceURI(String prefix) {
            if (prefix.equals("marc")) {
                return "http://www.loc.gov/MARC21/slim";
            }
            else if (prefix.equals("extraData") || prefix.equals("ns2")) {
                return "http://oclc.org/srw/extraData";
            }
            else if (prefix.equals("iisg")) {
                return "http://www.iisg.nl/api/sru/";
            }
            else if (prefix.equals("srw") || prefix.equals("ns1")) {
                return "http://www.loc.gov/zing/srw/";
            }
            return null;
        }

        public Iterator getPrefixes(String val) {
            return null;
        }

        public String getPrefix(String uri) {
            return null;
        }
    }

    /**
     * Constructor.
     */
    public IISHRecordLookupService() {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(new MARCNamespaceContext());

        try {
            xpAll = xpath.compile("/srw:searchRetrieveResponse");
            xpSearch = xpath.compile("//srw:record");
            xpSearch245aTitle = xpath.compile("ns1:recordData/marc:record/" +
                    "marc:datafield[@tag=245]" +
                    "/marc:subfield[@code=\"a\"]");
            xpSearch245bSubTitle = xpath.compile("ns1:recordData/marc:record/" +
                    "marc:datafield[@tag=245]" +
                    "/marc:subfield[@code=\"b\"]");
            xpSearch500aTitle = xpath.compile("ns1:recordData/marc:record/" +
                    "marc:datafield[@tag=500]" +
                    "/marc:subfield[@code=\"a\"]");
            xpSearch600aTitle = xpath.compile("ns1:recordData/marc:record/" +
                    "marc:datafield[@tag=600]" +
                    "/marc:subfield[@code=\"a\"]");
            xpSearch610aTitle = xpath.compile("ns1:recordData/marc:record/" +
                    "marc:datafield[@tag=610]" +
                    "/marc:subfield[@code=\"a\"]");
            xpSearch650aTitle = xpath.compile("ns1:recordData/marc:record/" +
                    "marc:datafield[@tag=650]" +
                    "/marc:subfield[@code=\"a\"]");
            xpSearch651aTitle = xpath.compile("ns1:recordData/marc:record/" +
                    "marc:datafield[@tag=651]" +
                    "/marc:subfield[@code=\"a\"]");
            xpSearch245kTitle = xpath.compile("ns1:recordData/marc:record/" +
                    "marc:datafield[@tag=245]" +
                    "/marc:subfield[@code=\"k\"]");

            xpSearchIdent = xpath.compile("ns1:extraRecordData/" +
                    "extraData:extraData/iisg:identifier");

            xpSearchMeta = xpath.compile("//marc:record");
            xpAuthor = xpath.compile("marc:datafield[@tag=100]" +
                    "/marc:subfield[@code=\"a\"]");
            xpAltAuthor = xpath.compile("marc:datafield[@tag=110]" +
                    "/marc:subfield[@code=\"a\"]");
            xpAlt2Author = xpath.compile("marc:datafield[@tag=700]" +
                    "/marc:subfield[@code=\"a\"]");
            xpAlt3Author = xpath.compile("marc:datafield[@tag=710]" +
                    "/marc:subfield[@code=\"a\"]");
            xp245aTitle = xpath.compile("marc:datafield[@tag=245]" +
                    "/marc:subfield[@code=\"a\"]");
            xp500aTitle = xpath.compile("marc:datafield[@tag=500]" +
                    "/marc:subfield[@code=\"a\"]");
            xp600aTitle = xpath.compile("marc:datafield[@tag=600]" +
                    "/marc:subfield[@code=\"a\"]");
            xp610aTitle = xpath.compile("marc:datafield[@tag=610]" +
                    "/marc:subfield[@code=\"a\"]");
            xp650aTitle = xpath.compile("marc:datafield[@tag=650]" +
                    "/marc:subfield[@code=\"a\"]");
            xp651aTitle = xpath.compile("marc:datafield[@tag=651]" +
                    "/marc:subfield[@code=\"a\"]");
            xp245kTitle = xpath.compile("marc:datafield[@tag=245]" +
                    "/marc:subfield[@code=\"k\"]");
            xp245bSubTitle = xpath.compile("marc:datafield[@tag=245]" +
                    "/marc:subfield[@code=\"b\"]");
            xpYear = xpath.compile("marc:datafield[@tag=260]" +
                    "/marc:subfield[@code=\"c\"]");
            xpSignatures = xpath.compile("marc:datafield[@tag=852]" +
                    "/marc:subfield[@code=\"j\"]");
            xpSerialNumbers = xpath.compile("marc:datafield[@tag=866]" +
                    "/marc:subfield[@code=\"a\"]");
            xpLeader = xpath.compile("marc:leader");
            xpNumberOfRecords = xpath.compile("//ns1:numberOfRecords");

        }
        catch (XPathExpressionException ex) {
            logger.error("Failed initializing XPath expressions");
            // Uh-oh
        }
    }

    /**
     * Actually execute the search.
     *
     * @param query The query to add to the search.
     * @param nrResultsPerPage The number of results to get per page.
     * @param resultStart The result number to start the page with (1 <= resultStart <= result count).
     * @return A list of nodes returned by the API service.
     */
    private Node doSearch(String query, int nrResultsPerPage, int resultStart) {
        String search;
        search = "version=1.1";
        search += "&operation=searchRetrieve";
        search += "&recordSchema=info:srw/schema/1/marcxml-v1.1";
        search += "&maximumRecords="+nrResultsPerPage;
        search += "&startRecord="+resultStart;
        search += "&resultSetTTL=300";
        search += "&recordPacking=xml";
        search += "&sortKeys=";
        search += "&stylesheet=";

        search += "&query=iisg.collectionName+=+\"iish.evergreen.biblio\"";
        search += "+or+iisg.collectionName+=+\"iish.archieven\"";
        search += "+or+iisg.collectionName+=+\"iish.eci\"";

        if (!query.isEmpty()) {
            search += "+and+"+query;
        }

        try {
            String apiProto = properties.getProperty("prop_apiProto");
            String apiDomain = properties.getProperty("prop_apiDomain");
            String apiBase = properties.getProperty("prop_apiBase");
            int apiPort = Integer.parseInt(properties.getProperty("prop_apiPort"));

            URI uri = new URI(apiProto, null, apiDomain, apiPort, apiBase, search, null);
            URL req = uri.toURL();
            logger.debug(String.format("doSearch(): Querying SRW API: %s", req.toString()));
            URLConnection conn = req.openConnection();

            BufferedReader rdr = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
            return (Node)xpAll.evaluate(new InputSource(rdr), XPathConstants.NODE);
        } catch (IOException ex) {
            logger.debug("doSearch(): API Connect Failed", ex);
            return null;
        } catch (URISyntaxException ex) {
            logger.debug("doSearch(): Invalid URI syntax", ex);
            return null;
        } catch (XPathExpressionException e) {
            logger.debug("doSearch(): Invalid XPath", e);
            return null;
        }
    }

    /**
     * Search for records with the specified title.
     * @param title The title to search for.
     * @return A map of {pid,title} key-value pairs.
     */
    public PageChunk getRecordsByTitle(String title, int resultCountPerChunk, int resultStart) {
        PageChunk pc = new PageChunk(resultCountPerChunk, resultStart);
        if (title == null) return pc;
        try {
            title = URLEncoder.encode(title, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw  new RuntimeException(e);
        }
        String query = "marc.245+all+\""+title+"\"";

        logger.debug(String.format("getRecordsByTitle(title: %s, resultcountPerChunk: %d, resultStart: %d)", title, resultCountPerChunk, resultStart));
        Node out = doSearch(query, pc.getResultCountPerChunk(), pc.getResultStart());

        NodeList search = null;
        try {
            search = (NodeList)xpSearch.evaluate(out, XPathConstants.NODESET);
            pc.setTotalResultCount(((Double) xpNumberOfRecords.evaluate(out, XPathConstants.NUMBER)).intValue());
        } catch (XPathExpressionException e) {
            logger.debug("getRecordsByTitle(): Invalid XPath", e);
            return pc;
        }

        if (search.getLength() == 0) {
            return pc;
        }

        int resCount = search.getLength();
        for (int i = 0; i < resCount; ++i) {
            Node node = search.item(i);

            String recPid, recTitle;
            try {
                recPid = xpSearchIdent.evaluate(node);

                recTitle = evaluateSearchTitle(node);
            } catch (XPathExpressionException ex) {
                continue;
            }
            String recSubTitle = "";
            try {
                 recSubTitle = " " + xpSearch245bSubTitle.evaluate(node).trim().replaceAll("[/:]$", "");
            } catch (XPathExpressionException ignored) {
            }

            if (recTitle != null && recPid != null) {
                // Strip trailing slashes.
                recTitle = recTitle.trim().replaceAll("[/:]$", "");
                if (!recTitle.isEmpty())
                    pc.getResults().put(recPid, recTitle + recSubTitle);
            }
        }
        return pc;
    }

    private String evaluateSearchTitle(Node node) throws XPathExpressionException {
        String recTitle;
        recTitle = xpSearch245aTitle.evaluate(node);
        if (recTitle.isEmpty())
            recTitle = xpSearch500aTitle.evaluate(node);
        if (recTitle.isEmpty())
            recTitle = xpSearch600aTitle.evaluate(node);
        if (recTitle.isEmpty())
            recTitle = xpSearch610aTitle.evaluate(node);
        if (recTitle.isEmpty())
            recTitle = xpSearch650aTitle.evaluate(node);
        if (recTitle.isEmpty())
            recTitle = xpSearch651aTitle.evaluate(node);
        if (recTitle.isEmpty())
            recTitle = xpSearch245kTitle.evaluate(node);
        return recTitle;
    }

    /**
     * Maps a PID to metadata of a record.
     * @param pid The PID to lookup.
     * @return The metadata of the record, if found.
     * @throws NoSuchPidException Thrown when the PID is not found.
     */
    public ExternalRecordInfo getRecordMetaDataByPid(String pid) throws
            NoSuchPidException {
        ExternalRecordInfo externalInfo = new ExternalRecordInfo();

        logger.debug(String.format("getRecordMetaDataByPid(%s)", pid));
        Node node = searchByPid(pid);

        String author = evaluateAuthor(node);
        if (author != null && !author.isEmpty()) {
            externalInfo.setAuthor(stripToSize(author, 125));
        }
        String title = evaluateTitle(node);
        if (title != null && !title.isEmpty()) {

            // Strip trailing slashes
            title = title.trim().replaceAll("[/:]$", "");
            String subTitle = evaluateSubTitle(node);
            if (subTitle != null && !subTitle.isEmpty()) {
                title += " " + subTitle.trim().replaceAll("[/:]$", "");
            }

            // Some titles from the API SRW exceed 255 characters. Strip them to
            // 251 characters (up to 252 , exclusive) to save up for the dash
            // and \0 termination.
            // EDIT: Trim this to ~125 characters for readability (this is the current max size of the field).
	        // EDIT 2: Back to 255 characters and removed signature from title
            title = stripToSize(title, 255);

            externalInfo.setTitle(title);
        } else {
            externalInfo.setTitle("Unknown Record");
        }


        String year = evaluateYear(node);
        if (year != null && !year.isEmpty()) {
            externalInfo.setDisplayYear(stripToSize(year, 30));
        }

        externalInfo.setMaterialType(evaluateMaterialType(node));


        return externalInfo;
    }

    /**
     * Get a map of holding signatures associated with this PID (if any
     * found), linking to additional holding info provided by the API.
     * @param pid The PID to search for.
     * @return A map of found (signature,holding info) tuples,
     * or an empty map if none were found.
     * @throws NoSuchPidException Thrown when the PID being searched for is
     * not found in the API.
     */
    public Map<String, ExternalHoldingInfo> getHoldingMetadataByPid(String pid) throws NoSuchPidException {
        Map<String, ExternalHoldingInfo> retMap = new
                           HashMap<String, ExternalHoldingInfo>();


        try {
            // TODO: 866 is not always available.
            logger.debug(String.format("getHoldingMetaDataByPid(%s)", pid));
            Node node = searchByPid(pid);
            NodeList sigNodes = (NodeList) xpSignatures.evaluate(node,
                    XPathConstants.NODESET);
            NodeList serNodes = (NodeList) xpSerialNumbers.evaluate(node,
                    XPathConstants.NODESET);

            if (sigNodes == null || serNodes == null)
                return retMap;

            for (int i = 0; i < sigNodes.getLength(); i++) {
                Node sig = sigNodes.item(i);
                Node ser = serNodes.item(i);

                if (sig == null) continue;
                
                ExternalHoldingInfo eh= new ExternalHoldingInfo();
                if (ser != null)
                    eh.setSerialNumbers(ser.getTextContent().replace(",", ", "));
                retMap.put(sig.getTextContent(),eh);
            }
        } catch (XPathExpressionException ignored) {
            logger.debug("getHoldingMetaDataByPid(): Invalid XPath", ignored);
        }
        return retMap;
    }

    /**
     * Search metadata by PID.
     * @param pid The PID to search for.
     * @return The main record node.
     * @throws NoSuchPidException Thrown when the search returns nothing.
     */
    private Node searchByPid(String pid) throws NoSuchPidException {
        String itemSeparator = properties.getProperty("prop_itemSeparator");

        if (pid.contains(itemSeparator)) {
            int idx = pid.lastIndexOf(itemSeparator);

            pid = pid.substring(0, idx);
        }

        String encodedPid;
        try {
            encodedPid = URLEncoder.encode(pid, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String query = "dc.identifier+=+\""+encodedPid+"\"";

        Node all = doSearch(query, 1, 1);

        NodeList search = null;
        int resultCount = 0;
        try {
            resultCount = ((Double) xpNumberOfRecords.evaluate(all, XPathConstants.NUMBER)).intValue();
            search = (NodeList)xpSearchMeta.evaluate(all, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            logger.debug("searchByPid(): Invalid XPath", e);
            throw new NoSuchPidException();
            // Handle this in case the IISH API is down.
        }

         if (resultCount == 0 || search == null) {
            logger.debug("searchByPid(): Zero results");
            throw new NoSuchPidException();
        }

        return search.item(0);
    }

    private String stripToSize(String string, int size) {
        if (string.length() > size) {
            string = string.substring(0, size);
            string = string.trim();
        }
        return string;
    }

    private ExternalRecordInfo.MaterialType evaluateMaterialType(Node node) {
        try {
            String leader = xpLeader.evaluate(node);
            return leaderToMaterialType(leader);
        } catch (XPathExpressionException e) {
            return ExternalRecordInfo.MaterialType.OTHER;
        }

    }

    private ExternalRecordInfo.MaterialType leaderToMaterialType(String leader) {
        String format;

        // Invalid leader fix.
        /*if (leader.length() < 9) {
            format = leader.substring(1, 3);
        } else {*/
            format = leader.substring(6, 8);
        //}

        if (format.equals("ar") || format.equals("as") || format.equals("ps"))
            return ExternalRecordInfo.MaterialType.SERIAL;

        if (format.equals("am") || format.equals("pm"))
            return ExternalRecordInfo.MaterialType.BOOK;

        if (format.equals("im") || format.equals("pi") || format.equals("ic"))
            return ExternalRecordInfo.MaterialType.SOUND;

        if (format.equals("do") || format.equals("oc"))
            return ExternalRecordInfo.MaterialType.DOCUMENTATION;

        if (format.equals("bm") || format.equals("pc"))
            return ExternalRecordInfo.MaterialType.ARCHIVE;

        if (format.equals("av") || format.equals("rm") || format.equals("gm") || format.equals("pv") || format.equals("km") || format.equals("kc"))
            return ExternalRecordInfo.MaterialType.VISUAL;
        
        return ExternalRecordInfo.MaterialType.OTHER;
    }

    private String evaluateYear(Node node) {
        try {
            return xpYear.evaluate(node);
        } catch (XPathExpressionException e) {
            return null;
        }

    }


    private String evaluateTitle(Node node) {
        try {
            String title = xp245aTitle.evaluate(node);

            if (title.isEmpty())
                title = xp500aTitle.evaluate(node);
            if (title.isEmpty())
                title = xp600aTitle.evaluate(node);
            if (title.isEmpty())
                title = xp610aTitle.evaluate(node);
            if (title.isEmpty())
                title = xp650aTitle.evaluate(node);
            if (title.isEmpty())
                title = xp651aTitle.evaluate(node);
            if (title.isEmpty())
                title = xp245kTitle.evaluate(node);
            return title;
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    private String evaluateSubTitle(Node node) {
        try {
            return xp245bSubTitle.evaluate(node);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    /**
     * Fetches author from MARCXML, first tries 100a, then 110a.
     * @param node The XML node to execute the XPath on.
     * @return The author found, or null if not present.
     */
    private String evaluateAuthor(Node node) {

        try {
            String author = xpAuthor.evaluate(node);

            if (author.isEmpty())
                author = xpAltAuthor.evaluate(node);
            if (author.isEmpty())
                author = xpAlt2Author.evaluate(node);
            if (author.isEmpty())
                author = xpAlt3Author.evaluate(node);
            return author;

        } catch (XPathExpressionException ex) {
            return null;
        }
    }
}
