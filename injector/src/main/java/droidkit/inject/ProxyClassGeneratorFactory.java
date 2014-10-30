package droidkit.inject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * @author Daniel Serdyukov
 */
class ProxyClassGeneratorFactory {

    private final Map<TypeMirror, ProxyClassGenerator> mProxyClasses = new HashMap<>();

    private final JavacTools mTools;

    private final TypeMirror mActivityMirror;

    private final TypeMirror mFragmentMirror;

    ProxyClassGeneratorFactory(ProcessingEnvironment env) {
        mTools = new JavacTools(env);
        mActivityMirror = env.getElementUtils().getTypeElement("android.app.Activity").asType();
        mFragmentMirror = env.getElementUtils().getTypeElement("android.app.Fragment").asType();
    }

    ProxyClassGenerator getGenerator(TypeMirror classMirror) {
        if (mTools.isSubtype(classMirror, mActivityMirror)) {
            return getActivityProxyGenerator(classMirror);
        } else if (mTools.isSubtype(classMirror, mFragmentMirror)) {
            return getFragmentProxyGenerator(classMirror);
        }
        return null;
    }

    void generateProxyClasses() {
        for (final ProxyClassGenerator generator : mProxyClasses.values()) {
            try {
                generator.generate();
                generator.proxy();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ProxyClassGenerator getActivityProxyGenerator(TypeMirror classMirror) {
        ProxyClassGenerator generator = mProxyClasses.get(classMirror);
        if (generator == null) {
            generator = new ActivityProxyClassGenerator(mTools, mTools.<TypeElement>asElement(classMirror));
            mProxyClasses.put(classMirror, generator);
        }
        return generator;
    }

    private ProxyClassGenerator getFragmentProxyGenerator(TypeMirror classMirror) {
        ProxyClassGenerator generator = mProxyClasses.get(classMirror);
        if (generator == null) {
            generator = new FragmentProxyClassGenerator(mTools, mTools.<TypeElement>asElement(classMirror));
            mProxyClasses.put(classMirror, generator);
        }
        return generator;
    }

}
