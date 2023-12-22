package com.spring.aws.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.spring.aws.domain.IsoMessage;
import com.spring.aws.domain.Master;

public interface IsoMessageService {
	
	IsoMessage saveTrama(IsoMessage isoMsg);
	
	List<IsoMessage> getTramaById(String entityId);
	
	List<IsoMessage> getTramaByAtt(String financialEntity,String typeMessage, String productOrigin,String typeTransaction);
	
	void deleteTrama(String entityId);
	//serviceAPItransversalRestemplate
	ResponseEntity<String> sendDecodeMSj(String respEncode);
	ResponseEntity<String> sendEncodeMSj(String respDecode);
	ResponseEntity<String> sendDecodeMSj2(String respEncode);

	List<Master> getMaster(String typeMaster, String masterCode);

}
