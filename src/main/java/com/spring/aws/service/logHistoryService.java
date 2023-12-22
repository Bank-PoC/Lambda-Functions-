package com.spring.aws.service;

import java.util.List;

import com.spring.aws.domain.LogHistory;
import com.spring.aws.dto.IsoMsgDTO;

public interface logHistoryService {

	/**
	 * Method that get all Logs by filters:financial Entity, Message Type, Product Origin, Transanction Type.
	 * @param financialEntity
	 * @param typeMessage
	 * @param productOrigin
	 * @param typeTransaction
	 * @return
	 */
	List<LogHistory> getLogsByFilters(String financialEntity,String typeMessage,String productOrigin, String typeTransaction);
	
	/**
	 * Method that save a Log History
	 * @param isoMsg
	 * @return
	 */
	
	IsoMsgDTO saveLogHistory(String entityIdSentParent, String entityIdSentChild);
	
	Object GetLogHistoryById(String entityId);

}
