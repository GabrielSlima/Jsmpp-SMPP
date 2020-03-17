package jsmpp.single.message.jsmpp.single.message;

import java.io.IOException;
import java.util.Date;

import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.TimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@SpringBootApplication
public class Application {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static int smsServicePort = 0;
    
    public static String smsServiceHost;
    public static String smsServiceUsername;
    public static String smsServicePassword;
    public static String smsSystemType;

    private String messageEnrichedPhoneNumber;

    boolean sessionConnection = false;

    boolean threadActivated = false;

    private String messageEnrichedMessageText = "Voce nao tem saldo suficiente para renovar seu PREZAO 9,99 POR SEMANA. Faca uma recarga agora e garanta a renovacao do seu Prezao.";
    
    
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	 @Bean
	 public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
	        return new PropertySourcesPlaceholderConfigurer();
	 }
		
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx,
												   @Value("${smsserver.host}") String host,
												   @Value("${smsserver.port}") int port,
												   @Value("${smsserver.username}") String username,
												   @Value("${smsserver.passwd}") String passwd,
												   @Value("${smsserver.systemType}") String systemType,
												   @Value("${smsserver.msisdn}") String msisdn) {
			
			return args -> {
				SMPPSession session = new SMPPSession();
				LOGGER.info("[CONNECTING]");
		        String systemId = session.connectAndBind(host,
		        										 port,
		        										 new BindParameter(BindType.BIND_TX,
		        												 		   username,
		        												 		   passwd,
		        												 		   systemType,
		        												 		   TypeOfNumber.INTERNATIONAL,
		        												 		   NumberingPlanIndicator.ISDN, null));
				LOGGER.info("[CONNECTED]");
				try {
		        String messageId = session.submitShortMessage("CMT",
		                									  TypeOfNumber.INTERNATIONAL,
		                									  NumberingPlanIndicator.UNKNOWN,
		                									  null,
		                									  TypeOfNumber.INTERNATIONAL,
		                									  NumberingPlanIndicator.UNKNOWN,
		                									  msisdn,
		                									  new ESMClass(),
		                									  (byte)0,
		                									  (byte)1, 
		                									  null,
		                									  null,
		                									  new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
		                									  						 (byte)0,
		                									  						 new GeneralDataCoding(Alphabet.ALPHA_DEFAULT,
		                									  								 			   MessageClass.CLASS1,
		                									  								 			   false),
		                									  						 (byte) 0,
				   									  						 messageEnrichedMessageText.getBytes());
				LOGGER.info("[SENDED]");
				session.close();
				} catch(IOException ex) {
					LOGGER.info("EXCEPTION: {}", ex);
					LOGGER.info("[CLOSING CONNECTION]");
					session.close();
				}
			};
		}
		
}
