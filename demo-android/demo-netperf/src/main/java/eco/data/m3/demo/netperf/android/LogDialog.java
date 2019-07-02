package eco.data.m3.demo.netperf.android;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * author: dai
 * date:   $date$
 * des:
 */
public class LogDialog extends DialogFragment {
    TextView contentTv;
    private String content;

    public LogDialog() {
        setStyle(STYLE_NO_FRAME, R.style.common_dialog_style);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_log, container, false);
        contentTv = view.findViewById(R.id.content_tv);
        cancelTv = view.findViewById(R.id.cancel_tv);
        confirmTv = view.findViewById(R.id.confirm_tv);
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnConfirmListener) {
                    mOnConfirmListener.onConfirmClick();
                }
                dismiss();
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnCancelListener){
                    mOnCancelListener.onCancelClick();
                }
                dismiss();
            }
        });

        contentTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        contentTv.setText(content);
        return view;
    }


    public void setOnConfirmListener(OnConfirmListener confirmListener) {
        this.mOnConfirmListener = confirmListener;
    }

    public void setOnCancelListener(onCancelListener cancelListener) {
        this.mOnCancelListener = cancelListener;
    }

    public interface OnConfirmListener {
        void onConfirmClick();
    }

    public interface onCancelListener {
        void onCancelClick();
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private onCancelListener mOnCancelListener;
    private OnConfirmListener mOnConfirmListener;


    public void setContent(String content) {
        this.content = content;
    }
}
