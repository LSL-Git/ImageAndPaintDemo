package lsl.com.imageandpaintdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;
    private WindowManager windowManager;
    private Bitmap blankbitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);

        // 触摸监听 知道用户移动轨迹
        iv.setOnTouchListener(new View.OnTouchListener() {
            // 定义手指开始位置的坐标
            int startX;
            int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // 手指第一次触摸屏幕
                        startX = (int) event.getX();  // 获取手指位置
                        startY = (int) event.getY();

                        break;
                    case MotionEvent.ACTION_MOVE: // 手指移动
                        int newX = (int) event.getX();
                        int newY = (int) event.getY();
                        // 在画布上画直线
                        canvas.drawLine(startX, startY, newX, newY, paint);
                        // 重新更新画笔的位置
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        // 将画出来的图片显示出来
                        iv.setImageBitmap(baseBitmap);
                        break;
                    case MotionEvent.ACTION_UP: // 手指离开屏幕

                        break;
                }
                // 保持触摸持续被响应 return true
                return true;
            }
        });
    }
    // 初始化
    private void initCanvesAndPaint() {
        // 创建一个可以被修改的bitmap
        baseBitmap = Bitmap.createBitmap(710, 1070, Bitmap.Config.ARGB_8888);
        // 创建画布 以baseBitmap为基准
        canvas = new Canvas(baseBitmap);
        canvas.drawColor(Color.WHITE); // 定义画布背景

        // 初始化画笔
        paint = new Paint();
        paint.setStrokeWidth(5); // 画笔宽度为5个相素
        paint.setColor(Color.GREEN); // 画笔颜色
    }

    public void selectImage(View v) {
        // 激活系统图库  选择一张图片
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*"); // 获取数据类型
        startActivityForResult(intent, 0);
    }

    public void createCanvas(View v) {
        initCanvesAndPaint();
    }

    public void save(View v) {
        try {
            // 文件保存路径 保存在sd卡（需要权限）  命名
            File file = new File(Environment.getExternalStorageDirectory() + "//img", System.currentTimeMillis() + ".jpg");
            // 创建文件输出流
            FileOutputStream outputStream = new FileOutputStream(file);
            // 图片保存形式 JPG 质量 100
            blankbitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//            baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            Toast.makeText(this, "图片保存成功",Toast.LENGTH_SHORT).show();

            // 发送广播 模拟SD卡挂载 是系统重新便利SD卡的内容
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MEDIA_MOUNTED); // 定义action
            intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory())); // 指定挂载媒体
            sendBroadcast(intent);
        } catch (Exception e) {
            Toast.makeText(this, "图片保存失败",Toast.LENGTH_SHORT).show();
            Log.e("M",e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData(); // 获取图片uri路径
            windowManager = getWindowManager();  // 获取windowManager对象
            blankbitmap = new BitmapUtil().getBlankBitmap(uri,windowManager,this);
            iv.setImageBitmap(blankbitmap);
//            iv.setImageURI(uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
