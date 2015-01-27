package droidkit.view;

import android.content.res.Resources;

/**
 * @author Daniel Serdyukov
 */
public class NoSuchViewException extends RuntimeException {

    private static final long serialVersionUID = -998833269062570659L;

    public NoSuchViewException(Resources res, int viewId) {
        super("No such view " + res.getResourceName(viewId));
    }

}
