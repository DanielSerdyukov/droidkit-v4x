package droidkit.processor;

import com.squareup.javapoet.JavaFile;

/**
 * @author Daniel Serdyukov
 */
interface ClassMaker {

    String AUTO_GENERATED = "AUTO-GENERATED FILE.  DO NOT MODIFY.";

    JavaFile make() throws Exception;

}
