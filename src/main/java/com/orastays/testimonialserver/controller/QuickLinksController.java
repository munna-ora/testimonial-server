package com.orastays.testimonialserver.controller;

import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orastays.testimonialserver.exceptions.FormExceptions;
import com.orastays.testimonialserver.helper.TestimonialConstant;
import com.orastays.testimonialserver.helper.Util;
import com.orastays.testimonialserver.model.QuickLinksModel;
import com.orastays.testimonialserver.model.ResponseModel;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
@Api(value = "QuickLinks", tags = "QuickLinks")
public class QuickLinksController extends BaseController {
	
private static final Logger logger = LogManager.getLogger(QuickLinksController.class);
	
	@PostMapping(value = "/add-quickLinks", produces = "application/json")
	@ApiOperation(value = "Add QuickLinks", response = ResponseModel.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Please Try after Sometime!!!"),
			@ApiResponse(code = 1602, message = "Please provide quicklinks title!!"),
			@ApiResponse(code = 1603, message = "Please provide quicklinks url!!") })
	public ResponseEntity<ResponseModel> addQuickLinks(@RequestBody QuickLinksModel quickLinksModel) {
	
		if (logger.isInfoEnabled()) {
			logger.info("addQuickLinks -- START");
		}

		ResponseModel responseModel = new ResponseModel();
		Util.printLog(quickLinksModel, TestimonialConstant.INCOMING, "Add QuickLinks", request);
		try {
			quicklinksService.addQuickLinks(quickLinksModel);
			responseModel.setResponseBody(messageUtil.getBundle("quickLinks.add.success"));
			responseModel.setResponseCode(messageUtil.getBundle(TestimonialConstant.COMMON_SUCCESS_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(TestimonialConstant.COMMON_SUCCESS_MESSAGE));
		}	 catch (FormExceptions fe) {

			for (Entry<String, Exception> entry : fe.getExceptions().entrySet()) {
				responseModel.setResponseCode(entry.getKey());
				responseModel.setResponseMessage(entry.getValue().getMessage());
				if (logger.isInfoEnabled()) {
					logger.info("FormExceptions in Add QuickLinks -- "+Util.errorToString(fe));
				}
				break;
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in Add QuickLinks -- "+Util.errorToString(e));
			}
			responseModel.setResponseCode(messageUtil.getBundle(TestimonialConstant.COMMON_ERROR_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(TestimonialConstant.COMMON_ERROR_MESSAGE));
		}
		
		Util.printLog(responseModel, TestimonialConstant.OUTGOING, "Add QuickLinks", request);

		if (logger.isInfoEnabled()) {
			logger.info("addQuickLinks -- END");
		}
		
		if (responseModel.getResponseCode().equals(messageUtil.getBundle(TestimonialConstant.COMMON_SUCCESS_CODE))) {
			return new ResponseEntity<>(responseModel, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(value = "/fetch-quickLinks", produces = "application/json")
	@ApiOperation(value = "Fetch QuickLinks", response = ResponseModel.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Please Try after Sometime!!!") })
	public ResponseEntity<ResponseModel> fetchQuickLinks() {
	
		if (logger.isInfoEnabled()) {
			logger.info("fetchQuickLinks -- START");
		}

		ResponseModel responseModel = new ResponseModel();
		Util.printLog(null, TestimonialConstant.INCOMING, "Fetch QuickLinks", request);
		try {
			List<QuickLinksModel> userQuickLinksModels = quicklinksService.fetchQuickLinks();
			responseModel.setResponseBody(userQuickLinksModels);
			responseModel.setResponseCode(messageUtil.getBundle(TestimonialConstant.COMMON_SUCCESS_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(TestimonialConstant.COMMON_SUCCESS_MESSAGE));
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in Fetch QuickLinks -- "+Util.errorToString(e));
			}
			responseModel.setResponseCode(messageUtil.getBundle(TestimonialConstant.COMMON_ERROR_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(TestimonialConstant.COMMON_ERROR_MESSAGE));
		}
		
		Util.printLog(responseModel, TestimonialConstant.OUTGOING, "Fetch QuickLinks", request);

		if (logger.isInfoEnabled()) {
			logger.info("fetchQuickLinks -- END");
		}
		
		if (responseModel.getResponseCode().equals(messageUtil.getBundle(TestimonialConstant.COMMON_SUCCESS_CODE))) {
			return new ResponseEntity<>(responseModel, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
		}
	}
	
	
}
