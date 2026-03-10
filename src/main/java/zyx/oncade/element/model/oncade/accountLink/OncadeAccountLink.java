package zyx.oncade.element.model.oncade.accountLink;

import java.time.Instant;

import org.bson.Document;

public class OncadeAccountLink {

    public static final String COLLECTION_NAME = "oncade_account_link";


    private String id;
    private String url;
    private String userRef;
    private String sessionKey;
    private String namazuUserId;
    private String lastIdempotencyKey;

    public OncadeAccountLink() {
    }

    public OncadeAccountLink(String url, String userRef, String sessionKey, String namazuUserId, String idempotencyKey) {
        this.url = url;
        this.userRef = userRef;
        this.sessionKey = sessionKey;
        this.namazuUserId = namazuUserId;
        this.lastIdempotencyKey = idempotencyKey;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getUserRef() {
        return userRef;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public String getNamazuUserId() {
        return namazuUserId;
    }

    public void setNamazuUserId(String namazuUserIding) {
        this.namazuUserId = namazuUserIding;
    }

    public String getLastIdempotencyKey() {
        return lastIdempotencyKey;
    }

    public void setLastIdempotencyKey(String lastIdempotencyKey) {
        this.lastIdempotencyKey = lastIdempotencyKey;
    }

    @Override
    public String toString() {
        return "OncadeAccountLink{" +
                "url='" + url + '\'' +
                ", userRef='" + userRef + '\'' +
                ", sessionKey='" + sessionKey + '\'' +
                ", namazuUserId='" + namazuUserId + '\'' +
                ", lastIdempotencyKey='" + lastIdempotencyKey + '\'' +
                '}';
    }

}
