package cn.swiftpass.wftpay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class FailureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failure);
    }
}
