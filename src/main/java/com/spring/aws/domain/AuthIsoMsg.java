package com.spring.aws.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.json.JSONObject;

public class AuthIsoMsg{
	
	 
	Map<String,String> typeMsgAns=Map.of("0200","0210" , "0420", "0430", "0800", "0810");	
	
	private JSONObject jsonObjectAns=new JSONObject();
	private String P38,P39;
	Map<String, String> mapFielsAns;
	
	Map<String,Runnable> conditionals=Map.of(
			"88020110150217040",()->{this.P38=this.generateRandom(6).toString();this.P39="00";},
			"4915110200080078",()->{this.P38="000000";this.P39="55";},
			"4646200115001354",()->{this.P38="000000";this.P39="51";},
			"4915110200000048",()->{this.P38="000000";this.P39="54";},
			"4915110200080110",()->{this.P38="000000";this.P39="12";},
			"4915110200103920",()->{this.P38="000000";this.P39="62";}
			);
	
	public AuthIsoMsg(JSONObject jsonObject) {
		String typeMsg=jsonObject.getString("t");
		
		jsonObject.put("t",typeMsgAns.get(typeMsg)); //Ponemos tipo de respuesta.
		
		if(jsonObject.has("1") && jsonObject.has("p")) {
			jsonObject.remove("1"); //Eliminamos bit map primario,
			jsonObject.remove("p"); //Eliminamos el elemento P,
		}
				
		if(typeMsg.equals("0200")||typeMsg.equals("0420")) {  //Si los msgs son de tipo 0200 o 0420, encendemos p38 y p39
			String cardNumber=jsonObject.getString("35").split("=")[0];
			conditionals.getOrDefault(cardNumber,()->{
				this.P38=generateRandom(6).toString();
				this.P39="00";
			}).run();
			jsonObject.put("38", this.P38);
			jsonObject.put("39", this.P39);
		}
		
		this.jsonObjectAns=jsonObject;
	};
	
	public BigInteger generateRandom(int lenght) {
		Double randomNumber=Math.random()*Math.pow(10, lenght);
       BigDecimal decimal = new BigDecimal(randomNumber);
       BigInteger integer = decimal.toBigInteger();
		return integer;	
		}

	
	public JSONObject getJsonObjectAns() {
		return jsonObjectAns;
	}

	public void setJsonObjectAns(JSONObject jsonObjectAns) {
		this.jsonObjectAns = jsonObjectAns;
	}

	
}
