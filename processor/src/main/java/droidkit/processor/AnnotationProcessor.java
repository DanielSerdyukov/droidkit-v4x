package droidkit.processor;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

import droidkit.annotation.InjectView;
import droidkit.annotation.OnActionClick;
import droidkit.annotation.OnClick;
import droidkit.annotation.OnCreateLoader;
import droidkit.annotation.SQLiteObject;

/**
 * @author Daniel Serdyukov
 */
@SupportedAnnotationTypes({
        "droidkit.annotation.SQLiteObject",
        "droidkit.annotation.InjectView",
        "droidkit.annotation.OnClick",
        "droidkit.annotation.OnActionClick",
        "droidkit.annotation.OnCreateLoader"
})
public class AnnotationProcessor extends AbstractProcessor {

    private static final List<Class<? extends Annotation>> METHOD_INJECTIONS = Arrays.asList(
            InjectView.class,
            OnClick.class,
            OnActionClick.class
    );

    private static final String ANDROID_APP_ACTIVITY = "android.app.Activity";

    private static final String ANDROID_APP_FRAGMENT = "android.app.Fragment";

    private static final String ANDROID_SUPPORT_V4_APP_FRAGMENT = "android.support.v4.app.Fragment";

    private final Map<Element, String> mViewInjectors = new HashMap<>();

    private TypeUtils mTypeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mTypeUtils = new TypeUtils((JavacProcessingEnvironment) processingEnv);
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
        try {
            processViewInjections(roundEnv);
            processMethodInjections(roundEnv);
            processSQLiteObjects(roundEnv);
            processLoaderCallbacks(roundEnv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void processViewInjections(RoundEnvironment roundEnv) throws Exception {
        final Map<Element, ViewInjectorMaker> makers = new LinkedHashMap<>();
        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(InjectView.class);
        for (final Element element : elements) {
            final Element originType = element.getEnclosingElement();
            ViewInjectorMaker maker = makers.get(originType);
            if (maker == null) {
                maker = new ViewInjectorMaker(processingEnv, originType);
                makers.put(originType, maker);
            }
            maker.emit((VariableElement) element);
        }
        for (final Map.Entry<Element, ViewInjectorMaker> entry : makers.entrySet()) {
            mViewInjectors.put(entry.getKey(), entry.getValue().make().typeSpec.name);
        }
    }

    private void processMethodInjections(RoundEnvironment roundEnv) throws Exception {
        final Set<Element> classMakers = new LinkedHashSet<>();
        for (final Class<? extends Annotation> annotation : METHOD_INJECTIONS) {
            final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (final Element element : elements) {
                final Element originType = element.getEnclosingElement();
                checkInNestedClass(originType, "@" + annotation.getSimpleName() + " not supported for nested classes");
                if (!classMakers.contains(originType)) {
                    final String viewInjectorName = mViewInjectors.get(originType);
                    if (mTypeUtils.isSubtype(originType, ANDROID_APP_ACTIVITY)) {
                        new ActivityMaker(processingEnv, originType)
                                .setViewInjectorName(viewInjectorName)
                                .make();
                        classMakers.add(originType);
                    } else if (mTypeUtils.isSubtype(originType, ANDROID_APP_FRAGMENT)
                            || mTypeUtils.isSubtype(originType, ANDROID_SUPPORT_V4_APP_FRAGMENT)) {
                        new FragmentMaker(processingEnv, originType)
                                .setViewInjectorName(viewInjectorName)
                                .make();
                        classMakers.add(originType);
                    }
                }
            }
        }
    }

    private void processSQLiteObjects(RoundEnvironment roundEnv) throws Exception {
        final SQLiteSchemaMaker schema = new SQLiteSchemaMaker(processingEnv);
        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SQLiteObject.class);
        for (final Element element : elements) {
            checkInNestedClass(element, "@SQLiteObject not supported for nested classes");
            final SQLiteObject annotation = element.getAnnotation(SQLiteObject.class);
            String tableName = annotation.value();
            if (StringUtils.isEmpty(tableName)) {
                tableName = element.getSimpleName().toString();
            }
            final SQLiteTableMaker tableMaker = new SQLiteTableMaker(processingEnv, element, tableName);
            schema.put(tableMaker.getGenericType(), tableMaker.make());
        }
        schema.make();
    }

    private void processLoaderCallbacks(RoundEnvironment roundEnv) throws Exception {
        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(OnCreateLoader.class);
        for (final Element element : elements) {
            final Element originType = element.getEnclosingElement();
            checkInNestedClass(originType, "@OnCreateLoader not supported for nested classes");
            final OnCreateLoader onCreateLoader = element.getAnnotation(OnCreateLoader.class);
            for (final int loaderId : onCreateLoader.value()) {
                if (onCreateLoader.support()) {
                    new SupportLoaderCallbacksMaker(processingEnv, originType, loaderId).make();
                } else {
                    new LoaderCallbacksMaker(processingEnv, originType, loaderId).make();
                }
            }
        }
    }

    private void checkInNestedClass(Element element, String message) {
        if (ElementKind.PACKAGE != element.getEnclosingElement().getKind()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
        }
    }

}
