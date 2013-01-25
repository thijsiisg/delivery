/*
 * Copyright 2011 International Institute of Social History
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.iisg.deliverance.api;

import org.iisg.deliverance.record.entity.ExternalHoldingInfo;
import org.iisg.deliverance.record.entity.ExternalRecordInfo;
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
 * Represents the api.iisg.nl lookup service.
 */
public class IISHRecordLookupService implements RecordLookupService {

    private XPathExpression xpSearch;
    private XPathExpression xpSearchTitle, xpSearchSubTitle;
    private XPathExpression xpSearchIdent;
    private XPathExpression xpSearchMeta, xpAuthor, xpAltAuthor, xpTitle;
    private XPathExpression xpSubTitle, xpYear, xpSerialNumbers, xpSignatures, xpLeader;

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


            xpSearch = xpath.compile("//srw:record");
            xpSearchTitle = xpath.compile("ns1:recordData/marc:record/" +
                    "marc:datafield[@tag=245]" +
                    "/marc:subfield[@code=\"a\"]");
            xpSearchSubTitle = xpath.compile("ns1:recordData/marc:record/" +
                    "marc:datafield[@tag=245]" +
                    "/marc:subfield[@code=\"b\"]");

            xpSearchIdent = xpath.compile("ns1:extraRecordData/" +
                    "extraData:extraData/iisg:identifier");

            xpSearchMeta = xpath.compile("//marc:record");
            xpAuthor = xpath.compile("marc:datafield[@tag=100]" +
                    "/marc:subfield[@code=\"a\"]");
            xpAltAuthor = xpath.compile("marc:datafield[@tag=110]" +
                    "/marc:subfield[@code=\"a\"]");
            xpTitle = xpath.compile("marc:datafield[@tag=245]" +
                    "/marc:subfield[@code=\"a\"]");
            xpSubTitle = xpath.compile("marc:datafield[@tag=245]" +
                    "/marc:subfield[@code=\"b\"]");
            xpYear = xpath.compile("marc:datafield[@tag=260]" +
                    "/marc:subfield[@code=\"c\"]");
            xpSignatures = xpath.compile("marc:datafield[@tag=852]" +
                    "/marc:subfield[@code=\"j\"]");
            xpSerialNumbers = xpath.compile("marc:datafield[@tag=866]" +
                    "/marc:subfield[@code=\"a\"]");
            xpLeader = xpath.compile("marc:leader");

        }
        catch (XPathExpressionException ex) {
            // Uh-oh
        }
    }

    /**
     * Actually execute the search.
     * @param xp The XPath expression to use to evaluate the result.
     * @param query The query to add to the search.
     * @param results The number of results to retrieve.
     * @return A list of nodes returned by the API service.
     */
    private NodeList doSearch(XPathExpression xp, String query, int results) {
        String search;
        search = "version=1.1";
        search += "&operation=searchRetrieve";
        search += "&recordSchema=info:srw/schema/1/marcxml-v1.1";
        search += "&maximumRecords="+results;
        search += "&startRecord=1";
        search += "&resultSetTTL=300";
        search += "&recordPacking=xml";
        search += "&sortKeys=";
        search += "&stylesheet=";

        search += "&query=iisg.collectionName+=+\"iish.archieven\"";
        search += "+or+iisg.collectionName+=+\"iish.evergreen.biblio\"";

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
            URLConnection conn = req.openConnection();

            BufferedReader rdr = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
            InputSource input = new InputSource(rdr);

            NodeList res = (NodeList)xp.evaluate(input, XPathConstants.NODESET);

            if (res.getLength() == 0) {
                return null;
            }
            return res;
        } catch (IOException ex) {
            return null;
        } catch (URISyntaxException ex) {
            return null;
        } catch (XPathExpressionException ex) {
            return null;
        }
    }


    /**
     * Search for records with the specified title.
     * @param title The title to search for.
     * @return A map of {pid,title} key-value pairs.
     */
    public Map<String, String> getRecordsByTitle(String title) {
        Map<String, String> results = new HashMap<String, String>();
        if (title == null) return results;
        try {
            title = URLEncoder.encode(title, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw  new RuntimeException(e);
        }
        String query = "marc.245+all+\""+title+"\"";

        NodeList search = doSearch(xpSearch, query, 15);

        if (search != null) {
            int resCount = search.getLength();
            for (int i = 0; i < resCount; ++i) {
                Node node = search.item(i);

                String recPid, recTitle;
                try {
                    recPid = xpSearchIdent.evaluate(node);
                    recTitle = xpSearchTitle.evaluate(node);
                } catch (XPathExpressionException ex) {
                    continue;
                }
                String recSubTitle = "";
                try {
                     recSubTitle = " " + xpSearchSubTitle.evaluate(node).trim().replaceAll("[/:]$", "");
                } catch (XPathExpressionException ignored) {
                }

                if (recTitle != null && recPid != null) {
                    // Strip trailing slashes.
                    recTitle = recTitle.trim().replaceAll("[/:]$", "");
                    if (!recTitle.isEmpty())
                        results.put(recPid, recTitle + recSubTitle);
                }
            }
        }
        return results;
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

        Node node = searchByPid(pid);

        String author = evaluateAuthor(node);
        if (author != null && !author.isEmpty()) {
            externalInfo.setAuthor(stripToSize(author, 255));
        }
        String title = evaluateTitle(node);
        if (title != null && !title.isEmpty()) {

            // Strip trailing slashes
            title = title.trim().replaceAll("[/:]$", "");
            String subTitle = evaluateSubTitle(node);
            if (subTitle != null && !subTitle.isEmpty()) {
                title += " " + subTitle.trim().replaceAll("[/:]$", "");
            }

            String itemSep = properties.getProperty("prop_itemSeparator");
            String itemNr = "";
            if (pid.contains(itemSep)) {
                int idx = pid.lastIndexOf(itemSep);
                itemNr = (idx < pid.length()-1) ? pid.substring(idx+1) : "";
            }
            // Some titles from the API SRW exceed 255 characters. Strip them to
            // 251 characters (up to 252 , exclusive) to save up for the dash
            // and \0 termination.

            title = stripToSize(title, 252 - itemNr.length());
            
            if (itemNr.length() > 0)
                title += " - " + itemNr;

            externalInfo.setTitle(title);
        } else {
            // Consider throwing a NoSuchPid exception instead?
            externalInfo.setTitle("Unknown Record");
        }


        String year = evaluateYear(node);
        if (year != null && !year.isEmpty()) {
            externalInfo.setDisplayYear(stripToSize(year, 255));
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

        NodeList search = doSearch(xpSearchMeta, query, 1);

        if (search == null) {
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
            return xpTitle.evaluate(node);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    private String evaluateSubTitle(Node node) {
        try {
            return xpSubTitle.evaluate(node);
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
            return xpAuthor.evaluate(node);

        } catch (XPathExpressionException ex) {
            try {
                return xpAltAuthor.evaluate(node);
            } catch (XPathExpressionException e) {
                return null;
            }
        }
    }
}
