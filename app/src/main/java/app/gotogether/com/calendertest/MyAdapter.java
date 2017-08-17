package app.gotogether.com.calendertest;


import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by 동덕 on 2017-05-11.
 */

public class MyAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<MyData> myDataList;
    private LayoutInflater layoutInflater;
    Dialog dig;
    AlertDialog.Builder builder;
    public MyAdapter(Context context, int layout, ArrayList<MyData> myDataList) {
        this.context = context;
        this.layout = layout;
        //원본 데이터를 가지고 있다(MyDataList)
        this.myDataList = myDataList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        //원본 데이터의 개수를 반환
        return myDataList.size();
    }

    @Override
    public Object getItem(int position) {
        //어떠한 위치에 있는 원본 데이터의 항목 반환(어떠한 타입도 반환 가능-object이기 때문)
        return myDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        //어떠한 위치에 있는 원본 데이터의 항복의 식별자를 반환
        return myDataList.get(position).get_id();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final int pos = position;

        if (view == null) {
            view = layoutInflater.inflate(layout, viewGroup, false);
        }
        //view안에서 찾기 때문에 view.findviewbyid를 해주어야 한다.
        TextView textNo = (TextView) view.findViewById(R.id.textViewNo);
        TextView textName = (TextView) view.findViewById(R.id.textViewName);
        Button btnCheck = (Button) view.findViewById(R.id.buttonCheck);
        Button btnRefuse = (Button)view.findViewById(R.id.Refuse);
        //숫자는 아이디를 찾기 때문에 문자열로 변환해주어야 한다.
        textNo.setText(Integer.valueOf(myDataList.get(position).get_id()).toString());
        textName.setText(myDataList.get(position).getName());

        btnCheck.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(context, myDataList.get(pos).getName() + "선택", Toast.LENGTH_SHORT).show();

            }
        });
        btnRefuse.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(context, myDataList.get(pos).getName() + "선택", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}