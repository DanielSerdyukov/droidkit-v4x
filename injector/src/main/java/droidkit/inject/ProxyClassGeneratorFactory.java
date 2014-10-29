package droidkit.inject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * @author Daniel Serdyukov
 */
class ProxyClassGeneratorFactory {

    private final Map<TypeMirror, ProxyClassGenerator> mProxyClasses = new HashMap<>();

    private final ProcessingEnvironment mEnv;

    private final Types mTypes;

    private final TypeMirror mActivityMirror;

    private final TypeMirror mFragmentMirror;

    ProxyClassGeneratorFactory(ProcessingEnvironment env) {
        mEnv = env;
        mTypes = mEnv.getTypeUtils();
        mActivityMirror = env.getElementUtils().getTypeElement("android.app.Activity").asType();
        mFragmentMirror = env.getElementUtils().getTypeElement("android.app.Fragment").asType();
    }

    ProxyClassGenerator getGenerator(TypeMirror classMirror) {
        if (mTypes.isSubtype(classMirror, mActivityMirror)) {
            return getActivityProxyGenerator(classMirror);
        } else if (mTypes.isSubtype(classMirror, mFragmentMirror)) {
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
            generator = new ActivityProxyClassGenerator(mEnv, (TypeElement) mTypes.asElement(classMirror));
            mProxyClasses.put(classMirror, generator);
        }
        return generator;
    }

    private ProxyClassGenerator getFragmentProxyGenerator(TypeMirror classMirror) {
        ProxyClassGenerator generator = mProxyClasses.get(classMirror);
        if (generator == null) {
            generator = new FragmentProxyClassGenerator(mEnv, (TypeElement) mTypes.asElement(classMirror));
            mProxyClasses.put(classMirror, generator);
        }
        return generator;
    }

}
