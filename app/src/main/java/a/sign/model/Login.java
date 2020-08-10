package a.sign.model;

import java.io.Serializable;
import java.util.Date;

public class Login implements Serializable {

    private Long id;
    private String username;
    private String docNum;
    private String docUsername;
    private String email;
    private String mobileNumState;
    private String mobileNum;
    private String passWord;
    private String passauxword;
    private String gcmRegId;
    private String deviceId;
    private String simSerialNumber;
    private String simOperator;
    private Date dateSignup;
    private byte[] rsaPubkey;
    private String currencyCode;
    private String lang;
//    private List<PymntMthd> pymntMthdList;
//    private List<Wallet> walletList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getDocUsername() {
        return docUsername;
    }

    public void setDocUsername(String docUsername) {
        this.docUsername = docUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumState() {
        return mobileNumState;
    }

    public void setMobileNumState(String mobileNumState) {
        this.mobileNumState = mobileNumState;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPassauxword() {
        return passauxword;
    }

    public void setPassauxword(String passauxword) {
        this.passauxword = passauxword;
    }

    public String getGcmRegId() {
        return gcmRegId;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSimSerialNumber() {
        return simSerialNumber;
    }

    public void setSimSerialNumber(String simSerialNumber) {
        this.simSerialNumber = simSerialNumber;
    }

    public String getSimOperator() {
        return simOperator;
    }

    public void setSimOperator(String simOperator) {
        this.simOperator = simOperator;
    }

    public Date getDateSignup() {
        return dateSignup;
    }

    public void setDateSignup(Date dateSignup) {
        this.dateSignup = dateSignup;
    }

    public byte[] getRsaPubkey() {
        return rsaPubkey;
    }

    public void setRsaPubkey(byte[] rsaPubkey) {
        this.rsaPubkey = rsaPubkey;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

//    public List<PymntMthd> getPymntMthdList() {
//        return pymntMthdList;
//    }
//
//    public void setPymntMthdList(List<PymntMthd> pymntMthdList) {
//        this.pymntMthdList = pymntMthdList;
//    }
//
//    public List<Wallet> getWalletList() {
//        return walletList;
//    }
//
//    public void setWalletList(List<Wallet> walletList) {
//        this.walletList = walletList;
//    }

}
