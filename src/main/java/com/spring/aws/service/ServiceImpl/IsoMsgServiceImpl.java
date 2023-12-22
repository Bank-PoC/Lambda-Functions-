package com.spring.aws.service.ServiceImpl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.spring.aws.repository.IsoMessageRepository;
import com.spring.aws.config.ConfigRestTemplate;
import com.spring.aws.domain.IsoMessage;
import com.spring.aws.domain.Master;
import com.spring.aws.exception.IsoMessageBadRequest;
import com.spring.aws.exception.IsoMessageConfict;
import com.spring.aws.exception.IsoMessageNotContent;
import com.spring.aws.exception.IsoMessageNotFound;
import com.spring.aws.service.IsoMessageService;

@Service
public class IsoMsgServiceImpl implements IsoMessageService{

	private final IsoMessageRepository repository;
	private ConfigRestTemplate configRestTemplate;
	private RestTemplate restTemplate;

	public IsoMsgServiceImpl(IsoMessageRepository repository,ConfigRestTemplate configRestTemplate,RestTemplate restTemplate) {
		this.repository = repository;
		this.configRestTemplate=configRestTemplate;
		this.restTemplate=restTemplate;
	}
	
	@Override
	public List<Master> getMaster(String typeMaster, String masterCode) {
		// TODO Auto-generated method stub
		return repository.getMaster(typeMaster, masterCode);
	}



		@Override
	    public IsoMessage saveTrama(IsoMessage isoMsg) throws IsoMessageConfict{
	        String entidad = isoMsg.getFinancialEntity();
	        if (repository.existeAtt(isoMsg.getIsoParentMessage(),entidad)) {
	            throw new IsoMessageConfict("La Trama IsoMessage Ya Existe En La Base De Datos Para La Entidad => "+entidad);
	        }

	        // TODO Auto-generated method stub
	        String mensaje = isoMsg.getIsoParentMessage();
	        IsoMessage ObjectMsj = new IsoMessage();
	        LocalDateTime fechaHoraActual = LocalDateTime.now(ZoneId.of("America/Bogota")); 
	        ObjectMsj.setDateRegisterMessage(fechaHoraActual.toString());

	        ObjectMsj.setFinancialEntity(isoMsg.getFinancialEntity());
	        String bit1 = mensaje.substring(16,17);

	        if (bit1.equals("8")  || bit1.equals("9") || bit1.equals("A") || bit1.equals("B") || bit1.equals("C") || bit1.equals("D") || bit1.equals("E") || bit1.equals("F")) {// Aquí puedes agregar la lógica que deseas ejecutar cuando se cumple la condición// Por ejemplo:// Realizar acciones específicas para los dígitos 8, 9, A, B, C, D, E o F en sistema binario
	            System.out.println("La ISO tiene bit Secundario");
	            ObjectMsj.setProductOrigin(mensaje.substring(3, 5));
	            ObjectMsj.setTypeMessage(mensaje.substring(12, 16));
	            ObjectMsj.setTypeTransaction(mensaje.substring(48, 54));
	            ObjectMsj.setAmountTransaction(mensaje.substring(54,66));
	            ObjectMsj.setIsoParentMessage(mensaje);

	        }else {
	            ObjectMsj.setProductOrigin(mensaje.substring(3, 5));
	            ObjectMsj.setTypeMessage(mensaje.substring(12, 16));
	            ObjectMsj.setTypeTransaction(mensaje.substring(32, 38));
	            ObjectMsj.setAmountTransaction(mensaje.substring(38,50));
	            ObjectMsj.setIsoParentMessage(mensaje);
	        }

	        sendDecodeMSj("{\"isoMessage\":\""+mensaje+"\"}");

	        return repository.save(ObjectMsj);

	 

	    }

	@Override
	public List<IsoMessage> getTramaById(String entityId) {
		// TODO Auto-generated method stub
		List<IsoMessage> resp = repository.GetTramaById(entityId);
		if (!resp.isEmpty()) {
			return repository.GetTramaById(entityId);
		} else {
			throw new IsoMessageNotFound("Id Trama IsoMessage No Encontrada");
		}

	}
	
	@Override
	public void deleteTrama(String entityId) {
		repository.deleteTrama(entityId);
	}

	@Override
	public List<IsoMessage> getTramaByAtt(String financialEntity, String typeMessage, String productOrigin,
			String typeTransaction) {
		IsoMessage getTT = new IsoMessage();
		getTT.setFinancialEntity(financialEntity);
		getTT.setTypeMessage(typeMessage);
		getTT.setProductOrigin(productOrigin);
		getTT.setTypeTransaction(typeTransaction);

		System.out.println(getTT.getTypeTransaction());

		// TODO Auto-generated method stub
		if (getTT.getFinancialEntity() != null && getTT.getTypeMessage() != null && getTT.getProductOrigin() != null
				&& getTT.getTypeTransaction() != null) {
			List<IsoMessage> resp = repository.GetTramaByAtt(financialEntity, typeMessage, productOrigin,
					typeTransaction);
			if (!resp.isEmpty()) {
				return repository.GetTramaByAtt(financialEntity, typeMessage, productOrigin, typeTransaction);
			} else {
				throw new IsoMessageNotContent("No se encontraron resultados");
			}
		} else {
			throw new IsoMessageBadRequest(
					"Los Campos: financialEntity, typeMessage, productOrigin y typeTransaction Son Requeridos Como QueryParams");
		}

	}

	
	//MetodosRestTemplate_ConsumoAPITrasnversal_EncodeDecode

		@Override
		public ResponseEntity<String> sendDecodeMSj(String respEncode) {
			// TODO Auto-generated method stub
			try {
				String urlDecode = configRestTemplate.getUrl() + "/decode";
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> requestEntity = new HttpEntity<>(respEncode, headers);

				ResponseEntity<String> responseEntity = restTemplate.postForEntity(urlDecode, requestEntity, String.class);
	
				return responseEntity;

			} catch (HttpClientErrorException | HttpServerErrorException e) {
				
				throw new IsoMessageBadRequest(e.getResponseBodyAsString());

			} catch (Exception e) {

				String responseError = e.getMessage();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);

			}
		}

		@Override
		public ResponseEntity<String> sendEncodeMSj(String respDecode) {
			// TODO Auto-generated method stub
			try {
				String urlDecode = configRestTemplate.getUrl() + "/encode";
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> requestEntity = new HttpEntity<>(respDecode, headers);

				ResponseEntity<String> responseEntity = restTemplate.postForEntity(urlDecode, requestEntity, String.class);

				return responseEntity;

			} catch (HttpClientErrorException | HttpServerErrorException e) {

				throw new IsoMessageBadRequest(e.getResponseBodyAsString());

			} catch (Exception e) {

				String responseError = e.getMessage();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);

			}

		}
		
		@Override
		public ResponseEntity<String> sendDecodeMSj2(String respEncode) {
			// TODO Auto-generated method stub
			try {
				String urlDecode = configRestTemplate.getUrl() + "/decode2";
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> requestEntity = new HttpEntity<>(respEncode, headers);

				ResponseEntity<String> responseEntity = restTemplate.postForEntity(urlDecode, requestEntity, String.class);
	
				return responseEntity;

			} catch (HttpClientErrorException | HttpServerErrorException e) {
				
				throw new IsoMessageBadRequest(e.getResponseBodyAsString());

			} catch (Exception e) {

				String responseError = e.getMessage();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);

			}
		}
}
