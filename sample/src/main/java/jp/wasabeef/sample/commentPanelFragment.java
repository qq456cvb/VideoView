package jp.wasabeef.sample;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;


public class commentPanelFragment extends Fragment {
    private RadioGroup cm_moderatio;
    public static commentPanelFragment newInstance(String param1, String param2) {
        commentPanelFragment fragment = new commentPanelFragment();
        return fragment;
    }

    public commentPanelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.texteditor_container, container, false);
        cm_moderatio=(RadioGroup)view.findViewById(R.id.cm_mode);
        FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        final TextEditorFragment tef=new TextEditorFragment();
        final WordUploadFragment wuf=new WordUploadFragment();

        cm_moderatio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.cm_txtratio){
                    fragmentTransaction.replace(R.id.cm_contentlayout,tef);
                    fragmentTransaction.commit();
                }else{
                    fragmentTransaction.replace(R.id.cm_contentlayout,wuf);
                    fragmentTransaction.commit();
                }
            }
        });
        return view;
    }

}
