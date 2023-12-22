package com.spring.aws.service.ServiceImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.aws.domain.ChildIsoMessage;
import com.spring.aws.domain.IsoMessage;
import com.spring.aws.dto.ResponseId;
import com.spring.aws.exception.IsoMessageBadRequest;
import com.spring.aws.exception.IsoMessageNotFound;
import com.spring.aws.repository.ChildIsoMessageRepository;
import com.spring.aws.service.ChildIsoMsgService;
import com.spring.aws.service.IsoMessageService;


@Service
public class ChildIsoMsgServiceImpl implements ChildIsoMsgService{
	
	@Autowired
	private ChildIsoMessageRepository childRepository;
	
	@Autowired
	private IsoMessageService isoMsgService;
	

	@Override
	public ChildIsoMessage saveChildMsg(ChildIsoMessage childMsg) {
		
		return childRepository.save(childMsg);
	}


	@Override
	public ResponseId getTramaPadreEncode(String isoDecode, String idParent) {
		try {
			ResponseEntity<String> encodeMsg = isoMsgService.sendEncodeMSj(isoDecode);
			String jsonString = encodeMsg.getBody();
			ObjectMapper mapper = new ObjectMapper();
			String mensajeISO = mapper.readTree(jsonString).get("isoMessage").textValue();
			LocalDateTime fechaHoraActual = LocalDateTime.now(ZoneId.of("America/Bogota"));
			ResponseId reponseId=new ResponseId();
			
			List<IsoMessage> resp1 = isoMsgService.getTramaById(idParent);
			for(IsoMessage obj: resp1) {
				ChildIsoMessage objChild = new ChildIsoMessage();
				
				objChild.setFinancialEntity(obj.getFinancialEntity());
				objChild.setTypeTransaction(obj.getTypeTransaction());
				objChild.setIdParentISOMsg(obj.getEntityId());
				objChild.setIsoChildMessage(mensajeISO);
				objChild.setDateRegisterMessage(fechaHoraActual.toString());
				objChild.setProductOrigin(obj.getProductOrigin());
				objChild.setTypeMessage(obj.getTypeMessage());
				
				childRepository.save(objChild);
				reponseId.setIdChild(objChild.getEntityId());
				reponseId.setIdParent(objChild.getIdParentISOMsg());
				reponseId.setHttpStatus(HttpStatus.CREATED.value());
				reponseId.setMessage("El mensaje ISO8583 ha sido registrado con exito");
				return reponseId;
				
			}
		}catch (Exception e) {
			throw new IsoMessageBadRequest(e.getMessage());
		}
		return null;
	}
	
	@Override
	public List<ChildIsoMessage> getTramaChildById(String entityId) {
		List<ChildIsoMessage> resp = childRepository.GetTramaChildById(entityId);

		if (!resp.isEmpty()) {
			return childRepository.GetTramaChildById(entityId);
		} else {
			throw new IsoMessageNotFound("Id Trama IsoMessage NotFound");
		}
	}


}
