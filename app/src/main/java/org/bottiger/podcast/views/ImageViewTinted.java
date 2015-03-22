package org.bottiger.podcast.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.bottiger.podcast.listeners.PaletteListener;
import org.bottiger.podcast.listeners.PaletteObservable;
import org.bottiger.podcast.utils.ColorExtractor;

/**
 * Created by apl on 21-03-2015.
 */
public class ImageViewTinted extends ImageView implements PaletteListener {

    String mPaletteKey = null;

    public ImageViewTinted(Context context) {
        super(context);
    }

    public ImageViewTinted(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewTinted(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageViewTinted(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setPaletteKey(@NonNull String argKey) {
        mPaletteKey = argKey;
        PaletteObservable.registerListener(this);
    }

    @Override
    public void onPaletteFound(Palette argChangedPalette) {
        ColorExtractor extractor = new ColorExtractor(argChangedPalette);
        setColorFilter(extractor.getPrimary(), PorterDuff.Mode.SRC_IN   );
        //setColorFilter(Color.WHITE, PorterDuff.Mode.ADD);
        invalidate();
    }

    @Override
    public String getPaletteUrl() {
        return mPaletteKey;
    }
}
