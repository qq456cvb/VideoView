package jp.wasabeef.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;


public class UploadPicOrVideoDialogFragment extends DialogFragment {
    private String TAG="UploadPicOrVideoDialogFragment";
    private Button btn_selfile;
    private TextView tv_selfile;
    private RadioGroup rg_uploadtype;
    private int FILE_SELECT_CODE=1;
    private String MIMEType="*/*";

    private String selectFileName=null;
    private String fileUrl=null;
    private String selectType=null;
    private int queryid=-1;

    public void setQueryid(int qid){
        queryid=qid;
    }

    public static UploadPicOrVideoDialogFragment newInstance() {
        UploadPicOrVideoDialogFragment fragment = new UploadPicOrVideoDialogFragment();
        return fragment;
    }

    public UploadPicOrVideoDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view=getActivity().getLayoutInflater().inflate(R.layout.fragment_upload_pic_or_video_dialog, null);
        btn_selfile=(Button)view.findViewById(R.id.btn_uploadselfile);
        tv_selfile=(TextView)view.findViewById(R.id.tv_uploadselectfile);
        rg_uploadtype=(RadioGroup)view.findViewById(R.id.rg_uploadtype);
        rg_uploadtype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_uploadvideo) {
                    MIMEType = "video/*";
                } else {
                    MIMEType = "images/*";
                }
            }
        });
        rg_uploadtype.check(R.id.radio_uploadvideo);
        btn_selfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton("上传",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    //TODO: upload related data to server
                        CommentPanelRetSwitcher cprs=(CommentPanelRetSwitcher)getActivity();
                        if(MIMEType.equals("images/*")){
                            cprs.uploadPic(queryid, fileUrl, selectFileName);
                        }else{
                            cprs.uploadVideo(queryid, fileUrl, selectFileName);
                        }

                    }
                }).setNegativeButton("取消", null);
        Dialog dialog=builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void showFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType(MIMEType);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        try {
//            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
//                    FILE_SELECT_CODE);
//        } catch (android.content.ActivityNotFoundException ex) {
//            // Potentially direct the user to the Market with a Dialog
//            Toast.makeText(getActivity(), "请安装文件管理器", Toast.LENGTH_SHORT)
//                    .show();
//        }
        Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(MIMEType.equals("video/*")){
            picture.setType(MIMEType);
        }
        startActivityForResult(picture, FILE_SELECT_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            String url;
//            try {
            url = FileUtils.getPath(getActivity(), uri);
            fileUrl = url;
            Log.i("ht", "url" + url);
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            tv_selfile.setText(fileName);
            selectFileName=fileName;
            selectType=MIMEType;
            Log.d(TAG, "select file:" + fileName);

//                Intent intent = new Intent(getActivity(), UploadServices.class);
//                intent.putExtra("fileName", fileName);
//                intent.putExtra("url", url);
//                intent.putExtra("type ", "");
//                intent.putExtra("fuid", "");
//                intent.putExtra("type", "");
//
//                getActivity().startService(intent);

//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
