package droidkit.inject.internal;

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
        "droidkit.inject.InjectView",
        "droidkit.inject.OnClick",
        "droidkit.inject.OnActionClick"
})
public class DroidkitProcessor extends AbstractProcessor {

    private final Map<Element, ProxyMaker> mMakers = new HashMap<>();

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
                    getOrCreateMaker(enclosingElement).emit(element, annotation);
                } else {
                    throw new RuntimeException("Injection not supported in nested classes");
                }
            }
        }
        for (final ProxyMaker generator : mMakers.values()) {
            try {
                generator.brewJava();
                generator.patchClass();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    private ProxyMaker getOrCreateMaker(Element enclosingElement) {
        ProxyMaker generator = mMakers.get(enclosingElement);
        if (generator == null) {
            if (mTools.isSubtype(enclosingElement.asType(), "android.app.Activity")) {
                generator = new ActivityMaker(mTools, enclosingElement);
            } else if (mTools.isSubtype(enclosingElement.asType(), "android.app.Fragment")
                    || mTools.isSubtype(enclosingElement.asType(), "android.support.v4.Fragment")) {
                generator = new FragmentMaker(mTools, enclosingElement);
            } else {
                throw new RuntimeException("Annotation processing for " + enclosingElement + " not supported");
            }
            mMakers.put(enclosingElement, generator);
        }
        return generator;
    }

}
