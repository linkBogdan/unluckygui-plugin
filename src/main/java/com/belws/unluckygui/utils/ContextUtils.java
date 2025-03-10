package com.belws.unluckygui.utils;

import net.luckperms.api.node.Node;

public class ContextUtils {

    // Utility method to format context for display
    public static String formatContextForDisplay(Node node) {
        String contextDisplay = "Global";  // Default to "Global" if empty
        if (!node.getContexts().isEmpty()) {
            // Extract the server context (if any)
            contextDisplay = node.getContexts().toString();
            if (contextDisplay.contains("server=")) {
                // Extract server name from the context
                String serverName = contextDisplay.substring(contextDisplay.indexOf("server=") + 7, contextDisplay.indexOf("]"));
                contextDisplay = "Server: " + serverName;
            }
        }
        return contextDisplay;
    }
}
