package com.orastays.testimonialserver.validation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.orastays.testimonialserver.dao.QuickLinksDAO;
import com.orastays.testimonialserver.dao.TestimonialDAO;
import com.orastays.testimonialserver.exceptions.FormExceptions;
import com.orastays.testimonialserver.helper.MessageUtil;
import com.orastays.testimonialserver.model.CommonModel;
import com.orastays.testimonialserver.model.ResponseModel;
import com.orastays.testimonialserver.model.UserModel;
import com.orastays.testimonialserver.service.QuickLinksService;
import com.orastays.testimonialserver.service.TestimonialService;


@Component
public class AuthorizeUserValidation {

	private static final Logger logger = LogManager.getLogger(AuthorizeUserValidation.class);
	
	@Value("${entitymanager.packagesToScan}")
	protected String entitymanagerPackagesToScan;
	
	@Autowired
	protected MessageUtil messageUtil;
	
	@Autowired
	protected RestTemplate restTemplate;
	
	@Autowired
	protected HttpServletRequest request;
	
	@Autowired
	protected TestimonialService testimonialService; 
	
	@Autowired
	protected QuickLinksService quickLinksService; 
	
	@Autowired
	protected TestimonialDAO testimonialDAO;
	
	@Autowired
	protected QuickLinksDAO quickLinksDAO;
	
	public UserModel getUserDetails(String userToken) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("getUserDetails -- START");
		}
		
		Map<String, Exception> exceptions = new LinkedHashMap<>();
		UserModel userModel = null;
		try {
			
			// Validate User Token
			if(StringUtils.isBlank(userToken)) {
				exceptions.put(messageUtil.getBundle("user.token.null.code"), new Exception(messageUtil.getBundle("user.token.null.message")));
			} else {
				try{
					//ResponseModel responseModel = restTemplate.getForObject("http://localhost:7080/api/check-token?userToken="+userToken, ResponseModel.class);
					ResponseModel responseModel = restTemplate.getForObject(messageUtil.getBundle("auth.server.url") +"check-token?userToken="+userToken, ResponseModel.class);
					Gson gson = new Gson();
					String jsonString = gson.toJson(responseModel.getResponseBody());
					userModel = gson.fromJson(jsonString, UserModel.class);
					if(Objects.isNull(userModel)) {
						exceptions.put(messageUtil.getBundle("session.expires.code"), new Exception(messageUtil.getBundle("session.expires.message")));
					}
				}catch(HttpClientErrorException e) {
					exceptions.put(messageUtil.getBundle("session.expires.code"), new Exception(messageUtil.getBundle("session.expires.message")));
				}
				
				if (logger.isInfoEnabled()) {
					logger.info("userModel ==>> "+userModel);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			// Disabled the below line to pass the Token Validation
			exceptions.put(messageUtil.getBundle("session.expires.code"), new Exception(messageUtil.getBundle("session.expires.message")));
		}
		
		if (exceptions.size() > 0)
			throw new FormExceptions(exceptions);
		
		if (logger.isInfoEnabled()) {
			logger.info("getUserDetails -- END");
		}
		
		return userModel;
	}
	
	public CommonModel validateLanguage(String languageId) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("validateLanguage -- START");
		}
		
		Map<String, Exception> exceptions = new LinkedHashMap<>();
		CommonModel commonModel = null;
		try {
			
			if(StringUtils.isBlank(languageId)) {
				exceptions.put(messageUtil.getBundle("language.id.null.code"), new Exception(messageUtil.getBundle("language.id.null.message")));
			} else {
				try{
				//ResponseModel responseModel = restTemplate.getForObject("http://localhost:7080/api/check-language?languageId="+languageId, ResponseModel.class);
				ResponseModel responseModel = restTemplate.getForObject(messageUtil.getBundle("auth.server.url") +"check-language?languageId="+languageId, ResponseModel.class);
				Gson gson = new Gson();
				String jsonString = gson.toJson(responseModel.getResponseBody());
				commonModel = gson.fromJson(jsonString, CommonModel.class);
				if(Objects.isNull(commonModel)) {
					exceptions.put(messageUtil.getBundle("language.id.invalid.code"), new Exception(messageUtil.getBundle("language.id.invalid.message")));
				}
				} catch(HttpClientErrorException e) {
					exceptions.put(messageUtil.getBundle("language.id.invalid.code"), new Exception(messageUtil.getBundle("language.id.invalid.message")));
				}
				
				if (logger.isInfoEnabled()) {
					logger.info("commonModel ==>> "+commonModel);
				}
			}
			
		} catch (Exception e) {
			// Disabled the below line to pass the Language Validation
			exceptions.put(messageUtil.getBundle("language.id.invalid.code"), new Exception(messageUtil.getBundle("language.id.invalid.message")));
		}
		
		if (exceptions.size() > 0)
			throw new FormExceptions(exceptions);
		
		if (logger.isInfoEnabled()) {
			logger.info("validateLanguage -- END");
		}
		
		return commonModel;
	}
}