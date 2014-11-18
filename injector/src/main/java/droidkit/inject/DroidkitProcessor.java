package droidkit.inject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

/**
 * @author Daniel Serdyukov
 */
@SupportedAnnotationTypes({
        "droidkit.annotation.InjectView",
        "droidkit.annotation.OnClick",
        "droidkit.annotation.OnActionClick",
        "droidkit.annotation.OnCreateLoader",
        "droidkit.annotation.OnLoadFinished"
})
public class DroidkitProcessor extends AbstractProcessor {

    private final Map<Element, ClassMaker> mProxyMakers = new HashMap<>();

    private final Map<Element, ClassMaker> mLcMakers = new HashMap<>();

    private JavacTools mTools;

    @Override
    public synchronized void init(ProcessingEnvironment pEnv) {
        super.init(pEnv);
        mTools = new JavacTools(pEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment rEnv) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }
        for (final TypeElement annotation : annotations) {
            final Set<? extends Element> elements = rEnv.getElementsAnnotatedWith(annotation);
            for (final Element element : elements) {
                final Element enclosingElement = element.getEnclosingElement();
                if (enclosingElement.getKind() == ElementKind.CLASS
                        && enclosingElement.getEnclosingElement().getKind() == ElementKind.PACKAGE) {
                    getOrCreateProxyMaker(enclosingElement).emit(element, annotation);
                    final ClassMaker lcMaker = getOrCreateLcMaker(enclosingElement, annotation);
                    if (lcMaker != null) {
                        lcMaker.emit(element, annotation);
                    }
                } else {
                    throw new RuntimeException("Injection not supported in nested classes");
                }
            }
        }
        try {
            for (final ClassMaker maker : mProxyMakers.values()) {
                maker.brewJava();
                maker.patchClass();
            }
            for (final ClassMaker maker : mLcMakers.values()) {
                maker.brewJava();
                maker.patchClass();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private ClassMaker getOrCreateProxyMaker(Element enclosingElement) {
        ClassMaker maker = mProxyMakers.get(enclosingElement);
        if (maker == null) {
            if (mTools.isSubtype(enclosingElement, "android.app.Activity")) {
                maker = new ActivityMaker(mTools, enclosingElement);
            } else if (mTools.isSubtype(enclosingElement, "android.app.Fragment")
                    || mTools.isSubtype(enclosingElement, "android.support.v4.app.Fragment")) {
                maker = new FragmentMaker(mTools, enclosingElement);
            } else {
                throw new RuntimeException("Annotation processing for " + enclosingElement + " not supported");
            }
            mProxyMakers.put(enclosingElement, maker);
        }
        return maker;
    }

    private ClassMaker getOrCreateLcMaker(Element enclosingElement, TypeElement annotation) {
        ClassMaker maker = mLcMakers.get(enclosingElement);
        if (maker == null && (mTools.isSubtype(annotation, "droidkit.annotation.OnCreateLoader")
                || mTools.isSubtype(annotation, "droidkit.annotation.OnLoadFinished"))) {
            maker = new LCMaker(mTools, enclosingElement);
            mLcMakers.put(enclosingElement, maker);
        }
        return maker;
    }

}
