package droidkit.processor;

import com.squareup.javapoet.ClassName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

/**
 * @author Daniel Serdyukov
 */
class SupportLoaderCallbacksMaker extends LoaderCallbacksMaker {

    public SupportLoaderCallbacksMaker(ProcessingEnvironment env, Element element, int loaderId) {
        super(env, element, loaderId);
    }

    @Override
    protected String getSuffix() {
        return "$LCv4";
    }

    @Override
    protected ClassName getLoaderVersion() {
        return ClassName.get("android.support.v4.content", "Loader");
    }

    @Override
    protected ClassName getLoaderCallbacksVersion() {
        return ClassName.get("android.support.v4.app", "LoaderManager", "LoaderCallbacks");
    }

}
