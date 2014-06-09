/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minetweaker;

import stanhebben.zenscript.annotations.ZenMethod;

/**
 *
 * @author Stan
 */
public interface ILogger {
	@ZenMethod
	public void logInfo(String message);
	
	@ZenMethod
	public void logWarning(String message);
	
	@ZenMethod
	public void logError(String message);
}
