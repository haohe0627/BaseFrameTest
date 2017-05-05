package util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import base.BaseApplication;

/**
 * Created by haohe on 2017/5/5 0005.
 */

public class ToastUtil {

    private static Toast mToast;
    private static int yOffset;

    public static Toast init(Context context){

        if (context == null) {
            throw new IllegalArgumentException("Context should not be null!!!");
        }
        if(mToast == null){
            mToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
            yOffset = mToast.getYOffset();
        }

        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.BOTTOM,0,yOffset);
        mToast.setMargin(0, 0);

        return  mToast;
    }

    public static void show(int resourceId){
        show(BaseApplication.getContext().getResources().getString(resourceId));
    };

    public static void show(String content){
        show(content, Gravity.BOTTOM);
    };

    public static void show(String content, int gravity){
        show(content,Gravity.BOTTOM, Toast.LENGTH_SHORT);
    };

    public static void show(String content, int gravity, int duration){
        if (mToast == null) init(BaseApplication.getContext());
        mToast.setText(content);
        mToast.setDuration(duration);
        mToast.setGravity(gravity, 0, yOffset);
        mToast.show();
    };
}
