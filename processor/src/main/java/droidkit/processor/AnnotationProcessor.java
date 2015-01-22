package droidkit.processor;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import droidkit.annotation.InjectView;
import droidkit.annotation.OnActionClick;
import droidkit.annotation.OnClick;
import droidkit.annotation.OnCreateLoader;
import droidkit.annotation.OnLoadFinished;
import droidkit.annotation.OnResetLoader;
import droidkit.annotation.SQLiteObject;

/**
 * @author Daniel Serdyukov
 */
@SupportedAnnotationTypes({
        "droidkit.annotation.SQLiteObject",
        "droidkit.annotation.InjectView",
        "droidkit.annotation.OnClick",
        "droidkit.annotation.OnActionClick",
        "droidkit.annotation.OnCreateLoader",
        "droidkit.annotation.OnLoadFinished",
        "droidkit.annotation.OnResetLoader"
})
public class AnnotationProcessor extends AbstractProcessor {

    private static final List<Class<? extends Annotation>> INJECTIONS = Arrays.asList(
            InjectView.class,
            OnClick.class,
            OnActionClick.class,
            OnCreateLoader.class,
            OnLoadFinished.class,
            OnResetLoader.class
    );

    private static final String ANDROID_APP_ACTIVITY = "android.app.Activity";

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
            processInjections(roundEnv);
            processSQLiteObjects(roundEnv);
            processLoaderCallbacks(roundEnv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void processInjections(RoundEnvironment roundEnv) throws Exception {
        final Set<Element> classMakers = new LinkedHashSet<>();
        for (final Class<? extends Annotation> annotation : INJECTIONS) {
            final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (final Element element : elements) {
                final Element originType = element.getEnclosingElement();
                checkInNestedClass(originType, "@" + annotation.getSimpleName() + " not supported for nested classes");
                if (!classMakers.contains(originType)) {
                    if (mTypeUtils.isSubtype(originType, ANDROID_APP_ACTIVITY)) {
                        new ActivityMaker(processingEnv, originType).make();
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

    }

    private void checkInNestedClass(Element element, String message) {
        if (ElementKind.PACKAGE != element.getEnclosingElement().getKind()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
        }
    }

}
