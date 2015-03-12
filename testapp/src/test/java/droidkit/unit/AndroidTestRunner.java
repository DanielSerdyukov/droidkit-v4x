package droidkit.unit;

import android.os.Build;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

/**
 * @author Daniel Serdyukov
 */
public class AndroidTestRunner extends RobolectricTestRunner {

    public AndroidTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        return new AndroidManifest(
                Fs.fileFromPath("build/intermediates/manifests/full/debug/AndroidManifest.xml"),
                Fs.fileFromPath("build/intermediates/res/debug")
        ) {
            @Override
            public int getTargetSdkVersion() {
                return Build.VERSION_CODES.JELLY_BEAN;
            }
        };
    }

}
