package lsl.com.imageandpaintdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;

import java.io.FileInputStream;

/** 图片复制
 * Created by M1308_000 on 2017/1/1.
 */

public class BitmapUtil {

    private Bitmap blankbitmap = null;

    public Bitmap getBlankBitmap(Uri uri, WindowManager windowManager, Context context) {
        try {
            FileInputStream inputStream = (FileInputStream) context.getContentResolver().openInputStream(uri);

            // 计算出屏幕的高度
            int windowWidth = windowManager.getDefaultDisplay().getWidth();
            int windowHeight = windowManager.getDefaultDisplay().getHeight();
            // 计算出图片的高度 和 宽度
            Log.e("M","屏幕的宽高：\nwidth:" + windowWidth + "\nheight：" + windowHeight);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 设置不去真正解析位图  不把他加载到内存 只是获取图片的宽高信息
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream);
            int bitmapWidth = opts.outWidth;
            int bitmapHeight = opts.outHeight;
//                Log.e("M","图片的宽高：\nwidth:" + bitmapWidth + "\nheight：" + bitmapHeight);
            if (bitmapHeight > windowHeight || bitmapWidth > windowWidth) {
                int scaleX = bitmapWidth/windowWidth;
                int scaleY = bitmapHeight/windowHeight;
                if (scaleX > scaleY) {
                    // 按照水平方向比例缩放
                    opts.inSampleSize = scaleX;
                } else {
                    // 按照竖直比例缩放
                    opts.inSampleSize = scaleY;
                }
            } else {
                // 如果图片比屏幕小就不去缩放了
                opts.inSampleSize = 1;
            }
            // 让位图工厂真正的去解析图片
            opts.inJustDecodeBounds = false;
            inputStream = (FileInputStream) context.getContentResolver().openInputStream(uri);
            // 原图
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, opts);
            // 创建一个图片拷贝
//                Bitmap bitmap = data.getParcelableExtra("data");// 得到一个缩略图
            // 创建一个新的空白图片,图片大小跟原来图片大小一样的
            blankbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),bitmap.getConfig());
            // 得到一个画布
            Canvas canvas2 = new Canvas(blankbitmap);
            // 在画布上画画
            // 第一个参数就是要画的参考图片
            canvas2.drawBitmap(bitmap, new Matrix(), new Paint());
            Paint paint = new Paint();
            paint.setTextSize(30);
            paint.setColor(Color.RED);
            canvas2.drawText("我是拷贝的图片", 10, 50, paint);

        } catch (Exception e) {
            Log.e("M","windowManager:" + windowManager.toString());
            e.printStackTrace();
        }
        return blankbitmap;
    }
}
