package com.alex.com.alex.livertmppushsdk.demo.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.alex.com.alex.livertmppushsdk.demo.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 图片处理工具类
 *
 * @author LHD
 */
@SuppressLint("SimpleDateFormat")
@SuppressWarnings("deprecation")
public class BitmapUtil {

    /**
     * 从给定的路径获取原始图片
     *
     * @return 图片对象
     */
    public static Bitmap getBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }

    /**
     * 从图片的字节数组中 加载位图对象 并对加载图片的宽高进行限制
     *
     * @param data  图片资源数组
     * @param width 指定收缩的宽度
     * @return 按比例收缩后的缩略图
     */

    public static Bitmap getThumbnail(byte[] data, int width, int height) {
        Bitmap bm = null;
        // 创建加载选项对象
        Options op = new Options();
        // 设置仅加载边界信息
        op.inJustDecodeBounds = true;
        // 加载为位图尺寸信息
        BitmapFactory.decodeByteArray(data, 0, data.length, op);
        // 计算并设置缩放比例
        int x = op.outWidth / width;
        int y = op.outHeight / height;
        op.inSampleSize = x > y ? x : y;
        // 取消仅加载边界信息的设置
        op.inJustDecodeBounds = false;
        // 加载位图
        bm = BitmapFactory.decodeByteArray(data, 0, data.length, op);
        return bm;
    }

    /**
     * 从图片的字节数组中 加载位图对象 并对加载图片的宽高进行限制
     *
     * @return 按比例收缩后的缩略图
     */

    public static Bitmap getThumbnail(String path, int width, int height) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int x = options.outWidth / width;
        int y = options.outHeight / height;
        options.inSampleSize = x > y ? x : y;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * 以最省内存的方式读取图片
     */
    public static Bitmap readBitmap(String path, int size) {
        try {
            FileInputStream stream = new FileInputStream(new File(path));
            Options opts = new Options();
            opts.inSampleSize = size;
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            Bitmap bitmap = BitmapFactory.decodeStream(stream, null, opts);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将位图对象保存到指定文件目录
     *
     * @param bm   位图对象
     * @param file 文件对象
     * @throws IOException
     */
    public static boolean save(Bitmap bm, File file) throws IOException {
        // 判断位图对象和文件对象是否有效
        // 如果父目录不存在 ，创建目录
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        // 如果文件不存在则创建文件
        if (!file.exists()) {
            file.createNewFile();
        }
        // 保存
        return bm.compress(CompressFormat.JPEG, 100, new FileOutputStream(file));
    }

    /**
     * 从手机内存中获取缩率图
     *
     * @param context
     * @param rowId
     * @return
     */
    public static Bitmap getThumbnail(Context context, int rowId) {
        return Thumbnails.getThumbnail(context.getContentResolver(), rowId, Thumbnails.MINI_KIND,
                null);
    }

    /**
     * 通过缩略图id取得原图路径
     *
     * @param context
     * @param id
     * @return
     */
    public static String getImagePath(Context context, String id) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, null, Media._ID + "=?",
                new String[]{id}, null);
        while (cursor.moveToNext()) {
            path = cursor.getString(cursor.getColumnIndex(Media.DATA));
        }
        cursor.close();
        return path;
    }

    /**
     * 根据URL获得图片的绝对路径
     *
     * @param uri
     * @return
     */
    public static String getAbsoluteImagePath(Context context, Uri uri) {
        // can post image
        String[] proj = {Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    /**
     * 将图片压缩转换为byte数组
     *
     * @param bitmap
     * @param quality 压缩的图片质量
     * @return
     */
    public static ByteArrayOutputStream getImageData(Bitmap bitmap, int quality) {
        // bitmap-->byteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 压缩到byteArrayOutputStream
        bitmap.compress(CompressFormat.JPEG, quality, outputStream);
        // 把流变成byte[]
        return outputStream;
    }

    /**
     * 将图片按比例压缩，转换为byte数组
     *
     * @param scale 压缩比例
     * @return
     */
    public static ByteArrayOutputStream getImageData(String path, int scale) {
        Bitmap bitmap = getBitmap(path);
        // bitmap-->byteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 压缩到byteArrayOutputStream
        bitmap.compress(getCompressType(path), 100 / scale, outputStream);
        // 把流变成byte[]
        bitmap.recycle();
        return outputStream;
    }

    /**
     * 从指定路径获取图片，并按指定范围加载图片，按指定压缩范围压缩图片返回字节数组输出流
     *
     * @param path   图片路径
     * @param width  加载宽度
     * @param height 加载高度
     * @return 图片字节输出流
     * @throws IOException
     */
    public static ByteArrayOutputStream getImageData(String path, int width, int height) throws IOException {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int x = options.outWidth / width;
        int y = options.outHeight / height;
        int scale = x > y ? x : y;
        if (scale <= 0) {
            scale = 1;
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Config.RGB_565;
        bitmap = BitmapFactory.decodeFile(path, options);
        // bitmap-->byteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 压缩到byteArrayOutputStream
        int count = 100 / scale <= 50 ? 50 : 100 / scale;
        bitmap.compress(getCompressType(path), count, outputStream);
        // 把流变成byte[]
        bitmap.recycle();
        outputStream.close();
        return outputStream;
    }

    /**
     * 从指定路径获取图片，并按指定范围加载图片，按指定压缩范围压缩图片返回字节数组输出流
     *
     * @param path   图片路径
     * @param width  加载宽度
     * @param height 加载高度
     * @return 图片字节输出流
     * @throws IOException
     */
    public static ByteArrayOutputStream getThumbData(String path, int width, int height) throws IOException {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        // options.inPreferredConfig = Config.RGB_565;
        bitmap = BitmapFactory.decodeFile(path, options);
        // bitmap-->byteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        /** BugFix-329-20160429-LHD-START */
        if (bitmap == null)
            throw new IOException("bitmap is null");
        /** BugFix-329-20160429-LHD-END */
        // 压缩到byteArrayOutputStream
        bitmap.compress(CompressFormat.JPEG, 90, outputStream);
        int opts = 90;
        while (outputStream.toByteArray().length / 1024 > 600) {// 大于600k
            outputStream.reset();
            opts -= 10;
            bitmap.compress(CompressFormat.JPEG, opts, outputStream);
            if (opts == 10) {
                break;
            }
        }
        // 把流变成byte[]
        bitmap.recycle();
        outputStream.close();
        return outputStream;
    }

    /**
     * 从指定路径获取图片，并按指定范围加载图片，按指定压缩范围压缩图片返回字节数组输出流
     *
     * @param path   图片路径
     * @param width  加载宽度
     * @param height 加载高度
     * @return 图片字节输出流
     * @throws IOException
     */
    public static ByteArrayOutputStream bycImgCompress(String path, int width, int height) throws IOException {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        // options.inPreferredConfig = Config.RGB_565;
        bitmap = BitmapFactory.decodeFile(path, options);
        // bitmap-->byteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        /** BugFix-329-20160429-LHD-START */
        if (bitmap == null)
            throw new IOException("bitmap is null");
        /** BugFix-329-20160429-LHD-END */
        // 压缩到byteArrayOutputStream
        bitmap.compress(CompressFormat.JPEG, 85, outputStream);
        int opts = 85;
        while (outputStream.toByteArray().length / 1024 > 2048) {// 不大于2M
            outputStream.reset();
            opts -= 10;
            bitmap.compress(CompressFormat.JPEG, opts, outputStream);
            if (opts == 10) {
                break;
            }
        }
        // 把流变成byte[]
        bitmap.recycle();
        outputStream.close();
        return outputStream;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 把图片压缩为指定宽高，并转换为byte缓存输出流
     *
     * @param path   图片路径
     * @param width  目标宽度
     * @param height 目标高度
     * @return 图片字节输出流
     */
    public static ByteArrayOutputStream getImageData(String path, double width, double height) {
        Bitmap bitmap = getBitmap(path);
        bitmap = zoomImage(bitmap, width, height);
        // bitmap-->byteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 压缩到byteArrayOutputStream
        bitmap.compress(getCompressType(path), 100, outputStream);
        // 把流变成byte[]
        bitmap.recycle();
        return outputStream;
    }

    /***
     * 图片的缩放方法
     *
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        if (bgimage == null) {
            return null;
        }
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 将bitmap转换为Base64字符串
     *
     * @return
     */
    public static String bmpConStr(String path, int width, int height) throws IOException {
        byte[] data = getImageData(path, width, height).toByteArray();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * base64字符串转bitmap
     *
     * @param base64
     * @return
     */
    public static Bitmap base64ToBmp(String base64, int width, int height) {
        if (TextUtils.isEmpty(base64)) {
            return null;
        }
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return getThumbnail(data, width, height);
    }

    /**
     * 获取压缩图片格式
     *
     * @param path 图片名称
     * @return
     */
    public static CompressFormat getCompressType(String path) {
        String type = getFileType(path);
        if (type.equals("png")) {
            return CompressFormat.PNG;
        }
        return CompressFormat.JPEG;
    }

    /**
     * 获取原图路径
     *
     * @return
     */
    public static String getRawPath(String path) {
        String rawPath = path.substring(0, path.indexOf("_tn"));
        return rawPath;
    }

    /**
     * 获取缩略图或者原图名称
     *
     * @param imageName 缩略图-原图名字符串
     * @param type      0取缩略图，1取原图名称
     * @return
     */
    public static String getImageName(String imageName, int type) {
        if (TextUtils.isEmpty(imageName)) {
            return null;
        }
        String[] str = imageName.split("-");
        if (str != null && str.length == 2) {
            switch (type) {
                case 0:// 缩略图名
                    return str[0];
                case 1:// 原图名
                    return str[1];
                default:
                    break;
            }
        } else if (str != null) {
            return str[0];
        }

        return null;
    }

    /**
     * 获取图片文件夹
     *
     * @return sd根路径
     */
    public static String getSDcard() {
        String sdStatus = Environment.getExternalStorageState(); // 获取sd状态
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 如果sd为安装
            return null;
        }
        // 创建文件对象
        // File fileDirectory = new File(
        // Environment.getExternalStorageDirectory(), "/qdong/image");
        File fileDirectory = new File(Constants.QD_IMAGE_DIR);
        // 判断文件夹是否存在
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        String path = fileDirectory.getAbsolutePath();
        return path;
    }

    /**
     * 获取yipai项目根目录
     * @return 路径
     */
    public static String getProject(){
        String sdStatus = Environment.getExternalStorageState(); // 获取sd状态
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 如果sd为安装
            return null;
        }
        // 创建文件对象
        // File fileDirectory = new File(
        // Environment.getExternalStorageDirectory(), "/qdong/image");
        File fileDirectory = new File(Constants.getQDongDirRoot());
        // 判断文件夹是否存在
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        String path = fileDirectory.getAbsolutePath();
        return path;
    }

    /**
     * 获取glide目录
     * @return 路径
     */
    public static String getGlidePath(){
        String sdStatus = Environment.getExternalStorageState(); // 获取sd状态
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 如果sd为安装
            return null;
        }
        // 创建文件对象
        // File fileDirectory = new File(
        // Environment.getExternalStorageDirectory(), "/qdong/image");
        File fileDirectory = new File(Constants.getGlideImageRootDir());
        // 判断文件夹是否存在
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        String path = fileDirectory.getAbsolutePath();
        return path;
    }

    /**
     * 获取log文件夹
     *
     * @return sd根路径
     */
    public static String getLogSDcard() {
        String sdStatus = Environment.getExternalStorageState(); // 获取sd状态
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 如果sd为安装
            return null;
        }
        // 创建文件对象
        // File fileDirectory = new File(
        // Environment.getExternalStorageDirectory(), "/qdong/image");
        File fileDirectory = new File(Constants.QD_LOG_DIR);
        // 判断文件夹是否存在
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        String path = fileDirectory.getAbsolutePath();
        return path;
    }

    /**
     * 返回用户个人目录
     *
     * @param nickname 用户昵称
     * @return 文件夹路径
     */
    public static String getUserDir(String nickname) {
        String path = getSDcard() + "/" + nickname;
        // 创建文件对象
        File fileDirectory = new File(path);
        // 判断文件夹是否存在
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        return path;
    }

    /**
     * 地图缓存目录
     *
     * @param
     * @return 文件夹路径
     */
    public static String getMapDir() {
        String sdStatus = Environment.getExternalStorageState(); // 获取sd状态
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 如果sd为安装
            return null;
        }
        // 创建文件对象
        File fileDirectory = new File(Constants.QD_MAP_DIR);
        // 判断文件夹是否存在
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        String path = fileDirectory.getAbsolutePath();
        return path;
    }

    /**
     * 获取图片名称
     *
     * @param path 图片路径
     * @return
     */
    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1, path.length());
    }

    /**
     * 获取图片后缀
     *
     * @param path 图片路径
     * @return
     */
    public static String getFileType(String path) {
        return path.substring(path.lastIndexOf(".") + 1, path.length());
    }

    /**
     * 获取图片大小
     *
     * @param path 图片路径
     * @return
     */
    public static long getFileSize(String path) {
        return new File(path).length();
    }

    /**
     * 获取裁剪后的圆形图片
     *
     * @param radius 半径
     * @param bmp    要被裁减的图片
     */
    public static Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        if (bmp == null) {
            return bmp;
        }
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);

        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2, scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);

        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    public static Bitmap getAmapLineBmp(int width, int fromcolor, int tocolor) {
        int w = width, h = width;
        Bitmap bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        // paint.setAntiAlias(true);
        // paint.setFilterBitmap(true);
        // paint.setDither(true);
        // canvas.drawARGB(0, 0, 0, 0);
        LinearGradient shader = new LinearGradient(0, 0, w, h, fromcolor, tocolor, Shader.TileMode.MIRROR);
        paint.setShader(shader);
        // canvas.drawBitmap(bmp, rect, rect, paint);
        canvas.drawRect(rect, paint);
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        canvas.concat(matrix);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bmp;
    }

    /**
     * 将背景图和显示的bitmap画在一起，返回新的bmp
     *
     * @param bitmap1 背景图片
     * @param bitmap2 显示图片
     * @return 合成的新图片
     */
    public static Bitmap drawBitmapBg(Bitmap bitmap1, Bitmap bitmap2) {
        Bitmap myBmp = bitmap1.copy(Config.ARGB_4444, true);
        Canvas canvas = new Canvas(myBmp);
        Paint paint = new Paint();
        int x = (myBmp.getWidth() - bitmap2.getWidth()) / 2;
        canvas.drawBitmap(bitmap2, x + 1, 7, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return myBmp;
    }


    /**
     * 获取图片资源
     *
     * @param R_ID
     * @return
     */
    public static Drawable getDrawable(Context context, int R_ID) {
        Drawable brawable = context.getResources().getDrawable(R_ID);
        brawable.setBounds(0, 0, brawable.getMinimumWidth(), brawable.getMinimumHeight());
        return brawable;
    }

    /**
     * 返回随机的图片路径
     *
     * @return
     */
    public static String getRandomImagePath() {
        return getSDcard() + "/" + System.currentTimeMillis() + ".jpg";
    }

    /**
     * 将文件转成字节数组后保存到设定sdcard中
     *
     * @param data
     * @return
     */
    public static File writeToSDCard(byte[] data) {
        String filePath = Constants.QD_PERSONAL_DIR;
        String fileName = System.currentTimeMillis() + "_qd";
        FileOutputStream fos = null;// 文件输出流
        // 判断是否有sdcard
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(filePath);
            // 判断文件是否存在
            if (!file.exists()) {
                file.mkdirs();
            }
            // 判断是否有同名文件
            File file2 = new File(filePath, fileName + ".jpg");
            if (file2.exists()) {
                file2 = new File(filePath, fileName + "_2" + ".jpg");
            }

            // 开始写入文件
            // 建立输出通道
            try {
                fos = new FileOutputStream(file2);
                fos.write(data);// 将数据写入输出流
                fos.flush();// 保证数据写出
                return file2;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 图片压缩（maxSize KB以内），用于上传到服务器前先对要上传的Bitmap压缩
     * 以减少网络流量
     *
     * @param bitmap  //原图
     * @param maxSize //压缩后能够达到的最大尺寸
     * @return
     */
    public static byte[] compressBmpToBytes(Bitmap bitmap, long maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length >= maxSize) {
            Log.e("compress", "当前length:" + baos.toByteArray().length);
            baos.reset();
            options -= 5;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }

        return baos.toByteArray();
    }

    /**
     * 获取指定Activity的截屏，
     *
     * @param activity
     * @return
     */
    public static Bitmap getScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        Log.i("TAG","Width:" + width);
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        Log.i("TAG","height:" + height);
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        b1.recycle();
        view.destroyDrawingCache();
        return b;
    }

    /**
     * 保存bmp到SD卡
     *
     * @param quality 质量
     * @param bitmap
     * @return
     */
    public static String saveBmpToSdcard(Bitmap bitmap, int quality) {
        boolean b = false;
        String filePath = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            filePath = getSDcard() + "/" + sdf.format(new Date()) + ".jpg";
            FileOutputStream fos = new FileOutputStream(filePath);
            b = bitmap.compress(CompressFormat.JPEG, quality, fos);
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (b) {
            return filePath;
        }
        return null;
    }

    /*	*//**
     * 加载头像方法
     *
     * @param context
     *            上下文对象
     * @param path
     *            图片地址
     * @param view
     *            ImageView控件
     *//*
	public static void loadHead(Context context, String path, GlideRoundTransform round, ImageView view) {
		try {
			if (context == null)
				return;
			Glide.with(context.getApplicationContext())
					.load(path)
					.transform(round)
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.dontAnimate()
					.placeholder(R.drawable.ic_default)
					.error(R.drawable.ic_head)
					.into(view);

		} catch (Exception e) {
			Log.e("loadHead","异常: "+e.toString());
		}
	}*/



    /** Bugfix-063-20160418-yyh-START */
    /**
     * 对指定的View进行截图并且保存在本地
     *
     * @param view 截图对象
     */
    public static void getViewShot(View view) {
        view.setDrawingCacheEnabled(true);
        try {
            String filePath = Constants.QD_PERSONAL_DIR;
            String fileName = System.currentTimeMillis() + "viewShot_qd";
            // 判断是否有sdcard
            String state = Environment.getExternalStorageState();
            File file = new File(filePath);
            // 判断文件是否存在
            if (!file.exists()) {
                file.mkdirs();
            }
            // 判断是否有同名文件
            File file2 = new File(filePath, fileName + ".jpg");
            if (file2.exists()) {
                file2 = new File(filePath, fileName + "_2" + ".jpg");
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file2));
            view.getDrawingCache().compress(CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
        }
        view.setDrawingCacheEnabled(false);
    }

    /**
     * 获取指定View的Bitmap对象
     *
     * @param view 对应的View
     * @return bitmap
     */
    public static Bitmap getViewBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * 将两个图片通过上下组合的方式合并在一起
     *
     * @param topView    上面的图片
     * @param bottomView 下面的图片
     * @return 合成图
     */
    public static String toMixBitmap(Activity activity, Bitmap topView, Bitmap bottomView, int marginLeft,
                                     int marginTop, int SrcHeight) {
        if (topView == null) {
            return null;
        }
        try {
            int bgWidth = topView.getWidth();
            int bgHeight = topView.getHeight();
            int fgWidth = bottomView.getWidth();
            int fgHeight = bottomView.getHeight();
            int Width = bgWidth > fgWidth ? bgWidth : fgWidth;
            // create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
            // Bitmap newbmp = Bitmap.createBitmap(Width, bgHeight + fgHeight +
            // marginTop + 355, Config.RGB_565);
            // Bitmap bagZoom = zoomImage(, newWidth, newHeight)
            /** Bugfix-302-20160426-yyh-START */
            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.abc_ab_bottom_solid_light_holo);
            Bitmap topZoom = zoomImage(topView, 500, 140);
            Bitmap botZoom = zoomImage(bottomView, 500, 140);
            int Width1 = topZoom.getWidth() > botZoom.getWidth() ? topZoom.getWidth() : botZoom.getWidth();
            Bitmap bgZoom = zoomImage(bitmap, Width1 + 50, 315);
            Bitmap newbmp = Bitmap.createBitmap(Width1 + 50, 315, Config.RGB_565);
            Canvas cv = new Canvas(newbmp);
            // draw bg into
            cv.drawBitmap(bgZoom, 0, 0, null);// 绘制背景
            /** Bugfix-302-20160426-yyh-END */
            /** Bugfix-063-20160420-yyh-START */
            cv.drawBitmap(topZoom, 25, 25, null);// 在0,0坐标开始画入bg
            // 在0,0坐标开始画入bg
            // draw fg into
            cv.drawBitmap(botZoom, 25, 158, null);// 上图之下进行画
            /** Bugfix-063-20160420-yyh-END */
            // 上图之下进行画
            // save all clip
            cv.save(Canvas.ALL_SAVE_FLAG);// 保存
            // store
            cv.restore();// 存储
            String filePath = Constants.QD_PERSONAL_DIR;
            String fileName = System.currentTimeMillis() + "Mix_qd";
            // 判断是否有sdcard
            String state = Environment.getExternalStorageState();
            File file = new File(filePath);
            // 判断文件是否存在
            if (!file.exists()) {
                file.mkdirs();
            }
            // 判断是否有同名文件
            File file2 = new File(filePath, fileName + ".jpg");
            if (file2.exists()) {
                file2 = new File(filePath, fileName + "_2" + ".jpg");
            }
            // 保存
            newbmp.compress(CompressFormat.JPEG, 80, new FileOutputStream(file2));
            return file2.getAbsolutePath().toString();
        } catch (FileNotFoundException e) {
            return null;
        }
    }
    /** Bugfix-063-20160418-yyh-END */


    /**
     * 将View中的图片保持到SD卡中,成功则返回绝对路径
     */
    public static String saveImageFromViewToSdcard(Context context, View view) {
        if (view != null) {
            view.setDrawingCacheEnabled(true);
            if (view.getDrawingCache() == null) {
                //ToastUtil.showCustomMessage(context, "保存失败");
                return null;
            }
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 100, baos);
            view.setDrawingCacheEnabled(false);
            byte[] data = baos.toByteArray();
            File saveFile = BitmapUtil.writeToSDCard(data);

            if (saveFile != null) {
                //ToastUtil.showCustomMessage(context, "成功保存到../"+Constants.QD_PERSONAL_DIR+"目录中");
                scanDirAsync(saveFile, context);

            } else {
                //ToastUtil.showCustomMessage(context, "保存失败");
                return null;
            }
            return saveFile.getAbsolutePath();
        }
        return null;
    }

    /**
     * 给系统发送广播，通知sdcard更新
     */

    public static void scanDirAsync(File file, Context context) {
        try {
            // 增加Android 内部媒体索引
            ContentValues values = new ContentValues();
            values.put("mime_type", "image/jpeg");
            values.put("_data", file.getAbsolutePath());
            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            // 刷新filePath的上一级目录
            MediaScannerConnection.scanFile(context,
                    new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()
                            + "/" + file.getParent()},
                    null, null);
        } catch (Exception e) {
        }
    }


    /**
     * 底片效果
     *
     * @param bm
     * @return
     */
    public static Bitmap handleImageNegative(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int color;
        int r, g, b, a;

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int[] oldPx = new int[width * height];
        int[] newPx = new int[width * height];

        bm.getPixels(oldPx, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            color = oldPx[i];
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);

            r = 255 - r;
            g = 255 - g;
            b = 255 - b;

            if (r > 255) {
                r = 255;
            } else if (r < 0) {
                r = 0;
            }

            if (g > 255) {
                g = 255;
            } else if (g < 0) {
                g = 0;
            }

            if (b > 255) {
                b = 255;
            } else if (b < 0) {
                b = 0;
            }
            newPx[i] = Color.argb(a, r, g, b);
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height);
        return bmp;
    }

    /**
     * 是图片黑白效果
     *
     * @param bm
     * @return
     */
    public static Bitmap handleImageBlackWhite(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int color;
        int r, g, b, a;

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int[] oldPx = new int[width * height];
        int[] newPx = new int[width * height];

        bm.getPixels(oldPx, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            color = oldPx[i];
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);


            r = (int) (0.393 * r + 0.769 * g + 0.189 * b);
            g = (int) (0.349 * r + 0.686 * g + 0.168 * b);
            b = (int) (0.272 * r + 0.534 * g + 0.131 * b);

            if (r > 255) {
                r = 255;
            } else if (r < 0) {
                r = 0;
            }

            if (g > 255) {
                g = 255;
            } else if (g < 0) {
                g = 0;
            }

            if (b > 255) {
                b = 255;
            } else if (b < 0) {
                b = 0;
            }
            newPx[i] = Color.argb(a, r, g, b);
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height);
        return bmp;
    }



   public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);


        return bm1;
    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree 旋转角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
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

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 将图片的旋转角度置为0  ，此方法可以解决某些机型拍照后图像，出现了旋转情况
     *
     * @Title: setPictureDegreeZero
     * @param path
     * @return void
     * @date 2012-12-10 上午10:54:46
     */
    public static void setPictureDegreeZero(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            // 修正图片的旋转角度，设置其不旋转。这里也可以设置其旋转的角度，可以传值过去，
            // 例如旋转90度，传值ExifInterface.ORIENTATION_ROTATE_90，需要将这个值转换为String类型的
            //exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "no");
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "1");//
            exifInterface.saveAttributes();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 旋转图片
     *
     * @param angle 旋转角度
     * @param bitmap 原图
     * @return bitmap 旋转后的图片
     */
    public static Bitmap rotateImage(int angle, Bitmap bitmap) {
        // 图片旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 得到旋转后的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }
}
