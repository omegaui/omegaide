/*
 * 09/20/2013
 *
 * SearchUtil - Utility methods for this package.
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.license.txt file for details.
 */
package org.fife.rsta.ui.search;


/**
 * Utility methods for this package.
 *
 * @author Robert Futrell
 * @version 1.0
 */
final class SearchUtil {


    /**
     * Private constructor to prevent instantiation.
     */
    private SearchUtil() {
    }

	/**
	 * Formats an error message from a find/replace button enable result for
	 * use in a tool tip.  This assumes the error (if any) came from a
	 * <code>PatternSyntaxException</code>.
	 *
	 * @param res The result.
	 * @return The tool tip, or <code>null</code> if no error message was
	 *         specified in <code>res</code>.
	 */
	public static String getToolTip(FindReplaceButtonsEnableResult res) {
		String tooltip = res.getError();
		if (tooltip!=null && tooltip.indexOf('\n')>-1) {
			tooltip = tooltip.replaceFirst("\\\n", "</b><br><pre>");
			tooltip = "<html><b>" + tooltip;
		}
		return tooltip;
	}


}
