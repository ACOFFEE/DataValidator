package com.cf.validator;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.cf.validator.annotation.ValidateClassFlag;
import com.cf.validator.annotation.ValidateField;
import com.cf.validator.annotation.Validator;
import com.cf.validator.model.Message;
import com.cf.validator.util.RegexUtil;

public class ValidateEngine {

	private String validatorFile = "validator";
	private boolean validatorLoad = false;

	private MessageEngine defaultMessageEngine = new MessageEngine();

	public void setValidatorFile(String validatorFile) {
		this.validatorFile = validatorFile;
	}

	private void loadValodatorMsg() {
		defaultMessageEngine.setEncoding("UTF-8");
		defaultMessageEngine.setProterties(validatorFile);
		defaultMessageEngine.setDebug(true);
		defaultMessageEngine.init();
		validatorLoad = true;
	}

	public Message doValidate(Object object) throws IllegalArgumentException,
			IllegalAccessException {
		if (!validatorLoad) {
			loadValodatorMsg();
		}
		if (null == object) {
			throw new NullPointerException();
		}
		Message result = new Message();
		Class<?> objClazz = object.getClass();
		ValidateClassFlag validateClassFlag = objClazz
				.getAnnotation(ValidateClassFlag.class);
		if (null != validateClassFlag && validateClassFlag.value()) {
			Field[] fields = objClazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				ValidateField validateField = fields[i]
						.getAnnotation(ValidateField.class);
				if (null != validateField) {
					Object value = fields[i].get(object);
					String msgPrefix = validateField.prefix();
					if (!validateField.isNull()) {
						nullValidator(result, value, msgPrefix);
						if (result.isError()) {
							return result;
						}
					}
					Validator validator = validateField.format();
					if (validator == Validator.EMAIL && value instanceof String) {
						emailValidator(result, value, msgPrefix);
						if (result.isError()) {
							return result;
						}
					}
					if (validator == Validator.NUMBER
							&& value instanceof String) {
						numberValidator(result, value, msgPrefix);
						if (result.isError()) {
							return result;
						}
					}
					minAndMaxValidator(result, value, msgPrefix, validateField);
					if (result.isError()) {
						return result;
					}
					textValidator(result, value, msgPrefix, validateField);
					if (result.isError()) {
						return result;
					}
				}
			}
		}
		return result;
	}

	private void numberValidator(Message message, Object object,
			String msgPrefix) {
		String strValue = object.toString().trim();
		if (!RegexUtil.isNumber(strValue)) {
			message.setError(true);
			message.setMessage(msgPrefix
					+ defaultMessageEngine.getValue("DefaultValidator.number"));
		}
	}

	private void textValidator(Message message, Object object,
			String msgPrefix, ValidateField validateField) {
		if (!(object instanceof String || object instanceof StringBuilder || object instanceof StringBuffer)) {
			return;
		}
		int textLength = object.toString().length();
		String minLen = validateField.minLength().trim();
		String maxLen = validateField.maxLength().trim();
		if (minLen.length() > 0) {
			try {
				int minLength = Integer.parseInt(minLen);
				if (textLength < minLength) {
					message.setError(true);
					message.setMessage(msgPrefix
							+ defaultMessageEngine
									.getValue("DefaultValidator.minLength")
							+ minLength);
					return;
				}
			} catch (NumberFormatException e) {
				message.setError(true);
				message.setMessage(msgPrefix
						+ defaultMessageEngine
								.getValue("DefaultValidator.minLengthNumber"));
				return;
			}
		}
		if (maxLen.length() > 0) {
			try {
				int maxLength = Integer.parseInt(maxLen);
				if (textLength > maxLength) {
					message.setError(true);
					message.setMessage(msgPrefix
							+ defaultMessageEngine
									.getValue("DefaultValidator.maxLength")
							+ maxLength);
					return;
				}
			} catch (NumberFormatException e) {
				message.setError(true);
				message.setMessage(msgPrefix
						+ defaultMessageEngine
								.getValue("DefaultValidator.maxLengthNumber"));
				return;
			}
		}
	}

	private void minAndMaxValidator(Message message, Object object,
			String msgPrefix, ValidateField validateField) {
		if (!(object instanceof Integer || object instanceof Double
				|| object instanceof Short || object instanceof Byte
				|| object instanceof Float || object instanceof Long
				|| object instanceof BigDecimal || object instanceof BigInteger)) {
			return;
		}
		String strMin = validateField.min().trim();
		String strMax = validateField.max().trim();
		if (strMin.length() > 0) {
			BigDecimal min = null;
			try {
				min = new BigDecimal(strMin);
			} catch (Exception e) {
				message.setError(true);
				message.setMessage(msgPrefix
						+ defaultMessageEngine
								.getValue("DefaultValidator.minNumber"));
				return;
			}
			BigDecimal bigValue = new BigDecimal(object.toString());
			if (bigValue.compareTo(min) == -1) {
				message.setError(true);
				message.setMessage(msgPrefix
						+ defaultMessageEngine.getValue("DefaultValidator.min")
						+ strMin);
				return;
			}
		}
		if (strMax.length() > 0) {
			BigDecimal max = null;
			try {
				max = new BigDecimal(strMax);
			} catch (Exception e) {
				message.setError(true);
				message.setMessage(msgPrefix
						+ defaultMessageEngine
								.getValue("DefaultValidator.maxNumber"));
				return;
			}

			BigDecimal bigValue = new BigDecimal(object.toString());
			if (bigValue.compareTo(max) == 1) {
				message.setError(true);
				message.setMessage(msgPrefix
						+ defaultMessageEngine.getValue("DefaultValidator.max")
						+ strMax);
				return;
			}
		}
	}

	private void nullValidator(Message message, Object object, String msgPrefix) {
		if (null == object) {
			message.setError(true);
			message.setMessage(msgPrefix
					+ defaultMessageEngine.getValue("DefaultValidator.null"));
		} else if (object instanceof String || object instanceof StringBuffer
				|| object instanceof StringBuilder) {// 如果是字符串，验证空字符串
			String strValue = object.toString().trim();
			if (strValue.length() == 0) {
				message.setError(true);
				message.setMessage(msgPrefix
						+ defaultMessageEngine
								.getValue("DefaultValidator.null"));
			}
		}
	}

	private void emailValidator(Message message, Object object, String msgPrefix) {
		String strValue = object.toString().trim();
		if (!RegexUtil.isEmail(strValue)) {
			message.setError(true);
			message.setMessage(msgPrefix
					+ defaultMessageEngine
							.getValue("DefaultValidator.emailFormat"));
		}
	}
}
