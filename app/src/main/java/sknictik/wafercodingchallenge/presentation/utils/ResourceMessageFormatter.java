package sknictik.wafercodingchallenge.presentation.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import sknictik.wafercodingchallenge.BuildConfig;

/**
 * Current implementation supports only strings and @StringRes integers. Using numbers to replace %d will not work.
 * Use %s instead and convert number to String.
 */
public class ResourceMessageFormatter {

    private static final String LOG_TAG = ResourceMessageFormatter.class.getSimpleName();

    private final Context context;

    public ResourceMessageFormatter(final Context context) {
        this.context = context;
    }

    /**
     * Format resourceMessage contents into one string. If exception occur while trying to format error message either if in DEBUG mode - throw exception.
     * Else just show it in log and return empty string.
     *
     * @param resourceMessage message contents
     * @return a string representing contents of resourceMessage. Wrong type inline variables will be replaced with "null".
     */
    public String format(final ResourceMessage resourceMessage) {
        if (context == null) {
            logOrThrow(new NullPointerException("Context can't be null"));
            return "";
        }
        if (resourceMessage == null) {
            logOrThrow(new NullPointerException("Message can't be null"));
            return "";
        }

        final String base = getStringFromObject(resourceMessage.getBaseMessage());
        if (base == null) {
            logOrThrow(new NullPointerException("Message base can't be null"));
            return "";
        }

        if (resourceMessage.getInlineVars() != null && resourceMessage.getInlineVars().length > 0) {
            final String[] inlineVars = new String[resourceMessage.getInlineVars().length];
            for (int i = 0; i < resourceMessage.getInlineVars().length; i++) {
                final String inlineVar = getStringFromObject(resourceMessage.getInlineVars()[i]);
                if (inlineVar != null) {
                    inlineVars[i] = inlineVar;
                }
            }
            return String.format(base, (Object[]) inlineVars);
        } else {
            return base;
        }
    }

    private static void logOrThrow(final RuntimeException e) {
        if (BuildConfig.DEBUG) {
            throw e;
        } else {
            Log.e(LOG_TAG, "Unable to format error", e);
        }
    }

    @Nullable
    private String getStringFromObject(final Object messageObj) {
        if (messageObj == null) {
            return null;
        }
        else if (messageObj instanceof Integer) {
            return context.getString((Integer) messageObj);
        } else if (messageObj instanceof String) {
            return (String) messageObj;
        } else {
            logOrThrow(new IllegalArgumentException("Unsupported variable format"));
            return null;
        }
    }

}
