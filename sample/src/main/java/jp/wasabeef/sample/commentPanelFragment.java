package jp.wasabeef.sample;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;


public class commentPanelFragment extends Fragment {
    private String TAG="commentPanelFragment";
    private RadioGroup cm_moderatio;
    private static commentPanelFragment fragment;
    public static commentPanelFragment newInstance(String param1, String param2) {
        if(fragment==null) {
            fragment = new commentPanelFragment();
        }
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

        final TextModeFragment tmf=new TextModeFragment();
        final WordUploadFragment wuf=new WordUploadFragment();
        cm_moderatio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, ""+checkedId);
                if(checkedId== R.id.cm_txtratio){
                    Log.d(TAG, "cmtxt set");
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager
                            .beginTransaction();
                    fragmentTransaction.replace(R.id.cm_contentlayout,tmf);
                    fragmentTransaction.commit();
                }else{
                    Log.d(TAG, "cmword set");
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager
                            .beginTransaction();
                    fragmentTransaction.replace(R.id.cm_contentlayout,wuf);
                    fragmentTransaction.commit();
                }
            }
        });
        cm_moderatio.check(R.id.cm_txtratio);
        return view;
    }

}
