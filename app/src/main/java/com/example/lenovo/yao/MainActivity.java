package com.example.lenovo.yao;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView text;
    private ImageView img;

    private SensorManager sensorManager;//定义传感器
    private Vibrator vibrator;//反馈
    private static  String strs[] = {"st","jd","b"};
    private static int pics[] = {R.mipmap.st,R.mipmap.jd,R.mipmap.b};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.txt);
        img = findViewById(R.id.imageView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获得什么样的传感器
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);//震动（需要在AndroidMainifest.xml中华注册）
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager!=null){//注册监听器
            sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
            //第一个参数是Listener，第二个参数是所得传感器类型（重力加速度），第三个参数是获取传感器信息的频率
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (sensorManager!=null){//取消监听器(在APP切到后台时不再获取信息)
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    //重力感应监听（获取传感器后处理用监听）
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @SuppressLint("MissingPermission")
        @Override
        public void onSensorChanged(SensorEvent event) {
            //传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0];//x轴方向的重力加速度，向右为正
            float y = values[1];//y轴方向的重力加速度，向前为正
            float z = values[2];//z轴方向的重力加速度，向上为正
            Log.i(TAG, "x["+x+"] y["+y+"] z["+z+"]");
            //一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态
            int medumValue = 10;//不同手机厂商数值可能不同
            if (Math.abs(x)>medumValue||Math.abs(y)>medumValue||Math.abs(z)>medumValue){
                vibrator.vibrate(200);
                Message msg = new Message();
                msg.what = 10;
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //传感器精度改变时执行该方法
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 10:
                    Log.i(TAG, "检测到摇晃，执行！");
                    java.util.Random r = new java.util.Random();
                    int num = Math.abs(r.nextInt())%3;
                    text.setText(strs[num]);
                    img.setImageResource(pics[num]);
                    break;
            }
        }
    };
}
