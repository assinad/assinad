package a.sign;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import a.sign.asynctask.SignerAsyncTask;
import a.sign.baseadapter.DigSignBaseAdapter;
import a.sign.model.DigCert;
import a.sign.service.StaticVars;
import a.sign.service.UtilsService;

public class DigSigActivity extends AppCompatActivity {

    private Activity activity = this;
    private ActionBar actionBar;
    private TextView mPwdView;
    private ListView itemsListView;
    private Uri uri;

    private DigCert digCert;
    private String cpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dig_sig);

        // ListView for BaseAdapter
//        itemsListView  = (ListView)findViewById(R.id.list);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_actionbar)));

        String digCertJson = null;

        Intent it = getIntent();
        if (it != null) {
            Bundle params = it.getExtras();
            if (params != null) {
                digCertJson = params.getString("digCertJson");
            }
        }

        digCert = new Gson().fromJson(digCertJson, DigCert.class);
        actionBar.setTitle(digCert.getName());
//        actionBar.setTitle("Seu Nome");
        cpf = digCert.getCpf();

        TextView msg = (TextView) findViewById(R.id.msg_textView);
        TextView msg_2 = (TextView) findViewById(R.id.msg_textView_2);
        msg.setText(activity.getResources().getText(R.string.tv_document_sign));
        String fileDirectorySignedPath = Environment.getExternalStorageDirectory() +
                File.separator + activity.getString(R.string.app_name) + File.separator + cpf;
//        String fileDirectorySignedPath = Environment.getExternalStorageDirectory() +
//                File.separator + activity.getString(R.string.app_name) + File.separator + "87643231322";
        msg_2.setText(activity.getResources().getText(R.string.tv_document_saved) + " " + fileDirectorySignedPath);

//        loadBaseAdapter();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dig_cert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_sign) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, StaticVars.OPEN_DOCUMENT_SIGNATURE_ACTIVITY_RESULT);
            return true;
        }
        else if (id == R.id.action_settings) {
            List<DigCert> digCertList = UtilsService.getDigCertList(activity);
            Iterator iterator = digCertList.iterator();
            int index = 0;
            DigCert digCertAux = null;
            while (iterator.hasNext()){
                digCertAux = (DigCert) iterator.next();
                if (digCertAux.getFileName().equals(digCert.getFileName())){
                    digCertList.remove(index);
                    break;
                }
                index ++;
            }

            UtilsService.saveObjectInternalStorage(activity, digCertList, StaticVars.DIGCERTLIST);

            Intent returnIntent = new Intent();
            activity.setResult(activity.RESULT_OK, returnIntent);
            activity.finish();
            finish();

            Toast toast = Toast.makeText(activity, activity.getString(R.string.toast_certificate_remove_success), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 300);
            toast.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public void loadBaseAdapter(){
//        DigSignBaseAdapter digSignBaseAdapter = new DigSignBaseAdapter(activity, cpf);
//
//        //set custom adapter as adapter to our list view
//        itemsListView.setAdapter(digSignBaseAdapter);
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == StaticVars.OPEN_DOCUMENT_SIGNATURE_ACTIVITY_RESULT) {
            if (data != null && data.getData() != null) {
                uri = data.getData();

                // add a view to AlertDialog
                FrameLayout frameView = new FrameLayout(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.layout_password,
                        frameView);

                mPwdView = (TextView) dialoglayout
                        .findViewById(R.id.password);

                new AlertDialog.Builder(activity)
                        .setTitle(
                                R.string.alertdialog_doc_sign_title)
                        .setMessage(
                                R.string.alertdialog_password_digital_certificate)
                        .setView(frameView)
                        .setPositiveButton(getText(R.string.button_title_ok).toString(),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        String pwd = mPwdView.getText().toString();
                                        SignerAsyncTask signerAsyncTask = new SignerAsyncTask(null, null, activity, uri, pwd, digCert.getCpf());
                                        signerAsyncTask.execute();
                                        uri = null;
                                    }
                                })
                        .setNegativeButton(getText(R.string.action_cancel).toString(),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dialog.dismiss();
                                    }
                                }).show();

            }
        }
    }


}
