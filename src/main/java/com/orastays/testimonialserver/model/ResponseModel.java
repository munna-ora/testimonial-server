/**
 * @author Abhideep
 */
package com.orastays.testimonialserver.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonInclude(Include.NON_NULL)
public class ResponseModel {

	@ApiModelProperty(notes = "Response Code", required = false)
	@JsonProperty("responseCode")
	private String responseCode;
	
	@ApiModelProperty(notes = "Response Message", required = false)
	@JsonProperty("responseMessage")
	private String responseMessage;
	
	@ApiModelProperty(notes = "Response", required = false)
	@JsonProperty("responseBody")
	private Object responseBody;

}
