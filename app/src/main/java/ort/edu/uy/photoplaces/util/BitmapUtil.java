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

   // public static int TAMANIO_MINIATURA=600;
  //  public static int TAMANIO_MAPA=120;

    public static BitmapDescriptor crearIcono(Context contexto, String rutaImagen,int dimensiones) throws IOException {
        try {
            Bitmap miniatura = crearMiniaturaCircular(contexto,rutaImagen,dimensiones);
            BitmapDescriptor icono = BitmapDescriptorFactory.fromBitmap(miniatura);
            return icono;
        }catch (Exception e){ throw new IOException(e); }
    }

    public static BitmapDescriptor crearIcono(Context contexto, int idIcono) {
        Drawable vectorDrawable = ContextCompat.getDrawable(contexto, idIcono);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static Bitmap createMiniature(Context contexto, String rutaImagen, int dimensiones) throws IOException {
        try {
            File archivoImagen = new File(rutaImagen);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = false;
            Bitmap imagenTemp = BitmapFactory.decodeStream(contexto.getContentResolver().openInputStream(Uri.fromFile(archivoImagen)),null,options);
            imagenTemp = Bitmap.createScaledBitmap(imagenTemp,dimensiones,dimensiones,true);
            return imagenTemp;
        }catch (Exception e){ throw new IOException(e); }
    }

    public static Bitmap crearMiniaturaCircular(Context contexto, String rutaImagen,int dimensiones) throws IOException {
        try {
            Bitmap miniatura = createMiniature(contexto,rutaImagen,dimensiones);
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
