package jp.wasabeef.sample;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;


public class TextModeFragment extends Fragment {
    private LinearLayout texteditorcontainer;
    private Button btn_txtsave;
    private Button btn_txtcancel;
    private TextEditorFragment tef;

    public static TextModeFragment newInstance() {
        TextModeFragment fragment = new TextModeFragment();
        return fragment;
    }

    public TextModeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_txt_mode,container,false);
        texteditorcontainer=(LinearLayout) view.findViewById(R.id.txtmode_editorlayout);
        btn_txtsave=(Button)view.findViewById(R.id.btn_txtsave);
        btn_txtcancel=(Button)view.findViewById(R.id.btn_txtcancel);
        tef=new TextEditorFragment();
        FragmentManager fm=getFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.txtmode_editorlayout, tef);
        ft.commit();
        btn_txtsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String title = tef.getTitle();
                    if (title == null || title.trim().equals("")) {
                        Toast.makeText(getActivity().getApplicationContext(), "请输入标题", Toast.LENGTH_LONG);
                    } else {
                        //need to save to local?
                        FileUtils.saveToMobile(getActivity().getApplicationContext(), tef.getTitle(), tef.getContent());
                        //TODO: upload text file to server
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_txtcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tef.clear();
                //TODO: tell activity set right panal to be channel list
                CommentPanelRetSwitcher sprs=(CommentPanelRetSwitcher)getActivity();
                sprs.switchRightPanel();
            }
        });
        return view;
    }
}
