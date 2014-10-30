package droidkit.inject;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * @author Daniel Serdyukov
 */
@SupportedAnnotationTypes({
        "droidkit.inject.InjectView",
        "droidkit.inject.OnClick"
})
public class DroidkitProcessor extends AbstractProcessor {

    private ProxyClassGeneratorFactory mGeneratorFactory;

    private JavacTools mTools;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mGeneratorFactory = new ProxyClassGeneratorFactory(processingEnv);
        mTools = new JavacTools(processingEnv);
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
        if (mTools.isSameType(annotation, InjectView.class)) {
            generator.injectView((VariableElement) element, element.getAnnotation(InjectView.class));
        } else if (mTools.isSameType(annotation, OnClick.class)) {
            generator.injectOnClick((ExecutableElement) element, element.getAnnotation(OnClick.class));
        }
    }

}
