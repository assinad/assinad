package a.sign.asynctask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import org.demoiselle.signer.policy.impl.cades.pkcs7.PKCS7Signer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.List;

import a.sign.DigSigActivity;
import a.sign.MainActivity;
import a.sign.R;
import a.sign.model.DigCert;
import a.sign.service.StaticVars;
import a.sign.service.UserMsgService;
import a.sign.service.UtilsService;
import a.sign.signer.KeyStorePKCS12;

public class SignerAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private boolean glassfishDown = false;
    private boolean signError = false;
    private Activity activity;
    private Uri uri;
    private String pwd;
    private String cpf;
    private View mProgressView;
    private ScrollView scrollView;
    private String fileDirectorySignedPath;

    public SignerAsyncTask (
            View mProgressView,
            ScrollView scrollView,
            Activity activity,
            Uri uri,
            String pwd,
            String cpf){
        this.activity = activity;
        this.uri = uri;
        this.pwd = pwd;
        this.cpf = cpf;
    }

    protected void onPreExecute() {
        mProgressView = activity.findViewById(R.id.activity_progress);
//        scrollView = (ScrollView) activity.findViewById(R.id.scroll_view);
        mProgressView.setVisibility(View.VISIBLE);

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            // get list of digital certificates from internal storage
            List<DigCert> digCertList = UtilsService.getDigCertList(activity);

            // get digital certificate in first position in the list, because as of now
            // the app is coded to deal with only one digital certificate when signing documents
            DigCert digCert = digCertList.get(0);

            // get bytes format of digital certificate
            byte[] digCertBytes = digCert.getDigCertBytes();

            // convert bytes format of digital certificate into inputStream
            InputStream digCertInputStream = UtilsService.convertBytesToInputStream(digCertBytes);

            // get digital certificate keystore if password is correct; otherwise, throw exception
            KeyStore keyStore = KeyStorePKCS12.loadKeystore(activity, digCertInputStream, pwd);

            pwd = null;

            // load private key from digital certificate
            PrivateKey privateKey = KeyStorePKCS12.loadPrivKey(keyStore);

            // get PKCS7 format for implementation of digital signatures
            PKCS7Signer signer = KeyStorePKCS12.signer(keyStore, privateKey);

            // load document from uri path into InputStream
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);

            // convert InputStream of document into bytes
            byte[] fileBytes = UtilsService.convertInputStreamToBytes(inputStream);

            // sign document and get bytes format of signed document
//            byte[] fileSignedBytes = signer.doDetachedSign(fileBytes);
            byte[] fileSignedBytes = signer.doAttachedSign(fileBytes);

            // get name of signed document
            String fileName = UtilsService.retrieveFileName(activity, uri);

            // create directory before attempting to a save file in a new directory
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + activity.getString(R.string.app_name) + File.separator + cpf);
//            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + activity.getString(R.string.app_name) + File.separator + "87643231322");
            directory.mkdirs();

            // save signed file to directory
            fileDirectorySignedPath = Environment.getExternalStorageDirectory() +
                    File.separator + activity.getString(R.string.app_name) + File.separator + cpf + File.separator + "sign" + fileName;
//            fileDirectorySignedPath = Environment.getExternalStorageDirectory() +
//                    File.separator + activity.getString(R.string.app_name) + File.separator + "87643231322" + File.separator + "assinado" + fileName;
            UtilsService.savefileExternalStorage(fileSignedBytes, fileDirectorySignedPath);

            File[] files = directory.listFiles();

        } catch (ConnectException exception) {

            glassfishDown = true;
            return false;

        }catch (CursorIndexOutOfBoundsException e) {
            e.getMessage();
        } catch (IOException e){
            signError = true;
            return false;
        } catch (Exception e) {
            e.getMessage();
            glassfishDown = true;
            return false;
        }

//        List<String> certInfoList = KeyStorePKCS12.certInfo(KeyStorePKCS12.certicateChain(keyStore));
//
//        PrivateKey chavePrivada = KeyStorePKCS12.loadPrivKey(keyStore);
//
//        PKCS7Signer signer = KeyStorePKCS12.signer(keyStore, chavePrivada);
//
//        // code block for signing docs
//        //*************************************************************
//        try {
//
//            File fileSign = new File(Environment.getExternalStorageDirectory() +
//                    File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + "encryption.jpg");
//            Uri uriFileSign = Uri.fromFile(fileSign);
//            InputStream is = activity.getContentResolver().openInputStream(uriFileSign);
//            int fileSize = is.available();
//            byte[] resultFileSign = new byte[fileSize];
//            is.read(resultFileSign);
//            is.close();
//
//            // line of code needed in asynctask
//            byte[] signature = signer.doDetachedSign(resultFileSign);
//
//            OutputStream outFileSign = new FileOutputStream(Environment.getExternalStorageDirectory() +
//                    File.separator + activity.getString(R.string.app_name) + File.separator + "DIRECTORY_DOWNLOADS.p7s");
//
//            outFileSign.write(signature);
//            outFileSign.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //****************************************************************************

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (success) {

            try{

//                if (activity.getClass().getSimpleName().equals("DigSigActivity")){
//                    DigSigActivity digSigActivity = (DigSigActivity) activity;
//                    digSigActivity.loadBaseAdapter();
//                }

                Toast toast = Toast.makeText(activity, activity.getString(R.string.toast_file_sign_success), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 300);
                toast.show();
                // prevents exception "file:///storage/emulated/0/... exposed beyond app through ClipData.Item.getUri()"
                // allows opening file from external storage directory
                // source: https://stackoverflow.com/questions/42251634/android-os-fileuriexposedexception-file-jpg-exposed-beyond-app-through-clipdata/45569709
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                File file = new File(fileDirectorySignedPath);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                shareIntent.setType("image/jpeg");
                activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getText(R.string.action_send_file)));
            } catch (Exception e){
                e.getMessage();
            }

        }
        else if (!success) {
            if (glassfishDown) {
//                UserMsgService.showDialogPositButtonMail(activity,
//                        activity, R.string.alertdialog_internal_problem_title,
//                        R.string.alertdialog_internal_problem_msg);
                UserMsgService.showDialog(activity,
                        R.string.alertdialog_no_connectivity_title,
                        R.string.alertdialog_no_connectivity);
            } else if (signError){
                Toast toast = Toast.makeText(activity, activity.getString(R.string.toast_file_sign_error), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM, 0, 300);
                toast.show();
            }

        }

        mProgressView.setVisibility(View.GONE);
    }

}
