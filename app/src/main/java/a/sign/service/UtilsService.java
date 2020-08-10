package a.sign.service;


import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Patterns;

import org.apache.commons.codec.binary.Base64;
import org.demoiselle.signer.policy.impl.cades.SignatureInformations;
import org.demoiselle.signer.policy.impl.cades.pkcs7.impl.CAdESChecker;
import org.spongycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import a.sign.R;
import a.sign.model.DigCert;
import a.sign.signer.KeyStorePKCS12;

public class UtilsService {

    public static String sha256Doc(Activity activity, InputStream is) {

        // read file
        byte[] bytes;
        String dataHexHashString = null;
        try {

            int fileSize = is.available();

            Runtime info = Runtime.getRuntime();
            long usedMemory = info.totalMemory() - info.freeMemory();
            // convert usedMemory value to bytes value
            long usedMemoryBytes = usedMemory * 10;
            // convert usedMemoryBytes to megabytes value in order to show this value to user
            long usedMemoryMegaBytes = (usedMemoryBytes / 1024) / 1024;
            // convert usedMemoryMegaBytes value to its bytes value
            long usedMemoryMegaBytesInBytes = usedMemoryMegaBytes * 1000000;

            if (fileSize <= usedMemoryMegaBytesInBytes) {
                bytes = new byte[fileSize];
                is.read(bytes, 0, bytes.length);
                // build digest
                try {
                    MessageDigest digest;
                    digest = MessageDigest.getInstance("SHA-256");
                    digest.update(bytes);
                    dataHexHashString = Hex.toHexString(digest.digest());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getResources().getText(R.string.alertdialog_doc_size_exceeded_title));
                builder.setMessage(
                        activity.getResources().getText(R.string.alertdialog_doc_size_exceeded) + " " + usedMemoryMegaBytes + "MB."
                );

                builder.setPositiveButton(R.string.button_title_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            is.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataHexHashString;
    }

    public static String sha256String(byte[] bytes){
        String dataHexHashString = null;
        try {
            MessageDigest digest;
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(bytes);
            dataHexHashString = Hex.toHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return dataHexHashString;
    }

    public static String sha512String(byte[] bytes){
        String dataHexHashString = null;
        try {
            MessageDigest digest;
            digest = MessageDigest.getInstance("SHA-512");
            digest.update(bytes);
            dataHexHashString = Hex.toHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return dataHexHashString;
    }

    public static String signfile(Activity activity, Uri uri) {

        try {

            InputStream is = activity.getContentResolver().openInputStream(uri);
            byte[] result= UtilsService.convertInputStreamToBytes(is);
            // get filename from uri
            String fileName = uri.getLastPathSegment().substring(uri.getLastPathSegment().lastIndexOf("/")+1);


            String fileNamePath = Environment.getExternalStorageDirectory() +
                    File.separator + activity.getString(R.string.app_name) + File.separator + "signedDettached_" + fileName + ".p7s";

            List<DigCert> digCertList = UtilsService.getDigCertList(activity);
            DigCert digCert = digCertList.get(0);

            InputStream inputStream = convertBytesToInputStream(digCert.getDigCertBytes());
            KeyStore keyStore = KeyStorePKCS12.loadKeystore(activity, inputStream, "17hinP");
            PrivateKey chavePrivada = KeyStorePKCS12.loadPrivKey(keyStore);
            KeyStorePKCS12.digSignPKCS7CAdESAttached(keyStore, chavePrivada, result);

            // copy file to folder
//            OutputStream out = new FileOutputStream(fileNamePath);
//
//            //https://stackoverflow.com/questions/4178168/how-to-programmatically-move-copy-and-delete-files-and-directories-on-sd
//            int len;
//            while ((len = is.read(bytes)) > 0) {
//                out.write(bytes, 0, len);
//            }
//
//            is.close();
//            out.close();


        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String copyfile(Activity activity, Uri uri) {

        try {

            InputStream is = activity.getContentResolver().openInputStream(uri);
            int fileSize = is.available();

            // get filename from uri
            String certName = uri.getLastPathSegment().substring(uri.getLastPathSegment().lastIndexOf("/")+1);
            // add ".p12" suffix to work with demoseille api
//            filename = filename.replace(" ", "_").replace(":", "_").concat(".p12");

            String certNamePath = Environment.getExternalStorageDirectory() +
                    File.separator + activity.getString(R.string.app_name) + File.separator + certName;

            byte[] result = new byte[fileSize];
            // copy file to folder
            OutputStream out = new FileOutputStream(certNamePath);

            //https://stackoverflow.com/questions/4178168/how-to-programmatically-move-copy-and-delete-files-and-directories-on-sd
            int len;
            while ((len = is.read(result)) > 0) {
                out.write(result, 0, len);
            }

            is.close();
            out.close();


        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    //    Internal storage:
    //
    //    It's always available.
    //
    //    Files saved here are accessible by only your app.
    //    When the user uninstalls your app, the system removes all your app's files from internal storage.
    //
    //    Internal storage is best when you want to be sure that neither the user nor other apps can access your files.
    // source: https://developer.android.com/training/data-storage/files.html#java
    public static void saveFileInternalStorage(Activity activity, Uri uri, String name) {

        try {

            InputStream is = activity.getContentResolver().openInputStream(uri);
            int fileSize = is.available();
            byte[] certBytes = new byte[fileSize];

            FileOutputStream outputStream;

            outputStream = activity.openFileOutput(name, Context.MODE_PRIVATE);
            //https://stackoverflow.com/questions/4178168/how-to-programmatically-move-copy-and-delete-files-and-directories-on-sd
            int len;
            while ((len = is.read(certBytes)) > 0) {
                outputStream.write(certBytes, 0, len);
            }

            is.close();
            outputStream.close();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void saveObjectInternalStorage(Activity activity, Object object, String name) {

        try {

            byte[] objBytes = convertObjectToBytes(object);

            FileOutputStream outputStream;

            outputStream = activity.openFileOutput(name, Context.MODE_PRIVATE);

            outputStream.write(objBytes);

            outputStream.close();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static byte[] convertObjectToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    public static File openFileInternalStorage(Activity activity, String certName) {
        File directory = activity.getFilesDir();
        File file = new File(directory, certName);
        return file;
    }

    public static Object getObjectFromFile(File file) {

        try {

            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();

            System.out.println("The Object has been read from the file");
            objectIn.close();
            return obj;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static InputStream convertBytesToInputStream(byte[] bytes){
        InputStream is = new ByteArrayInputStream(bytes);
        return is;
    }

    public static byte[] convertInputStreamToBytes (InputStream inputStream){

        byte[] bytes = null;
        try {

            int fileSize = inputStream.available();
            bytes = new byte[fileSize];
//            inputStream.read(bytes, 0, bytes.length);
            inputStream.read(bytes);
//            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static List getDigCertList(Activity activity){
        File file = UtilsService.openFileInternalStorage(activity, StaticVars.DIGCERTLIST);
        List<DigCert> digCertList = (List<DigCert>) UtilsService.getObjectFromFile(file);
        return digCertList;
    }

    public static OutputStream savefileExternalStorage(byte[] fileBytes, String filePath) {

        try {

            OutputStream outFileSign = new FileOutputStream(filePath);

            outFileSign.write(fileBytes);
            outFileSign.close();


        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    public static String getFilePath(Activity activity, Uri uri){
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = activity.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public static String getFilePath_(Activity activity, Uri uri){
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String documentId = cursor.getString(0);

        if (!isExternalStoragePublicDirectory(documentId)){

            cursor.close();

            cursor = activity.getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{documentId}, null);
            cursor.moveToFirst();

            int cursorCount = cursor.getCount();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
            return path;
        }

        return documentId;
    }

    private static boolean isExternalStoragePublicDirectory(String documentId){

        Pattern pat = Pattern.compile("[/\\\\]");
        Matcher m = pat.matcher(documentId);

        while (m.find()){
            return true;
        }
        return false;
    }

    public static byte[] filePathToBytes(Activity activity, String filePath){

        File fileSign = new File(filePath);
        Uri uriFileSign = Uri.fromFile(fileSign);
        InputStream fileSignStream = null;
        try {
            fileSignStream = activity.getContentResolver().openInputStream(uriFileSign);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] fileBytes = UtilsService.convertInputStreamToBytes(fileSignStream);

        return fileBytes;
    }

    /**
     * This method uses java.io.FileInputStream to read
     * file content into a byte array
     * @param file
     * @return
     */
    public static byte[] readFileToByteArray(File file){
        FileInputStream fis = null;
        // Creating a byte array using the length of the file
        // file.length returns long which is cast to int
        byte[] bArray = new byte[(int) file.length()];
        try{
            fis = new FileInputStream(file);
            fis.read(bArray);
            fis.close();

        }catch(IOException ioExp){
            ioExp.printStackTrace();
        }
        return bArray;
    }

    //6.6. Validação de assinatura PKCS7 sem o conteúdo anexado (dettached)
    //https://www.frameworkdemoiselle.gov.br/v3/signer/docs/policy-impl-cades-funcionalidades.html#policy-impl-cades-funcionalidades-validar
    public static void valSignPKCS7CAdESDettached(byte[] signature, byte[] content){

        CAdESChecker checker = new CAdESChecker();

        List<SignatureInformations> signaturesInfo = checker.checkDetachedSignature(content, signature);
        boolean t = true;

    }

    public static String generate() {
        final int PASSWORD_LENGTH = 4;
        StringBuffer sb = new StringBuffer();
        for (int x = 0; x < PASSWORD_LENGTH; x++) {
            sb.append((char) ((int) (Math.random() * 26) + 97));
        }
        return sb.toString();
    }

    public static String retrieveMimeType(Activity activity, Uri uri){
        // Retrieve a file's MIME type
        // https://developer.android.com/training/secure-file-sharing/retrieve-info#RetrieveMimeType
        String mimeType = activity.getContentResolver().getType(uri);

        return mimeType;
    }

    public static String retrieveFileName (Activity activity, Uri uri){
        // Retrieve a file's name and size
        // https://developer.android.com/training/secure-file-sharing/retrieve-info#RetrieveFileInfoj
        /*
         * Get the file's content URI from the incoming Intent,
         * then query the server app to get the file's display name
         * and size.
         */
        Cursor returnCursor =
                activity.getContentResolver().query(uri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String fileName = returnCursor.getString(nameIndex);
        return fileName;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}