package com.ruanmeng.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maning.mndialoglibrary.MProgressDialog;
import com.ruanmeng.base.BaseDialog;
import com.ruanmeng.park_inspector.R;

public class DialogHelper {

    @SuppressLint("StaticFieldLeak")
    private static MProgressDialog mMProgressDialog;

    private DialogHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void showDialog(Context context) {
        dismissDialog();

        mMProgressDialog = new MProgressDialog.Builder(context)
                .setCancelable(true)
                .isCanceledOnTouchOutside(false)
                .setDimAmount(0.5f)
                .build();

        mMProgressDialog.show();
    }

    public static void dismissDialog() {
        if (mMProgressDialog != null && mMProgressDialog.isShowing())
            mMProgressDialog.dismiss();
    }

    public static void showHintDialog(final Context context) {
        BaseDialog dialog = new BaseDialog(context) {
            @Override
            public View onCreateView() {
                widthScale(0.6f);
                return View.inflate(context, R.layout.dialog_clock_add, null);
            }
        };

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public static void showDelDialog(
            final Context context,
            final ClickCallBack callBack) {
        BaseDialog dialog = new BaseDialog(context) {
            @Override
            public View onCreateView() {
                widthScale(0.85f);
                View view = View.inflate(context, R.layout.dialog_custom_del, null);

                TextView tvCancel = view.findViewById(R.id.dialog_cancel);
                TextView tvSure = view.findViewById(R.id.dialog_sure);

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                tvSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                        callBack.onClick("");
                    }
                });

                return view;
            }
        };

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public static void showAddDialog(
            final Context context,
            final String title,
            final String content,
            final String hint,
            final ClickCallBack callBack) {
        BaseDialog dialog = new BaseDialog(context, true) {
            @Override
            public View onCreateView() {
                widthScale(0.85f);
                View view = View.inflate(context, R.layout.dialog_custom_add, null);

                TextView tvTitle = view.findViewById(R.id.dialog_title);
                final EditText etName = view.findViewById(R.id.dialog_name);
                TextView tvCancel = view.findViewById(R.id.dialog_cancel);
                TextView tvSure = view.findViewById(R.id.dialog_sure);

                tvTitle.setText(title);
                etName.setText(content);
                etName.setHint(hint);
                etName.setSelection(etName.getText().length());

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                tvSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etName.getText().toString().trim().isEmpty()) {
                            Toast.makeText(context, hint, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        dismiss();

                        callBack.onClick(etName.getText().toString().trim());
                    }
                });

                return view;
            }
        };

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public interface ClickCallBack {
        void onClick(String hint);
    }

}
