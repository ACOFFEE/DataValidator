package com.cf.validator;

import com.cf.validator.annotation.ValidateClassFlag;
import com.cf.validator.annotation.ValidateField;
import com.cf.validator.annotation.Validator;
import com.cf.validator.model.Message;

@ValidateClassFlag(true)
public class App {
	@ValidateField(isNull = false, format = Validator.EMAIL, prefix = "邮箱", maxLength = "80")
	private String email;

	@ValidateField(isNull = false, min = "0", max = "180")
	private int age = 25;

	@ValidateField(isNull = false, prefix = "体重")
	private Integer weight;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static void main(String[] args) {
		App app = new App();
		app.setEmail("32432341@qq.com");
		try {
			ValidateEngine validateEngine = new ValidateEngine();
			Message message = validateEngine.doValidate(app);
			if (message.isError()) {
				System.out.println(message.getMessage());
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
