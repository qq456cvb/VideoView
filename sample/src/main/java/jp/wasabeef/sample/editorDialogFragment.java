package jp.wasabeef.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


public class editorDialogFragment extends DialogFragment {
    private String TAG="editorDialogFragment";
    TextEditorFragment tef;
    private static View view=null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        FragmentManager fm=getActivity().getFragmentManager();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if(view!=null){
            //!fix the bug: reload a dialog will cause a crash
            ViewGroup parent=(ViewGroup)view.getParent();
                if(parent!=null){
                    parent.removeView(view);
                }
        }
        try{
            view=inflater.inflate(R.layout.fragment_editordialog,null);
        }catch(InflateException e){
            Log.d(TAG,"in fact exception is here, but don't worry, just ignore it");
        }

        tef=(TextEditorFragment)fm.findFragmentById(R.id.frag_edit);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("保存",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //TODO:save and change data
                            Log.d(TAG, "title:" + tef.getTitle() + "content:" + tef.getContent());
                            CommentPanelRetSwitcher sprs=(CommentPanelRetSwitcher)getActivity();
                            sprs.uploadTxtComment(tef.getTitle(), tef.getContent());
                            tef.clear();
                        }
                    })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tef.clear();
                    }
                });
        Dialog dialog=builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
