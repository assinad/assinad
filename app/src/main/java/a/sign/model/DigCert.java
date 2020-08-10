package a.sign.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.Date;

public class DigCert implements Serializable {

    private String fileName;
    private String name;
    private String cpf;
    private Date expirationDate;
    private byte[] digCertBytes;
    private Uri uri;

    public DigCert(String name, String cpf, Date expirationDate) {
        this.name = name;
        this.cpf = cpf;
        this.expirationDate = expirationDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public byte[] getDigCertBytes() {
        return digCertBytes;
    }

    public void setDigCertBytes(byte[] digCertBytes) {
        this.digCertBytes = digCertBytes;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
