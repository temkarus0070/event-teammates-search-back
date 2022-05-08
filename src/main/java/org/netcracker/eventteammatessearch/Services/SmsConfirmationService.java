package org.netcracker.eventteammatessearch.Services;


import org.netcracker.eventteammatessearch.entity.PhoneToken;
import org.netcracker.eventteammatessearch.entity.PhoneTokenKey;
import org.netcracker.eventteammatessearch.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
public class SmsConfirmationService {

private static Logger logger=LoggerFactory.getLogger(SmsConfirmationService.class);
    @Autowired
    private UserService userService;

    @Autowired
    private PhoneTokenService phoneTokenService;

    @Value("${sms.name}")
    private String smsName;

    @Value("${sms.credentials}")
    private String smsCredentials;

    @Value("${sms.service}")
    private String smsService;

    @Value("${sms.isProduction}")
    private boolean isProduction;



    public void sendToken(Principal principal, String phone) {
        if (!phoneTokenService.checkIfHasNotExpired(principal)) {
          try {
              String xmlForSending = "";
              if (isProduction)
                  xmlForSending=getXmlForSending(phone);
                RestTemplate restTemplate=new RestTemplate();
              HttpHeaders headers = new HttpHeaders();
              headers.setContentType(MediaType.APPLICATION_XML);
              HttpEntity<String> request = new HttpEntity<String>(xmlForSending, headers);
              ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(smsService, request, String.class);
              String body = stringResponseEntity.getBody();
              PhoneToken tokenFromServerResponse = getTokenFromServerResponse(phone, body, principal);

           phoneTokenService.add(tokenFromServerResponse);
          }
          catch (Exception ex){
              logger.error(ex.getMessage());
          }
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вам уже был отправлен код, запросите повторную отправку через 10 минут");

    }

    public PhoneToken getTokenFromServerResponse(String phone,String xml,Principal principal) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(xml)));
        NodeList response = document.getElementsByTagName("response");
        Node item = response.item(0);
        NodeList childNodes = item.getChildNodes();
        Node item1 = childNodes.item(0);
        if (item1.getNodeName().equals("error")){
            throw new Error("Ошибка "+item1.getTextContent());
        }
        else {
            NamedNodeMap attributes = item1.getAttributes();
            Node status = attributes.getNamedItem("status");
            switch (status.getTextContent()){
                case "not_delivered" :
                    throw new Error("не удалось отправить сообщение, повторите попытку позже");
                case "expired":
                    throw new Error("не удалось отправить сообщение на ваш номер");
            }
            PhoneToken phoneToken = new PhoneToken();
            phoneToken.setId(new PhoneTokenKey(LocalDateTime.now(), principal.getName()));
            phoneToken.setPhone(phone);
            phoneToken.setUser(new User(principal.getName()));
            phoneToken.setCode(attributes.getNamedItem("code").getTextContent());
            return phoneToken;
        }
    }

    public String getXmlForSending(String phoneNumber) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element request = document.createElement("request");
        Element security = document.createElement("security");
        Element phone = document.createElement("phone");
        Element sender = document.createElement("sender");
        Element codeLen = document.createElement("random_string_len");
        Element text = document.createElement("text");
        Element login = document.createElement("login");
        Element password = document.createElement("password");

        phone.setTextContent(phoneNumber);
        login.setTextContent(smsName);
        password.setTextContent(smsCredentials);
        sender.setTextContent("targettele");
        text.setTextContent("Введите данный код для подтверждения телефона {код}");
        codeLen.setTextContent("5");

        document.appendChild(request);
        request.appendChild(security);
        request.appendChild(phone);
       request.appendChild(sender);
        request.appendChild(codeLen);
        request.appendChild(text);
        security.appendChild(login);
        security.appendChild(password);


        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter stringWriter=new StringWriter();
        transformer.transform(new DOMSource(document),new StreamResult(stringWriter));

        String textContent = stringWriter.toString();
        return textContent;
    }

    public void setPhoneConfirmationToUser(Principal principal){
        userService.setPhoneConfirmed(principal);
    }

    public boolean checkToken(String phone, String code, Principal principal) {
    List<PhoneToken> userTokens = phoneTokenService.getUserTokens(principal);
    if (userTokens.stream().anyMatch(e->e.getCode().equals(code)&&e.getPhone().equals(phone))){
        return true;
    }
        return false;
    }
}
