package jp.wasabeef.sample;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.net.URISyntaxException;


public class WordUploadFragment extends Fragment {
    private String TAG="WordUploadFragment";
    private static final int FILE_SELECT_CODE = 1;
    private Button btn_fileselect;
    private Button btn_wordsave;
    private Button btn_wordcancel;

    public static WordUploadFragment newInstance(String param1, String param2) {
        WordUploadFragment fragment = new WordUploadFragment();
        return fragment;
    }

    public WordUploadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.commentwordmode, container, false);
        btn_fileselect=(Button)view.findViewById(R.id.btn_fileexplo);
        btn_wordsave=(Button)view.findViewById(R.id.btn_wordsave);
        btn_wordcancel=(Button)view.findViewById(R.id.btn_wordcancel);
        btn_fileselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getActivity(), "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            String url;
//            try {
                url = FileUtils.getPath(getActivity(), uri);
                Log.i("ht", "url" + url);
                String fileName = url.substring(url.lastIndexOf("/") + 1);
//                Intent intent = new Intent(getActivity(), UploadServices.class);
//                intent.putExtra("fileName", fileName);
//                intent.putExtra("url", url);
//                intent.putExtra("type ", "");
//                intent.putExtra("fuid", "");
//                intent.putExtra("type", "");
//
//                getActivity().startService(intent);
                Log.d(TAG,"select file:"+fileName);
//            } catch (URISyntaxException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
