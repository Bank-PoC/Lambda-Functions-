package com.spring.aws.service;

import java.util.List;

import com.spring.aws.domain.ChildIsoMessage;
import com.spring.aws.dto.ResponseId;

public interface ChildIsoMsgService {
	
	ChildIsoMessage saveChildMsg(ChildIsoMessage childMsg);
	
	ResponseId getTramaPadreEncode(String isoMsg,String idParent);
	
	List<ChildIsoMessage> getTramaChildById(String entityId);
	

}
