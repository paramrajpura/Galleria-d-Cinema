package app.com.example.android.galleriadcinema;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Admin on 13-Apr-16.
 */
public class CustomTarget implements Target{

    public String fileName;

    CustomTarget(String defaultFileName){
        fileName = defaultFileName;
    }
    @Override
    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(
                        Environment.getExternalStorageDirectory().getPath() + "/" +
                                fileName+ ".jpg");
                try {
                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,ostream);
                    ostream.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {}
}
