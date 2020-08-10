package a.sign.baseadapter;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import a.sign.R;

public class DigSignBaseAdapter extends BaseAdapter {
    private View view;
    private List<String> signedDocNameList = new ArrayList<String>();
    private Activity activity;

    public DigSignBaseAdapter(Activity activity, String cpf) {
        this.activity = activity;

        // create directory before attempting to a save file in a new directory
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + activity.getString(R.string.app_name) + File.separator + cpf);
        File[] files = directory.listFiles();
        for (File file : files){
            signedDocNameList.add(file.getName());
        }

        // sort string list alphabetically
        Collections.sort(signedDocNameList, Collator.getInstance());
    }

    @Override
    public int getCount() {
        return signedDocNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return signedDocNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //set white background color
        parent.setBackgroundColor(activity.getResources().getColor(R.color.white));

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_dig_sig, null);
        } else {
            view = convertView;
        }
        setTextViews(position);
        return view;
    }

    public void setTextViews(int position) {
        String fileName = signedDocNameList.get(position);


        TextView tvFileName = (TextView) view.findViewById(R.id.tv_file_name);
        tvFileName.setText(fileName);

    }
}
