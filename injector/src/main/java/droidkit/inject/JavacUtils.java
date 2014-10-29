package droidkit.inject;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Daniel Serdyukov
 */
final class JavacUtils {

    private JavacUtils() {
    }

    @SuppressWarnings("unchecked")
    static <T> List<T> list(T... items) {
        return List.from(items);
    }

    static <T> List<T> list(Iterable<T> items) {
        return List.from(items);
    }

    static JCTree.JCBlock block(TreeMaker maker, JCTree.JCStatement... statements) {
        return maker.Block(0, list(statements));
    }

    static JCTree.JCBlock block(TreeMaker maker, Iterable<JCTree.JCStatement> statements) {
        return maker.Block(0, list(statements));
    }

    static JCTree.JCExpression selector(TreeMaker maker, Names names, String... selectors) {
        final Iterator<String> iterator = Arrays.asList(selectors).iterator();
        if (iterator.hasNext()) {
            JCTree.JCExpression selector = maker.Ident(names.fromString(iterator.next()));
            while (iterator.hasNext()) {
                selector = maker.Select(selector, names.fromString(iterator.next()));
            }
            return selector;
        }
        throw new IllegalArgumentException("Empty selector");
    }

}
