package com.spring.aws.service.ServiceImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.spring.aws.config.ConfigRestTemplate;
import com.spring.aws.domain.AuthIsoMsg;
import com.spring.aws.domain.ChildIsoMessage;
import com.spring.aws.domain.IsoMessage;
import com.spring.aws.domain.LogHistory;
import com.spring.aws.domain.LogHistory.logChildEntry;
import com.spring.aws.domain.LogHistory.logParentEntry;
import com.spring.aws.dto.IsoMsgDTO;
import com.spring.aws.exception.IsoMessageBadRequest;
import com.spring.aws.repository.ChildIsoMessageRepository;
import com.spring.aws.repository.IsoMessageRepository;
import com.spring.aws.repository.logsHistoryRepository;
import com.spring.aws.service.IsoMessageService;
import com.spring.aws.service.logHistoryService;

@Service
public class logHistoryImpl implements logHistoryService{


	@Autowired
	private logsHistoryRepository logRepository;
	
	@Autowired
	private ConfigRestTemplate config;
	
	@Autowired
	private ChildIsoMessageRepository childRepository;
	
	@Autowired
	private IsoMessageRepository parentRepository;
	
	@Autowired
	private IsoMessageService isoMsgService;

	@Autowired
	private IsoMessageService iso; 
	
	Map<String,String> typeMsgAns=Map.of("00","Transacción Aprobada","55","Transacción Declianda por PIN Errado","51","Transacción Declinada por Fondos Insuficientes","54","Transacción declinada por tarjeta expirada","12","Transacción no permitida","62","Tarjeta Bloqueada");               

	public JSONObject decodeMsg(String msgToDecode) {
		ResponseEntity<String> responseEntity  = iso.sendDecodeMSj2("{\"isoMessage\":\""+msgToDecode+"\"}");
		String messageDecoded=responseEntity.getBody();
		JSONObject jsonMessage=new JSONObject(messageDecoded);	
		return jsonMessage;
	}
	
	public String encodeMsg(JSONObject msgToEncode) {
		ResponseEntity<String> responseEntity  = iso.sendEncodeMSj(msgToEncode.toString());
		String messageEncoded=responseEntity.getBody();
		return messageEncoded.substring(16, messageEncoded.length()-2);
	}
	
	@Override
	public List<LogHistory> getLogsByFilters(String financialEntity, String typeMessage, String productOrigin,
			String typeTransaction) {
		return logRepository.getDataByFilters(financialEntity, typeMessage, productOrigin, typeTransaction);
	}

	@Override
	public IsoMsgDTO saveLogHistory(String entityIdSentParent, String entityIdSentChild) {
		
		IsoMsgDTO responseAuth=new IsoMsgDTO();
		String ansAuth;
		try {
			//1. Creamos el objeto LogHistory para ir setteando los datos que tennemos.
			LogHistory objLogHistory= new LogHistory();
			LocalDateTime dateAnsMsg = LocalDateTime.now(ZoneId.of("America/Bogota")); 
			
			//Consultar tramaPadreXId
			List<IsoMessage> responseParentSent=parentRepository.GetTramaById(entityIdSentParent);
			IsoMessage parentSent=responseParentSent.get(0);
			
			//Consultar tramaHijoXId
			List<ChildIsoMessage> responseChildSent=childRepository.GetTramaChildById(entityIdSentChild);
			ChildIsoMessage childSent=responseChildSent.get(0);
			
			//Generar respuesta de autorizador con el Padre (Acá tengo que pasar trama codificada--> Un String normal)
			JSONObject jsonObjectParent=decodeMsg(parentSent.getIsoParentMessage()); 
			AuthIsoMsg parentAns=new AuthIsoMsg(jsonObjectParent);
			String ansParent=encodeMsg(parentAns.getJsonObjectAns());
			
			//Generar respuesta de autorizador con el Hijo(Acá tengo que pasar trama codificada-->Un String normal)
			JSONObject jsonObjectChild=decodeMsg(childSent.getIsoChildMessage()); 
			AuthIsoMsg childAns=new AuthIsoMsg(jsonObjectChild);
			String ansChild=encodeMsg(childAns.getJsonObjectAns());

			//Setear la tabla logHistory con los datos recibidos por el padre
			String dateSentParentMsg=parentSent.getDateRegisterMessage();
			String dateAnsParentMsg=dateAnsMsg.toString();
			String entryParentMsg=parentSent.getIsoParentMessage();
			String ansParentMsg=ansParent;
			objLogHistory.setLogParentEntry(new logParentEntry(dateSentParentMsg,dateAnsParentMsg,entryParentMsg,ansParentMsg));
			
			//Setear la tabla logHistory con los datos recibidos por el hijo
			String dateSentChildMsg=childSent.getDateRegisterMessage();
			String dateAnsChildMsg=dateAnsMsg.toString();
			String entryChildMsg=childSent.getIsoChildMessage();
			String ansChildMsg=ansChild;
			objLogHistory.setLogChildEntry(new logChildEntry(dateSentChildMsg,dateAnsChildMsg,entryChildMsg,ansChildMsg));
			
			//Terminar de settear los campos
			objLogHistory.setFinancialEntity(parentSent.getFinancialEntity());
			objLogHistory.setProductOrigin(parentSent.getProductOrigin());
			objLogHistory.setTypeMessage(parentSent.getTypeMessage());
			//objLogHistory.setTypeTransaction(parentSent.getTypeTransaction());
			objLogHistory.setTypeTransaction(parentSent.getTypeTransaction());
			
			if (childAns.getJsonObjectAns().has("39")) { 
				ansAuth=typeMsgAns.get(childAns.getJsonObjectAns().getString("39"));
			}else {
				ansAuth="Login exitoso";
			}

			//Guardar finalmente el LogHistory completo con todos los datos.
			logRepository.save(objLogHistory);
			responseAuth.setStatus(HttpStatus.CREATED.value());
			responseAuth.setMessage(ansAuth+" : "+ansChildMsg);
			
		}catch(Exception e) {
			throw new IsoMessageBadRequest(e.getMessage());
		}
		return responseAuth;
	}
	
	@Override
	public Object GetLogHistoryById(String entityId) {
		// TODO Auto-generated method stub
		ObjectMapper objectMapper = new ObjectMapper();
		List<LogHistory> listLog = logRepository.GetLogHistoryById(entityId);
		for (LogHistory obj : listLog) {

			String RequestParent = obj.getLogParentEntry().getEntryParentMsg();
			String ResponseParent = obj.getLogParentEntry().getAnsParentMsg();
			String RequestChild = obj.getLogChildEntry().getEntryChildMsg();
			String ResponseChild = obj.getLogChildEntry().getAnsChildMsg();

			//fechas
            String ParentRQDate = obj.getLogParentEntry().getDateAnsParentMsg();
            String ParentRSDate = obj.getLogParentEntry().getDateAnsParentMsg();

            String ChildRQDate = obj.getLogChildEntry().getDateSentChildMsg();
            String ChildRSDate = obj.getLogChildEntry().getDateAnsChildMsg();
			
			ResponseEntity<String> RQparent = isoMsgService.sendDecodeMSj("{\"isoMessage\":\"" + RequestParent + "\"}");
			ResponseEntity<String> RSparent = isoMsgService.sendDecodeMSj("{\"isoMessage\":\"" + ResponseParent + "\"}");

			ResponseEntity<String> RQchild = isoMsgService.sendDecodeMSj("{\"isoMessage\":\"" + RequestChild + "\"}");
			ResponseEntity<String> RSchild = isoMsgService.sendDecodeMSj("{\"isoMessage\":\"" + ResponseChild + "\"}");

			// Crear el objeto JSON final
			ObjectNode mensaje = objectMapper.createObjectNode();

			mensaje.put("RQparentDate", ParentRQDate);
            mensaje.put("RSparentDate", ParentRSDate);
            mensaje.put("RQchildDate", ChildRQDate);
            mensaje.put("RSchildDate", ChildRSDate);
			
			// Verificar si rq1 es un objeto JSON válido
			JsonNode rq1Node = null;
			try {
				rq1Node = objectMapper.readTree(RQparent.getBody());
			} catch (JsonProcessingException e) {
				// Manejar la excepción en caso de error al analizar el JSON de rq1
			}

			// Si rq1 es un objeto JSON válido, agregarlo al mensaje
			if (rq1Node != null && rq1Node.isArray()) {
				mensaje.set("RQparent", rq1Node);
			}

			// Verificar si rq2 es un arreglo JSON válido
			JsonNode rq2Node = null;
			try {
				rq2Node = objectMapper.readTree(RSparent.getBody());
			} catch (JsonProcessingException e) {
				// Manejar la excepción en caso de error al analizar el JSON de rq2
			}

			// Si rq2 es un arreglo JSON válido, agregarlo al mensaje
			if (rq2Node != null && rq2Node.isArray()) {
				mensaje.set("RSparent", rq2Node);
			}
			
			JsonNode rq3Node = null;
			try {
				rq3Node = objectMapper.readTree(RQchild.getBody());
			} catch (JsonProcessingException e) {
				// Manejar la excepción en caso de error al analizar el JSON de rq1
			}

			// Si rq1 es un objeto JSON válido, agregarlo al mensaje
			if (rq3Node != null && rq3Node.isArray()) {
				mensaje.set("RQchild", rq3Node);
			}

			// Verificar si rq2 es un arreglo JSON válido
			JsonNode rq4Node = null;
			try {
				rq4Node = objectMapper.readTree(RSchild.getBody());
			} catch (JsonProcessingException e) {
				// Manejar la excepción en caso de error al analizar el JSON de rq2
			}

			// Si rq2 es un arreglo JSON válido, agregarlo al mensaje
			if (rq4Node != null && rq4Node.isArray()) {
				mensaje.set("RSchild", rq4Node);
			}

			// Convertir el objeto mensaje a JSON como cadena de texto
			String mensajeJSON = null;
			try {
				mensajeJSON = objectMapper.writeValueAsString(mensaje);
			} catch (JsonProcessingException e) {
				// Manejar la excepción en caso de que ocurra un error al convertir a JSON
			}
			return mensajeJSON;

		}
		return null;
	}


}
