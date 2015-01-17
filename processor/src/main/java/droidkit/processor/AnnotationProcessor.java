package droidkit.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import droidkit.annotation.SQLiteObject;

/**
 * @author Daniel Serdyukov
 */
@SupportedAnnotationTypes({
        "droidkit.annotation.SQLiteObject"
})
public class AnnotationProcessor extends AbstractProcessor {

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
            processSQLiteObject(roundEnv);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void processSQLiteObject(RoundEnvironment roundEnv) throws Exception {
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

    private void checkInNestedClass(Element element, String message) {
        if (ElementKind.PACKAGE != element.getEnclosingElement().getKind()) {
            throw new RuntimeException(message);
        }
    }

}
