package jp.ac.it_college.std.nakasone.opencampas;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import jp.ac.it_college.std.nakasone.opencampas.view.SensorView;

public class SensorTestActivity extends Activity implements SensorEventListener {
    private SensorView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new SensorView(this);
        setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            view.setX(-sensorEvent.values[0]);
            view.setY(sensorEvent.values[1]);
            view.invalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
