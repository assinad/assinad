package a.sign.model;

import java.util.Date;

public class AppVersion {
    private Long id;
    private String appVersion;
    private Date dateVersion;
    private boolean verifyVersion;
    private String appName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Date getDateVersion() {
        return dateVersion;
    }

    public void setDateVersion(Date dateVersion) {
        this.dateVersion = dateVersion;
    }

    public boolean isVerifyVersion() {
        return verifyVersion;
    }

    public void setVerifyVersion(boolean verifyVersion) {
        this.verifyVersion = verifyVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
