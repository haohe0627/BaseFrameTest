package base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.Unbinder;

/**
 * Created by haohe on 2017/5/5 0005.
 */

public abstract class BaseActivity extends AppCompatActivity{

    protected Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected abstract int setLayoutId();

    protected abstract void init();
}
