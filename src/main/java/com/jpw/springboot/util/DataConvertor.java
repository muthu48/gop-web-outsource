package com.jpw.springboot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataConvertor {

	public static Date convertString2Date(String dateStr) throws ParseException {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    	return formatter.parse(dateStr);

	}
}
