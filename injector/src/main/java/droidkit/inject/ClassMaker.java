package droidkit.inject;

import java.io.IOException;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * @author Daniel Serdyukov
 */
interface ClassMaker {

    String M_DELEGATE = "mDelegate";

    void emit(Element element, TypeElement annotation);

    void brewJava() throws IOException;

    void patchClass();

}
