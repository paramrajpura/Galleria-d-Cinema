package app.com.example.android.galleriadcinema;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Admin on 13-Apr-16.
 */
public class CustomTarget implements Target{

    public String fileName;

    public CustomTarget(String defaultFileName){
        fileName = defaultFileName;
    }
    @Override
    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                fileName+ ".jpg");
                try {
                    boolean isFileCreated = file.createNewFile();
                    if(isFileCreated){
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,ostream);
                        ostream.close();
                    }

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
