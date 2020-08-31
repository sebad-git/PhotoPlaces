package ort.edu.uy.photoplaces.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.File;
import java.io.IOException;

public class BitmapUtil {

    public static BitmapDescriptor createIcon(Context context, String picturePath, int size) throws IOException {
        try {
            Bitmap miniature = createRoundMiniature(context,picturePath,size);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(miniature);
            return icon;
        }catch (Exception e){ throw new IOException(e); }
    }

    public static BitmapDescriptor createIcon(Context context, int iconId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, iconId);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static Bitmap createMiniature(Context context, String picturePath, int size) throws IOException {
        try {
            File imageFile = new File(picturePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = false;
            Bitmap imagenTemp = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.fromFile(imageFile)),null,options);
            imagenTemp = Bitmap.createScaledBitmap(imagenTemp,size,size,true);
            return imagenTemp;
        }catch (Exception e){ throw new IOException(e); }
    }

    public static Bitmap createRoundMiniature(Context context, String picturePath, int size) throws IOException {
        try {
            Bitmap miniatura = createMiniature(context,picturePath,size);
            Bitmap output = Bitmap.createBitmap(miniatura.getWidth(), miniatura.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, miniatura.getWidth(), miniatura.getHeight());
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawCircle(miniatura.getWidth() / 2, miniatura.getHeight() / 2, miniatura.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(miniatura, rect, rect, paint);
            return output;
        }catch (Exception e){ throw new IOException(e); }
    }
}
