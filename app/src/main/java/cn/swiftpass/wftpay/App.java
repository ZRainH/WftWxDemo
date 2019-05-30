package cn.swiftpass.wftpay;

import android.app.Application;

import com.soonchina.pay.utils.PayCashier;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
          PayCashier.builder()
                  .initPay(this);
    }
}
