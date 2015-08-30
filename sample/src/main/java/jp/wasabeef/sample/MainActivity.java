package jp.wasabeef.sample;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private LinearLayout loadll=null;
    private Button btn_testdialog;
//    editorDialogFragment dialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        setContentView(R.layout.dynamic_main);
//        loadll=(LinearLayout) findViewById(R.id.load);
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager
//                .beginTransaction();
//        commentPanelFragment cpf=new commentPanelFragment();
//        fragmentTransaction.replace(R.id.load,cpf);
//        fragmentTransaction.commit();
        btn_testdialog=(Button)findViewById(R.id.btn_testdialog);
//        dialogFragment=new editorDialogFragment();
        btn_testdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UploadPicOrVideoDialogFragment dialogFragment = UploadPicOrVideoDialogFragment.newInstance();
//                dialogFragment.show(getFragmentManager(), "uploaddialog");
                DialogFragment dialogFragment=new editorDialogFragment();
                dialogFragment.show(getFragmentManager(), "editdialog");
            }
        });


//        TextEditorFragment tef=new TextEditorFragment();
//        fragmentTransaction.replace(R.id.load,tef);
//        fragmentTransaction.commit();
    }
}
