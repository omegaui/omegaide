/*
 * 09/20/2013
 *
 * FindReplaceButtonsEnableResult - Whether "find" and "replace" buttons
 * should be enabled.
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.license.txt file for details.
 */
package org.fife.rsta.ui.search;

/**
 * Returns the result of whether the "action" buttons such as "Find"
 * and "Replace" should be enabled.
 *
 * @author Robert Futrell
 * @version 1.0
 */
// NOTE: This class is public to enable applications to create custom search
// dialogs that extend AbstractSearchDialog, such as a FindInFilesDialog.
public class FindReplaceButtonsEnableResult {

	private boolean enable;
	private String error;

	public FindReplaceButtonsEnableResult(boolean enable, String error) {
		this.enable = enable;
		this.error = error;
	}

	public boolean getEnable() {
		return enable;
	}

	public String getError() {
		return error;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

}
