package a.sign.signer;

import android.app.Activity;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.util.encoders.Hex;
import org.demoiselle.signer.cryptography.DigestAlgorithmEnum;
import org.demoiselle.signer.policy.engine.factory.PolicyFactory;
import org.demoiselle.signer.policy.impl.cades.SignatureInformations;
import org.demoiselle.signer.policy.impl.cades.SignerAlgorithmEnum;
import org.demoiselle.signer.policy.impl.cades.factory.PKCS1Factory;
import org.demoiselle.signer.policy.impl.cades.pkcs1.PKCS1Signer;
import org.demoiselle.signer.policy.impl.cades.factory.PKCS7Factory;
import org.demoiselle.signer.policy.impl.cades.pkcs7.PKCS7Signer;
import org.demoiselle.signer.policy.impl.cades.pkcs7.impl.CAdESChecker;

import a.sign.model.DigCert;
import slib.StringUtils;

public class KeyStorePKCS12 {
    public static void main(String args[]) {
        byte[] result = loadFile(StringUtils.signedFilesPath + StringUtils.fileToSign);

//        KeyStore keyStore = loadKeystore(StringUtils.libPath + StringUtils.certP12Name);
//
//        PrivateKey chavePrivada = loadPrivKey(keyStore);
//
//        digSignPKCS7CAdESDettached(keyStore, chavePrivada, result);
//        boolean t = true;

    }

    //6.9. Leitura do conteúdo anexado (Attached) a uma assinatura PKCS7
    //https://www.frameworkdemoiselle.gov.br/v3/signer/docs/policy-impl-cades-funcionalidades.html#policy-impl-cades-funcionalidades-validar
//    public static void readSignPKCS7CAdESAttached(byte[] content){
//
//        try {
//            byte[] signed = loadFile(StringUtils.signedFilesPath + "signedAttached_" + StringUtils.fileToSign + ".p7s"); /* implementar metodo de leitura de arquivo */
//            PKCS7Signer signer = PKCS7Factory.getInstance().factoryDefault();
//
//            /* Para extrair o conteudo original validando a assinatura */
//            byte[] contentRead = signer.getAttached(signed, true);
//
//            /* Para extrair o conteudo original sem validar a assinatura */
//            byte[] content2 = signer.getAttached(signed, false);
//
//            FileOutputStream out = new FileOutputStream(new File(StringUtils.signedFilesPath + "readSignedAttached_" + StringUtils.fileToSign));
//            out.write(content2);
//            out.close();
//
//
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }

    //6.7. Validação de assinatura PKCS7 com o conteúdo anexado (attached)
    //https://www.frameworkdemoiselle.gov.br/v3/signer/docs/policy-impl-cades-funcionalidades.html#policy-impl-cades-funcionalidades-validar
    public static void valSignPKCS7CAdESAttached(){

        byte[] signature = loadFile(StringUtils.signedFilesPath + "signedAttached_" + StringUtils.fileToSign + ".p7s"); /* implementar metodo de leitura de arquivo */

        CAdESChecker checker = new CAdESChecker();

        List<SignatureInformations> siagnaturesInfo = checker.checkAttachedSignature(signature);

    }

    //6.6. Validação de assinatura PKCS7 sem o conteúdo anexado (dettached)
    //https://www.frameworkdemoiselle.gov.br/v3/signer/docs/policy-impl-cades-funcionalidades.html#policy-impl-cades-funcionalidades-validar
    public static void valSignPKCS7CAdESDettached(byte[] content){

        byte[] signature = loadFile(StringUtils.signedFilesPath + "_signedDettached_" + StringUtils.fileToSign + ".p7s"); /* implementar metodo de leitura de arquivo */

        CAdESChecker checker = new CAdESChecker();

        List<SignatureInformations> signaturesInfo = checker.checkDetachedSignature(content, signature);
        boolean t = true;

    }

    //6.8. Validação de assinatura PKCS7 enviando apenas o resumo (Hash) do conteúdo
    //https://www.frameworkdemoiselle.gov.br/v3/signer/docs/policy-impl-cades-funcionalidades.html#policy-impl-cades-funcionalidades-validar
    public static void valSignHash(byte[] content){
        try {

            byte[] signature = loadFile(StringUtils.signedFilesPath + "signedHashed_" + StringUtils.fileToSign + ".p7s"); /* implementar metodo de leitura de arquivo */

            CAdESChecker checker = new CAdESChecker();

            // gera o hash do arquivo que foi assinado
            /* Gerando o HASH */
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance(DigestAlgorithmEnum.SHA_256.getAlgorithm());

            byte[] hash = md.digest(content);

            List<SignatureInformations> signaturesInfo = checker.checkSignatureByHash(SignerAlgorithmEnum.SHA256withRSA.getOIDAlgorithmHash(), hash, signature);
            boolean t = true;

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //6.4. Criação de Assinatura Digital enviando apenas o resumo (hash) do conteúdo
    //https://www.frameworkdemoiselle.gov.br/v3/signer/docs/policy-impl-cades-funcionalidades.html#policy-impl-cades-funcionalidades-validar
    public static void digSignHash(KeyStore keyStore, PrivateKey privKey, byte[] content){
        try {

            /* Gerando o HASH */
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance(DigestAlgorithmEnum.SHA_256.getAlgorithm());

            byte[] hash = md.digest(content);

            String dataHexHashString = Hex.toHexString(hash);

            PKCS7Signer signer = signer(keyStore, privKey);

            signer.setSignaturePolicy(PolicyFactory.Policies.AD_RB_CADES_2_2);

            byte[] signature = signer.doHashSign(hash);

            FileOutputStream out = new FileOutputStream(new File(StringUtils.signedFilesPath + "signedHashed_" + StringUtils.fileToSign + ".p7s"));

            out.write(signature);

            out.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //6.3. Assinatura Digital no Formato PKCS#7/CAdES com conteúdo anexado (attached)
    //https://www.frameworkdemoiselle.gov.br/v3/signer/docs/policy-impl-cades-funcionalidades.html#policy-impl-cades-funcionalidades-validar
    public static void digSignPKCS7CAdESAttached(KeyStore keyStore, PrivateKey privKey, byte[] content){
        try {

            PKCS7Signer signer = signer(keyStore, privKey);

            byte[] signature = signer.doAttachedSign(content);

            FileOutputStream out = new FileOutputStream(new File(StringUtils.signedFilesPath + "signedAttached_" + StringUtils.fileToSign + ".p7s"));

            out.write(signature);

            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //6.2. Assinatura Digital no Formato PKCS#7/CAdES sem o conteúdo anexado (detached)
    //https://www.frameworkdemoiselle.gov.br/v3/signer/docs/policy-impl-cades-funcionalidades.html#policy-impl-cades-funcionalidades-validar
    public static void digSignPKCS7CAdESDettached(KeyStore keyStore, PrivateKey privKey, byte[] content){
        try {

            PKCS7Signer signer = signer(keyStore, privKey);

            byte[] signature = signer.doDetachedSign(content);

            FileOutputStream out = new FileOutputStream(new File(StringUtils.signedFilesPath + "signedDettached_" + StringUtils.fileToSign + ".p7s"));

            out.write(signature);

            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //6.2. Assinatura Digital no Formato PKCS#7/CAdES sem o conteúdo anexado (detached)
    //https://www.frameworkdemoiselle.gov.br/v3/signer/docs/policy-impl-cades-funcionalidades.html#policy-impl-cades-funcionalidades-validar
    public static byte[] digSignPKCS7CAdESDettachedReturnBytes(KeyStore keyStore, PrivateKey privKey, byte[] content){

        PKCS7Signer signer = signer(keyStore, privKey);

        byte[] signature = signer.doDetachedSign(content);
        return signature;
    }

    //6.1. Assinatura Digital no Formato PKCS1
    //https://www.frameworkdemoiselle.gov.br/v3/signer/docs/policy-impl-cades-funcionalidades.html#policy-impl-cades-funcionalidades-assinar-p%E1%B8%B1cs1
    public static void digSignPKCS1 (PrivateKey privKey, byte[] content){


        FileOutputStream out = null;
        try {
            /* construindo um objeto PKCS1Signer atraves da fabrica */
            PKCS1Signer signer = PKCS1Factory.getInstance().factory();
            /* Configurando o algoritmo */
            signer.setAlgorithm(SignerAlgorithmEnum.SHA1withRSA);
            /* Configurando a chave privada */
            signer.setPrivateKey(privKey);
            /* Assinando um conjunto de bytes */
            byte[] signature = signer.doAttachedSign(content);
            out = new FileOutputStream(new File(StringUtils.signedFilesPath + "signedPKCS1format_" + StringUtils.fileToSign + ".p7s"));
            out.write(signature);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static PKCS7Signer signer(KeyStore keyStore, PrivateKey privKey){

        PKCS7Signer signer = PKCS7Factory.getInstance().factoryDefault();
        Certificate[] certificates = certicateChain(keyStore);
        signer.setCertificates(certificates);
        signer.setPrivateKey(privKey);

        return signer;
    }

    public static Certificate[] certicateChain(KeyStore keyStore){

        Certificate[] certificates = null;
        try {
            String certificateAlias = keyStore.aliases().nextElement();

            certificates = keyStore.getCertificateChain(certificateAlias);

        } catch (KeyStoreException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        }

        return certificates;
    }

    public static DigCert certInfo(Certificate[] certificates){
        Certificate certificate = certificates[0];
        DigCert digCert = null;
        if (certificate instanceof X509Certificate) {
            X509Certificate x509cert = (X509Certificate) certificate;

            Principal principal = x509cert.getSubjectDN();
            String subjectDn = principal.getName();
            int dnIndex = subjectDn.indexOf("CN=");
            subjectDn = subjectDn.substring(dnIndex + 3);
            List<String> certInfoList = new ArrayList<>(Arrays.asList(subjectDn.split(":", 2)));
            Date certExpiryDate = ((X509Certificate) certificate).getNotAfter();
            digCert = new DigCert(certInfoList.get(0), certInfoList.get(1), certExpiryDate);
        }
        return digCert;
    }

    //8.2. Carregamento de KeyStore PKCS#12
    //http://demoiselle.sourceforge.net/docs/components/certificate/reference/1.0.9/html/funcionalidades-keystore.html
    public static KeyStore loadKeystore(Activity activity, InputStream ispfx, String pwd) throws Exception, IOException{

        KeyStore keyStore = null;
        try {

            keyStore = KeyStore.getInstance("PKCS12");

            keyStore.load(ispfx, pwd.toCharArray());
//            ispfx.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return keyStore;
    }
    //8.2. Carregamento de KeyStore PKCS#12
    //http://demoiselle.sourceforge.net/docs/components/certificate/reference/1.0.9/html/funcionalidades-keystore.html
    public static KeyStore loadKeystoreFromUri (Activity activity, File certFile, String pwd){
        Uri uriCert = Uri.fromFile(certFile);
        InputStream ispfx = null;
        KeyStore keyStore = null;
        try {
            ispfx = activity.getContentResolver().openInputStream(uriCert);

            keyStore = KeyStore.getInstance("PKCS12");

            keyStore.load(ispfx, pwd.toCharArray());
            ispfx.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyStore;
    }

    //7.4. Carregar uma chave privada de um token
    //https://www.frameworkdemoiselle.gov.br/v3/signer/docs/policy-impl-cades-exemplos.html#d0e1395
    public static PrivateKey loadPrivKey(KeyStore keyStore){
        PrivateKey key = null;
        try {
            String certificateAlias = keyStore.aliases().nextElement();

            //https://stackoverflow.com/questions/19937890/how-to-retrieve-my-public-and-private-key-from-the-keystore-we-created
            key = (PrivateKey)keyStore.getKey(certificateAlias, "17hinP".toCharArray());


        } catch (KeyStoreException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        }
        return key;
    }

    //7.1. Carregar um array de bytes de um arquivo
    //https://www.frameworkdemoiselle.gov.br/v3/signer/docs/policy-impl-cades-exemplos.html#d0e1390
    public static byte[] loadFile(String fileLocation){
        FileInputStream is = null;
        byte[] result = null;
        try {

            File file = new File(fileLocation);
            is = new FileInputStream(file);
            result = new byte[(int) file.length()];
            is.read(result);
            is.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KeyStorePKCS12.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
