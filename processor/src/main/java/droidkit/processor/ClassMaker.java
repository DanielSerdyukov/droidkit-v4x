package droidkit.processor;

import com.squareup.javapoet.JavaFile;

import java.io.IOException;

/**
 * @author Daniel Serdyukov
 */
interface ClassMaker {

    String AUTO_GENERATED = "AUTO-GENERATED FILE. DO NOT MODIFY.";

    String PROXY = "$Proxy";

    String M_LIFECYCLE = "mLifecycle";

    JavaFile make() throws IOException;

}
