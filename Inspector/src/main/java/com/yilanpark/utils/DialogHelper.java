package com.yilanpark.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.maning.mndialoglibrary.MProgressDialog;
import com.yilanpark.base.BaseDialog;
import com.yilanpark.R;
import com.weigan.loopview.LoopView;

import java.util.List;

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

    public static void showItemDialog(
            final Context context,
            final String title,
            final List<String> items,
            final ItemCallBack callBack) {
        showItemDialog(context, title, 0, items, callBack);
    }

    public static void showItemDialog(
            final Context context,
            final String title,
            final int position,
            final List<String> items,
            final ItemCallBack callBack) {

        BottomBaseDialog dialog = new BottomBaseDialog(context) {

            private LoopView loopView;

            @Override
            public View onCreateView() {
                View view = View.inflate(context, R.layout.dialog_select_one, null);

                TextView tv_title = view.findViewById(R.id.tv_dialog_select_title);
                TextView tv_cancel = view.findViewById(R.id.tv_dialog_select_cancle);
                TextView tv_ok = view.findViewById(R.id.tv_dialog_select_ok);
                loopView = view.findViewById(R.id.lv_dialog_select_loop);

                tv_title.setText(title);
                loopView.setTextSize(15f);
                loopView.setDividerColor(context.getResources().getColor(R.color.divider));
                loopView.setNotLoop();

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        callBack.doWork(loopView.getSelectedItem(), items.get(loopView.getSelectedItem()));
                    }
                });

                return view;
            }

            @Override
            public void setUiBeforShow() {
                loopView.setItems(items);
                loopView.setInitPosition(position);
            }

        };

        dialog.show();
    }

    public interface ClickCallBack {
        void onClick(String hint);
    }

    public interface ItemCallBack {
        void doWork(int position, String name);
    }
}
