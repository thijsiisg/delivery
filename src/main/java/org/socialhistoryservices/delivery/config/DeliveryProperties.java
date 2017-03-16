package org.socialhistoryservices.delivery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Igor on 3/13/2017.
 */
@Component
@ConfigurationProperties(prefix = "delivery")
public class DeliveryProperties {

    private String log4j = "log4j.xml";
    private String apiBase = "/solr/all/srw";
    private String apiDomain = "api.socialhistoryservices.org";
    private int apiPort = 80;
    private String apiProto = "http";
    private String casUrl = "https://login.iisg.nl/cas";
    private String dateFormat = "yyyy-MM-dd";
    private String dbDialiect = "org.hibernate.dialect.PostgreSQLDialect";
    private String dbDriver = "org.postgresql.Driver";
    private String dbPassword = "delivery";
    private String dbUrl = "jdbc:postgresql://localhost/delivery";
    private String dbUsername = "delivery";
    private int externalInfoMinDaysCache = 30;
    private String holdingSeperator = "\\^";
    private String itemSeperator = ".";
    private String ldapManagerDn = "cn=admin,dc=socialhistoryservices,dc=org";
    private String ldapManagerPassword = "test";
    private String ldapUrl = "ldap://ds0.socialhistoryservices.org/dc=socialhistoryservices,dc=org";
    private String ldapUserSearchBase = "ou=users";
    private String ldapUserSearchFilter = "cn={0}";
    private boolean loadInitialData;
    private boolean mailEnabled;
    private String mailHost = "mailrelay0.socialhistoryservices.org";
    private String mailPassword = "test";
    private int mailPort = 25;
    private String mailReadingRoom = "blabla@iisg.nl";
    private String mailSystemAddress = "n0r3ply@iisg.nl";
    private String mailUsername = "delivery-web-be0.socialhistory.org@socialhistoryservices.org";
    private String payWayAddress = "https://payway-acc.socialhistoryservices.org/api";
    private String payWayPassPhraseIn = "bla";
    private String PayWayPassPhraseOut = "bla";
    private String payWayProjectName = "delivery";
    private int permissionMaxPageLen = 100;
    private int permissionPageLen = 20;
    private int permissionPageStepSize = 10;
    private String pidSeperator = ",";
    private String reCaptchaPrivateKey = "bla";
    private String ReCaptchaPublicKey = "bla";
    private String reCaptchaTheme = "clean";
    private int reproductionAdministrationCosts = 6;
    private int reproductionMaxDaysPayment = 21;
    private int reproductionMaxDaysReminder = 14;
    private int reproductionBtwPercentage = 21;
    private String requestAutoPrintStartTime = "9:00";
    private String requestLatestTime = "16:00";
    private int requestMaxPageLen = 100;
    private int requestPageLen = 20;
    private int requestPageStepSize = 10;
    private int reservationMaxDaysInAdvance = 31;
    private int reservationMaxItems = 3;
    private String sorAccessToken = "bla";
    private String sorAddress = "http://disseminate.objectrepository.org";
    private String timeFormat = "HH:mm:ss";
    private String urlSearch = "search-acc.socialhistory.org";
    private String urlSelf = "http://localhost:8181";
    private int recordPageLen = 20;

    public String getLog4j() {
        return log4j;
    }

    public void setLog4j(String log4j) {
        this.log4j = log4j;
    }

    public String getApiBase() {
        return apiBase;
    }

    public void setApiBase(String apiBase) {
        this.apiBase = apiBase;
    }

    public String getApiDomain() {
        return apiDomain;
    }

    public void setApiDomain(String apiDomain) {
        this.apiDomain = apiDomain;
    }

    public int getApiPort() {
        return apiPort;
    }

    public void setApiPort(int apiPort) {
        this.apiPort = apiPort;
    }

    public String getApiProto() {
        return apiProto;
    }

    public void setApiProto(String apiProto) {
        this.apiProto = apiProto;
    }

    public String getCasUrl() {
        return casUrl;
    }

    public void setCasUrl(String casUrl) {
        this.casUrl = casUrl;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDbDialiect() {
        return dbDialiect;
    }

    public void setDbDialiect(String dbDialiect) {
        this.dbDialiect = dbDialiect;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public int getExternalInfoMinDaysCache() {
        return externalInfoMinDaysCache;
    }

    public void setExternalInfoMinDaysCache(int externalInfoMinDaysCache) {
        this.externalInfoMinDaysCache = externalInfoMinDaysCache;
    }

    public String getHoldingSeperator() {
        return holdingSeperator;
    }

    public void setHoldingSeperator(String holdingSeperator) {
        this.holdingSeperator = holdingSeperator;
    }

    public String getItemSeperator() {
        return itemSeperator;
    }

    public void setItemSeperator(String itemSeperator) {
        this.itemSeperator = itemSeperator;
    }

    public String getLdapManagerDn() {
        return ldapManagerDn;
    }

    public void setLdapManagerDn(String ldapManagerDn) {
        this.ldapManagerDn = ldapManagerDn;
    }

    public String getLdapManagerPassword() {
        return ldapManagerPassword;
    }

    public void setLdapManagerPassword(String ldapManagerPassword) {
        this.ldapManagerPassword = ldapManagerPassword;
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public String getLdapUserSearchBase() {
        return ldapUserSearchBase;
    }

    public void setLdapUserSearchBase(String ldapUserSearchBase) {
        this.ldapUserSearchBase = ldapUserSearchBase;
    }

    public String getLdapUserSearchFilter() {
        return ldapUserSearchFilter;
    }

    public void setLdapUserSearchFilter(String ldapUserSearchFilter) {
        this.ldapUserSearchFilter = ldapUserSearchFilter;
    }

    public boolean isLoadInitialData() {
        return loadInitialData;
    }

    public void setLoadInitialData(boolean loadInitialData) {
        this.loadInitialData = loadInitialData;
    }

    public boolean isMailEnabled() {
        return mailEnabled;
    }

    public void setMailEnabled(boolean mailEnabled) {
        this.mailEnabled = mailEnabled;
    }

    public String getMailHost() {
        return mailHost;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public int getMailPort() {
        return mailPort;
    }

    public void setMailPort(int mailPort) {
        this.mailPort = mailPort;
    }

    public String getMailReadingRoom() {
        return mailReadingRoom;
    }

    public void setMailReadingRoom(String mailReadingRoom) {
        this.mailReadingRoom = mailReadingRoom;
    }

    public String getMailSystemAddress() {
        return mailSystemAddress;
    }

    public void setMailSystemAddress(String mailSystemAddress) {
        this.mailSystemAddress = mailSystemAddress;
    }

    public String getMailUsername() {
        return mailUsername;
    }

    public void setMailUsername(String mailUsername) {
        this.mailUsername = mailUsername;
    }

    public String getPayWayAddress() {
        return payWayAddress;
    }

    public void setPayWayAddress(String payWayAddress) {
        this.payWayAddress = payWayAddress;
    }

    public String getPayWayPassPhraseIn() {
        return payWayPassPhraseIn;
    }

    public void setPayWayPassPhraseIn(String payWayPassPhraseIn) {
        this.payWayPassPhraseIn = payWayPassPhraseIn;
    }

    public String getPayWayPassPhraseOut() {
        return PayWayPassPhraseOut;
    }

    public void setPayWayPassPhraseOut(String payWayPassPhraseOut) {
        this.PayWayPassPhraseOut = payWayPassPhraseOut;
    }

    public String getPayWayProjectName() {
        return payWayProjectName;
    }

    public void setPayWayProjectName(String payWayProjectName) {
        this.payWayProjectName = payWayProjectName;
    }

    public int getPermissionMaxPageLen() {
        return permissionMaxPageLen;
    }

    public void setPermissionMaxPageLen(int permissionMaxPageLen) {
        this.permissionMaxPageLen = permissionMaxPageLen;
    }

    public int getPermissionPageLen() {
        return permissionPageLen;
    }

    public void setPermissionPageLen(int permissionPageLen) {
        this.permissionPageLen = permissionPageLen;
    }

    public int getPermissionPageStepSize() {
        return permissionPageStepSize;
    }

    public void setPermissionPageStepSize(int permissionPageStepSize) {
        this.permissionPageStepSize = permissionPageStepSize;
    }

    public String getPidSeperator() {
        return pidSeperator;
    }

    public void setPidSeperator(String pidSeperator) {
        this.pidSeperator = pidSeperator;
    }

    public String getReCaptchaPrivateKey() {
        return reCaptchaPrivateKey;
    }

    public void setReCaptchaPrivateKey(String reCaptchaPrivateKey) {
        this.reCaptchaPrivateKey = reCaptchaPrivateKey;
    }

    public String getReCaptchaPublicKey() {
        return ReCaptchaPublicKey;
    }

    public void setReCaptchaPublicKey(String reCaptchaPublicKey) {
        this.ReCaptchaPublicKey = reCaptchaPublicKey;
    }

    public String getReCaptchaTheme() {
        return reCaptchaTheme;
    }

    public void setReCaptchaTheme(String reCaptchaTheme) {
        this.reCaptchaTheme = reCaptchaTheme;
    }

    public int getReproductionAdministrationCosts() {
        return reproductionAdministrationCosts;
    }

    public void setReproductionAdministrationCosts(int reproductionAdministrationCosts) {
        this.reproductionAdministrationCosts = reproductionAdministrationCosts;
    }

    public int getReproductionMaxDaysPayment() {
        return reproductionMaxDaysPayment;
    }

    public void setReproductionMaxDaysPayment(int reproductionMaxDaysPayment) {
        this.reproductionMaxDaysPayment = reproductionMaxDaysPayment;
    }

    public int getReproductionMaxDaysReminder() {
        return reproductionMaxDaysReminder;
    }

    public void setReproductionMaxDaysReminder(int reproductionMaxDaysReminder) {
        this.reproductionMaxDaysReminder = reproductionMaxDaysReminder;
    }

    public int getReproductionBtwPercentage() {
        return reproductionBtwPercentage;
    }

    public void setReproductionBtwPercentage(int reproductionBtwPercentage) {
        this.reproductionBtwPercentage = reproductionBtwPercentage;
    }

    public String getRequestAutoPrintStartTime() {
        return requestAutoPrintStartTime;
    }

    public void setRequestAutoPrintStartTime(String requestAutoPrintStartTime) {
        this.requestAutoPrintStartTime = requestAutoPrintStartTime;
    }

    public String getRequestLatestTime() {
        return requestLatestTime;
    }

    public void setRequestLatestTime(String requestLatestTime) {
        this.requestLatestTime = requestLatestTime;
    }

    public int getRequestMaxPageLen() {
        return requestMaxPageLen;
    }

    public void setRequestMaxPageLen(int requestMaxPageLen) {
        this.requestMaxPageLen = requestMaxPageLen;
    }

    public int getRequestPageLen() {
        return requestPageLen;
    }

    public void setRequestPageLen(int requestPageLen) {
        this.requestPageLen = requestPageLen;
    }

    public int getRequestPageStepSize() {
        return requestPageStepSize;
    }

    public void setRequestPageStepSize(int requestPageStepSize) {
        this.requestPageStepSize = requestPageStepSize;
    }

    public int getReservationMaxDaysInAdvance() {
        return reservationMaxDaysInAdvance;
    }

    public void setReservationMaxDaysInAdvance(int reservationMaxDaysInAdvance) {
        this.reservationMaxDaysInAdvance = reservationMaxDaysInAdvance;
    }

    public int getReservationMaxItems() {
        return reservationMaxItems;
    }

    public void setReservationMaxItems(int reservationMaxItems) {
        this.reservationMaxItems = reservationMaxItems;
    }

    public String getSorAccessToken() {
        return sorAccessToken;
    }

    public void setSorAccessToken(String sorAccessToken) {
        this.sorAccessToken = sorAccessToken;
    }

    public String getSorAddress() {
        return sorAddress;
    }

    public void setSorAddress(String sorAddress) {
        this.sorAddress = sorAddress;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getUrlSearch() {
        return urlSearch;
    }

    public void setUrlSearch(String urlSearch) {
        this.urlSearch = urlSearch;
    }

    public String getUrlSelf() {
        return urlSelf;
    }

    public void setUrlSelf(String urlSelf) {
        this.urlSelf = urlSelf;
    }

    public int getRecordPageLen() {
        return recordPageLen;
    }

    public void setRecordPageLen(int recordPageLen) {
        this.recordPageLen = recordPageLen;
    }
}
