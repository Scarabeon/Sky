package com.bskyb.internettv.utils;

import org.apache.commons.lang3.EnumUtils;
import com.bskyb.internettv.data.ControlLevelEnum;

/**
 * Utility class for expressions evaluation, cannot be instantiated
 * @author Michal Bielik
 *
 */
public abstract class Utils {

	/**
	 * Checks if given control level value exists
	 * @param controlLevelValue
	 * @return {@code TRUE} if exists, {@code FALSE} oherwise
	 */
	public static boolean existsControlLevel(String controlLevelValue) {
		return EnumUtils.isValidEnum(ControlLevelEnum.class, controlLevelValue);
	}

	/**
	 * Checks if given control level matches the limit level
	 * @param level
	 * @param limit
	 * @return {@code TRUE} if matches, {@code FALSE} otherwise
	 */
	public static boolean levelMatch(String level, String limit) {
		return ControlLevelEnum.valueOf(level).ordinal() <= ControlLevelEnum.valueOf(limit).ordinal();
	}
}