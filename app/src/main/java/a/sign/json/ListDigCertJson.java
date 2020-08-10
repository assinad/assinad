package a.sign.json;

import java.io.Serializable;
import java.util.List;

import a.sign.model.DigCert;

/**
 * Created by elton on 5/21/15.
 */
public class ListDigCertJson implements Serializable {

    private List<DigCert> digCertList;

    public List<DigCert> getDigCertList() {
        return digCertList;
    }

    public void setDigCertList(List<DigCert> digCertList) {
        this.digCertList = digCertList;
    }
}