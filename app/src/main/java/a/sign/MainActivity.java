package a.sign;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import a.apkt.asynctask.SignerAsyncTask;
import a.sign.asynctask.SignerAsyncTask;
import a.sign.model.DigCert;
import a.sign.service.StaticVars;
import a.sign.service.UtilsService;
import a.sign.signer.KeyStorePKCS12;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        ItemFragment.OnItemFragmentInteractionListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private ActionBar actionBar;
    private MenuItem actionImportItem;

    private Activity activity;

    private TextView mPwdView;
    private String pwd = null;
    private Uri uri;

    private Fragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // ask permission to WRITE_EXTERNAL_STORAGE
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
       /*FragmentTransaction transaction = getFragmentManager().beginTransaction();
       FragmentManager fragmentManager = getSupportFragmentManager();
       fragmentManager.beginTransaction()
               .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
               .commit();*/

        if (
                position == StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATES - 1
                ) {
            initItemFragment(position);
        }
    }

    public void initItemFragment(int position) {
        // Create new fragment and transaction
        newFragment = new ItemFragment();

        initFragment(position, newFragment);
    }

    public void initFragment(int position, Fragment newFragment) {
        Intent itLogin = getIntent();

        Bundle args = new Bundle();
        args.putInt("section_number", position + 1);

        newFragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.container, newFragment);

        // Commit the transaction
        transaction.commit();
    }

    public void onSectionAttached(int number) {

        switch (number) {
            case StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATES:
                mTitle = getString(R.string.title_section_digital_certificates);
                break;
        }
    }

    public void restoreActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

        // code block to setup ic_drawer
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer_white);
        // end of code block to setup ic_drawer
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            actionImportItem = menu.findItem(R.id.action_import);
            restoreActionBar();
            setActionItemVisible();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void setActionItemVisible() {
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_actionbar)));
        if (
                mTitle.equals(getString(R.string.title_section_digital_certificates))
        ) {
            actionImportItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

    if (actionImportItem != null && id == actionImportItem.getItemId()) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            startActivityForResult(intent, StaticVars.OPEN_DOCUMENT_DIGITAL_CERTIFICATE_ACTIVITY_RESULT);
            return true;
        }
//    else if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // import digital certificate
        if (requestCode == StaticVars.OPEN_DOCUMENT_DIGITAL_CERTIFICATE_ACTIVITY_RESULT) {
            if (data != null && data.getData() != null) {
                // get digital certificate path
                uri = data.getData();

                // loads user interface
                FrameLayout frameView = new FrameLayout(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.layout_password,
                        frameView);
                mPwdView = (TextView) dialoglayout
                        .findViewById(R.id.password);

                // set flag to false for digital certificate format x-pkcs12
                boolean isPfx = false;

                // Retrieve a file's MIME type
                String mimeType = UtilsService.retrieveMimeType(activity, uri);
                // set flag to true if uri location points to a digital certificate format x-pkcs12
                if (mimeType.endsWith("x-pkcs12")){
                    isPfx = true;
                }

                // import digital certificate and save to internal storage
                if (isPfx) {

                    // show an alert dialog requiring the user to type a password in order to import the digital certificate
                    new AlertDialog.Builder(activity)
                            .setTitle(
                                    R.string.alertdialog_import_digital_certificate_title)
                            .setMessage(
                                    R.string.alertdialog_password_digital_certificate)
                            .setView(frameView)
                            .setPositiveButton(getText(R.string.button_title_ok).toString(),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {

                                            try {

                                                // get list of digital certificate objects stored in internal storage where imported
                                                // digital certificate may be stored
                                                File certFile = UtilsService.openFileInternalStorage(activity, StaticVars.DIGCERTLIST);
                                                // load a digital certificates objects that may be stored in internal storage to a
                                                // digital certificate object list
                                                List<DigCert> digCertList = (List<DigCert>) UtilsService.getObjectFromFile(certFile);

                                                // retrieve file name from uri path
                                                String certName = UtilsService.retrieveFileName(activity, uri);

                                                // load digital certificate from uri path into InputStream
                                                InputStream digCertLoadKeystoreStream = activity.getContentResolver().openInputStream(uri);

                                                // get password informed by the user
                                                pwd = mPwdView.getText().toString();

                                                // get digital certificate keystore if password is correct; otherwise, throw exception
                                                KeyStore keyStore = KeyStorePKCS12.loadKeystore(activity, digCertLoadKeystoreStream, pwd);
//
                                                pwd = null;

                                                // load digital certificate from uri path into InputStream
                                                InputStream digCertStream = activity.getContentResolver().openInputStream(uri);
                                                byte[] digCertBytes = UtilsService.convertInputStreamToBytes(digCertStream);

                                                // for verification of existing certificate in internal storage
                                                boolean existDigCert = false;

                                                // initialize digital certificate object list if empty
                                                if (digCertList == null) {
                                                    digCertList = new ArrayList<>();
                                                // if digital certificate object list is not empty, check digCertBytes sha256 hash matches
                                                    // an existing digCert
                                                } else {

                                                    // load digital certificate from uri path into InputStream
                                                    InputStream digCertStreamSha256 = activity.getContentResolver().openInputStream(uri);
                                                    // get SHA 256 hash from digital certificate inputStream that was converted into bytes
                                                    // in method UtilsService.sha256Doc(activity, digCertStreamSha256)
                                                    String digCertSha256 = UtilsService.sha256Doc(activity, digCertStreamSha256);

                                                    // iterator to obtain a SHA 256 hash from each digital certificate in the list to compare
                                                    // with a SHA 256 hash from the digital certificate loaded in uri path
                                                    Iterator iterator = digCertList.iterator();
                                                    int index = 0;
                                                    while (iterator.hasNext()) {
                                                        DigCert digCertIt = (DigCert) iterator.next();
                                                        InputStream digCertItStream = UtilsService.convertBytesToInputStream(digCertIt.getDigCertBytes());
                                                        String digCertSha256Iterator = UtilsService.sha256Doc(activity, digCertItStream);
                                                        if (digCertSha256.equals(digCertSha256Iterator)) {
                                                            existDigCert = true;
                                                            Toast toast = Toast.makeText(activity, activity.getString(R.string.toast_certificate_import_already), Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.BOTTOM, 0, 300);
                                                            toast.show();
                                                        }
                                                    }
                                                }
                                                // save digital cetificate from uri path in internal storage if verified that
                                                // was not yet stored in internal storage
                                                if (!existDigCert) {

                                                    DigCert digCert = KeyStorePKCS12.certInfo(KeyStorePKCS12.certicateChain(keyStore));
                                                    digCert.setFileName(certName);
                                                    digCert.setDigCertBytes(digCertBytes);
                                                    digCertList.add(digCert);
                                                    UtilsService.saveObjectInternalStorage(activity, digCertList, StaticVars.DIGCERTLIST);
                                                    Toast toast = Toast.makeText(activity, activity.getString(R.string.toast_certificate_import_success), Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.BOTTOM, 0, 300);
                                                    toast.show();
                                                }
                                                initItemFragment(StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATES - 1);
                                                uri = null;
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                                uri = null;
                                            } catch (IOException e){
                                                Toast toast = Toast.makeText(activity, activity.getString(R.string.toast_file_sign_error), Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.BOTTOM, 0, 300);
                                                toast.show();
                                                uri = null;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                uri = null;
                                            }
                                        }
                                    })
                            .setNegativeButton(getText(R.string.action_cancel).toString(),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).show();

                } else {
                    Toast toast = Toast.makeText(activity, activity.getString(R.string.toast_file_invalid), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM, 0, 300);
                    toast.show();
                }

            }
        } else if (requestCode == StaticVars.DIG_CERT_ACTIVITY_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                initItemFragment(StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATES- 1);
            }
        }
    }

}

