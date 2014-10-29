package droidkit.inject;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author Daniel Serdyukov
 */
@SupportedAnnotationTypes({
        "droidkit.inject.InjectView"
})
public class DroidkitProcessor extends AbstractProcessor {

    private ProxyClassGeneratorFactory mGeneratorFactory;

    private Elements mElements;

    private Types mTypes;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mGeneratorFactory = new ProxyClassGeneratorFactory(processingEnv);
        mElements = processingEnv.getElementUtils();
        mTypes = processingEnv.getTypeUtils();
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
                final TypeMirror classMirror = element.getEnclosingElement().asType();
                final ProxyClassGenerator generator = mGeneratorFactory.getGenerator(classMirror);
                if (generator != null) {
                    process(generator, annotation, element);
                }
            }
        }
        mGeneratorFactory.generateProxyClasses();
        return true;
    }

    private void process(ProxyClassGenerator generator, TypeElement annotation, Element element) {
        if (isSameType(annotation, InjectView.class)) {
            generator.injectView(element, element.getAnnotation(InjectView.class));
        }
    }

    private boolean isSameType(TypeElement element, Class<? extends Annotation> type) {
        return mTypes.isSameType(element.asType(), mElements.getTypeElement(type.getName()).asType());
    }

}
