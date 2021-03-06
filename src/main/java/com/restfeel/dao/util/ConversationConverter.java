package com.restfeel.dao.util;

import java.util.ArrayList;
import java.util.List;

import com.restfeel.dto.AssertionDTO;
import com.restfeel.dto.BodyAssertDTO;
import com.restfeel.dto.ConversationDTO;
import com.restfeel.dto.FormDataDTO;
import com.restfeel.dto.RfHeaderDTO;
import com.restfeel.dto.RfRequestDTO;
import com.restfeel.dto.RfResponseDTO;
import com.restfeel.dto.UrlParamDTO;
import com.restfeel.entity.Assertion;
import com.restfeel.entity.BasicAuth;
import com.restfeel.entity.BodyAssert;
import com.restfeel.entity.Conversation;
import com.restfeel.entity.DigestAuth;
import com.restfeel.entity.FormParam;
import com.restfeel.entity.RfHeader;
import com.restfeel.entity.RfRequest;
import com.restfeel.entity.RfResponse;
import com.restfeel.entity.UrlParam;

//TODO : Need to use Spring Object Mapping : http://docs.spring.io/spring/previews/mapping.html
public class ConversationConverter {

    private ConversationConverter() {}

    public static Conversation convertToEntity(RfRequestDTO rfRequestDTO, RfResponseDTO responseDTO) {
        Conversation conversation = new Conversation();

        RfRequest rfRequest = new RfRequest();
        if (rfRequestDTO != null) {
            conversation.setWorkspaceId(rfRequestDTO.getWorkspaceId());
            rfRequest.setApiUrl(rfRequestDTO.getApiUrl());
            //Futuristic approach: evaluatedApiUrl not being used on UI currently.
            rfRequest.setEvaluatedApiUrl(rfRequestDTO.getEvaluatedApiUrl());
            rfRequest.setMethodType(rfRequestDTO.getMethodType());

            List<FormDataDTO> formDataDTOs = rfRequestDTO.getFormParams();
            List<FormParam> formParams = new ArrayList<FormParam>();

            if (rfRequestDTO.getBasicAuthDTO() != null) {
                BasicAuth basicAuth = new BasicAuth();
                basicAuth.setUsername(rfRequestDTO.getBasicAuthDTO().getUsername());
                basicAuth.setPassword(rfRequestDTO.getBasicAuthDTO().getPassword());
                rfRequest.setBasicAuth(basicAuth);
            }

            if (rfRequestDTO.getDigestAuthDTO() != null) {
                DigestAuth digestAuth = new DigestAuth();
                digestAuth.setUsername(rfRequestDTO.getDigestAuthDTO().getUsername());
                digestAuth.setPassword(rfRequestDTO.getDigestAuthDTO().getPassword());
                rfRequest.setDigestAuth(digestAuth);
            }

            if (rfRequestDTO.getApiBody() != null) {
                rfRequest.setApiBody(rfRequestDTO.getApiBody());
            } else if (formDataDTOs != null && !formDataDTOs.isEmpty()) {
                FormParam formParam;
                for (FormDataDTO formDataDTO : formDataDTOs) {
                    formParam = new FormParam();
                    formParam.setParamKey(formDataDTO.getKey());
                    formParam.setParamValue(formDataDTO.getValue());
                    formParams.add(formParam);
                }
                rfRequest.setFormParams(formParams);
            }

            List<UrlParamDTO> urlParamDTOs = rfRequestDTO.getUrlParams();
            List<UrlParam> urlParams = new ArrayList<UrlParam>();

            if (urlParamDTOs != null && !urlParamDTOs.isEmpty()) {
                UrlParam urlParam;
                for (UrlParamDTO urlParamDTO : urlParamDTOs) {
                    urlParam = new UrlParam();
                    urlParam.setParamKey(urlParamDTO.getKey());
                    urlParam.setParamValue(urlParamDTO.getValue());
                    urlParams.add(urlParam);
                }
                rfRequest.setUrlParams(urlParams);
            }

            List<RfHeaderDTO> headerDTOs = rfRequestDTO.getHeaders();
            List<RfHeader> headers = new ArrayList<RfHeader>();
            RfHeader header;
            if (headerDTOs != null && !headerDTOs.isEmpty()) {
                for (RfHeaderDTO rfHeaderDTO : headerDTOs) {
                    header = new RfHeader();
                    header.setHeaderName(rfHeaderDTO.getHeaderName());
                    header.setHeaderValue(rfHeaderDTO.getHeaderValue());
                    headers.add(header);
                }
                rfRequest.setRfHeaders(headers);
            }
        }
        conversation.setRfRequest(rfRequest);

        // TODO : We should have the option to configure whether to save response or not.
        RfResponse response = new RfResponse();
        conversation.setRfResponse(response);

        if (responseDTO == null && !rfRequestDTO.getApiUrl().isEmpty()) {
            response.setBody("Could not connect to " + rfRequestDTO.getApiUrl());
        } else {
            if (responseDTO != null && responseDTO.getBody() != null && !responseDTO.getBody().isEmpty()) {
                response.setBody(responseDTO.getBody());
            }
            AssertionDTO assertionDTO = rfRequestDTO != null ? rfRequestDTO.getAssertionDTO() : null;
            if (assertionDTO != null && assertionDTO.getBodyAssertDTOs() != null) {
                List<BodyAssertDTO> bodyAssertDTOs = assertionDTO.getBodyAssertDTOs();
                List<BodyAssert> bodyAsserts = new ArrayList<BodyAssert>();
                for (BodyAssertDTO bodyAssertDTO : bodyAssertDTOs) {
                    BodyAssert bodyAssert = new BodyAssert();
                    bodyAssert.setComparator(bodyAssertDTO.getComparator());
                    bodyAssert.setExpectedValue(bodyAssertDTO.getExpectedValue());
                    bodyAssert.setPropertyName(bodyAssertDTO.getPropertyName());
                    bodyAssert.setActualValue(bodyAssertDTO.getActualValue());
                    bodyAssert.setSuccess(bodyAssertDTO.isSuccess());
                    bodyAsserts.add(bodyAssert);
                }
                Assertion assertion = new Assertion();
                response.setAssertion(assertion);
            }

            List<RfHeaderDTO> headerDTOs = responseDTO != null ? responseDTO.getHeaders() : null;
            List<RfHeader> headers = new ArrayList<RfHeader>();
            RfHeader header;
            if (headerDTOs != null && !headerDTOs.isEmpty()) {
                for (RfHeaderDTO rfHeaderDTO : headerDTOs) {
                    header = new RfHeader();
                    header.setHeaderName(rfHeaderDTO.getHeaderName());
                    header.setHeaderValue(rfHeaderDTO.getHeaderValue());
                    headers.add(header);
                }
                response.setRfHeaders(headers);
            }
        }

        return conversation;

    }

    public static ConversationDTO convertToDTO(Conversation item) {
        ConversationDTO itemDTO = new ConversationDTO();

        RfRequestDTO rfRequestDTO = new RfRequestDTO();
        RfRequest rfRequest = item.getRfRequest();

        rfRequestDTO.setApiBody(rfRequest.getApiBody());

        rfRequestDTO.setApiUrl(rfRequest.getApiUrl());
        rfRequestDTO.setEvaluatedApiUrl(rfRequest.getEvaluatedApiUrl());
        rfRequestDTO.setMethodType(rfRequest.getMethodType());

        return itemDTO;
    }

}
