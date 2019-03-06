package com.orastays.testimonialserver.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.orastays.testimonialserver.entity.TestimonialEntity;
import com.orastays.testimonialserver.exceptions.FormExceptions;
import com.orastays.testimonialserver.helper.MessageUtil;
import com.orastays.testimonialserver.helper.Status;
import com.orastays.testimonialserver.helper.Util;
import com.orastays.testimonialserver.model.ResponseModel;
import com.orastays.testimonialserver.model.TestimonialModel;
import com.orastays.testimonialserver.model.UserModel;
import com.orastays.testimonialserver.service.TestimonialService;


@Service
@Transactional
public class TestimonialServiceImpl extends BaseServiceImpl implements TestimonialService {
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	MessageUtil messageUtil;
	
	private static final Logger logger = LogManager.getLogger(TestimonialServiceImpl.class);

	@Override
	public void addTestimonial(TestimonialModel testimonialModel) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("addTestimonial -- START");
		}
		
		testimonialValidation.validateAddTestimonial(testimonialModel);
		TestimonialEntity testimonialEntity = testimonialConverter.modelToEntity(testimonialModel);
		testimonialDAO.save(testimonialEntity);		
				
		if (logger.isInfoEnabled()) {
			logger.info("addTestimonial -- END");
		}
	}

	@Override
	public List<TestimonialModel> fetchTestimonial() throws FormExceptions {
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchTestimonial -- START");
		}
		
		List<TestimonialModel> testimonialModels = null;
		try {
			Map<String, String> innerMap1 = new LinkedHashMap<>();
			innerMap1.put("status", String.valueOf(Status.ACTIVE.ordinal()));
	
			Map<String, Map<String, String>> outerMap1 = new LinkedHashMap<>();
			outerMap1.put("eq", innerMap1);
	
			Map<String, Map<String, Map<String, String>>> alliasMap = new LinkedHashMap<>();
			alliasMap.put(entitymanagerPackagesToScan+".TestimonialEntity", outerMap1);
			
			testimonialModels = testimonialConverter.entityListToModelList(testimonialDAO.fetchListBySubCiteria(alliasMap));
			for (TestimonialModel testimonialModel : testimonialModels) {
				
				testimonialModel.setUserModel(getUserInfo(testimonialModel.getUserId()));
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in fetchTestimonial -- "+Util.errorToString(e));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchTestimonial -- END");
		}
		
		return testimonialModels;
	}

	private UserModel getUserInfo(String userId) {
		
		if (logger.isInfoEnabled()) {
			logger.info("getUserInfo -- START");
		}
		
		Map<String, Exception> exceptions = new LinkedHashMap<>();
		UserModel userModel = null;
		try {
			ResponseModel responseModel = restTemplate.getForObject(messageUtil.getBundle("auth.server.url") +"fetch-user-by-id?userId="+userId, ResponseModel.class);
			Gson gson = new Gson();
			String jsonString = gson.toJson(responseModel.getResponseBody());
			userModel = gson.fromJson(jsonString, UserModel.class);
			
		} catch(Exception e) {
			exceptions.put(messageUtil.getBundle("session.expires.code"), new Exception(messageUtil.getBundle("session.expires.message")));
		}
		
		return userModel;
	}
}