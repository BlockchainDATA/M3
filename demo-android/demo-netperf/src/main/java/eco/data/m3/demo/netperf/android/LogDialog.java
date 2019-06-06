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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_log, container, false);
        contentTv = view.findViewById(R.id.content_tv);
        contentTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        contentTv.setText(content);
        return view;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
