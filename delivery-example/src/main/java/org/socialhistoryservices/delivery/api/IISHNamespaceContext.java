package org.socialhistoryservices.delivery.api;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

public class IISHNamespaceContext implements NamespaceContext {
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
        else if (prefix.equals("oai")) {
            return "http://www.openarchives.org/OAI/2.0/";
        }
        else if (prefix.equals("ead")) {
            return "urn:isbn:1-931666-22-9";
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
