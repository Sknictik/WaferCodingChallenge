package sknictik.wafercodingchallenge.presentation.utils;

import java.io.Serializable;

/**
 * Data class containing message information.
 */
public class ResourceMessage implements Serializable {

    private Object mBaseMessage;
    private Object[] mInlineVars;

    public ResourceMessage() {
        mBaseMessage = "";
    }

    public ResourceMessage(final Object baseMessage) {
        this(baseMessage, (Object[]) null);
    }

    public ResourceMessage(final Object baseMessage, final Object... inlineVars) {
        mBaseMessage = baseMessage;
        mInlineVars = inlineVars;
    }

    public Object getBaseMessage() {
        return mBaseMessage;
    }

    public Object[] getInlineVars() {
        return mInlineVars;
    }
}
