package com.cyf.cyfimageselector.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.widget.ImageView;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.cyf.cyfimageselector.GlideApp;
import com.cyf.cyfimageselector.R;

import java.io.IOException;

/**
 * 从SDCard异步加载图片
 *
 * @author hanj 2013-8-22 19:25:46
 */
public class SDCardImageLoader {

    /**
     *  获取手机厂商 
     *      
     *  @return  手机厂商     
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        if (path.startsWith("http")) {
            return degree;
        }
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap toturn(Bitmap img, int rot) {
        Matrix matrix = new Matrix();
        matrix.postRotate(+rot); /*翻转90度*/
        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }

    public static void setImg(Context context, String path, ImageView piv) {
        if (path == null || "".equals(path.trim())) {
            piv.setImageResource(R.mipmap.ic_default_img);
            return;
        }
//        if (SDCardImageLoader.readPictureDegree(path) == 0) {
//            GlideApp.with(context).load(path).
//                    apply(new RequestOptions().error(R.mipmap.ic_default_img)).into(piv);
//        } else {
//            GlideApp.with(context).load(path).
//                    apply(new RequestOptions().error(R.mipmap.ic_default_img).fitCenter()).
//                    into(piv);
//        }
        GlideApp.with(context).load(path).disallowHardwareConfig().
                error(new ColorDrawable(Color.GRAY)).placeholder(null).fallback(null).
                fitCenter().encodeQuality(100).thumbnail(0.1f).format(DecodeFormat.PREFER_ARGB_8888).
                transition(new DrawableTransitionOptions().crossFade(200)).into(piv);
    }

    public static void setImgThumbnail(Context context, String path, ImageView piv) {
        if (path == null || "".equals(path.trim())) {
            piv.setImageResource(R.mipmap.ic_default_img);
            return;
        }
//        if (SDCardImageLoader.readPictureDegree(path) == 0) {
//            GlideApp.with(context).load(path).
//                    apply(new RequestOptions().error(R.mipmap.ic_default_img)).
//                    thumbnail(0.1f).into(piv);
//        } else {
//            GlideApp.with(context).load(path).
//                    apply(new RequestOptions().error(R.mipmap.ic_default_img).fitCenter()).
//                    thumbnail(0.1f).into(piv);
//        }
        GlideApp.with(context).load(path).disallowHardwareConfig().
                error(new ColorDrawable(Color.GRAY)).placeholder(R.mipmap.ic_default_img).fallback(new ColorDrawable(Color.GRAY)).
                encodeQuality(60).thumbnail(0.1f).format(DecodeFormat.PREFER_ARGB_8888).
                transition(new DrawableTransitionOptions().crossFade(200)).into(piv);
    }

}
