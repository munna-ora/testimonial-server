package com.orastays.testimonialserver.helper;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Util {

	private static final Logger logger = LogManager.getLogger(Util.class);

	public static<T> T transform(Object from, Class<T> valueType) throws Exception {
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		String json = objectMapper.writeValueAsString(from);
		return objectMapper.readValue(json, valueType);
	}
	
	public static String objectToJSON(Object from) throws Exception {
		
		String json = null;
		if(Objects.nonNull(from)) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			json = objectMapper.writeValueAsString(from);
		}
		return json;
	}
	
	public static<T> T jsonToObject(String value, Class<T> t) throws Exception {
		
		if(t != null && StringUtils.isNotBlank(value)) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			return objectMapper.readValue(value, t);
		} else {
			return null;
		}
		
	}
	
	public static boolean checkDate(String startDate, String endDate) {

		boolean flag = false;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date stDate = formatter.parse(startDate);
			Date edDate = formatter.parse(endDate);
			Date currDate = formatter.parse(Util.getCurrentDateTime());

			int i = (currDate.compareTo(stDate));
			int j = (currDate.compareTo(edDate));
			flag = (i >= 0 && j <= 0) ? true : false;

		} catch (Exception e) {

			return false;
		}

		return flag;
	}

	public static boolean checkNullOrSpaceValue(String value) {

		try {
			if (value != null) {
				if (value.trim().length() > 0) {
					if (Double.valueOf(value) > 0) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isNumber(String data) {

		String regex = "\\d+";
		String value = "";
		String[] result = data.split("\\.");
		if (result[1].length() == 1) {
			if (result[1].contains("0")) {
				value = result[0];
			} else {
				value = data;
			}
		}
		return !(value.matches(regex));
	}

	private static final String PHONE_NUMBER_GARBAGE_REGEX = "[()\\s-]+";
	private static final String PHONE_NUMBER_REGEX = "^((\\+[1-9]?[0-9])|0)?[7-9][0-9]{9}$";
	private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

	public static boolean validatePhoneNumber(String phoneNumber) {
		return phoneNumber != null
				&& PHONE_NUMBER_PATTERN.matcher(phoneNumber.replaceAll(PHONE_NUMBER_GARBAGE_REGEX, "")).matches();
	}

	public static void saveHttpRequestDataInLogFile(HttpServletRequest request) {

		if (logger.isInfoEnabled()) {

			logger.info("saveHttpRequestDataInLogFile ::  " + request);

		}

		try {
			Calendar c = Calendar.getInstance();

			long milliseconds = c.getTimeInMillis();

			String remoteAddress = null;
			String remoteHost = null;
			int remotePort = -1;
			String remoteUser = null;
			String requestUrl = null;

			try {

				remoteAddress = request.getRemoteAddr();
				remoteHost = request.getRemoteHost();
				remotePort = request.getRemotePort();
				remoteUser = request.getRemoteUser();
				requestUrl = request.getRequestURL().toString();

				if (logger.isInfoEnabled()) {

					logger.info("Request Data Follows -------> ");
					logger.info("Time Of API Hit :: " + milliseconds);
					logger.info("Remote Address :: " + remoteAddress);
					logger.info("Remote Host :: " + remoteHost);
					logger.info("Remote Port :: " + remotePort);
					logger.info("Remote User :: " + remoteUser);
					logger.info("Request URL ::  " + requestUrl);

				}

			} catch (Exception e) {

				if (logger.isInfoEnabled()) {

					logger.info("Exception In Receiving Request Data, Exception Message is :: " + e.getMessage());

				}

			}

		} catch (Exception e) {

			if (logger.isInfoEnabled()) {

				logger.info("Exception In Receiving Request Data, Exception Message is :: " + e.getMessage());

			}
		}

	}

	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	public static boolean isEmpty(Date date) {
		return date == null;
	}

	public static boolean isEmpty(Object object) {
		return object == null;
	}

	public static boolean stringIsEmpty(String value) {

		return value.equalsIgnoreCase("") || value == null;
	}

	public static boolean isNumeric(String value) {
		Pattern pattern = Pattern.compile("\\d+");

		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	public static String formatDateFromEntity(Date value) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

		String formatDate = simpleDateFormat.format(value);

		return formatDate;

	}

	public static String stringcut(String data) {
		return data.replace(" ", "-").trim().toLowerCase();
	}

	public static String utfEightConverter(String string) {

		try {
			byte[] array = string.getBytes("ISO-8859-1");
			String s = new String(array, Charset.forName("UTF-8"));
			string = StringEscapeUtils.unescapeJava(s);

		} catch (Exception e) {
			logger.error(e);
		}

		return string;
	}

	public static boolean emailValidator(String email) {


		String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(emailPattern);
		java.util.regex.Matcher m = p.matcher(email);

		return m.matches();
	}

	public static Date getDatefromTimestamp(String timestamp, String dateFormate) throws ParseException {
		
		long time = Long.parseLong(timestamp);
		Date date = new Date(time);
		Format format = new SimpleDateFormat(dateFormate);
		format.format(date);
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormate);
		return dateFormat.parse(format.format(date));
	}

	public static String constructAppUrl(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();

	}

	public static String currentDay() {
		
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
		
		return day.toUpperCase();
	}

	public static String getYesterdayDateString() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);

		return dateFormat.format(cal.getTime());
	}

	public static String getCurrentMonthAndYear() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		Calendar cal = Calendar.getInstance();

		return dateFormat.format(cal.getTime());
	}

	public static String getBeforeYesterdayDateString() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -2);

		return dateFormat.format(cal.getTime());
	}

	public static String getCurrentDateTime() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		return dateFormat.format(cal.getTime());

	}

	public static String getCurrentDate() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();

		return dateFormat.format(cal.getTime());

	}

	public static String getDateAfterNumberOfDays(String numberOfWeeks, String numberOfMonths) {

		final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
		final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		@SuppressWarnings("unused")
		final DateTimeFormatter dateFormat8 = DateTimeFormatter.ofPattern(DATE_FORMAT);
		Date currentDate = new Date();

		// convert date to localdatetime
		LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		// plus one
		if (!Objects.isNull(numberOfWeeks)) {
			localDateTime = localDateTime.minusWeeks(Integer.valueOf(numberOfWeeks));
		}
		if (!Objects.isNull(numberOfMonths)) {
			localDateTime = localDateTime.minusMonths(Integer.valueOf(numberOfMonths));
		}

		// convert LocalDateTime to date
		Date currentDatePlusOneDay = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		
		return dateFormat.format(currentDatePlusOneDay).toString();
	}

	public static String getDateAfterNumberOfMonths(String numberOfMonths, Date currentDate) {

		final String DATE_FORMAT = "yyyy-MM-dd";
		final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

		// convert date to localdatetime
		LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		// minus one

		if (!numberOfMonths.equalsIgnoreCase("6")) {
			if (!Objects.isNull(numberOfMonths)) {
				localDateTime = localDateTime.minusMonths(Integer.valueOf(numberOfMonths));
				localDateTime = localDateTime.plusDays(1);
			}
		} else {
			if (!Objects.isNull(numberOfMonths)) {
				localDateTime = localDateTime.minusMonths(Integer.valueOf(numberOfMonths));
				localDateTime = localDateTime.plusDays(1);
			}
		}

		// convert LocalDateTime to date
		Date currentDatePlusOneDay = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

		return dateFormat.format(currentDatePlusOneDay);
	}

	public static String getDateAfterNumberOfMonths1(String numberOfMonths, Date currentDate) {

		final String DATE_FORMAT = "yyyy-MM-dd";
		final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

		// convert date to localdatetime
		LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		// minus one

		if (!numberOfMonths.equalsIgnoreCase("6")) {
			if (!Objects.isNull(numberOfMonths)) {
				localDateTime = localDateTime.plusMonths(Integer.valueOf(numberOfMonths));
			}
		} else {
			if (!Objects.isNull(numberOfMonths)) {
				localDateTime = localDateTime.plusMonths(Integer.valueOf(numberOfMonths));
				localDateTime = localDateTime.plusDays(1);
			}
		}

		// convert LocalDateTime to date
		Date currentDatePlusOneDay = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

		return dateFormat.format(currentDatePlusOneDay);
	}

	public static Date changeTime(Date date) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		
		return cal.getTime();
	}

	public static String getDateAfterNumberOfDays(String numberOfDays) {

		final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
		final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		Date currentDate = new Date();

		// convert date to localdatetime
		LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		localDateTime = localDateTime.minusDays(Integer.valueOf(numberOfDays));

		// convert LocalDateTime to date
		Date currentDatePlusOneDay = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		
		return dateFormat.format(currentDatePlusOneDay).toString();
	}

	public static String getPreviousDateTime(String hour) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -Integer.parseInt(hour));
		String dateInString = dateFormat.format(cal.getTime());

		return dateInString;
	}

	public static String getRandom28() {

		char[] chars = "4b195529515dbcfa525f3d3261648b4c07ef6aa6ca7f548973758a6e25bb9bf17d43104245199d6a4b5e044c4f5f89401de337e16b77a2ac72e8e8a9150cbc2e"
				.toCharArray();

		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 28; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}

		return sb.toString();

	}

	public static String decPass(String hashPass) {

		String salt = Util.getRandomSalt();
		int saltLengthHalf = (salt.length()) / 2;
		String md5 = hashPass.substring(saltLengthHalf, hashPass.length());
		md5 = md5.substring(0, md5.length() - saltLengthHalf);

		return md5;
	}

	public static String getRandomSalt() {

		char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?"
				.toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 28; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}

		return sb.toString();

	}

	public static boolean isUTF8MisInterpreted(String input) {
		
		return isUTF8MisInterpreted(input, "Windows-1252");
	}

	public static boolean isUTF8MisInterpreted(String input, String encoding) {

		CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
		CharsetEncoder encoder = Charset.forName(encoding).newEncoder();
		ByteBuffer tmp;
		try {
			tmp = encoder.encode(CharBuffer.wrap(input));
		}

		catch (CharacterCodingException e) {
			return false;
		}

		try {
			decoder.decode(tmp);
			return true;
		} catch (CharacterCodingException e) {
			return false;
		}
	}


	public static String renameFileName(String fileName) {
		String imageExtention = fileName.substring(fileName.lastIndexOf(".") + 1);

		fileName = fileName.substring(0, fileName.lastIndexOf("."));

		fileName = fileName.replaceAll("[^\\w]", "") + "_" + System.currentTimeMillis() + "." + imageExtention;
		return fileName;
	}

	public static String generateUploadPath(String folderName) {

		if (logger.isInfoEnabled()) {
			logger.info("generateImageUploadPath-Start");
		}

		String rootPath = System.getProperty("catalina.home");
		rootPath = rootPath + File.separator + "webapps";

		File dir = new File(rootPath + File.separator + folderName);

		if (!dir.exists())
			dir.mkdirs();

		String uploadImagePath = dir + File.separator;

		if (logger.isInfoEnabled()) {
			logger.info("generateImageUploadPath-End");
		}
		return uploadImagePath;
	}

	public static String generateFileUploadPath(ServletContext context, String folderName) {

		if (logger.isInfoEnabled()) {
			logger.info("generateImageUploadPath-Start");
		}

		// get absolute path of the application
		String appPath = context.getRealPath("");
		appPath = appPath + "resources";

		File dir = new File(appPath + File.separator + folderName);

		if (!dir.exists())
			dir.mkdirs();

		String uploadImagePath = dir + File.separator;

		if (logger.isInfoEnabled()) {
			logger.info("generateImageUploadPath-End");
		}
		return uploadImagePath;
	}

	public static BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {

		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();

		return resizedImage;
	}

	private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

	private static Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

	public static boolean validatePassword(final String password) {

		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}

	Cipher ecipher;
	Cipher dcipher;
	// 8-byte Salt
	byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3,
			(byte) 0x03 };
	// Iteration count
	int iterationCount = 19;

	public static String getFinancialYear() {
		
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String finYear = "";
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		if (month <= 3) {
			finYear = (year - 1) + "-" + year;
			// System.out.println("Financial Year : " + (year - 1) + "-" +
			// year);
		} else {
			finYear = year + "-" + (year + 1);
			// System.out.println("Financial Year : " + year + "-" + (year +
			// 1));
		}

		return finYear;
	}

	public static String addDate(String time) {

		String output = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar c = Calendar.getInstance();
			c.setTime(new Date()); // Now use today date.
			c.add(Calendar.DATE, Integer.parseInt(time)); // Adding days
			output = sdf.format(c.getTime());
		} catch (Exception e) {

		}

		return output;

	}

	public static String addHour(String time) {

		String output = "";
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar c = Calendar.getInstance();
			c.setTime(new Date()); // Now use today date.
			c.add(Calendar.HOUR_OF_DAY, Integer.parseInt(time)); // Adding hour
			output = sdf.format(c.getTime());
		} catch (Exception e) {

		}

		return output;

	}

	public static String addMinute(String firstDate, String time) {

		String output = "";
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date date1 = sdf.parse(firstDate);
			Calendar c = Calendar.getInstance();
			c.setTime(date1); // Now use today date.
			c.add(Calendar.MINUTE, Integer.parseInt(time)); // Adding Minute
			output = sdf.format(c.getTime());
		} catch (Exception e) {

		}

		return output;

	}

	public static String addMinute(Date firstDate, String time) {

		String output = "";
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Calendar c = Calendar.getInstance();
			c.setTime(firstDate); // Now use today date.
			c.add(Calendar.MINUTE, Integer.parseInt(time)); // Adding Minute
			output = sdf.format(c.getTime());
		} catch (Exception e) {

		}

		return output;

	}

	public static int getDateDiff(String firstDate) {

		try {

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(firstDate);
			String currentDate = format.format(new Date());
			Date date2 = format.parse(currentDate);
			Double diffInMillies = (date2.getTime() - date1.getTime()) / (1000.0 * 60 * 60 * 24);
			return diffInMillies.intValue();

		} catch (ParseException e) {

			return 0;
		}
	}

	public static int getMinuteDiff(String firstDate) {

		try {

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(firstDate);
			String currentDate = format.format(new Date());
			Date date2 = format.parse(currentDate);
			Double diffInMillies = (date2.getTime() - date1.getTime()) / (1000.0 * 60);

			return diffInMillies.intValue();

		} catch (Exception e) {

			return 0;
		}
	}

	public static int getDayDiff(String firstDate, String endDate) {

		try {

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = format.parse(firstDate);
			Date date2 = format.parse(endDate);
			Double diffInMillies = (date2.getTime() - date1.getTime()) / (1000.0 * 60 * 60 * 24);
			return diffInMillies.intValue();

		} catch (ParseException e) {

			return 0;
		}
	}

	public static boolean passwordCheck(String password) {
		int count = 0;

		/*
		 * if(password.length() >= 8){ System.out.println("isAtLeast8"); count++; }
		 */
		if (!password.matches("[A-Za-z0-9 ]*")) {
			// System.out.println("hasSpecial");
			count++;
		}
		if (!password.equals(password.toLowerCase())) {
			// System.out.println("hasUppercase");
			count++;
		}
		if (!password.equals(password.toUpperCase())) {
			// System.out.println("hasLowercase");
			count++;
		}
		if (Pattern.compile("[0-9]").matcher(password).find()) {
			// System.out.println("hasNumber");
			count++;
		}
		return (count >= 3) ? true : false;
	}

	public static String generateMd5Str(String str) {
		
		StringBuffer sb = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			md.update(str.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			
			return sb.toString();
			
		} catch (NoSuchAlgorithmException e) {

		}
		
		return null;
	}


	static char digits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	public static char randomDecimalDigit() {
		return digits[(int) Math.floor(Math.random() * 10)];
	}

	public static String randomDecimalString(int ndigits) {

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < ndigits; i++) {
			result.append(randomDecimalDigit());
		}

		// System.out.println("Random Number ==>> "+result.toString());

		return result.toString();
	}

	/**
	 * this method is used to validate fax
	 * 
	 * @param fax
	 * @return
	 */
	public static boolean faxValidator(String fax) {

		String FAX_PATTERN = "\\d{1,15}";

		java.util.regex.Pattern p = java.util.regex.Pattern.compile(FAX_PATTERN);
		java.util.regex.Matcher m = p.matcher(fax);

		return m.matches();
	}

	/**
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean phoneValidator(String phone) {

		String PHONE_PATTERN = "\\d{10}";

		java.util.regex.Pattern p = java.util.regex.Pattern.compile(PHONE_PATTERN);
		java.util.regex.Matcher m = p.matcher(phone);

		return m.matches();
	}

	public static long getLongFromDateField(String effectiveStartDate) {

		String stringDate = effectiveStartDate;
		long milliseconds = 0L;

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d = f.parse(stringDate);
			milliseconds = d.getTime();

			System.out.println(milliseconds);
		} catch (ParseException e) {

		}

		return milliseconds;

	}

	public static boolean checkPasswordPolicy(String password) {

		String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,20}";
		return password.matches(pattern);
	}

	public static boolean checkSpecialCharater(String s) {

		s.trim();
		Pattern p = Pattern.compile("[^A-Za-z0-9]");
		Matcher m = p.matcher(s);
		boolean b = m.find();
		return b;

	}

	public static <T> T findAndRemoveFirst(Iterable<? extends T> collection, Predicate<? super T> test) {
		T value = null;
		for (Iterator<? extends T> it = collection.iterator(); it.hasNext();)
			if (test.test(value = it.next())) {
				it.remove();
				return value;
			}
		return null;
	}

	public static String formatDate(String date) {

		SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		String reformattedStr = null;
		try {

			reformattedStr = myFormat.format(fromUser.parse(date));
		} catch (ParseException e) {

		}

		return reformattedStr;
	}

	public static boolean checkAlpha(String str) {

		String regex = "^[0-9]\\d*(\\.\\d+)?$";
		return str.matches(regex);
	}

	public static boolean checkMobileNumber(String mobileNumber) {

		String mobileNumberPattern = "((?=.*\\d)(?=.*[0-9])(?=.*[+]).{10,14})";
		Pattern pattern = Pattern.compile(mobileNumberPattern);
		Matcher matcher = pattern.matcher(mobileNumber);
		return matcher.matches();
	}

	public static String encryptCard(String input) {

		String output = "";
		if (!isEmpty(input)) {

			if (input.length() > 4) {

				String input1 = input.substring(input.length() - 4, input.length());
				String input2 = "";
				input = input.substring(0, input.length() - 4);
				for (int i = 0; i < input.length(); i++)
					input2 = input2 + "*";
				output = input2 + input1;
			}
		}

		return output;
	}

	public static boolean checkOnlyZero(String amount) {

		Boolean flag = true;
		if (!isEmpty(amount)) {
			for (int i = 0; i < amount.length(); i++) {
				if (!(amount.charAt(i) == '0' || amount.charAt(i) == '.')) {
					flag = false;
					break;
				}
			}
		}
		return flag;
	}

	public static List<String> findDate() {

		List<String> dates = new ArrayList<String>();

		Date date3 = new Date();
		LocalDateTime localDate = date3.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		int year = localDate.getYear();
		int month = localDate.getMonthValue();
		int hour = localDate.getHour();
		int minute = localDate.getMinute();
		int second = localDate.getSecond();

		YearMonth yearMonthObject = YearMonth.of(year, month);
		int daysInMonth = yearMonthObject.lengthOfMonth(); // 28

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String currentDate = dateFormat.format(date);

		String months = "";
		if (month == 1 || month == 2 || month == 3 || month == 4 || month == 5 || month == 6 || month == 7 || month == 8
				|| month == 9) {
			months = "0" + month;
		} else {
			months = String.valueOf(month);
		}

		String startDate = year + "-" + months + "-0" + 1 + " 00:00:01";
		String endDate = year + "-" + months + "-" + daysInMonth + " " + hour + ":" + minute + ":" + second;
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = dateFormat.parse(currentDate);
			date2 = dateFormat.parse(endDate);
		} catch (ParseException e) {

		}
		if (date1.before(date2)) {
			endDate = currentDate;
		}

		dates.add(startDate);
		dates.add(endDate);

		return dates;
	}

	public static List<String> getDateFormat(String input) {

		List<String> finalOutput = new ArrayList<String>();
		if (input.equalsIgnoreCase("today")) {

			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd 00:00:01");
			Date date1 = new Date();

			finalOutput.add(dateFormat1.format(date1));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();

			finalOutput.add(dateFormat.format(date));

		} else if (input.equalsIgnoreCase("week")) {

			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			SimpleDateFormat dateFormatStart = new SimpleDateFormat("yyyy-MM-dd 00:00:01");
			String firstDayOfWeek = dateFormatStart.format(c.getTime());

			finalOutput.add(firstDayOfWeek);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			finalOutput.add(dateFormat.format(date));

		} else if (input.equalsIgnoreCase("month")) {

			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = 1;
			c.set(year, month, day);

			SimpleDateFormat dateFormatStart = new SimpleDateFormat("yyyy-MM-dd 00:00:01");
			String firstDayOfMonth = dateFormatStart.format(c.getTime());

			finalOutput.add(firstDayOfMonth);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			finalOutput.add(dateFormat.format(date));

		}

		return finalOutput;

	}

	@SuppressWarnings("deprecation")
	public static Date getLastDate() {

		try {
			Date currentDate = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(currentDate);
			if ((currentDate.getMonth() + 1) <= 6) {
				int month = 6 - (currentDate.getMonth() + 1);
				int date = 30 - currentDate.getDate();
				c.add(Calendar.MONTH, month);
				c.add(Calendar.DATE, date);
				Date currentDatePlusOne = c.getTime();
				return changeTime(currentDatePlusOne);
			} else {
				int month = 12 - (currentDate.getMonth() + 1);
				int date = 31 - currentDate.getDate();
				c.add(Calendar.MONTH, month);
				c.add(Calendar.DATE, date);
				Date currentDatePlusOne = c.getTime();
				return changeTime(currentDatePlusOne);
			}
		} catch (Exception e) {
		}
		return null;

	}

	public static boolean compareDates(String d1) {
		boolean flag = false;
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = sdf.parse(d1);
			Date date2 = sdf.parse(getCurrentDateTime());

			if (date1.before(date2)) {
				flag = true;
			}

		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		return flag;
	}

	public static Double roundOff(Double amount) {

		String value = String.valueOf(amount);
		String[] expArr = value.split("\\.");
		Double amt = 0.0;
		if (expArr != null && expArr.length > 0) {
			String exp = expArr[1];
			int counterFlag = 0;

			if (!exp.equalsIgnoreCase("") || (exp != null)) {
				for (int i = 0; i < exp.length(); i++) {

					if (exp.charAt(i) == '9') {
						counterFlag++;
						if (counterFlag == 3) {
							amt = Double.valueOf(Math.round(amount));
							break;
						}
					}
				}
			}
		}
		if (amt == 0) {
			amt = amount;
		}

		return amt;
	}

	public static boolean checkAlphabet(String text) {

		return text.matches("^[^0-9][A-Z a-z]+$");
	}

	public static String authGenerator(String credentials) {
		try {
			byte[] encodedBytes = Base64.getEncoder().encode(credentials.getBytes());
			credentials = new String(encodedBytes);
			credentials = "Basic " + credentials;
		} catch (Exception e) {

			return null;
		}
		return credentials;
	}

	public static HttpHeaders createHttpHeaders(String credentials) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", credentials);

		return headers;
	}

	public static int getDateDiff1(String firstDate) {

		try {

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = format.parse(firstDate);
			String currentDate = format.format(new Date());
			Date date2 = format.parse(currentDate);
			Double diffInMillies = (date2.getTime() - date1.getTime()) / (1000.0 * 60 * 60 * 24);
			return diffInMillies.intValue();

		} catch (ParseException e) {

			return 0;
		}
	}

	public static Object transform(ModelMapper modelMapper, Object input, Object output) {

		if(Objects.nonNull(input))
			return modelMapper.map(input, output.getClass());
		else
			return null;
	}
	
	public static void printLog(Object model, String type, String apiName, HttpServletRequest request) {
		
		try {
			
			if (logger.isInfoEnabled()) {
				logger.info(type+" -- " + apiName +  " -- " + request.getRemoteAddr() + " -- " 
				+ new ObjectMapper().writeValueAsString(model) );
			}
			System.out.println(type+" -- " + apiName +  " -- " + request.getRemoteAddr() + " -- " 
					+ new ObjectMapper().writeValueAsString(model) );
			
		} catch (Exception e) {
		}
	}
	
	public static boolean checkEmail(String emailId) {
		
		String emailPattern = "(^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$)";
		Pattern pattern = Pattern.compile(emailPattern);
		Matcher matcher = pattern.matcher(emailId);
		return matcher.matches();
	}
	
	public static int generateOTP() {
		
		return (int)(Math.random() *  9000) + 1000;
	}
	
	public static String errorToString(Exception e) {
		
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

	public static void main(String[] args) {

		try {
			System.out.println(checkAlphabet("Sudeep Chhetri"));
		} catch (Exception e) {

		}
	}
}