package com.harvi.tailor.utils;

import java.util.Arrays;
import java.util.logging.Logger;

import com.harvi.tailor.entities.ApiError;

public class Utils {
	public static ApiError createApiError(Exception e, String shortErrorMsg, Logger logger) {
		String longErrorMsg = e == null ? ""
				: "ExceptionMsg: " + e.getMessage() + "\nStackTrace: " + Arrays.toString(e.getStackTrace());
		logger.severe(shortErrorMsg + (e == null ? "" : (": " + longErrorMsg)));
		ApiError apiError = new ApiError(shortErrorMsg, longErrorMsg);
		return apiError;
	}
}
