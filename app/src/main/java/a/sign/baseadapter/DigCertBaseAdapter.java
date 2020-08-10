package a.sign.baseadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import a.sign.R;
import a.sign.model.DigCert;

public class DigCertBaseAdapter extends BaseAdapter {
    private View view;
    private List<DigCert> digCertList;
    private Activity activity;

    public DigCertBaseAdapter(Activity activity, List<DigCert> digCertList) {
        this.activity = activity;
        this.digCertList = digCertList;
    }

    @Override
    public int getCount() {
        return digCertList.size();
    }

    @Override
    public Object getItem(int position) {
        return digCertList.get(position);
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
            view = inflater.inflate(R.layout.list_dig_cert, null);
        } else {
            view = convertView;
        }
        setTextViews(position);
        return view;
    }

    public void setTextViews(int position) {
        DigCert digCert = digCertList.get(position);


        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvCpf = (TextView) view.findViewById(R.id.tv_cpf);
        TextView tvExpirationDate = (TextView) view.findViewById(R.id.tv_expiration_date);

        tvName.setText(digCert.getName());
//        tvName.setText("Seu Nome");
        tvCpf.setText(digCert.getCpf());
//        tvCpf.setText("87643231322");
        tvExpirationDate.setText(digCert.getExpirationDate().toLocaleString());
    }
}
