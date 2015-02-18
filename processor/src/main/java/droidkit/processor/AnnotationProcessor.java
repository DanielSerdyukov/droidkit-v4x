package droidkit.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import droidkit.annotation.InjectView;
import droidkit.annotation.OnClick;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Daniel Serdyukov
 */
@SupportedAnnotationTypes({
        "droidkit.annotation.InjectView"
})
public class AnnotationProcessor extends AbstractProcessor {

    private static final String ANDROID_APP_ACTIVITY = "android.app.Activity";

    private static final String ANDROID_APP_FRAGMENT = "android.app.Fragment";

    private static final String ANDROID_SUPPORT_V4_APP_FRAGMENT = "android.support.v4.app.Fragment";

    private final Map<Element, LifecycleMaker> mLifecycleMakers = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        JavacLog.init(processingEnv);
        JavacUtils.init(processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }
        processInjectView(roundEnv);
        processOnClick(roundEnv);
        try {
            for (final Map.Entry<Element, LifecycleMaker> entry : mLifecycleMakers.entrySet()) {
                final Element element = entry.getKey();
                final JavaFile lifecycle = entry.getValue().make();
                if (JavacUtils.isSubtype(element, ANDROID_APP_ACTIVITY)) {
                    new ActivityMaker(processingEnv)
                            .withOriginType((TypeElement) element)
                            .withLifecycle(ClassName.get(lifecycle.packageName, lifecycle.typeSpec.name))
                            .make();
                } else if (JavacUtils.isSubtype(element, ANDROID_APP_FRAGMENT)
                        || JavacUtils.isSubtype(element, ANDROID_SUPPORT_V4_APP_FRAGMENT)) {
                    new FragmentMaker(processingEnv)
                            .withOriginType((TypeElement) element)
                            .withLifecycle(ClassName.get(lifecycle.packageName, lifecycle.typeSpec.name))
                            .make();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void processInjectView(RoundEnvironment roundEnv) {
        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(InjectView.class);
        for (final Element element : elements) {
            final Element originElement = element.getEnclosingElement();
            if (ElementKind.PACKAGE != originElement.getEnclosingElement().getKind()) {
                JavacLog.error(element, "@InjectView not supported for nested classes");
            } else {
                getOrCreateMaker(originElement).emit(element, element.getAnnotation(InjectView.class));
            }
        }
    }

    private void processOnClick(RoundEnvironment roundEnv) {
        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(OnClick.class);
        for (final Element element : elements) {
            final Element originElement = element.getEnclosingElement();
            if (ElementKind.PACKAGE != originElement.getEnclosingElement().getKind()) {
                JavacLog.error(element, "@OnClick not supported for nested classes");
            } else {
                getOrCreateMaker(originElement).emit(element, element.getAnnotation(OnClick.class));
            }
        }
    }

    private LifecycleMaker getOrCreateMaker(Element originElement) {
        LifecycleMaker maker = mLifecycleMakers.get(originElement);
        if (maker == null) {
            maker = new LifecycleMaker(processingEnv).withOriginType((TypeElement) originElement);
            mLifecycleMakers.put(originElement, maker);
        }
        return maker;
    }

}
