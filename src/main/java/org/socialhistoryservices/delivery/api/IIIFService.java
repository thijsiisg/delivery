package org.socialhistoryservices.delivery.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.socialhistoryservices.delivery.record.entity.Record;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the International Image Interoperability Framework (IIIF) service.
 */
public class IIIFService {
    private static final Log LOGGER = LogFactory.getLog(IIIFService.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String url;
    private String accessToken;

    public IIIFService(String url, String accessToken) {
        this.url = url;
        this.accessToken = accessToken;
    }

    public void registerToken(String token, Record record) throws IIIFServiceException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            Record parent = record.getParent();
            String collection = (parent != null)
                    ? parent.getHoldings().get(0).getSignature() + "." + record.getHoldings().get(0).getSignature()
                    : record.getHoldings().get(0).getSignature();

            LocalDate from = LocalDate.now();
            LocalDate to = LocalDate.now().plusMonths(1);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("access_token", this.accessToken));
            params.add(new BasicNameValuePair("token", token));
            params.add(new BasicNameValuePair("collection", collection));
            params.add(new BasicNameValuePair("from", from.format(DATE_FORMAT)));
            params.add(new BasicNameValuePair("to", to.format(DATE_FORMAT)));

            HttpPost httpPost = new HttpPost(this.url + "/admin/register_token");
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            LOGGER.debug(String.format("registerToken(): Sending token to IIIF service: %s", httpPost.getRequestLine()));

            httpClient.execute(httpPost);
        }
        catch (IOException ioe) {
            LOGGER.error("registerToken(): IIIF server connection failed", ioe);
            throw new IIIFServiceException(
                    String.format("Failed to register the token %s for record %s!", token, record.getPid()), ioe);
        }
    }
}
